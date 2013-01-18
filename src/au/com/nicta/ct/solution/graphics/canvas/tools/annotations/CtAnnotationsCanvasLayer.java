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


import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Alan
 */
public class CtAnnotationsCanvasLayer extends CtViewpointCanvasLayer { //implements CtCanvasPainter {

    public static final String CANVAS_LAYER_NAME = "annotations-canvas-layer";
//    private Point2D.Double point = new Point2D.Double();
//    protected CtAnnotationsModel annotationModel;
//    protected CtImageSequenceModel imgSeqModel;

    public static void addFactoryTo( CtViewpointZoomCanvasPanelFactory zcpf ) {
        zcpf._canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtAnnotationsCanvasLayer();
            }
        } );
    }

    protected HashMap< CtAnnotations, Rectangle2D > bounds = new HashMap< CtAnnotations, Rectangle2D >();

//    CtZoomCanvas zc;

    protected boolean showOnAllChannels = true;

    public CtAnnotationsCanvasLayer() {// CtZoomCanvas zc) {
        super( CANVAS_LAYER_NAME );
//        this.zc = zc;
        CtAnnotationsController.get().getAnnotationsModel().addModelChangeListener( this );

        new CtAnnotationsMouseListener( this );
    }

//    public CtAnnotationsCanvasLayer( CtAnnotationsModel annotationModel, CtImageSequenceModel imgSeqModel ) {
//        this.annotationModel = annotationModel;
//        this.imgSeqModel = imgSeqModel;
//    }

    @Override public void stopListening() {
        super.stopListening();

        CtAnnotationsController.get().getAnnotationsModel().removeListener( this );
    }

    protected void setShowOnAllChannels( boolean b ) {
        showOnAllChannels = b;
    }

    public void centreAnnonations( Set< CtAnnotations > setA ) {
        assert !setA.isEmpty();

        Rectangle2D.Double bb = null;

        if( setA.size() == 1 ) {
            CtAnnotations a = setA.iterator().next();
            double w = _zc.toNaturalX(_zc.getSize().width ) - _zc.toNaturalX(0);
            double h = _zc.toNaturalY(_zc.getSize().height) - _zc.toNaturalY(0);
            double cx = a.getX();
            double cy = a.getY();
            bb = new Rectangle2D.Double(cx-w/2, cy-h/2, w, h);
        }
        else {
            // zoom the the bounding box of all annotations
            double l = Double.POSITIVE_INFINITY;
            double r = Double.NEGATIVE_INFINITY;
            double t = Double.POSITIVE_INFINITY;
            double b = Double.NEGATIVE_INFINITY;
            for( CtAnnotations a : setA ) {
                l = Math.min(l, a.getX());
                r = Math.max(r, a.getX());
                t = Math.min(t, a.getY());
                b = Math.max(b, a.getY());
            }
            bb = new Rectangle2D.Double( l, t, r-l, b-t );

            // give some buffer
//            final double EXPAND_FACTOR = 2;
//            bb.x -= bb.width  * (1-EXPAND_FACTOR) / 2;
//            bb.y -= bb.height * (1-EXPAND_FACTOR) / 2;
//            bb.width  *= EXPAND_FACTOR;
//            bb.height *= EXPAND_FACTOR;
        }

        _zc.zoomNaturalWindow( bb ); // calls viewpoint methods etc.
//        _zc.repaint();
    }

//    public void setAnnotationModel( CtAnnotationsModel annotationModel ) {
//        this.annotationModel = annotationModel;
//    }
//
//    public void setImageSequenceModel( CtImageSequenceModel imgSeqModel ) {
//        this.imgSeqModel = imgSeqModel;
//    }
    
