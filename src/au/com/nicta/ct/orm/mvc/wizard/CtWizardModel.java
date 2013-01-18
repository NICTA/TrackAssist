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

import au.com.nicta.ct.orm.mvc.CtModel;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtWizardModel extends CtModel {

    public static final String HELP = "Help";
    public static final String BACK = "Back";
    public static final String NEXT = "Next";
    public static final String DONE = "Done";
    public static final String CANCEL = "Cancel";

    public static final String ACTION_MODEL_CHANGED = "Model Changed";
    public static final String ACTION_PAGE_CHANGED = "Page Changed";
    public static final String ACTION_EDIT_DONE   = "Edit Done";
    public static final String ACTION_EDIT_CANCEL = "Edit Cancel";

    public static final int STATE_ACTIVE = 0;
    public static final int STATE_DONE   = 1;
    public static final int STATE_CANCEL = 2;

    protected HashMap< Object, CtWizardPage > _pages = new HashMap< Object, CtWizardPage >();
    protected CtWizardPage _page;
    protected CtWizardPage _page0;

    protected int _state = STATE_ACTIVE;
    
    public CtWizardModel() {

    }

    public boolean isDone() {
        return false;
    }
    
    public boolean canCancel() {
        return false;
    }
//    public boolean active() {
//        return( _state == STATE_ACTIVE );
//    }
//
//    public boolean finished() {
//        return( !active() );
////        if(    ( _state == STATE_DONE   )
////            || ( _state == STATE_CANCEL ) ) {
////            return true;
////        }
////        return false;
//    }

    public int getState() {
        return _state;
    }
    
    public void setState( int state ) {
        _state = state;
    }
    
    public void addPage( String title, JPanel p ) {
        addPage( new CtWizardPage( title, p ) );
    }

    public void addPage( CtWizardPage wp ) {
        Object id = wp.id();
        JComponent c = wp._page;
        _pages.put( id, wp );

        int pages = _pages.size();
        if( pages == 1 ) {
            _page0 = wp;
            _page = _page0;
        }
        else { // pages > 1
            // get last and prev, link them
            CtWizardPage back = getPage( pages -2 );
            wp._back = back;
            back._next = wp;
        }
    }

    public int pages() {
        return _pages.size();
    }

    public CtWizardPage getPage( int index ) {

        int i = 0;

        CtWizardPage p = _page0;

        while( p != null  ) {

            if( i == index ) {
                return p;
            }

            p = p.next();
            ++i;
        }

        return null;
    }

    public CtWizardPage getPage() {
        return _page;
    }
    
    public void setPage( Object id ) {
        CtWizardPage wp = _pages.get( id );
        _page = wp;
    }

}
