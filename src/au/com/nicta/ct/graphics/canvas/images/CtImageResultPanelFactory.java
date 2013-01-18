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

package au.com.nicta.ct.graphics.canvas.images;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindow;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.graphics.canvas.images.toggle.CtComponentVisibilityToggle;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 *
 * @author davidjr
 */
public class CtImageResultPanelFactory extends CtViewpointZoomCanvasPanelFactory {

    public ArrayList< CtImageResultToggleFactory > _imageResultToggleFactories = new ArrayList< CtImageResultToggleFactory >();

    public CtImageResultPanelFactory() {
        super();

        _canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtImageResultsCanvasLayer();
            }
        } );
    }

    public static CtImageResultsCanvasLayer getImageResultsCanvasLayer( CtViewpointZoomCanvas zc ) {
        return (CtImageResultsCanvasLayer)zc.getLayer( CtImageResultsCanvasLayer.CANVAS_LAYER_NAME );
    }

//    @Override public JComponent createComponent() {
//        JComponent c = super.createComponent();
//        CtZoomCanvasPanel zcp = (CtZoomCanvasPanel)c;
//
//        for( CtImageResultToggleFactory irtf : _imageResultToggleFactories ) {
//            CtComponentVisibilityToggle cvt = irtf.createImageResultToggle( this, zcp );
//
//            addImageResultToggle( cvt, zcp );
//        }
//
//        return c;
//    }

    @Override public void onWindowOpening( CtDockableWindow dw, JComponent c ) {
        super.onWindowOpening( dw, c );
        try {
            CtZoomCanvasPanel zcp = (CtZoomCanvasPanel)c;

            for( CtImageResultToggleFactory irtf : _imageResultToggleFactories ) {
                CtComponentVisibilityToggle cvt = irtf.createImageResultToggle( this, zcp );

                addImageResultToggleTo( cvt, dw );
            }
        }
        catch( ClassCastException cce ) {
            // ignore
        }
    }

    protected void addImageResultToggleTo( CtComponentVisibilityToggle cvt, CtDockableWindow dw ) {
        JToolBar tb = dw.getToolBar();
        tb.add( cvt.createToggleButton() );

        JComponent controls = cvt.getComponent();

        if( controls == null ) {
            return;
        }

        String edge = cvt.getEdge(); //getToolBarStackSide();
        String title = cvt.getName();//getControlsTitle();

        CtToolBarStack tbs = dw.getToolBarStack( edge );
        tbs.addTool( controls, title );

        controls.setVisible( cvt.getSelected() );
    }
    
//    protected void addImageResultToggle( CtComponentVisibilityToggle cvt, CtZoomCanvasPanel zcp ) {
//        JToolBar tb = zcp.getToolBar();
//        tb.add( cvt.createToggleButton() );
////        cvt.addToggleButtonTo( tb, false );
//
//        JComponent controls = cvt.getComponent();
//
//        if( controls == null ) {
//            return;
//        }
//
//        String edge = cvt.getEdge(); //getToolBarStackSide();
//        String title = cvt.getName();//getControlsTitle();
//
//        CtToolBarStack tbs = zcp.getToolBarStack( edge );
//        tbs.addTool( controls, title );
//    }

}
