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
import au.com.nicta.ct.db.hibernate.CtSolutions;

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
public class CtDatabaseWorker {
//worker?

    public static final String DEFAULT_SOLUTION_NAME = "default";
    public static final String _successMessage = "Success";
    
    public CtExperiments experiment;
    ////////////////////////////////////////////////////////////////////////////////
    public CtSolutions solution; // DAVE: For now, 1 soln per experiment. We will expand later if reqd. The Soln will automatically be created and tied to the expt.
    ////////////////////////////////////////////////////////////////////////////////
    public List< CtImages >            images;
    public List< CtCoordinates >       coordinates;
    public List< CtImagesCoordinates > imagesCoordinates;
    public String                      imageSourceDirectory;


    public ArrayList<String>           missingFileNames;

    public CtDatabaseWorker( String experimentName, String experimentURI, String imageSourceDirectory) {
        experiment = new CtExperiments();
        experiment.setName( experimentName );
        experiment.setUri ( experimentURI  );
        ////////////////////////////////////////////////////////////////////////////////
        solution = new CtSolutions();
        solution.setName( DEFAULT_SOLUTION_NAME );
        solution.setCtExperiments( experiment );
        ////////////////////////////////////////////////////////////////////////////////
        this.imageSourceDirectory = imageSourceDirectory;

        missingFileNames = new ArrayList<String>();
    }

    public String createImages(ArrayList< String > imageFileNames ) {

        images = new ArrayList< CtImages > ();

        try {
            int totalFiles = imageFileNames.size();

            for(  int fileNo=0; fileNo<totalFiles; fileNo++  ) {
                
                CtImages image = new CtImages();

                String experimentURI = experiment.getUri();

                if( ( experimentURI.substring( experimentURI.length() - 1 ) ).equals( File.separator ) ) {
                    image.setUri(  experimentURI + imageFileNames.get( fileNo )  );
                }
                else {
                    image.setUri( experimentURI + File.separator + imageFileNames.get( fileNo ) );
                }
                image.setCtExperiments( experiment );
                images.add( image );
            }
            return CtDatabaseWorker._successMessage;
        }
        catch(  HibernateException he  ) {
            he.printStackTrace();
            return "Error while creating image sets";
        }
    }

    public void saveImages( Session s ) throws HibernateException {

        int totalImages = images.size();
        for(  int imageNo=0; imageNo < totalImages ; imageNo++ ) {

            Serializable ss = s.save( images.get( imageNo ) );
            ( images.get( imageNo ) ).setPkImage( Integer.parseInt( ss.toString() ) );
        }
    }

    public String createCoordinatesImagesCoordinates( List< CtImageFileNameParts > coordinateParts ) {

        coordinates       = new ArrayList< CtCoordinates >();
        imagesCoordinates = new ArrayList< CtImagesCoordinates >();

        for( CtImages im : images ) {

            String imageURI        = im.getUri();
            String currentFileName = imageURI.substring( imageURI.lastIndexOf( File.separator ) + 1);
            String remainingPart   = currentFileName;


            
            for(  CtImageFileNameParts np :  coordinateParts ) {

                if( ! ( np.dataType ).equals( CtFileNameParsingPanel._typeNumber ) ) {

                    boolean isFixedWidth        =   np .isFixedWidth;
                    String widthDelimeterString =   np .widthDelimeterString;

                    if( isFixedWidth ) {
                        remainingPart =   remainingPart.substring( Integer.parseInt( widthDelimeterString ) );
                    }
                    else {
                        int indexOfDelimeter   = remainingPart.indexOf( widthDelimeterString );

                        if( widthDelimeterString.isEmpty() ) {
                            indexOfDelimeter = ( np.coordinatePartString ).length();
                        }
                        remainingPart          = remainingPart.substring( indexOfDelimeter + widthDelimeterString.length());
                    }
                    continue;
                }

                boolean isFixedWidth        =   np .isFixedWidth;
                String widthDelimeterString =   np .widthDelimeterString;

                int coordinateValue;
                try {
                    if( isFixedWidth ) {

                        int partLength  = Integer.parseInt       ( np .widthDelimeterString );
                        coordinateValue = Integer.parseInt       ( remainingPart.substring( 0 , partLength ) );
                        remainingPart   = remainingPart.substring( partLength );
                    }
                    else{

                        int indexOfDelimeter   = remainingPart .indexOf( widthDelimeterString );

                        coordinateValue  = Integer.parseInt( remainingPart.substring( 0, indexOfDelimeter ) );
                        remainingPart    = remainingPart .substring( indexOfDelimeter + widthDelimeterString.length() );

                        
                    }
                }
                catch( Exception e ) {
                    coordinates     .clear();
                    imagesCoordinates.clear();
                    return "Could not convert into numeric. Failed on " + currentFileName;
                }

                CtCoordinates      co     = new CtCoordinates();
                
                CtCoordinatesTypes cTypes = ( CtCoordinatesTypes )
                                              CtSession.getObject( "from CtCoordinatesTypes where name='"+ np.coordinateName+"'" );

                System.out.println( "Info "+co.getName()+co.getValue() + co.getCtImagesCoordinateses().size() );

                co.setValue( coordinateValue );
                co.setCtCoordinatesTypes( cTypes );

                CtImagesCoordinates ic = new CtImagesCoordinates();
                ic .setCtCoordinates( co );
                ic .setCtImages( im );

                co.getCtImagesCoordinateses().add( ic );
                im.getCtImagesCoordinateses().add( ic );

                coordinates      .add( co );
                imagesCoordinates.add( ic );
            }
        }

        //now check if any coordinate point is missing or not
        CtMissingCoordinateDetector missingCoordinateDetector = new CtMissingCoordinateDetector( images,
                                                                     coordinates,
                                                                     imagesCoordinates,
                                                                     coordinateParts);
        if( !missingCoordinateDetector.detectMissingCoordinates() ) {

            //if there is a missing coordinate,
            //temporarily save the missing file names
            //if the users wants to continue, these names
            //will be retrieved from here.
            missingFileNames.clear();
            missingFileNames = missingCoordinateDetector.getMissingFileNames();
            return missingCoordinateDetector.getResultString();
        }

        return CtDatabaseWorker._successMessage;
    }

