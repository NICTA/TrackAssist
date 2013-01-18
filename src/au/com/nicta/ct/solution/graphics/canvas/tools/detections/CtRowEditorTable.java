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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author alan
 */
public class CtRowEditorTable extends JTable {

    protected CtRowEditorTableModel rowEditorTableModel = new CtRowEditorTableModel();

    public CtRowEditorTable() {
        super();
    }

    public CtRowEditorTable(TableModel tm) {
        super(tm);
    }

    public CtRowEditorTable(TableModel tm, TableColumnModel cm) {
        super(tm, cm);
    }

    public CtRowEditorTable(TableModel tm, TableColumnModel cm, ListSelectionModel sm) {
        super(tm, cm, sm);
    }

    public CtRowEditorTable(int rows, int cols) {
        super(rows, cols);
    }

    public CtRowEditorTable(final Vector rowData, final Vector columnNames) {
        super(rowData, columnNames);
    }

    public CtRowEditorTable(final Object[][] rowData, final Object[] colNames) {
        super(rowData, colNames);
    }

    // new constructor
    public CtRowEditorTable(TableModel tm, CtRowEditorTableModel model) {
        super(tm, null, null);
        this.rowEditorTableModel = model;
    }

    public void setRowEditorModel(CtRowEditorTableModel model) {
        this.rowEditorTableModel = model;
    }

    public CtRowEditorTableModel getRowEditorTableModel() {
        return rowEditorTableModel;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor e = rowEditorTableModel.getEditor(row);
        if(e != null) {
            return e;
        }
        return super.getCellEditor(row, col);
    }
}
