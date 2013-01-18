// ====================================================================================================================
// Copyright (c) 2013, National ICT Australia Ltd and The Walter and Eliza Hall Institute of Medical Research.
// All rights reserved.
//
// This software and source code is made available under a GPL v2 licence.
// The terms of the licence can be read here: http://www.gnu.org/licenses/gpl-2.0.txt
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// ====================================================================================================================

package au.com.nicta.tk;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

/**
 *
 * @author Alan
 */
public class TkKalmanFilter {

    // parameters
    public final int stateDims;
    public final int msurDims;

    public Matrix processMatrix;
    public Matrix processNoiseCov;
    public Matrix msurMatrix;
    public Matrix msurNoiseCov;

    // cached transposed versions
    protected Matrix processMatrixTCached;
    protected Matrix msurMatrixTCached;

    protected Matrix eyeStateDimByStateDim;

    public TkKalmanFilter(int stateVectorLength, int measurementVectorLength) {
        stateDims = stateVectorLength;
        msurDims = measurementVectorLength;
        eyeStateDimByStateDim = MatrixFactory.eye( ValueType.DOUBLE, stateDims, stateDims );
    }

    public void predict( TkKalmanState post, TkKalmanState prior ) {
        prior.state = predictState( post );
        
        // P" = F * P * F'  +  Q;
        prior.stateCov = processMatrix.mtimes( post.stateCov ).mtimes( getProcessMatrixT() ).plus( processNoiseCov );
    }

    public Matrix predictState( TkKalmanState post ) {
        return predictState( post.state );
    }

    public Matrix predictState( Matrix postState ) {
        // x" = F * x;
        return processMatrix.mtimes( postState );
    }

    public void update(
            TkKalmanState prior,
            TkKalmanState post,
            Matrix msur ) {
        
        update( prior, post, msur, innovCov(prior) );
    }

    /**
     * innovation covariance using linear model
     * @return
     */
    public Matrix innovCov( TkKalmanState prior ) {
        // innovCov = H * P" * H'  +  R;
        return msurMatrix.mtimes( prior.stateCov ).mtimes( getMsurMatrixT() ).plus( msurNoiseCov );
    }

    /**
     * Predicted measurement using linear model
     * 
     * @param prior
     * @return
     */
    public Matrix predictedMsur( TkKalmanState prior ) {
        return predictedMsur( prior.state );
    }

    public Matrix predictedMsur( Matrix priorState ) {
        // z" = H * x;
        return msurMatrix.mtimes( priorState );
    }

    public void update(
            TkKalmanState prior,
            TkKalmanState post,
            Matrix msur,
            Matrix innovCov ) {

        // z" = H * x;
        Matrix predictedMsur = predictedMsur( prior );

		// K = predicted_tracks(t).var * tracker.H' * inv(predicted_tracks(t).innov_cov)
        Matrix kalmanGain = prior.stateCov.mtimes( getMsurMatrixT() ).mtimes( innovCov.inv() );

        // kalman filter updateSafe
        Matrix innov = msur.minus( predictedMsur );

        // x" = x + K * innov;
        post.state = prior.state.plus( kalmanGain.mtimes(innov)  );

		// Update the variance
    	// var = (eye(size(K*tracker.H))-K*tracker.H)*predicted_tracks(t).var;
        post.stateCov = eyeStateDimByStateDim.minus( kalmanGain.mtimes( msurMatrix ) ).mtimes( prior.stateCov );
    }

    protected Matrix getMsurMatrixT() {
        if( msurMatrixTCached == null ) {
            msurMatrixTCached = msurMatrix.transpose();
        }
        return msurMatrixTCached;
    }

    // cached transposed versions
    protected Matrix getProcessMatrixT() {
        if( processMatrixTCached == null ) {
            processMatrixTCached = processMatrix.transpose();
        }
        return processMatrixTCached;
    }

}