    public void saveExperiment( Session s ) throws HibernateException {
        Serializable ss = s.save( experiment );
        experiment.setPkExperiment( Integer.parseInt( ss.toString() ) );
        ////////////////////////////////////////////////////////////////////////////////
        Serializable ss2 = s.save( solution );
        solution.setPkSolution( Integer.parseInt( ss2.toString() ) );
        experiment.getCtSolutionses().add( solution );
        s.update(experiment);
        ////////////////////////////////////////////////////////////////////////////////
    }
    
    public void saveCoordinatesImagesCoordinates( Session s ) throws HibernateException {
        saveCoordinates( s );
        saveImagesCoordinates( s );
    }

    public void saveCoordinates( Session s  ) throws HibernateException {
        for(int coordinateIndex=0; coordinateIndex < coordinates.size(); coordinateIndex++ ) {
            Serializable ss = s.save( coordinates.get( coordinateIndex ) );
            ( imagesCoordinates.get( coordinateIndex ).getCtCoordinates() ).setPkCoordinate( Integer.parseInt( ss.toString() ) );
        }
    }

    public void saveImagesCoordinates( Session s ) throws HibernateException {
        for( CtImagesCoordinates cic : imagesCoordinates ) {
            s.save( cic );
        }
    }

    public String addMissingImagesANDImagesCoordinates( List< CtImageFileNameParts > coordinateParts ) {

        if( missingFileNames.isEmpty() ) {
            return CtDatabaseWorker._successMessage;
        }

        List<CtImages> newImages = new ArrayList<CtImages>();

        for(  int fileNo=0; fileNo < missingFileNames.size() ; fileNo++  ) {

                CtImages image = new CtImages();

                String experimentURI = experiment.getUri();

                if( ( experimentURI.substring( experimentURI.length() - 1 ) ).equals( File.separator ) ) {
                    image.setUri(  experimentURI + missingFileNames.get( fileNo )  );
                }
                else {
                    image.setUri( experimentURI + File.separator + missingFileNames.get( fileNo ) );
                }
                image.setCtExperiments( experiment );
                newImages.add( image );
                images.add( image );
        }
        
        for( CtImages im : newImages ) {

            String imageURI        = im.getUri();
            String currentFileName = imageURI.substring( imageURI.lastIndexOf( File.separator ) + 1);
            String remainingPart   = currentFileName;


            
            for(  CtImageFileNameParts np :  coordinateParts ) {

                if( ! ( np.dataType ).equals( CtFileNameParsingPanel._typeNumber ) ) {

                    boolean isFixedWidth        =   np .isFixedWidth;
                    String widthDelimeterString =   np .widthDelimeterString;

                    if( isFixedWidth ) {
                        remainingPart =   remainingPart.substring( Integer.parseInt( widthDelimeterString ) );
                    }
                    else {
                        int indexOfDelimeter   = remainingPart.indexOf( widthDelimeterString );

                        if( widthDelimeterString.isEmpty() ) {
                            indexOfDelimeter = ( np.coordinatePartString ).length();
                        }
                        remainingPart          = remainingPart.substring( indexOfDelimeter + widthDelimeterString.length());
                    }
                    continue;
                }

                boolean isFixedWidth        =   np .isFixedWidth;
                String widthDelimeterString =   np .widthDelimeterString;

                int coordinateValue;
                try {
                    if( isFixedWidth ) {

                        int partLength  = Integer.parseInt       ( np .widthDelimeterString );
                        coordinateValue = Integer.parseInt       ( remainingPart.substring( 0 , partLength ) );
                        remainingPart   = remainingPart.substring( partLength );
                    }
                    else{

                        int indexOfDelimeter   = remainingPart .indexOf( widthDelimeterString );

                        coordinateValue  = Integer.parseInt( remainingPart.substring( 0, indexOfDelimeter ) );
                        remainingPart    = remainingPart .substring( indexOfDelimeter + widthDelimeterString.length() );

                        
                    }
                }
                catch( Exception e ) {
                    coordinates     .clear();
                    imagesCoordinates.clear();
                    return "Could not convert into numeric. Failed on " + currentFileName;
                }

                CtCoordinates      co     = new CtCoordinates();
                
                CtCoordinatesTypes cTypes = ( CtCoordinatesTypes )
                                              CtSession.getObject( "from CtCoordinatesTypes where name='"+ np.coordinateName+"'" );

                System.out.println( "Info "+co.getName()+co.getValue() + co.getCtImagesCoordinateses().size() );

                co.setValue( coordinateValue );
                co.setCtCoordinatesTypes( cTypes );

                CtImagesCoordinates ic = new CtImagesCoordinates();
                ic .setCtCoordinates( co );
                ic .setCtImages( im );

                co.getCtImagesCoordinateses().add( ic );
                im.getCtImagesCoordinateses().add( ic );

                coordinates      .add( co );
                imagesCoordinates.add( ic );
            }
        }

        return CtDatabaseWorker._successMessage;
        
    }
}
