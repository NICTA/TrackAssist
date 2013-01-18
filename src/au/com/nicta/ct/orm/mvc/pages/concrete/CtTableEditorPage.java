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

import au.com.nicta.ct.ui.swing.util.CtMenuBuilder;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.orm.mvc.pages.CtPageAction;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel;
import au.com.nicta.ct.ui.swing.components.CtDialogPanel.CtComponentResizePolicy;
import au.com.nicta.ct.ui.swing.components.CtFooterPanel;
import au.com.nicta.ct.ui.swing.components.CtHeaderPanel;
import au.com.nicta.ct.db.editor.CtTableModel;
import au.com.nicta.ct.db.editor.CtTableView;
import au.com.nicta.ct.ui.style.CtStyle;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Default page for
 * @author davidjr
 */
public class CtTableEditorPage extends JPanel implements CtPage {

    public static final String TABLE_EDITOR_TABLE_NAME = "table-editor-table-name";
    public static final String TABLE_EDITOR_TABLE_NAMES_PROPERTY_KEY = "table-editor-table-names";
    
    public JPanel head() {
        return new CtHeaderPanel( "Table Editor" );
    }

    public JPanel foot() {
        return _foot;
    }

    public JPanel body() {
        return this;
    }

    public String key() {
        return "table-editor";
    }

    public String state() {
        return _state;
    }

    public void onExit() {

    }

    protected CtTableModel _tm;
    protected CtTableView _tv;
    protected String _state = CtPageStates.SELECT;
    protected JPanel _foot;

    public CtTableEditorPage() {
        super();

        CtPages.setBorder( this );
        setOpaque( false );
        setLayout( new BorderLayout() );

        _foot = new CtFooterPanel();

        JButton saveRow = new JButton( "Save" );
        JButton deleteRow = new JButton( "Delete" );
        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        buttons.setOpaque( true );
        buttons.setBackground( CtConstants.NictaYellow );
        buttons.add( saveRow );
        buttons.add( deleteRow );
        _foot.add( buttons, BorderLayout.EAST );

        JLabel ee = new JLabel( CtStyle.h3( "Selected Table" ) );
        ee.setOpaque( false );
        JLabel ee1 = new JLabel( CtStyle.h3( "Table Data" ) );
        ee1.setOpaque( false );

        // populate the list of tables we allow them to edit:
        try {
            _tm = new CtTableModel();
            _tm.appendRow();
            _tm.setEditable( true );
            _tm.setSaveFlag( false );
//        _tm.hidePk();
            _tm.lockPk();
            _tv = new CtTableView( _tm );
            _tv._tv.setColumnSelectionAllowed( false );
            _tv._tv.setRowSelectionAllowed( false );
//            CtConstants.setPreferredSize( _tv._tv );
        }
        catch( Throwable t ) {
            System.err.print( t );
//            System.exit( -1 );
        }

        final JComboBox tableSelection = new JComboBox();

        Collection< String > tableNames = getTableNames();

        for( String s : tableNames ) {
            tableSelection.addItem( s );
        }

        ArrayList< CtComponentResizePolicy > c = new ArrayList< CtComponentResizePolicy >();
        c.add( new CtComponentResizePolicy( ee, false ) );
        c.add( new CtComponentResizePolicy( tableSelection, false ) );
        c.add( new CtComponentResizePolicy( ee1, false ) );
        c.add( new CtComponentResizePolicy( _tv, true ) );
        JPanel dialog = CtDialogPanel.create( c );

        add( dialog, BorderLayout.CENTER );

        saveRow.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to save changes to database?", "Save row[s]", JOptionPane.YES_NO_OPTION );
                if( n != JOptionPane.YES_OPTION ) {
                    return;
                }

                _tm.savePendingRows();
                _tm.appendRow();
                _tv._tv.repaint();
            }
        });

        deleteRow.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to delete selected row?", "Add row[s]", JOptionPane.YES_NO_OPTION );
                if( n != JOptionPane.YES_OPTION ) {
                    return;
                }

                int rowIndex = _tv._tv.getSelectedRow();
                _tm.deleteRow( rowIndex );
                _tv._tv.repaint();
            }
        });

        tableSelection.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String selected = (String)cb.getSelectedItem();
                setTable( selected );
            }
        });

        String initialTableName = (String)CtObjectDirectory.get( TABLE_EDITOR_TABLE_NAME );

        if( initialTableName != null ) {
            tableSelection.setSelectedItem( initialTableName );
        }
    }

    public void setTable( String hibernateTableName ) {
        _tm = new CtTableModel( hibernateTableName );//map.get( selected ) );
        _tm.appendRow();
        _tv.setModel( _tm );
    }

    public static void addMenuItems( CtMenuBuilder mb, String menuName ) {
        Collection< String > cs = getTableNames();

        for( final String tableName : cs ) {
            CtPageAction pa = new CtPageAction( tableName, "edit-table" ) {
                @Override public void actionPerformed( ActionEvent ae ) {
                    CtObjectDirectory.put( TABLE_EDITOR_TABLE_NAME, tableName );
                    super.actionPerformed( ae );
                }
            };
            
            mb.addMenuItem( menuName, pa );
        }
    }

    public static Collection< String > getTableNames() {
        String tableNames = CtKeyValueProperties.getValue( TABLE_EDITOR_TABLE_NAMES_PROPERTY_KEY );

        if( tableNames == null ) {
            return new ArrayList< String >();
        }

        String[] ss = tableNames.split( "(?<!\\\\)," );

        Collection< String > cs = Arrays.asList( ss );

        return cs;
    }

}
