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

package au.com.nicta.ct.db.entities;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.db.hibernate.CtEntityPropertiesTypes;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtCellEditorTable;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtCellEditorTableModel;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 *
 * author alan
 */
public class CtEntityPropertyTable extends JPanel {

    final String ID_PROPERTY_NAME = "id";
    final String ID_PROPERTY_TYPE = "id";

    final String PARENT_PROPERTY_NAME = "parent";
    final String PARENT_PROPERTY_TYPE = "id";


    class CtEntityPropertyTypesComboListener implements ItemListener {

        Object lastItem;

        CtEntityPropertyTypesModel eptModel;
        CtEntityPropertyTableModel epModel;

        public CtEntityPropertyTypesComboListener(
                CtEntityPropertyTypesModel eptModel,
                CtEntityPropertyTableModel epModel ) {
            this.eptModel = eptModel;
            this.epModel = epModel;
        }

        public void selectedManage(ItemEvent e) {
            CtEntityPropertyTypesManager manager = new CtEntityPropertyTypesManager();
            manager.show( propertyTypesCombo, eptModel.entity );
            eptModel.reload();
            epModel.reload();
            epModel.ignoreNextSetValue();
        }

        public void itemStateChanged(ItemEvent e) {
            String item = (String) e.getItem();

            // remember last item to reset when rolling back action
            if( e.getStateChange() == ItemEvent.DESELECTED ) {
                lastItem = item;
                return;
            }
            if( item.equals(CtEntityPropertyTypesCombo.MANAGE) ) {
                selectedManage(e);
                return;
            }
        }

    }

//    void comboActionListener(ActionEvent e) {
//        JComboBox cb = (JComboBox) e.getSource();
//        String sel = (String) cb.getSelectedItem();
//        if( sel == null ) {
//            return;
//        }
//        if( sel.equals(CtEntityPropertyTypesCombo.MANAGE) ) {
//            CtEntityPropertyTypesManager manager = new CtEntityPropertyTypesManager();
//            manager.show( propertyTypesCombo,entity );
////            ( (JComboBox) e.getSource() ).setSelectedItem(lastItem);
////            eptModel.reload();
////            epModel.reload();
//            tableModel.reload();
//        }
//    }

    void keyListener(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_DELETE ) {
            int sel = table.getSelectedRow();
            if(    sel != -1 // something selected
                && sel != table.getRowCount() - 1 // not last row
                ) {
                tableModel.removeRow( table.getSelectedRow() );
            }
        }
    }

    int lastRowCount = 0;

    public Map<String, TableCellEditor> getPropertyTypeCellEditors() {
        return propertyTypeCellEditors;
    }

    public final void setPropertyTypeCellEditor(String propertyType, TableCellEditor e) {
        propertyTypeCellEditors.put(propertyType, e);
    }

    void setCellEditors() {
        for( int row = 0; row < tableModel.properties.size(); ++row ) {
            CtEntityProperties ep = tableModel.properties.get(row);
            String name = ep.getName();
            String type = null;
            // Find the property with the same name so we knwo what data type it is
            List<CtEntityPropertiesTypes> allTypes = CtEntityPropertiesTypesUtil.find(CtSession.Current(), entity, null );
            for( CtEntityPropertiesTypes ept : allTypes ) { //propertyTypes.allTypes ) {
                if( ept.getName().equals( name ) ) {
                    type = ept.getType();
                    break;
                }
            }
            assert type != null;

            TableCellEditor e = propertyTypeCellEditors.get( type );
            assert e != null;

            if( e != null ) {
                cellEditorTM.setEditor( row, 1, e );
            }
            // else use default
        }

    }

    void tableModelListener(TableModelEvent e) {
//        int c = tableModel.getRowCount();
//        if( lastRowCount != c ) {
//            lastRowCount = c;
            propertyTypes.load(entity);

            cellEditorTM.clear();
            cellEditorTM.setEditor(tableModel.getRowCount()-1, 0, lastRowEditor);
            setCellEditors();
//        }
    }


    CtEntityPropertyTableModel tableModel = new CtEntityPropertyTableModel();
    CtEntityPropertyTypesModel propertyTypes = new CtEntityPropertyTypesModel();
    CtCellEditorTableModel cellEditorTM;
    Map<String, TableCellEditor> propertyTypeCellEditors = new HashMap<String, TableCellEditor>();

    public CtCellEditorTable table;
    JScrollPane scrollPane;
    DefaultCellEditor lastRowEditor;
    JComboBox propertyTypesCombo;

    Class entity;
    int entityPk;

    public void load( Class entity, int entityPk ) {
        this.entity = entity;
        this.entityPk = entityPk;
        reload();
    }

    public void reload() {
        tableModel.load(entity, entityPk);
        propertyTypes.load(entity);
    }

    public void clear() {
        tableModel.clear();
    }

    public CtEntityPropertyTable() {

        table = new CtCellEditorTable(tableModel);
        cellEditorTM = table.getCellEditorTableModel();
        table.setFillsViewportHeight(true);
        TableColumn col = table.getColumnModel().getColumn(CtEntityPropertyTableModel.COLUMN_INDEX_PROPERTY_VALUE);
        col.setCellEditor(new DefaultCellEditor(new JTextField()));
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        int fontHeight = metrics.getHeight();
        table.setRowHeight( fontHeight + 5 );

        scrollPane = new JScrollPane(table);

        propertyTypes.filterWith(tableModel);
        propertyTypes.addPermanentItem( CtEntityPropertyTypesCombo.MANAGE );

        propertyTypesCombo = new JComboBox(propertyTypes);
        propertyTypesCombo.setEditable(true);
        propertyTypesCombo.addItemListener( new CtEntityPropertyTypesComboListener( propertyTypes, tableModel ) );

//        propertyTypesCombo.addActionListener( new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                comboActionListener(e);
//            }
//        });


        lastRowEditor = new DefaultCellEditor( propertyTypesCombo );

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyListener(e);
            }
        });

        table.getModel().addTableModelListener( new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                tableModelListener(e);
            }
        });

        addIdProperty(ID_PROPERTY_NAME, ID_PROPERTY_NAME);
        addIdProperty(PARENT_PROPERTY_NAME, ID_PROPERTY_NAME);

        // layout
        setLayout( new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scrollPane);
        scrollPane.setVisible(true);
        scrollPane.setOpaque(true);
        table.setOpaque(true);

        setOpaque(true);
        setVisible(true);
    }

    final void addIdProperty(String propertyName, String filterOnProperty) {
        // add property type cell editors
        final CtIdPropertyModel idPropertyModel = new CtIdPropertyModel(filterOnProperty);
        table.getModel().addTableModelListener( new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                idPropertyModel.load(entity);
            }
        });

        final JComboBox idCombo = new JComboBox( idPropertyModel );
        idCombo.setEditable(true);
        idCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            // capture enter key when manually editing
            @Override
            public void keyPressed(KeyEvent e) {
                if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    idCombo.setSelectedItem(idCombo.getEditor().getItem());
                    idPropertyModel.reload();
                }
            }
        });
        setPropertyTypeCellEditor(propertyName, new DefaultCellEditor(idCombo));
    }


    public void setEnabled(boolean b) {
        table.setEnabled(b);
        tableModel.setEnabled(b);
    }
}
