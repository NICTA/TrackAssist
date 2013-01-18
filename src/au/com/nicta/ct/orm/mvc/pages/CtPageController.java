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

package au.com.nicta.ct.orm.mvc.pages;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.orm.mvc.CtController;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtPageController extends CtController {

    public static final String ACTION_TRANSITION = "transition";

    public CtPageController() {
        this( new CtPageModel() );
    }
    
    public CtPageController( CtPageModel pm ) {
        super( pm, null );
        CtObjectDirectory.put( "page-controller", this );
    }

    public CtPage getPage() {
        CtPageModel pm = (CtPageModel)_m;
        return pm.getPage();
    }

    public void transition( String key ) { // go direct to this page
        CtPageModel pm = (CtPageModel)_m;
//        try {
            pm.transition( key );
//        }
//        catch( NullPointerException npe ) {
//            System.err.println( "ERROR: Transition exception, defaulting to error page:" );
//            String key2 = CtKeyValueProperties.getValue( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
//            if( !key2.equals( key ) ) {
//                transition( key2 );
//            }
//            else {
//                System.err.println( "ERROR: Error page transition failed." );
//            }
//        }
        fireModelChanged();
    }

    public void transition() {
        CtPageModel pm = (CtPageModel)_m;
//        try {
            pm.transition();
//        }
//        catch( NullPointerException npe ) {
//            System.err.println( "ERROR: Transition exception, defaulting to error page:" );
//            npe.printStackTrace();
//            System.exit( -1 );
////            String key = CtKeyValueProperties.getValue( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
////            transition( key );
//        }
        fireModelChanged();
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();

        if( s.equals( CtModel.ACTION_MODEL_CHANGED ) ) {
            super.actionPerformed( ae );
        }
        else {
//            try {
                if( s.equals( ACTION_TRANSITION ) ) {
                    transition();
                }
                else if( CtPages.hasPage( s ) ) {
                    transition( s );
                }
//            }
//            catch( NullPointerException npe ) {
//                transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
//            }
        }
    }

    public void set( CtPageFrame f, CtPage p ) {
        JComponent head = p.head();
        JPanel body = p.body();
        JPanel foot = p.foot();

        JPanel cp = (JPanel)f.getContentPane();

        cp.removeAll(); // this removes the old content
        cp.setLayout( new BorderLayout() );
        cp.add( body, BorderLayout.CENTER );

        if( head != null ) cp.add( head, BorderLayout.NORTH );            
        if( foot != null ) cp.add( foot, BorderLayout.SOUTH );

        if( !f.isVisible() ) {
            f.pack();
            f.setVisible( true );
        }
        else {
            cp.revalidate();
            cp.requestFocusInWindow(); // This requests focus for each page
//            pack();
//            revalidate();
        }
    }

}
