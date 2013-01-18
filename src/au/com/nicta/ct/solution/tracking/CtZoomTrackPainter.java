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

import au.com.nicta.ct.ui.style.CtPolygonStyle;
import au.com.nicta.ct.ui.style.CtLineStyle;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.*;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

/**
 * Was: CtDetectionEditor2.CtDetectionPolygon
 * @author davidjr
 */
public class CtZoomTrackPainter {

    public HashMap< CtItemState, CtPolygonStyle > _polygonStyles = new HashMap< CtItemState, CtPolygonStyle >();
    public HashMap< CtItemState, CtLineStyle > _lineStylesHistory = new HashMap< CtItemState, CtLineStyle >();
    public HashMap< CtItemState, CtLineStyle > _lineStylesFuture = new HashMap< CtItemState, CtLineStyle >();
    public HashMap< CtItemState, CtLineStyle > _circleStylesOrphan = new HashMap< CtItemState, CtLineStyle >();
    public HashMap< CtItemState, CtLineStyle > _circleStylesTrack = new HashMap< CtItemState, CtLineStyle >();
//    public static int unitsPerNaturalPixel = 5; // how many units is contained per real pixel, controls sub-pixel resolution
//    public Polygon polygon = new Polygon();
    public Color _markerCurrentDetection;
    public Color _markerTrackStart;
    public Color _markerTrackEnd;

    public static final double SENSITIVE_RADIUS_FRACTION = 0.9;

    protected AffineTransform temp = new AffineTransform(); // saves allocation

    public CtZoomTrackPainter() {
        addStyles();
    }

