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

package au.com.nicta.ct.experiment.setup;


import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.editor.CtComboModel;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultComboBoxModel;




public class CtFileNameParsingPanel extends JPanel {

    public static final String _ACTION_ADD_NEW  = "Add new coordinate";
    
    public static final String _typeNumber      = "Number";
    public static final String _typeString      = "String";
    public static final String _typeDateTime    = "Date/Time";
    public static final String _typeNone        = "";


    private JComboBox    coordinatesTypes;
    private JComboBox    coordinateDataTypes;
    private JLabel       labelSampleFile;
    private JTextField   textFieldSampleFile;
    private JScrollPane  scrollPaneNameParts;
    private JLabel       labelNameParts;
    private JButton      buttonReset;
//    private JPanel       panelReset;


    private CtFileNameParsingTableModel model;
    private JTable table;

    private String sampleImageFileName;

    private String validationResult;

    public CtFileNameParsingPanel() {

        coordinatesTypes    = new JComboBox();
        coordinateDataTypes = new JComboBox();
        labelSampleFile     = new JLabel( CtStyle.h3( "Example File Name" ) );
        textFieldSampleFile = new JTextField( "" );
        scrollPaneNameParts = new JScrollPane();
        scrollPaneNameParts.setOpaque( false );
        buttonReset         = new JButton( "Reset" );
        labelNameParts      = new JLabel( CtStyle.h3( "File Name Breakdown" ) );
        
        createCoordinateTypesComboBox();
        createCoordinateDataTypeComboBox();

        buttonReset.addMouseListener( new MouseAdapter() {
            @Override public void mouseClicked( MouseEvent m ) {
                    resetTableModel();
                }
            }
         );

        model = new CtFileNameParsingTableModel( this, "" );
        
        CtSetupController sc = CtSetupController.instance();

        

        setSampleFileName( sc.getFileNames().iterator().next() );
        if( !sc.isImageSourceDirectoryChanged() ) {
            System.out.println(" False ");
            model = sc.getParsingTableModel();
        }
        else{
            model.resetModel( textFieldSampleFile.getText(),CtFileNameParsingTableModel._ignore );
        }
        
        textFieldSampleFile.setEditable( false );
        
        //setting up renderer and editor for ComboBoxes
        table = new JTable( model );
        table.getColumnModel() .getColumn( 3 ) .setCellRenderer( new DefaultTableCellRenderer() );
        table.getColumnModel() .getColumn( 4 ) .setCellRenderer( new DefaultTableCellRenderer() );
        table.getColumnModel() .getColumn( 3 ) .setCellEditor( new DefaultCellEditor( coordinatesTypes ) );
        table.getColumnModel() .getColumn( 4 ) .setCellEditor( new DefaultCellEditor( coordinateDataTypes ) );
        table.setRowHeight( 24 );
        table.setBackground( Color.WHITE );
        scrollPaneNameParts.setViewportView( table );

        // build gui

        JPanel sampleFile = new JPanel( new BorderLayout() );
        sampleFile.setOpaque( false );
        sampleFile.add( labelSampleFile, BorderLayout.NORTH );
        sampleFile.add( textFieldSampleFile, BorderLayout.CENTER );

        JPanel flow = new JPanel( new FlowLayout() );
        flow.add( buttonReset );
        flow.setBackground(CtConstants.NictaYellow );
        JPanel partsReset = new JPanel( new BorderLayout() );
        partsReset.add( labelNameParts, BorderLayout.WEST );
        partsReset.add( flow, BorderLayout.EAST );
        partsReset.setOpaque( false );

        JPanel top = new JPanel( new BorderLayout() );
        top.setOpaque( false );
        top.add( sampleFile, BorderLayout.NORTH );
        top.add( partsReset, BorderLayout.CENTER );

        setLayout( new BorderLayout() );
        setOpaque( false );
        add( top, BorderLayout.NORTH );
        add( scrollPaneNameParts, BorderLayout.CENTER );

        validationResult = "";
  }

  private void setSampleFileName( String sampleImageFileName ) {

        this.sampleImageFileName = sampleImageFileName;
        textFieldSampleFile.setText( sampleImageFileName );
        return;
    }

    private void createCoordinateTypesComboBox() {

        CtComboModel lstImageCoordinates;
        try {
            lstImageCoordinates=new CtComboModel( CtSession.getObjects( "from CtCoordinatesTypes" ),"name" );

            DefaultComboBoxModel allCoordinates = new DefaultComboBoxModel();

            allCoordinates.addElement( CtFileNameParsingTableModel._ignore );
            for( int c=0; c<lstImageCoordinates.getSize(  ); c++ ) {
                allCoordinates.addElement( lstImageCoordinates.getElementAt( c ) );
            }
            coordinatesTypes.setModel( allCoordinates );
            coordinatesTypes.setSelectedIndex( 0 );
        }
        catch( Throwable t ) {

            DefaultComboBoxModel allCoordinates=new DefaultComboBoxModel();

            allCoordinates.addElement( CtFileNameParsingPanel._ACTION_ADD_NEW );
            coordinatesTypes.setModel( allCoordinates );
            coordinatesTypes.setSelectedIndex( 0 );

        }
        return;
    }

    private void createCoordinateDataTypeComboBox() {

        DefaultComboBoxModel lstCoordinateDataTypes=new DefaultComboBoxModel();

        lstCoordinateDataTypes .addElement( CtFileNameParsingPanel._typeNone     );
        lstCoordinateDataTypes .addElement( CtFileNameParsingPanel._typeString   );
        lstCoordinateDataTypes .addElement( CtFileNameParsingPanel._typeNumber   );
        lstCoordinateDataTypes .addElement( CtFileNameParsingPanel._typeDateTime );

        coordinateDataTypes.setModel( lstCoordinateDataTypes );

        coordinateDataTypes.setSelectedIndex( 0 );
        return;
    }

    public void resetTableModel() {
        model.resetModel( this.sampleImageFileName, CtFileNameParsingTableModel._ignore );
    }

    public void resetCoordinates() {
        coordinatesTypes.removeAll();
        createCoordinateTypesComboBox();
    }

    public CtFileNameParsingTableModel getParsedParts() {
        return model;
    }

    public boolean isParsed() {
        return model.validateEntries();
    }

    public String getParsingValidationResult() {
        return model.getValidationResult();
    }
}
