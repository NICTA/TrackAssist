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


import au.com.nicta.ct.experiment.setup.util.CtImageFileFilter;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.DefaultListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;

/**
 *
 * @author rch
 */

public class CtFileNamePanel extends JPanel {

    private JButton     browse;
    private JList       fileNameList;
    private JTextField  textFieldImageSourceDir;
    private JLabel      labelImageSourceDir;
    private JLabel      labelImageFileList;
    private JScrollPane fileListHolderScrollPanel;

    public CtFileNamePanel() {

        textFieldImageSourceDir   = new JTextField ();
        labelImageSourceDir       = new JLabel     ();
        browse                    = new JButton    ();
        labelImageFileList        = new JLabel     ();
   //     fileListHolderScrollPanel;// = new JScrollPane();
        fileNameList              = new JList      ();

        labelImageSourceDir.setText( CtStyle.h3( "Image Source Location" ) );
        labelImageFileList.setText( CtStyle.h3( "Selected Files" ) );
        browse.setText( "Browse" );

        fileListHolderScrollPanel = new JScrollPane( fileNameList );
//        fileListHolderScrollPanel.setViewportView( fileNameList );
//        CtConstants.setPreferredSize( fileNameList );

        browse.addMouseListener( new MouseAdapter() {
                @Override public void mouseClicked( MouseEvent m ) {
                    chooseSourceDirectory( );
                }
            }
        );

        textFieldImageSourceDir.addKeyListener( new KeyAdapter() {
                @Override public void keyPressed( KeyEvent evt ) {
                    if(  evt.getKeyCode() == KeyEvent.VK_ENTER ) {
                        setSourceDirectory();
                    }
                }
            }
        );

        CtSetupController sc = CtSetupController.instance();

        //for back situation
        textFieldImageSourceDir.setText( sc.getSourceDirectory() ); // for "back" situation
        //for back situation, the file list also needs to be populated
        if( ! ( textFieldImageSourceDir.getText() ).isEmpty() ) {
            populateFileListBox();
        }

        JPanel flow = new JPanel( new FlowLayout() );
        flow.add( browse );
        flow.setBackground(CtConstants.NictaYellow );

        JPanel source = new JPanel( new BorderLayout() );
        source.setOpaque( false );
        source.add( labelImageSourceDir, BorderLayout.WEST );
        source.add( flow, BorderLayout.EAST );


//        JPanel both = new JPanel( new BorderLayout() );
//        both.setOpaque( false );
//        both.add( source, BorderLayout.NORTH );
//        both.add( textFieldImageSourceDir, BorderLayout.CENTER );

//        JPanel files = new JPanel( new BorderLayout() );
//        files.setOpaque( false );
//        files.add( labelImageFileList, BorderLayout.NORTH );
//        files.add( fileListHolderScrollPanel, BorderLayout.CENTER );
//
//        JPanel all = new JPanel( new BorderLayout() );
//        all.setOpaque( false );
//        all.add( both, BorderLayout.NORTH );
//        all.add( files, BorderLayout.CENTER );

        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( source, false ) );
        c.add( new CtComponentResizePolicy( textFieldImageSourceDir, false ) );
        c.add( new CtComponentResizePolicy( labelImageFileList, false ) );
        c.add( new CtComponentResizePolicy( fileListHolderScrollPanel, true ) );
        JPanel dialog = CtDialogPanel.create( c );

        setLayout( new BorderLayout() );
        add( dialog, BorderLayout.CENTER );
//        add( all, BorderLayout.CENTER );
        setOpaque( false );
    }

    public void chooseSourceDirectory() {

        JFileChooser dlgDirChooser         = new JFileChooser();
        dlgDirChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        //if something is returned we need to check whether
        //it is a valid directory
        int returnVal  =  dlgDirChooser.showDialog(  this, "Choose Directory"  );
        if( returnVal == JFileChooser.APPROVE_OPTION ) {
            String chosenDirectory=( dlgDirChooser.getSelectedFile() ).getAbsolutePath( );
            if(  chosenDirectory.isEmpty() ) {
                textFieldImageSourceDir.setText( "" );
            }
            else {
                ( (JFrame) ( this.getTopLevelAncestor() ) ).setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                boolean isValid = checkSourceDirectory( chosenDirectory );
                ( this.getTopLevelAncestor() ).setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                if(  !isValid  ) {
                    CtPageFrame.error( this, "No image files found at that location." );
                    textFieldImageSourceDir.setText( "" );
                }
                else {
                    textFieldImageSourceDir.setText( chosenDirectory );
                    populateFileListBox();
                    ( this.getTopLevelAncestor() ).setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                }
                ( (JFrame) ( this.getTopLevelAncestor() ) ).setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
         }
         else {
            CtPageFrame.error( this, "No image files found at that location." );
            textFieldImageSourceDir.setText( "" );
         }
        return;
    }

    // To check whether the supplied text is actually
    // a directory and does contain at least one image file
    private boolean checkSourceDirectory( String chosenDirectory ) {

        if( chosenDirectory.equals( "" ) || chosenDirectory.isEmpty() ) {
            return false;
        }

//        ( (JFrame) ( this.getTopLevelAncestor() ) ).setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        CtImageFileFilter iNF  = new CtImageFileFilter();
        File file              = new File( chosenDirectory );
        try {
            String fileNames[] = file.list(  iNF  );

            if( fileNames.length > 0 ) {
               return true;
            }
            else {
                return false;
            }
        }
        catch( Exception e ) {
            return false;
        }
    }

    public void populateFileListBox() {

        DefaultListModel lM    = new DefaultListModel();

        String chosenDirectory = textFieldImageSourceDir.getText();
        
        CtImageFileFilter iNF  = new CtImageFileFilter();
        File file              = new File( chosenDirectory );
        String fileNames[]     = file.list( iNF );

        int totalFiles         = fileNames.length;

        for( int fileNo=0 ; fileNo < totalFiles ; fileNo++ ) {
            lM.addElement( fileNames[ fileNo ] );
        }

        fileNameList.setModel( lM );
        return;
    }

    public String getSourceDirectory() {
        return textFieldImageSourceDir.getText();
    }

    public void setSourceDirectory() {

        String chosenDirectory  = textFieldImageSourceDir.getText();
        if( chosenDirectory.isEmpty() ) {
           textFieldImageSourceDir.setText( "" );
        }
        else{

           boolean isValid = checkSourceDirectory(  chosenDirectory  );

           if(  !isValid  ) {
                CtPageFrame.error( this, "No image files found at that location." );
                textFieldImageSourceDir.setText( "" );
           }
           else {
                textFieldImageSourceDir.setText( chosenDirectory );
                populateFileListBox();
            }
        }
        return;
    }

    public boolean isImageSourceValid(){
        if(   !( textFieldImageSourceDir.getText() ).isEmpty()
            && ( fileNameList.getModel() ).getSize() > 0 ) {

            return true;
        }
        else {
            return false;
        }
    }

    public String getSampleFile() {
        DefaultListModel lm = (DefaultListModel ) fileNameList.getModel();
        return ( String ) lm.get( 0 );
    }

    public Collection< String > getImageFilenames() {
        DefaultListModel dlm = (DefaultListModel)fileNameList.getModel();
        ArrayList< String > c = new ArrayList< String >();

        int images = dlm.size();

        for( int n=0; n < images; n++ ) {
            c.add( (String)dlm.get( n ) );
        }

        return c;
    }

}
