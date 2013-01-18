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

package au.com.nicta.ct.experiment.setup;


import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.Date;
import java.text.DateFormat;


public class CtFileNameParsingTableModel extends AbstractTableModel implements TableModelListener{

    public static final String   _error     = "ERROR";
    public static final String   _ignore    = "N/A";

    private static final String[] names     = {"Name Part","Width/Delimeter","Fixed Width?","Coordinates","Coordinate Type","Parsed Result"};

    private static final String   _partColor  = "Black";
    private static final String   _delimColor = "Red";

    private static final String   _timeAxis   = "time";


    public  List< String  >   parts               = new ArrayList< String > ();
    public  List< Boolean >   choiceFixedWidth    = new ArrayList< Boolean >();
    public  List< String  >   widthDelimeter      = new ArrayList< String > ();
    public  List< String  >   coordinates         = new ArrayList< String > ();
    public  List< String  >   coordinateDataTypes = new ArrayList< String > ();
    public  List< String  >   parseResults        = new ArrayList< String > ();
    public  List< Integer >   startIndexes        = new ArrayList< Integer >();
    public  List< Integer >   endIndexes          = new ArrayList< Integer >();
    public  List< String  >   displayParts        = new ArrayList< String > ();

    CtFileNameParsingPanel parentPanel;

    String startingString;

    private String validationResult;

    public CtFileNameParsingTableModel( CtFileNameParsingPanel p,
                                        String startingString ) {

        addTableModelListener( this );
        this.parentPanel = p;
        this.startingString = startingString;

        validationResult = "";
    }

    public void resetModel( String part,
                            String coordinate ) {

        parts              .clear();
        choiceFixedWidth   .clear();
        widthDelimeter     .clear();
        coordinates        .clear();
        coordinateDataTypes.clear();
        parseResults       .clear();
        displayParts       .clear();

        addNewRow( part,
                    false,
                    "",
                    coordinate,
                    CtFileNameParsingPanel._typeNone,
                    part,
                    0,
                    part.length(),
                    false );

        this.startingString = part;
        this.fireTableDataChanged();
    }

    public void replaceRow( String part,
                            boolean isFixedWidth,
                            String widthDelim,
                            String cmbCoordinates,
                            String cmbCoordinateDataTypes,
                            String parseResult,
                            int startIndex,
                            int endIndex,
                            int rowIndex,
                            boolean joinPartDelimeter ) {


        if( rowIndex<0 || rowIndex>getRowCount() ) {
            return;
        }

                   parts.set( rowIndex, part                  );
        choiceFixedWidth.set( rowIndex, isFixedWidth          );
          widthDelimeter.set( rowIndex, widthDelim            );
             coordinates.set( rowIndex, cmbCoordinates        );
     coordinateDataTypes.set( rowIndex, cmbCoordinateDataTypes);
            parseResults.set( rowIndex, parseResult           );
            startIndexes.set( rowIndex, startIndex            );
              endIndexes.set( rowIndex, endIndex              );

       if( !isFixedWidth && !part.equals( widthDelim ) && joinPartDelimeter ) {

           displayParts.set( rowIndex, "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+widthDelim+
                                         "</font></html>");
       }
       else if( !joinPartDelimeter ) {
           displayParts.set( rowIndex, "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+""+
                                         "</font></html>");
       }

    }
    public void addNewRow( String part,
                           boolean isFixedWidth,
                           String widthDelim,
                           String cmbCoordinates,
                           String cmbCoordinateDataTypes,
                           String parseResult,
                           int startIndex,
                           int endIndex,
                           boolean joinPartDelimeter) {

                       parts.add( part                  );
            choiceFixedWidth.add( isFixedWidth          );
              widthDelimeter.add( widthDelim            );
                 coordinates.add( cmbCoordinates        );
         coordinateDataTypes.add( cmbCoordinateDataTypes);
                parseResults.add( parseResult           );
                startIndexes.add( startIndex            );
                  endIndexes.add( endIndex              );

        if( !isFixedWidth && !part.equals( widthDelim) && joinPartDelimeter ) {

           displayParts.add( "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+widthDelim+
                                         "</font></html>");
        }
        else if( !joinPartDelimeter ) {
           displayParts.add( "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+""+
                                         "</font></html>");
       }

    }

