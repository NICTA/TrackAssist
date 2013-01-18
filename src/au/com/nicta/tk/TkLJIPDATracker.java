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
import org.ujmp.core.enums.ValueType;

/**
 *
 * @author Alan
 */
public class TkLJIPDATracker {

    // parameters
    public double initExistenceProb = Double.NaN;
    public double existenceGamma    = Double.NaN;
    public double terminateExistenceThresh = Double.NaN;
    //
    private static final boolean DEBUG = false;

    public TkStringKey<TkLJIPDAState> stateKey = TkStringKey.newInstance( "state" );

    protected TkKalmanFilter kalmanFilter;
    protected TkAssociator associator;
    
    public final int stateDims;
    public final int msurDims;

    public TkLJIPDATracker(
            int stateVectorLength,
            int measurementVectorLength,
            TkStringKey<TkLJIPDAState> keyState) {
        this.stateDims = stateVectorLength;
        this.msurDims = measurementVectorLength;
        this.stateKey = TkStringKey.newInstance(keyState);

        kalmanFilter = new TkKalmanFilter( stateDims, msurDims );
        associator = new TkLJIPDAAssociator( keyState );
    }

    public TkStringKey<TkLJIPDAState> getStateKey() {
        return TkStringKey.newInstance(stateKey);
    }


    /**
     * We use setter and getter to perform optional setup when the components
     * change.
     * @param kf
     */
    public void setKalmanFilter( TkKalmanFilter kf ) {
        kalmanFilter = kf;
    }

    public TkKalmanFilter getKalmanFilter() {
        return kalmanFilter;
    }

    public void setAssociator( TkAssociator assoc ) {
        this.associator = assoc;
    }

    public TkAssociator getAssociator() {
        return associator;
    }

    double getInitExistenceProb() {
        return initExistenceProb;
    }

//    public void predictSafe( TkTracks tracks ) {
//        synchronized( tracks ) {
//            predict( tracks );
//        }
//    }
//
//    public void updateSafe( TkTracks tracks, TkDetections detections ) {
//        synchronized( tracks ) {
//            update( tracks, detections );
//        }
//
//    }

    public void predict( TkTracks tracks ) {
        TkTracks activeTracks = tracks.getTracksWithStatus( TkTrack.Status.ACTIVE );

        predictTracks( activeTracks );
    }

    public void predictTracks( TkTracks activeTracks ) {
        for( TkTrack t : activeTracks ) {
            predictTrack( t );
        }
    }

    /**
     * Lets say we predictSafe from t to t+1
     * The prior is associated with t, not t+1 because if there are no measurements
     * at time t+1, the track ends at t.
     * @param track
     */
    protected void predictTrack( TkTrack track ) {
        predictState( track.getLast().find( stateKey ) );
    }

    protected void predictState( TkLJIPDAState state ) {

        kalmanFilter.predict( state.post, state.prior );

        // e" = gamma * e;
//        state.existencePrior = existenceGamma * state.existencePost;
        state.existencePrior = existenceGamma * state.existencePost;

        // These are required by LJIPDAAssociator
        state.predictedMsur = kalmanFilter.predictedMsur( state.prior );

        // innovCov = H * P" * H'  +  R; where P" is the predicted prior
        state.innovCov = kalmanFilter.innovCov( state.prior );

//        // gateVolume = pi * gateThreshold * sqrt( det(innovCov) )
//        // TODO: singular determinant?
//        state.gateVolume = Math.PI * gateThreshold * Math.sqrt( state.innovCov.det() );
    }


    /**
     * Top level function to call when you want to update
     * 
     * @param tracks
     * @param detections
     * @return
     */
    public void update( TkTracks allTracks, TkDetections detections ) {
        TkTracks activeTracks = allTracks.getTracksWithStatus( TkTrack.Status.ACTIVE );

        TkTracks newTracks = updateTracks( activeTracks, detections );

        allTracks.addAll( newTracks );
    }


