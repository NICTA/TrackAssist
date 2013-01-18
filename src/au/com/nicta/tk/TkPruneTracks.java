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

import au.com.nicta.tk.tree.TkTree;
import java.util.ArrayList;

/**
 *
 * @author alan
 */
public class TkPruneTracks {
    /*
     * Perform post processing on tracks, eg. remove short tracks
     *
     */

//    public int minNumDetectionsPerFinalTrack = 0; // min no. of detections in a track that terminates
    public int minNumDetectionsPerTrack = 0;
    public double maxMissingDetectionRatePerTrack = 0.3;

    public TkPruneTracks() {
    }

    public TkTracks process( TkTracks tracks ) {
        int trackCount = tracks.size();

        tracks = removeShortTracks( tracks );
        tracks = removeFalseSplits( tracks );

        System.out.println( "Number of tracks removed: " + (trackCount-tracks.size()) );

        return tracks;
    }

    public TkTracks removeFalseSplits( TkTracks tracks ) {
        TkTracks result = new TkTracks();

        for( TkTrack child: tracks ) {
            if( child.getParent() != null ) {
                ArrayList<TkTree> siblings = child.getParent().getCopyOfChildren();
                if( siblings.size() == 1 ) { // i.e. false split, parent only has this current child
                    child.mergeToParent();
                    continue;
                }
            }
            result.add(child);
        }

        return result;
    }

    public TkTracks removeShortTracks( TkTracks tracks ) {
        TkTracks result = new TkTracks();

        // find all new tracks
        for( TkTrack t: tracks ) {
            if( isBadTrack( t ) ) {
                t.resetParent();
                continue;
            }
            result.add(t);
        }

        return result;
    }

    public boolean isBadTrack( TkTrack t ) {
        if( t.elements.size() < minNumDetectionsPerTrack ) {
            return true;
        }

//        if(    !t.hasChildren()
//            && t.elements.size() < minNumDetectionsPerFinalTrack ) {
//            return true;
//        }


        int cnt = 0;
        for( TkTrackElement e : t.elements ) {
            if( e.det != null ) {
                ++cnt;
            }
        }
        if(    cnt < minNumDetectionsPerTrack
            && !t.hasChildren() ) {
            return true;
        }

        double fractionOfMissingDetections = 1 - cnt / (float)t.elements.size();
        if( fractionOfMissingDetections > maxMissingDetectionRatePerTrack ) {
            return true;
        }

        return false;
    }

}
