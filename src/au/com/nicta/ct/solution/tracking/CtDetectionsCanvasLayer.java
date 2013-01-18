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
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.Graphics2D;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtDetectionsCanvasLayer extends CtViewpointCanvasLayer { //implements CtCanvasPainter, CtChangeListener {

    public static final String CANVAS_LAYER_NAME = "detections-canvas-layer";

    public static void addFactoryTo( CtViewpointZoomCanvasPanelFactory zcpf ) {
        zcpf._canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtDetectionsCanvasLayer();
            }
        } );
    }

    protected CtZoomTrackPainter _ztp = new CtZoomTrackPainter(); // TODO Make persistent

    public CtDetectionsCanvasLayer() {
//        setZoomCanvas( zc );
        super( CANVAS_LAYER_NAME );
        CtTrackingController.get().addModelChangeListener( this );
        CtTrackingController.get().addAppearanceChangeListener( this );
    }

    public CtZoomTrackPainter getZoomTrackPainter() {
        return _ztp;
    }
//
//
//    public void setZoomCanvas( CtZoomCanvas zc ) {
//
//        if( _zc != null ) {
//            _zc.detachLayer( "DetectionLayer" );
//        }
//
//        _zc = zc;
//
//        try {
//            _cl = null;
//            _cl = new CtCanvasLayer();
//            _zc.addLayer( _cl, "DetectionLayer" );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }
//
//        _cl.addPainter( this );
//    }
//
//    public CtCanvasLayer getCanvasLayer() {
//        return _cl;
//    }
//
//    @Override public void propertyChange( PropertyChangeEvent evt ) {
//        repaint();
//    }
//
//    public void repaint() {
//
////        _zc.repaint(); // assume some model change, repaint everything
//        _cl.repaint();
//    }

    @Override public void paint( Graphics2D g ) {//, CtCanvasLayer cl ) {

        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            return;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            return;
        }

        if( !tm.valid() ) {
            return;
        }

        CtViewpointController vc = _zc.getViewpointController();
        int currentIndex = vc.getTimeOrdinate();
//        int currentIndex = tm._ism.currentIndex();// - and + is navigatin the current axes

        Collection< CtDetections > cd = tm._s.getCtDetectionses();
        
        for( CtDetections d : cd ) {

            int detectionIndex = tm.getTimeOrdinate( d );

            if( !tm._twm.isInWindow( currentIndex, detectionIndex ) ) {
                continue;
            }

            CtItemState is = tm.getState( d );
            CtZoomPolygon zp = tm.getBoundary( d );
            CtItemState is2 = mapItemState( is, currentIndex, detectionIndex );

            _ztp.paintDetectionContour( g, _zc, this, zp, is2 );
        }
    }

    protected CtItemState mapItemState( CtItemState is, int currentIndex, int itemIndex ) {
        // if different time, reduce visual impact of normally presented items.
        if( currentIndex != itemIndex ) {
            if( is == CtItemState.NORMAL ) {
                return CtItemState.IGNORE;
            }
        }
        return is;
    }

//    protected void paint( Graphics2D g, CtCanvasLayer cl, CtDetectionsModel dm, CtDetections d, CtPolygonStyle zps ) {
//        CtZoomPolygon zp = dm.getBoundary( d );
//        zp.paint( g, _zc, zps );
//    }
//
}
