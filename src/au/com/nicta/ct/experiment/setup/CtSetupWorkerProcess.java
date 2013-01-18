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

import au.com.nicta.ct.db.*;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import java.awt.Cursor;
import org.hibernate.HibernateException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import javax.swing.JFrame;



/**
 * - does multiple steps against DB and/or filesystem asynchronously
 * - has an error retrieval mechanism
 * - does DB as 1 transaction and rolls back if error
 * (in fact all operations should roll back on error)
 *
 * @author rch
 */


public class CtSetupWorkerProcess implements Runnable {

    public static final String _completionMessage = "Experiment was successfully created.";

    private CtSetupModel setupModel;
    private List< String > messages;
    private JFrame containerFrame;
    private CtExperiments created;

    public boolean allDataSavedSuccessfully = false;

    public CtSetupWorkerProcess( CtSetupModel setupModel, JFrame f ) {

        this.setupModel     = setupModel;
        this.containerFrame = f;

        messages = new ArrayList< String >();

    }

    public CtExperiments created() {
        return created;
    }
    
    public String getLatestMessage() {
        return messages.get( 0 );
    }

    public List< String > getAllMessages() {
        return messages;
    }

    public void addNewMessage( String msg ) {
        messages.add( 0, msg );
    }

    private void printAllMessages() {
        for( int index = 0 ; index < messages.size() ; index++) {
            System.out.println( messages.get( index ) );
        }
    }

    public boolean createExperimentFolder() {

        boolean result = validateExperimentName();

        setupModel.experimentURI = "";

        if( result ) {

            String path = CtApplication.createApplicationPath();

            String uri = path+File.separator + setupModel.experimentName;
            File f = new File( uri );

            try {
                if( !f.mkdir() ) {
                    throw new Exception( "ERROR: Could not create experiment directory." );
                }
                else {
                    setupModel.experimentURI = uri;
                    result = true;
                }
            }
            catch( Exception e ) {
                addNewMessage( "Could not create directory for experiment." );
                result = false;
            }
        }
        return result;
    }

    public boolean validateExperimentName() {

        String experimentName = setupModel.experimentName;
        String experimentURI  = CtApplication.experimentsPath()+File.separator+ experimentName;

        if( ( experimentName ).isEmpty() ) {
            addNewMessage( "Experiment name is empty." );
            return false;
        }
        try {

            String query = "from CtExperiments where name ='\""+experimentName+"\"'";
            //query       += " or uri = '" + experimentURI2 + "'";

            CtExperiments e = ( CtExperiments ) CtSession.getObject( query );

            if( e == null ) {
                addNewMessage( "Experiment does not exist previously.");
                return true;
            }
            else {
                addNewMessage( "Experiment with the same name and/or URI already exists.");
                return false;
            }
        }
        catch(  HibernateException he ) {
            addNewMessage( "Error in retreiving experiment from database.");
            he.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        containerFrame.setCursor( new Cursor( Cursor.WAIT_CURSOR ) ); // BUG: If there is an error, the cursor is not restored. Make this bug impossible
        created = null;

        //first attemp to create the experiment URI
        boolean result = createExperimentFolder();

        if( result ) {
            CtDBProgressDialog dbProgressDialog = new CtDBProgressDialog( containerFrame, setupModel, this );

            if( this.allDataSavedSuccessfully ) {
                CtCopyImageProgressDialog pd  = new CtCopyImageProgressDialog( containerFrame, setupModel.imageSourceDirectory , setupModel.experimentURI );
                addNewMessage( CtSetupWorkerProcess._completionMessage );
            }
            else {
                addNewMessage( "All data could not be saved" );
            }
            
            javax.swing.JOptionPane.showMessageDialog( containerFrame, getLatestMessage(), "Report", javax.swing.JOptionPane.INFORMATION_MESSAGE );

        }
        else {
            //javax.swing.JOptionPane.showMessageDialog( containerFrame, getLatestMessage(), "Report", javax.swing.JOptionPane.OK_OPTION );
            printAllMessages();

        }
        containerFrame.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) ); // BUG: If there is an error, the cursor is not restored. Make this bug impossible
        return;

    }

    public void setdataSaveResult( boolean result ) {
        allDataSavedSuccessfully = result;
    }

    public void setExperiment( CtExperiments e ) {
        created = e;
    }
}
