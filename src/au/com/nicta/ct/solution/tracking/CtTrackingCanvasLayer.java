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

import au.com.nicta.ct.orm.patterns.CtQueue3;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author davidjr
 */
public class CtTrackingCanvasLayer extends CtViewpointCanvasLayer {//implements CtCanvasPainter, CtChangeListener {

    public static final String CANVAS_LAYER_NAME = "tracking-canvas-layer";
    public static final double SHOW_DETECTION_BACKGROUND_RADIUS = 2.5; // show 2.5x the dia. of the detection on each side.

    public static void addFactoryTo( CtViewpointZoomCanvasPanelFactory zcpf ) {
        zcpf._canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtTrackingCanvasLayer();
            }
        } );
    }

    protected CtZoomTrackPainter _ztp = new CtZoomTrackPainter(); // TODO Make persistent

    public CtTrackingCanvasLayer() {
//        setZoomCanvas( zc );
        super( CANVAS_LAYER_NAME );
        CtTrackingController.get().addModelChangeListener( this );
        CtTrackingController.get().addAppearanceChangeListener( this );
    }

    public CtZoomTrackPainter getZoomTrackPainter() {
        return _ztp;
    }
//
//    public void setZoomCanvas( CtZoomCanvas zc ) {
//
//        if( _zc != null ) {
//            _zc.detachLayer( "TrackingLayer" );
//        }
//
//        _zc = zc;
//
//        try {
//            _cl = null;
//            _cl = new CtCanvasLayer();
//            _zc.addLayer( _cl, "TrackingLayer" );
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

//    public Rectangle2D getBoundingBox( CtDetections d ) {
//        CtTrackingController tc = CtTrackingController.get();//dc;
//
//        if( tc == null ) {
//            return;
//        }
//
//        CtTrackingModel tm = tc.getTrackingModel();
//
//        if( tm == null ) {
//            return;
//        }
//
//        CtZoomPolygon zp = tm.getBoundary( d );
//        Rectangle2D r2d = zp.getBoundingBox();
//        double w = r2d.getWidth();
//        double h = r2d.getHeight();
//
//        double wWindow = w * SHOW_DETECTION_BACKGROUND_RADIUS;
//        double hWindow = h * SHOW_DETECTION_BACKGROUND_RADIUS;
//        double xWindow = r2d.getCenterX() - (wWindow * 0.5);
//        double yWindow = r2d.getCenterY() - (hWindow * 0.5);
//
//        double reciprocal = 1.0 / CtSubPixelResolution.unitsPerNaturalPixel;
//
//        xWindow *= reciprocal;
//        yWindow *= reciprocal;
//        wWindow *= reciprocal;
//        hWindow *= reciprocal;
//
//        _zc.zoomNaturalWindow( xWindow, yWindow, wWindow, hWindow );
//    }

//    public void propertyChange( PropertyChangeEvent evt ) {
//        repaint();
//    }
//
//    public void repaint() {
//
////        _zc.repaint(); // assume some model change, repaint everything
//        _cl.repaint();
//    }
//
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

        // paint orphans:
        CtViewpointController vc = _zc.getViewpointController();
        int currentIndex = vc.getTimeOrdinate();
