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

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import au.com.nicta.ct.db.*;
import au.com.nicta.ct.db.hibernate.CtGroupsExperiments;
import au.com.nicta.ct.db.hibernate.CtUsers;
import au.com.nicta.ct.db.hibernate.CtUsersGroups;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import java.io.File;
import java.util.Set;
import javax.swing.JFrame;

public class CtDBProgressDialog extends JDialog {

    private static final int  _updateInterval = 20;

    private Timer              taskTimer      = null;
    private CtDatabaseWorker   task           = null;
    private JProgressBar       progressBar    = null;
    private JLabel             statusLabel    = null;
    private JFrame             containerFrame;

    private CtSetupModel       setupModel;

    //not used yet, but can be used to have interaction
    //with the user
    private String CMD_CANCEL = "cmd.cancel";
    private String CMD_DONE   = "cmd.done";
    private String CMD_STOP   = "cmd.stop";

    private CtSetupWorkerProcess setupWorker;


//    Session s;
//    Transaction t;

    private int taskCounter;
    
    public CtDBProgressDialog( JFrame parent, CtSetupModel setupModel,CtSetupWorkerProcess setupWorker ) {

        super( parent , true);

        this.containerFrame = parent;
        this.setupModel     = setupModel;
        this.setupWorker    = setupWorker;


//        Session s = CtSession.Current();
//        t = s.beginTransaction();
//        t.begin();
        

        initComponents();
        initTask();

        this.setSize( 300 , 120 );
        this.setLocation( parent.getX()+(int) parent.getWidth()/2 - 150 , parent.getY()+(int) parent.getHeight()/2 - 60 );

        this.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        this.setVisible( true );
    }

    private void initTask() {

        if(task != null)  {
            task = null;
        }

        task = new CtDatabaseWorker( setupModel.experimentName, setupModel.experimentURI, setupModel.imageSourceDirectory );

        progressBar.setMaximum( 100 );

        setTitle( "Copying data ... " ) ;

        //t = s.getTransaction();

        // start a timer to watch the task and update the progress
        taskTimer = new Timer( CtDBProgressDialog._updateInterval, new ActionListener() {
            
            public void actionPerformed(ActionEvent event) {

                    //not very elegant way of doing things, but ...
                    if( taskCounter == 0 ) {
                        Transaction t = null;
                        try {
                            Session s = CtSession.Current();
                            t = s.getTransaction();
                            t.begin();
                            System.out.println( "Experiment : " + t.isActive() );
                            task.saveExperiment( s );
                            taskCounter++;
                            progressBar.setValue( 10 );
                            statusLabel.setText(" Copying Image data");
                            System.out.println( "Saving of Experiment Finished");

                        }
                        catch( HibernateException he ) {
                            if( t != null) {
                                t.rollback();
                            }

                            he.printStackTrace();
                            //in this case we need to delete the experiment folder we created
                            String experimentURI = CtApplication.experimentsPath()+File.separator+ setupModel.experimentName;
                            File   destDir       = new File( experimentURI );
                            destDir.delete();
                            javax.swing.JOptionPane.showMessageDialog( containerFrame, "Could not save all data in the database", "Report", javax.swing.JOptionPane.OK_OPTION );
                            setupWorker.setdataSaveResult( false );
                            windowAction( CMD_DONE );
                        }
                    }
                    else if(taskCounter == 1) {
                        System.out.println( "Creating Images");
                        String result = task.createImages( setupModel.imageFileNames );
                        progressBar.setValue( 25 );
                        System.out.println( "Creating Coordinates");
                        result = task.createCoordinatesImagesCoordinates( setupModel.imageNameParts );
                        progressBar.setValue( 75 );

                        System.out.println( "Result String : " + result);
                        
                        if( result.equals( CtMissingCoordinateDetector._missingImageMessage ) ) {

                            int userInput = javax.swing.JOptionPane.showConfirmDialog( containerFrame, result+".Continue creating the experiment?", "Warning",javax.swing.JOptionPane.YES_NO_OPTION );
                            if( userInput == javax.swing.JOptionPane.NO_OPTION ) {

                                String experimentURI = CtApplication.experimentsPath()+File.separator+ setupModel.experimentName;
                                File   destDir       = new File( experimentURI );
                                destDir.delete();

                                try {
                                   //somehow the rollback does not work
                                   //so taking things in hand, deleting the
                                   //created experiment manually.
                                   //May sound trouble for later saving operations
                                   //if rollback really does not work
                                   Session s = CtSession.Current();
                                   s.delete( task.solution );
                                   s.delete( task.experiment );
                                   
                                   //s.delete( task.experiment.getCtSolutionses() );
                                   //t = s.getTransaction();
                                   //t.rollback();
                                }
                                catch( HibernateException heInner ) {

                                }
                                //in this case we need to delete the experiment folder we created
                                

                                setupWorker.setdataSaveResult( false );
                                windowAction( CMD_DONE );
                            }

                            else {
                                //if the user wants to continue with the missing
                                //images, the dummy image name and corresponding
                                //coordinates are to be added to the list
                                task.addMissingImagesANDImagesCoordinates(  setupModel.imageNameParts );
                            }
                        }
                        taskCounter++;
                        progressBar.setValue( 20 );
                        statusLabel.setText(" Copying Image data");
                    }
                    else if(taskCounter == 2) {
                        Transaction t = null;
                        try {
                            Session s = CtSession.Current();
                            t = s.getTransaction();
                            t.begin();
                            
                            task.saveImages( s );
                            progressBar.setValue( 85 );
                            statusLabel.setText(" Copying Coordinates ");

                            task.saveCoordinatesImagesCoordinates( s );
                            taskCounter++;
                            progressBar.setValue( 85 );
                            statusLabel.setText(" Setting coordinate limits " );

                            CtUsers u = (CtUsers)CtObjectDirectory.get( "user" );
                            Set ugs = u.getCtUsersGroupses();

                            for( Object o : ugs ) {
                                CtUsersGroups ug = (CtUsersGroups)o;
                                CtGroupsExperiments ge = new CtGroupsExperiments();
                                ge.setCtGroups( ug.getCtGroups() );
                                ge.setCtExperiments( task.experiment );
                                CtSession.Current().save( ge );
                            }
                            // determine the limits for the experiment:
//                            CtLimits l = new CtLimits();
//                            l.updateLimits( task.experiment );
                            t.commit();
                            s.flush();
//                            s.flush();
                            setupWorker.setExperiment( task.experiment );
                            progressBar.setValue( 100 );
                            statusLabel.setText( "Done" );
                            setupWorker.setdataSaveResult( true );
                        }
                        catch( HibernateException he ) {

                            //in this case we need to delete the experiment folder we created
                            String experimentURI = CtApplication.experimentsPath()+File.separator+ setupModel.experimentName;
                            File   destDir       = new File( experimentURI );
                            destDir.delete();


                            he.printStackTrace();
                            Session s = CtSession.Current();
//                            t = s.getTransaction();
                            System.out.println( "Exception : " + t.isActive() );
                            if( t != null) {
                                t.rollback();
                            }


                            javax.swing.JOptionPane.showMessageDialog( containerFrame, "Could not save all data in the database", "Report", javax.swing.JOptionPane.OK_OPTION );
                            setupWorker.setdataSaveResult( false );
                            windowAction( CMD_DONE );
                        }
                        
                    }
                    if( progressBar.getValue() >= 100 ) {
                        setupWorker.setdataSaveResult( true );
                        windowAction( CMD_DONE );
                    }
                }
        });
        taskTimer.setInitialDelay( 0 );
        taskTimer.start();
    }

