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

import au.com.nicta.ct.db.CtSession;
import java.lang.Object;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.Query;

/**
 *
 * @author davidjr
 */
public class CtTableModel extends AbstractTableModel {//implements TableModel {

    public HashSet< String > _hiddenFields = new HashSet< String >();
    public HashSet< String > _lockedFields = new HashSet< String >();
    public HashMap< String, String > _substituteColumnNames = new HashMap< String, String >();
    protected HashSet< TableModelListener > _listeners = new HashSet< TableModelListener >();

    protected Class< ? > _cols;
    protected Collection _rows;
    protected Object _transientRow;
    protected Class< ? > _renderableCols;

    protected ArrayList< Integer > _columnIndices = new ArrayList< Integer >();
    protected int _columnIndexPK = 0;
    
    public boolean _editable = true; // TODO make per-column options
////////////////////////////////////////////////////////////////////////////////
// NHAT
    public boolean _saveFlag = true; // DAVE: Seems to mean SAVE ON CHANGE iff TRUE, else SAVE ON COMMAND

    ArrayList< Integer > _colSaveIndices = new ArrayList< Integer >(); // DAVE: Seems to be a record of changed data with pending save.
    ArrayList< Integer > _rowSaveIndices = new ArrayList< Integer >();
    ArrayList< Object > _value = new ArrayList< Object >();
// END NHAT
////////////////////////////////////////////////////////////////////////////////
    
    public CtTableModel() {

    }

    public CtTableModel( String hibernateTableName ) {
        create( hibernateTableName );
    }

    public CtTableModel( Collection queryResults ) {
        create( queryResults );
    }

    public void create( String hibernateTableName ) {
        Collection queryResults = getTableData( hibernateTableName );
        create( queryResults );
    }

    public void create( Collection queryResults ) {
        cols( queryResults );
        rows( queryResults );
        createColumnIndex();
    }

    public static Collection getTableData( String hibernateTableName ) {
        String hql = "from " + hibernateTableName;
        List< Object > l = CtSession.getObjects( hql );
        return l;
    }

    public void lockPk() {
        int internalColumnIndex = _columnIndexPK;

        try {
            Object[] objects = _rows.toArray();
    //        Object row = _rows.get( rowIndex );
            Object row = objects[ 0 ];
            Field f = _cols.getDeclaredFields()[ internalColumnIndex ];

            _lockedFields.add( f.getName() );
        }
        catch( NullPointerException npe ) {
            // nothing if table not set yet
        }
    }

    public void hidePk() {
        int internalColumnIndex = _columnIndexPK;

        Object[] objects = _rows.toArray();
//        Object row = _rows.get( rowIndex );
        Object row = objects[ 0 ];
        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];

        _hiddenFields.add( f.getName() );
    }

    public void uppercaseFieldNames() {
//        for( )
    }

    public void setEditable( boolean editable ) {
        _editable = editable;
    }

////////////////////////////////////////////////////////////////////////////////
// NHAT
    public boolean isSaveFlag() {
        return _saveFlag;
    }

    public void setSaveFlag( boolean saveFlag ) {
        this._saveFlag = saveFlag;
    }

    public void savePendingRows() {
        // Save edited cells and add last row if necessary
        setSaveFlag( true );

        int pendingRows = getRowSaveIndices().size();

        for( int i=0; i < pendingRows; i++ ) {
            int row = getRowSaveIndices().get( i );
            int col = getColSaveIndices().get( i );
            Object value = getValue().get( i );
            setValueAt( value, row, col );
        }

        getRowSaveIndices().clear();
        getColSaveIndices().clear();
        getValue().clear();

//        appendRow();
        setSaveFlag( false );
    }

    public ArrayList<Integer> getColSaveIndices() {
        return _colSaveIndices;
    }

    public void setColSaveIndices(ArrayList<Integer> _colSaveIndices) {
        this._colSaveIndices = _colSaveIndices;
    }

    public ArrayList<Integer> getRowSaveIndices() {
        return _rowSaveIndices;
    }

    public void setRowSaveIndices(ArrayList<Integer> _rowSaveIndices) {
        this._rowSaveIndices = _rowSaveIndices;
    }

    public ArrayList<Object> getValue() {
        return _value;
    }

    public void setValue(ArrayList<Object> _value) {
        this._value = _value;
    }
