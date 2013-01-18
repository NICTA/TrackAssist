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

import au.com.nicta.ct.orm.mvc.CtView;
import au.com.nicta.ct.orm.mvc.pages.util.CtBreadcrumbPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

/**
 * HEAD (page title) [step N of M]
 * -------------------------------
 * JComponent from the current page
 * -------------------------------
 * LOGO            Wizard controls
 *
 * @author davidjr
 */
public class CtWizardView extends CtWizardDefaultPage implements CtView {

    protected CtWizardModel _wm;

    protected JLabel _title;
    protected JPanel _head;
    protected JPanel _foot;  // BorderLayout [LOGO HELP         BACK/NEXT/DONE]
    protected JPanel _cp;
    protected CardLayout _cl;

    protected int _buttonMask = 0;
    
//    private JComponent _header;
//    private JPanel _viewPanel;
//    private JPanel _cardPanel;
//    private CardLayout _cardLayout;
//
//    private JPanel _buttonPanel;
//    private Box _buttonBox;
//    private int _buttons = 0;
//
//    public static final int END_CANCEL = 0;
//    public static final int END_FINISH = 1;
//
//    private int _ending = 0;

    private JButton   _helpButton;
    private JButton   _backButton;
    private JButton   _nextButton;
    private JButton   _doneButton;
    private JButton _cancelButton; // maybe "finish" or "cancel"

    private ArrayList< JButton > _buttons = new ArrayList< JButton >();
    
    public static final int BUTTON_HELP   = 1 << 0;
    public static final int BUTTON_BACK   = 1 << 1;
    public static final int BUTTON_NEXT   = 1 << 2;
    public static final int BUTTON_CANCEL = 1 << 3;
    public static final int BUTTON_DONE   = 1 << 4;
    public static final int BUTTON_SEPARATION = 20;

    public CtWizardView( CtWizardModel wm, int buttons ) {
        super( "WIZARD" );

        _wm = wm;

        _buttonMask = buttons;
        _buttonMask |= BUTTON_BACK;
        _buttonMask |= BUTTON_NEXT; // gotta have these

        initComponents();
        refresh();
    }

    @Override protected JComponent initHead() {

        // HEADER
        _head = new JPanel();
        _head.setLayout( new BorderLayout() );
        _head.add( new JSeparator(), BorderLayout.SOUTH ); // separate button set from content card
        _head.setBackground( Color.WHITE );
        _head.setOpaque( true );

        _title = new JLabel( "<html><h1>Wizard Pages...</h1></html>" ); // TODO get HTML from database
        _head.add( _title, BorderLayout.WEST );
        
        return _head;
    }

    @Override protected JComponent initFoot() {

        // FOOTER 
        _foot = new JPanel();
        _foot.setLayout( new BorderLayout() );
        _foot.add( new JSeparator(), BorderLayout.NORTH ); // separate button set from content card
        _foot.setBackground( Color.WHITE );
        _foot.setOpaque( true );


        // LEFT SIDE
        JPanel left = new CtBreadcrumbPanel();
//        JPanel left = new JPanel( new FlowLayout() );
//               left.setOpaque( false );
//        JLabel logo = new JLabel( "<html><h1>CellTrack</h1></html>" );
//               logo.setIcon( new ImageIcon( "./artwork/nicta/nicta_icon.png" ) );
//
//        JButton help = new JButton( "Help" );
//
//        left.add( logo );
//        left.add( Box.createHorizontalStrut( BUTTON_SEPARATION ) );
//        left.add( help );


        // RIGHT SIDE
        JPanel right = new JPanel( new FlowLayout() );
               right.setOpaque( false );

        if( ( _buttonMask & BUTTON_BACK   ) != 0 ) _backButton = initButton( right, "Back" );
        if( ( _buttonMask & BUTTON_NEXT   ) != 0 ) _nextButton = initButton( right, "Next" );
        right.add( Box.createHorizontalStrut( BUTTON_SEPARATION ) );
        if( ( _buttonMask & BUTTON_DONE   ) != 0 )   _doneButton = initButton( right, "Done" );
        if( ( _buttonMask & BUTTON_CANCEL ) != 0 ) _cancelButton = initButton( right, "Cancel" );


        // ASSEMBLY
        _foot.add(  left, BorderLayout.WEST );
        _foot.add( right, BorderLayout.EAST );
        
        return _foot;
    }

