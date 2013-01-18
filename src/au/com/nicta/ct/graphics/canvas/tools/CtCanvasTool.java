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

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import java.awt.event.MouseEvent;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.event.MouseInputAdapter;

/**
 * Tool that involves mouse interaction with a zoom-canvas. Since there are many
 * windows with canvases, which canvas in which window?? The answer is each tool
 * operates in all windows, IFF the relevant layers are present. So this means
 * that tools must be dynamically added / removed to various layers as the
 * windows are created/destroyed, so that events can be intercepted.
 * 
 * or: have a single tool registry (tool model) that is listening to all layers,
 *      and relays commands to tools that are active in that layer?
 *
 * OR: What if since the window is destroyed, no events occur. So just need to
 * add to every window, on window creation. Reference to listener (this) is held
 * in class (canvas) that will be deleted, as long as this doesn't have a ref. to the
 * canvas.
 *
 * OK this tool gets notification of every window's canvases and every layer in
 * them. Then it has the option to add listeners to specific events to named
 * layers. e.g. just listen to DRAG events on the IMAGEING layer. Or CLICK events
 * in the DETECTIONS layer.
 * @author davidjr
 */

public abstract class CtCanvasTool extends CtTool {

    public static final int APPLY_ON_MOUSE_CLICKED  = 0;
    public static final int APPLY_ON_MOUSE_MOVED    = 1;
    public static final int APPLY_ON_MOUSE_PRESSED  = 2;
    public static final int APPLY_ON_MOUSE_RELEASED = 3;
    public static final int APPLY_ON_MOUSE_DRAGGED  = 4;

    protected HashMap< String, HashSet< Integer > > _namedCanvasLayerMouseActions = new HashMap< String, HashSet< Integer > >();
//    CtZoomCanvas _zc;
//    protected CtCanvasLayer _cl;

    public abstract boolean doApply( MouseEvent e, CtCanvasLayer cl );

    public CtCanvasTool( CtToolsModel tm, String name ) {//, CtCanvasLayer cl ) {

        super( tm, name );

//        this._zc = cl._zc;
//        this._cl = cl;
    }

    @Override public void onCreateCanvasLayer( final CtCanvasLayer cl ){
        String name = cl.getName();
//        Integer mouseAction = _namedCanvasLayerMouseActions.get( name );
        HashSet< Integer > mouseActions = _namedCanvasLayerMouseActions.get( name );

        if( mouseActions != null ) {
//           switch( mouseAction ) {
            if( mouseActions.contains( APPLY_ON_MOUSE_CLICKED ) ) {
                   cl.addMouseListener( new MouseInputAdapter() {
                        @Override public void mouseClicked(MouseEvent e) { CtCanvasTool.this.mouseClicked( e, cl ); }
                   });
            }
            if( mouseActions.contains( APPLY_ON_MOUSE_MOVED ) ) {
                   cl.addMouseListener( new MouseInputAdapter() {
                        @Override public void mouseMoved(MouseEvent e) { CtCanvasTool.this.mouseMoved( e, cl ); }
                   });
            }
            if( mouseActions.contains( APPLY_ON_MOUSE_PRESSED ) ) {
                   cl.addMouseListener( new MouseInputAdapter() {
                        @Override public void mousePressed(MouseEvent e) { CtCanvasTool.this.mousePressed( e, cl ); }
                   });
            }
            if( mouseActions.contains( APPLY_ON_MOUSE_RELEASED ) ) {
                   cl.addMouseListener( new MouseInputAdapter() {
                        @Override public void mouseReleased(MouseEvent e) { CtCanvasTool.this.mouseReleased( e, cl ); }
                   });
            }
            if( mouseActions.contains( APPLY_ON_MOUSE_DRAGGED ) ) {
                   cl.addMouseMotionListener( new MouseInputAdapter() {
                        @Override public void mouseDragged(MouseEvent e) { CtCanvasTool.this.mouseDragged( e, cl ); }
                   });
            }
        }
    } // tells the tool about canvases it may need to respond to, as these are all graphical tools

    protected void addMouseAction( String canvasLayerName, int mouseAction ) {
        HashSet< Integer > hs = _namedCanvasLayerMouseActions.get( canvasLayerName );
        if( hs == null ) {
            hs = new HashSet< Integer >();
            _namedCanvasLayerMouseActions.put( canvasLayerName, hs );
        }

        hs.add( mouseAction );
    }
    
    protected void applyOnMouseClicked( String canvasLayerName ) {
        addMouseAction( canvasLayerName, APPLY_ON_MOUSE_CLICKED );
    }

    protected void applyOnMouseMoved( String canvasLayerName ) {
        addMouseAction( canvasLayerName, APPLY_ON_MOUSE_MOVED );
    }

    protected void applyOnMousePressed( String canvasLayerName ) {
        addMouseAction( canvasLayerName, APPLY_ON_MOUSE_PRESSED );
    }

    protected void applyOnMouseReleased( String canvasLayerName ) {
        addMouseAction( canvasLayerName, APPLY_ON_MOUSE_RELEASED );
    }

    protected void applyOnMouseDragged( String canvasLayerName ) {
        addMouseAction( canvasLayerName, APPLY_ON_MOUSE_DRAGGED );
    }

    public void mouseClicked(MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }
        apply( e, cl );
    }

    public void mouseMoved(MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }
        apply( e, cl );
    }

    public void mousePressed(MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }
        apply( e, cl );
    }

    public void mouseReleased(MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }
        apply( e, cl );
    }

    public void mouseDragged(MouseEvent e, CtCanvasLayer cl ) {
        if( !isActive() ) {
            return;
        }
        apply( e, cl );
    }

    protected void apply( MouseEvent e, CtCanvasLayer cl ) {

        if( !isActive() ) {
            return;
        }

        if( doApply( e, cl ) ) {
            e.consume();
        }
//        _zc.repaint();
    }

}
