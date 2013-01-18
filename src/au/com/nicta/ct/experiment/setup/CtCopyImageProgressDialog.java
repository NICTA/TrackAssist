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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class CtCopyImageProgressDialog extends JDialog {

    private static final int  _updateInterval = 20;

    private Timer              taskTimer      = null;
    private CtCopyImagesWorker task           = null;
    private JProgressBar       progressBar    = null;
    private JLabel             statusLabel    = null;
    private String             sourceDir      = "";
    private String             destinationDir = "";

    //not used yet, but can be used to have interaction
    //with the user
    private String CMD_CANCEL = "cmd.cancel";
    private String CMD_DONE   = "cmd.done";
    private String CMD_STOP   = "cmd.stop";

    public CtCopyImageProgressDialog( JFrame parent, String sourceDir, String destinationDir ) {

        super( parent , true);

        this.sourceDir      = sourceDir;
        this.destinationDir = destinationDir;

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

        task = new CtCopyImagesWorker();
        task.setDestDir  ( this.destinationDir );
        task.setSourceDir( this.sourceDir      );

        progressBar.setMaximum( task.getTotalFileCount() );

        setTitle( "Copying Images ... " ) ;
        // start a timer to watch the task and update the progress
        taskTimer = new Timer( CtCopyImageProgressDialog._updateInterval, new ActionListener() {

            public void actionPerformed(ActionEvent event) {

                int completedImages = task.copyAllImages();
                progressBar.setValue( completedImages );
                statusLabel.setText ( completedImages + " of "+ task.getTotalFileCount() + " images copied" );
                if( completedImages >= task.getTotalFileCount() ) {
                    task = null;
                    taskTimer.stop();
                    windowAction( CMD_DONE );
                }
            }

        });
        taskTimer.setInitialDelay( 0 );
        taskTimer.start();
    }

    private void stopTaskAndTimer() {
        //not used yet, but potentially in future
        return;
    }

    private void initComponents() {

        // window properties
        setTitle( "Copying Images ... " ) ;

        progressBar = new JProgressBar();
        progressBar.setMinimum( 0 );
        progressBar.setValue( 0 );
        progressBar.setPreferredSize( new Dimension( 220, 14 ) );

        
        statusLabel = new JLabel( "Copying Image ... " );

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
        else if( cmd.equals(CMD_DONE) ) {
             task = null;
             this.dispose();
        }
        else if( cmd.equals(CMD_STOP) ) {

        }
     }
}
