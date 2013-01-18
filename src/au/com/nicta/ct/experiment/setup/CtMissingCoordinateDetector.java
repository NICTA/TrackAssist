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

import au.com.nicta.ct.experiment.setup.util.CtImageFileNameParts;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.db.CtSession;


import org.hibernate.Session;
import org.hibernate.HibernateException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Dr. R Chakravorty
 */


public class CtMissingCoordinateDetector {

    //private CtExperiments               experiment;
    private List< CtImages >            images;
    private List< CtCoordinates >       coordinates;
    private List< CtImagesCoordinates > imagesCoordinates;
    //private String                      imageSourceDirectory;
    private List< CtImageFileNameParts >coordinateParts;

    ArrayList< Integer > coordinateMaxLimits;
    ArrayList< Integer > coordinateMinLimits;

    private String detectionResultString;

    public static String _missingImageMessage = "Some images are missing.";

    private int timeIndexRow;

    public ArrayList<String> missingCoordinateName;
    public ArrayList<Integer> missingCoordinateValue;
    public ArrayList<Integer> missingCoordinateTimeValue;

    public ArrayList<String> missingFileNames;


    public CtMissingCoordinateDetector( List< CtImages > im,
                                      List< CtCoordinates > coords,
                                      List< CtImagesCoordinates > imageCoords,
                                      List< CtImageFileNameParts > nameParts ) {

        this.coordinates       = coords;
        this.imagesCoordinates = imageCoords;
        this.images            = im;
        this.coordinateParts   = nameParts;

        detectionResultString = "";

        timeIndexRow = -1;

        missingCoordinateName      = new ArrayList<String>();
        missingCoordinateValue     = new ArrayList<Integer>();
        missingCoordinateTimeValue = new ArrayList<Integer>();
        missingFileNames           = new ArrayList<String>();

    }

    //this method finds the maximum and minimum of each of the
    //coordinate types
    //for coordinate types that are absent max = -1;min = -1
    private void detectMinMaxLimits( ) {

        coordinateMaxLimits = new ArrayList< Integer >();
        for(  CtImageFileNameParts np :  coordinateParts ) {
            coordinateMaxLimits.add(-1);
        }

        int coordinatePartIndex = 0;
        for(  CtImageFileNameParts np :  coordinateParts ) {
            for(  CtImagesCoordinates ic :  imagesCoordinates ) {
                if( ( ( ( ic.getCtCoordinates() ).getCtCoordinatesTypes() ).getName() ).equals( np.coordinateName ) ) {

                    if( coordinateMaxLimits.get( coordinatePartIndex ) < ( ic.getCtCoordinates() ).getValue() ) {
                        coordinateMaxLimits.set(coordinatePartIndex, ( ic.getCtCoordinates() ).getValue() );
                    }
                }
            }
            coordinatePartIndex++;
        }

        coordinatePartIndex = 0;
        coordinateMinLimits = new ArrayList< Integer >();
        for(  CtImageFileNameParts np :  coordinateParts ) {
            coordinateMinLimits.add( coordinateMaxLimits.get( coordinatePartIndex ) );
            coordinatePartIndex++;
        }

        coordinatePartIndex = 0;
        for(  CtImageFileNameParts np :  coordinateParts ) {
            for(  CtImagesCoordinates ic :  imagesCoordinates ) {
                if( ( ( ( ic.getCtCoordinates() ).getCtCoordinatesTypes() ).getName() ).equals( np.coordinateName ) ) {

                    if( coordinateMinLimits.get( coordinatePartIndex ) > ( ic.getCtCoordinates() ).getValue() ) {
                        coordinateMinLimits.set(coordinatePartIndex, ( ic.getCtCoordinates() ).getValue() );
                    }
                }
            }
            coordinatePartIndex++;
        }
       
    }

