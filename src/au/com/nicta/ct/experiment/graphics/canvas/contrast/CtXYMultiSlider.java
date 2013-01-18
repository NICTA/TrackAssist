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

package au.com.nicta.ct.experiment.graphics.canvas.contrast;

import au.com.nicta.ct.ui.style.CtColorPalette;
import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasPainter;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author Alan
 */
public class CtXYMultiSlider implements CtCanvasPainter, CtChangeListener {

    CtCanvasLayer layer;
    CtXYMultiSliderModel model;

    // parameters
    int lineWidth = 1;
    int lineCursorCatchmentSize = 3;
    int spotRadius = 5;
    int spotCursorCatchmentSizeX = 6;
    int spotCursorCatchmentSizeY = 12;
    boolean showVerticalLine = true;
    boolean showHorizontalLine = true;

    // state
    boolean valueIsAdjusting = false;
    boolean isValueChanged;

    public boolean isIsValueChanged() {
        return isValueChanged;
    }

    public void setIsValueChanged(boolean isValueChanged) {
        this.isValueChanged = isValueChanged;
    }
    Point mousePressedLocation = new Point();

    Stroke lineStroke = new BasicStroke(1);
    Color lineColor = CtColorPalette.NICTA_PURPLE;
    Color fixedLineColor = new Color(128,128,128);

    Color spotColor = CtColorPalette.NICTA_DARK_PURPLE;
    Color fixedSpotColor = new Color(128,128,128);
    Color mouseOverSpotColor = CtColorPalette.NICTA_LIGHT_PURPLE;


    enum MouseOverState {
        NONE,
        SPOT,
        VERTICAL_LINE,
        HORIZONTAL_LINE
    };

    MouseOverState mouseOverState = MouseOverState.NONE;
    int mouseOverIdx = -1;
    int selectedIdx = -1;
    
    Path2D.Float hourGlass;

    {
        final float r = 10;
        hourGlass = new Path2D.Float();
        hourGlass.moveTo( 0, 0);
        hourGlass.lineTo( r/2, r);
        hourGlass.lineTo( -r/2, r);
        hourGlass.lineTo( 0, 0);
        hourGlass.lineTo( -r/2, -r);
        hourGlass.lineTo(  r/2, -r);
        hourGlass.closePath();
    }

    CtXYMultiSlider(
            CtCanvasLayer layer,
            int minX, int maxX,
            int minY, int maxY,
            boolean orderedX,
            boolean orderedY ) {

        this.layer = layer;
        model = new CtXYMultiSliderModel(minX, maxX, minY, maxY, orderedX, orderedY);
        model.changeSupport.addListener(this);
        setupMouseListener();
        setupMouseMotionListener();
    }

    public void addThumb(int x, int y) {
        addThumb(model.getNumPoints(), x, y);
    }

    public void addThumb(int idx, int x, int y) {
        model.addValue(idx, x, y);
    }

    public void addListener(CtChangeListener l) {
        model.changeSupport.addListener(l);
    }

    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    public void setShowVerticalLine(boolean b) {
        showVerticalLine = b;
    }

    public void setShowHorizontalLine(boolean b) {
        showHorizontalLine = b;
    }

    public void setFixed(int idx, boolean x, boolean y) {
        model.setFixedX(idx, x);
        model.setFixedY(idx, y);
    }

    public int getScreenX(int idx) {
        return fromModelToScreenX( model.getValueX(idx) );
    }

    public int getScreenY(int idx) {
        return fromModelToScreenY( model.getValueY(idx) );
    }

    public int getValueX(int idx) {
        return (int)model.getValueX(idx);
    }

    public int getValueY(int idx) {
        return (int)model.getValueY(idx);
    }

    public int getNumPoints() {
        return model.getNumPoints();
    }

    public int getMinX() {
        return (int)model.getMinX();
    }

    public int getMaxX() {
        return (int)model.getMaxX();
    }

    public int getMinY() {
        return (int)model.getMinY();
    }

    public int getMaxY() {
        return (int)model.getMaxY();
    }

    public void setMinMax(int minX, int maxX, int minY, int maxY) {
        model.setMinMax(minX, maxX, minY, maxY);
    }

