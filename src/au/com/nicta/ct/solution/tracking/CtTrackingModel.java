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

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import au.com.nicta.ct.orm.interactive.CtResult;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * All changes to the tracking model (detections, tracks) made through here.
 * When you call a function in the MODEL, it returns TRUE if changes were made.
 * The controller is notified and when all the transactions are complete a
 * single broadcast of model changed is made. So every method that changes any
 * state in this class or persistent hibernate pojos must call fireModelChanged()
 * The controller will batch these events and update the view. Only the controller
 * should listen to the model. Other classes should listen to the controller.
 * fireAppearanceChanged is used to indicate a change in detections observable
 * properties OR the set of currently visible tracks or detections or their states.
 * This allows a limited repaint etc. when there are no logical changes to the
 * tracks and detections relationships.
 * @author davidjr
 */
public class CtTrackingModel extends CtChangeModel {

////////////////////////////////////////////////////////////////////////////////
// MEMBER VARIABLES
////////////////////////////////////////////////////////////////////////////////
    public static final String EVT_APPEARANCE_CHANGED = "tracking-model-event-appearance-changed";

    public CtCoordinatesController _cc;
    public CtImageSequenceModel _ism; //continue here with validity of this model.
    public CtTimeWindowModel _twm;
    public CtSolutions _s;

    protected HashSet< CtDetections > _orphans = new HashSet< CtDetections >();
    protected HashMap< CtDetections, CtItemState > _detectionsStates = new HashMap< CtDetections, CtItemState >();
    protected HashMap< CtDetections, CtZoomPolygon > _detectionsBoundaries = new HashMap< CtDetections, CtZoomPolygon >();
    protected HashMap< CtTracks, CtItemState > _tracksStates = new HashMap< CtTracks, CtItemState >();
    public HashMap< CtTracks, TreeMap< Integer, CtDetections > > _tracksSequencedDetections = new HashMap< CtTracks, TreeMap< Integer, CtDetections > >();

////////////////////////////////////////////////////////////////////////////////
// BASIC INTERACTION
////////////////////////////////////////////////////////////////////////////////

    public CtTrackingModel() {
        super( null );
//        addModelChangedListener(
//            new CtChangeListener() {
//                @Override public void propertyChange( PropertyChangeEvent evt ) {
//                    CtTrackingController.get().onModelChanged();
//                }
//            }
//        );
    }

    public void fireAppearanceChanged() {
        changeSupport.fire( EVT_APPEARANCE_CHANGED );
    }

    public void addAppearanceChangeListener( CtChangeListener cl ) {
        changeSupport.addListener( EVT_APPEARANCE_CHANGED, cl );
    }
    
    public boolean valid() {
        if( _s == null ) {
            return false;
        }
        return true;
    }

    public void clear() {
        _cc = null;
        _ism = null;
        _twm = null;
        _s = null;

        _orphans.clear();
        _detectionsStates.clear();
        _detectionsBoundaries.clear();
        _tracksStates.clear();
        _tracksSequencedDetections.clear();

//        fireModelChanged();
    }

    public void update(
        CtCoordinatesController cc,
        CtImageSequenceModel ism,
        CtTimeWindowModel twm ) {
        _cc = cc;
        _ism = ism;
        _twm = twm;

//        if( !valid() ) return;

        fireAppearanceChanged();
    }

    public void refresh(
        CtCoordinatesController cc,
        CtImageSequenceModel ism, 
        CtTimeWindowModel twm,
        CtSolutions s,
        boolean showProgress ) {

        clear();

        // how to get historic and future images?
        _cc = cc;
        _ism = ism;
        _twm = twm;
        _s = s;

        if( !valid() ) {
//            if( cb != null ) cb.call( 0,0 );
            return;
        }

        if( showProgress ) {
            CtTrackingLoader tl = new CtTrackingLoader( this );
            tl.enqueue();//start();
            return;
        }

        Set< CtDetections > cd = s.getCtDetectionses();
        Set< CtTracks > ct = s.getCtTrackses();

//        int total = cd.size() + ct.size();
//        int complete = 0;
//        double reciprocal = 1.0 / (double)total;
        
        for( CtDetections d : cd ) {
            if( d.getCtTracksDetectionses().isEmpty() ) {
                _orphans.add( d );
            }

            _detectionsStates.put( d, CtItemState.NORMAL );
            _detectionsBoundaries.put( d, new CtZoomPolygon( d.getBoundary() ) );

//            ++complete;
//            if( cb != null ) cb.call( complete, total );
        }

        for( CtTracks t : ct ) {
            _tracksStates.put( t, CtItemState.NORMAL );

            updateTracksSequencedDetections( t );

//            ++complete;
//            if( cb != null ) cb.call( complete, total );
        }

//        if( cb != null ) cb.call( 0,0 );

        fireModelChanged();
    }

    public Rectangle2D getBoundingBox( CtDetections d ) {
        CtZoomPolygon zp = getBoundary( d );
        Rectangle2D r2d = zp.getBoundingBox();

        double reciprocal = 1.0 / CtSubPixelResolution.unitsPerNaturalPixel;

        Rectangle2D.Double r2d2 = new Rectangle2D.Double( r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight() );

        r2d2.x *= reciprocal;
        r2d2.y *= reciprocal;
        r2d2.width *= reciprocal;
        r2d2.height *= reciprocal;

        return r2d2;
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
    }

////////////////////////////////////////////////////////////////////////////////
// STATE ACCESS
////////////////////////////////////////////////////////////////////////////////

    public Collection< CtDetections > getOrphans() {
        return _orphans;
    }

    public Collection< CtDetections > getOrphansInWindow() {
        if( !valid() ) return new ArrayList< CtDetections >();

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getOrphansInWindow( index1, index2 );
    }

    public Collection< CtDetections > getOrphansInWindow( int index1, int index2 ) {
        ArrayList< CtDetections > al = new ArrayList< CtDetections >();

        for( CtDetections d : _orphans ) {
            CtImages d_i = d.getCtImages();
            int index = _cc.getTimeOrdinate( d_i );

            if(    ( index < index1 )
                || ( index > index2 ) ) {
                continue;
            }

            al.add( d );
        }

        return al;
    }

