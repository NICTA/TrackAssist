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

package au.com.nicta.ct.solution.pages;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.ui.swing.util.CtMenuBuilder;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationsCanvasLayer;
import au.com.nicta.ct.experiment.graphics.canvas.composite.CtCompositeToolFactory;
import au.com.nicta.ct.experiment.graphics.canvas.contrast.CtContrastToolFactory;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultPanelFactory;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.graphics.canvas.images.toggle.CtCanvasLayersToggle;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultsSelectionToolFactory;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellTools;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsCanvasLayer;
import au.com.nicta.ct.graphics.canvas.images.toggle.CtComponentVisibilityToggle;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationTools;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDetectionTools;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtTrackingTools;
import au.com.nicta.ct.experiment.coordinates.time.CtTimeControlPanel;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * Handy functions for pulling together all the components ... otherwise its
 * hard to remember exactly what to write, and have to repeat the same code
 * everywhere.
 * 
 * @author davidjr
 */
public class CtToolToggles {

    public static void addTimeToggle( CtMenuBuilder mb, String menu, CtTimeControlPanel tcp, boolean enabled ) {
        CtComponentVisibilityToggle tt = new CtComponentVisibilityToggle( "Time", null, null, tcp );
        tt.setSelected( enabled );
        mb.addCheckableMenuItem( menu, tt.getAction() );
    }

    public static void addTimeToggle( JToolBar tb, CtTimeControlPanel tcp, boolean enabled ) {
        CtComponentVisibilityToggle tt = new CtComponentVisibilityToggle( "Time", null, null, tcp );
        tt.setSelected( enabled );
        JToggleButton b = tt.createToggleButton();
        tb.add( b );
    }

    public static CtMicrowellTools addMicrowellTools( CtMenuBuilder mb, String menu, CtToolBarStack tbs, CtViewpointZoomCanvasPanelFactory zcpf, boolean enabled, CtDockableWindowGrid dwg, Collection< String > windowTypes ) {
        CtMicrowellTools mt = new CtMicrowellTools( dwg, windowTypes );// zc );
        tbs.addTool( mt, "Microwells" );
        CtCanvasLayersToggle.add( mb, menu, mt, "Microwells", null, null, enabled, zcpf, CtMicrowellsCanvasLayer.CANVAS_LAYER_NAME );
        return mt;
    }

    public static CtAnnotationTools addAnnotationTools( CtMenuBuilder mb, String menu, CtToolBarStack tbs, CtViewpointZoomCanvasPanelFactory zcpf, boolean enabled ) {
        CtAnnotationTools at = new CtAnnotationTools();// zc );
        tbs.addTool( at, "Annotations" );
        CtCanvasLayersToggle.add( mb, menu, at, "Annotations", null, null, enabled, zcpf, CtAnnotationsCanvasLayer.CANVAS_LAYER_NAME );
        return at;
    }

    public static CtDetectionTools addDetectionTools( CtMenuBuilder mb, String menu, CtToolBarStack tbs, CtViewpointZoomCanvasPanelFactory zcpf, boolean enabled ) {
        CtDetectionTools dt = new CtDetectionTools();//, _gmc );//, wells.getModel() );
        tbs.addTool( dt, "Detections" );
        zcpf._canvasLayerListeners.add( dt._tm );
        ArrayList< String > canvasLayerNames = new ArrayList< String >();
        canvasLayerNames.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        canvasLayerNames.add( dt._tm.getCanvasLayerName() );
        CtCanvasLayersToggle.add( mb, menu, dt, "Detections", null, null, enabled, zcpf, canvasLayerNames );
        return dt;
    }