    /**
     * Returns newly initiated tracks.
     * @param tracks
     * @param detections
     * @return
     */
    protected TkTracks updateTracks( TkTracks activeTracks, TkDetections detections ) {
        TkAssocResult assoc = associator.associate( activeTracks, detections );
        if( DEBUG ) {
            System.out.println( "assoc result: " + assoc );
        }

        updateTracksWithAssoc( activeTracks, detections, assoc );

        return createNewTracks( detections, assoc );
    }
    
    protected void updateTracksWithAssoc( TkTracks tracks, TkDetections detections, TkAssocResult assoc ) {
        for( int trackIdx = 0; trackIdx < tracks.size(); ++trackIdx )
        {
            updateTrack( trackIdx, tracks, detections, assoc );
        } // for each track
    }

    protected void updateTrack( int trackIdx, TkTracks tracks, TkDetections detections, TkAssocResult assocResult ) {
        TkTrack track = tracks.get( trackIdx );

        int dIdx;

        if( assocResult.assoc.trackToDetection.length == 0 ) {
            dIdx = -1;
        }
        else {
            dIdx = assocResult.assoc.trackToDetection[ trackIdx ];
        }

        TkLJIPDAState oldState  = track.getLast().find( stateKey );
        TkLJIPDAState newState  = new TkLJIPDAState( stateDims, msurDims );

        if( dIdx >= 0 ) {
            updateTrackStateByLJIPDA( dIdx, detections, oldState, newState );
//            if( trackIdx == 4 ) {
//                System.out.println( "valid detection Track 2's prior existence: " + oldState.existencePrior );
//                System.out.println( "valid detection Track 2's prior existence: " + oldState.prior.state );
//            }
        }
        else {
            updateTrackStateNoValidAssociation( oldState, newState );
//            if( trackIdx == 4 ) {
//                System.out.println( "invalid detection Track 2's prior existence: " + oldState.existencePrior );
//                System.out.println( "invalid detection Track 2's prior existence: " + oldState.prior.state );
//            }
        }

        // New LJIPDA code to updateSafe track's state, var, exist
        TkTrackElement e = new TkTrackElement( dIdx >= 0 ? detections.get(dIdx) : null );

        e.add( stateKey, newState );

        track.elements.add( e );

        // test for termination probabilities
        // NOTE: do this after we added the new track element because we want
        // the newState.exist to show that it's below terminateExistenceThresh
        if( newState.existencePost < terminateExistenceThresh ) {
            track.status = TkTrack.Status.TERMINATED;
        }
    }

    protected void updateTrackStateNoValidAssociation( TkLJIPDAState oldState, TkLJIPDAState newState ) {

        // state posterior = prior
        newState.post.state = oldState.prior.state.clone();

        // state covariance posterior = prior
        newState.post.stateCov = oldState.prior.stateCov.clone();

        // Update exist
        // predicted_tracks(t).exist =   (1-tracker.PD*tracker.PG) * predicted_tracks(t).exist
        //                                 / (1-tracker.PD*tracker.PG*predicted_tracks(t).exist);
        newState.existencePost = oldState.existencePrior;
    }