    public CtDetections getTrackDetectionAtIndex( CtTracks t, int index ) {
        Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

        for( CtTracksDetections td : tds ) {
            CtDetections d = td.getCtDetections();
            int index_d = this.getTimeOrdinate( d );

            if( index_d == index ) {
                return d;
            }
        }

        return null;
    }

    public CtAbstractPair< Integer, CtDetections > getTrackFirstDetection( CtTracks t ) {
        TreeMap< Integer, CtDetections > hm = _tracksSequencedDetections.get( t );

        if( hm == null ) {
            return null;
        }

        Entry< Integer, CtDetections > e = hm.firstEntry();

        if( e == null ) {
            return null;
        }

        int first = e.getKey();
        CtDetections d = e.getValue();

        CtAbstractPair< Integer, CtDetections > ap = new CtAbstractPair< Integer, CtDetections >( first, d );

        return ap;
    }

    public CtAbstractPair< Integer, CtDetections > getTrackLastDetection( CtTracks t ) {
        TreeMap< Integer, CtDetections > hm = _tracksSequencedDetections.get( t );

        if( hm == null ) {
            return null;
        }

        Entry< Integer, CtDetections > e = hm.lastEntry();

        if( e == null ) {
            return null;
        }

        int first = e.getKey();
        CtDetections d = e.getValue();

        CtAbstractPair< Integer, CtDetections > ap = new CtAbstractPair< Integer, CtDetections >( first, d );

        return ap;
    }

    public TreeMap< Integer, CtDetections > getTracksSequencedDetections( CtTracks t ) {
        return _tracksSequencedDetections.get( t );
    }

    public Collection< CtTracks > getTracks( CtDetections d ) {
        Set< CtTracks > s = new HashSet< CtTracks >();
        Set< CtTracksDetections > tds = d.getCtTracksDetectionses();

        if( tds == null ) {
            return s;
        }

        if( tds.isEmpty() ) {
            return s;
        }

        // although we allow detection to be in multiple tracks, this will get the first..
        Iterator i = tds.iterator();

        while( i.hasNext() ) {
            CtTracksDetections td = (CtTracksDetections)i.next();
            CtTracks t = td.getCtTracks();

            s.add( t );
        }

        return s;
    }
    
    public Collection< CtTracks > getTracksWithState( CtItemState ds ) {
        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( ds );
        return getTracksWithStates( al );
    }

    public Collection< CtTracks > getTracksWithStates( Collection< CtItemState > c ) {
        ArrayList< CtTracks > al = new ArrayList< CtTracks >();

        Set< Entry< CtTracks, CtItemState > > es = _tracksStates.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< CtTracks, CtItemState > e = (Entry< CtTracks, CtItemState >)i.next();

            CtTracks t = e.getKey();
            CtItemState ds_i = e.getValue();

            for( CtItemState ds : c ) {
                if( ds_i == ds ) {
                    al.add( t );
                    break;
                }
            }
        }