//        int currentIndex = tm._ism.currentIndex();// - and + is navigatin the current axes

        Collection< CtDetections > orphans = tm.getOrphans();

        for( CtDetections d : orphans ) {

            int detectionIndex = tm.getTimeOrdinate( d );

            if( !tm._twm.isInWindow( currentIndex, detectionIndex ) ) {
                continue;
            }

            CtItemState is = tm.getState( d );
            CtZoomPolygon zp = tm.getBoundary( d );
//            CtItemState is2 = mapItemState( is, currentIndex, detectionIndex );

            _ztp.paintDetectionCircle( g, _zc, this, zp, is, currentIndex, detectionIndex, true, false, false );
        }

        // paint tracked detections in tracks:
        CtQueue3< CtAbstractPair< CtDetections, Integer > > rt = new CtQueue3< CtAbstractPair< CtDetections, Integer > >();
        
        Set< Entry< CtTracks, TreeMap< Integer, CtDetections > > > es = tm._tracksSequencedDetections.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< CtTracks, TreeMap< Integer, CtDetections > > e = (Entry< CtTracks, TreeMap< Integer, CtDetections > >)i.next();

            CtTracks t = e.getKey();
            CtItemState is = tm.getState( t );

            TreeMap< Integer, CtDetections > hm = e.getValue();

            // remember previous and next detections.
            Set< Entry< Integer, CtDetections > > es_t = hm.entrySet();

            Iterator i_t = es_t.iterator();

            while( i_t.hasNext() ) {

                rt.rotateAB( false ); // A := B

                Entry< Integer, CtDetections > e_t = (Entry< Integer, CtDetections >)i_t.next();

                int detectionIndex = e_t.getKey();

//                if( !tm._twm.isInWindow( currentIndex, detectionIndex ) ) {
//                    continue;
//                }
//
                rt._c = new CtAbstractPair< CtDetections, Integer >( e_t.getValue(), detectionIndex ); // always append all detections in track.. even if not within time window

                paintTrackTuple( g, this, currentIndex, tm, is, rt );
            }

            while( rt.anyNotNull() ) {
                rt.rotateAB( false ); // A := B

                paintTrackTuple( g, this, currentIndex, tm, is, rt );
            }
        }

        super.paint( g ); // call base class to paint sub-layers e.g. legened layer
    }

    protected void paintTrackTuple(
            Graphics2D g,
            CtCanvasLayer cl,
            int currentIndex,
            CtTrackingModel tm,
            CtItemState is,
            CtQueue3< CtAbstractPair< CtDetections, Integer > > t ) {

        // using unfiltered list determine if (b) is start or end of a track:
        boolean isStart = false;
        boolean isEnd   = false;

        if( t._a == null ) {
            isStart = true;
        }
        if( t._c == null ) {
            isEnd = true;
        }

        // produce a time-window-filtered queue that only includes detections within the observable time-window:
        CtQueue3< CtAbstractPair< CtDetections, Integer > > t2 = new CtQueue3< CtAbstractPair< CtDetections, Integer > >();

        if( ( t._a != null ) && tm._twm.isInWindow( currentIndex, t._a._second ) ) { t2._a = t._a; }
        if( ( t._b != null ) && tm._twm.isInWindow( currentIndex, t._b._second ) ) { t2._b = t._b; }
        if( ( t._c != null ) && tm._twm.isInWindow( currentIndex, t._c._second ) ) { t2._c = t._c; }

        if( t2._b == null ) {
            return;
        }

        CtZoomPolygon zpA = null;
        CtZoomPolygon zpB = null;
        CtZoomPolygon zpC = null;

//        CtItemState isA = is;
//        CtItemState isB = is;
//        CtItemState isC = is;

        int indexA = 0;
        int indexB = 0;
        int indexC = 0;

        if( t2._a != null ) { zpA = tm.getBoundary( t2._a._first ); indexA = t2._a._second; }//isA = mapItemState( is, currentIndex, t2._a._second ); }
        if( t2._b != null ) { zpB = tm.getBoundary( t2._b._first ); indexB = t2._b._second; }//isB = mapItemState( is, currentIndex, t2._b._second ); }
        if( t2._c != null ) { zpC = tm.getBoundary( t2._c._first ); indexC = t2._c._second; }//isC = mapItemState( is, currentIndex, t2._c._second ); }

        _ztp.paintTrackTuple( g, _zc, cl, currentIndex, is, zpA, indexA, zpB, indexB, zpC, indexC, isStart, isEnd ); // paint( track, trackState, prev.det, this.det, next.det )
    }

// DAVE: This mechanism is too limiting
//    protected CtItemState mapItemState( CtItemState is, int currentIndex, int itemIndex ) {
//        // if different time, reduce visual impact of normally presented items.
//        if( currentIndex != itemIndex ) {
//            if( is == CtItemState.NORMAL ) {
//                return CtItemState.IGNORE;
//            }
//        }
//        return is;
//    }
////        int future  = _sm._twm._future;
////        int history = Math.abs( _sm._twm._history );
//
//        Set< CtImages > window = sm._imagesDetectionsModels.keySet();
//
//        for( CtImages i : window ) {
//            CtDetectionsModel dm = sm._imagesDetectionsModels.get( i );
//
//            Collection< CtDetections > ds = dm.getDetections();
//
////            int index = _sm.getSequenceIndex( i );
//
//            for( CtDetections d : ds ) {
//                paint( g, cl, i, /*index,*/ current, sm, dm, d );
//            }
//        }
//
//        // for painting tracks, paint everything on each image at a time.
//        // paint each image
//        CtImages i0 = null;
//        CtImages i1 = null;
//
//        for( int index = (current-history); index <= (current+future); ++index ) {
//            try {
//                i0 = i1;
//
//                i1 = _sm._ism.get( index );
//
//                if( i0 != null ) {
//                    paintTracks( g, cl, i0,i1 );
//                }
//            }
//            catch( Exception e ){} // when I go past the ends of the video
//        }

//        Collection< CtTracks > tracks = _sm.getTracks();
//
//        for( CtTracks t : tracks ) {
//            paint( g, cl, t );
//        }
// style should depend on history time??
//        AbstractCollection< CtDetections > ac = _dm.getDetections();
//
//        for( CtDetections d : ac ) {
//            CtItemState ds = _dm.getState( d );
//            CtPolygonStyle s = _stateStyles.get( ds );
//            paint( g, cl, d, s );
//        }
//    }

//    protected void paintTracks( Graphics2D g, CtCanvasLayer cl, CtImages i1, CtImages i2 ) {
//
//    }
//
//    protected void paint( Graphics2D g, CtCanvasLayer cl, CtTracks t ) {
//
//        // order the detections in the track in image sequence order...
//
//    }

