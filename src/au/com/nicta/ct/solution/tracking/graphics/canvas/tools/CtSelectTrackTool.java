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

package au.com.nicta.ct.solution.tracking.graphics.canvas.tools;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.tools.CtCanvasTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtSelectTrackTool extends CtCanvasTool {

    ArrayList< CtAbstractPair< CtItemState, CtItemState > > _mappings = new ArrayList< CtAbstractPair< CtItemState, CtItemState > >();
    
    public CtSelectTrackTool( CtToolsModel tm ) {//, CtSolutionController sc ) {//, final CtEditMode editMode) {
        super( tm, "select-track" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        sc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

        _mappings.add( new CtAbstractPair< CtItemState, CtItemState >( CtItemState.SELECTED, CtItemState.NORMAL ) );
        _mappings.add( new CtAbstractPair< CtItemState, CtItemState >( CtItemState.NORMAL, CtItemState.SELECTED ) );

        applyOnMouseClicked( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        updateEnabled();
    }

    public void onSolutionModelChanged() {
        updateEnabled();
    }

    @Override public void updateEnabled() {

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel tm = sc.getTrackingModel();

        Collection< CtTracks > ct = tm.getTracksInWindow();
        Collection< CtDetections > cd = tm.getOrphansInWindow();

        if( ct.isEmpty() && cd.isEmpty() ) {
            setEnabled( false );
        }
        else {
            setEnabled( true );
        }
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override protected String iconFile() {
        return new String( "select_track.png" );
    }

    @Override protected String toolTip() {
        return new String( "Select detection/track" );
    }

    @Override protected String toolDescription() {
        return "Select detections and tracks.";
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
//        CtSolutionController sc = (CtSolutionController)CtObjectDirectory.get( CtSolutionController.name() );

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        int x = (int)Math.rint( zc.toNaturalX( e.getX() ) );
        int y = (int)Math.rint( zc.toNaturalY( e.getY() ) );

        Point2D p2d = new Point2D.Float( x, y );

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return false;
        }

        boolean matchingItems = sc.setStatesInWindowAt( _mappings, p2d );
        return matchingItems;
    }
}