    //this method detects if there is any missing coordinate
    //if yes - it also detects which one and creates dummy
    //image file names
    public boolean detectMissingCoordinates() {

        detectionResultString = "";
        
        detectMinMaxLimits();
        

        //a 2D array to store all possible values of each coordinate type.
        ArrayList<ArrayList> rows = new ArrayList<ArrayList>();
        for( int totalCoordinates = 0; totalCoordinates < coordinateMaxLimits.size() ; totalCoordinates++ ) {
            ArrayList cols = new ArrayList();

            for ( Integer limit = coordinateMinLimits.get(totalCoordinates) ; limit <= coordinateMaxLimits.get(totalCoordinates) ; limit++ ) {
                cols.add( limit );
            }
            rows.add( cols );
            if( ( ( coordinateParts.get( totalCoordinates ) ).coordinateName ).equals("time") ) {
                timeIndexRow = totalCoordinates;
            }
        }

        
        /*for( int totalCoordinates = 0; totalCoordinates < coordinateMaxLimits.size() ; totalCoordinates++ ) {
            ArrayList cols = rows.get( totalCoordinates );
            System.out.println( this.coordinateParts.get(totalCoordinates).coordinateName+" "+ cols );
        }*/

        //first it finds out how many image files should there be
        int totalValidCount = 1;
        for( int totalCoordinates = 0; totalCoordinates < coordinateMaxLimits.size() ; totalCoordinates++ ) {

            if( coordinateMaxLimits.get( totalCoordinates ) == -1 ||
                    coordinateMinLimits.get( totalCoordinates ) == -1 )
                continue;

            ArrayList cols = rows.get( totalCoordinates );

            totalValidCount *= cols.size();
        }

        //if actual image counts is less than that, there must be something
        //missing
        if ( images.size() < totalValidCount ) {

            //if missing

            //identify the missing coordinate points
            identifyMissingCoordinates( rows );

            //create the dummy file names
            createMissingFileNames( rows );
            detectionResultString = _missingImageMessage;
            return false;
        }
        return true;
    }

    public String getResultString() {
        return detectionResultString;
    }

    public ArrayList<String> getMissingFileNames() {
        return this.missingFileNames;
    }

    private void createMissingFileNames( ArrayList<ArrayList> rows ) {

        int totalMissingFiles = missingCoordinateName.size();
        
        for( int rowIndex = 0; rowIndex < totalMissingFiles ; rowIndex ++ ) {

            ArrayList<String> newFileNames = new ArrayList<String>();

            int newFilesRequired = 1;

            for( int partIndex = 0; partIndex < coordinateParts.size() ; partIndex ++ ) {

                if( ( coordinateParts.get( partIndex ).coordinateName ).equals( "time" ) ||
                ( coordinateParts.get( partIndex ).coordinateName ).equals( missingCoordinateName.get( rowIndex ) ) ) {
                    continue;
                }
                ArrayList cols = rows.get( partIndex );

                if( (Integer) cols.get( 0 ) == -1 ) {
                    continue;
                }

                newFilesRequired *= cols.size();

            }
            for( int limitIndex  = 0 ; limitIndex < newFilesRequired ; limitIndex ++ ) {


                //recreating the file name according to the parsing rule
                String fileName = "";

                for( int partIndex = 0; partIndex < coordinateParts.size() ; partIndex ++ ) {

                    CtImageFileNameParts part = this.coordinateParts.get( partIndex );

                    if( ( part.coordinateName).equals( CtFileNameParsingTableModel._ignore ) ) {

                        if( !part.isFixedWidth ) {

                            if( ! ( part.coordinatePartString ).equals( part.widthDelimeterString ) ) {
                                fileName = fileName +  (String) part.coordinatePartString + part.widthDelimeterString ;
                            }
                            else {
                                fileName = fileName +  (String) part.coordinatePartString ;
                            }
                        }
                        else {
                            fileName = fileName +  (String) part.coordinatePartString;
                        }
                    }

                    else if( (part.coordinateName).equals( missingCoordinateName.get(rowIndex) ) ) {
                        
                        if( !part.isFixedWidth ) {
                            fileName = fileName +  missingCoordinateValue.get( rowIndex ).toString()+ part.widthDelimeterString;
                        }
                        else {
                            fileName = fileName + String.format( "%" + Integer.valueOf(part.widthDelimeterString) +"s",  missingCoordinateValue.get( rowIndex ).toString() ).replace(' ', '0');
                        }
                    }
                    else if((part.coordinateName).equals( "time" )) {
                        
                        if( !part.isFixedWidth ) {
                            fileName = fileName +  this.missingCoordinateTimeValue.get( rowIndex ).toString()+ part.widthDelimeterString;
                        }
                        else {
                            fileName = fileName + String.format( "%" + Integer.valueOf( part.widthDelimeterString ) +"s",  missingCoordinateTimeValue.get( rowIndex ).toString() ).replace(' ', '0');
                        }
                    }
                    else {
                        if( !part.isFixedWidth ) {
                            fileName = fileName +  (String) part.coordinatePartString+ part.widthDelimeterString;
                        }
                        else {
                            fileName = fileName +  String.format( "%" + Integer.valueOf( part.widthDelimeterString ) +"s",  part.coordinatePartString ).replace(' ', '0');;
                        }
                    }
                }
                newFileNames.add( limitIndex, fileName);
                //System.out.println ( fileName );
            }
            for ( int limitIndex  = 0 ; limitIndex < newFilesRequired ; limitIndex ++ ) {

                    missingFileNames.add( newFileNames.get( limitIndex ) );
            }
        }
    }

