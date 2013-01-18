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

import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.ui.swing.graphics.CtOval;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 *
 * @author Alan
 */
public class CtControlPoint {

    public static String EVT_MOVED = "CtControlPointMoved";
    public CtChangeSupport changeSupport = new CtChangeSupport(this);

    protected static BasicStroke DEFAULT_STROKE = new BasicStroke(1);  // saves on the number of objects
    protected static int DEFAULT_RADIUS = 10;

    public Point2D.Double p = new Point2D.Double( DEFAULT_RADIUS, DEFAULT_RADIUS );
    // offset the control point away from the handle of the point (handle is the
    // mouse manipulable and visible part on the screen
    private Point2D.Double handleOffset = new Point2D.Double(0, 0);

    private boolean selected = false;
    private boolean mouseOver = false;

    public class Style {
        public int radius;
        public Paint fillPaint;
        public Paint borderPaint;
        public Stroke borderStroke;
        public Shape cursorCatchment;
    }

    public Style defaultStyle = new Style();
    public Style selectedStyle = new Style();
    public Style mouseOverStyle = new Style();

//    CtViewpointZoomCanvas zc; // may be null
    boolean mousePressOverPoint = false;
    double pointerOffsetX = 0;
    double pointerOffsetY = 0;
    Shape cursorCatchment;

    public CtControlPoint( Point2D p ) {
//        this( null, p );
//    }
//
//    public CtControlPoint(CtViewpointZoomCanvas zc, Point2D p) {
//        this(zc);
//        setLocation(p);
//    }
//
//    public CtControlPoint(CtViewpointZoomCanvas zc) {
//        this.zc = zc;

        defaultStyle.radius = DEFAULT_RADIUS;
        defaultStyle.fillPaint = CtConstants.NictaYellow;//Color.BLUE;
        defaultStyle.borderPaint = Color.BLACK;
        defaultStyle.borderStroke = DEFAULT_STROKE;
        defaultStyle.cursorCatchment = new Rectangle(
                -defaultStyle.radius,
                -defaultStyle.radius,
                 defaultStyle.radius*2,
                 defaultStyle.radius*2 );

        selectedStyle.radius = DEFAULT_RADIUS+2;
        selectedStyle.fillPaint = Color.WHITE;//CtConstants.NictaHighlight;//Color.RED;
        selectedStyle.borderPaint = Color.BLACK;
        selectedStyle.borderStroke = DEFAULT_STROKE;
        selectedStyle.cursorCatchment = new Rectangle(
                -selectedStyle.radius,
                -selectedStyle.radius,
                 selectedStyle.radius*2,
                 selectedStyle.radius*2 );

        mouseOverStyle.radius = DEFAULT_RADIUS;
        mouseOverStyle.fillPaint = CtConstants.NictaPurple;// Color.RED;
        mouseOverStyle.borderPaint = Color.BLACK;
        mouseOverStyle.borderStroke = DEFAULT_STROKE;
        mouseOverStyle.cursorCatchment = new Rectangle(
                -mouseOverStyle.radius,
                -mouseOverStyle.radius,
                 mouseOverStyle.radius*2,
                 mouseOverStyle.radius*2 );

        cursorCatchment = defaultStyle.cursorCatchment;

        setLocation( p );
    }

    void setHandleOffset( int x, int y ) {
        handleOffset.x = x;
        handleOffset.y = y;
    }

    void setHandleOffsetPolar(double magnitude, double angle) {
        handleOffset.x = magnitude * Math.cos(angle);
        handleOffset.y = magnitude * Math.sin(angle);
    }

    public void setSelected(boolean b) {
        selected = b;
        if( selected ) {
            cursorCatchment = selectedStyle.cursorCatchment;
        }
        else {
            cursorCatchment = defaultStyle.cursorCatchment;
        }
    }

    public double getX() {
        return p.x;
    }

    public double getY() {
        return p.y;
    }

    public Point2D.Double getLocation() {
        return getLocation(new Point2D.Double());
    }

    public Point2D.Double getLocation(Point2D.Double p) {
        p.x = this.p.x;
        p.y = this.p.y;
        return p;
    }

    public void setLocation(Point2D p) {
        setLocation(p, true);
    }

    public void setLocation(Point2D p, boolean dispatchEvent) {
        setLocation(p.getX(), p.getY(), dispatchEvent);
    }

    public void setLocation(double x, double y) {
        setLocation(x, y, true);
    }

    public void setLocation(double x, double y, boolean dispatchEvent) {
        if( this.p.x == x && this.p.y == y ) {
            if( dispatchEvent ) {
                changeSupport.fire(EVT_MOVED);
            }
            return;
        }
        
        this.p.x = x;
        this.p.y = y;
        if( dispatchEvent ) {
            changeSupport.fire(EVT_MOVED);
        }
    }

    public void setMouseOver(boolean b) {
        mouseOver = b;
    }

    public boolean isSelected() {
        return selected;
    }

    protected void paint( Graphics2D g, CtZoomCanvas zc )
    {
        if( isSelected() ) {
            draw(g, zc, selectedStyle);
        }
        else {
            if( mouseOver ) {
                draw( g, zc, mouseOverStyle );
            }
            else {
                draw( g, zc, defaultStyle );
            }
        }
    }

    public void draw( Graphics2D g, CtZoomCanvas zc, Style style ) {

        double sx = zc.toScreenX(p.x) + handleOffset.x;
        double sy = zc.toScreenY(p.y) + handleOffset.y;

        if( style.fillPaint != null ){
            g.setPaint(style.fillPaint);

            CtOval.fillOval(g, sx, sy, style.radius);
        }

        g.setPaint(style.borderPaint);
        g.setStroke(style.borderStroke);
        CtOval.drawOval(g, sx, sy, style.radius);
    }

    public boolean isInCatchment( CtZoomCanvas zc, int x, int y ) {
        return cursorCatchment.contains(
                x - zc.toScreenX(this.p.x) - handleOffset.x,
                y - zc.toScreenY(this.p.y) - handleOffset.y );
    }

}