        return al;
    }

    public Collection< CtTracks > getTracksInWindow() {
        if( !valid() ) return new ArrayList< CtTracks >();

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getTracksWithStatesInWindowAt( CtInteractions.getAllItemStates(), index1, index2, null );
    }

    public Collection< CtTracks > getTracksWithStateInWindow( CtItemState ds ) {
        if( !valid() ) return new ArrayList< CtTracks >();

        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( ds );

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getTracksWithStatesInWindowAt( al, index1, index2, null );
    }
    
    public Collection< CtTracks > getTracksWithStatesInWindowAt( Collection< CtItemState > cis, int index1, int index2, Point2D p2d ) {
        Collection< CtDetections > cd = getDetectionsWithStatesInWindowAt( cis, index1, index2, p2d );

        if( cd == null ) return new ArrayList< CtTracks >();

        HashSet< CtTracks > ct = new HashSet< CtTracks >();

        for( CtDetections d : cd ) {

            Set< CtTracksDetections > ctd = d.getCtTracksDetectionses();

            for( CtTracksDetections td : ctd ) {

                CtTracks t = td.getCtTracks();

                ct.add( t );
            }
        }

        return ct;
    }
    
    public void setTracksStates( CtItemState ds ) {
        if( !valid() ) return;

        Collection< CtTracks > ct = _s.getCtTrackses();
        for( CtTracks t : ct ) {
            _tracksStates.put( t, ds );
        }

        updateDetectionsStatesToTracksStates();
        fireAppearanceChanged();
    }

    public int getTimeOrdinate( CtDetections d ) {
        if( !valid() ) return -1;
        CtImages i = d.getCtImages();
        int index = _cc.getTimeOrdinate( i );//_ism.index( i );
        return index;
    }

    public CtZoomPolygon getBoundary( CtDetections d ) {
        return _detectionsBoundaries.get( d );
    }

    public void setBoundary( CtDetections d, CtZoomPolygon zp ) {
        if( !valid() ) return;
        _detectionsBoundaries.put( d, new CtZoomPolygon( zp ) );
        d.setBoundary( zp.serialize() );

        Session s = CtSession.Current();
        s.beginTransaction();
        s.save( d );
        s.flush();
        CtSession.Current().getTransaction().commit();
        fireAppearanceChanged();
    }

    public void translateDetections( Collection< CtDetections > cd, int dx, int dy ) {
        if( !valid() ) return;
        if( cd.isEmpty() ) return;

        Session s = CtSession.Current();
        s.beginTransaction();

        for( CtDetections d : cd ) {
            CtZoomPolygon zp = _detectionsBoundaries.get( d );

            zp.translate( dx, dy );

            _detectionsBoundaries.put( d, new CtZoomPolygon( zp ) );
            d.setBoundary( zp.serialize() );

            s.save( d );
        }

        s.flush();

        CtSession.Current().getTransaction().commit();

        fireAppearanceChanged();
    }

    public CtItemState getState( CtDetections d ) {
        return _detectionsStates.get( d );
    }

    public CtItemState getState( CtTracks t ) {
        return _tracksStates.get( t );
    }

    public void setState( CtItemState is ) {

        if( !valid() ) return;

        Set< CtDetections > cd = _s.getCtDetectionses();

        for( CtDetections d : cd ) {
            _detectionsStates.put( d, is );
        }

        Set< CtTracks > ct = _s.getCtTrackses();

        for( CtTracks t : ct ) {
            _tracksStates.put( t, is );
        }

        // don't need to align tracks & detections states - they're all the same.
        
        fireAppearanceChanged();
    }

    public void setState( CtDetections d, CtItemState ds ) {
        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
        al.add( d );
        setDetectionsState( al, ds );
    }

    public void setState( CtTracks t, CtItemState ds ) {
        ArrayList< CtTracks > al = new ArrayList< CtTracks >();
        al.add( t );
        setTracksState( al, ds );
    }

    public void setDetectionsState( Collection< CtDetections > cd, CtItemState ds ) {
        setDetectionsState( cd, ds, true );
    }

    public void setDetectionsState( Collection< CtDetections > cd, CtItemState ds, boolean fireAppearanceChanged ) {
        for( CtDetections d : cd ) {
            _detectionsStates.put( d, ds );
        }
        
        if( fireAppearanceChanged ) {
            fireAppearanceChanged();
        }
    }

    public void setTracksState( Collection< CtTracks > ct, CtItemState ds ) {
        for( CtTracks t : ct ) {
            _tracksStates.put( t, ds );
        }
        updateDetectionsStatesToTracksStates(); // doesn't fire appearance changed event
        fireAppearanceChanged();
    }
    
    public boolean isOrphan( CtDetections d ) {
        if( _orphans.contains( d ) ) {
            return true;
        }
        return false;
    }

    public Collection< CtDetections > getDetectionsWithState( CtItemState ds ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( ds );

        int index1 = _ism.getMinIndex();
        int index2 = _ism.getMaxIndex();

        return getDetectionsWithStatesInWindowAt( al, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsWithStateInWindow( CtItemState ds ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( ds );

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getDetectionsWithStatesInWindowAt( al, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsWithStateInWindowAt( CtItemState ds, Point2D p2d ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( ds );

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getDetectionsWithStatesInWindowAt( al, index1, index2, p2d );
    }

//    public Collection< CtDetections > getDetectionsWithStates( Collection< CtItemState > cis ) {
//        HashSet< CtDetections > at = new HashSet< CtDetections >();
//
//        Set< Entry< CtDetections, CtItemState > > es = _detectionsStates.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< CtDetections, CtItemState > e = (Entry< CtDetections, CtItemState >)i.next();
//
//            CtItemState is0 = e.getValue();
//
//            for( CtItemState is : cis ) {
//                if( is == is0 ) {
//                    at.add( e.getKey() );
//                    break;
//                }
//            }
//        }
//
//        return at;
//    }

    public Collection< CtDetections > getDetectionsInWindow() {
        if( !valid() ) return new ArrayList< CtDetections >();

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getDetectionsWithStatesInWindowAt( null, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsInRange( int index1, int index2 ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        return getDetectionsWithStatesInWindowAt( null, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsWithStatesInWindow( Collection< CtItemState > cis ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return getDetectionsWithStatesInWindowAt( cis, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsWithStates( Collection< CtItemState > cis ) {
        if( !valid() ) return new ArrayList< CtDetections >();

        int index1 = _ism.getMinIndex();
        int index2 = _ism.getMaxIndex();

        return getDetectionsWithStatesInWindowAt( cis, index1, index2, null );
    }

    public Collection< CtDetections > getDetectionsWithStatesInWindowAt( Collection< CtItemState > cis, int index1, int index2, Point2D p2d ) {

        if( !valid() ) return new ArrayList< CtDetections >();

        HashSet< CtDetections > at = new HashSet< CtDetections >();

        Set< Entry< CtDetections, CtItemState > > es = _detectionsStates.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< CtDetections, CtItemState > e = (Entry< CtDetections, CtItemState >)i.next();

            CtDetections d = e.getKey();
            CtImages d_i = d.getCtImages();
            int index = _cc.getTimeOrdinate( d_i );

            if(    ( index < index1 )
                || ( index > index2 ) ) {
                continue;
            }

            if( cis == null ) {

                if( p2d != null ) {
                    CtZoomPolygon zp = _detectionsBoundaries.get( d );

                    if( !zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
                        continue; // for loop
                    }
                }

                at.add( d );
                continue;
            }

            CtItemState is0 = e.getValue();

            for( CtItemState is : cis ) {
                if( is == is0 ) {

                    if( p2d != null ) {
                        CtZoomPolygon zp = _detectionsBoundaries.get( d );

                        if( !zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
                            continue; // for loop
                        }
                    }

                    at.add( d );
                    break;
                }
            }
        }

        return at;
    }

//    public Collection< CtDetections > getDetectionsWithStatesAt( Point2D p2d, Collection< CtItemState > cis ) {
//        HashSet< CtDetections > at = new HashSet< CtDetections >();
//
//        Set< Entry< CtDetections, CtItemState > > es = _detectionsStates.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< CtDetections, CtItemState > e = (Entry< CtDetections, CtItemState >)i.next();
//
//            CtDetections d = e.getKey();
//            CtItemState is0 = e.getValue();
//
//            for( CtItemState is : cis ) {
//                if( is == is0 ) {
//                    CtZoomPolygon zp = _detectionsBoundaries.get( d );
//
//                    if( zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
//                        at.add( d );
//                        break;
//                    }
//                }
//            }
//        }
//
//        return at;
//    }
//CtZoomTrackPainter.SENSITIVE_RADIUS_FRACTION
    public Collection< CtDetections > getDetectionsWithStatesCentresNear( Point2D p2d, double radius, Collection< CtItemState > ac ) {
//    public Collection< CtDetections > getDetectionsWithStates( Point2D p2d, Collection< CtDetectionState > ac ) {

        ArrayList< CtDetections > al = new ArrayList< CtDetections >();

        Set< Entry< CtDetections, CtZoomPolygon > > es = _detectionsBoundaries.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< CtDetections, CtZoomPolygon > e = (Entry< CtDetections, CtZoomPolygon >)i.next();

            CtZoomPolygon zp = e.getValue();
            CtDetections d = e.getKey();

            boolean valid = true;

            if( p2d != null ) {
                if( !zp.withinRadiusOfNaturalCoord( radius, p2d.getX(), p2d.getY() ) ) {
                    valid = false;
                }
            }

            if( !valid ) {
                continue;
            }

            CtItemState is_i = getState( d );

            for( CtItemState is : ac ) {
                if( is_i == is ) {
                    al.add( d );
                    break;
                }
            }
        }

        return al;
    }

    public boolean setDetectionsStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, Point2D p2d ) {

        if( !valid() ) return false;

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return setDetectionsStatesInWindowAt( mappings, index1, index2, p2d );
    }

    public boolean setDetectionsStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, int index1, int index2, Point2D p2d ) {

        if( !valid() ) return false;

        HashSet< CtItemState > hs = new HashSet< CtItemState >();

        for( CtAbstractPair< CtItemState, CtItemState > ap : mappings ) {
            hs.add( ap._first );
        }

        Collection< CtDetections > ac = getDetectionsWithStatesInWindowAt( hs, index1, index2, p2d );

        if( ac.isEmpty() ) {
            return false;
        }

        for( CtDetections d : ac ) {

            CtItemState ds = _detectionsStates.get( d );

            // d is in one of the states we're interested in at the point we're interested in.
            for( CtAbstractPair< CtItemState, CtItemState > ap : mappings ) {
                if( ap._first == ds ) {
                    setState( d, ap._second );
                    break;
                }
            }
        }

        fireAppearanceChanged();

        return true;
    }

    public boolean setStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, Point2D p2d ) {

        if( !valid() ) return false;

        int index1 = _ism.minIndexInWindow( _twm );
        int index2 = _ism.maxIndexInWindow( _twm );

        return setStatesInWindowAt( mappings, index1, index2, p2d );
    }

    public boolean setStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, int index1, int index2, Point2D p2d ) {

        if( !valid() ) return false;

        HashSet< CtItemState > hs = new HashSet< CtItemState >();

        for( CtAbstractPair< CtItemState, CtItemState > ap : mappings ) {
            hs.add( ap._first );
        }

        Collection< CtDetections > cd = getDetectionsWithStatesInWindowAt( hs, index1, index2, p2d );

        HashSet< CtTracks > tabu = new HashSet< CtTracks >(); // tabu means must be ignored and not touched! ie ignore these tracks.

        boolean changed = false;

        for( CtDetections d : cd ) {

            CtImages i = d.getCtImages();
            CtItemState ds_d = _detectionsStates.get( d ); //getState( d );

            // d is in one of the states we're interested in at the point we're interested in.
            for( CtAbstractPair< CtItemState, CtItemState > ap : mappings ) {
                if( ap._first == ds_d ) {
                    //dm.setState( d, ap._second );
                    _detectionsStates.put( d, ap._second );
                    changed = true;
                    break;
                }
            }

//            CtTracks t = getTrack( d );
            Collection< CtTracks > ct = getTracks( d );

            for( CtTracks t : ct ) {

                if( t == null ) {
                    continue; /// detection not in track
                }

                if( tabu.contains( t ) ) {
                    continue; // already mapped the state of this track
                }

                CtItemState ds_t = _tracksStates.get( t );

                // d is in one of the states we're interested in at the point we're interested in.
                for( CtAbstractPair< CtItemState, CtItemState > ap : mappings ) {
                    if( ap._first == ds_t ) {
                        setState( t, ap._second );
                        changed = true;
                        tabu.add( t );
                        break;
                    }
                }
            }
        }

        if( changed ) {
            updateDetectionsStatesToTracksStates();
            fireAppearanceChanged();
        }

        return changed;
//        printSelected();
    }

    public boolean simultaneousDetectionsIn( Collection< CtDetections > cd, Collection< CtTracks > ct ) {

        if( !valid() ) return false;

        Map< Integer, CtDetections > indices = new HashMap< Integer, CtDetections >();

        if( cd != null ) {
            for( CtDetections d : cd ) {
                CtImages i = d.getCtImages();
                int index = _cc.getTimeOrdinate( i );//_ism.index( i );

                CtDetections d2 = indices.get( index );

                if( d2 == null ) {
                    indices.put( index, d );
                }
                else {
                    if( d.getPkDetection() != d2.getPkDetection() ) {
                        return true; // bad overlap of different detections
                    }
                }
            }
        }

        if( ct != null ) {
            for( CtTracks t : ct ) {
                Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

                for( CtTracksDetections td : tds ) {
                    CtDetections d = td.getCtDetections();
                    CtImages i = d.getCtImages();
                    int index = _cc.getTimeOrdinate( i );//_ism.index( i );

                    CtDetections d2 = indices.get( index );

                    if( d2 == null ) {
                        indices.put( index, d );
                    }
                    else {
                        if( d.getPkDetection() != d2.getPkDetection() ) {
                            return true; // bad overlap
                        }
                    }
                }
            }
        }
        
        return false;
    }

////////////////////////////////////////////////////////////////////////////////
// COMPLEX TRACKING MODIFIER METHODS
////////////////////////////////////////////////////////////////////////////////

//    public CtResult createDetection( CtZoomPolygon zp ) {
//        return createDetection( zp.serialize() );
//    }
//
    public CtResult createDetection( CtZoomPolygon zp ) {

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );

        CtImages i = _ism.current();

        return createDetection( zp, i );
    }

    public CtResult createDetections( Collection< CtZoomPolygon > czp, CtImages i ) {

        if( czp == null ) return CtResult.unchanged( "No polygons." );
        if( !valid() ) return CtResult.unchanged( "Solution not valid." );

        Session s = CtSession.Current();
        s.beginTransaction();
        
        for( CtZoomPolygon zp : czp ) {
            String boundary = zp.serialize();

            CtDetections d = new CtDetections();
            d.setBoundary( boundary );
            d.setCtImages( i );
            i.getCtDetectionses().add( d );
            d.setCtSolutions( _s );
            _s.getCtDetectionses().add( d );
            // todo: set solution?

            s.save( d );
            s.save( _s );//update( _s );

            // update local cache state:
            _orphans.add( d ); // must be an orphan
            _detectionsStates.put( d, CtItemState.NORMAL );
            _detectionsBoundaries.put( d, zp );
    //    protected HashSet< CtDetections > _orphans = new HashSet< CtDetections >();
    //    protected HashMap< CtDetections, CtItemState > _detectionsStates = new HashMap< CtDetections, CtItemState >();
    //    protected HashMap< CtDetections, CtZoomPolygon > _detectionsBoundaries = new HashMap< CtDetections, CtZoomPolygon >();
    //    protected HashMap< CtTracks, CtItemState > _tracksStates = new HashMap< CtTracks, CtItemState >();
    //    public HashMap< CtTracks, TreeMap< Integer, CtDetections > > _tracksSequencedDetections = new HashMap< CtTracks, TreeMap< Integer, CtDetections > >();
        }

        s.flush();
        CtSession.Current().getTransaction().commit();

        fireModelChanged();

        return CtResult.success( "Detection[s] created." );
    }

    // handy fn to create in ANY image:
    public CtResult createDetection( CtZoomPolygon zp, CtImages i ) {
        ArrayList< CtZoomPolygon > al = new ArrayList< CtZoomPolygon >();
        al.add( zp );
        return createDetections( al, i );
    }

    public CtResult mergeDetections( Collection< CtDetections > cd ) { // even if they are in tracks.
        if( !valid() ) return CtResult.unchanged( "Solution not valid." );

        int size = cd.size();
        if( size < 2 ) return CtResult.unchanged( "Insufficient detections to merge." );

        HashSet< Integer > times = new HashSet< Integer >();

        for( CtDetections d : cd ) {
            int time = getTimeOrdinate( d );

            times.add( time );
        }

        if( times.size() > 1 ) return CtResult.unchanged( "Detections span more than 1 image, can't merge." );

        // remove all detections from tracks, to produce a merged orphan. User can decide where to re-associate.
        // delete all detections except one.
        // Keep copies of the polygons. Make a combined polygon as per the use of brush tool.
        CtZoomPolygon zp = new CtZoomPolygon();
        CtImages i = null;
//        tc.setBoundary( d, zp );

        for( CtDetections d : cd ) {
            i = d.getCtImages();
            CtZoomPolygon zp2 = _detectionsBoundaries.get( d );
            zp.add( zp2 ); // now a combination
        }
        
        CtResult r1 = deleteDetections( cd, false ); // event will be generated when
//        CtImages i = _ism.current();
        CtResult r2 = createDetection( zp, i );

        return CtResult.combine( r1, r2 );
    }

    public CtResult deleteDetections( Collection< CtDetections > cd ) { // even if they are in tracks.
        return deleteDetections( cd, true );
    }

    public CtResult deleteDetections( Collection< CtDetections > cd, boolean fireEvent ) { // even if they are in tracks.

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );
        if( cd.isEmpty() ) return CtResult.unchanged( "No detections to delete." );

//        CtImages i = _ism.current();

        Session s = CtSession.Current();
        s.beginTransaction();

        for( CtDetections d : cd ) {
            CtImages i = d.getCtImages(); // in case not current image!

            i.getCtDetectionses().remove( d );
            _s.getCtDetectionses().remove( d );

            _orphans.remove( d );
            _detectionsStates.remove( d );
            _detectionsBoundaries.remove( d );

            // detection may be in many tracks
            HashSet< CtTracks > defunctTracks = new HashSet< CtTracks >();
            HashSet< CtTracks > modifiedTracks = new HashSet< CtTracks >();
            HashSet< CtTracksDetections > defunctTrackDetections = new HashSet< CtTracksDetections >();

            Set< CtTracksDetections > tds = d.getCtTracksDetectionses();

            // add tracks-detections of this detection must be removed
            for( CtTracksDetections td : tds ) {
//                CtTracks t = td.getCtTracks();
                defunctTrackDetections.add( td );
            }

            // if this leaves an empty track, remove the track too:
            for( CtTracksDetections td : defunctTrackDetections ) {
                CtTracks t = td.getCtTracks();
                Set< CtTracksDetections > tds2 = t.getCtTracksDetectionses();
                tds.remove( td );
                tds2.remove( td );
                s.delete( td );

                // maybe track should be deleted too..
                if( tds2.isEmpty() ) {
                    defunctTracks.add( t );
                }
                modifiedTracks.add( t );
            }

            for( CtTracks t : defunctTracks ) {
                deleteTrackReferences( s, t );
            }

            for( CtTracks t : modifiedTracks ) {
                if( defunctTracks.contains( t ) ) {
                    continue;
                }
                updateTracksSequencedDetections( t );
            }
            s.delete( d );
        }

        s.flush();
        CtSession.Current().getTransaction().commit();

        fireModelChanged();

        return CtResult.success( "Detections deleted." );
    }

//    public CtResult associate(
//        Collection< CtDetections > cd,
//        Collection< CtTracks > ct ) {
//
//        // must be exactly 0 or 1 track,
//        int detections = cd.size();
//        int tracks = ct.size();
//
////        if( tracks > 1 ) {
////            return "Can't associate detections with more than 1 track."; // actually can join 2 tracks, tho
////        }
//
//        if( tracks == 0 ) {
//            // new track
//            return createTrack( cd, ct );
//        }
//        else if( tracks == 1 ) {
//            // Exactly 1 track. So, detections:
//            if( detections == 0 ) {
//                return "No detections to associate with single selected track.";
//            }
//
//            if( collectionsAreForkable( cd, ct ) ) {
//                return forkTrack( cd, ct.iterator().next() );
//            }
//            else {
//                return createTrack( cd, ct );//ct.iterator().next() );
//            }
//        }
//        else if( tracks > 1 ) {
////            if( simultaneousDetectionsIn( cd, ct ) ) {
////                return "Can't associate overlapping detections within selected tracks.";
////            }
////
//            return createTrack( cd, ct );
//        }
//        else {
//            return "Nothing selected to be associated.";
//        }
//    }

    public CtResult separate(
        Collection< CtDetections > cd, // optionally limit to these detections, leave null to select entire tracks.
        Collection< CtTracks > ct ) {

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );
        if( ct.isEmpty() ) return CtResult.unchanged( "No tracks to separate." );
        // separate these detections cd of these tracks ct...

        CtResult r = null;

        Session session = CtSession.Current();
        session.beginTransaction();

        ArrayList< ArrayList< CtDetections > > trackTails = new ArrayList< ArrayList< CtDetections > >();

        for( CtTracks t : ct ) {

//System.out.println( "separating track t="+t.getPkTrack() );
            ArrayList< CtDetections > trackTail = new ArrayList< CtDetections >();
            HashSet< CtTracksDetections > removed = new HashSet< CtTracksDetections >();

            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

            for( CtTracksDetections td : tds ) {

                CtDetections d = td.getCtDetections();

                // FILTER DETECTIONS TO SEPARATE BY SUPPLIED LIST:
                boolean selected = false;

                if( cd != null ) {
                    for( CtDetections d2 : cd ) {
                        if( d2.getPkDetection() == d.getPkDetection() ) {
                            selected = true;
                            break;
                        }
                    }
                }

                if( !selected ) {
                    // work out if its in the 1st or 2nd part of the track.
                    // retain those in 1st part,
                    CtImages i = d.getCtImages();
                    int index = _cc.getTimeOrdinate( i );//_ism.index( i );
                    int currentIndex = _ism.getIndex();

                    if( index <= currentIndex ) {
                        continue; // retain this detection in the track.
                    }

                    // put those in 2nd part into new track:
                    trackTail.add( d );
                }

                removed.add( td );
            }
            
            // if track now empty, delete it
            for( CtTracksDetections td : removed ) {
                deleteTrackAssociation( session, td );
            }

            if( tds.size() == 0 ) {
                deleteTrackReferences( session, t );
            }
            else {
                updateTracksSequencedDetections( t );
            }

            // Now we *may* have a bunch of detections to put into a new track, due to a split in the middle:
            if( trackTail.size() > 1 ) {
                trackTails.add( trackTail );
            }
        }

        session.getTransaction().commit();

        for( ArrayList< CtDetections > al : trackTails ) {
            r = CtResult.combine( r, createTrack( al ) ); // fireModelChanged() here if reqd
        }

        fireModelChanged();

        return r;
    }

// redundant as have method separate( cd, ct) with same capability already
//    public CtResult separate( Collection< CtTracks > ct ) {
//
//        // separate these tracks ct...
//        String error = null;
//
//        // must be 1 or more tracks
//        int tracks = ct.size();
//
//        if( tracks < 1 ) {
//            return "Select track[s] to separate into detections.";
//        }
//
//        Session session = CtSession.Current();
//        session.beginTransaction();
//
//        ArrayList< ArrayList< CtDetections > > trackTails = new ArrayList< ArrayList< CtDetections > >();
//
//        for( CtTracks t : ct ) {
//
//            HashSet< CtTracksDetections > removed = new HashSet< CtTracksDetections >();
//
//            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();
//
//            for( CtTracksDetections td : tds ) {
//
//                CtDetections d = td.getCtDetections();
//
//                removed.add( td );
//            }
//
//            for( CtTracksDetections td : removed ) {
//                deleteTrackAssociation( session, td );
//            }
//
//            if( tds.size() == 0 ) {
//                deleteTrackReferences( session, t );
//            }
//            else { // should never happen
//                updateTracksSequencedDetections( t );
//            }
//
//        }
//
//        session.getTransaction().commit();
//
//        fireModelChanged();
//
//        return error;
//    }

//    public boolean collectionOverlaps( Collection< CtDetections > cd ) {
//        Set< Integer > indices = new HashSet< Integer >();
//
//        for( CtDetections d2 : cd ) {
//            CtImages i = d2.getCtImages();
//            int index = _cc.getTimeOrdinate( i );//_ism.index( i );
//
//            if( indices.contains( index ) ) {
//                return true; // is an overlap in sequence
//            }
//            indices.add( index );
//        }
//
//        return false;
//    }

    public boolean collectionsAreForkable(
        Collection< CtDetections > cd,
        Collection< CtTracks > ct ) {

        if( !valid() ) {
            return false;
        }

//        if( cd.size() != 0 ) {
//            return false;
//        }

        if( ct.size() != 3 ) {
            return false;
        }

        CtTracks mother = null;
        int motherIndex = Integer.MAX_VALUE;

        for( CtTracks t : ct ) {
            CtDetections d = getTrackLastDetection( t )._second;

            if( d == null ) {
                System.err.println("ERROR: Something wrong with the database, this track "+t.getPkTrack()+ " has no detections!" );
                return false;
            }

            CtImages i = d.getCtImages();
            int index = _cc.getTimeOrdinate( i );

            if( index < motherIndex ) {
                mother = t;
                motherIndex = index;
            }
        }

        // check daughters don't start til after mother ends.
        for( CtTracks t : ct ) {
            if( t == mother ) continue;

            CtDetections d = getTrackFirstDetection( t )._second;

            CtImages i = d.getCtImages();
            int index = _cc.getTimeOrdinate( i );

            if( index <= motherIndex ) {
                return false; // can't fork cos daughters start before mother ends
            }
        }

        return true;
//        CtDetections d0 = getTrackLastDetection( ct.iterator().next() )._second;
//
//        if( d0 == null ) {
//            return false;
//        }
//
//        CtImages i0 = d0.getCtImages();
//        int index0 = _cc.getTimeOrdinate( i0 );//_ism.index( i0 );
//        int suitableDetections = 0;
//
//        for( CtDetections d : cd ) {
//
//            CtImages i = d.getCtImages();
//            int index = _cc.getTimeOrdinate( i );//_ism.index( i );
//
//            if( index > index0 ) {
//                ++suitableDetections;
//            }
//        }
//
//        if( suitableDetections >= 2 ) {
//            return true;
//        }
//
//        return false;
    }

//    protected CtDetections findLastDetection( CtTracks t ) {
//        HashMap< Integer, CtDetections > hm = getTracksSequencedDetections( t );
//
//        if( hm.size() < 1 ) {
//            return null;
//        }
//
//        int maxIndex = 0;
//        CtDetections d = null;
//
//        Set< Entry< Integer, CtDetections > > es = hm.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//            Entry< Integer, CtDetections > e = (Entry< Integer, CtDetections >)i.next();
//
//            int index = e.getKey();
//
//            if(    ( d == null )
//                || ( index >= maxIndex ) ) {
//                maxIndex = index;
//                d = e.getValue();
//            }
//        }
//
//        return d;
//    }
    
    public CtResult forkSelected( Collection< CtDetections > cd, Collection< CtTracks > ct ) {

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );
//        if( cd.size() != 0 ) return CtResult.unchanged( "You must select 3 tracks and 0 orphans to fork. You have selected orphans." );
        if( ct.size() != 3 ) return CtResult.unchanged( "You must select 3 tracks and 0 orphans to fork. You have selected the wrong number of tracks." );

        CtTracks mother = null;
        int motherIndex = Integer.MAX_VALUE;

        for( CtTracks t : ct ) {
            CtDetections d = getTrackLastDetection( t )._second;

            if( d == null ) {
                return CtResult.unchanged( "ERROR: Something wrong with the database, this track "+t.getPkTrack()+ " has no detections!" );
            }

            CtImages i = d.getCtImages();
            int index = _cc.getTimeOrdinate( i );

            if( index < motherIndex ) {
                mother = t;
                motherIndex = index;
            }
        }

        // check daughters don't start til after mother ends.
        for( CtTracks t : ct ) {
            if( t == mother ) continue;

            CtDetections d = getTrackFirstDetection( t )._second;

            CtImages i = d.getCtImages();
            int index = _cc.getTimeOrdinate( i );

            if( index <= motherIndex ) {
                return CtResult.unchanged( "Can't fork because daughter tracks begin before mother track ends." );
            }
        }

        // add the LAST detection in mother to the two daughter tracks.
        CtDetections d = getTrackLastDetection( mother )._second;

        ArrayList< CtDetections > cd2 = new ArrayList< CtDetections >();
        cd2.add( d );

        CtResult r = CtResult.unchanged( "" );

        for( CtTracks t : ct ) {
            if( t == mother ) continue; // don't change mum

            ArrayList< CtTracks > ct2 = new ArrayList< CtTracks >();
            ct2.add( t );

            r = CtResult.combine( r, createTrack( cd2, ct2 ) ); // fireModelChanged() here if reqd
        }

//            r = CtResult.combine( r, createTrack( al ) ); // fireModelChanged() here if reqd

        // fireModelChanged() above if reqd
        return r;
    }

