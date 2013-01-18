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

package au.com.nicta.ct.experiment.coordinates.viewpoint.ui;

import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasException;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A model of many canvas views, each with its own layer.. the layer also listens
 * to changes in the canvas viewpoint, as this may impact painting.
 * 
 * Specific layers can be found by searching in the ZoomCanvas. (getLayer( String))
 * 
 * Addition of layers depends on what type of canvas it is (e.g. lineage doesn't 
 * want same layers as imaging view). 
 *
 * But how to dispose of layers after the canvas is gone?
 * @author davidjr
 */
public abstract class CtViewpointCanvasLayer extends CtCanvasLayer implements CtChangeListener {//CtCanvasPainter, CtChangeListener {

//    protected String _viewName;
    protected CtViewpointZoomCanvas _zc; // only a ZC cos we need the viewpoint therein
//    protected CtCanvasLayer _cl;

    public CtViewpointCanvasLayer( String viewName ) {
        super( viewName );
//        _viewName = viewName;
//        setZoomCanvas( zc );
//        setParent( zc );
    }

// don't need to detach, as am only listening to the viewpoint..
//    @Override public void stopListening() {
//        // detach listeners..
//    }

    @Override public void setParent( CtCanvas c ) {
        if( !( c instanceof CtViewpointZoomCanvas ) ) {
            throw new ClassCastException( "ERROR: Can only add Viewpoint canvas layers to ZoomCanvas objects." );
        }

        _zc = (CtViewpointZoomCanvas)c;
        _zc.getViewpointController().addListener( this );
        
        super.setParent( c );
    }

//    public void setZoomCanvas( CtZoomCanvas zc ) {
//
//        if( _zc != null ) {
//            _zc.detachLayer( _cl.name );//_viewName );
//        }
//
//        _zc = zc;
//
//        try {
//            _cl = null;
//            _cl = new CtCanvasLayer();
//            _zc.addLayer( _cl, _cl.name );//_viewName );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }
//
//        _cl.addPainter( this );
//    }
//
//    public CtCanvasLayer getCanvasLayer() {
//        return _cl;
//    }

    @Override public void propertyChange( PropertyChangeEvent evt ) { // ie viewpoint changed
        repaint();
    }

//    public void repaint() {
////        _zc.repaint(); // assume some model change, repaint everything
//        _cl.repaint();
//    }

}
