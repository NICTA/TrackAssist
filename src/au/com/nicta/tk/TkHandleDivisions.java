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

/**
 *
 * @author Alan
 */
public class TkHandleDivisions {

    TkStringKey<TkLJIPDAState> stateKey;

    public TkHandleDivisions( TkStringKey<TkLJIPDAState> stateKey ) {
        this.stateKey = stateKey;
    }

    public void process( TkTracks tracks ) {

        int splitCount = 0;

        // We'll be modifying tracks, so can't iterate over it directly.
        TkTrack[] tracksCopy = new TkTrack[ tracks.size() ];
        tracks.toArray( tracksCopy );

        // find all new tracks
        for( TkTrack t: tracksCopy ) {
            // if not a root then it's already a split track, so don't touch it.
            if( !t.isRoot() ) {
                continue;
            }

            if( lookForDivisions( t, tracks ) ) {
                ++splitCount;
            }
        }

        System.out.println( "Number of divisions: " + splitCount );
    }

    boolean lookForDivisions( TkTrack track, TkTracks tracks ) {
        TkDetection detectionT1 = track.getFirst().det;
        double r1 = detectionT1.find(TkCell.name).radius;

        int timeIdxT0 = detectionT1.timeIdx - 1; // one step earlier

        // now have a detection that's 1 time step back
        TkLJIPDAState stateT1 = track.getFirst().find(stateKey);

        double best = Double.MAX_VALUE;
        TkTrack bestParent = null;
        int bestParentElementIdx = -1;

        for( TkTrack t :tracks ) {
            
            int elementIdx = findElementAtTimeIdx( t, timeIdxT0 );
            if( elementIdx < 0 ) {
                continue; // this track goes back to the first image, so ignore it
            }

            // consider splitting tracks that appear after the 1st frame
            TkTrackElement e = t.elements.get(elementIdx);

            TkLJIPDAState stateT0 = e.find(stateKey);

            Matrix innovCovInv = stateT0.innovCov.inv(); // could be non-invertible?

            // innov = stateT1 - stateT0
            if( stateT0.prior.state == null ) {
                continue;
            }
//try{
            // test to see if this track t could be the parent/sibling of track
            Matrix innov = stateT1.post.state.minus( stateT0.prior.state );

            double chi = innov.transpose().mtimes( innovCovInv  ).mtimes( innov ).doubleValue();

//            final int DOF = 2;
//            final int NUM_DECIMAL_PLACES = 10;
//            double p = 1 - ChiSquareDist.cdf(DOF, NUM_DECIMAL_PLACES, chi);
//            // add size penalty for division event for siblings
//            final double SIBLING_RADIUS_STD = 2.5; // gaussian
//            if( t.elements.size() > elementIdx+1 ) { // only if parent track has more elements
//                TkDetection d2 = t.elements.get(elementIdx+1).det;
//                if( d2 != null ) {
//                    double r2 = d2.find(TkCell.name).radius;
//                    double diff = r2 - r1;
//                    double w = Math.exp( -1 * (diff/SIBLING_RADIUS_STD) * (diff/SIBLING_RADIUS_STD) );
//                    p *= w;
//                }
//            }
//
//            // add size penalty for division event for parent-child
//            final double SHRINK_FACTOR = 0.7; // gaussian
//            final double SHRINK_RADIUS_STD = 1; // gaussian
//            double parentR = e.det.find(TkCell.name).radius;
//            double diff = parentR*SHRINK_FACTOR - r1;
//            double w = Math.exp( -1 * (diff/SHRINK_RADIUS_STD) * (diff/SHRINK_RADIUS_STD) );
//            p *= w;

            // keep maximum
//            if( bestP < p ) {
//                bestP = p;
//                bestParent = t;
//                bestParentElementIdx = elementIdx;
//            }


            // keep minimum
            if( best > chi ) {
                best = chi;
                bestParent = t;
                bestParentElementIdx = elementIdx;
            }
//}
//catch( NullPointerException npe ) {
//    int gg = 0;
//}
        }

        if( bestParent == null ) {
            return false; // no candidate to split
        }

        // cut the single track into two and insert the new track
        TkTrack child = cutTrack( bestParent, bestParentElementIdx );
        if(    !child.elements.isEmpty()
            &&  child.elements.get(0).det != null ) {
            tracks.add( child );
        }
        if( bestParent.elements.isEmpty() ) {
            throw new Error("Empty track");
        }

        // link the
        track.setParent(bestParent);

        return true;
    }
    
    int findElementAtTimeIdx( TkTrack t, int timeIdx ) {
        for( int i = 0; i < t.elements.size(); ++i ) {
            TkTrackElement e = t.elements.get(i);
            if(    e.det != null
                && e.det.timeIdx == timeIdx ) {
                return i;
            }
        }
        return -1;
    }

    TkTrack cutTrack( TkTrack track, int cutAfterThisElementIdx ) {
        int cutBefore = cutAfterThisElementIdx + 1;

        TkTrack newTrack = new TkTrack();

        // copy into new track
        for( int i = cutBefore; i < track.elements.size(); ++i ) {
            newTrack.elements.add( track.elements.get(i) );
        }

        // remove from old track
        track.elements.subList( cutBefore, track.elements.size() ).clear();

        // link new and old track
        newTrack.setParent(track);

        return newTrack;
    }

}
