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

package au.com.nicta.ct.graphics.canvas.tools;

import au.com.nicta.ct.graphics.canvas.brushes.CtBrush;
import au.com.nicta.ct.ui.swing.util.CtSpinnerQuantisedNumberModel;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.brushes.CtBrushLayer;
import au.com.nicta.ct.graphics.canvas.brushes.CtBrushSet;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Alan
 */
public abstract class CtBrushTool extends CtCanvasTool {

//    public abstract void doApply(MouseEvent e);

    // External passed in
//    CtEditMode editMode;

    // Member
//    CtCursor cursor;
    protected String _brush;
    protected CtBrush _b;
//    protected CtBrushSet _bs;
//    protected CtBrushLayer _bl;
    protected HashSet< CtBrushLayer > _bls = new HashSet< CtBrushLayer >();

    public JSpinner                      brushSizeSpinner;
    public CtSpinnerQuantisedNumberModel brushSizeSpinnerModel;
//    public JPanel _p;

    // states
//    boolean mouseInside = false;

    // Temporaries
    AffineTransform affTemp = new AffineTransform();

    public CtBrushTool( CtToolsModel tm, String name, String brush, CtBrush b ) {//, CtBrushLayer bl, CtCanvasLayer cl ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
//        super(zc, editMode);
        super( tm, name );//, cl );
//        this._zc = zc;
//        this.editMode = editMode;

//        cursor = new CtCursor(zc);
        _brush = brush;
        _b = b;
//        _bs = bs;
//        _bl = bl;
//        _bl._bs.add( _brush, b );
        _tm._bs.add( _brush, b );
//        editMode.changeSupport.addListener(CtEditMode.EVT_ACTIVE, new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                // deactivate tool when edit mode deactivates
//                if(editMode.isActive() == false) {
//                    setActive(false);
//                }
//            }
//        });
//
//        editMode.cursorSizeSpinnerModel.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                cursorSizeChanged(e);
//            }
//        });

//        setupCursorLayer();
//        setBrushLayer( bl );
//        createPanel();
    }

//    protected void createPanel() {
//    }

    @Override public void onCreateCanvasLayer( CtCanvasLayer cl ){
        super.onCreateCanvasLayer( cl );

        if( cl instanceof CtBrushLayer ) {
            _bls.add( (CtBrushLayer)cl );
        }
    } // tells the tool about canvases it may need to respond to, as these are all graphical tools

    @Override public void onDeleteCanvasLayer( CtCanvasLayer cl ){
        super.onDeleteCanvasLayer( cl );

        if( cl instanceof CtBrushLayer ) {
            _bls.remove( (CtBrushLayer)cl );
        }
    }
//
    @Override public JComponent panel() {
        brushSizeSpinnerModel = new CtSpinnerQuantisedNumberModel();
        brushSizeSpinnerModel.setValue( new Double(10.0) );
        brushSizeSpinnerModel.setMinimum( 1.0 );
        brushSizeSpinnerModel.setMaximum( 50.0 );
        brushSizeSpinner = new JSpinner( brushSizeSpinnerModel );
        brushSizeSpinner.setEditor( new JSpinner.NumberEditor( brushSizeSpinner, "0.00" ) );

        brushSizeSpinnerModel.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                brushSizeChanged(e);
            }
        });

        JPanel p = new JPanel( new FlowLayout() );
        p.setOpaque( false );
        JLabel l = new JLabel( "Brush size" );
        p.add( l );
        p.add( brushSizeSpinner );
        return p;
    }
    
    public void brushSizeChanged( ChangeEvent e ) {
        if( _b == null ) {
            return;
        }

        double size = brushSizeSpinnerModel.getNumber().doubleValue();

        _b.resize( size );
//        _bl.repaint();
        for( CtBrushLayer bl : _bls ) {
            bl.repaint();
        }
    }


//    final void setupCursorLayer() {
//    final void setBrushLayer( CtBrushLayer bl ) {
//        bl._cl.addMouseListener( new MouseInputAdapter() {
//            @Override public void mousePressed(MouseEvent e) { CtBrushTool.this.mousePressed(e); }
//        });
//
//        bl._cl.addMouseMotionListener( new MouseInputAdapter() {
//            @Override public void mouseDragged(MouseEvent e) { CtBrushTool.this.mouseDragged(e); }
//        });
//    }

    @Override public void activate() {
//        cursor.setActive(true);
        brushSizeChanged( null );
        _tm._bs.set( _brush );
//        _bl.setActive( true );
//        _bl.repaint();
        for( CtBrushLayer bl : _bls ) {
            bl.setActive( true );
            bl.repaint();
        }
    }

    @Override public void deactivate() {
//        cursor.setActive(false);
//        _bl.setActive( false );
//        _bl.repaint();
        for( CtBrushLayer bl : _bls ) {
            bl.setActive( false );
            bl.repaint();
        }
    }

//    void cursorSizeChanged(ChangeEvent e) {
//        if( editMode.currDetectionModel == null ) {
//            return;
//        }
//
//        double radius = editMode.cursorSizeSpinnerModel.getNumber().doubleValue();
//
//        cursor.cursorPolygon.unitsPerNaturalPixel = editMode.currDetectionModel.detection.detectionPolygon.unitsPerNaturalPixel;
//        cursor.cursorPolygon.polygon = CtRaster.circle(
//                0,
//                0,
//                (int)(radius * cursor.cursorPolygon.unitsPerNaturalPixel),
//                CtRaster.Connectedness.EIGHT );
//
//        cursor.repaint();
//    }

//    public void mouseDragged(MouseEvent e)
//    {
//        if( !isActive() ) {
//            return;
//        }
//
//        // dave: already in BrushLayer handler:
////        cursor.mouseX = e.getX();
////        cursor.mouseY = e.getY();
//        _bl.mouseDragged( e ); // cos event will be consumed
//        apply( e );
//    }
//
//    public void mousePressed(MouseEvent e) {
//        if( !isActive() ) {
//            return;
//        }
//
//        apply( e );
//    }

//    @Override void apply( MouseEvent e ) {
//
//        if( !isActive() ) {
//            return;
//        }
//
//        doApply(e);
//        e.consume();
//        _bl._zc.repaint();
//    }

//    Area getCursorArea() {
//        if( ( _b == null ) || ( _b._p == null ) ) {
//            return null;
//        }
//
//        Area a = new Area( _b._p );
//
//        int x = (int)Math.rint( _bl._zc.toNaturalX(_bl._mouseX) * _b.unitsPerNaturalPixel );
//        int y = (int)Math.rint( _bl._zc.toNaturalY(_bl._mouseY) * _b.unitsPerNaturalPixel );
//
//        affTemp.setToTranslation(x, y);
//        a.transform(affTemp);
//
//        return a;
//    }
}
