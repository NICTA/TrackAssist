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

package au.com.nicta.ct.solution.tracking;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 *
 * @author davidjr
 */
public class CtTrackingLoader extends CtEventDispatchThreadProgress {

    CtTrackingModel _tm;
    Set< CtDetections > _cd;
    Set< CtTracks > _ct;

    public CtTrackingLoader( CtTrackingModel tm ) {
        super( "Loading tracks & detections... ", "tracking-loader", 0 );

        _tm = tm;
        _cd = _tm._s.getCtDetectionses();
        _ct = _tm._s.getCtTrackses();
    }

    @Override public int getLength() {
        if(    ( _cd == null )
            || ( _ct == null ) ) {
            return 0;
        }

        int length = _cd.size() + _ct.size() +1;
        return length;
    }

    @Override public void doStep( int step ) {

        if( _tm == null ) {
            return;
        }

        int stepCounter = 0;

        for( CtDetections d : _cd ) {

            if( step != stepCounter ) {
                ++stepCounter;
                continue;
            }

            if( d.getCtTracksDetectionses().isEmpty() ) {
                _tm._orphans.add( d );
            }

            _tm._detectionsStates.put( d, CtItemState.NORMAL );
            _tm._detectionsBoundaries.put( d, new CtZoomPolygon( d.getBoundary() ) );

            return; // done one step.
        }

        for( CtTracks t : _ct ) {

            if( step != stepCounter ) {
                ++stepCounter;
                continue;
            }

            _tm._tracksStates.put( t, CtItemState.NORMAL );
            _tm.updateTracksSequencedDetections( t );

            return; // done one step.
        }

        _tm.fireModelChanged();
    }
}
