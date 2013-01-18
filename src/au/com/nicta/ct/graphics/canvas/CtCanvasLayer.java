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

import au.com.nicta.ct.ui.swing.util.CtTransientListener;
import java.awt.AWTEvent;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 *
 * Implement to get a callback on a layer repaint event.
 *
 * @author Alan
 */
public class CtCanvasLayer implements CtTransientListener {

    protected CtCanvas parent;
    protected String name;
    protected boolean enabled = true;
    public List<CtCanvasPainter> painters = new ArrayList<CtCanvasPainter>();
    public List<MouseListener>       mouseListeners       = new ArrayList<MouseListener>();
    public List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    public List<MouseWheelListener>  mouseWheelListeners  = new ArrayList<MouseWheelListener>();

    // these layers are displayed on top of current layer. Allowes for
    // groups of layers
    List<CtCanvasLayer> layers = new ArrayList<CtCanvasLayer>();

    public CtCanvasLayer() {
        this( null );
    }

    public CtCanvasLayer( String name )
    {
        this.name = name;
    }

    @Override public void stopListening() {}

    public void addLayer( CtCanvasLayer l ) {
        layers.add(l);
        l.setParent(parent);
    }

    public void setEnabled(boolean b) {
        enabled = b;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setParent(CtCanvas parent) {
        this.parent = parent;
//        parent.addLayer( this, this.name );
        enableEvents();
    }

    public CtCanvas getParent() {
        return parent;
    }

    public int getWidth() {
        return parent.getWidth();
    }

    public int getHeight() {
        return parent.getHeight();
    }

    public int getPreTransformWidth() {
        return parent.getPreTransformWidth();
    }

    public int getPreTransformHeight() {
        return parent.getPreTransformHeight();
    }

    public boolean isRotate90() {
        return parent.isRotate90();
    }

    public boolean isFlipLeftRight() {
        return parent.isFlipLeftRight();
    }

    public boolean isFlipUpDown() {
        return parent.isFlipUpDown();
    }

    public void addMouseListener(MouseListener m) {
        addListener(mouseListeners, m);
    }
    public void addMouseMotionListener(MouseMotionListener m) {
        addListener(mouseMotionListeners, m);
    }

    public void addMouseWheelListener(MouseWheelListener m) {
        addListener(mouseWheelListeners, m);
    }

    <T extends EventListener>
    void addListener(List<T> listeners, T m) {
        listeners.add(m);
        enableEvents();
    }

    void enableEvents() {
        if( parent == null ) {
            return;
        }
        if( !mouseListeners.isEmpty()       ) {
            parent.enableEvent2( AWTEvent.MOUSE_EVENT_MASK );
        }
        if( !mouseMotionListeners.isEmpty() ) {
            parent.enableEvent2( AWTEvent.MOUSE_MOTION_EVENT_MASK );
        }
        if( !mouseWheelListeners.isEmpty()  ) {
            parent.enableEvent2( AWTEvent.MOUSE_WHEEL_EVENT_MASK );
        }
    }
///*
    public void clearListeners() {
        mouseListeners.clear();
        mouseMotionListeners.clear();
        mouseWheelListeners.clear();
    }

    public void clearPainters() {
        painters.clear();
    }//*/

    public void addPainter(CtCanvasPainter painter) {
        painters.add(painter);
    }

    public String getName() {
        return name;
    }

    public void repaint() {
        //TODO For now, just calls repaint of the parent. We may want to buffer and
        // repaint only the current layer, just like the way the cursor layer
        // is handled.
        if( parent != null ) {
            parent.repaint();
        }
    }
    
    public void paint(Graphics2D g) {
        for( CtCanvasPainter p : painters ) {
            p.paint(g, this);
        }
        for( CtCanvasLayer l : layers ) { // last added layer painted on top
            if( l.isEnabled() ) {
                l.paint(g);
            }
        }
    }

    public void handleMouseEvent(MouseEvent e)
    {
        // since last added layers are on top, we need to call mouse
        // handlers in reverse order
        for( int i = layers.size()-1; i >= 0; --i ) {
            CtCanvasLayer l = layers.get(i);
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseEvent(e);
            if( e.isConsumed() ) {
                return;
            }
        }

        for( MouseListener i : mouseListeners ) {
            handleMouseEvent(e, i);
        }
    }

    void handleMouseEvent(MouseEvent e, MouseListener m)
    {
        int id = e.getID();
        switch( id ) {
            case (MouseEvent.MOUSE_CLICKED ): m.mouseClicked   (e); break;
            case (MouseEvent.MOUSE_ENTERED ): m.mouseEntered   (e); break;
            case (MouseEvent.MOUSE_EXITED  ): m.mouseExited    (e); break;
            case (MouseEvent.MOUSE_PRESSED ): m.mousePressed   (e); break;
            case (MouseEvent.MOUSE_RELEASED): m.mouseReleased  (e); break;
        }
    }

    public void handleMouseMotionEvent(MouseEvent e)
    {
        // since last added layers are on top, we need to call mouse
        // handlers in reverse order
        for( int i = layers.size()-1; i >= 0; --i ) {
            CtCanvasLayer l = layers.get(i);
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseMotionEvent(e);
            if( e.isConsumed() ) {
                return;
            }
        }

        for( MouseMotionListener i : mouseMotionListeners ) {
            handleMouseMotionEvent(e, i);
        }
    }

    void handleMouseMotionEvent(MouseEvent e, MouseMotionListener m)
    {
        int id = e.getID();
        switch( id ) {
              case (MouseEvent.MOUSE_MOVED   ): m.mouseMoved  (e); break;
              case (MouseEvent.MOUSE_DRAGGED ): m.mouseDragged(e); break;
        }
    }

    public void handleMouseWheelEvent(MouseWheelEvent e)
    {
        // since last added layers are on top, we need to call mouse
        // handlers in reverse order
        for( int i = layers.size()-1; i >= 0; --i ) {
            CtCanvasLayer l = layers.get(i);
            if( !l.isEnabled() ) {
                continue;
            }
            l.handleMouseWheelEvent(e);
            if( e.isConsumed() ) {
                return;
            }
        }

        for( MouseWheelListener i : mouseWheelListeners ) {
            i.mouseWheelMoved(e);
        }
    }
}


