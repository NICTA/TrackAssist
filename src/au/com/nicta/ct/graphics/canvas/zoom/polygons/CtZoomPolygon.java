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

package au.com.nicta.ct.graphics.canvas.zoom.polygons;

import au.com.nicta.ct.ui.style.CtPolygonStyle;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.orm.patterns.CtSerializable;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Was: CtDetectionEditor2.CtDetectionPolygon
 * @author davidjr
 */
public class CtZoomPolygon implements CtSerializable {

    protected Point2D center;
//    public class CtZoomPolygonStyle {
//    }

//    public static int unitsPerNaturalPixel = 5; // how many units is contained per real pixel, controls sub-pixel resolution
    public Polygon polygon = new Polygon();
    protected AffineTransform temp = new AffineTransform(); // saves allocation
    
    public CtZoomPolygon() {

    }

    public CtZoomPolygon( Polygon p ) {
        this( p, 1.0 );
    }

    public CtZoomPolygon( Polygon p, double scale ) {
        polygon = new Polygon(
                Arrays.copyOf( p.xpoints, p.npoints ),
                Arrays.copyOf( p.ypoints, p.npoints ),
                p.npoints );
        scale( scale );
    }

    public CtZoomPolygon( String s ) {
        deserialize( s );
    }
    
    public CtZoomPolygon( CtZoomPolygon b ) {
        Polygon pp = b.polygon;
        polygon = new Polygon(
                Arrays.copyOf( b.polygon.xpoints, b.polygon.npoints ),
                Arrays.copyOf( b.polygon.ypoints, b.polygon.npoints ),
                b.polygon.npoints );

        setCenter( findCentroid() );
    }

//    public static Polygon circle( int xc, int yc, int r ) {
////        int[] xs2 = new int[ 4 ];
////        int[] ys2 = new int[ 4 ];
////        xs2[ 0 ] = 100;
////        xs2[ 1 ] = 200;
////        xs2[ 2 ] = 200;
////        xs2[ 3 ] = 100;
////        ys2[ 0 ] = 100;
////        ys2[ 1 ] = 100;
////        ys2[ 2 ] = 200;
////        ys2[ 3 ] = 200;
////
////Polygon p2 = new Polygon( xs2,ys2, 4 );
////return p2;
//        // this alg uses symmetry to compute 8x circle points simultaneously..
//        // However, Polygon must be a single ordered list. So build 8x separate
//        // lists, then concatenate them. Note some lists must be assembled in
//        // reverse order.
//        int r2 = r * r;
//
//        ArrayList< ArrayDeque< Point2D > > al = new ArrayList< ArrayDeque< Point2D > >();
//
//        for( int t = 0; t < 8; ++t ) {
//            al.add( new ArrayDeque() );
//        }
//
//        al.get( 0 ).add( new Point2D.Float( xc, yc - r ) ); // N
//        al.get( 2 ).add( new Point2D.Float( xc + r, yc ) ); // E
//        al.get( 4 ).add( new Point2D.Float( xc, yc + r ) ); // S
//        al.get( 6 ).add( new Point2D.Float( xc - r, yc ) ); // W
//
//        int x = 1;
//        int y = (int)( Math.sqrt( r2 -1 ) + 0.5 );
//
//        while( x < y ) {
//
//            // make a clockwise contour walk in image coord system..
//            al.get( 0 ).addLast ( new Point2D.Float( xc + x, yc - y ) );
//            al.get( 3 ).addFirst( new Point2D.Float( xc + x, yc + y ) ); // == +x, -y as y decreasing, need to add to start to make Clockwise
//            al.get( 4 ).addLast ( new Point2D.Float( xc - x, yc + y ) );
//            al.get( 7 ).addFirst( new Point2D.Float( xc - x, yc - y ) );
//
//            al.get( 2 ).addLast ( new Point2D.Float( xc + y, yc + x ) );
//            al.get( 1 ).addFirst( new Point2D.Float( xc + y, yc - x ) );
//            al.get( 6 ).addLast ( new Point2D.Float( xc - y, yc - x ) );
//            al.get( 5 ).addFirst( new Point2D.Float( xc - y, yc + x ) );
//
//            // +x, -y
//            x += 1;
//            y = (int)( Math.sqrt( r2 - x*x ) + 0.5 ); // since x is getting larger, r2-x*x is subtracting a larger number and hence getting smaller .Y is decreasng.
//        }
//
//        if( x == y ) {
//            al.get( 0 ).addLast( new Point2D.Float( xc + x, yc - y ) );
//            al.get( 3 ).addFirst( new Point2D.Float( xc + x, yc + y ) );
//            al.get( 4 ).addLast ( new Point2D.Float( xc - x, yc + y ) );
//            al.get( 7 ).addFirst( new Point2D.Float( xc - x, yc - y ) );
//        }
//
//        // now assemble the final list of points by concatenating the sectors:
//        int totalLength = 0;
//
//        for( int t = 0; t < 8; ++t ) {
//            totalLength += al.get( t ).size();
//        }
//
//        int[] xs = new int[ totalLength ];
//        int[] ys = new int[ totalLength ];
//
//        int n = 0;
//
//        for( int t = 0; t < 8; ++t ) {
//            ArrayDeque< Point2D > ad = al.get( t );
//
//            for( Point2D p2d : ad ) {
//                xs[ n ] = (int)p2d.getX();
//                ys[ n ] = (int)p2d.getY();
//
//                ++n;
//            }
//        }
//
//        Polygon p = new Polygon( xs, ys, totalLength );
//        return p;
////        return new CtZoomPolygon( p );
//    }