//    public void paint( Graphics2D g, CtCanvasLayer cl ) {
    @Override public void paint( Graphics2D g ) {//, CtCanvasLayer cl ) {

        clearBounds(); // cos zoom/pan may have changed

        CtAnnotationsController ac = CtAnnotationsController.get();//dc;

        if( ac == null ) {
            return;
        }

        CtAnnotationsModel am = ac.getAnnotationsModel();

        if( am == null ) {
            return;
        }

//        if( annotationModel == null ) {
//            return;
//        }
//
//        if( imgSeqModel == null ) {
//            return;
//        }
//
//        CtImages i = imgSeqModel.current();
//
//        if( i == null ) {
//            return;
//        }

        CtViewpointController vc = _zc.getViewpointController();
        CtImages i = vc.getViewpointModel().getImage();
//        int currentIndex = vc.getTimeOrdinate();

        if( i == null ) {
            return;
        }

        Set< CtAnnotations > s =
                  showOnAllChannels
                ? am.getByImageTime( i )
                : am.get( i );

        if( s == null ) {
            return;
        }

        for( CtAnnotations a : s ) {
            paint( g, a );
        }
        
    }

    public Rectangle2D getBounds( CtAnnotations a ) {
        return bounds.get( a );
    }

    public void setBounds( CtAnnotations a, Rectangle2D r ) {
        bounds.put( a, r );
    }

    public void clearBounds() {
        bounds.clear();
    }
    
    public Rectangle2D removeBounds( CtAnnotations a ) {
        return bounds.remove( a );
    }
    
    public Rectangle2D stringBounds( CtAnnotations a, Graphics2D g ) {
        String s = a.getValue();
        FontRenderContext frc = g.getFontRenderContext();
        return g.getFont().getStringBounds( s, frc );
    }

    public Rectangle2D boxBounds( Rectangle2D stringBounds, int x, int y ) {
        int border = 10;
        double h = stringBounds.getHeight();
        Rectangle2D boxBounds = new Rectangle2D.Double( x-border, y-(int)(h*1.1), (int)stringBounds.getWidth()+(2*border), (int)h+(border) );

        return boxBounds;
    }

    public Rectangle2D findBoxBounds( CtAnnotations a, Graphics2D g, int x, int y ) {
        Rectangle2D boxBounds = getBounds( a );

        if( boxBounds != null ) {
            return boxBounds;
        }

        Rectangle2D stringBounds = stringBounds( a, g );

        boxBounds = boxBounds( stringBounds, x, y );

        setBounds( a, boxBounds );

        return boxBounds;
    }

    public CtAnnotations findAnnotationAt( int x, int y ) {

        Set< Entry< CtAnnotations, Rectangle2D > > es = bounds.entrySet();

        Iterator i = es.iterator();
        
        while( i.hasNext() ) {

            Entry< CtAnnotations, Rectangle2D > entry = (Entry< CtAnnotations, Rectangle2D >)i.next();

            Rectangle2D bounds = entry.getValue();
            
            if( bounds.contains( x, y ) ) {
                CtAnnotations a = entry.getKey();
                return a;
            }
        }

        return null;
    }

    public void paint( Graphics2D g, CtAnnotations a ) {

        String s = a.getValue();
//        String[] words = sentence.split(" ");
        double xi = a.getX();
        double yi = a.getY();

        int x = (int)_zc.toScreenX( xi );
        int y = (int)_zc.toScreenY( yi );

//        FontRenderContext frc = g.getFontRenderContext();
//        Rectangle2D bounds = g.getFont().getStringBounds( s, frc );
        Rectangle2D b = findBoxBounds( a, g, x, y );

        g.setColor( CtStyle.BRIGHT_GREEN_TRANSLUCENT );
//        g.fillRect( x-border, y-border, (int)bounds.getWidth()+(2*border), (int)bounds.getHeight()+(border) );
        g.fillRect( (int)b.getX(), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight() );
        
        Stroke st = g.getStroke();
        g.setStroke( CtStyle.THICK_STROKE );
        g.setColor( CtStyle.BRIGHT_GREEN );
//        g.drawRect( x-border, y-border, (int)bounds.getWidth()+(2*border), (int)bounds.getHeight()+(border) );
        g.drawRect( (int)b.getX(), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight() );
        g.setColor( Color.WHITE );//CtConstants.NictaPurple );
//        int yOffset = (int)( bounds.getHeight() * 0.5 );
        g.drawString( s, x, y );//+yOffset );
        g.setStroke( st );

//        float width = (float) bounds.getWidth();
//
//        g.setColor( CtConstants.NictaYellow );
//        g.setStroke( new BasicStroke( 3 ) );
//        g.drawString( sentence, 50,50 );
    }

}