// redundant; same as createTrack( cd, ct )
//    public String appendTrack( Collection< CtDetections > c, CtTracks t ) {
//
//        String error = null;
//        boolean modelChanged = false;
//
//        Session session = CtSession.Current();
//        session.beginTransaction();
//
//        for( CtDetections d : c ) {
//
//            modelChanged = true;
//
//            CtTracksDetections td = createTrackAssociation( session, t, d );
//        }
//
//        updateTracksSequencedDetections( t );
//
//        session.getTransaction().commit();
//
//        if( modelChanged ) {
//            updateDetectionsStatesToTracksStates();
//            fireModelChanged();
//        }
//
//        return error;
//    }

    public CtResult createTrack( Collection< CtDetections > cd, Collection< CtTracks > ct ) {

        if( ct.isEmpty() ) {
            return createTrack( cd );
        }

        // ct.size() > 0
        int dSize = cd.size();
        int tSize = ct.size();

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );
        if( ( dSize + tSize ) < 2 ) return CtResult.unchanged( "Not enough detections/tracks to associate." );
//        if( cd.size() < 2 ) return CtResult.unchanged( "Not enough detections to associate." );
        if( simultaneousDetectionsIn( cd, ct ) ) return CtResult.unchanged( "Simultaneous detections can't be associated." );

        Session session = CtSession.Current();
        session.beginTransaction();

        // build a set of detections we are going to add to the remaining track t0:
        HashSet< CtDetections > hs = new HashSet< CtDetections >();

        for( CtDetections d : cd ) {
            hs.add( d );
        }

        // we will keep ct(0) and transfer everything to it.
        CtTracks t0 = null;
        Iterator i = ct.iterator();

        HashSet< CtDetections > t0Detections = new HashSet< CtDetections >();

        while( i.hasNext() ) {
            CtTracks t = (CtTracks)i.next();
            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

            if( t0 == null ) {
                t0 = t;

                for( CtTracksDetections td : tds ) {
                    t0Detections.add( td.getCtDetections() );
                }

                continue;
            }

            Set< CtTracksDetections > removed = new HashSet< CtTracksDetections >();

            for( CtTracksDetections td : tds ) {
                CtDetections d = td.getCtDetections();
                hs.add( d );
                removed.add( td ); // can't do now as causes concurrent mod except.
            }

            for( CtTracksDetections td : removed ) {
                deleteTrackAssociation( session, td );
            }

            deleteTrackReferences( session, t );
        }

        // now add all these detections to the retained track, t0:
        for( CtDetections d : hs ) {

            if( t0Detections.contains( d ) ) {
                continue; // already in this track
            }

            CtTracksDetections td = createTrackAssociation( session, t0, d );
        }
        
        updateTracksSequencedDetections( t0 );

        session.getTransaction().commit();

        updateDetectionsStatesToTracksStates();
        fireModelChanged();

        return CtResult.success( "Track created." ); // no error message
    }

    public CtResult createTrack( Collection< CtDetections > cd ) {

        if( !valid() ) return CtResult.unchanged( "Solution not valid." );
        if( cd.isEmpty() ) return CtResult.unchanged( "No detections to associate." );
        if( cd.size() < 2 ) return CtResult.unchanged( "Not enough detections to associate." );
        if( simultaneousDetectionsIn( cd, null ) ) return CtResult.unchanged( "Simultaneous detections can't be associated." );

        Session session = CtSession.Current();
        session.beginTransaction();

        CtTracks t = createTrackReferences( session );

        for( CtDetections d : cd ) {

            // update hibernate objects:
            CtTracksDetections td = createTrackAssociation( session, t, d );
        }

        updateTracksSequencedDetections( t );

        session.getTransaction().commit();

        updateDetectionsStatesToTracksStates();
        fireModelChanged();

        return CtResult.success( "Track created." ); // no error message
    }

