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

import java.util.Collection;
import javax.swing.DefaultComboBoxModel;

/**
 * Allows selection of table whole rows only to implement combo-box interface.
 * @author davidjr
 */
public class CtComboModel extends DefaultComboBoxModel {

    public CtTableModel _tm;
    String _displayFieldName;
    int _selectedRowIndex = 0;
    
//    public CtComboModel() {
//
//    }

    public CtComboModel( CtTableModel tm, String displayFieldName ) {
        create( tm, displayFieldName );
    }

    public CtComboModel( Collection queryResults, String displayFieldName ) {
        create( new CtTableModel( queryResults ), displayFieldName );
    }

//    public CtComboModel( String query, String displayFieldName ) throws Throwable {
//        create( CtTableModel.Create( query ), displayFieldName );
//    }

    public void create( CtTableModel tm, String displayFieldName ) {
        removeAllElements();

        _tm = tm;
        _displayFieldName = displayFieldName;

        int column = _tm.getColumnIndex( _displayFieldName );
        int rows = _tm.getRowCount();

        for( int row = 0; row < rows; ++row ) {
            Object o = _tm.getValueAt( row, column );
            addElement( o );
        }
    }

    @Override public Object getSelectedItem() {
        int column = _tm.getColumnIndex( _displayFieldName );
        return _tm.getValueAt( _selectedRowIndex, column );
    }

    @Override public void setSelectedItem( Object item ) {
        int column = _tm.getColumnIndex( _displayFieldName );
        int rows = _tm.getRowCount();
        
        for( int row = 0; row < rows; ++row ) {
            Object o = _tm.getValueAt( row, column );

            if( o == item ) {
                _selectedRowIndex = row;
            }
        }
    }

    public Object getSelectedRow() {
        return _tm.getRowObject( _selectedRowIndex );
    }






}
