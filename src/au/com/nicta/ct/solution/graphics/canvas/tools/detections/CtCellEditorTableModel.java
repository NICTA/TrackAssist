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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author alan
 */
public class CtCellEditorTableModel {

    private class Row {
        public Map<Integer, TableCellEditor> cols = new HashMap<Integer, TableCellEditor>();
    }

    private Map<Integer, Row> rows = new HashMap<Integer, Row>();

    public void clear() {
        rows.clear();
    }

    public void clearCol(int col) {
        for( Row i : rows.values() ) {
            i.cols.remove(col);
        }
    }

    public void clearRow(int row) {
        rows.remove(row);
    }

    private Row getRow(int row) {
        return rows.get(row);
    }

    public TableCellEditor getEditor(int row, int col) {
        Row r = getRow(row);
        if( r == null ) {
            return null;
        }
        return r.cols.get(col);
    }

    public void setEditor(int row, int col, TableCellEditor e ) {
        Row r = getRow(row);
        if( r == null ) {
            r = new Row();
            rows.put( row, r );
        }
        r.cols.put(col, e);
    }

    public void removeEditor(int row, int col) {
        Row r = getRow(row);
        if( r != null ) {
            r.cols.remove(col);
            if( r.cols.isEmpty() ) {
                rows.remove(row);
            }
        }
    }

}
