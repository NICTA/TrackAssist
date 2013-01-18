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

import au.com.nicta.tk.tree.TkStringKey;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;

/**
 *
 * @author Alan
 */
public class TkLJIPDAAssociator implements TkAssociator {

    private boolean DEBUG = false;

    protected TkStringKey<TkLJIPDAState> stateKey = TkStringKey.EMPTY_KEY;
    protected TkAssocOptimizer assocOptimizer = new TkHungarianAlgorithmAssocOptimizer();

    public double gateThreshold = Double.NaN;
    public double PD = Double.NaN;
    public double PG = Double.NaN;

    public TkLJIPDAAssociator(TkStringKey<TkLJIPDAState> keyState) {
        this.stateKey = TkStringKey.newInstance( keyState );
    }

    public void setOptimizer( TkAssocOptimizer op ) {
        assocOptimizer = op;
    }

    public TkAssocOptimizer getOptimizer() {
        return assocOptimizer;
    }


    public TkAssocResult associate( TkTracks tracks, TkDetections detections ) {

        TkAssocScoreMatrix scores = getAssocScoreMatrix( tracks, detections );
        TkAssocMatrix      assoc  = assocOptimizer.getAssocMatrix( scores );

        return new TkAssocResult( scores, assoc );
    }


    protected TkAssocScoreMatrix getAssocScoreMatrix( TkTracks tracks, TkDetections detections ) {

        final boolean isDistance = false; // higher score is better

        TkAssocScoreMatrix ret = new TkAssocScoreMatrix( tracks.size(), detections.size(), isDistance );

        if(    tracks.size() == 0
            || detections.size() == 0 ) {

            return ret;
        }

        Matrix generalLike = getGeneralLikelihood(tracks, detections);
        if( DEBUG ) {
            System.out.println( "generalLike: " + TkMatrixUtil.format("%.5e", generalLike) );
        }


        Matrix normGeneralLike = normalizeGeneralLikelihood( generalLike );
        if( DEBUG ) {
            System.out.println("normGeneralLike: " + (normGeneralLike) );
        }

        double delta = PD * PG;
        normGeneralLike.times( Ret.ORIG, false, delta );
        if( DEBUG ) {
            System.out.println( "normGeneralLike: " + (normGeneralLike) );
        }

        Matrix commonMat = getCommonMatrix( normGeneralLike );
        if( DEBUG ) {
            System.out.println( "commonMat: " + (commonMat) );
        }

        Matrix trackDetectionMat = getTrackDetectionMatrix( commonMat );
        if( DEBUG ) {
            System.out.println( "trackDetectionMat: " + TkMatrixUtil.format("%.4e", trackDetectionMat) );
        }

        Matrix trackDetectionBigMat = getTrackDetectionBigMatrix( trackDetectionMat, generalLike );
        if( DEBUG ) {
            System.out.println( "trackDetectionBigMat: " + TkMatrixUtil.format("%.4e", trackDetectionBigMat) );
        }

        // for each track
        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
            fillAssocScoreMatrixForTrack( tIdx, tracks, trackDetectionMat, trackDetectionBigMat, ret );
        }

        if( DEBUG ) {
            System.out.println( "assoc scores: " + TkMatrixUtil.format("%.4e", ret.scores) );
        }