    /**
     * Matlab Code:
     * here the algorithm deviates from LJIPDA, in LJIPDA
     * the state updateSafe happens as a weighted sum of the
     * validated measurement
     * here the state is made equal to the measurement
     * assigned, while the variance is updated using KF
     * variance updateSafe equation.
     * K=predicted_tracks(t).var*tracker.H'*inv(predicted_tracks(t).innov_cov);
     * predicted_tracks(t).state=predicted_tracks(t).state+K*(measurements(k).observations(:,assigned_detection)-tracker.F*predicted_tracks(t).state);
     * innov=measurements(k).observations(:,assigned_detection)-tracker.H*predicted_tracks(t).state;
     * likelihood=(1/(sqrt(2*pi)))*exp(-0.5.*(innov'*inv(predicted_tracks(t).innov_cov)*innov));
     * predicted_tracks(t).state=measurements(k).observations(:,assigned_detection);
     * predicted_tracks(t).var=(eye(size(K*tracker.H))-K*tracker.H)*predicted_tracks(t).var;
     * predicted_tracks(t).associated_detection=assigned_detection;
     * predicted_tracks(t).display_state=measurements(k).observations(:,assigned_detection);
     * if there is an assigned matrix the exist updateSafe is
     * the same in LJIPDA
     * predicted_tracks(t).exist=updated_existence(t,1);
     * delta=tracker.PD*tracker.PG*(1-likelihood);
     * predicted_tracks(t).exist=(1-delta)*predicted_tracks(t).exist/(1-delta*predicted_tracks(t).exist);
     * predicted_tracks(t).gap_frames=0;
     */
    protected void updateTrackStateByLJIPDA(
            int detectionIdx,
            TkDetections detections,
            TkLJIPDAState oldState,
            TkLJIPDAState newState ) {

        Matrix msur = getCentroidMatrix( detections.get( detectionIdx ) );

        kalmanFilter.update( oldState.prior, newState.post, msur );

		// Update the exist
        newState.existencePost = oldState.existenceGivenNextMsur;
    }


//    protected Matrix getCentroidMatrix( TkDetections detections ) {
//        Matrix m = MatrixFactory.dense( ValueType.DOUBLE, msurDims, detections.size() );
//
//        for( int i = 0; i < detections.size(); ++i ) {
//            TkCell cell = detections.find( i ).find( TkCell.name );
//
//            m.setAsDouble( cell.cx, 0, i );
//            m.setAsDouble( cell.cy, 1, i );
//        }
//
//        return m;
//    }

    protected Matrix getCentroidMatrix( TkDetection detection ) {
        Matrix m = MatrixFactory.dense( ValueType.DOUBLE, msurDims, 1 );

        TkCell cell = detection.find( TkCell.name );

        m.setAsDouble( cell.cx, 0, 0 );
        m.setAsDouble( cell.cy, 1, 0 );

        return m;
    }

//    protected TkAssocResult associate( TkTracks tracks, TkDetections detections ) {
//
//        CtAssocScoreMatrix scores = getAssocScoreMatrix( tracks, detections );
//        CtAssocMatrix      assoc  = associator.getAssocMatrix( scores );
//
//        return new TkAssocResult( scores, assoc );
//    }


//    protected Matrix getGeneralLikelihood( TkTracks tracks, TkDetections detections ) {
//
//        // Get the matrix that contain the centroids of all detections
//        Matrix msur = TkCell.getCentroidMatrix( detections.getList( TkCell.name ) );
//
//        Matrix like = MatrixFactory.dense( ValueType.DOUBLE, tracks.size(), detections.size() );
//
//        // for each track
//        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
//            TkLJIPDAState stateLJIPDA = tracks.find( tIdx ).getLast().find( stateKey );
//
//            Matrix innovCovInv = kalmanFilter.innovCov( stateLJIPDA.prior ).inv(); // could be non-invertible?
//
//            // for each detection
//            for( int dIdx = 0; dIdx < detections.size(); ++ dIdx ) {
//
//                Matrix z = msur.subMatrix( Calculation.Ret.LINK, 0, dIdx, msur.getRowCount()-1, dIdx );
//
//                // innov = z - z_predicted
//                Matrix innov = z.minus( kalmanFilter.predictedMsur(stateLJIPDA.prior) );
//
//                // Matlab code:
//                // if innov'*inv_innov_cov*innov<=tracker.gate_threshold
//                //    general_likelihood(t,o)=
//                // (1/((sqrt(2*pi))^k*sqrt(det(innov_cov))))
//                // *exp(-0.5*(innov'*inv_innov_cov*innov));
//                // end
//                //------------------------------------------------------
//                double d = innov.transpose().mtimes( innovCovInv  ).mtimes( innov ).doubleValue();
//                if( d < gateThreshold ) {
//                    double numer = Math.exp( -0.5 * d );
//                    double denom =   Math.pow( Math.sqrt(2*Math.PI), msurDims )
//                                   * Math.sqrt( kalmanFilter.innovCov( stateLJIPDA.prior ).det() );
//
//                    like.setAsDouble( numer / denom, tIdx, dIdx );
//                }
//            } // for each detection
//        } // for each track
//
//        return like;
//	}

//    protected Matrix normalizeGeneralLikelihood( Matrix src ) {
//        Matrix sum = src.sum( Ret.LINK, 0, false ); // sum columns
//        Matrix dst = src.clone();
//
//        for( int c = 0; c < dst.getColumnCount(); ++c ) {
//            Matrix col = dst.selectColumns( Ret.LINK, c );
//            col.divide( Ret.ORIG, false, sum.getAsDouble(0,c) );
//        }
//
//        return dst;
//    }

