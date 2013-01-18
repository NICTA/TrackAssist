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

package au.com.nicta.ct.solution.graphics.canvas.tools.annotations;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtAnnotationsTypes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.solution.CtSolutionController;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtAnnotationsModel extends CtChangeModel {

    protected Map<Integer, Set<CtAnnotations>> imagePkMap   = new HashMap< Integer, Set< CtAnnotations > >(); // key = image pk
    protected Map<Integer, Set<CtAnnotations>> imageTimeMap = new HashMap< Integer, Set< CtAnnotations > >(); // key = image time

    public CtAnnotationsModel() {
        super( null );
    }

    public int getImageTime(CtImages image) {
        return CtCoordinatesController.get().getTimeOrdinate( image );
    }

    public void removeFromMaps(CtAnnotations a) {
        imagePkMap  .get( a.getCtImages().getPkImage() ).remove( a );
        imageTimeMap.get( getImageTime( a.getCtImages() ) ).remove( a );
    }

    public void addToMaps(CtAnnotations a) {
        int imagePk = a.getCtImages().getPkImage();
        addToMap(imagePk, a, imagePkMap );

        int imageTime = CtCoordinatesController.get().getTimeOrdinate( a.getCtImages() );
        addToMap(imageTime, a, imageTimeMap );
    }

    public void addToMap(int key, CtAnnotations a, Map<Integer, Set<CtAnnotations>> map ) {
        Set<CtAnnotations> setA = map.get(key);
        if( setA == null ) {
            setA = new HashSet<CtAnnotations>();
            map.put(key, setA);
        }
        setA.add(a);
    }

    public void clear() {
        imagePkMap.clear();
        imageTimeMap.clear();
    }
//    public void load( ) {
//        // load current solution
//        CtSolutions sol = CtSolutionController.getSolutions();
    public void setSolution( CtSolutions s ) {

        if( s == null ) {
            clear();
            return;
        }

        Session session = CtSession.Current();
        session.beginTransaction();

        Query q = session.createQuery(
                " SELECT ctA"
              + " FROM CtAnnotations as ctA"
              + " WHERE ctA.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", s.getPkSolution() );

        for( CtAnnotations a : (List<CtAnnotations>) q.list() ) {
            addToMaps(a);
        }

        session.getTransaction().commit();
    }

    public boolean isImageAnnotated( CtImages image ) {
        Set<CtAnnotations> s = imagePkMap.get( image.getPkImage() );
        if(    s == null
            || s.isEmpty() ) {
            return false;
        }
        return true;
    }

    public boolean isTimeAnnotated( CtImages image ) {
        Set<CtAnnotations> s = imageTimeMap.get( getImageTime( image ) );
        if(    s == null
            || s.isEmpty() ) {
            return false;
        }
        return true;
    }

    public Set<CtAnnotations> getByImageTime( CtImages image ) {
//        System.out.println( "getImageTime(image): " + getImageTime(image) );
        Set<CtAnnotations> s = imageTimeMap.get( getImageTime(image) );
//        System.out.println( "imageTimeMap.size() " + imageTimeMap.size() );
        if( s == null ) {
            return Collections.EMPTY_SET;
        }
        return s;
    }

    public Set<CtAnnotations> get( CtImages image ) {
        Set<CtAnnotations> s = imagePkMap.get( image.getPkImage() );
        if( s == null ) {
            return Collections.EMPTY_SET;
        }
        return s;
    }

    public void save( CtAnnotations a ) {
        Session s = CtSession.Current();
        s.beginTransaction();
        s.update(a);
        s.flush();
        s.getTransaction().commit();
    }

    public void removeRangeType( int startFrameIdx, int endFrameIdx, CtAnnotationsTypes type ) {
        Set<CtAnnotations> toDelete = new HashSet<CtAnnotations>();

        for( Map.Entry<Integer, Set<CtAnnotations>> timeMapEntry : imageTimeMap.entrySet() ) {
            int time = timeMapEntry.getKey().intValue();
            if(    time < startFrameIdx
                || time >   endFrameIdx ) {
                continue;
            }
            for( CtAnnotations a : timeMapEntry.getValue() ) {
                if( a.getCtAnnotationsTypes().getValue().equals( type.getValue() ) ) {
                    toDelete.add(a);
                }
            }
        }

        Session s = CtSession.Current();
        s.beginTransaction();

        for( CtAnnotations a : toDelete ) {
            remove(s, a);
        }

        s.flush();
        s.getTransaction().commit();
    }

    public void remove( CtAnnotations a ) {
        Session s = CtSession.Current();
        s.beginTransaction();
        
        remove( s, a );

        s.flush();
        s.getTransaction().commit();
    }

    public void remove( Session s, CtAnnotations a ) {
        s.delete(a);
        removeFromMaps(a);
    }

//    public CtAnnotations add(CtImages image, int x, int y, String value, CtAnnotationsTypes type) {
//        int defaultTypePk = 1;
////        CtAnnotationsTypes defaultType = (CtAnnotationsTypes) CtSession.Current().get( CtAnnotationsTypes.class, defaultTypePk );
////        return add(image, x, y, value, defaultType );
//        return add(image, x, y, value, defaultType );
//    }

    public CtAnnotations create(CtImages image, double x, double y, String value, CtAnnotationsTypes type ) {
        // Save to DB first because the hash code of CtAnnotations may change
        // after the pk is set by hibernate.
        Session s = CtSession.Current();
        s.beginTransaction();

        CtAnnotations a = new CtAnnotations(); // NB not persisted yet
        a.setCtAnnotationsTypes(type);
        a.setCtImages(image);
        a.setCtSolutions(CtSolutionController.getSolutions());
        a.setValue( value );
        a.setX( x );
        a.setY( y );


        s.save(a);
        s.flush();
        s.getTransaction().commit();

        // Add to cache
        addToMaps(a);

        return a;
    }

    public Rectangle2D getBoundingBox( Collection< CtAnnotations > ca ) {

        if( ca.isEmpty() ) {
            return null;
        }

        Rectangle2D.Double bb = null;

//        if( ca.size() == 1 ) { DAVE: Why is this neccessary ??
//            CtAnnotations a = ca.iterator().next();
////            double w = _zc.toNaturalX(_zc.getSize().width ) - _zc.toNaturalX(0);
////            double h = _zc.toNaturalY(_zc.getSize().height) - _zc.toNaturalY(0);
//            double cx = a.getX();
//            double cy = a.getY();
//            bb = new Rectangle2D.Double(cx-w/2, cy-h/2, w, h);
//        }
//        else {
            // zoom the the bounding box of all annotations
            double l = Double.POSITIVE_INFINITY;
            double r = Double.NEGATIVE_INFINITY;
            double t = Double.POSITIVE_INFINITY;
            double b = Double.NEGATIVE_INFINITY;

            for( CtAnnotations a : ca ) {
                l = Math.min( l, a.getX() );
                r = Math.max( r, a.getX() );
                t = Math.min( t, a.getY() );
                b = Math.max( b, a.getY() );
            }

            bb = new Rectangle2D.Double( l, t, r-l, b-t ); // w and h may be 0

            // give some buffer
//            final double EXPAND_FACTOR = 2;
//            bb.x -= bb.width  * (1-EXPAND_FACTOR) / 2;
//            bb.y -= bb.height * (1-EXPAND_FACTOR) / 2;
//            bb.width  *= EXPAND_FACTOR;
//            bb.height *= EXPAND_FACTOR;
//        }

        return bb;
//
//        _zc.zoomNaturalWindow( bb );
//        _zc.repaint();
    }
//    public CtAnnotations at( CtImages i, int xImage, int yImage ) {
//
//        Set< CtAnnotations > s = _cache.get( i );
//
//        if( s.isEmpty() ) {
//            return null;
//        }
//
//        for( CtAnnotations a : s ) {
//            if( inside( a, xImage, yImage ) ) {
//                return a;
//            }
//        }
//
//        return null;
//    }
}
