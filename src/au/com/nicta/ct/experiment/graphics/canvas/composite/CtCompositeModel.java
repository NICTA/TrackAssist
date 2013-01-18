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

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.io.IOException;

/**
 *
 * @author Rajib
 */
public class CtCompositeModel {

    CtAxisValueColour _redSequence;
    CtAxisValueColour _greenSequence;
    CtAxisValueColour _blueSequence;
    CtAxisValueColour _originalSequence;
    
    String _compositeAxisName;
    String _sequenceAxisName;

    public CtCompositeModel( String sequenceAxis ){

        _compositeAxisName         = "";
        _redSequence      = new CtAxisValueColour( CtCompositeController._red      );
        _greenSequence    = new CtAxisValueColour( CtCompositeController._green    );
        _blueSequence     = new CtAxisValueColour( CtCompositeController._blue     );
        _originalSequence = new CtAxisValueColour( CtCompositeController._original );

        _sequenceAxisName = sequenceAxis;
    }

    public void clear() {
        _redSequence.reset();
        _greenSequence.reset();
        _blueSequence.reset();
    }

    public void setCompositeAxisName( String compositeAxisName ) {
        clear();
        this._compositeAxisName = compositeAxisName;
    }

    public void addNewSequence( //String axisName,
                                int axisValue,
                                String colorCode ) {//,
//                                CtImageSequenceModel ism ) {

        System.out.println( "Adding New Seq: " + _compositeAxisName + ":"+ axisValue+":"+colorCode );

//        if( !_compositeAxisName.equals( axisName ) ) {
//            axisChanged( axisName );
//        }
/* NONE of this needed, simply get the image on demand..
        CtImageSequenceModel ismCopy = new CtImageSequenceModel();
        if( axisName.equals( _sequenceAxisName ) ){

            System.out.println( "Time == SequenceAxis" );

            int totalImages = ism.size();
            boolean imageFound = false;
            for( int imageIndex = 0; imageIndex < totalImages; imageIndex++ ){
                try{
                   CtImages ci = ism.get( imageIndex );

                   Set< CtImagesCoordinates > ics = ci.getCtImagesCoordinateses();

                   Iterator i = ics.iterator();

                   while( i.hasNext() ) {
                        CtImagesCoordinates ic = (CtImagesCoordinates)i.next();
                        CtCoordinates c_i = ic.getCtCoordinates();
                        CtCoordinatesTypes ct_i = c_i.getCtCoordinatesTypes();

                        String s = ct_i.getName();
                        if( s.equals( _sequenceAxisName ) && c_i.getValue() == axisValue ){
//                            ismCopy.add( ci );
                            imageFound = true;
                            break;
                        }
                   }
                   if( imageFound == true ){
                       break;
                   }
                }
                catch( Exception e){
                }
            }
        }
        else{
            System.out.println( "Time <> SequenceAxis" );
//            ismCopy = ism;
        }
//        ism = ismCopy;*/

        //red
        if( colorCode.equals( CtCompositeController._red ) ){
//            _redSequence.setImageSequence( axisName );//ism );
            _redSequence.setAxisValue    ( axisValue );
        }

        //green
        if( colorCode.equals( CtCompositeController._green ) ){
//            _greenSequence.setImageSequence( axisName );//ism );
            _greenSequence.setAxisValue    ( axisValue );
        }

        //blue
        if( colorCode.equals( CtCompositeController._blue ) ){
//            _blueSequence.setImageSequence( axisName );//ism );
            _blueSequence.setAxisValue    ( axisValue );
        }
    }

    public void deleteSequence( String axisName, int axisValue, String colorCode ) {

        if( colorCode.equals( CtCompositeController._red ) ){
            _redSequence.reset( );
        }
        if( colorCode.equals( CtCompositeController._green ) ){
            _greenSequence.reset( );
        }
        if( colorCode.equals( CtCompositeController._blue ) ){
            _blueSequence.reset( );
        }
    }

//    public void axisChanged( String axisName ) {
//        clear();
//        _compositeAxisName = axisName;
//    }

// DAVE: Makes no sense.
//    public void replaceSequence( String axisName, int axisValue, String colorCode, CtImageSequenceModel ism ) {
//        if( colorCode.equals( CtCollateToolsController._red ) ){
//            _redSequence.setImageSequence( axisName );//ism );
//        }
//        if( colorCode.equals( CtCollateToolsController._green ) ){
//            _greenSequence.setImageSequence( axisName );//ism );
//        }
//        if( colorCode.equals( CtCollateToolsController._blue ) ){
//            _blueSequence.setImageSequence( axisName );//ism );
//        }
//    }