    public void translate( int x, int y ) {
        polygon.translate( x, y );

        setCenter( findCentroid() );
    }

    public void scale( double s ) {

        if( s == 1.0 ) return;
        
        for( int p = 0; p < polygon.npoints; ++p ) {
            polygon.xpoints[ p ] *= s;
            polygon.ypoints[ p ] *= s;
        }

        setCenter( findCentroid() );
    }

    public void transform( AffineTransform at ) {
        Point2D p2d2 = new Point2D.Float();

        for( int p = 0; p < polygon.npoints; ++p ) {
            Point2D p2d1 = new Point2D.Float( polygon.xpoints[ p ], polygon.ypoints[ p ] );

            at.transform( p2d1, p2d2 );

            polygon.xpoints[ p ] = (int)p2d2.getX();
            polygon.ypoints[ p ] = (int)p2d2.getY();
        }

        setCenter( findCentroid() );
    }

    public Point2D getCenter() {
        if( center == null ) {
            center = findCentroid();
        }
        return center;
    }

    public void setCenter( Point2D p2d ) {
        center = p2d;
    }
    
    // http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
    Point2D findCentroid() {//const Point2D* vertices, int vertexCount) {
//        Point2D centroid = new Point2D.Double( 0.0, 0.0 );
//        Point2D centroid = {0, 0};
        double x = 0.0;
        double y = 0.0;
        double signedArea = 0.0;
        double x0 = 0.0; // Current vertex X
        double y0 = 0.0; // Current vertex Y
        double x1 = 0.0; // Next vertex X
        double y1 = 0.0; // Next vertex Y
        double a = 0.0;  // Partial signed area

        // For all vertices except last
        int i = 0;

        for( i = 0; i < (polygon.npoints-1); ++i ) {
            x0 = polygon.xpoints[ i   ];// vertices[i].x;
            y0 = polygon.ypoints[ i   ];// vertices[i].y;
            x1 = polygon.xpoints[ i+1 ];// vertices[i+1].x;
            y1 = polygon.ypoints[ i+1 ];// vertices[i+1].y;

            a = x0*y1 - x1*y0;
            signedArea += a;
            x += (x0 + x1) * a;
            y += (y0 + y1) * a;
        }

        // Do last vertex
        x0 = polygon.xpoints[ i ];//vertices[i].x;
        y0 = polygon.ypoints[ i ];// vertices[i].y;
        x1 = polygon.xpoints[ 0 ];//vertices[0].x;
        y1 = polygon.ypoints[ 0 ];//vertices[0].y;

        a = x0*y1 - x1*y0;
        signedArea += a;
        x += (x0 + x1)*a;
        y += (y0 + y1)*a;

        signedArea *= 0.5;
        x /= (6*signedArea);
        y /= (6*signedArea);

        Point2D centroid = new Point2D.Double( x, y );
        return centroid;
    }

    public String serialize() {
        String s = new String();

//        s += polygon.npoints;
//        s += ',';

        for( int p = 0; p < polygon.npoints; ++p ) {
            s += polygon.xpoints[ p ];
            s += ',';
            s += polygon.ypoints[ p ];
            s += ',';
        }

        return s;
    }

    public void deserialize( String s ) {
        String[] numbers = s.split( "," );

        polygon = new Polygon();

        int values = numbers.length;
        int points = values >> 1;

        for( int p = 0; p < points; ++p ) {
            int x = Integer.valueOf( numbers[ (p*2)   ] );
            int y = Integer.valueOf( numbers[ (p*2)+1 ] );
            polygon.addPoint( x, y );
        }

        setCenter( findCentroid() );
    }

//    public AffineTransform getAffineToScreen( CtZoomCanvas zc, AffineTransform returnValue ) {
//        returnValue = zc.getAffineToScreen( returnValue );
//        returnValue.scale( 1.0/CtSubPixelResolution.unitsPerNaturalPixel,
//                           1.0/CtSubPixelResolution.unitsPerNaturalPixel  );
//        return returnValue;
//    }

