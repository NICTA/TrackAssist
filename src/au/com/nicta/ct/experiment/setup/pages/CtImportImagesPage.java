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

import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtUsers;
import au.com.nicta.ct.experiment.setup.CtFileNamePanel;
import au.com.nicta.ct.experiment.setup.CtSetupController;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtWizardPage;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtImportImagesPage extends CtWizardPage {

    CtFileNamePanel _fnp;
    
    public CtImportImagesPage() {
        super( "Import Images", "import-images", CtPageStates.NG, false );
    }

    @Override public JPanel createWizardPanel() {
        _fnp = new CtFileNamePanel();
        return _fnp;
    }

    @Override public String allow( String actionCommand ) {
        if( actionCommand.equals( CtPageStates.NEXT ) ) {
            if( !_fnp.isImageSourceValid() ) {
                return new String( "You must specify a directory containing images to import." );
            }
            CtSetupController sc = CtSetupController.instance();

            sc.setSourceDirectory( _fnp.getSourceDirectory() );
            sc.setFileNames( _fnp.getImageFilenames() );
        }
        return null;
    }

}
