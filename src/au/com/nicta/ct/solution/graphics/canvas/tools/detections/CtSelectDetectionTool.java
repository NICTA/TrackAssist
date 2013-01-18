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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.tools.CtCanvasTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtTransferableChangeListener;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
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
public class CtSelectDetectionTool extends CtCanvasTool {

//    CtDetectionsController _dc;
    CtTransferableChangeListener _tcl;// = new CtTransferableChangeListener();

    ArrayList< CtAbstractPair< CtItemState, CtItemState > > _mappings = new ArrayList< CtAbstractPair< CtItemState, CtItemState > >();
    
    public CtSelectDetectionTool( CtToolsModel tm, CtViewpointZoomCanvas zc ) {//, final CtEditMode editMode) {
        super( tm, "select-detection" );

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

        applyOnMouseClicked( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        updateEnabled();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override public void updateEnabled() {

        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }
        
        Collection< CtDetections > matching = tm.getDetectionsInWindow();

        int matches = matching.size();

        boolean valid = false;

        if( matches > 0 ) {
            valid = true;
        }

        setEnabled( valid );
    }

    @Override protected String iconFile() {
        return new String( "select_detection.png" );
    }

    @Override protected String toolTip() {
        return new String( "Select detection" );
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            return false;
        }

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        int x = (int)Math.rint( zc.toNaturalX( e.getX() ) );
        int y = (int)Math.rint( zc.toNaturalY( e.getY() ) );

        Point2D p2d = new Point2D.Float( x, y );

//        dc.setDetectionsStatesAt( p2d, CtItemState.SELECTED );
        boolean matchingDetections = tc.setDetectionsStatesInWindowAt( _mappings, p2d );
        return matchingDetections;
    }
}
