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

/**
 *
 * @author Alan
 */
public class TkKalmanState {

    public final int stateDims;
    public final int msurDims;

    public Matrix state;
    public Matrix stateCov;

//    public Matrix statePost;
//    public Matrix statePrior;
//    public Matrix stateCovPost;
//    public Matrix stateCovPrior;
//    public Matrix innovCov;
//    public Matrix msurPredicted;

    public TkKalmanState( TkKalmanState src ) {
        this( src.stateDims, src.msurDims );
        copyMore( src ); // always call a final method in constructor
    }

    public TkKalmanState(int stateVectorLength, int measurementVectorLength) {
        stateDims = stateVectorLength;
        msurDims = measurementVectorLength;
    }

    /**
     * Overridable version of copy
     */
    public void copy( TkKalmanState src ) {
        copyMore( src );
    }

    /**
     * Non-overridable version
     */
    protected final void copyMore( TkKalmanState src ) {
        this.state    = src.state   .clone();
        this.stateCov = src.stateCov.clone();
    }

    public void errorCheck() {
        assert errorCheckReturnBool() : "Inconsistent matrix dimensions.";
    }
    
    public boolean errorCheckReturnBool() {
        return    TkMatrixUtil.isSameSizeOrNull(state,     stateDims, 1)
               && TkMatrixUtil.isSameSizeOrNull(stateCov,  stateDims, stateDims);
    }

}
