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

package au.com.nicta.ct.solution.pages;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtQueries;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.db.editor.CtTableModel;
import au.com.nicta.ct.db.editor.CtTableView;
import au.com.nicta.ct.solution.CtSolutionController;
import au.com.nicta.ct.solution.CtSolutionController;
import au.com.nicta.ct.solution.tracking.CtSolutionManagerGui;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtSelectSolutionPage extends JPanel implements ActionListener, CtPage, MouseListener {

    public JPanel head() {
        return new CtHeaderPanel( "Select Solution" );
    }

    public JPanel foot() {
        return _foot;
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return "select-solution";
    }

    public String state() {
        return _state;
    }

    public void onExit() {}

    public CtTableView _tv;
    protected CtTableModel _tm;
    protected String _state = CtPageStates.SELECT;
    protected JPanel _foot;

    public CtSelectSolutionPage() {
        super();

        CtPages.setBorder( this );
        setOpaque( false );
        setLayout( new BorderLayout() );

        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );

        if( e == null ) {
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
            return;
        }

        _foot = new CtFooterPanel();

        try {
            _tm = createSolutionsTableModel(e);
            _tv = new CtTableView( _tm );
            _tv._tv.setColumnSelectionAllowed( false );
            _tv._tv.setRowSelectionAllowed( true );
            _tv._tv.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            CtConstants.setPreferredSize( _tv._tv );
        }
        catch( Throwable t ) {
            System.err.print( t );
//            System.exit( -1 );
        }

        CtSolutionManagerGui solutionManagerGui = new CtSolutionManagerGui(this);

        JButton create = new JButton( "New" );
                create.setActionCommand( CtPageStates.CREATE );
                create.addActionListener( this );

        JButton select = new JButton( "Open" );
                select.setActionCommand( CtPageStates.DISPLAY );
                select.addActionListener( this );

        JButton rename = new JButton( "Rename" );
                rename.setActionCommand( CtSolutionManagerGui.RENAME );
                rename.addActionListener( solutionManagerGui );

        JButton remove = new JButton( "Delete" );
                remove.setActionCommand( CtSolutionManagerGui.DELETE );
                remove.addActionListener( solutionManagerGui );

        JButton copy = new JButton( "Copy" );
                copy.setActionCommand( CtSolutionManagerGui.COPY );
                copy.addActionListener( solutionManagerGui );

        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        buttons.setOpaque( true );
        buttons.setBackground( CtConstants.NictaYellow );
        buttons.add( create );
        buttons.add( select );
        buttons.add( rename );
        buttons.add( remove );
        buttons.add( copy );

        _foot.add( buttons, BorderLayout.EAST );

        JLabel ee = new JLabel( CtStyle.h3( "Existing Solutions" ) );
        ee.setOpaque( false );
        /*
        JPanel list = new JPanel( new BorderLayout() );
        list.setOpaque( false );
        list.add( ee, BorderLayout.NORTH );
        list.add( _tv, BorderLayout.CENTER );
        JPanel both = new JPanel();
        both.setOpaque( false );
        both.setLayout( new BorderLayout() );
//        both.add( _tv, BorderLayout.NORTH );
        both.add( list, BorderLayout.NORTH );
//        both.add( buttons, BorderLayout.SOUTH );

        add( both, BorderLayout.CENTER );
*/
        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( ee, false ) );
        c.add( new CtComponentResizePolicy( _tv, true ) );
        JPanel dialog = CtDialogPanel.create( c );
        add( dialog, BorderLayout.CENTER );

        // for convenience add double-click event on the table:
        _tv._tv.addMouseListener( this );
    }

    public CtTableModel createSolutionsTableModel(CtExperiments e) {
        CtTableModel tm = null;
        try {
            Collection solutions = CtQueries.solutions( e );
            tm = new CtTableModel();
            tm._hiddenFields.add( "ctExperiments" );
            tm.create( solutions );
            tm.setEditable( false );
        }
        catch( Throwable t ) {
            System.err.print( t );
//            System.exit( -1 );
        }
        
        return tm;
    }

    public void refreshSolutionsTable() {
        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );
        try {
            _tm = createSolutionsTableModel( e );
            _tv.setModel( _tm );
        }
        catch( Throwable t ) {
            System.err.print( t );
//            System.exit( -1 );
        }
    }

    public void mouseClicked( MouseEvent e ) {
        if( e.getClickCount() == 2 ) {
            actionPerformed( new ActionEvent( this, 0, CtPageStates.DISPLAY ) );
        }
    }
    public void mouseEntered( MouseEvent e ){}
    public void mouseExited( MouseEvent e ){}
    public void mousePressed( MouseEvent e ){}
    public void mouseReleased( MouseEvent e ){}

    @Override public void actionPerformed( ActionEvent ae ) {
        String action = ae.getActionCommand();

//        _state = s;

        if( action.equals( CtPageStates.DISPLAY ) ) {

            CtSolutions s = null;

            try {
                int rowIndex = _tv._tv.getSelectedRow();
                int pk = _tm.pk( rowIndex );
                s = (CtSolutions)CtSession.getObject( CtSolutions.class, pk );
            }
            catch( Exception ex ) {
                String message = "You must select a solution to open.";
                JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
                return;
            }

            CtSolutionController.set( s );
//            CtObjectDirectory.put( "solution", s );

            _state = action;

            /// lookup in the properties the next page after login.. apply it
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
            return;
        }
        else if( action.equals( CtPageStates.SELECT ) ) {
            _state = action;
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
            return;
        }
        else if( action.equals( CtPageStates.CREATE ) ) {
            String message = "Solution name:";
            String name = JOptionPane.showInputDialog( this.getTopLevelAncestor(), message );

            if( name != null ) {
                CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );

                if( e == null ) {
                    CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
                    pc.transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
                    return;
                }

                CtSolutions s = new CtSolutions();
                s.setName( name );
                s.setCtExperiments( e );
                Session session = CtSession.Current();
                session.beginTransaction();
                session.save( s );
                session.flush();
                CtSession.Current().getTransaction().commit();

                _state = action;
                CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
                pc.transition();
            }
            return;
        }
    }
}
