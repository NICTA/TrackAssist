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

package au.com.nicta.ct.graphics.canvas.brushes;

import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasPainter;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

/**
 * Paint the current brush onto the (zoom)canvas at current scale
 * 
 * @author davidjr
 */
public class CtBrushLayer extends CtCanvasLayer {

//    public CtZoomCanvas _zc;
//    public CtCanvasLayer _cl;
    public static final String CANVAS_LAYER_NAME = "brush-layer";

// Tools model provides factory:
//    public static void addFactoryTo( CtZoomCanvasPanelFactory zcpf ) {
//        zcpf._canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
//            @Override public CtCanvasLayer create() {
//                return new CtBrushLayer();
//            }
//        } );
//    }

    public CtBrushSet _bs;// = new CtBrushSet();

    public boolean _mouseOnCanvas = false;
    public int _mouseX = 0;
    public int _mouseY = 0;

//    AffineTransform _temp = new AffineTransform();

    public CtBrushLayer( String canvasLayerName, CtBrushSet bs ) {// CtZoomCanvas zc ) throws CtCanvasException {
        super( canvasLayerName ); // maybe more than 1 brush layer
        this._bs = bs;
//        this._zc = zc;
//
//        _cl = new CtCanvasLayer();
//
////        _zc.addLayer( _cl, "BrushLayer" );
//
//        _cl.addMouseListener( new MouseInputAdapter() {
//            @Override public void mouseEntered(MouseEvent e) { CtBrushLayer.this.mouseEntered(e); }
//            @Override public void mouseExited (MouseEvent e) { CtBrushLayer.this.mouseExited(e); }
//        });
//
//        _cl.addMouseMotionListener( new MouseInputAdapter() {
//            @Override public void mouseMoved  (MouseEvent e) { CtBrushLayer.this.mouseMoved(e); }
//            @Override public void mouseDragged(MouseEvent e) { CtBrushLayer.this.mouseDragged(e); }
//        });

        this.addPainter( new CtCanvasPainter() {
            public void paint(Graphics2D g, CtCanvasLayer l) {
                CtBrush b = _bs.get();

                if( b != null ) {
                    b.paint( g, (CtViewpointZoomCanvas)parent, _mouseX, _mouseY );
                }
            }
        });
    }

    @Override public void setParent( CtCanvas c ) {
        super.setParent( c );

        addMouseListener( new MouseInputAdapter() {
            @Override public void mouseEntered(MouseEvent e) { CtBrushLayer.this.mouseEntered(e); }
            @Override public void mouseExited (MouseEvent e) { CtBrushLayer.this.mouseExited(e); }
        });

        addMouseMotionListener( new MouseInputAdapter() {
            @Override public void mouseMoved  (MouseEvent e) { CtBrushLayer.this.mouseMoved(e); }
            @Override public void mouseDragged(MouseEvent e) { CtBrushLayer.this.mouseDragged(e); }
        });
    }

    public void setActive( boolean b ) {
        if( parent == null ) {
            return;
        }

        if( b ) {
            this.setEnabled( true );
            parent.setGlassLayer( this );//_cl );
        }
        else {
            this.setEnabled( false );
            parent.setGlassLayer( null ); //fixes it when off - whenever glas layer on, display  is stuffed
        }
    }

    public void repaint() {
//        _zc.repaint();

        parent.repaintGlassLayerOnly();
//        zc.repaintCursorLayerOnly();
//    public void repaintCursorLayerOnly() {
//        super.repaint(0, 0, 0, getWidth(), getHeight());
//    }
    }

//    void paintCursor(Graphics2D g) {
//        if( !cursorVisible ) {
//            return;
//        }
//        AffineTransform old = g.getTransform();
//
//        g.setStroke(CURSOR_STROKE);
//        g.translate(mouseX, mouseY);
//        g.scale(zc.getZoomScale(), zc.getZoomScale());
//        g.scale( 1.0/cursorPolygon.unitsPerNaturalPixel,
//                 1.0/cursorPolygon.unitsPerNaturalPixel );
//        g.draw(cursorPolygon.polygon);
//
//        g.setTransform(old);
//    }

    public void mouseEntered(MouseEvent e)
    {
        _mouseOnCanvas = true;
        parent.repaintGlassLayerOnly();
    }

    public void mouseExited(MouseEvent e)
    {
        _mouseOnCanvas = false;
        parent.repaintGlassLayerOnly();
    }

    public void mouseMoved(MouseEvent e)
    {
        _mouseX = e.getX();
        _mouseY = e.getY();
        parent.repaintGlassLayerOnly();
    }

    public void mouseDragged(MouseEvent e)
    {
        _mouseX = e.getX();
        _mouseY = e.getY();
        parent.repaintGlassLayerOnly();
    }

}
