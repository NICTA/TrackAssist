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
import au.com.nicta.tk.TkArray;
import java.util.Arrays;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

/**
 *
 * @author Alan
 */
public class TkLJIPDAForcedAssociator extends TkLJIPDAAssociator {

    public TkLJIPDAForcedAssociator(TkStringKey<TkLJIPDAState> keyState) {
        super(keyState);
    }


    @Override
    public TkAssocResult associate(TkTracks tracks, TkDetections detections) {
        TkAssocScoreMatrix scores = getAssocScoreMatrix( tracks, detections );
        System.out.println( "scores: \n" + TkMatrixUtil.format( "%f ", scores.scores ) );

        return associate(tracks, detections, scores);
    }

    public TkAssocResult associate(TkTracks tracks, TkDetections detections, TkAssocScoreMatrix scores) {
        int[] trackDetectionAssoc = findForcedAssoc( tracks, detections );

        TkAssocScoreMatrix modifiedScores = forceScores( scores, trackDetectionAssoc );

        System.out.println( "modifiedScores: \n" + TkMatrixUtil.format( "%f ", modifiedScores.scores ) );

        TkAssocMatrix assoc = assocOptimizer.getAssocMatrix( modifiedScores );

        TkAssocMatrix modifiedAssoc = forceAssoc( assoc, trackDetectionAssoc );

        if( !isAssocConsistent( modifiedAssoc ) ) {
            throw new Error("Forced associations in assoctiator is in consistent");
        }

        return new TkAssocResult( scores, modifiedAssoc );
    }


    boolean isAssocConsistent( TkAssocMatrix assoc ) {
        if( assoc.trackToDetection != null ) {
            for( int tIdx = 0; tIdx < assoc.trackToDetection.length; ++tIdx ) {
                int dIdx = assoc.trackToDetection[tIdx];
                if(    dIdx >= 0
                    && assoc.detectionToTrack[ dIdx ] != tIdx ) {
                    return false;
                }
            }
        }

        if( assoc.detectionToTrack != null ) {
            for( int dIdx = 0; dIdx < assoc.detectionToTrack.length; ++dIdx ) {
                int tIdx = assoc.detectionToTrack[dIdx];
                if(    tIdx >= 0
                    && assoc.trackToDetection[ tIdx ] != dIdx ) {
                    return false;
                }
            }
        }

        return true;
    }

    final int NO_FORCED_ASSOC = -1;

    TkAssocMatrix forceAssoc( TkAssocMatrix assoc, int[] forcedTrackDetectionAssoc ) {

        TkAssocMatrix newAssoc = assoc.newCopy();

        newAssoc.totalMatched = 0;

        for( int tIdx = 0; tIdx < forcedTrackDetectionAssoc.length; ++tIdx ) {
            int dIdx = forcedTrackDetectionAssoc[tIdx];
            
            if( dIdx >= 0 ) {
                // forcing an association if track is not already associated with the
                // correct detection
                int currDetectionAssoc = newAssoc.trackToDetection[tIdx];
                if( currDetectionAssoc != dIdx ) {
                    // unassociate with detection
                    newAssoc.detectionToTrack[currDetectionAssoc] = -1;
                    newAssoc.detectionToTrack[dIdx] = tIdx;
                    newAssoc.trackToDetection[tIdx] = dIdx;
                }
                // else, already correct do nothing
            }

            if( newAssoc.trackToDetection[tIdx] >= 0 ) {
                newAssoc.totalMatched++;
            }
        }

        return newAssoc;
    }
    

    int[] findForcedAssoc( TkTracks tracks, TkDetections detections) {
        int numForced = 0;

        int[] trackDetectionAssoc = new int[ tracks.size() ];

        // default is not forcing anything
        Arrays.fill( trackDetectionAssoc, NO_FORCED_ASSOC );

        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
            TkTrack t = tracks.get(tIdx);

            // find the last detection
            TkDetection d = t.getLast().det;

            if( d == null ) { // possible when track terminates
                continue;
            }

            TkDetectionAssocInfo assocInfo = d.tryFind( TkDetectionAssocInfo.name );
            if( assocInfo == null ) {
                continue;
            }

            if( assocInfo.terminateTrack ) {
                t.status = TkTrack.Status.TERMINATED;
                continue;
            }

            if( assocInfo.next == null ) {
                continue;
            }

            // See if the manual association for the next detection is in the
            // current list of detections. It may not be if there is a missing
            // detection at the current frame.
            int dIdx = detections.indexOf( assocInfo.next );

            if( dIdx == -1 ) {
                throw new Error( "Can not force track to associate with nothing in the next frame." );
            }

            trackDetectionAssoc[ tIdx ] = dIdx;

            ++numForced;
        }

        System.out.println( "Number of forced detection associations: " + numForced );

        return trackDetectionAssoc;
    }


    TkAssocScoreMatrix forceScores(
            TkAssocScoreMatrix scores,
            int[] trackDetectionAssoc ) {

        assert trackDetectionAssoc.length == scores.numTracks;

        double[][] scoresArray = scores.scores.toDoubleArray();

        double min = TkArray.min( scoresArray );
        double max = TkArray.max( scoresArray );

        final double factor = 10;

        double lo = min - (max - min)*factor;
        double hi = max + (max - min)*factor;

        double forceAssocScore, forceUnassocScore;

        if( scores.isDistance ) {
            forceAssocScore   = lo;
            forceUnassocScore = hi;
        }
        else
        {
            forceAssocScore   = hi;
            forceUnassocScore = lo;
        }

        System.out.println("forceToScore: " + (forceAssocScore) );

        forceAssocScore *= factor;

        TkAssocScoreMatrix forcedScores = scores.newCopy();

        for( int tIdx = 0; tIdx < trackDetectionAssoc.length; ++tIdx ) {
            int dIdx = trackDetectionAssoc[tIdx];

            if( dIdx == NO_FORCED_ASSOC ) {
                continue;
            }

            // forced association with a detection
            forcedScores.scores.setAsDouble( forceAssocScore, tIdx, dIdx );
        }
        
        return forcedScores;
    }

}
