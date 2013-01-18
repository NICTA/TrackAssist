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

/**
 * The idea here is to separate data management and algorithms that operate
 * on the data. So the same data can be operated on by different algorithms.
 *
 * @author Alan
 */
public class TkLJIPDAState {

    public TkKalmanState prior;
    public TkKalmanState post;

    public Matrix innovCov      = MatrixFactory.EMPTYMATRIX;
    public Matrix predictedMsur = MatrixFactory.EMPTYMATRIX;

    public double existencePrior; // existence corresponding to prior.state, i.e existence given prediction from t-1 --> t
    public double existencePost; // existence corresponding to post.state, i.e existence given current msur at time t
    public double existenceGivenNextMsur; // existence corresponding to post.state, i.e existence given next msur at time t+1

    public TkLJIPDAState(int stateVectorLength, int measurementVectorLength) {
        prior = new TkKalmanState( stateVectorLength, measurementVectorLength );
        post  = new TkKalmanState( stateVectorLength, measurementVectorLength );
        existencePrior = Double.NaN;
        existencePost = Double.NaN;
        existenceGivenNextMsur = Double.NaN;
    }

    public void copyPostToPrior() {
        prior = new TkKalmanState(post);
        existencePrior = existencePost;
    }
}
