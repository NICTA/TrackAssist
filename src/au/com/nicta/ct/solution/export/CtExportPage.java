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

package au.com.nicta.ct.solution.export;

import au.com.nicta.ct.solution.CtSolutionPages;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.export.concrete.CtExportDetectionLabelsProcess;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.orm.mvc.pages.util.CtPageNavigationPanel;
import au.com.nicta.ct.solution.export.concrete.CtAnnotationsExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtDetectionsExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtDetectionsMomentsExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtDetectionsPropertiesExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtImagesCoordinatesExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtImagesRegistrationResultExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtTrackEventExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtTrackedDetectionCountExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtTracksExportProcess;
import au.com.nicta.ct.solution.export.concrete.CtTracksPropertiesExportProcess;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author davidjr
 */
public class CtExportPage extends JPanel implements ActionListener, CtPage {

    public JComponent head() {
        return new CtHeaderPanel( "Exporting "+_name );
    }

    public JPanel foot() {
        return _foot;
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return "export";
    }

    public String state() {
        return _state;
    }

    public void onExit() {}

    // logical:
    protected String _name = "n/a";
    protected String _state = CtPageStates.DISPLAY;// on creation
    public static final String COMMAND_CHANGE_FILE_PATH = "CHANGE_FILE_PATH";
    public static final String COMMAND_EXPORT = "EXPORT";
    
    // graphical:
    JTextField _filePathField;
    JComboBox _exportDataCombo;
    CtFooterPanel _foot;

    public CtExportPage() {

        super();

        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );
        CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );

        if(    ( e == null )
            || ( s == null ) ) {
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
            return;
        }
        create( e, s, new String( "time" ) );
    }

    public CtExportPage( CtExperiments e, CtSolutions s ) { // show e on f
        super();
        create( e, s, new String( "time" ) );
    }

    public CtExportPage( CtExperiments e, CtSolutions s, String sequenceAxis ) {
        super();
        create( e, s, sequenceAxis );
    }

    public void create( CtExperiments e, CtSolutions s, String sequenceAxis ) { // show e on f

        CtPages.setBorder( this );
        setOpaque( false );
        setLayout( new BorderLayout() );

        _name = e.getName();

        Collection< CtAbstractPair< String, String > > options = CtSolutionPages.getExportPageOptions();
        JPanel p = new CtPageNavigationPanel( options );

        _foot = new CtFooterPanel();
        _foot.addEast( p );

        // centre panel content
//        JPanel row0 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
//        row0.setOpaque( false );
//        JLabel row0Label = new JLabel( CtApplication.h3( "Options" ) );
//        row0.add( row0Label );
        JPanel row1 = new JPanel( new BorderLayout() );
//               row1.setOpaque( false );
        JLabel     filePathLabel = new JLabel( CtStyle.h3( "Export to " ) );
                  _filePathField = new JTextField( CtConstants.TEXT_FIELD_WIDTH_CHARACTERS );
           JButton filePathBrowse = new JButton( "Change" );
                   filePathBrowse.setActionCommand( COMMAND_CHANGE_FILE_PATH );
                   filePathBrowse.addActionListener( this );

//        JPanel source = new JPanel( new BorderLayout() );
//        source.setOpaque( false );
//        source.add( labelImageSourceDir, BorderLayout.WEST );
//        source.add( flow, BorderLayout.EAST );

        JPanel flow1 = new JPanel( new FlowLayout() );
        flow1.add( filePathBrowse );
        flow1.setBackground(CtConstants.NictaYellow );

        row1.add( _filePathField, BorderLayout.CENTER );
        row1.add( flow1, BorderLayout.EAST );
        row1.setBackground(Color.green);

        JPanel row2 = new JPanel( new BorderLayout() );
 //              row2.setOpaque( false );
        row2.setBackground(Color.red);
        JLabel exportDataLabel = new JLabel( CtStyle.h3( "Export format " ) );

        CtExportController ec = CtExportController.get();

        ec.add( new CtTrackEventExportProcess() );
        ec.add( new CtDetectionsMomentsExportProcess() );
        ec.add( new CtExportDetectionLabelsProcess() );
        ec.add( new CtDetectionsPropertiesExportProcess() );
        ec.add( new CtImagesCoordinatesExportProcess() );
        ec.add( new CtAnnotationsExportProcess() );
        ec.add( new CtTracksPropertiesExportProcess() );
        ec.add( new CtTracksExportProcess() );
        ec.add( new CtDetectionsExportProcess() );
        ec.add( new CtTrackedDetectionCountExportProcess() );
        ec.add( new CtImagesRegistrationResultExportProcess() );



        Collection< String > names = ec.getModel().getNames();
//        String[] dataModel = {"shot","very very very very very very very long"};
        _exportDataCombo = new JComboBox( names.toArray() );//dataModel );

//        JPanel flow2 = new JPanel( new FlowLayout() );
//        flow2.add( exportDataCombo );
//        flow2.setBackground(CtConstants.NictaYellow );

//        row2.add( exportDataLabel );
        row2.add( _exportDataCombo, BorderLayout.CENTER );

        JPanel row3 = new JPanel( new BorderLayout() );
               row3.setOpaque( false );
        row1.setBackground(Color.magenta);
        JButton exportButton = new JButton( "Export" );
        exportButton.setPreferredSize( filePathBrowse.getPreferredSize() );
        exportButton.setActionCommand( COMMAND_EXPORT );
        exportButton.addActionListener( this );

        JPanel flow3 = new JPanel( new FlowLayout() );
        flow3.add( exportButton );
        flow3.setBackground( CtConstants.NictaYellow );

        JPanel blank = new JPanel( new FlowLayout() );
        blank.setOpaque( false );
        blank.setPreferredSize( _filePathField.getPreferredSize() );

        row3.add( blank, BorderLayout.CENTER );
        row3.add( flow3, BorderLayout.EAST );

        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( exportDataLabel, false ) );
        c.add( new CtComponentResizePolicy( row2, false ) );
        c.add( new CtComponentResizePolicy( filePathLabel, false ) );
        c.add( new CtComponentResizePolicy( row1, false ) );
        c.add( new CtComponentResizePolicy( row3, false ) );
        JPanel dialog = CtDialogPanel.create( c );
        add( dialog, BorderLayout.CENTER );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String action = ae.getActionCommand();

        if( action.equals( COMMAND_CHANGE_FILE_PATH ) ) {
            JFileChooser fc = CtExportController.get().getFileChooser( (String)_exportDataCombo.getSelectedItem() );

            int result = fc.showSaveDialog( CtPageFrame.find() );

            if( result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                try {
                    String s = new String( file.getCanonicalPath() );
                    _filePathField.setText( s );
                }
                catch( IOException ioe ) {
                    ioe.printStackTrace();
                }
            }
        }
        else if( action.equals( COMMAND_EXPORT ) ) {
            CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );
            String exportType = (String)_exportDataCombo.getSelectedItem();
            String filePath = _filePathField.getText();

            if( filePath.isEmpty() ) {
                String message = "You must specify filename for export data.";
                JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
                return;
            }

            if( s == null ) {
                String message = "No solution available.";
                JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
                return;
            }

            CtExportController ec = CtExportController.get();
            String result = ec.apply( s, exportType, filePath );

            if( result == null ) {
                result = "Data exported successfully to file: "+filePath;
            }

            JOptionPane.showMessageDialog( this.getTopLevelAncestor(), result );

//            try {
////                File f = new File( s );
//
//                // do export.. save data...
//            }
//            catch( IOException ioe ) {
//                String message = "You must specify a valid filename for export data.";
//                JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
//                return;
//            }
        }
    }
}