//    protected void getAdjacentDetections( CtDetections d, Collection< CtDetections > before, Collection< CtDetections > after, CtImageSequenceModel ism, int index ) {
//        Set< CtTracksDetections > tds = d.getCtTracksDetectionses();
//
//        if( tds == null ) {
//            return;
//        }
//
//        if( tds.isEmpty() ) {
//            return;
//        }
//
//        // although we allow detection to be in multiple tracks, this will get the first..
//        CtTracksDetections td = tds.iterator().next();
//        CtTracks t = td.getCtTracks();
//
//        Set< CtTracksDetections > td no this is too big.
//
//        return t;
//    }

//    protected void paint( Graphics2D g, CtCanvasLayer cl, CtImages i, /*int index,*/ int current, CtTrackingModel sm, CtDetectionsModel dm, CtDetections d1 ) {
//
////        _sm.printSelected();
//
//        CtZoomTrackPainter ztp = new CtZoomTrackPainter(); // TODO Make persistent
//
//        int currentIndex = sm._ism.currentIndex();
//        int index = sm._cc.getTimeOrdinate( i );//sm._ism.index( i );
//        int relativeTime = index - currentIndex;
////        int relativeTime = index - current;
////System.out.println("rel.t="+relativeTime );
//
//        CtPolygonStyle ps = getStyle( i, relativeTime, sm, dm, d1 );
//        CtZoomPolygon zp1 = dm.getBoundary( d1 );
//
////        zp1.paint( g, _zc, ps );
//
//        boolean isOrphan = sm.isOrphan( d1 );
//
//        if( isOrphan ) {
//            ztp.paintAt( g, _zc, zp1, ps, null );
//            return;
//        }
////        else { // paint always... ie paint detections that are in tracks
////            ztp.paintAt( g, _zc, zp1, ps, null );
////        }
//
//         // can I get the next detection in the track?
////        CtTracks t = _sm.getTrack( d1 );
//        Collection< CtTracks > ct = sm.getTracks( d1 );
//
//        for( CtTracks t : ct ) {
//
////System.out.println("painting track pk="+t.getPkTrack() );
//
//            CtLineStyle ls = getStyle( t, sm );
//
//            if( relativeTime == 0 ) { // in a track, current detection:
//                ztp.paintAt( g, _zc, zp1, ps, ls );
//            }
//
//            HashMap< Integer, CtDetections > hm = sm.getTracksSequencedDetections( t );
//
//            CtDetections before = hm.get( index -1 );
//            CtDetections after  = hm.get( index +1 );
//
//            CtDetections d2 = null;
//
//            if( after == null ) { // if nothing after
//                d2 = d1; // after = this
//
//                CtImages i2 = d2.getCtImages();
//                CtDetectionsModel dm2 = sm._imagesDetectionsModels.get( i2 );
//                CtZoomPolygon zp2 = dm2.getBoundary( d2 );
//
//                ztp.paintTerminus( g, _zc, zp2, ls );
//
//                if( before != null ) {
////                    return;
//                    continue;
//                }
//
//                ztp.paintBetween( g, _zc, zp1, zp2, ls );
//            }
//            else { // there are later detections
//    //            for( CtDetections d : after ) {
//                    d2 = after; //d;
//
//                    CtImages i2 = d2.getCtImages();
//                    CtDetectionsModel dm2 = sm._imagesDetectionsModels.get( i2 );
//
//                    if( dm2 == null ) {
//    //                    continue;
//                        return;
//                    }
//
//                    CtZoomPolygon zp2 = dm2.getBoundary( d2 );
//
//                    ztp.paintBetween( g, _zc, zp1, zp2, ls );
////            }
//            } // else
//        } // for each track
//    }

//    public CtLineStyle getTrackStyle( CtTracks t, CtTrackingModel sm ) {
//        CtItemState ds = sm.getState( t );
////System.out.println( "paint track t="+t.getPkTrack()+" with style="+ds );
//        return _trackStyles.get( ds );
//    }
//
//    public CtPolygonStyle getDetectionStyle( CtImages i, int relativeTime, CtTrackingModel sm, CtDetectionsModel dm, CtDetections d ) {
//
//        // orphan can be selected individually..? Style determined by DM in SM.
//        if( sm.isOrphan( d ) ) {
//
//            CtItemState ds = dm.getState( d );
//
//            if(    ( ds == CtItemState.NORMAL )
//                && ( relativeTime != 0 ) ) {
//                ds = CtItemState.IGNORE;
//            }
//
//            CtPolygonStyle s = _detectionStyles.get( ds );
//
//            return s;
//        }
//
//        // now in the case that a detection is in a track:
//        try {
//            Collection< CtTracks > ct = sm.getTracks( d );
//
//            // if in 1 track, this is easy.
//            // if in >1 track, it's at a fork. Then select if any track is selected.
//            CtItemState ds = CtItemState.NORMAL;
//
//            for( CtTracks t : ct ) {
//                CtItemState ds_t = sm.getState( t );
//
//                if( ds_t == CtItemState.SELECTED ) {
//                    ds = CtItemState.SELECTED;
//                }
//            }
//
//            CtPolygonStyle s = _detectionStyles.get( ds );
//
//            return s;
//        }
//        catch( NullPointerException npe ) {
//            System.err.println( "ERROR: Detection is not an orphan and has no track, shouldn't happen." );
//            return null;
//        }
//    }
    
}
