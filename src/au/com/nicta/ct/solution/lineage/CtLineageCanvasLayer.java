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

package au.com.nicta.ct.solution.lineage;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceController;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author davidjr
 */
public class CtLineageCanvasLayer extends CtViewpointCanvasLayer implements MouseListener {//implements CtChangeListener, CtCanvasPainter, MouseListener {

    public static final String CANVAS_LAYER_NAME = "lineage-canvas-layer";

//    protected CtLineageModel _lm;
//    protected CtZoomCanvas _zc;
//    protected CtCanvasLayer _cl;
    protected AffineTransform temp = new AffineTransform(); // saves allocation

    CtDockableWindowGrid _dwg;
    Collection< String > _windowTypes;

    public CtLineageCanvasLayer( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {
//        super();
//        setZoomCanvas( zc );
        super( CANVAS_LAYER_NAME );

        _dwg = dwg;
        _windowTypes = windowTypes;
//        CtLineageController.getModel().addModelChangeListener( this );

        CtTrackingModel tm = CtTrackingController.getModel();
        tm.addListener( this );

        CtLineageModel lm = CtLineageController.getModel();
        lm.addListener( this );

        // listen to the tracking model for appearance events, and other events
        // which may lead to structural changes in lineage. Don't need to listen
        // to lineage directly. Also note the default viewpoint handler for the
        // events is fine, cos it calls repaint()
        addMouseListener( this );
    }

    @Override public void stopListening() {
        super.stopListening();

        CtTrackingModel tm = CtTrackingController.getModel();
        tm.removeListener( this );

        CtLineageModel lm = CtLineageController.getModel();
        lm.removeListener( this );
    }
//    public void setZoomCanvas( CtZoomCanvas zc ) {
//
//        if( _zc != null ) {
//            _zc.detachLayer( "LineageLayer" );
//        }
//
//        _zc = zc;
//
//        try {
//            _cl = null;
//            _cl = new CtCanvasLayer();
//            _zc.addLayer( _cl, "LineageLayer" );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }
//
//        _cl.addMouseListener( this );
//        _cl.addPainter( this );
//    }
//
//    public CtCanvasLayer getCanvasLayer() {
//        return _cl;
//    }
//
//    public void setModel( CtLineageModel lm ) {
//        if( _lm != null ) {
//            _lm.changeSupport.removeListener( this );
//        }
//        _lm = lm;
//        _lm.changeSupport.addListener( this );
//    }

//    public void propertyChange( PropertyChangeEvent evt ) {
//        _cl.repaint(); // assume some model change, repaint everything
//    }
//
//    public void repaint() {
//        _cl.repaint();
//    }

    @Override public void paint( Graphics2D g ) {//, CtCanvasLayer cl ) {

         CtLineageModel lm = CtLineageController.getModel();
//        CtTrackingController sc = CtTrackingController.get();
//        sc.addSolutionModelChangeListener( this );// duplicates avoided but too slow!

        if( lm == null ) {
            return;
        }

        if( lm._cc == null ) {
            return;
        }
         
        if( lm._ism == null ) {
            return;
        }

        int minIndex = lm._ism.getMinIndex();
        int index = lm._ism.getIndex();
        int cols = lm.getWidth();
        int rows = lm.getHeight();

        double w = _zc.getNaturalWidth();
        double h = _zc.getNaturalHeight();

        double wCell = (double)w / (double)cols;
        double hCell = (double)h / (double)rows;
        double xCell0 = wCell * 0.5;
        double yCell0 = hCell * 0.5;

        double detectionSizeFactor = 0.9;
        double sizeCell = Math.min( wCell * 1.0, hCell * 0.5 ) * detectionSizeFactor;
        double xLine1 = wCell * (index-minIndex); // make zero based
        double xLine2 = xLine1 + wCell;

//        int x1 = (int)xLine1;
//        int x2 = (int)xLine2;
//        int y1 = 0;
//        int y2 = (int)h;
//
//        AffineTransform at = g.getTransform(); // save state
//
//        temp = _zc.getAffineToScreen( temp );//CtSubPixelResolution.getAffineToScreen( _zc, temp );
//
//        g.transform( temp );

        int x0s = (int)_zc.toScreenX( 0 ); // s = screen
        int y0s = (int)_zc.toScreenY( 0 );
        int ws  = (int)_zc.toScreenScale( w );
        int hs  = (int)_zc.toScreenScale( h );
        int y1s = y0s+hs;
        int wcs = (int)_zc.toScreenScale( wCell );
        int hcs = (int)_zc.toScreenScale( hCell );
        int x0cs = (int)_zc.toScreenX( xCell0 );
        int y0cs = (int)_zc.toScreenY( yCell0 );
        int x0ls = (int)_zc.toScreenX( xLine1 );
//        int x1ls = (int)_zc.toScreenX( xLine2 );
        int whcs = (int)_zc.toScreenScale( wCell * 0.5 );

        // BACKGROUND
        g.setColor( new Color( 90,90,90,120 ) );//Color.WHITE );
        g.fillRect( x0s, y0s, ws, hs );

        // HORIZONTAL STRIPES (alternating rows)
        g.setColor( new Color( 248,250,240,120 ) );
        for( int r = 0; r < rows; ++r ) {
            if( ( r % 2 ) > 0 ) continue;
            g.fillRect( x0s, (int)_zc.toScreenScale( hCell * r ) + y0s, ws, hcs );
        }

        // CURRENT TIME WINDOW INDICATOR
        g.setColor( CtStyle.BRIGHT_GREEN_TRANSLUCENT );

//                CtConstants.NictaPurple.getRed(), CtConstants.NictaPurple.getGreen(), CtConstants.NictaPurple.getBlue(), 127 ) );
        g.fillRect( x0ls, y0s, Math.max( wcs, 3 ), hs );
//        g.setColor( CtConstants.NictaYellow );
        Stroke stroke = g.getStroke();
        g.setStroke( CtStyle.THICK_STROKE );
        g.setColor( CtStyle.BRIGHT_GREEN );
        g.drawLine( x0ls+whcs, y0s, x0ls+whcs, y1s );
        g.setStroke( stroke );
        g.setColor( new Color( 128,128,128,128 ) );//Color.BLACK );
        g.drawRect( x0s, y0s, ws, hs );
//        g.setColor( Color.BLUE );

        CtTrackingController tc = CtTrackingController.get();
        CtTrackingModel tm = tc.getModel();

        CtSolutions s = lm.getSolution();
        Set< CtTracks > ct = s.getCtTrackses();

//        if( ct == null ) {
//            System.err.println( "ERROR: Set of tracks is null, should be non-null and empty." );
//            return;
//        }
//        int size = (int)( wCell * 0.2 );
//        int r = size >> 1;
//        double detectionSizeFactor = 0.2;
//        int size = (int)( _zc.toScreenScale( wCell ) * detectionSizeFactor );
        int diameter = (int)( _zc.toScreenScale( sizeCell       ) );
        int radius   = (int)( _zc.toScreenScale( sizeCell * 0.5 ) );
//        double _zc.toScreenScale( wCell )
//        int r = (int)( _zc.toScreenScale( wCell ) * detectionSizeFactor * 0.5 );
        
        for( CtTracks t : ct ) {

            int row = lm.getY( t );
            
            CtItemState ds = tm.getState( t );

            if( ds == CtItemState.SELECTED ) {
//                g.setColor( CtConstants.NictaGreen );
                g.setColor( CtStyle.BRIGHT_GREEN_TRANSLUCENT );
                g.fillRect( x0s+1, (int)_zc.toScreenScale( hCell * row ) + y0s, ws-1, hcs );
            }

            // DRAW *one* LINE BETWEEN START AND END OF TRACK
            CtAbstractPair< Integer, CtDetections > ap1 = tm.getTrackFirstDetection( t );
            CtAbstractPair< Integer, CtDetections > ap2 = tm.getTrackLastDetection ( t );

            if(    ( ap1 == null )
                || ( ap2 == null ) ) {
                System.err.println( "ERROR: Track with no first/last detection!?" );
                continue;
            }

            int col1 = ap1._first - minIndex;//lm.getX( d );
            int col2 = ap2._first - minIndex;//lm.getX( d );

            double yt  = _zc.toScreenScale( (double)row  * hCell ) + y0cs;
            double xt1 = _zc.toScreenScale( (double)col1 * wCell ) + x0cs;
            double xt2 = _zc.toScreenScale( (double)col2 * wCell ) + x0cs;

            g.setColor( Color.BLACK );
            g.drawLine( (int)xt1, (int)yt, (int)xt2, (int)yt );

            // DRAW *one* LINE to *parent* row IFF there is a parent:
            CtTracks t2 = lm.getParent( t ); // try to draw line to parent, if not root track:

            if( t2 != null ) {
                CtAbstractPair< Integer, CtDetections > ap3 = tm.getTrackLastDetection ( t2 );

                int col3 = ap3._first - minIndex;//lm.getX( d );
                int row3 = lm.getY( t2 );
                double yt3 = _zc.toScreenScale( (double)row3 * hCell ) + y0cs;
                double xt3 = _zc.toScreenScale( (double)col3 * wCell ) + x0cs;

                g.drawLine( (int)xt1, (int)yt, (int)xt3, (int)yt3 );
            }

//            int pkMin = 0;
//            int colMin = _lm.getWidth();
//
//            for( CtTracksDetections td : tds ) {
//                CtDetections d = td.getCtDetections();
//
//                int col = _lm.getX( d );
//
//                if( col < colMin ) {
//                    colMin = col;
//                    pkMin = d.getPkDetection();
//                }
//            }
//TODO: try painting the lines once per track, between first and last detection.
//This has more loops but less objects to paint.
//Then paint the individual detections.
//Problem: Cant change the state of the line at each step?
            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();
    
            for( CtTracksDetections td : tds ) {
                CtDetections d = td.getCtDetections();

//                if( d == null ) {
//                    System.err.println( "POJO not completely loaded?" );
//                    continue;
//                } // how?
                
                int col = lm.getX( d );

//                int x = (int)( wCell * (double)col + xCell0 - (double)r );
//                int y = (int)( hCell * (double)row + yCell0 - (double)r );
//                int x = (int)( wcs * (double)col + x0cs - (double)r );
//                int y = (int)( hcs * (double)row + y0cs - (double)r );
                double xr = _zc.toScreenScale( (double)col * wCell ) + x0cs;
                double yr = _zc.toScreenScale( (double)row * hCell ) + y0cs;

//                int xd1 = (int)( xr );
//                int yd1 = (int)( yr );

                int x = (int)( xr - (double)radius );
                int y = (int)( yr - (double)radius );

                g.setColor( CtConstants.NictaYellow ); //Color.BLACK );
//                g.fillOval( x, y, diameter,diameter );
                g.fillRect( x, y, diameter,diameter ); // DAVE: rectangles are faster
                g.setColor( Color.BLACK ); //Color.BLACK );
//                g.drawOval( x, y, diameter,diameter );
                g.drawRect( x, y, diameter,diameter );
            } // end: for-each detection
//                g.setColor( Color.BLACK ); // TODO: SetStyle (depending on track state)

//                int xd1 = (int)( wCell * (double)col + xCell0 );
//                int yd1 = (int)( hCell * (double)row + yCell0 );
//                int x = (int)( _zc.toScreenScale( (double)col * wCell ) + x0cs - (double)r );
//                int y = (int)( _zc.toScreenScale( (double)row * hCell ) + y0cs - (double)r );
//
//                boolean prior = false;
//
////////////////////////////////////////////////////////////////////////////////
//                int colMin = col;//_lm.getWidth();
//
//                for( CtTracksDetections td2 : tds ) {
//                    CtDetections d2 = td2.getCtDetections();
//                    if( d2.getPkDetection() == d.getPkDetection() ) continue;
//
//                    int col2 = lm.getX( d2 );
//
//                    if( col2 < colMin ) {
//                        colMin = col2;
//                    }
//
//                    if( col2 != (col -1) ) continue; // if not preceding col
//
//                    prior = true;
//
//                    break;
////                    int xd2 = (int)( wCell * (double)col2 + xCell0 );
////                    int xd2 = (int)( wcs * (double)col2 + x0cs );
////
////                    g.drawLine( xd1, yd1, xd2, yd1 );
//                }
////////////////////////////////////////////////////////////////////////////////
//
//                if( colMin != col ) { // if( first detection in sequence/track )
//                    prior = true;
//                }
//
//                if( prior ) {
//                    double xr2 = _zc.toScreenScale( (double)(col-1) * wCell ) + x0cs;
//                    int xd2 = (int)( xr2 );
//
//                    g.drawLine( xd1, yd1, xd2, yd1 );
//                }
//                else { // no prior track
//                    CtTracks t2 = lm.getParent( t ); // try to draw line to parent, if not root track:
//
//                    if( t2 != null ) {
//                        int row2 = lm.getY( t2 );
////                        int yd2 = (int)( hCell * (double)row2 + yCell0 );
////                        int yd2 = (int)( hcs * (double)row2 + y0cs );
//                        double yr2 = _zc.toScreenScale( (double)row2 * hCell ) + y0cs;
//
//                        int yd2 = (int)( yr2 );
//
//                        g.drawLine( xd1, yd1, xd1, yd2 );
//                    }
//                }
//            }
        }

//        g.setTransform( at ); // restore state

    }

//    public void mouseMoved(MouseEvent e) {}
//    public void mouseDragged(MouseEvent e) {}
    @Override public void mousePressed( MouseEvent e ) {}
    @Override public void mouseReleased( MouseEvent e ) {}
    @Override public void mouseEntered( MouseEvent e ) {}
    @Override public void mouseExited( MouseEvent e ) {}
    @Override public void mouseClicked( MouseEvent e ) {
//        System.out.println("Mouse clicked"+ e);
        if( _zc == null ) {
            return;
        }

        int x = e.getX();
        int y = e.getY();

        double xn = _zc.toNaturalX( x ); // s = screen
        double yn = _zc.toNaturalY( y );

        System.out.println( "Mouse clicked @"+xn+","+yn );

        CtLineageModel lm = CtLineageController.getModel();

        if( lm == null ) {
            return;
        }

        if( lm._ism == null ) {
            return;
        }

        double w = _zc.getNaturalWidth();
        double h = _zc.getNaturalHeight();
        int cols = lm.getWidth();
        int rows = lm.getHeight();
        double wCell = (double)w / (double)cols;
        double hCell = (double)h / (double)rows;
        double xCell = xn / wCell;
        double yCell = yn / hCell;

        int trackClicked = (int)yCell;
        int indexClicked = (int)xCell;
//        int index = lm._ism.getIndex();
        int minIndex = lm._ism.getMinIndex();

        if( indexClicked < 0 ) {
            return;
        }
        
        if( indexClicked >= cols ) {
            return;
        }

        // get the relevant track:
        if( e.getClickCount() > 1 ) {
            CtTracks t = lm.getTrack( trackClicked );

            if( t != null ) {
                CtTrackingController tc = CtTrackingController.get();
                CtTrackingModel tm = tc.getModel();
                CtItemState ds1 = tm.getState( t );
                CtItemState ds2 = CtItemState.SELECTED;
                if( ds1 == CtItemState.SELECTED ) {
                    ds2 = CtItemState.NORMAL;
                }

                tc.setState( t, ds2 );
//                problem: doesnt repaint on update.
            }

            return;
        }

//        if( indexClicked == index ) {
//            return;
//        }

        // for starters, let's do time:
//        CtExperimentModel em = CtExperimentModel.get();
//        CtImageSequenceController isc = em._isf.getController();
        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        CtImageSequenceController isc = cm.getImageSequenceController();

        isc.setCurrentIndex( indexClicked+minIndex );

        // then space:
        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            return;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            return;
        }

        CtTracks t = lm.getTrack( trackClicked );

        if( t == null ) {
            return;
        }

        CtDetections d = tm.getTrackDetectionAtIndex( t, indexClicked+minIndex );

        if( d == null ) {
            return;
        }

////////////////////////////////////////////////////////////////////////////////
        Rectangle2D boundingBox = tm.getBoundingBox( d );

        Collection< CtZoomCanvasPanel > czcp = CtViewpointZoomCanvasPanelFactory.find( _dwg, _windowTypes );

        for( CtZoomCanvasPanel zcp : czcp ) {
            CtZoomCanvas zc = zcp.getZoomCanvas();
            zc.zoomNaturalWindowAround( boundingBox, 0.2 );
        }

//
//        CtCanvasLayer cl = _zc.getLayer( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
//
//        if( cl == null ) {
//            return;
//        }
//
//        CtTrackingCanvasLayer tv = (CtTrackingCanvasLayer)cl;//tc.getTrackingView();
//
////        if( tv == null ) {
////            return;
////        }
//
//        tv.show( d );
    }

}