    /**
     * ????ALAN
     * Matlab Code:-------------------------------
     * pTiTrackComplement=1-pTiTrack;
     * common_matrix=pTiTrack./pTiTrackComplement;
     * -------------------------------------------
     */
//    protected Matrix getCommonMatrix( Matrix src ) {
//        Matrix dst = src.zeros( Ret.NEW );
//
//        for( int r = 0; r < dst.getRowCount(); ++r )
//        {
//            for( int c = 0; c < dst.getColumnCount(); ++c ) {
//                // TODO: should we check for ZERO division
//                try {
//                    double val = src.getAsDouble( r, c );
//                    dst.setAsDouble( val / (1 - val), r, c );
//                }
//                catch( ArrayIndexOutOfBoundsException e ) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return dst;
//    }

//    /**
//     * Matlab Code:-------------------------------------------------
//     * pViTrackiMeas=zeros(size(general_likelihood));
//     * for o=1:total_observations
//     * 		pViTrackiMeas(:,o)=common_matrix(:,o)+1;
//     * 		pViTrackiMeas(:,o)=pViTrackiMeas(:,o)/(1+sum(common_matrix(:,o)));
//     * end
//     * --------------------------------------------------------------
//     */
//    protected Matrix getTrackDetectionMatrix( Matrix commonMatrix ) {
//        // returns pViTrackiMeas
//        Matrix commonColSum = commonMatrix.sum( Ret.LINK, 0, false );
//        Matrix dst = commonMatrix.zeros( Ret.NEW );
//
//        for( int c = 0; c < commonMatrix.getColumnCount(); ++c ) {
//            double s = commonColSum.getAsDouble( 0, c );
//
//            // TODO: should we check for ZERO division
//            for( int r = 0; r < commonMatrix.getRowCount(); ++r ) {
//
//                double val = commonMatrix.getAsDouble( r, c );
//                dst.setAsDouble( ( 1 + val )/( 1 + s ), r, c );
//            }
//        }
//        return dst;
//    }

//    /**
//     * Matlab Code:-------------------------------------------------
//     * pViTrackiMeas=zeros(size(general_likelihood));
//     * for o=1:total_observations
//     * 		pViTrackiMeas(:,o)=common_matrix(:,o)+1;
//     * 		pViTrackiMeas(:,o)=pViTrackiMeas(:,o)/(1+sum(common_matrix(:,o)));
//     * end
//     *
//     * big_matrix=(pViTrackiMeas.*pViTrackiMeas).*general_likelihood;
//     * // pViTrackiMeas --> trackDetectionMatrix
//     * --------------------------------------------------------------
//     */
//    protected Matrix getTrackDetectionBigMatrix( Matrix trackDetectionMatrix, Matrix generalLike ) {
//        return trackDetectionMatrix.times( trackDetectionMatrix ).times( generalLike );
//    }
//
//    protected void fillAssocScoreMatrixForTrack(
//            int trackIdx,
//            TkTracks tracks,
//            Matrix trackDetectionMatrix,
//            Matrix trackDetectionBigMatrix,
//            CtAssocScoreMatrix scoreMatrix ) {
//
//        // Matlab Code:
//        // mprime=0;
//        // pVRowComplement=1-pViTrackiMeas(t,:);
//        // mult=1;
//        Matrix trackDetectionMatrixComplement = trackDetectionMatrix.times( Ret.LINK, false, -1).plus( 1 );
//
//
//        // Matlab Code:
//        //for o=1:total_observations
//        //	if big_matrix(t,o)~=0
//        //		mprime=mprime+pViTrackiMeas(t,o);
//        //		mult=mult*pVRowComplement(1,o);
//        //	end
//        //end
//        double mprime = 0;
//        double mult = 1.0;
//        for( int dIdx = 0; dIdx < trackDetectionMatrix.getColumnCount(); ++dIdx ) {
//            if( trackDetectionBigMatrix.getAsDouble( trackIdx, dIdx ) != 0 ) {
//                mprime += trackDetectionMatrix          .getAsDouble( trackIdx, dIdx );
//                mult   *= trackDetectionMatrixComplement.getAsDouble( trackIdx, dIdx );
//            }
//        }
//
//        TkTrack track = tracks.find( trackIdx );
//        TkLJIPDAState state = track.getLast().find( stateKey );
//
//        // mprime=mprime-tracker.PD*tracker.PG*predicted_tracks(t).exist*(1-mult);
//        mprime -=   PD
//                  * PG
//                  * state.existencePrior
//                  * ( 1 - mult );
//
//
//        // Matlab Code:
//        // delta=tracker.PD*tracker.PG;
//        //    if mprime>0
//        //        delta=delta.*(1-(predicted_tracks(t).gate_volume/mprime)*sum(big_matrix(t,:)));
//        //    end
//        double delta = PD * PG;
//        if( mprime > 0 ) {
//            // Calculate sum of row of big matrix
//            double s =  trackDetectionBigMatrix
//                       .selectRows( Ret.LINK, trackIdx )
//                       .sum( Ret.LINK, Matrix.COLUMN, false )
//                       .getAsDouble( 0 );
//
//            delta *= 1 - state.gateVolume / mprime * s;
//        }
//
//        // updated_existence(t,1)= (1-delta)*predicted_tracks(t).exist /
//        //                         (1-delta*predicted_tracks(t).exist);
//
////        state.existencePost =   ( 1 - delta ) * state.existencePrior
////                              / ( 1 - delta * state.existencePrior );
//        state.existenceGivenNextMsur =   ( 1 - delta ) * state.existencePrior
//                                       / ( 1 - delta * state.existencePrior );
//
//        // Matlab Code:
//        // if mprime>0
//        //	  coeff=tracker.PD*tracker.PG*predicted_tracks(t).gate_volume/(mprime*(1-delta));
//        // 	  beta_matrix(t,:)=big_matrix(t,:).*coeff;
//        // else
//        //	  beta_matrix(t,:)=0*big_matrix(t,:);
//        // end
//        if( mprime > 0 ) {
//            double coeff =   PD
//                           * PG
//                           * state.gateVolume
//                           / ( mprime * (1-delta) );
//
//            for( int dIdx = 0; dIdx < trackDetectionMatrix.getColumnCount(); ++dIdx ) {
//                double score = coeff * trackDetectionBigMatrix.getAsDouble( trackIdx, dIdx );
//                scoreMatrix.scores.setAsDouble( score, trackIdx, dIdx );
//            }
//        }
//        else
//        {
//            scoreMatrix.scores.selectRows( Ret.LINK, trackIdx ).fill( Ret.ORIG, 0 );
//        }
//    }


//    protected CtAssocScoreMatrix getAssocScoreMatrix( TkTracks tracks, TkDetections detections ) {
//
//        final boolean isDistance = false; // higher score is better
//
//        CtAssocScoreMatrix ret = new CtAssocScoreMatrix( tracks.size(), detections.size(), isDistance );
//
//        if(    tracks.size() == 0
//            || detections.size() == 0 ) {
//
//            return ret;
//        }
//
////        Matrix generalLike = assocScoreCalculator.getScores(tracks, detections);
//        Matrix generalLike = getGeneralLikelihood(tracks, detections);
//        if( DEBUG ) {
//            System.out.println( "generalLike: " + CtMatrixUtil.format("%.5e", generalLike) );
//        }
//
//
//        Matrix normGeneralLike = normalizeGeneralLikelihood( generalLike );
//        if( DEBUG ) {
//            System.out.println("normGeneralLike: " + (normGeneralLike) );
//        }
//
//        double delta = PD * PG;
//        normGeneralLike.times( Ret.ORIG, false, delta );
//        if( DEBUG ) {
//            System.out.println( "normGeneralLike: " + (normGeneralLike) );
//        }
//
//        Matrix commonMat = getCommonMatrix( normGeneralLike );
//        if( DEBUG ) {
//            System.out.println( "commonMat: " + (commonMat) );
//        }
//
//        Matrix trackDetectionMat = getTrackDetectionMatrix( commonMat );
//        if( DEBUG ) {
//            System.out.println( "trackDetectionMat: " + CtMatrixUtil.format("%.4e", trackDetectionMat) );
//        }
//
//        Matrix trackDetectionBigMat = getTrackDetectionBigMatrix( trackDetectionMat, generalLike );
//        if( DEBUG ) {
//            System.out.println( "trackDetectionBigMat: " + CtMatrixUtil.format("%.4e", trackDetectionBigMat) );
//        }
//
//        // for each track
//        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
//            fillAssocScoreMatrixForTrack( tIdx, tracks, trackDetectionMat, trackDetectionBigMat, ret );
//        }
//
//        if( DEBUG ) {
//            System.out.println( "assoc scores: " + CtMatrixUtil.format("%.4e", ret.scores) );
//        }
//
//        return ret;
//    }


