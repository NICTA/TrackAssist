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

package au.com.nicta.ct.orm.mvc.pages.util;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.awt.FlowLayout;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtPageNavigationPanel extends JPanel {

    public CtPageNavigationPanel( Collection< CtAbstractPair< String, String > > options ) {
//        super( new FlowLayout() );
        super( new FlowLayout( FlowLayout.CENTER ) );

        setOpaque( true );
        setBackground( CtConstants.NictaYellow );

        CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );

        for( CtAbstractPair< String, String > ap : options ) {
            String option = ap._first;
            String command = ap._second;

            JButton b = new JButton( option );
            b.setActionCommand( command );
            b.addActionListener( pc );

            add( b );
        }

    }

}
