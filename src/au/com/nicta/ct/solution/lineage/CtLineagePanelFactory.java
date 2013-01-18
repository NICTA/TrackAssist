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

package au.com.nicta.ct.solution.lineage;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.util.Collection;
import javax.swing.JComponent;

/**
 * Uses a zoom canvas to allow the user to browse a lineage tree.
 *
 * Is default locked in Pan/Zoom because it shouldn't be related to imagery
 * windows.
 *
 * @author davidjr
 */
public class CtLineagePanelFactory extends CtViewpointZoomCanvasPanelFactory {

    CtDockableWindowGrid _dwg;
    Collection< String > _windowTypes;

    public CtLineagePanelFactory( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {
        super();

        _dwg = dwg;
        _windowTypes = windowTypes;

        _canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtLineageCanvasLayer( _dwg, _windowTypes );
            }
        } );
    }

    @Override public JComponent createComponent() {
        CtZoomCanvasPanel zcp = (CtZoomCanvasPanel)super.createComponent();
        CtViewpointController vc = zcp.getViewpointController();

        vc.setPanZoomLock( false );
//        CtZoomCanvasPanel zcp = new CtZoomCanvasPanel();
//        CtZoomCanvas zc = zcp.getZoomCanvas();
//
//        for( CtAbstractFactory< CtCanvasLayer > clf : _canvasLayerFactories ) {
//            CtCanvasLayer cl = clf.create();// zc );
//            zc.addLayer( cl );
////            cl.setParent( zc );
//
//            for( CtZoomCanvasLayerListener zcll : _canvasLayerListeners ) {
//                zcll.onCreateCanvasLayer( cl );
//            }
//        }
//
////        CtImageResultsCanvasLayer ircl = new CtImageResultsCanvasLayer( zcp.getZoomCanvas() );
        return zcp;
    }

    @Override public String getWindowType() {
        return "Lineage";
    }

}
