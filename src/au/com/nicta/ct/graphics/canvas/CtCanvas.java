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

package au.com.nicta.ct.graphics.canvas;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JPanel;


/**
 *
 * For graphics overlays
 *
 * @author Alan
 */
public class CtCanvas extends JPanel {

    public List<CtCanvasLayer> layers = new ArrayList<CtCanvasLayer>();
    public Set<String> layerNames = new TreeSet<String>();
    public CtCanvasLayer glassLayer;
    
    protected boolean repaintAllLayers;
    protected BufferedImage snapshot;
    protected Graphics2D snapshotG2D;

    protected boolean flipLeftRight = false;
    protected boolean flipUpDown = false;
    protected boolean rotate90 = false;
    protected AffineTransform aff = new AffineTransform();
    protected Point pt = new Point();

    void fillAffine( AffineTransform aff ) {

        // remember last added is applied first:
        // aff.xxx(tx)  <==>  aff = aff * Tx

        aff.setToIdentity();

        if( flipUpDown ) {
            aff.translate( 0, getHeight() );
            aff.scale(1, -1);
        }

        if( flipLeftRight ) {
            aff.translate( getWidth(), 0 );
            aff.scale(-1, 1);
        }

        if( rotate90 ) {
            aff.translate( getWidth(), 0 );
            aff.rotate( 0, 1 ); // rotate 90 degrees
        }
    }

    public void setFlipLeftRight( boolean b ) {
        flipLeftRight = b;
    }

    public void setFlipUpDown( boolean b ) {
        flipUpDown = b;
    }

    public boolean isFlipLeftRight() {
        return flipLeftRight;
    }

    public boolean isFlipUpDown() {
        return flipUpDown;
    }

    public void setRotate90( boolean b ) {
        rotate90 = b;
    }

    public boolean isRotate90() {
        return rotate90;
    }


    public CtCanvas() {
        // nothing
    }

    
    public CtCanvasLayer createLayer( String name ) throws CtCanvasException {
        CtCanvasLayer l = new CtCanvasLayer();
        addLayer( l, name );
        return l;
    }

    public CtCanvasLayer createLayer() {
        CtCanvasLayer l = new CtCanvasLayer();
        addLayer( l );
        return l;
    }

    public CtCanvasLayer detachLayer( String name ) {
        CtCanvasLayer detached = null;

        for( CtCanvasLayer cl : layers ) {
            if( cl.name.equals( name ) ) {
                layers.remove( cl );
                break;
            }
        }
        layerNames.remove( name );

        return detached;
    }

    public CtCanvasLayer getLayer( String name ) {

        for( CtCanvasLayer cl : layers ) {
            if( cl.name.equals( name ) ) {
                return cl;
            }
        }

        return null;
    }

    /**
     * Anonymous layer, no naming conflict possible.
     * @param l
     */
    public void addLayer( CtCanvasLayer l ) {
//        l.name = null;
//        addLayerImpl( l );
        addLayer( l, l.name );
    }

    public void addLayer( CtCanvasLayer l, String name ) throws CtCanvasException {
        if( layerNames.contains(name) ) {
            throw new CtCanvasException("Duplicate layer name: " + l.getName());
        }
        l.name = name;
        layerNames.add(name);
        addLayerImpl( l );
    }

    void addLayerImpl( CtCanvasLayer l ) {
        l.setParent(this);
        layers.add(l);
    }

    public void clearLayers() {
        layerNames.clear();
        layers    .clear();
    }


    public void setGlassLayer(CtCanvasLayer glassLayer) {
        this.glassLayer = glassLayer;
        repaintAllLayers = true;
    }

    public int getPreTransformWidth() {
        return   rotate90
               ? getHeight()
               : getWidth();
    }

    public int getPreTransformHeight() {
        return   rotate90
               ? getWidth()
               : getHeight();
    }

    @Override
    protected void paintComponent(Graphics g1) {
//        super.paintComponent(g1);

        Graphics2D g = (Graphics2D) g1;

        AffineTransform old = g.getTransform();
        fillAffine(aff);
        g.transform(aff);

        paintComponent2(g);
        
        g.setTransform(old);

        repaintAllLayers = false;
    }


    protected void paintComponent2(Graphics2D g) {
        // DAVE: Keep this here cos it tells you how many repaints are generated
        System.out.println( "repaint"+Math.random() );

        // normal functionality
//        if( glassLayer == null ) {
            System.out.println( "no glass layer, normal repaint" );
            paintLayers(g);
            if( glassLayer != null ) {
                if( glassLayer.isEnabled() ) {// use this to hide/show layers, leave them permanently added. Add a layer toggle tool ..
                    glassLayer.paint(g);
                }                
            }
            return;
//        }
/*
        // buffered functionality:
        if(    snapshot == null
            || snapshot.getWidth()  != getWidth()
            || snapshot.getHeight() != getHeight() ) {
System.out.println( "imgw="+getWidth()+" imgh="+getHeight());
            snapshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            snapshotG2D = snapshot.createGraphics();
            System.out.println("Snapshot resized");
            repaintAllLayers = true; // since the snapshot is now invalid
        }

        // either set somewhere, or true cos snapshot size changed:
        if( repaintAllLayers == true ) { // then only paint the mouse layer
            // Buffer the layers below the mouse layer
//super.paintComponent( g );
            paintLayers(snapshotG2D);
            System.out.println("Redrawing all layers");
        }
        else {
            super.paintComponent( g );
        }

       // paint the snapshot we've buffered:
        g.drawImage( snapshot, 0, 0, null );
        glassLayer.paint(g);*/
    }

