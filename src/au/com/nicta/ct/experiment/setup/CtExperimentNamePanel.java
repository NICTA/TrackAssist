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

import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.experiment.setup.pages.CtCreateExperimentPage;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.io.File;
import java.util.ArrayList;



public class CtExperimentNamePanel extends JPanel {

    public static final int MIN_NAME_LENGTH = 3;

    private JLabel     labelExperimentName;
    private JTextField textFieldExperimentName;

    private String experimentNameVerificationResult;
    
    public CtExperimentNamePanel( CtCreateExperimentPage cep ) {

        experimentNameVerificationResult = "";

        labelExperimentName    = new JLabel( CtStyle.h3( "Experiment Name:  " ) );
        textFieldExperimentName= new JTextField( "" );

        CtSetupController sc = CtSetupController.instance();

        textFieldExperimentName.setText( sc.getExperimentName() );

        setLayout( new BorderLayout() );
        setOpaque( false );
//        add( labelExperimentName, BorderLayout.NORTH );
//        add( textFieldExperimentName, BorderLayout.CENTER );

        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( labelExperimentName, false ) );
        c.add( new CtComponentResizePolicy( textFieldExperimentName, false ) );
        JPanel dialog = CtDialogPanel.create( c );
        add( dialog, BorderLayout.CENTER );

        textFieldExperimentName.addActionListener( cep );
        textFieldExperimentName.setActionCommand( CtPageStates.FINISH );
    }
//    private void setMaximumSizes() {
//        textFieldExperimentName.setMaximumSize( new Dimension( Integer.MAX_VALUE,30 ) );
//        labelExperimentName.setMaximumSize( new Dimension( 20 , 30 ) );
//    }

    public boolean validateExperimentName() {

        experimentNameVerificationResult = "";
        
        String name = textFieldExperimentName.getText();

        //the space at the end seems to be a problem.
        //java .mkdir() method allows the directory to be
        //created even with speace at the end and does not
        //reoprt the error. So thought of handling it here
        //with a direct check. This may be vulnerable when used
        //in non-windows system or may cause other problems in
        //non-windows OS. Have to test later.
        if( name.length() < MIN_NAME_LENGTH && name.endsWith( " " )) {
            return false;
        }

        String name2 = name.replaceAll("['\"`?!,;~!@#$%^&*?:|<()]", "" ); // TODO fix for international!

        if( !name2.equals( name ) ) { // ie unchanged by bad
            experimentNameVerificationResult = "You must give a name for the experiment, using at least "+CtExperimentNamePanel.MIN_NAME_LENGTH+" ordinary characters.";
            return false;
        }

        String experimentPath = CtApplication.experimentsPath();
        experimentPath = experimentPath.replace( "/", File.separator );

        String thisExperimentPath = experimentPath + File.separator + name;
        
        //try to create the directory
        File f = new File( thisExperimentPath );
        boolean success = f.mkdir();


        //if it fails
        //if( !success && ( f.getAbsolutePath() ).equals( thisExperimentPath ) ) {
        if( !success ) {
            experimentNameVerificationResult = "A valid experiment directory cannot be created with the name or the directory may already exist";;
            return false;
        }

        //even if it succeeds, we need to check whether
        //the supplied name and the created directory is the same
        File fe = new File( thisExperimentPath );

        if( ( fe.getAbsolutePath() ).equals( thisExperimentPath ) ) {
            //well the name works, but we need to delete the directory
            //here because this is not the right place to create it
            //this creation was for name checking
            
            fe.delete();
            return true;
        }
        else {
            experimentNameVerificationResult = "A valid experiment directory cannot be created with the name";
            return false;
        }

    }
        
    
    public String getExperimentName() {
        return textFieldExperimentName.getText() ;
    }

    public String getExperimentNameVerificationResult() {
        return experimentNameVerificationResult;
    }
}
