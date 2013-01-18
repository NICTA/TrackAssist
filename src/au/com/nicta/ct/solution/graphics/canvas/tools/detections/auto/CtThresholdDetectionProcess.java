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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto;

import au.com.nicta.ct.graphics.canvas.images.CtImageResultDialog;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg.CtBackgroundModel;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg.CtImageNormaliser;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg.CtImageRegistration;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg.CtStdDevNormaliser;
import au.com.nicta.ij.operations.CtClipOperation;
import au.com.nicta.ij.operations.CtConvertToByteOperation;
import au.com.nicta.ij.operations.CtGaussianBlurOperation;
import au.com.nicta.ij.operations.CtImageOperation;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ij.operations.CtInvertOperation;
import au.com.nicta.ij.operations.segmentation.CtContours2Detections;
import au.com.nicta.ij.operations.segmentation.CtWatershedOperation;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtRegion;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;
import ij.ImagePlus;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.io.IOException;
import java.util.Collection;
import javax.swing.JOptionPane;

/**
 *
 * @author davidjr
 */
public class CtThresholdDetectionProcess extends CtEventDispatchThreadProgress {

    CtImageNormaliser _in;// = new CtStdDevNormaliser();
    CtImageResult _debugResult;
    int _debugStages = 0;
    boolean _createDetections = false;
    int _previousDetectionCount = 0;
    double _maxSpawnRate = Double.MAX_VALUE;
    double _normalizerStdDev = 3.0;
    CtRegion _roi = null;
//    boolean _showDebugImage = false;

    int _index1 = 0;
    int _index2 = 0;
    int _radius = 0;
    int _minValue = 0;
    int _minArea = 0;

    public CtThresholdDetectionProcess(
        int index1,
        int index2,
        double normalizerStdDev,
        int radius,
        int minValue,
        int minArea,
        CtRegion roi,
        double maxSpawnRate,
        boolean createDetections ) throws java.io.IOException {
        super( "Creating detections... ", "threshold-detection-process" );

        _index1 = index1;
        _index2 = index2;

        _normalizerStdDev = normalizerStdDev;
        _radius = radius;
        _minValue = minValue;
        _minArea = minArea;

        _createDetections = createDetections;
        _maxSpawnRate = maxSpawnRate;
        _roi = roi;

        _in = getImageNormalizer();
    }

    @Override public int getLength() {
        return ( _index2 - _index1 +1 );
    }