    public ImageProcessor getImageProcessor( CtCoordinatesController cc, String colorCode ) {//, int index ){

        CtAxisValueColour c = null;

        if( colorCode.equals( CtCompositeController._red ) ){
            c = _redSequence;
        }
        else if(colorCode.equals( CtCompositeController._green) ) {
            c = _greenSequence;
        }
        else if(colorCode.equals( CtCompositeController._blue) ) {
            c = _blueSequence;
        }

        if( c._axisValue == -1 ){
            return null;
        }

        try {
            CtCoordinatesModel cm = cc.getCoordinatesModel();
//            ImagePlus ip = cm.getSingleImage( _compositeAxisName, c._axisValue +1 );//getRedProcessor( index );
            CtImages i = cm.getImage( _compositeAxisName, c._axisValue +1 );
            ImagePlus ip = cm.getImagePlus( i );//_compositeAxisName, c._axisValue +1 );//getRedProcessor( index );

            return ip.getProcessor();
        }
        catch( IOException ioe ) {
            return null;
        }
    }

//    private ImageProcessor getRedProcessor( int index ){
//        if( _redSequence._axisValue == -1 ){
//            return null;
//        }
//        try{
//            if( _compositeAxisName.equals( _sequenceAxisName ) ){
//
//                ImageProcessor ip =  _redSequence._ism.getCachedImage( 0 ).getProcessor();
//                return ip;
//            }
//            ImageProcessor ip =  _redSequence._ism.getCachedImage( index ).getProcessor();
//            return ip;
//        }
//        catch( Exception e ){
//            return null;
//        }
//    }
//
//    private ImageProcessor getGreenProcessor( int index ){
//
//        if( _greenSequence._axisValue == -1 ){
//            return null;
//        }
//        try{
//            if( _compositeAxisName.equals( _sequenceAxisName ) ){
//
//                ImageProcessor ip =  _greenSequence._ism.getCachedImage( 0 ).getProcessor();
//                return ip;
//            }
//
//            ImageProcessor ip = _greenSequence._ism.getCachedImage( index ).getProcessor();
//            return ip;
//        }
//        catch( Exception e ){
//            return null;
//        }
//    }
//
//    private ImageProcessor getBlueProcessor( int index ){
//
//        if( _blueSequence._axisValue == -1 ){
//            return null;
//        }
//        try{
//           if( _compositeAxisName.equals( _sequenceAxisName ) ){
//                ImageProcessor ip =  _blueSequence._ism.getCachedImage( 0 ).getProcessor();
//                return ip;
//            }
//            ImageProcessor ip = _blueSequence._ism.getCachedImage( index ).getProcessor();
//            return ip;
//        }
//        catch( Exception e ){
//            return null;
//        }
//    }

    public boolean isImageChannelValid( String colorCode ){
        
        if( colorCode.equals( CtCompositeController._red ) ){
            return isRedValid();
        }
        else if(colorCode.equals(CtCompositeController._green ) )
        {
            return isGreenValid();
        }
        else if(colorCode.equals(CtCompositeController._blue ) )
        {
            return isBlueValid();
        }
        else{
            return false;
        }
    }

    private boolean isGreenValid(){
        if( _greenSequence._axisValue == -1 ){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean isRedValid(){
        if( _redSequence._axisValue == -1 ){
            return false;
        }
        else{
            return true;
        }
    }
    private boolean isBlueValid(){
        if( _blueSequence._axisValue == -1 ){
            return false;
        }
        else{
            return true;
        }
    }

    public int getSequenceNo(){

        int totalSequence = 0;

        if( _redSequence._axisValue >= 0 ){
            totalSequence++;
        }
        if( _greenSequence._axisValue >= 0 ){
            totalSequence++;
        }
        if( _blueSequence._axisValue >= 0 ){
            totalSequence++;
        }
        return totalSequence;
    }
   
    public String getAxisName(){
        return _compositeAxisName;
    }

    public boolean isSequenceAxisSelected(){
        if( _compositeAxisName.equals( _sequenceAxisName ) ){
            return true;
        }
        else{
            return false;
        }
    }

}
