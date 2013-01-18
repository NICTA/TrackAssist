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

import au.com.nicta.ct.db.CtCredentials;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtUsers;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author davidjr
 */
public class CtLoginPage extends JPanel implements ActionListener, CtPage {

    public JPanel head() {
        return new CtHeaderPanel( "Login" );
    }

    public JPanel foot() {
        return new CtFooterPanel();
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return "login";
    }
    
    public String state() {
        Object o = CtObjectDirectory.get( "user" );

        if( o == null ) {
            return CtPageStates.NG;
        }
        else {
            return CtPageStates.OK;
        }
    }

    public void onExit() {}

    public static final String COMMAND_LOGIN ="login";

    JLabel _username;
    JLabel _password;
    JTextField _usernameField;
    JPasswordField _passwordField;

    public CtLoginPage() {
        super();

//        JPanel body = new JPanel();
        setOpaque( false );
        setLayout( new GridBagLayout() );
        JPanel grid = new JPanel();
        GroupLayout gl = new GroupLayout( grid );
        grid.setLayout( gl );
        grid.setOpaque( false );
        gl.setAutoCreateGaps( true );
        gl.setAutoCreateContainerGaps( true );

        
        _username = new JLabel( CtStyle.h3( "Username:" ) );
        _password = new JLabel( CtStyle.h3( "Password:" ) );

        _username.setHorizontalAlignment( SwingConstants.RIGHT );
        _password.setHorizontalAlignment( SwingConstants.RIGHT );
        _username.setMaximumSize( _username.getSize() );
        _password.setMaximumSize( _password.getSize() );
        
        _usernameField = new JTextField();
        _passwordField = new JPasswordField();

        Dimension maxSize = _usernameField.getMaximumSize();
                  maxSize.width  = _username.getPreferredSize().width * 2;
                  maxSize.height = _usernameField.getPreferredSize().height;
        _usernameField.setMaximumSize( maxSize );
        _passwordField.setMaximumSize( maxSize );
        _usernameField.setMinimumSize( maxSize );
        _passwordField.setMinimumSize( maxSize );

        JButton login = new JButton( "Login" );
                login.setActionCommand( COMMAND_LOGIN );
                login.addActionListener( this );
                
        gl.setHorizontalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent(_username)
                   .addComponent(_password) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent(_usernameField)
                   .addComponent(_passwordField) )
        );

        gl.setVerticalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent(_username)
                   .addComponent(_usernameField) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent(_password)
                   .addComponent(_passwordField) )
        );

        _passwordField.setActionCommand( COMMAND_LOGIN );
        _passwordField.addActionListener( this );

        JPanel box = new JPanel();
        box.setLayout( new BoxLayout( box, BoxLayout.PAGE_AXIS ) );
        box.setOpaque( false );
        box.add( grid );
        box.add( login );

        GridBagConstraints c = new GridBagConstraints();
        add( box, c );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();

        if( s.equals( COMMAND_LOGIN ) ) {
            String username = _usernameField.getText();
            String password = new String( _passwordField.getPassword() );

            doLogin( username, password );

            /// lookup in the properties the next page after login.. apply it
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
        }
    }

    public static void doLogin( String username, String password ) {
        CtUsers u = CtCredentials.user( username, password );

        if( u == null ) {
            JOptionPane.showMessageDialog( CtPageFrame.find(), "Invalid username/password combination." );
        }
        else {
            CtObjectDirectory.put( "user", u );
        }
    }
}
