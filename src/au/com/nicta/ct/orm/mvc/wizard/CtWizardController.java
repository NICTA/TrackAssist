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

package au.com.nicta.ct.orm.mvc.wizard;

import au.com.nicta.ct.orm.mvc.CtController;
import java.awt.event.ActionEvent;

/**
 *
 * @author davidjr
 */
public class CtWizardController extends CtController {//implements ActionListener {

    public CtWizardController( CtWizardModel wm, CtWizardView wv ) {
        super( wm, wv );
        wv.addActionListener( this );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();

             if( s.equals( CtWizardModel.HELP   ) ) help();
        else if( s.equals( CtWizardModel.BACK   ) ) back();
        else if( s.equals( CtWizardModel.NEXT   ) ) next();
        else if( s.equals( CtWizardModel.DONE   ) ) done();
        else if( s.equals( CtWizardModel.CANCEL ) ) cancel();
    }

    public void help() {
        // TODO
    }

    public void back() {
        CtWizardModel wm = (CtWizardModel)_m;
        CtWizardPage wp = wm.getPage();
        CtWizardPage back = wp.back();

        if( back == null ) {
            return;
        }

        wm.setPage( back.id() );

        createActionEvent( CtWizardModel.ACTION_PAGE_CHANGED );
    }

    public void next() {
        CtWizardModel wm = (CtWizardModel)_m;
        CtWizardPage wp = wm.getPage();

        if( wp == null ) {
            return;
        }
        
        CtWizardPage next = wp.next();

        if( next == null ) {
            return;
        }

        wm.setPage( next.id() );

        createActionEvent( CtWizardModel.ACTION_PAGE_CHANGED );
    }

    public void done() {
        createActionEvent( CtWizardModel.ACTION_EDIT_DONE );
    }

    public void cancel() {
        createActionEvent( CtWizardModel.ACTION_EDIT_CANCEL );
    }
}
