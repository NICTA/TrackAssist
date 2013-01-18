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

import au.com.nicta.ct.solution.CtSolutionPages;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.ui.swing.components.CtTitleToolBar;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.mvc.pages.util.CtPageNavigationPanel;
import au.com.nicta.ct.experiment.coordinates.time.CtTimeControlPanel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtLineagePage implements ActionListener, CtPage {

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
        return "lineage";
    }

    public String state() {
        return _state;
    }

    public void onExit() {}
    
    // logical:
    protected String _name = "Lineage Tree";
    protected String _state = CtPageStates.DISPLAY;// on creation

    // graphical:
    JPanel _centre;
//    CtExperimentModel _em;

//    CtLineageController _lc;

    CtZoomCanvasPanel _irp;
    CtTimeControlPanel _tcp;
    CtTitleToolBar _ctb;

    CtToolBarStack _west;
    CtToolBarStack _east;
    CtToolBarStack _south;

    public CtLineagePage() {
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

    public CtLineagePage( CtExperiments e, CtSolutions s ) {
        create( e, s );
    }

    public void create( CtExperiments e, CtSolutions s ) { // show e on f

        _name = "Lineage of "+e.getName();
//        CtExperimentModel em = CtExperimentModel.get();
//      _em = CtExperimentModel.get();
//        _em = CtExperimentModel.get( e, "time" );//new CtExperimentModel( e );

        _ctb = new CtTitleToolBar( "ToolBar" );//CtFrame.find().newToolBar();
        _ctb.setTitle( _name );

        Collection< CtAbstractPair< String, String > > options = CtSolutionPages.getLineagePageOptions();
        JPanel p = new CtPageNavigationPanel( options );

        _irp = new CtZoomCanvasPanel();// _em._cc, _em._isf );
//        _lc = CtLineageController.get();// _em._cc, _em._isf );
//        _tcp = new CtTimeControlPanel( p );

        CtLineageController lc = CtLineageController.get();// _irp.getZoomCanvas() );
//        lc.setZoomCanvas( _irp.getZoomCanvas() );
//        _em._isf.addImageSequenceListener( _tcp );
//        _em._cc.setRange( sequenceAxis );

        // Panel to hold the experiment pannel and the histogram curve
        _centre = new JPanel();
        _centre.setOpaque(false);
        _centre.setLayout( new BorderLayout() );
//        _centre.add( _lc.getLineageView(), BorderLayout.CENTER );
        _centre.add( _irp, BorderLayout.CENTER );

        _west = new CtToolBarStack();// " Selected Tools " );
        _east = new CtToolBarStack();// " Special " );
        _south = new CtToolBarStack();// " Wide tools " );
        
        _centre.add( _west, BorderLayout.WEST );
        _centre.add( _east, BorderLayout.EAST );
        _centre.add( _south, BorderLayout.SOUTH );

//        _irp.getZoomCanvas().addImageControlsTo( _ctb );

        body().addAncestorListener( _ctb );
    }


    @Override public void actionPerformed( ActionEvent ae ) {
        // uh.. no way to go to any other page from here currently!
    }
}
