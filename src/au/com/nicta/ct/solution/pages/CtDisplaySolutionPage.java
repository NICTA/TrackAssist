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

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.ui.swing.components.CtTitleToolBar;
import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.ui.swing.mdi.CtWindowMenu;
import au.com.nicta.ct.ui.swing.util.CtMenuBuilder;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationsCanvasLayer;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultPanelFactory;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellTools;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsCanvasLayer;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationTools;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDetectionTools;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtTrackingTools;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageAction;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtTableEditorPage;
import au.com.nicta.ct.experiment.coordinates.time.CtTimeControlPanel;
import au.com.nicta.ct.solution.lineage.CtLineagePanelFactory;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtDisplaySolutionPage implements ActionListener, CtPage {

    // logical:
    protected String _name = "Display Solution";
    protected String _state = CtPageStates.DISPLAY;// on creation

    // graphical:
    CtTitleToolBar _ctb;
    CtWindowMenu _wm;
    CtImageResultPanelFactory _irpf;
    CtLineagePanelFactory _lpf;
    CtDockableWindowGrid _dwg;
    JPanel _centre;
    CtToolBarStack _east;
    CtToolBarStack _west;
    CtToolBarStack _south;
    CtTimeControlPanel _tcp;

    public CtDisplaySolutionPage() {
        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );
        CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );

        if(    ( e == null )
            || ( s == null ) ) {
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
            return;
        }

        create( e, s );
    }

    public CtDisplaySolutionPage( CtExperiments e, CtSolutions s ) { // show e on f
        create( e, s );
    }

    public void create( CtExperiments e, CtSolutions s ) { // show e on f

        _name = "Editing "+e.getName()+" ("+s.getName()+")";

        _ctb = new CtTitleToolBar( "ToolBar" );//CtFrame.find().newToolBar();
        _ctb.setTitle( _name );

//        Collection< CtAbstractPair< String, String > > options = CtSolutionPages.getSplitPageOptions();
//        JPanel p = new CtPageNavigationPanel( options );

//        JComponent centre = createCentreComponent();
        _dwg = new CtDockableWindowGrid( 1,1 );//2, 3 );
        _irpf = new CtImageResultPanelFactory();

        ArrayList< String > al = new ArrayList< String >();
        al.add( _irpf.getWindowType() );

        _lpf = new CtLineagePanelFactory( _dwg, al );
        _tcp = new CtTimeControlPanel( null, _dwg, al );

        // Panel to hold the experiment pannel and the histogram curve
        _centre = new JPanel();
        _centre.setOpaque(false);
        _centre.setLayout( new BorderLayout() );
        _centre.add( _dwg, BorderLayout.CENTER );

        _east  = new CtToolBarStack();// "East dock" );
        _west  = new CtToolBarStack();// "West dock" );
        _south = new CtToolBarStack();// "South dock" );

        _centre.add(  _east, BorderLayout.EAST  );
        _centre.add(  _west, BorderLayout.WEST  );
        _centre.add( _south, BorderLayout.SOUTH );

        addTools();

        body().addAncestorListener( _ctb );
    }

//    protected abstract JComponent createCentreComponent();// {
//        CtZoomCanvasPanel irp = new CtZoomCanvasPanel();// _em._cc, _em._isf );
//
//        CtZoomCanvas zc = irp.getZoomCanvas();//.addImageControls( _ctb );
////        zc.addImageControlsTo( (CtTitleToolBar)irp.getToolBar() );
//
//        CtTrackingController tc = CtTrackingController.get();// zc1 );
//        tc.setZoomCanvas( zc );
//        CtLineageController lc = CtLineageController.get();// zc2 );
//        lc.setZoomCanvas( zc );
//
//        return irp;
//    }
//
//    protected void addTools() { // override to add some
//    }
    protected void addTools() { // override to add some

        _wm = new CtWindowMenu( _dwg );
//        _wm.addToFrame();
        _dwg.addWindowContentFactory( _irpf );
        _dwg.addWindowContentFactory( _lpf );

        CtAnnotationsCanvasLayer.addFactoryTo( _irpf );
        CtDetectionsCanvasLayer.addFactoryTo( _irpf );
        CtTrackingCanvasLayer.addFactoryTo( _irpf );
        CtMicrowellsCanvasLayer.addFactoryTo( _irpf );

        CtMenuBuilder mb = new CtMenuBuilder();

        mb.setMenuOrder( "Edit", 0 );
        mb.setMenuOrder( "Data", 1 );
        mb.setMenuOrder( "View", 2 );

//        mb.addMenuItem( "Edit", new CtPageAction( "tableszz", "edit-table" ) );
        CtTableEditorPage.addMenuItems( mb, "Edit" );

        mb.addMenuItem( "Data", new CtPageAction( "Import", "import-images" ) );
        mb.addMenuItem( "Data", new CtPageAction( "Export", "export") );

        // all-windows:
        ArrayList< String > al = new ArrayList< String >();
        al.add( _irpf.getWindowType() );

                               CtToolToggles.addTimeToggle( mb, "View", _tcp, true );
        CtAnnotationTools at = CtToolToggles.addAnnotationTools( mb, "View", _east, _irpf, false );
        CtDetectionTools  dt = CtToolToggles.addDetectionTools( mb, "View", _west, _irpf, false );
        CtTrackingTools   st = CtToolToggles.addTrackingTools( mb, "View", _west, _irpf, false );
        CtMicrowellTools  mt = CtToolToggles.addMicrowellTools( mb, "View", _east, _irpf, false, _dwg, al );

        _irpf._canvasLayerFactories.add( dt._tm );
        _irpf._canvasLayerFactories.add( st._tm );

        // per-window:
        CtToolToggles.addLayerSelectionTool( _irpf );
        CtToolToggles.addContrastTool( _irpf );
        CtToolToggles.addCompositeTool( _irpf );

        mb.addMenusToFrame();
        _wm.addToFrame();
        _dwg.openWindow( _irpf.getWindowType() ); // default: open 1x imaging window.
    }

    public JComponent head() {
        return _ctb;//CtFrame.find().getToolBar();
    }

    public JPanel foot() {
        return _tcp;
    }

    public JPanel body() {
        return _centre;
    }

    public String key() {
        return "display-solution";
    }

    public String state() {
        return _state;
    }

    public void onExit() {
        CtPageFrame.find().clearMenuBar();
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        // uh.. no way to go to any other page from here currently!
    }

}
