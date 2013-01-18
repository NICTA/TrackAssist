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

package au.com.nicta.ct.experiment.graphics.canvas.composite;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author Dr. R Chakravorty
 */
public class CtCompositeTableModel extends AbstractTableModel implements TableModelListener{

    public static String _notUsed = "Not Used";

    public static final String[]  _names    = {"Color","Value"};

    public  List< String  >   colors    = new ArrayList< String > ();
    public  List< String  >   axisValue = new ArrayList< String > ();

    public CtCompositeTableModel( CtCompositeTools ct, String axisName ) {

        colors.add( CtCompositeController._original );
        colors.add( CtCompositeController._green    );
        colors.add( CtCompositeController._red      );
        colors.add( CtCompositeController._blue     );

        resetModel( axisName );
    }

    public void resetOriginalAxis( String axisName ){
        axisValue.set( 0, axisName );
        this.fireTableDataChanged();
    }

    public void resetModel( String axisName ) {

        axisValue.clear();

        axisValue.add( "" );
        axisValue.add( CtCompositeTableModel._notUsed );
        axisValue.add( CtCompositeTableModel._notUsed );
        axisValue.add( CtCompositeTableModel._notUsed );

        resetOriginalAxis( axisName );
        
        this.fireTableDataChanged();
    }

    public void replaceRow( String _color, String _axisValue ) {

        int colorIndex = 1;
        for(  ; colorIndex < colors.size() ; colorIndex++ ){
            if( _color.equals( colors.get( colorIndex ) ) ){

                axisValue.set( colorIndex, _axisValue );
                break;
            }
        }
        this.fireTableRowsUpdated( colorIndex , colorIndex );
    }

    @Override
    public void tableChanged( TableModelEvent e ) {
        return;
    }
    
    @Override
    public int getRowCount() {
        return colors.size();
    }

    @Override
    public int getColumnCount() {
        return _names.length;
    }

    @Override
    public String getColumnName( int col ) {
        return _names[col];
    }

    @Override
    public Object getValueAt( int row, int col ) {
        if( col == 0 ) {
            return colors.get( row );
        }
        else {
            return axisValue.get ( row );
        }
    }

    @Override
    public void setValueAt( Object o,int row, int col ) {
        if( col == 0 ) {
            colors.set( row,( String ) o );
        }
        else if( col == 1 ) {
            axisValue.set( row,(String ) o);
        }
        this.fireTableDataChanged();
        return;
    }

    @Override
    public Class<?> getColumnClass( int col ) {
        if( col == 2 ) {
            return String.class;
        }
        else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable( int row, int col ) {
        return false;
    }
}
