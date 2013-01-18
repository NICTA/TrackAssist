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
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author davidjr
 */
public class CtMessagePage extends JPanel implements ActionListener, CtPage {

    public JPanel head() {
        return new CtHeaderPanel( _title );
    }

    public JPanel foot() {
        return new CtFooterPanel();
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return _key;
    }
    
    public String state() {
        return CtPageStates.OK;
    }

    public void onExit() {}
    
    String _key;
    String _title;
    String _message;

    public JLabel _messageLabel;

    public CtMessagePage( String key, String title, String message ) {
        super();

        _key = key;
        _title = title;
        _message = message;

        setOpaque( false );
        setLayout( new GridBagLayout() );

//        JPanel grid = new JPanel();
//        GroupLayout gl = new GroupLayout( grid );
//        grid.setLayout( gl );
//        grid.setOpaque( false );
//        gl.setAutoCreateGaps( true );
//        gl.setAutoCreateContainerGaps( true );

        _messageLabel = new JLabel( CtStyle.h2( _message ) );
        _messageLabel.setOpaque( false );

        JButton ok = new JButton( "OK" );
        ok.addActionListener( this );

        JLabel blankW = new JLabel( CtStyle.h1(" " ) );
        JLabel blankE = new JLabel( CtStyle.h1(" " ) );
        JPanel blankS = new JPanel( new FlowLayout() );

        blankS.setBackground( CtConstants.NictaYellow );
        blankS.add( ok );
        
        JPanel box = new JPanel( new BorderLayout() );
        box.add( blankE, BorderLayout.EAST );
        box.add( blankW, BorderLayout.WEST );
        box.add( _messageLabel, BorderLayout.CENTER );
//        box.add( ok, BorderLayout.SOUTH );
        box.add( blankS, BorderLayout.SOUTH );
        box.setOpaque( true );
        box.setBackground( Color.WHITE );
        box.setBorder( new LineBorder( Color.BLACK ) );

//        grid.add( box );

        add( box );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
        pc.transition();
    }
}
