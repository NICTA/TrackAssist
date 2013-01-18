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

package au.com.nicta.ct.ui.swing.components;

import au.com.nicta.ct.ui.swing.components.CtBackgroundImagePanel;
import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.ui.swing.components.CtSplashScreen;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

/**
 * App. main frame with standard features, layout, menus etc.
 * @author davidjr
 */
public class CtPageFrame extends JFrame implements ActionListener {

    public static final String OBJECT_KEY = "frame";
    public static final String SPLASH = "splash.png";
    public static final String BACKGROUND = "background.jpg";
    public static final String ICON  = "nicta_icon_transparent.png";

    public static CtPageFrame find() {
        return (CtPageFrame)CtObjectDirectory.get( CtPageFrame.OBJECT_KEY );
    }

    public CtPageFrame() {

        super();
        
        setSystemLookAndFeel();

        addWindowListener( new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                setExtendedState( getExtendedState() | JFrame.MAXIMIZED_BOTH );
            }
        } );
        
        CtObjectDirectory.put( CtPageFrame.OBJECT_KEY, this );

//        String s = CtApplication.datafile( SPLASH );
//        CtSplashScreen ss = new CtSplashScreen( this, s, CtApplication.TITLE+" "+CtApplication.VERSION, true );
        showSplashScreen( true );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setIconImage( new ImageIcon( CtApplication.datafile( ICON ) ).getImage() );
        setTitle( CtApplication.TITLE+" "+CtApplication.VERSION );
//        setExtendedState( Frame.MAXIMIZED_BOTH );
//        setExtendedState( getExtendedState() | JFrame.MAXIMIZED_BOTH );
        //setBackground( Color.WHITE );
//        SwingUtilities.updateComponentTreeUI( this );

        setBackground( this );
    }

    public static void showSplashScreen( boolean timer ) {
        CtPageFrame f = CtPageFrame.find();
        String s = CtApplication.datafile( SPLASH );
        CtSplashScreen ss = new CtSplashScreen( f, s, CtApplication.TITLE+" "+CtApplication.VERSION, timer );
    }

    public static void setBackground( JFrame f ) {
        try {
//            URL url = System.ggetResource( "background.jpg" );
            CtBackgroundImagePanel backgroundImage = new CtBackgroundImagePanel();// CtApplication.datafile( BACKGROUND ), true );
            backgroundImage.setOpaque( true );
            f.setContentPane( backgroundImage );
        }
        catch( IOException ioe ) {
            System.err.println( ioe );
        }
    }

    public static void showWaitCursor() {
        find().setCursor( new Cursor( Cursor.WAIT_CURSOR ) ); // BUG: If there is an error, the cursor is not restored. Make this bug impossible
    }
    
    public static void showDefaultCursor() {
        find().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) ); // BUG: If there is an error, the cursor is not restored. Make this bug impossible
    }

    public static void error( JComponent c, String message ) {
        javax.swing.JOptionPane.showMessageDialog( c, message , "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
    }

    public static void info( JComponent c, String message ) {
        javax.swing.JOptionPane.showMessageDialog( c, message , "Information", javax.swing.JOptionPane.INFORMATION_MESSAGE );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        setPage();
    }

    public JMenuBar getOrCreateMenuBar() {
        JMenuBar mb = getJMenuBar();

        if( mb == null ) {
            mb = new JMenuBar();
//            mb.setBackground( Color.WHITE );
            setJMenuBar( mb );
        }

        return mb;
    }

    public void clearMenuBar() {
        JMenuBar mb = getJMenuBar();

        if( mb != null ) {
            setJMenuBar( null );
        }
    }

    public void setPage() {
        CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );

//        clearMenuBar();

        CtPage p = pc.getPage();

        if( p == null ) {
            return;
        }

        pc.set( this, p );
    }

    public void setSystemLookAndFeel() {
        try { // Use the system look and feel for the swing application
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch( Exception e ) {
            System.err.println( "Can't set system look and feel." );
            e.printStackTrace();
        }
    }
    
    @Override public void setVisible( boolean b ) {
        super.setVisible( b );
    }
}
