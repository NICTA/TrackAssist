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
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import au.com.nicta.ct.orm.interactive.CtQuantityItemStateConstraint;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtMoveDetectionTool extends CtCanvasTool {

    CtQuantityItemStateConstraint _qisc = new CtQuantityItemStateConstraint();
//
//    Collection< CtDetections > _moving = null;

    public CtMoveDetectionTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "move-detection" );

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

//        _states.add( CtDetectionState.NORMAL );
        _qisc._states.add( CtItemState.SELECTED );

        applyOnMouseMoved( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        applyOnMousePressed( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        applyOnMouseDragged( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        applyOnMouseReleased( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );

        updateEnabled();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override protected String iconFile() {
        return new String( "detection_move.png" );
    }

    @Override protected String toolTip() {
        return new String( "Move detection" );
    }

    @Override protected String toolDescription() {
        return "Drag detections to move them.";
    }

    @Override public void updateEnabled() {

        CtTrackingModel tm = CtTrackingController.getModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }

        if( _qisc == null ) {
            setEnabled( false );
            return;
        }
        
        Collection< CtDetections > cd = tm.getDetectionsWithStatesInWindow( _qisc._states );

        int quantity = 0;

        if( cd != null ) {
            quantity = cd.size();
        }

        boolean valid = _qisc.isSatisfied( quantity );

        setEnabled( valid );
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
        return dragging;
//        return false;
    }

    boolean dragging = false;
    int mouseX = 0;
    int mouseY = 0;
    double dragErrorX = 0;
    double dragErrorY = 0;

    @Override public void mouseClicked( MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }

        apply( e, cl );
    }

    @Override public void mousePressed( MouseEvent e, CtCanvasLayer cl ) {

        if( !isActive() ) {
            return;
        }

        dragging = false;
        mouseX = e.getX();
        mouseY = e.getY();
        dragErrorX = 0;
        dragErrorY = 0;

//        CtZoomCanvas zc = (CtZoomCanvas)_cl.getParent();
//
//        int x = (int)Math.rint( zc.toNaturalX( e.getX() ) );
//        int y = (int)Math.rint( zc.toNaturalY( e.getY() ) );
//
//        Point2D p2d = new Point2D.Float( x, y );
//
//        CtDetectionsController dc = CtDetectionsController.get();//(CtDetectionsController)CtObjectDirectory.get( CtDetectionsController.name() );
//        CtDetectionsModel dm = dc.getModel();
//        _moving = dm.getDetections( p2d );
//
//        if( _moving != null ) {
//            if( !_moving.isEmpty() ) {
                apply( e, cl );
//            }
//        }
    }

    public void mouseReleased( MouseEvent e, CtCanvasLayer cl ) {

        dragging = false;
        
        if( !isActive() ) {
            return;
        }

//        if( _moving != null ) {
//            _moving = null;
            apply( e, cl );
//        }
    }

//    public void mouseMoved(MouseEvent e) {
//        if( !isActive() ) {
//            return;
//        }
//
//        if( _moving == null ) {
//            return;
//        }
//
//        if( _moving.isEmpty() ) {
//            return;
//        }
//
//        apply( e );
//    }

    @Override public void mouseDragged( MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return;
        }

        Collection< CtDetections > cd = tc.getSelectedDetections();

        if( cd == null ) {
            return;
        }

        if( cd.isEmpty() ) {
            return;
        }
//        if( _moving == null ) {
//            return;
//        }
//
//        if( _moving.isEmpty() ) {
//            return;
//        }

        dragging = true;

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        double dx = (e.getX() - mouseX) / zc.getZoomScale() * CtSubPixelResolution.unitsPerNaturalPixel;
        double dy = (e.getY() - mouseY) / zc.getZoomScale() * CtSubPixelResolution.unitsPerNaturalPixel;

        dragErrorX += dx;
        dragErrorY += dy;

        int ix = (int)Math.rint(dragErrorX);
        int iy = (int)Math.rint(dragErrorY);

        dragErrorX -= ix;
        dragErrorY -= iy;

//        CtDetectionsModel dm = _dc.getModel();
//
//        for( CtDetections d : _moving ) {
//            CtZoomPolygon zp = dm.getBoundary( d );
//
//            zp.translate( ix, iy );
//        }
//
//        CtTrackingController tc = CtTrackingController.get();//(CtDetectionsController)CtObjectDirectory.get( CtDetectionsController.name() );
//        dc.move( _moving, ix, iy );
        tc.translateSelectedDetections( ix, iy );
//        currentDetection.detectionPolygon.polygon.translate(ix, iy);
//        currentDetection.changeSupport.fire(CtDetection.EVT_DETECTION_CHANGED);
//        changeSupport.fire(EVT_DETECTION_MOVED);

        mouseX = e.getX();
        mouseY = e.getY();

        apply( e, cl );
        cl.repaint();
    }

}