    @Override public void doStep( int step ) {

        if( _in == null ) {
            _in = getImageNormalizer();
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceModel ism = cc.getImageSequenceModel();

//        int minIndex = ism.getMinIndex();
        int index = step + _index1;//minIndex;//_index1;

        try {
            _debugResult = null;

            CtImages i = ism.get( index );

            CtPageFrame.showWaitCursor();

            CtImageResult ir1 = null;
            CtImageResult ir2 = null;

            int stage = 0;
            int stages = getDebugStages();// -1;
            int maxStage = Math.min( stages, _debugStages+1 );

            while( stage < maxStage ) {
                ir2 = doStage( i, ir1, stage );

                if( ir2 == null ) {
                    break;
                }

                ir1 = ir2; // swap

                _debugResult = ir2; // remember latest result

                ++stage;
            }

            CtPageFrame.showDefaultCursor();

            if( _debugResult != null ) {
                _debugResult.refresh();

                // final stage must be finished separately, as it involves creating detections.
                if( _createDetections ) { // ie I wanted to do one more
                    CtContours2Detections c2d = new CtContours2Detections( _previousDetectionCount, _maxSpawnRate, _roi );
                    Collection< CtZoomPolygon > czp = c2d.contours2Detections( (ShortProcessor)_debugResult.getIP(), i );
                    if( czp != null ) {
                        _previousDetectionCount = czp.size();
                    }
                    else {
//                        _previousDetectionCount = ? leave unchanged
                    }
                }
                else {
                    CtImageResultDialog.show( "Labelled foreground components", _debugResult );
                }
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if( index == _index2 ) {

//            JOptionPane.showMessageDialog(
//                CtFrame.find(),
//                "Background modelling completed.",
//                "Background Complete", JOptionPane.INFORMATION_MESSAGE );
        }
    }

    public void setMaxSpawnRate( double maxSpawnRate ) {
        _maxSpawnRate = maxSpawnRate;
    }

    public void setRegion( CtRegion roi ) {
        _roi = roi;
    }
    
    public boolean getCreateDetections() {
        return _createDetections;
    }

    public void setCreateDetections( boolean createDetections ) {
        _createDetections = createDetections;
    }
    
    public void setDebugStage( int debugStage ) {
        _debugStages = debugStage;
    }

    public static int getDebugStages() {
        return 4;
    }

    protected CtImageResult doStage( CtImages i, CtImageResult ir1, int stage ) {

        CtImageOperation io = null;

        switch( stage ) {
            case 0: CtImageResult ir2 = getNormalizedImageResult( i );
                    return ir2;
            case 1: io = getBlurOp     ( ir1 ); break;
            case 2: io = getClipOp     ( ir1 ); break;
            case 3: io = getWatershedOp( ir1 ); break;
        }

        return io.getDst();
    }
    
    public CtImageNormaliser getImageNormalizer() {
        CtStdDevNormaliser n = new CtStdDevNormaliser();
        n.min = 0.0;
        n.max = 255.0;
        n.numStdDev = _normalizerStdDev;
        return n;
    }

    protected CtImageResult getNormalizedImageResult( CtImages i ) {

        CtCoordinatesController cc = CtCoordinatesController.get();

        try {
            ImagePlus ip = cc.getCoordinatesModel().getImagePlus( i );
            ShortProcessor sp = (ShortProcessor) ip.getProcessor().duplicate();// clone it to avoid damaging original in memory
            _in.normalise( sp );
//            sp = CtImageRegistration.ScaleUp( sp, _ir.getParam().scaleUp );
//
//            ShortProcessor bg = (ShortProcessor)_bm._sp;
//            ShortProcessor bg2 = (ShortProcessor)bg.duplicate();
//            _ir.setRefImage( bg2 );
//
//            CtImageRegistration.Result result = _ir.register( sp );
//            bg.setRoi( result.getRoiA() );
//            sp.setRoi( result.getRoiB() ); // TODO load registration params if not available
//
//            ShortProcessor diff = CtImageRegistration.AbsDiff( bg, sp );
//
//            diff.setMinAndMax( 0, 255 );
//            CtImageResult r = new CtImageResult( diff );
            sp.setMinAndMax( 0, 255 );
            CtImageResult r = new CtImageResult( sp );

            return r;
//               ir.setIP(  );
//                ir.refresh();
//            CtImageResultDialog.show( "Foreground (difference image)", r );
        }
        catch( IOException ioe ) {
            JOptionPane.showMessageDialog(
                CtPageFrame.find(),
                "No background model available",
                "Error", JOptionPane.ERROR_MESSAGE );
            return null;
        }
    }
//                _ir.setRefImage( sp );
//
//                _bm.addImage( _ir.getRefImage() ); // scaled up
//            }
//            else { // register against reference image
//                CtImageRegistration.Result result = _ir.register( sp );

    CtImageOperation getBlurOp( CtImageResult input ) {
//        final int radius = ((SpinnerNumberModel)_segmentationSmoothing.getModel()).getNumber().intValue();
        CtConvertToByteOperation convertOp = new CtConvertToByteOperation( input, true );
        CtGaussianBlurOperation blurOp = new CtGaussianBlurOperation( convertOp.getDst() ) {
            @Override protected void blur( ImageProcessor dst, GaussianBlur gb ) {
                gb.blur( dst, _radius );// 2.0);
            }
        };
        return blurOp;
    }

    CtImageOperation getClipOp( CtImageResult input ) {
//        final int minValue = ((SpinnerNumberModel)_segmentationThreshold.getModel()).getNumber().intValue();
        CtClipOperation clipOp = new CtClipOperation( input );
        clipOp.setThreshold( _minValue );
        clipOp.setClipped( 0 ); // values that are clipped are clipped to this value
        clipOp.setClipLessThan( true );
        clipOp.setScaleResult( true );
        return clipOp;
    }

    CtWatershedOperation getWatershedOp( CtImageResult input ) {//, CtImageResult filter ) {//, int maskValue ) {
        int min = 0;
        int max = 255;
        int maskValue = 0;
//        int filterThreshold = 1;
//        final int minArea = ((SpinnerNumberModel)this._segmentationAreaPixels.getModel()).getNumber().intValue();

        CtInvertOperation invertOp = new CtInvertOperation( input, min, max );
        CtWatershedOperation wOp = new CtWatershedOperation( invertOp.getDst(), _minArea );//maskOp.getDst() );//blurOp.getDst() );
//        wOp.setFilter( filter, filterThreshold );
        wOp.setMask( input, maskValue );
        return wOp;
    }

}