    public void addStyles() {
        _polygonStyles.put( CtItemState.IGNORE, CtPolygonStyle.faint() );
        _polygonStyles.put( CtItemState.NORMAL, CtPolygonStyle.background() );
        _polygonStyles.put( CtItemState.FOCUS, CtPolygonStyle.focus() );
        _polygonStyles.put( CtItemState.SELECTED, CtPolygonStyle.selected() );
        _polygonStyles.put( CtItemState.ATTENTION, CtPolygonStyle.attention() );

//        CtLineStyle trackNormal   = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 0, 127, 0, 100 ) );
//        CtLineStyle orphanNormal   = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( c.getRed(), c.getGreen(), c.getBlue(), 127 ) );
        CtLineStyle historyNormal   = new CtLineStyle( new BasicStroke( 1 * CtSubPixelResolution.unitsPerNaturalPixel,
                                                                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ),
                                                       new Color( 255, 127, 255, 150 ) );
        CtLineStyle historySelected = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel,
                                                                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ),
                                                       new Color( 255, 127, 255, 220 ) );

        CtLineStyle futureNormal   = new CtLineStyle( new BasicStroke( 1 * CtSubPixelResolution.unitsPerNaturalPixel,
                                                                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ),
                                                       new Color( 255, 255, 0, 150 ) );
        CtLineStyle futureSelected = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel,
                                                                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ),
                                                       new Color( 255, 255, 0, 220 ) );

        _lineStylesHistory.put( CtItemState.NORMAL,   historyNormal   );
        _lineStylesHistory.put( CtItemState.SELECTED, historySelected );

        _lineStylesFuture.put( CtItemState.NORMAL,   futureNormal   );
        _lineStylesFuture.put( CtItemState.SELECTED, futureSelected );

        Color c = CtConstants.NictaYellow;
        CtLineStyle orphanNormal   = new CtLineStyle( new BasicStroke( 1 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( c.getRed(), c.getGreen(), c.getBlue(), 50 ) );
        CtLineStyle orphanFocus    = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( c.getRed(), c.getGreen(), c.getBlue(), 100 ) );
        CtLineStyle orphanSelected = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( 255, 255, 200 ) );

        CtLineStyle trackNormal   = new CtLineStyle( new BasicStroke( 1 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( 0, 255, 0,  50 ) );
        CtLineStyle trackFocus    = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( 0, 255, 0, 100 ) );
        CtLineStyle trackSelected = new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( 0, 255, 0, 200 ) );

        _circleStylesOrphan.put( CtItemState.NORMAL,   orphanNormal   );
        _circleStylesOrphan.put( CtItemState.FOCUS,    orphanFocus    );
        _circleStylesOrphan.put( CtItemState.SELECTED, orphanSelected );

        _circleStylesTrack.put( CtItemState.NORMAL,   trackNormal   );
        _circleStylesTrack.put( CtItemState.FOCUS,    trackFocus    );
        _circleStylesTrack.put( CtItemState.SELECTED, trackSelected );

        _markerCurrentDetection = new Color( 255, 0, 255, 200 );
        _markerTrackStart = new Color( 255, 255, 255, 200 );
        _markerTrackEnd = new Color( 255, 255, 0, 200 );
    }

    protected void paintDetectionContour( Graphics2D g, CtViewpointZoomCanvas zc, CtCanvasLayer cl, CtZoomPolygon zp, CtItemState is ) {
        CtPolygonStyle ps = _polygonStyles.get( is ); // TODO different set of styles?
        zp.paint( g, zc, ps);
    }

    public void paintDetectionCircle( Graphics2D g, CtViewpointZoomCanvas zc, CtCanvasLayer cl, CtZoomPolygon zp, CtItemState is, int currentIndex, int detectionIndex, boolean isOrphan, boolean isStart, boolean isEnd ) {
        paintDetectionCircle( g, CtSubPixelResolution.getAffineToScreen( zc, temp ), cl, zp, is, currentIndex, detectionIndex, isOrphan, isStart, isEnd );
    }

    public void paintDetectionCircle( Graphics2D g, AffineTransform toScreen, CtCanvasLayer cl, CtZoomPolygon zp, CtItemState is, int currentIndex, int detectionIndex, boolean isOrphan, boolean isStart, boolean isEnd ) {

        CtItemState is2 = is;

        if(    ( is == CtItemState.NORMAL )
            && ( currentIndex == detectionIndex ) ) {
            is2 = CtItemState.FOCUS;
        }
        
        CtLineStyle ls = null;

        if( isOrphan ) {
            ls = _circleStylesOrphan.get( is2 );
        }
        else {
            ls = _circleStylesTrack.get( is2 );
        }
//        if( )
//        CtPolygonStyle ps = _polygonStyles.get( is );
        Rectangle2D r2d = zp.getBoundingBox();
        Point2D p2d = zp.getCenter();

        double size = Math.min( r2d.getWidth(), r2d.getHeight() ) * SENSITIVE_RADIUS_FRACTION;
        double radius = size * 0.5;

        double x = p2d.getX() - radius;
        double y = p2d.getY() - radius;

        AffineTransform at = g.getTransform(); // save state

//        temp = CtSubPixelResolution.getAffineToScreen( zc, temp );

        g.transform( toScreen );
        g.setStroke( ls._s );//borderStroke );
        g.setColor( ls._c );//g.setPaint( ls.borderPaint );

//         g.setColor( Color.RED );
        g.drawOval( (int)x, (int)y, (int)size, (int)size );

        if( !isOrphan ) {
            Color c = null;

            if( isEnd ) {
                c = _markerTrackEnd;
            }
            else if( isStart ) {
                c = _markerTrackStart;
            }
            else if(currentIndex == detectionIndex) {
                c = _markerCurrentDetection;
            }

            if( c != null ) {
                g.setColor( c );
                int sizeMarker = 3 * CtSubPixelResolution.unitsPerNaturalPixel;
                int radiusMarker = sizeMarker >> 1;
                x = p2d.getX() - (double)radiusMarker;
                y = p2d.getY() - (double)radiusMarker;
                g.fillOval( (int)x, (int)y, sizeMarker, sizeMarker );
            }
        }

        g.setTransform( at ); // restore state
    }

    public void paintTrackTuple(
            Graphics2D g,
            CtViewpointZoomCanvas zc,
            CtCanvasLayer cl,
            int index,
            CtItemState is,
            CtZoomPolygon zpA,
            int indexA,
            CtZoomPolygon zpB,
            int indexB,
            CtZoomPolygon zpC,
            int indexC,
            boolean isStart,
            boolean isEnd ) { // paint( track, trackState, prev.det, this.det, next.det )

        paintTrackTuple(g, CtSubPixelResolution.getAffineToScreen( zc, temp ), cl, index, is, zpA, indexA, zpB, indexB, zpC, indexC, isStart, isEnd );
    }
    
    public void paintTrackTuple(
        Graphics2D g,
        AffineTransform toScreen,
        CtCanvasLayer cl,
        int index,
        CtItemState is,
        CtZoomPolygon zpA,
        int indexA,
        CtZoomPolygon zpB,
        int indexB,
        CtZoomPolygon zpC,
        int indexC,
        boolean isStart,
        boolean isEnd ) { // paint( track, trackState, prev.det, this.det, next.det )

        paintDetectionCircle( g, toScreen, cl, zpB, is, index, indexB, false, isStart, isEnd ); // always

        if( zpC != null ) {
            boolean future = false;

            if( indexC > index ) {
                future = true;
            }
//            paintDetectionCircle( g, zc, cl, zpC, is, index, indexA, false );
            paintBetween( g, toScreen, is, zpB, zpC, future );
        }

//        if( zpA != null ) {
////            paintDetectionCircle( g, zc, cl, zpA, is, index, indexA, false );
//            paintBetween( g, zc, is, zpA, zpB, future );
//        }

    }