    //identifying which coordinate point is missing
    private void identifyMissingCoordinates ( ArrayList<ArrayList> rows ) {

        for(int totalCoordinates = 0; totalCoordinates < coordinateParts.size() ; totalCoordinates++ ) {

            if( totalCoordinates == timeIndexRow )
                continue;
            
            ArrayList cols = rows.get( totalCoordinates );

            if( (Integer) ( cols.get( 0 ) ) == -1 )
                continue;
            
            String coordinateName = ( coordinateParts.get( totalCoordinates ) ).coordinateName;

            System.out.println( coordinateName );

            ArrayList timeRow = rows.get( timeIndexRow );

            for( int index = 0; index < cols.size(); index++  ) {

                int value = ( Integer ) cols.get( index );
                ArrayList<Integer> present = new ArrayList<Integer>();

                for( int ii = 0 ; ii < timeRow.size(); ii ++) {
                    present.add( -1 );
                }

                List<CtImages> axisImages = new ArrayList<CtImages>();

                for( CtCoordinates c : this.coordinates ) {

                    if( ( ( c.getCtCoordinatesTypes() ).getName() ).equals( coordinateName ) &&
                        ( ( c.getValue() ) == value ) ) {

                        for( CtImagesCoordinates cic : this.imagesCoordinates ) {

                            if( ( cic.getCtCoordinates() ).equals( c ) ) {

                                axisImages.add( cic.getCtImages( ) );
                            }
                        }
                    }
                }

                for( CtImages ci : axisImages ) {

                    for( CtImagesCoordinates cic : this.imagesCoordinates  ) {

                        if( ( cic.getCtImages() ).equals( ci )  &&
                            ( ( ( ( cic.getCtCoordinates() ).getCtCoordinatesTypes() ) ).getName() ).equals( "time" ) ) {

                            present.set( (int) ( ( cic.getCtCoordinates() ).getValue() - (Integer) timeRow.get( 0 ) ) , 1 ) ;
                        }
                    }
                }

                for( int i=0 ;i < present.size() ; i ++ ) {

                    if( present.get( i ) == -1 ) {

                             missingCoordinateName.add( coordinateName );
                            missingCoordinateValue.add( value );
                        missingCoordinateTimeValue.add( (Integer) timeRow.get( i ) );
                    }
                }
            }
        }
    }
    
}

