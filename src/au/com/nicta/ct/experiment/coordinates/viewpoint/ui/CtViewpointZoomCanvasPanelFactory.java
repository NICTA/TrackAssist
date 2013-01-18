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

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindow;
import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.ui.swing.mdi.CtWindowContentFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointComponents;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 *
 * @author davidjr
 */
public class CtViewpointZoomCanvasPanelFactory implements CtWindowContentFactory {

    public ArrayList< CtAbstractFactory< CtCanvasLayer > > _canvasLayerFactories = new ArrayList< CtAbstractFactory< CtCanvasLayer > >();
    public HashSet< CtZoomCanvasLayerListener > _canvasLayerListeners = new HashSet< CtZoomCanvasLayerListener >();
//    factory< canvas layer > ? to ensure all layers added... the ircl can be one.
//        listener - tool. onlayerAdded..

    public CtViewpointZoomCanvasPanelFactory() {

    }
    
//    Finds windows containing Zoom Canvases by enumerating all dockable windows of
//    a particular type, and assuming if their content panel is a ZoomCanvasPanel
//    then we're interested in them.
    public static Collection< CtZoomCanvasPanel > find( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {

        ArrayList< CtZoomCanvasPanel > al = new ArrayList< CtZoomCanvasPanel >();

        Collection< CtDockableWindow > cdw = dwg.findWindowsOfType( windowTypes ); // e.g. all imaging windows

        for( CtDockableWindow dw : cdw ) {
            JComponent c = dw.getContent();

            if( c instanceof CtZoomCanvasPanel ) {
                al.add( (CtZoomCanvasPanel)c );
            }
        }

        return al;
    }

    public JComponent createComponent() {
        CtZoomCanvasPanel zcp = new CtZoomCanvasPanel();
        CtViewpointZoomCanvas zc = zcp.getZoomCanvas();

        for( CtAbstractFactory< CtCanvasLayer > clf : _canvasLayerFactories ) {
            CtCanvasLayer cl = clf.create();// zc );
            zc.addLayer( cl );
//            cl.setParent( zc );

            for( CtZoomCanvasLayerListener zcll : _canvasLayerListeners ) {
                zcll.onCreateCanvasLayer( cl );
            }
        }

//        CtImageResultsCanvasLayer ircl = new CtImageResultsCanvasLayer( zcp.getZoomCanvas() );
        return zcp;
    }

    public String getWindowType() {
        return "Imaging";
    }

    @Override public void onWindowOpening( CtDockableWindow dw, JComponent c ) {
        try {
            CtZoomCanvasPanel zcp = (CtZoomCanvasPanel)c;
            JToolBar tb = dw.getToolBar();
            CtViewpointComponents.addComponentsTo( zcp._zc._vc, tb );
        }
        catch( ClassCastException cce ) {
            // ignore
        }
    }

    @Override public void onWindowClosing( JComponent c ) {
        try {
            CtZoomCanvasPanel zcp = (CtZoomCanvasPanel)c;
            zcp.stopListening(); // will tell all layers to stop listening too..

            for( CtCanvasLayer cl : zcp.getZoomCanvas().layers ) {
                for( CtZoomCanvasLayerListener zcll : _canvasLayerListeners ) {
                    zcll.onDeleteCanvasLayer( cl );
                }
            }
        }
        catch( ClassCastException cce ) {
            // ignore
        }
    }

}