    public void addNewRowAt( String part,
                             boolean isFixedWidth,
                             String widthDelim,
                             String cmbCoordinates,
                             String cmbCoordinateDataTypes,
                             String parseResult,
                             int startIndex,
                             int endIndex,
                             int rowIndex,
                             boolean joinPartDelimeter ) {

        if( rowIndex<0 || rowIndex>getRowCount() ) {
            return;
        }
                       parts.add( rowIndex, part                 );
            choiceFixedWidth.add( rowIndex,isFixedWidth          );
              widthDelimeter.add( rowIndex,widthDelim            );
                 coordinates.add( rowIndex,cmbCoordinates        );
         coordinateDataTypes.add( rowIndex,cmbCoordinateDataTypes );
                parseResults.add( rowIndex,parseResult           );
                startIndexes.add( rowIndex,startIndex            );
                  endIndexes.add( rowIndex,endIndex              );

       if( !isFixedWidth && !part.equals( widthDelim) && joinPartDelimeter ) {
           displayParts.add( rowIndex, "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+widthDelim+
                                         "</font></html>");
       }
       else if( !joinPartDelimeter ) {
           displayParts.add( rowIndex, "<html><font color=\""+_partColor+"\">"+part+
                                         "</font><font color=\""+_delimColor+"\">"+""+
                                         "</font></html>");
       }

    }