//    public void paintAt( Graphics2D g, CtZoomCanvas zc, CtZoomPolygon zp, CtPolygonStyle ps, CtLineStyle ls ) {
//
//        Rectangle2D r2d = zp.getBoundingBox();
//        Point2D p2d = zp.getCenter();
//
//        double size = Math.min( r2d.getWidth(), r2d.getHeight() ) * SENSITIVE_RADIUS_FRACTION;
//        double radius = size * 0.5;
//
//        double x = p2d.getX() - radius;
//        double y = p2d.getY() - radius;
//
//        AffineTransform at = g.getTransform(); // save state
//
//        temp = CtSubPixelResolution.getAffineToScreen( zc, temp );
//
//        g.transform( temp );
//
////        g.scale( zc.getZoomScale(), zc.getZoomScale() );
////        g.scale( 1.0/CtSubPixelResolution.unitsPerNaturalPixel,
////                 1.0/CtSubPixelResolution.unitsPerNaturalPixel );
//
//        if( ls != null ) {
//            g.setStroke( ls._s );
//            g.setColor( ls._c );
//            g.drawOval( (int)x, (int)y, (int)size, (int)size );
//        }
//
//        g.setStroke( ps.borderStroke );
//        g.setPaint( ps.borderPaint );
//
////         g.setColor( Color.RED );
////System.out.println( "fuskssksksdk*******************************" );
//        g.drawOval( (int)x, (int)y, (int)size, (int)size );
//        g.setTransform( at ); // restore state
//
//    }
//
//    public void paintTerminus( Graphics2D g, CtZoomCanvas zc, CtZoomPolygon zp, CtLineStyle ls ) {
//        AffineTransform at = g.getTransform(); // save state
//
//        temp = CtSubPixelResolution.getAffineToScreen( zc, temp );
//
//        g.transform( temp );
//
//        g.setStroke( ls._s );
//        g.setColor( Color.YELLOW );
//
//        Point2D p2d1 = zp.getCenter();
//
//        BasicStroke s = (BasicStroke)ls._s;
//        double size = s.getLineWidth();
//        double radius = size * 0.5;
//
//        g.fillOval( (int)( p2d1.getX() -radius ), (int)( p2d1.getY() -radius ), (int)size, (int)size );
//        g.setTransform( at ); // restore state
//    }

    public void paintBetween( Graphics2D g, CtViewpointZoomCanvas zc, CtItemState is, CtZoomPolygon zp1, CtZoomPolygon zp2, boolean future ) {
        paintBetween( g, CtSubPixelResolution.getAffineToScreen( zc, temp ), is, zp1, zp2, future );
    }

    public void paintBetween( Graphics2D g, AffineTransform toScreen, CtItemState is, CtZoomPolygon zp1, CtZoomPolygon zp2, boolean future ) {

        CtLineStyle ls = null;

        if( future ) {
            ls = _lineStylesFuture.get( is );
        }
        else {
            ls = _lineStylesHistory.get( is );
        }

        AffineTransform at = g.getTransform(); // save state

        g.transform( toScreen );
        g.setStroke( ls._s );
        g.setColor( ls._c );

        Point2D p2d1 = zp1.getCenter();
        Point2D p2d2 = zp2.getCenter();

//         g.setColor( Color.RED );
        g.drawLine( (int)p2d1.getX(), (int)p2d1.getY(), (int)p2d2.getX(), (int)p2d2.getY() );
        g.setTransform( at ); // restore state
    }

}
