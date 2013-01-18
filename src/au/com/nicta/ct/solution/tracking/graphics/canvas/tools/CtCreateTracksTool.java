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

package au.com.nicta.ct.solution.tracking.graphics.canvas.tools;

import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.tracking.jipda.CtParaTracker;
import au.com.nicta.ct.solution.tracking.jipda.CtTkDBTracker;
import au.com.nicta.ct.solution.tracking.jipda.CtTrackerProcess;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author davidjr
 */
public class CtCreateTracksTool extends CtTool implements ActionListener{    
    
    JComponent _c;
    JTextField txtCov;
    JFormattedTextField txtGamma;
    JFormattedTextField txtPTS;
    JFormattedTextField txtPTE;
    JFormattedTextField txtMicroWell;
//    JFormattedTextField txtMaxMissingDetectionRate;
//    JFormattedTextField txtMinDetectionPerTrack;
    CtParaTracker para;

    final String MIN_DETECTIONS_PER_TRACK = "Min Det. per track";
    final String MAX_MISSING_DETECTION_RATE = "Max Miss Det. Rate";

    final double DEFAULT_MAX_MISSING_DETECTION_RATE = 0.3;
    final int DEFAULT_MIN_DETECTION_PER_TRACK = 5;
    final int DEFAULT_MAX_CONSEC_MISSING_DETECTIONS = 2;

    public CtCreateTracksTool( CtToolsModel tm ) {//, CtSolutionController sc ) {//, final CtEditMode editMode) {
        super( tm, "create-tracks" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        sc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

//        _mappings.add( new CtAbstractPair< CtDetectionState, CtDetectionState >( CtDetectionState.SELECTED, CtDetectionState.NORMAL ) );
//        _mappings.add( new CtAbstractPair< CtDetectionState, CtDetectionState >( CtDetectionState.NORMAL, CtDetectionState.SELECTED ) );

//        applyOnMouseClicked();
        para = new CtParaTracker();
        createPanel();
        updateEnabled();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    private void createPanel() { 
//        JPanel p1 = new JPanel();
//        GridLayout layout = new GridLayout( 4, 2 );
//        p1.setLayout( layout );

        JPanel pg = new JPanel();
        GroupLayout gl = new GroupLayout( pg );
        pg.setLayout( gl );
        pg.setOpaque( false );
        gl.setAutoCreateGaps( true );
        gl.setAutoCreateContainerGaps( true );
        
        txtCov = new JTextField("4000");
        txtCov.setColumns( 5 );
        txtGamma = new JFormattedTextField( new NumberFormatter() );
        txtGamma.setValue( 0.95 );        
        txtGamma.setColumns( 5 );        
        txtPTS = new JFormattedTextField( new NumberFormatter() );
        txtPTS.setValue( 0.1 );
        txtPTS.setColumns( 5 );
        txtPTE = new JFormattedTextField( new NumberFormatter() );
        txtPTE.setValue( 0.01 );
        txtPTE.setColumns( 5 );
        txtMicroWell = new JFormattedTextField();
        txtMicroWell.setValue( "All" );
        txtMicroWell.setColumns( 5 );

        JLabel l1 = new JLabel( "Cov. Motility" );
        JLabel l2 = new JLabel( "Gamma" );
        JLabel l3 = new JLabel( "P. Track begin" );
        JLabel l4 = new JLabel( "P. Track end" );
        JLabel l5 = new JLabel( "Micro-well" );

        gl.setHorizontalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent( l1 )
                   .addComponent( l2 )
                   .addComponent( l3 )
                   .addComponent( l4 )
                   .addComponent( l5 ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent( txtCov )
                   .addComponent( txtGamma )
                   .addComponent( txtPTS )
                   .addComponent( txtPTE )
                   .addComponent( txtMicroWell ) )
        );

        gl.setVerticalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l1 )
                   .addComponent( txtCov ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l2 )
                   .addComponent( txtGamma ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l3 )
                   .addComponent( txtPTS ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l4 )
                   .addComponent( txtPTE ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l5 )
                   .addComponent( txtMicroWell ) )
        );

