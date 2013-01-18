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

package au.com.nicta.ct.graphics.canvas.control;

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Alan
 */
public class CtControlPointsMouseAdapter extends MouseAdapter {// implements CtSerializable {

//    public CtEventDispatcher eventDispatcher = new CtEventDispatcher();
//    public static String ACTION_MOVED = "MOVED";

    static final boolean DEFER_PROPERTIES_WRITE = false;
    static final double DEFAULT_FINE_MOVEMENT_FACTOR = 0.2;

    CtZoomCanvas zc; // may be null
    public List<CtControlPoint> points = new ArrayList<CtControlPoint>();
    boolean mousePressInsidePoint = false;
    double fineMovementFactor = DEFAULT_FINE_MOVEMENT_FACTOR;

    Point prevMouseLocation = new Point();

//    public CtControlPointsMouseAdapter() {
//        this( null );
//    }
//
    public CtControlPointsMouseAdapter( CtCanvasLayer cl ) {//, CtZoomCanvas z ) {
        CtZoomCanvas zc = (CtZoomCanvas)cl.getParent();
        this.zc = zc;
        
        cl.addMouseListener( this );
        cl.addMouseMotionListener( this );
    }

    public void unselectedAll() {
        for(CtControlPoint p : points ) {
            p.setSelected(false);
        }
    }

    public void paint(Graphics2D g) {
        if( zc == null ) {
            return;
        }

        for(CtControlPoint p : points ) {
            p.paint( g, zc );
        }
    }

//    public String serialize() {
//        String s = new String();
//
//        for( CtControlPoint cp : points ) {
////            cp.p (x,y)
//            s += cp.p.x;
//            s += ',';
//            s += cp.p.y;
//            s += ',';
//
//        }
//
//        return s;
//    }
//
//    public void deserialize( String s ) {
//        String[] numbers = s.split( "," );
//
//        int values = numbers.length;
//        int coords = values >> 1;
//
////        for( CtControlPoint cp : points ) {
//        for( int p = 0; p < coords; ++p ) {
//
//            CtControlPoint cp = points.get( p );
//
//            double x = Double.valueOf( numbers[ (p*2)   ] );
//            double y = Double.valueOf( numbers[ (p*2)+1 ] );
//
//            cp.setLocation( x, y, false );
//        }
//    }

    @Override public void mousePressed(MouseEvent e) {
        int ex = e.getX();
        int ey = e.getY();

        mousePressInsidePoint = false;
        for(CtControlPoint p : points ) {
            if( !p.isInCatchment( zc, ex, ey ) ) {
                continue;
            }

            mousePressInsidePoint = true;

            prevMouseLocation.x = e.getX();
            prevMouseLocation.y = e.getY();

            unselectedAll();

            // select current
            p.setSelected(true);
            p.pointerOffsetX = ex - zc.toScreenX(p.getX());
            p.pointerOffsetY = ey - zc.toScreenY(p.getY());
            zc.repaint();
            e.consume();  // no lower layers get this event
            break;
        }
    }

    @Override public void mouseMoved(MouseEvent e) {
        int ex = e.getX();
        int ey = e.getY();
        for(CtControlPoint p : points ) {
            if( p.isInCatchment( zc, ex, ey ) ) {
                p.setMouseOver(true);
                e.consume();  // no lower layers get this event
                zc.repaint();
            }
            else {
                p.setMouseOver(false);
                zc.repaint();
            }
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        mouseDraggedOrReleased( e, true );
    }

    @Override public void mouseDragged( MouseEvent e ) {
        if( DEFER_PROPERTIES_WRITE ) {
            mouseDraggedOrReleased( e, false );
        }
        else {
            mouseDraggedOrReleased( e, true );
        }
    }

    public void mouseDraggedOrReleased( MouseEvent e, boolean fireEvents ) {

        if( !mousePressInsidePoint ) {
            // then we're not dragging any of the points, just let event
            // go to lower layer.
            return;
        }

        for( CtControlPoint p : points ) {
            if( !p.isSelected() ) {
                continue;
            }

//            System.out.println("e.getX(): " + (e.getX()) );
//            System.out.println("p.pointerOffsetX: " + (p.pointerOffsetX) );
//            System.out.println("e.getY(): " + (e.getY()) );
//            System.out.println("p.pointerOffsetY: " + (p.pointerOffsetY) );

            if( (e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0 ) { // fine movement
                double dx = (e.getX() - prevMouseLocation.x) / zc.getZoomScale();
                double dy = (e.getY() - prevMouseLocation.y) / zc.getZoomScale();
                p.setLocation( p.getX() + fineMovementFactor * dx,
                               p.getY() + fineMovementFactor * dy,
                               fireEvents );
            }
            else {
                double x = zc.toNaturalX(e.getX()-p.pointerOffsetX);
                double y = zc.toNaturalY(e.getY()-p.pointerOffsetY);
                p.setLocation( x, y, fireEvents );
            }

            prevMouseLocation.x = e.getX();
            prevMouseLocation.y = e.getY();

//            eventDispatcher.dispatchActionEvent(new ActionEvent(this, Event.ACTION_EVENT, ACTION_MOVED));

            e.consume();
            zc.repaint();
        }
    }

    @Override public void mouseClicked(MouseEvent e) {
        if( !mousePressInsidePoint ) {
            unselectedAll();
            zc.repaint();
        }
    }

}
