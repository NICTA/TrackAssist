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

package au.com.nicta.ct.orm.mvc.pages.concrete;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.ui.swing.components.CtTestPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtWizardPage extends JPanel implements ActionListener, CtPage {

    public JPanel head() {
        return new CtHeaderPanel( _title );
    }

    public JPanel foot() {
        return _foot;
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return _key;
    }

    public String state() {
        return _state;
    }

    public void onExit() {}

    protected String _title;
    protected String _key;
    protected String _state;

    protected JPanel _foot;
    protected JButton _back;
    protected JButton _next;
    protected JButton _cancel;
    protected JButton _finish;

    public CtWizardPage( String title, String key, String defaultState, boolean finishButton ) {
        super();

        _title = title;
        _key = key;
        _state = defaultState;

//        setOpaque( false );
        CtPages.setBorder( this );
        setOpaque( false );
        setLayout( new BorderLayout() );

        _foot = new CtFooterPanel();

        JPanel buttons = createWizardButtons( finishButton );
        JPanel middle = createWizardPanel();

//        add( middle, BorderLayout.NORTH );
        add( middle, BorderLayout.CENTER );

        _foot.add( buttons, BorderLayout.EAST );

//        this._
/*        setLayout( new GridBagLayout() );
        JPanel grid = new JPanel();
        GroupLayout gl = new GroupLayout( grid );
        grid.setLayout( gl );
        grid.setOpaque( false );
        gl.setAutoCreateGaps( true );
        gl.setAutoCreateContainerGaps( true );

        JPanel buttons = createWizardButtons( finishButton );
        JPanel middle = createWizardPanel();

        JPanel both = new JPanel();
        both.setOpaque( false );
        both.setLayout( new BorderLayout() );
        both.add( middle, BorderLayout.NORTH );
        both.add( buttons, BorderLayout.SOUTH );

        add( both );*/
    }

    public JPanel createWizardPanel() {
        return new CtTestPanel( "TODO..." );
    }

    public String allow( String actionCommand ) {
        return "TODO";
    }

    public JPanel createWizardButtons( boolean finishButton ) {

        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        buttons.setOpaque( true );
        buttons.setBackground( CtConstants.NictaYellow );

        _back = new JButton( "Back" );
        _back.setActionCommand( CtPageStates.BACK );
        _back.addActionListener( this );

        if( !finishButton ) {
            _next = new JButton( "Next" );
            _next.setActionCommand( CtPageStates.NEXT );
            _next.addActionListener( this );
        }
        
        _cancel = new JButton( "Cancel" );
        _cancel.setActionCommand( CtPageStates.CANCEL );
        _cancel.addActionListener( this );

        if( finishButton ) {
            _finish = new JButton( "Finish" );
            _finish.setActionCommand( CtPageStates.FINISH );
            _finish.addActionListener( this );
        }

        buttons.add( _back );

        if( _next != null ) {
            buttons.add( _next );
        }

        buttons.add( _cancel );

        if( _finish != null ) {
            buttons.add( _finish );
        }

        return buttons;
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        String action = ae.getActionCommand();
        String error = allow( action );
        
        if( error == null ) {
            _state = action;
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
        }
        else {
            CtPageFrame.error( this, error );
        }
    }

}
