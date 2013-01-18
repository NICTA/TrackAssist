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

import java.util.Arrays;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;



/**
 * Encapsulates association scores and assignment.
 * 
 * @author Alan
 */
public class TkAssocMatrix {

    public int[] trackToDetection;
    public int[] detectionToTrack;
    public int totalMatched = 0;

    private TkAssocMatrix() {
        // nothing
    }

    TkAssocMatrix( int numTracks, int numDetections ) {
        if( numTracks > 0 ) {
            trackToDetection = new int[numTracks];
            Arrays.fill(trackToDetection, 0, numTracks, -1);
        }
        if( numDetections > 0 ) {
            detectionToTrack = new int[numDetections];
            Arrays.fill(detectionToTrack, 0, numDetections, -1);
        }
    }
    
    public TkAssocMatrix newCopy() {
        TkAssocMatrix ret = new TkAssocMatrix();
        if( trackToDetection != null ) {
            ret.trackToDetection = Arrays.copyOf( trackToDetection, trackToDetection.length );
        }
        if( detectionToTrack != null ) {
            ret.detectionToTrack = Arrays.copyOf( detectionToTrack, detectionToTrack.length );
        }
        ret.totalMatched = totalMatched;
        return ret;
    }


    @Override
    public String toString() {
        String ret = "";
        ret = ret + "\ntotalMatched: " + totalMatched;
        ret = ret + "\ntrackToDetection: " + Arrays.toString( trackToDetection );
        ret = ret + "\ndetectionToTrack: " + Arrays.toString( detectionToTrack );
        return ret;
    }

}