    private void stopTaskAndTimer() {
        return;
    }

    private void initComponents() {

        // window properties
        setTitle( "Copying data ... " ) ;

        progressBar = new JProgressBar();
        progressBar.setMinimum( 0 );
        progressBar.setValue( 0 );
        progressBar.setPreferredSize( new Dimension( 220, 14 ) );

        taskCounter = 0;


        statusLabel = new JLabel( "Copying Data ... " );

        Container c = this.getContentPane();

        GroupLayout pnlContainerLayout = new GroupLayout( c );
        this.setLayout( pnlContainerLayout );

        pnlContainerLayout.setAutoCreateGaps( true );
        pnlContainerLayout.setAutoCreateContainerGaps( true );

        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createSequentialGroup()
                .addComponent( statusLabel )
                .addComponent( progressBar )
         );

        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup( GroupLayout.Alignment.LEADING, true )
                .addComponent( statusLabel )
                .addComponent( progressBar )
         );

        this.pack();


        addWindowListener( new WindowAdapter() {
            @Override public void windowClosing( WindowEvent event ) {
                //windowAction( CMD_CANCEL );
            }
        });
    }

    private void windowAction( Object actionCommand ) {
        String cmd = null;
        if( actionCommand != null ) {
            if( actionCommand instanceof ActionEvent ) {
                cmd = ( (ActionEvent) actionCommand ).getActionCommand();
            }
            else {
                cmd = actionCommand.toString();
            }
        }
        if( cmd == null ) {
             // do nothing
        }
        else if( cmd.equals( CMD_CANCEL ) ) {

        }
        else if( cmd.equals( CMD_DONE ) ) {
            System.out.println( " Inside CMD_DONE  ");
            taskTimer.stop();
            task = null;
            this.dispose();
        }
        else if( cmd.equals( CMD_STOP ) ) {

        }
     }
}

