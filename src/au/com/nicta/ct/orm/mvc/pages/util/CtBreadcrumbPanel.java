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

import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.desktop.CtFileOpener;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtBreadcrumbPanel extends JPanel {

    public static final String HELP_FILE = "NICTA Track Assist User Guide.pdf";
    
    public static final int BUTTON_SEPARATION = 20;

    public CtBreadcrumbPanel() {
//        super( new FlowLayout() );
        super( new BorderLayout() );

        JLabel logo = new JLabel( CtStyle.h1( CtApplication.NAME ) );
               logo.setIcon( new ImageIcon( CtApplication.datafile( "nicta_icon.png" ) ) );
//               logo.setOpaque( true );
//               logo.setBackground( Color.WHITE );
               
        JButton help = new JButton( "Help" );
        JButton about = new JButton( "About" );
//        JButton logout = new JButton( "Logout" );
        JButton experiments = new JButton( "Experiments" );

        Object o = CtObjectDirectory.get( "user" );
        
        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        buttons.setOpaque( true );
        buttons.setBackground( CtConstants.NictaYellow );
        buttons.add( help );
        buttons.add( about );
        
        if( o != null ) {
//            buttons.add( logout );
            buttons.add( experiments );
        }

        CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );

        help .addActionListener( new ActionListener() {
            @Override public void actionPerformed( ActionEvent ae ) {
                String filePath = CtApplication.datafile( HELP_FILE );
                CtFileOpener.open( filePath );
            }
        });
        about.addActionListener( new ActionListener() {
            @Override public void actionPerformed( ActionEvent ae ) {
                CtPageFrame.showSplashScreen( false );
            }
        });

        if( o != null ) {
//            logout     .setActionCommand( "logout" );
            experiments.setActionCommand( "select-experiment" );
        }
//        help       .addActionListener( pc );
//        logout     .addActionListener( pc );
        experiments.addActionListener( pc );

        setOpaque( true );
        setBackground( Color.WHITE );

//        add( logo );
//        add( Box.createHorizontalStrut( BUTTON_SEPARATION ) );
//        add( buttons );
        add( logo, BorderLayout.WEST );
        add( Box.createHorizontalStrut( BUTTON_SEPARATION ), BorderLayout.CENTER );
        add( buttons, BorderLayout.EAST );
    }
}