// END NHAT
////////////////////////////////////////////////////////////////////////////////

//    public CtTableModel( List queryResults ) throws Throwable {
//        model( queryResults );
//    }

//    public static CtTableModel Create( String query ) throws Throwable {
//        CtTableModel tm = new CtTableModel();
////tm._hiddenFields.add( "value" );
//tm.substituteColumnName( "pkCoordinate", "ID" );
//tm.substituteColumnName( "ctCoordinatesTypes", "Type" );
//        tm.create( query );
////tm.appendRow();
//        return tm;
//    }

//    public static CtTableModel CreateTableFieldValue( String table, String field, String value ) throws Throwable {
//        String query = "FROM "+table+" WHERE "+field+" = "+value;
//        return Create( query );
//    }
//
//    public static CtTableModel CreateTableWhere( String table, String whereClause ) throws Throwable {
//        String query = "FROM "+table+" WHERE "+whereClause;
//        return Create( query );
//    }

//    public void create( String query ) throws Throwable {
//        Session s = CtSession.Current();
//        s.beginTransaction();
//
//        Query q = s.createQuery( query );
//
//        List results = q.list();
//
//        create( results );
//
//        s.getTransaction().commit();
//    }
//
//    public static CtTableModel Create( Collection queryResults ) throws Throwable {
//        CtTableModel tm = new CtTableModel();
//        tm.create( queryResults );
//        return tm;
//    }

    @Override public void addTableModelListener( TableModelListener tml ) {
        _listeners.add( tml );
    } // Adds a listener to the list that is notified each time a change to the data model occurs.

    @Override public void removeTableModelListener( TableModelListener tml ) {
        _listeners.remove( tml );
    } // Removes a listener from the list that is notified each time a change to the data model occurs.

    protected int externalColumns() {
        return _columnIndices.size();
    }

    protected int internalColumnIndex( int externalColumnIndex ) throws IndexOutOfBoundsException {

        int columns = _columnIndices.size();

        if( externalColumnIndex >= columns ) {
            throw new IndexOutOfBoundsException();
        }

        int internalColumnIndex = _columnIndices.get( externalColumnIndex );
        return internalColumnIndex;
    }

    protected int externalColumnIndex( int internalColumnIndex ) throws IndexOutOfBoundsException {

        int columns = _columnIndices.size();

        for( int externalColumnIndex = 0; externalColumnIndex < columns; ++externalColumnIndex ) {
            if( _columnIndices.get( externalColumnIndex ) == internalColumnIndex ) {
                return externalColumnIndex;
            }
        }

        return columns;
    }

    public int pk( int rowIndex ) {
//if( columnIndex > getColumnCount()-2 ) return null;

        int internalColumnIndex = _columnIndexPK;

        Object[] objects = _rows.toArray();
//        Object row = _rows.get( rowIndex );
        Object row = objects[ rowIndex ];
        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];

        try{
            Method method = getMethod( f );
            Object o = method.invoke( row );
            Integer n = (Integer)o;
            int pk = (int)n;

            return pk;
        }
        catch( NoSuchMethodException nsme ) {
            System.err.println( "Can't access member X via member function (expected getX())." );
        }
        catch( IllegalAccessException iae ) {
            System.err.println( "Can't access member X on object to discover fields." );
        }
        catch( InvocationTargetException iae ) {
            System.err.println( "Can't invoke getX() on object to discover fields." );
        }
        catch( ClassCastException cce ) {
            System.err.println( "Can't cast primary key to expected type int." );
        }

        return 0;
    }

    protected boolean hidden( String fieldName ) {
        for( String hiddenField : _hiddenFields ) {
            if( fieldName.equals( hiddenField ) ) {
                return true;
            }
        }

        return false;
    }
    protected void createColumnIndex() {

        _columnIndices.clear();

        if( _cols == null ) {
            return;
        }
        
        int externalColumnIndex = 0;
        int internalColumnIndex = 0;

        for( Field f : _cols.getDeclaredFields() ) {

            String s = f.getName();

System.out.println( s );

            boolean skip = false;

            if( s.startsWith( "pk" ) ) {
               System.out.print( "PRIMARY KEY " );
//               skip = true;
               _columnIndexPK = internalColumnIndex;
            }

            if( s.startsWith( "ct" ) ) {
               System.out.print( "FOREIGN KEY " );
               Class< ? > clazz = f.getType();
               if( clazz.getName().contains( "Set" ) ) {
//               if(  instanceof java.util.Set )) {
//                   System.out.print( " set" );
                   skip = true;
               }
            }
            
            if( hidden( s ) ) {
                skip = true;
            }

            if( skip ) {
                ++internalColumnIndex;
            }
            else {
                _columnIndices.add( internalColumnIndex );

                ++internalColumnIndex;
                ++externalColumnIndex;
            }
        }
    }

    @Override public int getRowCount() {
        if( _rows == null ) return 0;
        int rows = _rows.size();

        if( _transientRow != null ) {
            ++rows;
        }
        
        return rows;
    } // Returns the number of rows in the model.

    @Override public int getColumnCount() {
        if( _cols == null ) return 0;
//        int cols = _cols.getDeclaredFields().length;
        int cols = externalColumns();
        return cols;
    } // Returns the number of columns in the model.

    public Class getRowClass() {
        Object o = getRowObject( 0 );
        Class c = o.getClass();
        return c; // TODO allow a way to specify when there's no rows in the table
    }

    @Override public Class getColumnClass( int columnIndex ) {
        int internalColumnIndex = internalColumnIndex( columnIndex );
        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];
