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

import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.solution.CtSolutionController;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author alan
 */
//public class CtPropertyTableModel extends DefaultTableModel {
public class CtEntityPropertyTableModel extends AbstractTableModel {

    Session s = CtSession.Current();
    List<CtEntityProperties> properties = Collections.EMPTY_LIST;
    Class entity = null;
    Integer entityPk = null;
    boolean enabled = true;

    boolean ignoreNextSetValue = false;

    public void clear() {
        properties.clear();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }


    void ignoreNextSetValue() {
        ignoreNextSetValue = true;
    }

    public void load( Class entity ) {
        load( entity, null );
    }

    public void load( Class entity, Integer entityPk ) {
        this.entity = entity;
        this.entityPk = entityPk;
        reload();
    }

    public void reload() {

        CtManualFlush mf = new CtManualFlush(s);
        s.beginTransaction();
        properties = CtEntityPropertiesUtil.find( s, CtSolutionController.getSolutions(), entity, entityPk, null );
        Collections.sort(properties, CtEntityPropertiesUtil.COMPARE_NAME);

        s.getTransaction().commit();
        mf.restore();

//        properties.add(new CtEntityProperties(0, "Test", 1, "Test", "Test" ) );

        fireTableDataChanged();
    }

    public void print() {
        for( CtEntityProperties ep : properties ) {
            System.out.println( "property name: " + ep.getName() + " value: " + ep.getValue() );
        }
    }

    protected void initProperties() {
        if( properties == null ) {
            properties = Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	if( isLastRow(rowIndex) && columnIndex == COLUMN_INDEX_PROPERTY_NAME ) {
            return true;
        }
	if( !isLastRow(rowIndex) && columnIndex == COLUMN_INDEX_PROPERTY_VALUE ) {
            return true;
        }
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        super.removeTableModelListener(l);
    }




    @Override
    public int getRowCount() {
        initProperties();
        if( !isEnabled() ) {
            return properties.size();
        }
        return properties.size()+1; // +1 to reserve for adding of rows
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    public static final String[] columnNames = { "Name", "Value" };
    public static final Class[] columnClasses = { String.class, Integer.class };
    public static final int COLUMN_INDEX_PROPERTY_NAME = 0;
    public static final int COLUMN_INDEX_PROPERTY_VALUE = 1;

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public Object getValueAt(CtEntityProperties ep, int columnIndex) {
        switch( columnIndex ) {
            case COLUMN_INDEX_PROPERTY_NAME:
                return ep.getName();
            case COLUMN_INDEX_PROPERTY_VALUE:
                return ep.getValue();
            default:
                throw new RuntimeException("Unsupported column index: " + columnIndex);
        }
    }

    public boolean isLastRow(int rowIndex) {
        return rowIndex == properties.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // last row
        if( isLastRow( rowIndex ) ) {
            switch( columnIndex ) {
                case COLUMN_INDEX_PROPERTY_NAME:
                    return CtEntityPropertyTypesCombo.PLACE_HOLDER;
                case COLUMN_INDEX_PROPERTY_VALUE:
                    return "";
                default:
                    throw new RuntimeException("Unsupported column index: " + columnIndex);
            }
        }

        return getValueAt(properties.get(rowIndex), columnIndex);
    }

    public void setValueAt(Object aValue, CtEntityProperties ep, int columnIndex) {
        switch(columnIndex) {
            case COLUMN_INDEX_PROPERTY_NAME:
                ep.setName( (String) aValue );
                break;
            case COLUMN_INDEX_PROPERTY_VALUE:
                ep.setValue( aValue.toString() );
                break;
            default:
                throw new RuntimeException("Unsupported column index: " + columnIndex);
        }
    }

    public boolean isNameDuplicate(String name, CtEntityProperties exclude) {
        for( CtEntityProperties i : properties ) {
            if( i == exclude ) { // don't test against self
                continue;
            }
            if( i.getName().equals( name ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isNameDuplicate(String name) {
        for( CtEntityProperties i : properties ) {
            if( i.getName().equals( name ) ) {
                return true;
            }
        }
        return false;
    }

    public void setValue(Object o, CtEntityProperties ep) {
        ep.setValue(o != null ? o.toString() : "");
    }

    public boolean setName(Object o, CtEntityProperties ep) {
//        // check for uniqueness
//        if( isNameDuplicate(newName, ep) ) {
//            JOptionPane.showMessageDialog(null, "Duplicate Name");
//            return false;
//        }

        ep.setName(o != null ? o.toString() : "");
        return true;
    }

    public boolean handleCreate(Object aValue, int rowIndex ) {
        if( !isLastRow(rowIndex) ) {
            return false;
        }

        String name = (String)aValue;
        if( isNameDuplicate(name) ) {
            JOptionPane.showMessageDialog(null, "Duplicate Name");
            return true; // event has been handled;
        }

        s.beginTransaction();

        CtEntityProperties ep = new CtEntityProperties();
        ep.setPkEntityProperty(0);
        ep.setCtSolutions(CtSolutionController.getSolutions());
        ep.setName(name);
        ep.setEntityName( CtEntityPropertiesUtil.getClassName(entity) );
        ep.setEntityPk( entityPk );
        ep.setValue("");
        s.saveOrUpdate(ep);
        s.flush();
        s.getTransaction().commit();

        reload();
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if( ignoreNextSetValue ) {
            ignoreNextSetValue = false;
            return;
        }

        if( aValue == null ) {
            return;
        }

        if( aValue.toString().isEmpty() ) {
            return;
        }

        if( columnIndex == COLUMN_INDEX_PROPERTY_NAME ) {
            if( aValue.toString().equals( CtEntityPropertyTypesCombo.PLACE_HOLDER ) ) {
                return;
            }
            if( aValue.toString().equals( "" ) ) {
                return;
            }
        }

        if( handleCreate(aValue, rowIndex) ) {
            return;
        }


        CtEntityProperties ep = properties.get(rowIndex);

        if( columnIndex == COLUMN_INDEX_PROPERTY_NAME ) {
            setName(aValue, ep);
        }
        
        if( columnIndex == COLUMN_INDEX_PROPERTY_VALUE ) {
            setValue(aValue, ep);
        }

        Transaction t = s.beginTransaction();
        s.saveOrUpdate(ep);
//        s.update(ep);
        System.out.println( "Flush mode: " + s.getFlushMode() );
        s.flush();
        t.commit();

        System.out.println( "ep: " + ep.getValue() );

        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    public void removeRow(int rowIndex) {
        s.beginTransaction();
        s.delete( properties.get(rowIndex) );
        s.flush();
        s.getTransaction().commit();

        properties.remove( rowIndex );
        fireTableDataChanged();
    }


}