    protected JButton initButton( JPanel p, String label ) {
        JButton b = new JButton( label );
        p.add( b );
        _buttons.add( b );
        return b;
    }
    
    @Override protected JComponent initBody() {

        _cp = new JPanel();
        _cp.setBorder( new EmptyBorder( new Insets( 5, 10, 5, 10 ) ) );
        _cp.setOpaque( false );
        _cl = new CardLayout();
        _cp.setLayout( _cl );

        int pages = _wm.pages();
        int page = 0;

        while( page < pages ) {
            CtWizardPage wp = _wm.getPage( page );
            Object id = wp.id();
            JComponent c = wp._page;
            _cp.add( c, id );
            ++page;
        }

        return _cp;
//        JLabel body = new JLabel( "body" );
//        return body;
    }
    
    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();

        if( s.equals( CtWizardModel.ACTION_MODEL_CHANGED ) ) {
            refresh();
        }
        else if( s.equals( CtWizardModel.ACTION_PAGE_CHANGED ) ) {
            refresh();
        }
    }

    public void refresh() {
        
//        CtWizardModel wm = (CtWizardModel)_wm;
        CtWizardPage wp   = _wm.getPage();

        if( wp == null ) {
            _cl.invalidateLayout( _cp );
            return;
        }
        
        CtWizardPage back = wp.back();
        CtWizardPage next = wp.next();

        _backButton.setEnabled( false );
        _nextButton.setEnabled( false );

        if( back != null ) _backButton.setEnabled( true );
        if( next != null ) _nextButton.setEnabled( true );

        if( _wm.canCancel() ) {
            _cancelButton.setEnabled( true );
        }
        else {
            _cancelButton.setEnabled( false );
        }

        if( _wm.isDone() ) {
            _doneButton.setEnabled( true );
        }
        else {
            _doneButton.setEnabled( false );
        }

        Object id = wp.id();
        String title = wp._title;

        _title.setText( title );
//        JComponent c = wp._page;

        _cl.show( _cp, id.toString() );
    }

    public void addActionListener( ActionListener al ) {
        for( JButton b : _buttons ) {
            b.addActionListener( al );
        }
    }
    
//    protected void initComponents( int buttons ) {
//
//        _viewPanel = new JPanel();
//
//        _buttonPanel = new JPanel();
//        _buttonPanel.setLayout( new BorderLayout() );
//        _buttonPanel.add( new JSeparator(), BorderLayout.NORTH ); // separate button set from content card
//
//        _buttonBox = new Box( BoxLayout.X_AXIS );
//        _buttonBox.setBorder( new EmptyBorder( new Insets( 5, 10, 5, 10 ) ) );
//
//        _cardPanel = new JPanel();
//        _cardPanel.setBorder( new EmptyBorder( new Insets( 5, 10, 5, 10 ) ) );
//
//        _cardLayout = new CardLayout();
//        _cardPanel.setLayout( _cardLayout );
//
//     //   initButtons( buttons );
//
//        _buttonPanel.add( _buttonBox, java.awt.BorderLayout.EAST );
//
//        _viewPanel.setLayout( new BorderLayout() );
//        _viewPanel.add( _cardPanel, java.awt.BorderLayout.CENTER );
//        _viewPanel.add( _buttonPanel, java.awt.BorderLayout.SOUTH );
//        _viewPanel.setMaximumSize( new Dimension() );

//        _f.setView( _viewPanel );
//        _f.getContentPane().add( _viewPanel );
//
//        CtWizardModel wm = (CtWizardModel)_m;
//        int pages = wm.pages();
//        int page = 0;
//
//        while( page < pages ) {
//            CtWizardPage wp = wm.getPage( page );
//            Object id = wp.id();
//            JPanel p = wp._page;
//            _cardPanel.add( p, id );
//            ++page;
//        }
//    }

}