//        Class< ? > clazz = f.getDeclaringClass();
        Class< ? > clazz = f.getType();

        return objectEquivalent( clazz );
//        return f.getType();
//        return f.
    } // Returns the most specific superclass for all the cell values in the column.

    protected Class objectEquivalent( Class< ? > clazz ) {

        String s = clazz.getName();

        if( s.equals( "int" ) ) {
                return Integer.class;
        }
        else if( s.equals( "long" ) ) {
                return Long.class;
        }

        return clazz;
    }

    public void substituteColumnName( String columnName, String substitute ) {
        _substituteColumnNames.put( columnName, substitute );
    }

    protected String substitutedColumnName( String columnName ) {
        Object o = _substituteColumnNames.get( columnName );
        
        if( o == null ) {
            return columnName;
        }
        
        String s = (String)o;
        
        return s;
    }

    @Override public String getColumnName( int columnIndex ) {
        int internalColumnIndex = internalColumnIndex( columnIndex );
        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];
        String name = f.getName();
        return substitutedColumnName( name );
    } // Returns the name of the column at columnIndex.

    @Override public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return _editable;
    } // Returns true if the cell at rowIndex and columnIndex is editable.

    public int getColumnIndex( String fieldName ) {
        int internalColumnIndex = getInternalColumnIndex( fieldName );
        int externalColumnIndex = externalColumnIndex( internalColumnIndex );
        return externalColumnIndex;
    }
    
    public int getInternalColumnIndex( String fieldName ) {

        int internalColumnIndex = 0;

        for( Field f : _cols.getDeclaredFields() ) {
            String s = f.getName();

            if( s.equals( fieldName ) ) {
                break;
            }

            ++internalColumnIndex;
        }

        return internalColumnIndex;
    }

    @Override public Object getValueAt( int rowIndex, int columnIndex ) {

//if( columnIndex > getColumnCount()-2 ) return null;

        int internalColumnIndex = internalColumnIndex( columnIndex );
        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];

        Object row = getRowObject( rowIndex );

        try{
            Method method = getMethod( f );
            Object value = method.invoke( row );
/* magic: Dave - what was this for again???
if( value.getClass().getName().contains( "ypes" ) ) {
    Class c2 = value.getClass();
    Method method2 = getMethod( c2, "name" );
    Object value2 = method2.invoke( value );
 //   Method method2 = getMethod( "name" );
    int g = 0;
    return value2;
}
 * 
 */
            return value;
        }
        catch( NoSuchMethodException nsme ) {
            System.err.println( "Can't access member X via member function (expected getX())." );
        }
        catch( IllegalAccessException iae ) {
            System.err.println( "Can't access member X on object to discover fields." );
        }
        catch( InvocationTargetException iae ) {
            System.err.println( "Can't invoke getX() on object to discover fields." );
        }

        return null;
    } // Returns the value for the cell at columnIndex and rowIndex.