//        p1.add( new JLabel( "Cov. Motility" ) );
//        p1.add( txtCov );
//        p1.add( new JLabel( "Gamma" ) );
//        p1.add( txtGamma );
//        p1.add( new JLabel( "P. Track begin" ) );
//        p1.add( txtPTS );
//        p1.add( new JLabel( "P. Track end" ) );
//        p1.add( txtPTE );
//        p1.setOpaque( false );


        JButton apply = new JButton( "Apply" );
        apply.addActionListener( this );  
        JPanel p2 = new JPanel( new FlowLayout( FlowLayout.TRAILING ) );
        p2.setOpaque( false );
        p2.add( apply );
        
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.PAGE_AXIS ) );
//        p.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        p.setOpaque( false );
        p.add( pg );
        p.add( p2 ); 
        _c = p;
    }   

    @Override public JComponent panel() {
        return _c;
    }
    
    public void onSolutionModelChanged() {
        updateEnabled();
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        
        // Get parameters
        para.setProcessCov( Double.parseDouble(txtCov.getText()) );
        para.setGamma( Double.parseDouble(txtGamma.getText()) );
        para.setPTS( Double.parseDouble(txtPTS.getText()) );
        para.setPTE( Double.parseDouble(txtPTE.getText()) );
//        para.setMaxMissingDetectionRate( Double.parseDouble(txtMaxMissingDetectionRate.getText()) );
//        para.setMinDetectionPerTrack( Integer.parseInt(txtMinDetectionPerTrack.getText()) );
        para.setMaxMissingDetectionRate( DEFAULT_MAX_MISSING_DETECTION_RATE );
        para.setMinDetectionPerTrack( DEFAULT_MIN_DETECTION_PER_TRACK );
        para.setMaxConsecMissingDetections(DEFAULT_MAX_CONSEC_MISSING_DETECTIONS);

        int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to create tracks for all images in the solution?", "Create Tracks", JOptionPane.YES_NO_OPTION );
        if( n != JOptionPane.YES_OPTION ) {
            return;
        }

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return;
        }

//        CtFrame.showWaitCursor();
//        tc.clear();
//        CtTkDBTracker.run();
        CtTkDBTracker t = new CtTkDBTracker( para );
        t.setMicroWellName( txtMicroWell.getText() );
        
        CtTrackerProcess tp = new CtTrackerProcess( t );
        tp.enqueue();
//        tp.run( t, "Creating Tracks...", "" );

//        t.runAsynch();
//        t.doAllSteps();
//        sc.refresh( true );
//        CtFrame.showDefaultCursor();

        _tm.activateDefault();        
    }
    
    @Override public void updateEnabled() {

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        setEnabled( true );
    }

    @Override protected String iconFile() {
        return "track_create_all.png";
    }

    @Override protected String toolTip() {
        return "Create tracks";
    }

    
//    @Override public void activate(){
//        int n = JOptionPane.showConfirmDialog( CtFrame.find(), "Are you sure you want to create tracks for all images in the solution?", "Create Tracks", JOptionPane.YES_NO_OPTION );
//        if( n != JOptionPane.YES_OPTION ) {
//            return;
//        }
//
//        CtTrackingController tc = CtTrackingController.get();
//
//        if( tc == null ) {
//            return;
//        }
//
////        CtFrame.showWaitCursor();
////        tc.clear();
////        CtTkDBTracker.run();
//        CtTkDBTracker t = new CtTkDBTracker();
//        CtTrackerProcess tp = new CtTrackerProcess( t );
//        tp.enqueue();
////        tp.run( t, "Creating Tracks...", "" );
//
////        t.runAsynch();
////        t.doAllSteps();
////        sc.refresh( true );
////        CtFrame.showDefaultCursor();
//
//        _tm.activateDefault();
//    }
//
//    @Override protected void updateButton( JToggleButton button ) {
////        button.setEnabled( _mode.isEnabled() && this.isEnabled() );
//        boolean selected1 = button.isSelected();
//
//        button.setEnabled( this.isEnabled() );
//        button.setSelected( this.isActive() );
//
//        boolean selected2 = button.isSelected();
//
//        if( selected2 & (!selected1) ) { // if changed to selected:
//            setActive( false );
//        }
//    }
//
//    @Override public void apply( MouseEvent e ) {
////        CtSolutionController sc = (CtSolutionController)CtObjectDirectory.get( CtSolutionController.name() );
//
//        _sc.associateSelected();
//    }

}