    public void paint( Graphics2D g, CtViewpointZoomCanvas zc, CtPolygonStyle zps ) {

        AffineTransform at = g.getTransform(); // save state

        temp = CtSubPixelResolution.getAffineToScreen( zc, temp );

        g.transform( temp );

//        int unitsPerNaturalPixel = b.unitsPerNaturalPixel;

        float lineWidth = zps.borderStroke.getLineWidth();

        if( (float)zc.getZoomScale() > 1 ) { // if zooming in
            lineWidth *= CtSubPixelResolution.unitsPerNaturalPixel / (float)zc.getZoomScale();
        }

        g.setStroke( new BasicStroke( lineWidth ) );
        g.setPaint( zps.borderPaint );
        g.draw( polygon );

        if( zps.fillPaint != null ) {
            g.setPaint( zps.fillPaint );
            g.fill( polygon );
        }

        g.setTransform( at ); // restore state
    }

    public boolean containsNaturalCoord( double naturalX, double naturalY ) {
        return polygon.contains(
                naturalX * CtSubPixelResolution.unitsPerNaturalPixel,
                naturalY * CtSubPixelResolution.unitsPerNaturalPixel  );
    }

    public boolean withinRadiusOfNaturalCoord( double radiusFraction, double naturalX, double naturalY ) {
        Rectangle2D r2d = getBoundingBox();
        Point2D p2d2 = getCenter();

//        naturalX *= CtSubPixelResolution.unitsPerNaturalPixel;
//        naturalY *= CtSubPixelResolution.unitsPerNaturalPixel;

        Point2D p2d1 = new Point2D.Double( naturalX * CtSubPixelResolution.unitsPerNaturalPixel, naturalY * CtSubPixelResolution.unitsPerNaturalPixel );

        double size = Math.min( r2d.getWidth(), r2d.getHeight() ) * radiusFraction;
        double radius = size * 0.5;

        double distance = p2d1.distance( p2d2 );

        if( distance > radius ) {
            return false;
        }

        return true;
    }

    public void add( CtZoomPolygon zp ) {
        add( zp.polygon );
    }

    public void sub( CtZoomPolygon zp ) {
        sub( zp.polygon );
    }

    public void add( Polygon p ) {
        Area a1 = new Area( polygon );
        Area a2 = new Area( p );
        a1.add( a2 );

        polygon = areaToPolygon( a1 );
        setCenter( findCentroid() );
    }

    public void sub( Polygon p ) {
        Area a1 = new Area( polygon );
        Area a2 = new Area( p );
        a1.subtract( a2 );

        polygon = areaToPolygon( a1 );
        setCenter( findCentroid() );
    }

    public Rectangle2D getBoundingBox() {
        return polygon.getBounds2D();
    }
    
    public Area toArea() {// CtZoomCanvas zc, ) {
        Area a = new Area( polygon );

//        int x = (int)Math.rint( zc.toNaturalX(cursor.mouseX) * polygon.unitsPerNaturalPixel );
//        int y = (int)Math.rint( zc.toNaturalY(cursor.mouseY) * polygon.unitsPerNaturalPixel );
//
//        affTemp.setToTranslation(x, y);
//        a.transform(affTemp);

        return a;
    }

    public static Polygon areaToPolygon( Area a ) {

        assert a.isPolygonal() : "detectionArea has to be polygonal";

        PathIterator i = a.getPathIterator( null );

        double[] coords = new double[6];

        // Keep the contour with the largest bounding box
        boolean singlePolygon = true;
        Polygon largest = null;

        while( !i.isDone() ) {

            Polygon p = new Polygon();

            for( ; !i.isDone(); i.next() ) {
                int type = i.currentSegment( coords );
                if( type == PathIterator.SEG_CLOSE ) {
                    break;
                }
                p.addPoint( (int)coords[0], (int)coords[1] );
            }

            i.next();

            if( !i.isDone() ) {
                singlePolygon = false;
            }

            if( largest == null ) {
                largest = p;
            }
            else {
                int largestArea =   largest.getBounds().width
                                  * largest.getBounds().height;
                int area =   p.getBounds().width
                           * p.getBounds().height;
                if( largestArea < area ) {
                    largest = p;
                }
            }
        }

//        if( !singlePolygon ) {
//           JOptionPane.showMessageDialog(
//                    null,
//                    CONFIRM_INVALID_POLYGON,
//                    "Commit Edit",
//                    JOptionPane.OK_OPTION );
//        }
//
//        currDetectionModel.detection.detectionPolygon.polygon = largest;

        return largest;
    }

//    todo add code for (CENTRAL) moments...
//
//    public void moments() {
//        for( int p = 0; p < polygon.npoints; ++p ) {
//            s += polygon.xpoints[ p ];
//            s += ',';
//            s += polygon.ypoints[ p ];
//            s += ',';
//        }
//    }

}