    protected TkTracks createNewTracks( TkDetections detections, TkAssocResult assocResult ) {
        // Prefering to return an empty object rather than null so that
        // any logic that works with the size of CtCtracks don't have to handle
        // the null case.
        // NOTE: that detections.size() could be 0 but detections is never null.
        TkTracks tracks = new TkTracks();

        if( detections.size() == 0 ) {
            return tracks;
        }

        int[] detectionToTrack = assocResult.assoc.detectionToTrack;

        for( int i = 0; i < detectionToTrack.length; ++i ) {
            if( detectionToTrack[i] == -1 ) { // no associated track
                TkTrack t = createNewTrack( detections.get(i) );
                tracks.add( t );
            }
        }

        return tracks;
    }

    protected TkTrack createNewTrack( TkDetection detection ) {
        TkTrackElement e = createNewTrackElementLJIPDA( detection );

        TkTrack track = new TkTrack();
        track.elements.add( e );

        System.out.println( "Created new track." );

        return track;
    }

    protected TkTrackElement createNewTrackElementLJIPDA( TkDetection detection ) {
        // find detection info
        TkCell cell = detection.find( TkCell.name );

        // initialise track status
        TkLJIPDAState state = new TkLJIPDAState( stateDims, msurDims );

        state.post.state = MatrixFactory.dense( ValueType.DOUBLE, stateDims, 1 );
        state.post.state.setAsDouble( cell.cx, 0, 0);
        state.post.state.setAsDouble( cell.cy, 1, 0);

        // initialise track state covariance with the msur noise matrix
        state.post.stateCov = kalmanFilter.msurNoiseCov;

        // initialize exist probability
//        state.existencePost = getInitExistenceProb();
        state.existencePost = getInitExistenceProb();

        state.copyPostToPrior();


        // we want to keep a history of the track states, so we add the status
        // to a track element. A new track element is created for each time step.
        TkTrackElement e = new TkTrackElement( detection );
        e.add( stateKey, state );

        return e;
    }


}
