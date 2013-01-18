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
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.awt.geom.Point2D;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * has
 *  - serializable persistent properties, stored as strings
 *  - transient object data, stored in object-directory??
 *  - core DB data, stored as DB types?
 * @author davidjr
 */
//public class CtDetectionsModel extends CtChangeModel { // sensitive to changes and emits events on changes

//    public enum CtDetectionStateQty {
//        NONE,
//        ONE,
//        TWO,
//        ZERO_OR_MORE,
//        ONE_OR_MORE,
//        TWO_OR_MORE
//    }
//
//    public enum CtInteractionState {
//        WEAK, // e.g. not confirmed, uncertain, weak, tentative
//        NORMAL, // ordinary view
//        FOCUS, // e.g. on mouseover
//        SELECTED, // deliberately selected individually or in group
//        ATTENTION // e.g. should draw attention
//    }

//    protected ArrayList< CtDetections > _detections = new ArrayList< CtDetections >(); // persistent database objects, keyed by PK
//    protected HashMap< CtDetections, CtInteractionState > _states = new HashMap< CtDetections, CtInteractionState >();
//    protected HashMap< CtDetections, CtZoomPolygon > _boundaries = new HashMap< CtDetections, CtZoomPolygon >();
//    protected HashMap< CtImages, CtZoomPolygon > _boundaries = new HashMap< CtDetections, CtZoomPolygon >();

//    protected HashMap< CtDetections, Integer > _times = new HashMap< CtDetections, Integer >();
//    protected HashSet< CtDetections > _selected = new HashSet< CtDetections >();

//    public CtImageSequenceModel _ism;

//    public CtDetectionsModel() {// CtImageSequenceModel ism ) {
//        super( null );
////        this._ism = ism;
//    }

//    public AbstractCollection< CtDetections > getDetections() { // I might want to change the container type
//        return _detections;
//    }

//    public AbstractCollection< CtDetections > getSelected() { // I might want to change the container type
//        return _selected;
//    }

//    public boolean satisfies( CtDetectionStateQty q, ArrayList< CtInteractionState > states ) {
//
//        if( states.isEmpty() ) {
//            return true;
//        }
//
//        Collection< CtDetections > matching = getDetectionsWithStates( states );
//
//        int matches = matching.size();
//
//        boolean satisfies = false;
//
//        switch( q ) {
//            case NONE: if( matches == 0 ) satisfies = true; break;
//            case ONE: if( matches == 1 ) satisfies = true; break;
//            case TWO: if( matches == 2 ) satisfies = true; break;
//            case ZERO_OR_MORE: satisfies = true; break;
//            case ONE_OR_MORE: if( matches >= 1 ) satisfies = true; break;
//            case TWO_OR_MORE: if( matches >= 2 ) satisfies = true; break;
//        }
//
//        return satisfies;
//    }

////////////////////////////////////////////////////////////////////////////////
//    public void addDetectionsIn( CtImages i, CtSolutions s ) {
//        Set< CtDetections > ds = i.getCtDetectionses();
//
//        // filter by solution:
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//
//        int pkSolution = s.getPkSolution();
//
//        for( CtDetections d : ds ) {
//            if( d.getCtSolutions().getPkSolution() == pkSolution ) {
//                al.add( d );
//            }
//        }
//
//        addDetections( al );
//    }
//
//    public void addDetection( CtDetections d ) {
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//        al.add( d );
//        addDetections( al );
//    }
//
//    public void deleteDetection( CtDetections d ) {
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//        al.add( d );
//        deleteDetections( al );
//    }
    
//    public void addDetections( Collection< CtDetections > ac ) {
//        for( CtDetections d : ac ) {
//            _detections.add( d );
////            _states.add( ...
////            _boundaries.add( ...
//            _boundaries.put( d, new CtZoomPolygon( d.getBoundary() ) );
//            _states.put( d, CtInteractionState.NORMAL );
////            _times.put(
//        }
//    }

//    public void deleteDetections( Collection< CtDetections > ac ) {
//        for( CtDetections d : ac ) {
//            _detections.remove( d );
//            _states.remove( d );
//            _boundaries.remove( d );
////            _times.remove( d );
//        }
//    }
//
//    public void deleteDetections() {
//        deleteDetections( new ArrayList< CtDetections >( _detections ) );
//    }

//    public int getTime( CtDetections d ) {
//        return _times.get( d );
//    }

//    public CtInteractionState getState( CtDetections d ) {
//        return _states.get( d );
//    }
//
//    public void setState( CtDetections d, CtInteractionState ds ) {
//        _states.put( d, ds );
//        // TODO: Apply to persistent state
//    }
//
//    public void setState( Collection< CtDetections > ac, CtInteractionState ds ) {
//        for( CtDetections d : ac ) {
//            setState( d, ds );
//        }
//    }
    
//    public void selectDetections( AbstractCollection< CtDetections > ac ) {
//        for( CtDetections d : ac ) {
//            _selected.add( d );
//        }
//    }
//
//    public void deselectDetections( AbstractCollection< CtDetections > ac ) {
//        for( CtDetections d : ac ) {
//            _selected.remove( d );
//        }
//    }