    @Override
    public int getRowCount() {
        return parts.size();
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public Object getValueAt( int row, int col ) {

        if( col == 0 ) {
            return displayParts.get ( row );
        }
        else if( col == 1 ) {
            return widthDelimeter.get( row );
        }
        else if( col == 2 ) {
            return ( choiceFixedWidth.get( row ) );
        }
        else if( col==3 ) {
            return ( coordinates.get( row ) );
        }
        else if( col==4 ) {
            return coordinateDataTypes.get( row );
        }
        else if( col==5 )
            return parseResults.get( row );

        else if( col==6 )
            return startIndexes.get( row );

        else
            return endIndexes.get( row );
    }

    @Override
    public void setValueAt( Object o,int row, int col ) {

        if( col == 0 ) {
            displayParts.set( row,(String ) o);
        }
        else if( col == 1 ) {
            widthDelimeter.set( row,( String ) o );
        }
        else if( col == 2 ) {
            this.fireTableCellUpdated( row , col );
            choiceFixedWidth.set( row,( Boolean ) o );
        }
        else if( col==3 ) {
            coordinates.set( row,( String ) o );

            if( ! ( ( String ) getValueAt( row, col )).equals( CtFileNameParsingTableModel._ignore  ) ) {
                coordinateDataTypes.set( row, CtFileNameParsingPanel._typeNumber );
            }
            else {
                coordinateDataTypes.set( row, CtFileNameParsingPanel._typeNone );
            }
        }
        else if( col==4 ) {
            coordinateDataTypes.set( row,( String ) o );
        }
        else{
            parseResults.set( row,( String ) o );
        }

        if( col==1 || col==2 || col==4 ) {
            this.fireTableCellUpdated( row, col );

        }
        if( col == 3 ) {
            this.fireTableCellUpdated( row, 3 );
            this.fireTableCellUpdated( row, 4 );
        }
        return;
    }

    @Override
    public Class<?> getColumnClass( int col ) {
        if( col == 2 ) {
            return Boolean.class;
        }
        else {
            return String.class;
        }
    }

    @Override
    public String getColumnName( int col ) {
        return names[col];
    }

    @Override
    public boolean isCellEditable( int row, int col ) {

        if( col ==  0 ) {
            return false;
        }
        if( col == 1 ) {
            return true;
        }
        if( col == 2 ) {
            return true;
        }
        if( col == 3 ) {
            return true;
        }
        if( col == 4 ) {

            if( getValueAt( row, 3 ) == CtFileNameParsingTableModel._ignore ) {
                return false;
            }
            else{
                return true;
            }
        }
        if( col == 5 ) {
            return false;
        }
        return false;
    }

    public void tableChanged( TableModelEvent e ) {
        int column  = e.getColumn();
        int row     = e.getFirstRow();

        if( column==2  || column ==1 ) {
            reParse( row,column );
        }

        else if( column == 3 ) {
            if( ( coordinates.get( row ) ) == null ) {
                coordinates.set( row, CtFileNameParsingTableModel._ignore );
            }
            if( ( coordinates.get( row ) ).equals( CtFileNameParsingTableModel._ignore ) ) {

                parseResults       .set( row , parts.get(row) );
                coordinateDataTypes.set( row , CtFileNameParsingPanel._typeNone );

                this.fireTableRowsUpdated( row, row );
            }
        }

        else if( column == 4 ) {
            convert( row,column );
        }
    }

    public void reParse( int row, int col ) {

        if( this.choiceFixedWidth.get( row ) ) {
            reParseFixedWidth( row,col );
        }
        
        else{
            reParseDelimeter ( row,col );
        }
    }

    public void reParseDelimeter( int row,int col ) {

        String delimeterString = (String) this.getValueAt( row, 1);  //this.widthDelimeter.get( row );

        String previousString ="";
        for( int rowIndexBefore=0; rowIndexBefore<row; rowIndexBefore++ ) {

            previousString+=parts.get( rowIndexBefore );

            //the following lines are added
            //in the case that the delimeter
            //chosen will not be shown in the
            //following row

            if( !choiceFixedWidth.get(rowIndexBefore )  &&
                !( parts.get( rowIndexBefore ) ).equals( widthDelimeter.get( rowIndexBefore ) )  ) {

                previousString += widthDelimeter.get( rowIndexBefore );
            }
        }

        String remainingString = startingString;

        if( previousString.length()!=0 )
            remainingString = startingString.substring( previousString.length() );


        int startIndex = 0, endIndex = remainingString.length();
        if( remainingString.length() == 1 )
            endIndex = 0;
        else
            endIndex   = remainingString.indexOf( delimeterString, startIndex );

        if( widthDelimeter.get( row ).equals( "" )  ||
                                      endIndex == -1     ) {
           
            startIndex = 0;
            endIndex   = remainingString.length();
        }

        String newPart="", suffixString="";

        try {
            if( endIndex == 0 ) {
                newPart          = delimeterString;
            }
            else{
                newPart           = remainingString.substring( startIndex,endIndex );
            }
        }
        catch( StringIndexOutOfBoundsException oob ) {
            return;
        }

        if( endIndex  + delimeterString.length()<remainingString.length() )
        {
            suffixString  = remainingString.substring( endIndex  + delimeterString.length() );
        }
            

        for( int rowIndex=getRowCount() -1; rowIndex >= row; rowIndex-- ) {
            parts              .remove( rowIndex );
            choiceFixedWidth   .remove( rowIndex );
            widthDelimeter     .remove( rowIndex );
            coordinates        .remove( rowIndex );
            coordinateDataTypes.remove( rowIndex );
            parseResults       .remove( rowIndex );
            startIndexes       .remove( rowIndex );
            endIndexes         .remove( rowIndex );
            displayParts       .remove( rowIndex );
        }

        if( !newPart.equals( "" ) ) {

            if( !newPart.equals( delimeterString ) ) {

                if( endIndex   != remainingString.length() ) {
                    this.addNewRow( newPart, false, delimeterString, CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, newPart,startIndex+previousString.length(),endIndex+previousString.length(), true );
                }
                else {
                    this.addNewRow( newPart, false, delimeterString, CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, newPart,startIndex+previousString.length(),endIndex+previousString.length(), false );
                }

            }
            else {
                this.addNewRow( newPart, false, delimeterString, CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, newPart,startIndex+previousString.length(),endIndex+previousString.length(), false );
            }
        }
        
        if( !suffixString.equals( "" ) ) {
            this.addNewRow( suffixString, false, "", CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, suffixString,-1,-1, false );
        }

        this.fireTableDataChanged();
    }


    public void reParseFixedWidth( int row, int col ) {

        String widthString = (String) this.getValueAt( row, 1);
        
        int startIndex = 0 , endIndex = 0;

        try {
            if( ! widthString.equals( "" ) ) {
                endIndex   = Integer.parseInt( widthString  );
            }
        }
        catch( Exception e ) {
            showError( "The fixed width input was expected to be numeric" );
            if( choiceFixedWidth. get( row ) ) {
                reParseDelimeter( row, col );
            }
            this.fireTableRowsUpdated(row, row);
            return;
        }

        String previousString ="";
        for( int rowIndexBefore=0; rowIndexBefore<row; rowIndexBefore++ ) {
            previousString+=parts.get( rowIndexBefore );

            //the following lines are added
            //in the case that the delimeter
            //chosen will not be shown in the
            //following row
            if( !choiceFixedWidth.get(rowIndexBefore ) &&
                !( parts.get( rowIndexBefore ) ).equals( widthDelimeter.get( rowIndexBefore ) ) ) {

                previousString += widthDelimeter.get( rowIndexBefore );
            }
        }

        String remainingString = startingString;
        if( previousString.length()!=0 ) {
            remainingString = startingString.substring( previousString.length() );
        }

        if( endIndex > remainingString.length() ) {
            endIndex = parts.get( row ).length();
            this.widthDelimeter.set( row , Integer.toString( endIndex ) );
        }

        if( widthDelimeter.get( row ).equals( "" ) ) {
            startIndex = 0;
            endIndex   = remainingString.length();
        }

        String suffixString="";
        String newPart = remainingString.substring( startIndex,endIndex );

        if( endIndex <= remainingString.length() -1 ) {
            suffixString=remainingString.substring( endIndex );
        }

        for( int rowIndex=getRowCount()-1; rowIndex>=row; rowIndex-- ) {
            parts              .remove( rowIndex );
            choiceFixedWidth   .remove( rowIndex );
            widthDelimeter     .remove( rowIndex );
            coordinates        .remove( rowIndex );
            coordinateDataTypes.remove( rowIndex );
            parseResults       .remove( rowIndex );
            startIndexes       .remove( rowIndex );
            endIndexes         .remove( rowIndex );
            displayParts       .remove( rowIndex );
        }

        if( !newPart.equals( "" ) ) {

            if( ! widthString.equals( "" ) ) {
                this.addNewRow( newPart, true, widthString, CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, newPart,startIndex+previousString.length(),endIndex+previousString.length(),false );
            }
            else{
                this.addNewRow( newPart, false, widthString, CtFileNameParsingTableModel._ignore, CtFileNameParsingPanel._typeNone, newPart,startIndex+previousString.length(),endIndex+previousString.length(), false );
            }
        }
        if( !suffixString.equals( "" ) ) {
            this.addNewRow( suffixString, false, "", CtFileNameParsingTableModel._ignore , CtFileNameParsingPanel._typeNone, suffixString,-1,-1, false );
        }

        this.fireTableDataChanged();
    }

    public void convert( int row, int column ) {

        String dataType = ( String ) this.getValueAt( row, column );

        if( dataType.equals( CtFileNameParsingPanel._typeNumber ) ) {

            try{
                int value       = Integer.parseInt( parts.get( row ) );
                parseResults.set( row, Integer.toString( value    ) );
            }
            catch( Exception e ) {

                parseResults.set( row,CtFileNameParsingTableModel._error );
                this.fireTableRowsUpdated( row, row );
                
                showError( "Cannot conver the part into a number" );
                return;
            }
        }
        
        if( dataType.equals( CtFileNameParsingPanel._typeDateTime ) ) {

            try{
                Date dt = DateFormat   .getInstance().parse( parts.get( row ) );
                parseResults.set( row, dt.toString() );
            }
            catch( Exception e ) {
                
                parseResults.set( row,CtFileNameParsingTableModel._error );
                this        .fireTableRowsUpdated( row, row );
                showError( "Cannot convert the part into a date/time" );
                return;

            }
        }
        if( dataType.equals( CtFileNameParsingPanel._typeString ) ||
            dataType.equals( CtFileNameParsingPanel._typeNone ) ) {

            parseResults.set( row,parts.get( row ) );
        }
        this.fireTableRowsUpdated( row, row );
    }

    public void refreshCoordinates() {
        parentPanel.resetCoordinates();
    }

    public void showError( String errString ) {
        javax.swing.JOptionPane.showMessageDialog( parentPanel, errString , "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
    }

    public boolean validateEntries() {

        validationResult = "";
        
        boolean validEntry = false;
        int tableRows      = getRowCount();

        //first check if all are ignores
        for( int rowIndex=0; rowIndex < tableRows; rowIndex++ ) {
            if( ! ( ( String ) getValueAt( rowIndex, 3 ) ).equals( CtFileNameParsingTableModel._ignore ) ) {
                validEntry = true;
                break;
            }
        }

        if( !validEntry ) {
            validationResult = "There was no coordinate type assinged to"
                       + " any part of the file name";
            return false;
        }
        
        //next if there was any error in the parsing
        for(  int rowIndex=0; rowIndex< tableRows; rowIndex++ ) {
            if( ( ( String ) getValueAt( rowIndex, 5 ) ).equals( CtFileNameParsingTableModel._error ) ) {
                validationResult = "There was error in parsing some "
                           + "parts of the file name. Please fix it "
                           + "before continuing";

                return false;
            }
        }

        //next if there was any coordinate part assigned to multiple parts of the file name
        for( int outerLoopIndex=0; outerLoopIndex< tableRows -1;  outerLoopIndex++ ) {
            for( int innerLoopIndex=outerLoopIndex+1;  innerLoopIndex < tableRows; innerLoopIndex++ ) {

                if( ( getValueAt( innerLoopIndex , 3 ) ).equals( ( getValueAt( outerLoopIndex,3 ) ) )
                    &&( getValueAt( innerLoopIndex , 4 ) ).equals(  CtFileNameParsingPanel._typeNumber ) ) {

                    validationResult = "Some coordinates are assigned more than once";
                    return false;
                }
            }
        }

        //next if there was any coordinate types not assigned to any data type
        for( int rowIndex=0; rowIndex < tableRows; rowIndex++ ) {

            if(    !( getValueAt( rowIndex , 3 ) ).equals( CtFileNameParsingTableModel._ignore )
                 && ( getValueAt( rowIndex,  4 ) ).equals( CtFileNameParsingPanel._typeNone ) ) {

                    validationResult = "Some coordinates data types are not assigned.";
                    return false;
            }
        }

        boolean isTimePartAvailable = false;
        //next if there was any coordinate types not assigned to any data type
        for( int rowIndex=0; rowIndex < tableRows; rowIndex++ ) {

            if(    ( getValueAt( rowIndex , 3 ) ).equals( CtFileNameParsingTableModel._timeAxis ) ) {
                isTimePartAvailable = true;
                break;
            }
        }

        if( !isTimePartAvailable ) {
            validationResult = "Please assign a time indicator in the file name before continuing";
            return false;
        }

        return true;
    }

    public String getValidationResult() {
        return validationResult;
    }
}