    void setupMouseListener() {
        layer.addMouseListener( new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedLocation = e.getPoint();
                updateMouseState( e.getX(), e.getY(), true );
                valueIsAdjusting = (mouseOverState != MouseOverState.NONE);
                selectedIdx = -1;
                layer.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                valueIsAdjusting = false;
                model.changeSupport.fire( CtXYMultiSliderModel.EVT_CHANGED );
                layer.repaint();
            }

        });
    }

    void doMouseMoved(MouseEvent e) {
        boolean stateChanged = updateMouseState(e.getX(), e.getY(), true );

        int c;

        if( mouseOverState == MouseOverState.VERTICAL_LINE ) {
            c =   layer.isRotate90()
                ? Cursor.S_RESIZE_CURSOR
                : Cursor.E_RESIZE_CURSOR;
        }
        else
        if( mouseOverState == MouseOverState.HORIZONTAL_LINE ) {
            c =   layer.isRotate90()
                ? Cursor.E_RESIZE_CURSOR
                : Cursor.S_RESIZE_CURSOR;
        }
        else {
            c = Cursor.DEFAULT_CURSOR;
        }

        layer.getParent().setCursor( Cursor.getPredefinedCursor(c) );

        if( stateChanged ) {
            layer.repaint();
        }
    }

    void doMouseDragged(MouseEvent e) {
        isValueChanged = true;
        if( mouseOverState == MouseOverState.NONE ) {
            return;
        }

        // handle the case where lines or thumbs are overlapping with each other
        if( selectedIdx < 0 ) {
            switch( mouseOverState ) {
                case VERTICAL_LINE:
                    if( e.getX() == mousePressedLocation.x ) {
                        break;  // wait for a horizontal drag
                    }
                    boolean findLeftMostLine = e.getX() < mousePressedLocation.x;
                    updateMouseState( mousePressedLocation.x, mousePressedLocation.y, findLeftMostLine );
                    selectedIdx = mouseOverIdx;
                    break;

                case HORIZONTAL_LINE:
                    if( e.getY() == mousePressedLocation.y ) {
                        break;
                    }
                    boolean findTopMostLine = e.getY() < mousePressedLocation.y;
                    updateMouseState( mousePressedLocation.x, mousePressedLocation.y, findTopMostLine );
                    selectedIdx = mouseOverIdx;
                    break;

                default: // dragging on a spot
                    selectedIdx = mouseOverIdx;
            }
        }

        if( selectedIdx >= 0 ) { // need this test since we could be dragging parallel to the line
            double x = fromScreenToModelX( e.getX() );
            double y = fromScreenToModelY( e.getY() );

            switch( mouseOverState ) {
                case VERTICAL_LINE:
                   model.setValue( selectedIdx, x, model.getValueY(selectedIdx) );
                   break;
                case HORIZONTAL_LINE:
                   model.setValue( selectedIdx, model.getValueX(selectedIdx), y );
                   break;
                case SPOT:
                   model.setValue( selectedIdx, x, y );
                   break;
            }
        }
    }

    
    void setupMouseMotionListener() {
        layer.addMouseMotionListener( new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                doMouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                doMouseDragged(e);
            }

        });
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        layer.repaint();
    }

    MouseOverState checkMouseState( int dx, int dy, int idx, boolean fixedX, boolean fixedY ) {

        if( !fixedX || !fixedY ) {
            if(    dx < spotCursorCatchmentSizeX
                && dy < spotCursorCatchmentSizeY ) {
                return MouseOverState.SPOT;
            }
        }
        
        boolean mouseOverVerLine = !fixedX && showVerticalLine   && dx < lineCursorCatchmentSize;
        boolean mouseOverHorLine = !fixedY && showHorizontalLine && dy < lineCursorCatchmentSize;

        if( mouseOverVerLine && mouseOverHorLine ) {
            if( dx < dy ) {
                return MouseOverState.VERTICAL_LINE;
            }
            else {
                return MouseOverState.HORIZONTAL_LINE;
            }
        }
        else if( mouseOverVerLine ) {
            return MouseOverState.VERTICAL_LINE;
        }
        else if( mouseOverHorLine ) {
            return MouseOverState.HORIZONTAL_LINE;
        }
        else {
            return MouseOverState.NONE;
        }
    }

    boolean updateMouseState( int x, int y, boolean findFirst ) {
        MouseOverState old = mouseOverState;
        doUpdateMouseState(x, y, findFirst);
        
        return mouseOverState != old;
    }

    /**
     *
     * @param x
     * @param y
     * @param findFirst
     * @return true if mouse state changed
     */
    void doUpdateMouseState( int x, int y, boolean findFirst ) {
        mouseOverState = MouseOverState.NONE;
        mouseOverIdx = -1;

        if( model.getNumPoints() <= 0 ) {
            return;
        }

        int min = Integer.MAX_VALUE;
        for( int i = 0; i < model.getNumPoints(); ++i ) {
            int dx = Math.abs( x - fromModelToScreenX( model.getValueX(i) ) );
            int dy = Math.abs( y - fromModelToScreenY( model.getValueY(i) ) );

            MouseOverState state = checkMouseState( dx, dy, i, model.isFixedX(i), model.isFixedY(i) );

            int diff;
            switch( state ) {
                case SPOT:
                    diff = Math.min(dx, dy);
                    break;
                case VERTICAL_LINE:
                    diff = dx;
                    break;
                case HORIZONTAL_LINE:
                    diff = dy;
                    break;
                default:
                    continue;
            }

            if( findFirst ) {
                if( diff < min ) {
                    min = diff;
                    mouseOverIdx = i;
                    mouseOverState = state;
                }
            }
            else {
                if( diff <= min ) {
                    min = diff;
                    mouseOverIdx = i;
                    mouseOverState = state;
                }
            }
        }
    }

    int fromModelToScreenX(double x) {
        double f =   ( (double)x - model.getMinX() )
                  / (double)model.getRangeX()
                  * ( (double)layer.getPreTransformWidth() - 1 );

        return (int)Math.round(f);
    }

    int fromModelToScreenY(double y) {
        double f =   ( (double)y - model.getMinY() )
                  / (double)model.getRangeY()
                  * ( (double)layer.getPreTransformHeight() - 1 );

        return layer.getPreTransformHeight() - (int)Math.round(f); // y = 0 is bottom of screen.
    }

    double fromScreenToModelX(int x) {
        return   (double)x
               / ( (double)layer.getPreTransformWidth() - 1 )
               * (double)model.getRangeX()
               + model.getMinX();
    }

    double fromScreenToModelY(int y) {
        return   (double)(layer.getPreTransformHeight()-y)
               / ( (double)layer.getPreTransformHeight() - 1 )
               * (double)model.getRangeY()
               + model.getMinY();
    }

    public void paint(Graphics2D g, CtCanvasLayer l) {
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // draw fixed first
        for( int i = 0; i < model.getNumPoints(); ++i ) {
            if(    model.isFixedX(i)
                || model.isFixedY(i) ) {
                drawLine(g, i);
            }
            if(    model.isFixedX(i)
                && model.isFixedY(i) ) {
                drawSpot(g, i);
            }
        }

        // draw movable
        for( int i = 0; i < model.getNumPoints(); ++i ) {
            if(    !model.isFixedX(i)
                && !model.isFixedY(i) ) {
                drawLine(g, i);
            }
            if(    !model.isFixedX(i)
                || !model.isFixedY(i) ) {
                drawSpot(g, i);
            }
        }
    }

    void drawSpot(Graphics2D g, int idx) {
        int x = fromModelToScreenX( model.getValueX(idx) );
        int y = fromModelToScreenY( model.getValueY(idx) );

        if(    model.isFixedX(idx)
            && model.isFixedY(idx) ) {
            g.setPaint( fixedSpotColor );
        }
        else
        if( idx == mouseOverIdx ) {
            g.setPaint( mouseOverSpotColor );
        }
        else {
            g.setPaint( spotColor );
        }

        Path2D.Float t = new Path2D.Float( hourGlass );
        t.transform( new AffineTransform(1, 0, 0, 1, x, y) );
        g.fill(t);
    }

    void drawLine(Graphics2D g, int idx) {
        g.setStroke(lineStroke);

        int x = fromModelToScreenX( model.getValueX(idx) );
        int y = fromModelToScreenY( model.getValueY(idx) );

        if( showVerticalLine ) {
            if( model.isFixedX(idx) ) {
                g.setPaint( fixedLineColor );
            }
            else {
                g.setPaint( lineColor );
            }
            g.drawLine( x, 0, x, layer.getPreTransformHeight() );
        }
        if( showHorizontalLine ) {
            if( model.isFixedY(idx) ) {
                g.setPaint( fixedLineColor );
            }
            else {
                g.setPaint( lineColor );
            }
            g.drawLine( 0, y, layer.getPreTransformWidth(), y );
        }
    }

}