    public static CtTrackingTools addTrackingTools( CtMenuBuilder mb, String menu, CtToolBarStack tbs, CtViewpointZoomCanvasPanelFactory zcpf, boolean enabled ) {
        CtTrackingTools st = new CtTrackingTools();// zc );//, wells.getModel() );
        tbs.addTool( st, "Tracking" );
        zcpf._canvasLayerListeners.add( st._tm );
        ArrayList< String > canvasLayerNames = new ArrayList< String >();
        canvasLayerNames.add( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        canvasLayerNames.add( st._tm.getCanvasLayerName() );
        CtCanvasLayersToggle.add( mb, menu, st, "Tracking", null, null, enabled, zcpf, canvasLayerNames );
        return st;
    }

    public static void addLayerSelectionTool( CtImageResultPanelFactory irpf ) {
        irpf._imageResultToggleFactories.add( new CtImageResultsSelectionToolFactory() );
    }

    public static void addContrastTool( CtImageResultPanelFactory irpf ) {
        irpf._imageResultToggleFactories.add( new CtContrastToolFactory() );
    }

    public static void addCompositeTool( CtImageResultPanelFactory irpf ) {
        irpf._imageResultToggleFactories.add( new CtCompositeToolFactory() );
    }


// dont need this anymore
//    public static void addLineageToggle( JToolBar tb, CtZoomCanvasPanel irp, boolean enabled ) {
//       CtComponentVisibilityToggle.addToggleButton( tb, irp, "Lineage", enabled );
//    }

//    public static void addToolToggleTo( JToolBar tb, JComponent tool, String title, boolean enabled ) {
//        CtComponentVisibilityToggle tt = new CtComponentVisibilityToggle( title, tool );
//        JToggleButton b = tt.getButton();
//        tb.add( b );
//
//        b.setSelected( true ); // cos default to showing
//
//        if( !enabled ) {
//            b.setSelected( false ); // cos default to showing
//        }
//    }
//
//    public static void addCanvasLayerTool( JToolBar tb, CtToolBarStack tbs, String title, JPanel toolPanel, CtCanvasLayer cl, boolean enabled ) {
//        ArrayList< CtCanvasLayer > ccl = new ArrayList< CtCanvasLayer >();
//        ccl.add( cl );
//        addCanvasLayerTool( tb, tbs, title, toolPanel, ccl, enabled );
//    }
//
//    public static void addCanvasLayerTool( JToolBar tb, CtToolBarStack tbs, String title, JPanel toolPanel, Collection< CtCanvasLayer > ccl, boolean enabled ) {
//        CtCanvasLayersToggle clt = new CtCanvasLayersToggle( title, toolPanel, ccl );
//        JToggleButton active = clt.getButton();
//
//        tbs.addTool( toolPanel, title );
//        tb.add( active );
//
//        active.setSelected( true ); // cos default to showing
//
//        if( !enabled ) {
//            active.setSelected( false ); // cos default to showing
//        }
//    }

//        CtCanvasLayerToggle clt = new CtCanvasLayerToggle( "Annotations", at, at.getCanvasLayer() );
//        JToggleButton active = clt.getButton();
//
//        tbs.addTool( at, "Annotations" );
//        tb.add( active );
//
//        active.setSelected( true ); // cos default to showing
//        active.setSelected( false ); // cos default to showing

//    public static void addDetectionTools( JComponent parent, CtToolBarStack tbs, CtZoomCanvasPanelFactory zcpf ) {
//        CtDetectionTools dt = new CtDetectionTools();//, _gmc );//, wells.getModel() );
//        CtCanvasLayersToggle.add( parent, null, "Detections", false, zcpf, dt._tm.getCanvasLayerNames() );
//        tbs.addTool( dt, "Detections" );
//
////        addCanvasLayerTool( irp.getToolBar(), tbs, "Detections", dt, dt.getCanvasLayers(), false );
//// ref       CtCanvasLayersToggle.add( JComponent c, JComponent tool, String title, boolean enable, CtZoomCanvasPanelFactory zcpf, Collection< String > canvasLayerNames )
////        CtCanvasLayerToggle clt = new CtCanvasLayerToggle( "Detections", dt, dt.getCanvasLayers() );
////        JToggleButton active = clt.getButton();
////
////        tbs.addTool( dt, "Detections" );
////        tb.add( active );
////
////        active.setSelected( true ); // cos default to showing
////        active.setSelected( false ); // cos default to showing
//    }

//    public static void addSolutionTools( JComponent parent, CtToolBarStack tbs, CtZoomCanvasPanelFactory zcpf ) {
////        CtZoomCanvas zc = irp.getZoomCanvas();
//        CtSolutionTools st = new CtSolutionTools();// zc );//, wells.getModel() );
//        CtCanvasLayersToggle.add( parent, null, "Tracking", false, zcpf, st._tm.getCanvasLayerNames() );
//        tbs.addTool( st, "Tracking" );
//
////        addCanvasLayerTool( irp.getToolBar(), tbs, "Tracking", st, st.getCanvasLayers(), false );
//
////        CtCanvasLayerToggle clt = new CtCanvasLayerToggle( "Tracks", dt, dt.getCanvasLayer() );
////        JToggleButton active = clt.getButton();
////
////        tbs.addTool( dt, "Tracking" );
////        tb.add( active );
////
////        active.setSelected( true ); // cos default to showing
////        active.setSelected( false ); // cos default to showing
//    }

//    public static void addLegendTool(CtImageResultPanel irp) {
//        CtLegendLayer llt = new CtLegendLayer( irp.getZoomCanvas() );
//
//        JToggleButton active = llt.getButton();
//        active.setSelected(true);
//
//        irp.getToolBar().add( active );
//    }

    public static void addMicrowellTool( JComponent parent, CtToolBarStack tbs, CtViewpointZoomCanvasPanelFactory zcpf ) {
//        CtMicrowellTools mt = new CtMicrowellTools();//, wells.getModel() );
////        addCanvasLayerTool( parent, tbs, "Microwells", mt, mt.getCanvasLayer(), false );
//        CtCanvasLayersToggle.add( parent, null, "Microwells", false, zcpf, CtMicrowellsCanvasLayer.CANVAS_LAYER_NAME );
//        tbs.addTool( mt, "Microwells" );
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
//    public static CtImageResultsSelectionTool addLayerSelectionTool( JComponent parent, CtToolBarStack tbs, CtZoomCanvasPanelFactory zcpf ) {
//        // add layer tool
////        JToolBar tb = irp.getToolBar();
////        CtZoomCanvas zc = irp.getZoomCanvas();
////        CtCanvasLayer cl = zc.getLayer( CtImageResultsCanvasLayer.CANVAS_LAYER_NAME );
////        CtImageResultCanvasLayer ircl = (CtImageResultCanvasLayer)cl;
////        CtImageResultsCanvasLayer ircl = CtImageResultsCanvasLayer.get( zc );
//        CtImageResultsSelectionTool lst = new CtImageResultsSelectionTool( zcpf );// ircl );
//        parent.add( lst );
//        return lst;
//    }
//
//    public static void addImageResultTool(
////        JToolBar tb,
//        CtToolBarStack tbs,
//        CtZoomCanvasPanel irp,
//        CtImageResultsSelectionTool lst,
//        String title,
//        JComponent toolPanel,
//        CtImageResult ir,
//        boolean enabled ) {
//
//        JToolBar tb = irp.getToolBar();
//        CtImageResultToggle irt = new CtImageResultToggle( title, toolPanel, irp, ir );
//        JToggleButton active = irt.getButton();
//
//        tb.add( active );
//        tbs.addTool( toolPanel, title );
////        tbs.add( toolPanel, BorderLayout.SOUTH );
//
//        active.setSelected( true ); // cos default to showing
//
//        if( !enabled ) {
//            active.setSelected( false ); // cos default to showing
//        }
//
//        // add layer handler
//        lst.add( title, irt );
//    }
//
//    public static void addContrastTool( CtToolBarStack tbs, CtZoomCanvasPanel irp, CtImageResultsSelectionTool lst ) {
//
//        CtGrayscaleMapCanvas gmc = new CtGrayscaleMapCanvas( irp.getOriginal() );// _ep );
//
//        gmc.setFlipLeftRight( false );
//        gmc.setFlipUpDown( false );
//        gmc.setRotate90( false );
//        gmc.setPreferredSize( new Dimension( 300, 100 ) );
//
//        addImageResultTool( tbs, irp, lst, "Contrast", gmc, gmc.getDst(), false );
//
////        CtImageResultToggle iot = new CtImageResultToggle( "Contrast", gmc, irp, gmc.getImageResult() );
////        JToggleButton active = iot.getButton();
////
////        tb.add( active );
////        tbs.add( gmc, BorderLayout.SOUTH );
////
////        active.setSelected( true ); // cos default to showing
////        active.setSelected( false ); // cos default to showing
////
////        // add layer handler
////        lst.add( "Contrast" , iot );
//   }
//
//    public static void addCollateTools( CtToolBarStack tbs, CtZoomCanvasPanel irp, CtImageResultsSelectionTool lst ) {
//
//        CtZoomCanvas zc = irp.getZoomCanvas();
//        CtCollateTools ct = new CtCollateTools( zc );
//
//        addImageResultTool( tbs, irp, lst, "Compositing", ct, ct.getImageResult(), false );
//
////        CtImageResultToggle iot = new CtImageResultToggle( "Composite", ct, irp, ct.getImageResult() );
////        JToggleButton active = iot.getButton();
////
////        tb.add( active );
////        tbs.addTool( ct, "Compositing" );
////
////        active.setSelected( true );
////        active.setSelected( false );
////
////        // add layer handler
////        lst.add( "Composite" , iot );
//   }

//    public static void addAxesTools( JToolBar tb ) {
//
////        CtExperimentModel em = CtExperimentModel.get();
//        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtCoordinatesModel cm = cc.getCoordinatesModel();
//
//        String rangeType = cm.getRangeType();//"time";
//
//        for( CtExperimentsAxes ea : cm._am.axes() ) {
//
//            String coordinateType = ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes().getName();
//
//            if( !coordinateType.equals( rangeType ) ) {
//                 tb.add( new CtCoordinateComboBox( cc, coordinateType ) );
//            }
//        }
//    }

}