//    public void addRow( Object[] rowData ) {
//
//    }
    public void deleteRow( int rowIndex ) {
        Object row = getRowObject( rowIndex );

////////////////////////////////////////////////////////////////////////////////
// DAVE
//        _rows.remove( rowIndex );
////////////////////////////////////////////////////////////////////////////////
// NHAT
        _rows.remove( row ); // fixed, was wrong command
////////////////////////////////////////////////////////////////////////////////

        rowDeleted( row );

        fireTableDataChanged();
    }

    public void appendRow() {

        if( _transientRow != null ) {
            return;
        }

        try {
            Class c = getRowClass();

            _transientRow = c.newInstance();

            fireTableDataChanged();
        }
        catch( InstantiationException ie ) {
            System.err.println( ie );
        }
        catch( IllegalAccessException iae ) {
            System.err.println( iae );
        }
        catch( NullPointerException npe ) {
            // nothing.
        }
    }

    public void persistRow() {
            if( _transientRow == null ) {
                return;
            }

            rowAdded( _transientRow );

            _rows.add( _transientRow );
            _transientRow = null;

            appendRow();
            
//            fireTableDataChanged();
    }
    
    protected Object getRowObject( int rowIndex ) {
        Object row = null;

        int rows = _rows.size();

        Object[] objects = _rows.toArray();

        if( rowIndex < rows ) {
//            row = _rows.get( rowIndex );
            row = objects[ rowIndex ];
        }
        else if( rowIndex == rows ) {
            if( _transientRow != null ) {
                row = _transientRow;
            }
        }
        return row;
    }

    @Override public void setValueAt( Object value, int rowIndex, int columnIndex ) {

        int internalColumnIndex = internalColumnIndex( columnIndex );

        Field f = _cols.getDeclaredFields()[ internalColumnIndex ];

        if( _lockedFields.contains( f.getName() ) ) {
            return;
        }

        Object row = getRowObject( rowIndex );
        
        try{
            Method method = setMethod( f );
            method.invoke( row, value );
        }
        catch( NoSuchMethodException nsme ) {
            System.err.println( "Can't access member X via member function (expected getX())." );
        }
        catch( IllegalAccessException iae ) {
            System.err.println( "Can't access member X on object to discover fields." );
        }
        catch( InvocationTargetException iae ) {
            System.err.println( "Can't invoke getX() on object to discover fields." );
        }

////////////////////////////////////////////////////////////////////////////////
// DAVE
//        if( row == _transientRow ) {
//            persistRow();
//        }
//        if( row != _transientRow ) {
//            rowChanged( row );
//        }
//
//        fireTableCellUpdated( rowIndex, columnIndex );
////////////////////////////////////////////////////////////////////////////////
// NHAT
        if( _saveFlag ) { // DAVE: if (save now)
            if( row == _transientRow ) {
                persistRow();
            }
            if( row != _transientRow ) {
                rowChanged( row );
            }

            fireTableCellUpdated( rowIndex, columnIndex );
        }
        else { // save later:
            _rowSaveIndices.add( rowIndex );
            _colSaveIndices.add( columnIndex );
            _value.add( value );
//            System.out.println("FIELDS = " + f.toString() + " INDEX = " + columnIndex);
//            System.out.println("ROWS = " + row.toString());
        }
// END
////////////////////////////////////////////////////////////////////////////////
    }

    public void rowAdded( Object row ) {  // Sets the value in the cell at columnIndex and rowIndex to aValue.

        Transaction t = null;

        try {
            Session s = CtSession.Current();
            t = s.beginTransaction();
            s.save( row );
            t.commit(); //you might even want to wrap this in another try/catch block.
        }
        catch( HibernateException he ) {
            // log.error(....);
            if( t != null ) {
                t.rollback();
            }
            throw he;
        }
        finally {

        }
    }

    public void rowChanged( Object row ) {  // Sets the value in the cell at columnIndex and rowIndex to aValue.

        // http://blog.sherifmansour.com/?p=236
//        Transaction t = s.beginTransaction();
//        s.update( row );
//        t.commit();
////        s.save( row );
////        s.flush();
        Transaction t = null;
        
        try {
            Session s = CtSession.Current();
            t = s.beginTransaction();
            s.update( row );
            t.commit(); //you might even want to wrap this in another try/catch block.
        }
        catch( HibernateException he ) {
            // log.error(....);
            if( t != null ) {
                t.rollback();
            }
            throw he;
        }
        finally {

        }
    }

    public void rowDeleted( Object row ) {  // Sets the value in the cell at columnIndex and rowIndex to aValue.

        Transaction t = null;

        try {
            Session s = CtSession.Current();
            t = s.beginTransaction();
            s.delete( row );
            t.commit(); //you might even want to wrap this in another try/catch block.
        }
        catch( HibernateException he ) {
            // log.error(....);
            if( t != null ) {
                t.rollback();
            }
            throw he;
        }
        finally {

        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // http://stackoverflow.com/questions/966743/display-hibernate-query-in-jtable
    // list< map >
    // map
    // map
    // map
//    ArrayList< Class< ? > > _fields;// = new ArrayList< Class< ? > >();

    protected void cols( Collection queryResults ) {

        _cols = null;

        if( queryResults.isEmpty() ) {
            return;
        }

//        Object o = queryResults.get( 0 );
        Object o = queryResults.iterator().next();

// *****************
//        Hibernate.in
        if( o.getClass().getName().contains( "$$EnhancerByCGLIB$$" ) ) {
//            Hibernate.initialize( o );
            Class< ? > clazz = o.getClass().getSuperclass();

            _cols = clazz;
        }
        else {
            Class< ? > clazz = o.getClass(); // this is the object's metadata. I don't know the classname but I can find out anything about it.

            _cols = clazz;
        }
// *****************
        
    }

    protected void rows( Collection queryResults ) {
        _rows = queryResults;
    }

//    List< Map > createTable( List queryResults ) throws Throwable {
////        List< Map > l = new LinkedList< Map >();
//        for( Object o : queryResults ) {
////             l.add( entityMap( o ) );
//            Class< ? > clazz = o.getClass(); // this is the object's metadata. I don't know the classname but I can find out anything about it.
//
//            for( Field f : clazz.getDeclaredFields() ) {
////                f.
//                String s = f.toString();
//                System.out.println( s );
//            }
//        }
//        return null;
//    }
//
//    Map entityMap( Object obj ) throws Throwable {
//        Map m = new HashMap();
//
//        for( Field f : getFields( obj.getClass() ) ) {
//            Method method = getMethod( f );
//            Object value = method.invoke( obj );
//            m.put( f, value );
//        }
//
//        return m;
//    }
//
//    List< Field > getFields( Class< ? > clazz ) {
//        List< Field > fields = new LinkedList< Field >();
//
//        for( Field field : clazz.getDeclaredFields() ) {
////            Column col = field.getAnnotation( Column.class );
////            if( col != null ) {
//                fields.add( field );
////            }
//        }
//        return fields;
//    }

    Method getMethod( Class< ? > clazz, String attributeName ) throws NoSuchMethodException {
//        Class< ? > clazz = field.getDeclaringClass();
        String name = "get" + uppercase( attributeName );
        Method method = clazz.getMethod( name );
        return method;
    }

    Method getMethod( Field field ) throws NoSuchMethodException {
        Class< ? > clazz = field.getDeclaringClass();
        String name = "get" + uppercase( field.getName() );
        Method method = clazz.getMethod( name );
        return method;
    }

    Method setMethod( Field field ) throws NoSuchMethodException {
        Class< ? > clazz = field.getDeclaringClass();
        String name = "set" + uppercase( field.getName() );
        Method method = clazz.getMethod( name, field.getType() );
        return method;
    }

    String uppercase( String str ) {
        return str.substring( 0,1 ).toUpperCase() + str.substring( 1 );
    }

}
