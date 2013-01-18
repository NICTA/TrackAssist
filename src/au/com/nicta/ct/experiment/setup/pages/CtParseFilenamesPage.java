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
import au.com.nicta.ct.experiment.setup.CtFileNameParsingPanel;
import au.com.nicta.ct.experiment.setup.CtFileNameParsingTableModel;
import au.com.nicta.ct.experiment.setup.CtSetupController;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtWizardPage;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtParseFilenamesPage extends CtWizardPage {

    CtFileNameParsingPanel _fnpp;
    
    public CtParseFilenamesPage() {
        super( "Parse Filenames", "parse-filenames", CtPageStates.NG, false );
    }

    @Override public JPanel createWizardPanel() {
        _fnpp = new CtFileNameParsingPanel();
        return _fnpp;
    }

    @Override public String allow( String actionCommand ) {
        if( actionCommand.equals( CtPageStates.NEXT ) ) {
            if( !_fnpp.isParsed() ) {

                return _fnpp.getParsingValidationResult();
                //return new String( "You must define how the filenames are interpreted before continuing." );
            }

            CtSetupController sc = CtSetupController.instance();
            CtFileNameParsingTableModel fnptm = _fnpp.getParsedParts();

            sc.setParsingTableModel( fnptm );

            if( !sc.createImageNameParts( fnptm ) ) {
                return new String( "The filenames cannot be interpreted given the criteria provided." );
            }

            sc.setImageSourceDirectoryChanged( false );
        }

        if( actionCommand.equals( CtPageStates.BACK ) ) {

            CtSetupController sc = CtSetupController.instance();
            CtFileNameParsingTableModel fnptm = _fnpp.getParsedParts();

            sc.setParsingTableModel( fnptm );
        }
        
        return null;
    }

}
