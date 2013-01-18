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

package au.com.nicta.ct.experiment.setup.pages;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.experiment.setup.CtExperimentNamePanel;
import au.com.nicta.ct.experiment.setup.CtSetupController;
import au.com.nicta.ct.experiment.setup.CtSetupModel;
import au.com.nicta.ct.experiment.setup.CtSetupWorkerProcess;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtWizardPage;
import au.com.nicta.ct.experiment.CtExperimentController;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtCreateExperimentPage extends CtWizardPage {

    CtExperimentNamePanel _enp;

    public CtCreateExperimentPage() {
        super( "Create Experiment", "create-experiment", CtPageStates.NG, true );
    }

    @Override public JPanel createWizardPanel() {
        _enp = new CtExperimentNamePanel( this );
        return _enp;
    }

    @Override public String allow( String actionCommand ) {
        if( actionCommand.equals( CtPageStates.FINISH ) ) {
            if( !_enp.validateExperimentName() ) {
                //return new String( "You must give a name for the experiment, using at least "+CtExperimentNamePanel.MIN_NAME_LENGTH+" ordinary characters." );
                return _enp.getExperimentNameVerificationResult();
            }
            String name = _enp.getExperimentName();

            CtExperiments e = (CtExperiments)CtSession.getObject( " from CtExperiments where name = '"+name+"'" );

            if( e != null ) {
                return new String( "That name is already in use. Please give a unique name for this experiment." );
            }

            CtSetupController sc = CtSetupController.instance();
                              sc.setExperimentName( name );

            CtSetupWorkerProcess swp = new CtSetupWorkerProcess( (CtSetupModel)sc.getModel(), (CtPageFrame)getTopLevelAncestor() );
                                 swp.run();

            e = swp.created();

            if( e != null ) {
                CtExperimentController.set( e );
//                CtObjectDirectory.put( "experiment", e );
            }
            else {
                return new String( "Failed to create an Experiment. Please try again with a different name" );
            }
            
            sc.clear();
        }
        else if( actionCommand.equals( CtPageStates.BACK ) ) {

            String name = _enp.getExperimentName();

            CtSetupController sc = CtSetupController.instance();
                              sc.setExperimentName( name );

        }



        return null;
    }
}

