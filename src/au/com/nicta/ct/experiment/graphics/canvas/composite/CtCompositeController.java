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
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

/**
 *
 * @author Rajib
 */
public class CtCompositeController {

    CtCompositeModel _ctm;

    public static String _red       = "Red";
    public static String _green     = "Green";
    public static String _blue      = "Blue";
    public static String _original  = "Original";

    public static int _alphaMaximum = 255;

    public CtCompositeController( String sequenceAxis ) {
        _ctm = new CtCompositeModel( sequenceAxis );
    }

//    public void addNewSequence( String axisName, int axisValue,
//                                String colorCode,
//                                CtImageSequenceModel originalSequence ){
//
//        _ctm.addNewSequence( axisName, axisValue, colorCode, originalSequence );
//    }

    public void deleteSequence( String axisName, int axisValue, String colorCode ) {
        _ctm.deleteSequence( axisName, axisValue, colorCode );
    }

    public void clearSequences(){
        _ctm.clear();
    }

    public ImageProcessor collateImages( CtViewpointController vc, CtImages i ) {//ImagePlus imo ) {//, int originalIndex ){

        CtCoordinatesController cc = CtCoordinatesController.get();

        try {

            ImagePlus imo = cc.getCoordinatesModel().getImagePlus( i );

            if( _ctm.getSequenceNo() == 0 ) {
                return imo.getProcessor();
            }

            ImageProcessor original = imo.getProcessor().duplicate().convertToByte( true );

            FloatProcessor fp = null, fpOriginal = null;

            ImageProcessor ipRed = imo.getProcessor().duplicate().convertToByte( true );
            if( _ctm.isImageChannelValid( CtCompositeController._red ) ){
                ipRed = _ctm.getImageProcessor( cc, CtCompositeController._red ).convertToByte( true );
            }
            
            ImageProcessor ipGreen = imo.getProcessor().duplicate().convertToByte( true );
            if( _ctm.isImageChannelValid( CtCompositeController._green ) ){
                ipGreen = _ctm.getImageProcessor( cc, CtCompositeController._green ).convertToByte( true );
            }

            ImageProcessor ipBlue = imo.getProcessor().duplicate().convertToByte( true );
            if( _ctm.isImageChannelValid( CtCompositeController._blue ) ){
                ipBlue = _ctm.getImageProcessor( cc, CtCompositeController._blue ).convertToByte( true );
            }

            ImageProcessor ip = original.duplicate().convertToRGB();

            for( int channel = 0; channel < ip.getNChannels(); channel++ ) { //grayscale: once. RBG: once per color, i.e., 3 times

               float weight = (float) 0.0;
               
               fpOriginal = ip.toFloat( channel , null );

               if( channel == 0 ){
                   if( !_ctm.isImageChannelValid( CtCompositeController._red ) ){
                       continue;
                   }
                   fp = ipRed.duplicate().toFloat(0, null );
               }
               if( channel == 1 ){
                   if( !_ctm.isImageChannelValid( CtCompositeController._green ) ){
                       continue;
                   }
                   fp = ipGreen.duplicate().toFloat(0, null );
               }
               if( channel == 2 ){
                   if( !_ctm.isImageChannelValid( CtCompositeController._blue ) ){
                       continue;
                   }
                   fp = ipBlue.duplicate().toFloat(0, null );
               }

               float[] fPixels = (float[]) fp.getPixels();
               float[] fpOriginalPixels = (float[]) fpOriginal.getPixels();

               float reciprocal       = (float) (1.0 / fp.getMax());
               float weightComplement = ( 1 - weight );

               double max = fp.getMax();
               
               for (int p = 0; p < fPixels.length; p++) {

                    weight = (float) ( fPixels[p] * reciprocal );
                    if( weight > 1){
                        weight = 1;
                    }
                    if( weight < 0){
                        weight = 0;
                    }
                    fPixels[ p ] = (float) ( weightComplement * fpOriginalPixels[p] + weight * max );
               }
               ip.setPixels( channel, fp ) ;
            }
            return ip;
            
        }
        catch( Exception e ) {
            return null;
        }
    }

//    public String getCompositeAxisName(){
//        return _ctm.getAxisName();
//    }

//    public boolean isSequenceAxisSelected(){
//        return _ctm.isSequenceAxisSelected();
//    }

//    public void changeAxisName( String axisName ){
//        _ctm.axisChanged( axisName );
//    }
}