////////////////////////////////////////////////////////////////////////////////
// INTERNAL UTILITY FUNCTIONS
// These methods require valid() == true
// and do NOT call fireModelChanged().
// It is assumed that they are only called from within other methods.
////////////////////////////////////////////////////////////////////////////////

    public void printSelected() {

        ArrayList< CtItemState > al = new ArrayList< CtItemState >();
        al.add( CtItemState.SELECTED );

        System.out.print( "Selected detections: " );
        Collection< CtDetections > c = getDetectionsWithStates( al );

        for( CtDetections d : c ) {
            System.out.print( d.getPkDetection()+", " );
        }

        System.out.println( ";" );
        System.out.print( "Selected tracks: " );

        Collection< CtTracks > ct = getTracksWithStates( al );

        for( CtTracks t : ct ) {
            System.out.print( t.getPkTrack()+", " );
        }

        System.out.println( ";" );
    }

    protected void updateDetectionsStatesToTracksStates() {

        if( !valid() ) return;

        // Because detections and tracks independently have states, and because
        // detections that are in tracks are not always visible (for clarity)
        // we need to ensure that the state of the track reflects the state of
        // the detections within it.. so after any change to a track's detections
        // or the state of the track, update the state of the detections.
        Set< CtTracks > ct = _s.getCtTrackses();

        for( CtTracks t : ct ) {
            CtItemState ds_t = _tracksStates.get( t );

            // build a container of all the detections in this track
            ArrayList< CtDetections > al = new ArrayList< CtDetections >();

            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

            for( CtTracksDetections td : tds ) {
                al.add( td.getCtDetections() );
            }

            // now make sure all the detections in this track are in this state.
            setDetectionsState( al, ds_t, false ); // don't fire event, handled elsewhere
        }
    }

    protected void deleteTracks( boolean showProgress ) { // i.e. all tracks

        if( !valid() ) return;

//        CtSolutions solution = (CtSolutions)CtObjectDirectory.get( "solution" );

//        String hql1 = " DELETE FROM CtTracksDetections td "
//                    + " WHERE td.ctTracks IN ( "
//                    + " SELECT t FROM CtTracks t "
//                    + " INNER JOIN t.ctSolutions s "
//                    + " WHERE s.pkSolution = " + solution.getPkSolution() +" ) ";

        String hql1 = " SELECT td FROM CtTracksDetections td "
                    + " WHERE td.ctTracks IN ( "
                    + " SELECT t FROM CtTracks t "
                    + " INNER JOIN t.ctSolutions s "
                    + " WHERE s.pkSolution = " + _s.getPkSolution() +" ) ";

//        String hql1 = " SELECT t FROM CtTracks t "
//                    + " INNER JOIN t.ctSolutions s "
//                    + " WHERE s.pkSolution = " + solution.getPkSolution();// +" ) ";
        String hql2 = " DELETE FROM CtTracksDetections td "
                    + " WHERE td IN (:vals) ";

        String hql3 = " DELETE FROM CtTracks t "
                    + " WHERE t.ctSolutions = " + _s.getPkSolution();

//        clear();

        Session session = CtSession.Current();
        session.beginTransaction();

        Query q1 = session.createQuery( hql1 );
        List results = q1.list();

        if( results.isEmpty() ) {
            session.getTransaction().commit();
            return; // mnothing changed
        }

        Query q2 = session.createQuery( hql2 );

        q2.setParameterList( "vals", results );

        int rowCount2 = q2.executeUpdate(); // this query fails

        Query q3 = session.createQuery( hql3 );
        int rowCount3 = q3.executeUpdate();

        session.getTransaction().commit();
        session.refresh( _s );

        ////////////////////////////////////////////////////////////////////////
        // Manually refresh detections and tracks as seems to be a bug with 
        // their consistency
        ////////////////////////////////////////////////////////////////////////
        CtPageFrame.showWaitCursor();

        Set< CtDetections > cd = _s.getCtDetectionses();
        Set< CtTracks     > ct = _s.getCtTrackses();

        for( CtDetections d : cd ) {
            session.refresh( d );
        }

        for( CtTracks t : ct ) {
            session.refresh( t );
        }

        CtPageFrame.showDefaultCursor();
        ////////////////////////////////////////////////////////////////////////

        refresh( _cc, _ism, _twm, _s, showProgress );
//        fireModelChanged(); in update anyway
    }

    protected void deleteDetections() { // i.e. all

        if( !valid() ) return;

//        deleteTracks();
//
//        ArrayList< CtDetections > cd = new ArrayList< CtDetections >( _orphans );

        Set< CtDetections > cd = _s.getCtDetectionses();

        deleteDetections( cd ); // will delete tracks too

        fireModelChanged();
    }

    protected CtTracksDetections createTrackAssociation( Session s, CtTracks t, CtDetections d ) {

        if( !valid() ) return null;

        CtTracksDetections td = new CtTracksDetections();
        td.setCtDetections( d );
        td.setCtTracks( t );
        t.getCtTracksDetectionses().add( td );
        d.getCtTracksDetectionses().add( td );
        s.save( td );

        // update local cached state:
        _orphans.remove( d ); // cant be orphan now (ALWAYS?)

        return td;
    }

    protected void deleteTrackAssociation( Session s, CtTracksDetections td ) {

        if( !valid() ) return;

        CtTracks t = td.getCtTracks();
        CtDetections d = td.getCtDetections();
        Set< CtTracksDetections > tds = d.getCtTracksDetectionses();
        tds.remove( td );
        t.getCtTracksDetectionses().remove( td );
        s.delete( td );

        if( tds.isEmpty() ) {
            _orphans.add( d );
        }
    }

    protected CtTracks createTrackReferences( Session s ) {

        if( !valid() ) return null;

        CtTracks t = new CtTracks();
//        CtSolutions solution = (CtSolutions)CtObjectDirectory.get( "solution" );
        _s.getCtTrackses().add( t );
        t.setCtSolutions( _s );
        s.save( t );
//        updateTracksSequencedDetections( t );
//        _tracks.add( t );
        _tracksStates.put( t, CtItemState.NORMAL );
        return t;
    }

    protected void deleteTrackReferences( Session s, CtTracks t ) {

        if( !valid() ) return;

        _s.getCtTrackses().remove( t );
        removeTracksSequencedDetections( t ); //why not?
        s.delete( t );
        _tracksStates.remove( t );
    }

    protected void removeTracksSequencedDetections( CtTracks t ) {
        _tracksSequencedDetections.remove( t );
    }

    protected void updateTracksSequencedDetections( CtTracks t ) {

        if( !valid() ) return;

        // replace with empty set, if it exists:
        _tracksSequencedDetections.remove( t );

        TreeMap< Integer, CtDetections > hm = new TreeMap< Integer, CtDetections >();
        _tracksSequencedDetections.put( t, hm );

        // Now complete the sequence of detections:
        Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

        for( CtTracksDetections td : tds ) {
            CtDetections d = td.getCtDetections();
            CtImages i = d.getCtImages();
            int index = _cc.getTimeOrdinate( i );// _ism.index( i );
//            if( index == null ) continue; // not in sequence

            hm.put( index, d );
//            ArrayList< CtDetections > al = hm.get( index );
//
//            if( al == null ) {
//                al = new ArrayList< CtDetections >();
//                hm.put( index, al );
//            }
//
//            al.add( d );
        }
    }
}