    protected void paintLayers(Graphics2D g) {
        super.paintComponent(g);

//        paintCnt++;
//        System.out.println("paintCnt: " + paintCnt);

        for( CtCanvasLayer l : layers ) {
            if( !l.isEnabled() ) {// use this to hide/show layers, leave them permanently added. Add a layer toggle tool ..
                continue;
            }
            l.paint(g);
            System.out.println("Painted: " + l.getName() );
        }
    }

    public boolean isCoordinateTranformed() {
        return    flipLeftRight
               || flipUpDown
               || rotate90;
    }

    void doTransformMouseEvent(MouseEvent e, Point p) {
        fillAffine(aff);
        try {
            aff.inverseTransform( e.getPoint(), p );
        }
        catch( NoninvertibleTransformException ex ) {
            System.err.println(
                      "Should not be possible since we construct the forward transformation."
                    + "Probably a case of numerical error." );
            ex.printStackTrace();
        }
    }

    protected MouseEvent transformMouseEvent(MouseEvent e) {
        if( !isCoordinateTranformed() ) {
            return e;
        }

        doTransformMouseEvent(e, pt);

        return new MouseEvent(
                (Component)e.getSource(),
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                (int)pt.getX(), // points after transform
                (int)pt.getY(), // points after transform
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton() );
    }

    protected MouseWheelEvent transformMouseWheelEvent(MouseWheelEvent e) {
        if( !isCoordinateTranformed() ) {
            return e;
        }

        doTransformMouseEvent(e, pt);

        return new MouseWheelEvent(
                (Component)e.getSource(),
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                (int)pt.getX(), // points after transform
                (int)pt.getY(), // points after transform
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getScrollType(),
                e.getScrollAmount(),
                e.getWheelRotation() );
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        MouseEvent e2 = transformMouseEvent(e);
        processMouseEventForLayers( e2 );
        if( e2.isConsumed() ) {
            e.consume();
        }
        else {
            // handle the untransformed event
            super.processMouseEvent(e);
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e)
    {
        MouseEvent e2 = transformMouseEvent(e);
        processMouseMotionEventForLayers( e2 );
        if( e2.isConsumed() ) {
            e.consume();
        }
        else {
            // handle the untransformed event
            super.processMouseMotionEvent(e);
        }
    }
    
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e)
    {
        MouseWheelEvent e2 = transformMouseWheelEvent(e);
        processMouseWheelEventForLayers(e2);
        if( e2.isConsumed() ) {
            e.consume();
        }
        else {
            // handle the untransformed event
            super.processMouseWheelEvent(e);
        }
    }


    protected void processMouseEventForLayers(MouseEvent e) {
        if(    glassLayer != null
            && glassLayer.isEnabled() ) {
            glassLayer.handleMouseEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }

        ListIterator<CtCanvasLayer> i = layers.listIterator(layers.size());
        while( i.hasPrevious() ) {
            CtCanvasLayer l = i.previous();
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }
        super.processMouseEvent(e); //?Alan: should not call this here
    }

    protected void processMouseMotionEventForLayers(MouseEvent e) {
        if(    glassLayer != null
            && glassLayer.isEnabled() ) {
            glassLayer.handleMouseMotionEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }

        ListIterator<CtCanvasLayer> i = layers.listIterator(layers.size());
        while( i.hasPrevious() ) {
            CtCanvasLayer l = i.previous();
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseMotionEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }
    }

    protected void processMouseWheelEventForLayers(MouseWheelEvent e) {
        if(    glassLayer != null
            && glassLayer.isEnabled() ) {
            glassLayer.handleMouseWheelEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }

        ListIterator<CtCanvasLayer> i = layers.listIterator(layers.size());
        while( i.hasPrevious() ) {
            CtCanvasLayer l = i.previous();
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseWheelEvent( e );
            if( e.isConsumed() ) {
                return;
            }
        }
    }

    
    void enableEvent2( long eventMask ) {
        enableEvents( eventMask );
    }

    public void repaintGlassLayerOnly() {
        super.repaint(0, 0, 0, getWidth(), getHeight());
// DAVE: Added
//        repaintAllLayers = true;
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height)
    {
        repaintAllLayers = true;
        super.repaint(tm, x, y, width, height);
    }

}
















