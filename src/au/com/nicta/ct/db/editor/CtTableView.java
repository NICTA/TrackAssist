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

package au.com.nicta.ct.db.editor;

import java.awt.GridLayout;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author davidjr
 */
public class CtTableView extends JPanel {

    public CtTableModel _tm;
    public JTable _tv;
    public JScrollPane _sp;
    
    public CtTableView( CtTableModel tm ) {

        super( new GridLayout(1,0) );

        _tm = tm;
        _tv = new JTable( tm );
        _tv.setFillsViewportHeight( true );

        //Create the scroll pane and add the table to it.
        _sp = new JScrollPane( _tv );
        _sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );

        //Add the scroll pane to this panel.
        add( _sp );

        setOpaque( true ); //content panes must be opaque
    }

    public void setModel( CtTableModel tm ) {
        _tm = tm;
        _tv.setModel( tm );
//        _sp.revalidate();
    }

    public void setComboCellEditor( Collection queryResults, String fieldName ) {

        CtComboModel cm = null;
        try {
            cm = new CtComboModel( queryResults, fieldName );
        }
        catch( Throwable t ) {
            System.err.print( t );
            System.exit( -1 );
        }

        JComboBox cb = new JComboBox( cm );

        int columnIndex = cm._tm.getColumnIndex( fieldName );
        TableColumn tc = _tv.getColumnModel().getColumn( columnIndex );

        CtForeignKeyEditor fke = new CtForeignKeyEditor( cb, cm );
//        DefaultCellEditor dce = new DefaultCellEditor( cb );
//        DefaultTableCellRenderer dcr = new DefaultTableCellRenderer( cb );

//        tc.setCellEditor( dce );
        tc.setCellEditor( fke );
    }

//    @Override public void editingStopped(ChangeEvent e) {
//        // Take in the new value
//        TableCellEditor editor = getCellEditor();
//        if (editor != null) {
//            Object value = editor.getCellEditorValue();
//            setValueAt(value, editingRow, editingColumn);
//            removeEditor();
//        }
//    }
}