        return ret;
    }

    protected Matrix getGeneralLikelihood( TkTracks tracks, TkDetections detections ) {

        // Get the matrix that contain the centroids of all detections
        Matrix msur = TkCell.getCentroidMatrix( detections.find( TkCell.name ) );
        Matrix like = MatrixFactory.dense( ValueType.DOUBLE, tracks.size(), detections.size() );

        int msurDims = (int)msur.getRowCount();

        // for each track
        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
            TkLJIPDAState stateLJIPDA = tracks.get( tIdx ).getLast().find( stateKey );

            Matrix innovCovInv = stateLJIPDA.innovCov.inv(); // could be non-invertible?
//            double innovCovDet = stateLJIPDA.innovCov.det();

            // for each detection
            for( int dIdx = 0; dIdx < detections.size(); ++ dIdx ) {

                Matrix z = msur.subMatrix( Calculation.Ret.LINK, 0, dIdx, msur.getRowCount()-1, dIdx );

                // innov = z - z_predicted
                Matrix innov = z.minus( stateLJIPDA.predictedMsur );

                // Matlab code:
                // if innov'*inv_innov_cov*innov<=tracker.gate_threshold
                //    general_likelihood(t,o)=
                // (1/((sqrt(2*pi))^k*sqrt(det(innov_cov))))
                // *exp(-0.5*(innov'*inv_innov_cov*innov));
                // end
                //------------------------------------------------------
                double d = innov.transpose().mtimes( innovCovInv  ).mtimes( innov ).doubleValue();
                if( d < gateThreshold ) {
                    double numer = Math.exp( -0.5 * d );
                    double denom =   Math.pow( Math.sqrt(2*Math.PI), msurDims )
                                   * Math.sqrt( stateLJIPDA.innovCov.det() );

                    like.setAsDouble( numer / denom, tIdx, dIdx );
                }
            } // for each detection
        } // for each track

        return like;
    }

    protected Matrix normalizeGeneralLikelihood( Matrix src ) {
        Matrix sum = src.sum( Ret.LINK, 0, false ); // sum columns
        Matrix dst = src.clone();

        for( int c = 0; c < dst.getColumnCount(); ++c ) {
            Matrix col = dst.selectColumns( Ret.LINK, c );
            col.divide( Ret.ORIG, false, sum.getAsDouble(0,c) );
        }

        return dst;
    }

    protected Matrix getCommonMatrix( Matrix src ) {
        Matrix dst = src.zeros( Ret.NEW );

        for( int r = 0; r < dst.getRowCount(); ++r )
        {
            for( int c = 0; c < dst.getColumnCount(); ++c ) {
                // TODO: should we check for ZERO division
                try {
                    double val = src.getAsDouble( r, c );
                    dst.setAsDouble( val / (1 - val), r, c );
                }
                catch( ArrayIndexOutOfBoundsException e ) {
                    e.printStackTrace();
                }
            }
        }

        return dst;
    }


    /**
     * Matlab Code:-------------------------------------------------
     * pViTrackiMeas=zeros(size(general_likelihood));
     * for o=1:total_observations
     * 		pViTrackiMeas(:,o)=common_matrix(:,o)+1;
     * 		pViTrackiMeas(:,o)=pViTrackiMeas(:,o)/(1+sum(common_matrix(:,o)));
     * end
     * --------------------------------------------------------------
     */
    protected Matrix getTrackDetectionMatrix( Matrix commonMatrix ) {
        // returns pViTrackiMeas
        Matrix commonColSum = commonMatrix.sum( Ret.LINK, 0, false );
        Matrix dst = commonMatrix.zeros( Ret.NEW );

        for( int c = 0; c < commonMatrix.getColumnCount(); ++c ) {
            double s = commonColSum.getAsDouble( 0, c );

            // TODO: should we check for ZERO division
            for( int r = 0; r < commonMatrix.getRowCount(); ++r ) {

                double val = commonMatrix.getAsDouble( r, c );
                dst.setAsDouble( ( 1 + val )/( 1 + s ), r, c );
            }
        }
        return dst;
    }

        /**
     * Matlab Code:-------------------------------------------------
     * pViTrackiMeas=zeros(size(general_likelihood));
     * for o=1:total_observations
     * 		pViTrackiMeas(:,o)=common_matrix(:,o)+1;
     * 		pViTrackiMeas(:,o)=pViTrackiMeas(:,o)/(1+sum(common_matrix(:,o)));
     * end
     *
     * big_matrix=(pViTrackiMeas.*pViTrackiMeas).*general_likelihood;
     * // pViTrackiMeas --> trackDetectionMatrix
     * --------------------------------------------------------------
     */
    protected Matrix getTrackDetectionBigMatrix( Matrix trackDetectionMatrix, Matrix generalLike ) {
        return trackDetectionMatrix.times( trackDetectionMatrix ).times( generalLike );
    }

    protected void fillAssocScoreMatrixForTrack(
            int trackIdx,
            TkTracks tracks,
            Matrix trackDetectionMatrix,
            Matrix trackDetectionBigMatrix,
            TkAssocScoreMatrix scoreMatrix ) {

        // Matlab Code:
        // mprime=0;
        // pVRowComplement=1-pViTrackiMeas(t,:);
        // mult=1;
        Matrix trackDetectionMatrixComplement = trackDetectionMatrix.times( Ret.LINK, false, -1).plus( 1 );


        // Matlab Code:
        //for o=1:total_observations
        //	if big_matrix(t,o)~=0
        //		mprime=mprime+pViTrackiMeas(t,o);
        //		mult=mult*pVRowComplement(1,o);
        //	end
        //end
        double mprime = 0;
        double mult = 1.0;
        for( int dIdx = 0; dIdx < trackDetectionMatrix.getColumnCount(); ++dIdx ) {
            if( trackDetectionBigMatrix.getAsDouble( trackIdx, dIdx ) != 0 ) {
                mprime += trackDetectionMatrix          .getAsDouble( trackIdx, dIdx );
                mult   *= trackDetectionMatrixComplement.getAsDouble( trackIdx, dIdx );
            }
        }

        TkTrack track = tracks.get( trackIdx );
        TkLJIPDAState state = track.getLast().find( stateKey );

        // mprime=mprime-tracker.PD*tracker.PG*predicted_tracks(t).exist*(1-mult);
        mprime -=   PD
                  * PG
                  * state.existencePrior
                  * ( 1 - mult );

        double gateVolume = Math.PI * gateThreshold * Math.sqrt( state.innovCov.det() );

        // Matlab Code:
        // delta=tracker.PD*tracker.PG;
        //    if mprime>0
        //        delta=delta.*(1-(predicted_tracks(t).gate_volume/mprime)*sum(big_matrix(t,:)));
        //    end
        double delta = PD * PG;
        if( mprime > 0 ) {
            // Calculate sum of row of big matrix
            double s =  trackDetectionBigMatrix
                       .selectRows( Ret.LINK, trackIdx )
                       .sum( Ret.LINK, Matrix.COLUMN, false )
                       .getAsDouble( 0 );

            delta *= 1 - gateVolume / mprime * s;
        }

        // updated_existence(t,1)= (1-delta)*predicted_tracks(t).exist /
        //                         (1-delta*predicted_tracks(t).exist);

//        state.existencePost =   ( 1 - delta ) * state.existencePrior
//                              / ( 1 - delta * state.existencePrior );
        state.existenceGivenNextMsur =   ( 1 - delta ) * state.existencePrior
                                       / ( 1 - delta * state.existencePrior );

        // Matlab Code:
        // if mprime>0
        //	  coeff=tracker.PD*tracker.PG*predicted_tracks(t).gate_volume/(mprime*(1-delta));
        // 	  beta_matrix(t,:)=big_matrix(t,:).*coeff;
        // else
        //	  beta_matrix(t,:)=0*big_matrix(t,:);
        // end
        if( mprime > 0 ) {
            double coeff =   PD
                           * PG
                           * gateVolume
                           / ( mprime * (1-delta) );

            for( int dIdx = 0; dIdx < trackDetectionMatrix.getColumnCount(); ++dIdx ) {
                double score = coeff * trackDetectionBigMatrix.getAsDouble( trackIdx, dIdx );
                scoreMatrix.scores.setAsDouble( score, trackIdx, dIdx );
            }
        }
        else
        {
            scoreMatrix.scores.selectRows( Ret.LINK, trackIdx ).fill( Ret.ORIG, 0 );
        }
    }

}