//    public void setDetectionsStatesAt( Point2D p2d, Collection< CtAbstractPair< CtInteractionState, CtInteractionState > > mappings ) {
//
//        HashSet< CtInteractionState > hs = new HashSet< CtInteractionState >();
//
//        for( CtAbstractPair< CtInteractionState, CtInteractionState > ap : mappings ) {
//            hs.add( ap._first );
//        }
//
//        Collection< CtDetections > ac = getDetectionsWithStates( p2d, hs );
//
//        for( CtDetections d : ac ) {
//
//            CtInteractionState ds = getState( d );
//            // d is in one of the states we're interested in at the point we're interested in.
//            for( CtAbstractPair< CtInteractionState, CtInteractionState > ap : mappings ) {
//                if( ap._first == ds ) {
//                    setState( d, ap._second );
//                    break;
//                }
//            }
//        }
//    }

//    public Collection< CtDetections > getDetectionsWithState( CtInteractionState ds ) {
//        ArrayList< CtInteractionState > al = new ArrayList< CtInteractionState >();
//        al.add( ds );
//        return getDetectionsWithStates( null, al );
//    }
//
//    public Collection< CtDetections > getDetectionsWithStates( Collection< CtInteractionState > ac ) {
//        return getDetectionsWithStates( null, ac );
//    }
//
//    public Collection< CtDetections > getDetectionsWithStates( Point2D p2d, Collection< CtInteractionState > ac ) {
//
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//
//        Set< Entry< CtDetections, CtZoomPolygon > > es = _boundaries.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< CtDetections, CtZoomPolygon > e = (Entry< CtDetections, CtZoomPolygon >)i.next();
//
//            CtZoomPolygon zp = e.getValue();
//            CtDetections d = e.getKey();
//
//            boolean valid = true;
//
//            if( p2d != null ) {
//                if( !zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
//                    valid = false;
//                }
//            }
//
//            if( !valid ) {
//                continue;
//            }
//
//            CtInteractionState ds_i = getState( d );
//
//            for( CtInteractionState ds : ac ) {
//                if( ds_i == ds ) {
//                    al.add( d );
//                    break;
//                }
//            }
//        }
//
//        return al;
//    }
//
//    public Collection< CtDetections > getDetections( Point2D p2d ) {
//
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//
//        Set< Entry< CtDetections, CtZoomPolygon > > es = _boundaries.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< CtDetections, CtZoomPolygon > e = (Entry< CtDetections, CtZoomPolygon >)i.next();
//
//            CtZoomPolygon zp = e.getValue();
//            CtDetections d = e.getKey();
//
//            if( zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
//                al.add( d );
//            }
//        }
//
//        return al;
//    }

//    public Collection< CtDetections > getDetectionsWithStatesCentredAt( Point2D p2d, Collection< CtInteractionState > ac ) {
////    public Collection< CtDetections > getDetectionsWithStates( Point2D p2d, Collection< CtDetectionState > ac ) {
//
//        ArrayList< CtDetections > al = new ArrayList< CtDetections >();
//
//        Set< Entry< CtDetections, CtZoomPolygon > > es = _boundaries.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< CtDetections, CtZoomPolygon > e = (Entry< CtDetections, CtZoomPolygon >)i.next();
//
//            CtZoomPolygon zp = e.getValue();
//            CtDetections d = e.getKey();
//
//            boolean valid = true;
//
//            if( p2d != null ) {
////                if( !zp.containsNaturalCoord( p2d.getX(), p2d.getY() ) ) {
////                    valid = false;
////                }
////                Rectangle2D r2d = zp.getBoundingBox();
////                Point2D p2d2 = zp.getCenter();
////
////                double size = Math.min( r2d.getWidth(), r2d.getHeight() ) * CtZoomTrackPainter.TRACK_DETECTION_RADIUS_FRACTION;
////                double radius = size * 0.5;
////                double distance = p2d.distance( p2d2 );
////
////                if( distance > radius ) {
////                    valid = false;
////                }
//                if( !zp.withinRadiusOfNaturalCoord( CtZoomTrackPainter.TRACK_DETECTION_RADIUS_FRACTION, p2d.getX(), p2d.getY() ) ) {
//                    valid = false;
//                }
//            }
//
//            if( !valid ) {
//                continue;
//            }
//
//            CtInteractionState ds_i = getState( d );
//
//            for( CtInteractionState ds : ac ) {
//                if( ds_i == ds ) {
//                    al.add( d );
//                    break;
//                }
//            }
//        }
//
//        return al;
//    }

//    public CtZoomPolygon getBoundary( CtDetections d ) {
//        return _boundaries.get( d );
//    }

//    public void setBoundary( CtDetections d, CtZoomPolygon zp ) {
//        _boundaries.put( d, new CtZoomPolygon( zp ) );
////        d.setBoundary( zp.serialize() );
////        CtSession.Current().flush();
//        // TODO: Apply to persistent state
//    }
//    public String boundary()
    // need easy access to properties
//    public static String propertyKey( String property, CtDetections d ) {
//        return propertyKey( property, d.getPkDetection() );
//    }
//
//    public static String propertyKey( String property, int pk ) {
//        return null;
//    //public static String propertyKey( String classname, String prefix, int pk ) {
//    }
//
//    public Object getProperty( String property ) {
//        return null;
//    }
//
//    public void putProperty( String property, Object o ) {
//
//    }
//}//
