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

package au.com.nicta.ct.experiment;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageStates;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtQueries;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtUsers;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPageGraph;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.db.editor.CtTableModel;
import au.com.nicta.ct.db.editor.CtTableView;
import au.com.nicta.ct.experiment.CtExperimentController;
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

/**
 *
 * @author davidjr
 */
public class CtSelectExperimentPage extends JPanel implements ActionListener, CtPage, MouseListener {

    public JPanel head() {
        return new CtHeaderPanel( "Select Experiment" );
    }

    public JPanel foot() {
        return _foot;
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return "select-experiment";
    }

    public String state() {
        return _state;
    }

    public void onExit() {}

    protected CtTableModel _tm;
    protected CtTableView _tv;
    protected String _state = CtPageStates.SELECT;
    protected JPanel _foot;

    public CtSelectExperimentPage() {
        super();

        CtPages.setBorder( this );
        setOpaque( false );
        setLayout( new BorderLayout() );
//        setLayout( new GridBagLayout() );
//        JPanel grid = new JPanel();
//        GroupLayout gl = new GroupLayout( grid );
//        grid.setLayout( gl );
//        grid.setOpaque( false );
//        gl.setAutoCreateGaps( true );
//        gl.setAutoCreateContainerGaps( true );

        CtUsers u = (CtUsers)CtObjectDirectory.get( "user" );

        if( u == null ) {
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
            return;
        }

        _foot = new CtFooterPanel();
        
        Collection experiments = CtQueries.experiments( u );

        try {
            _tm = new CtTableModel( experiments );
            _tm.setEditable( false );
//        tm.hidePk();
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

        JButton create = new JButton( "New" );
                create.setActionCommand( CtPageStates.CREATE );
                create.addActionListener( this );

        JButton select = new JButton( "Open" );
                select.setActionCommand( CtPageStates.DISPLAY );
                select.addActionListener( this );

        JButton remove = new JButton( "Delete" );
                remove.setActionCommand( CtPageStates.REMOVE );
                remove.addActionListener( this );

        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        buttons.setOpaque( true );
        buttons.setBackground( CtConstants.NictaYellow );
        buttons.add( create );
        buttons.add( select );
        buttons.add( remove );

        _foot.add( buttons, BorderLayout.EAST );
        
        JLabel ee = new JLabel( CtStyle.h3( "Existing Experiments" ) );
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
/*
        JPanel dialog = new JPanel();// new BorderLayout() );
//        dialog.setPreferredSize( new Dimension( 400, dialog.getSize().height ) );
        dialog.setBackground(Color.blue );
        dialog.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();

        int heightComponents = 2;
        JPanel blank0 = new JPanel( new FlowLayout() );
        blank0.add( new JLabel("blah0") );
        JPanel blank2 = new JPanel( new FlowLayout() );
        blank2.add( new JLabel("blah0") );
        blank0.setBackground(Color.pink );
        blank2.setBackground(Color.magenta );
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;
        gbc.gridheight = heightComponents;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        dialog.add( blank0, gbc );
        gbc.gridx = 2;
        dialog.add( blank2, gbc );
        gbc.weightx = 0.4;
        gbc.weighty = 1.0 / (double)heightComponents;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        
        // for-each( component ):
        int yComponent = 0;
        gbc.gridy = yComponent;   yComponent++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add( ee, gbc );
        gbc.gridy = yComponent;   yComponent++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        dialog.add( _tv, gbc );
*/
        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( ee, false ) );
        c.add( new CtComponentResizePolicy( _tv, true ) );
        JPanel dialog = CtDialogPanel.create( c );

//        JPanel recursive1 = new JPanel();
//        BoxLayout bl = new BoxLayout( recursive1, BoxLayout.PAGE_AXIS );
//        recursive1.setBackground(Color.red );
//        recursive1.setLayout( bl );
//        recursive1.add( ee, BorderLayout.NORTH );
//        recursive1.add( _tv, BorderLayout.SOUTH );
//        dialog.add( recursive1, BorderLayout.CENTER );
        add( dialog, BorderLayout.CENTER );

        // for convenience add double-click event on the table:
        _tv._tv.addMouseListener( this );
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

            CtExperiments e = null;

            try {
                int rowIndex = _tv._tv.getSelectedRow();
                int pk = _tm.pk( rowIndex );
                e = (CtExperiments)CtSession.getObject( CtExperiments.class, pk );
            }
            catch( Exception ex ) {
                String message = "You must select an experiment to open.";
                JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
                return;
            }

            CtExperimentController.set( e );
//            CtExperimentController.set( e, "time" );
//            CtCoordinatesController.get().setRange( "time" ); now done automatically by coord. controler
//            CtObjectDirectory.put( "experiment", e );
//            CtExperimentModel em = CtExperimentModel.set( e, "time" );
//            CtObjectDirectory.put( CtExperimentModel.name(), em );

////////////////////////////////////////////////////////////////////////////////
// For now, assume 1 soln per expt, and fetch it automatically.
// otherwise we'd have a page to select the specific solution...
//            CtSolutions s = CtQueries.solution( e );
//            CtObjectDirectory.put( "solution", s );
////////////////////////////////////////////////////////////////////////////////

            _state = action;

            /// lookup in the properties the next page after login.. apply it
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
            return;
        }
        else if( action.equals( CtPageStates.REMOVE ) ) {
            String message = "Not implemented yet.";
            JOptionPane.showMessageDialog( this.getTopLevelAncestor(), message );
            return;
        }
        else if(    action.equals( CtPageStates.CREATE )
                 || action.equals( CtPageStates.SELECT ) ) {
            _state = action;
            CtPageController pc = (CtPageController)CtObjectDirectory.get( "page-controller" );
            pc.transition();
            return;
        }
    }

}
