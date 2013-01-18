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
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtFrameRangeView;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowell;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsController;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Detection of cells in an image sequence (or part of) using fixed thresholding
 * and then postprocessing of the foreground. Since the cells move, they are
 * detected as foreground.
 *
 * Pipeline:
 *   PER-IMAGE:
 * 4. Normalization of each image.
 *  4.1 Postprocessing - Gaussian blur, then range stretch.
 * 5. Thresholding of difference images to give binary segmentation.
 * 6. Distance transform on foreground contours.
 * 7. Watershed transform to segment touching cells better (they clump)
 * 8. Contour-walking to result in detection.
 *
 * Parameters & controls:
 * 
 *
 * @author davidjr
 */
public class CtThresholdDetectionTool extends CtTool implements ActionListener {

    public CtThresholdDetectionTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {
        super( tm, "threshold-detection-tool" );
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override protected String iconFile() {
        return new String( "detection_auto_fluoro.png" );
    }

    @Override protected String toolTip() {
        return new String( "Create detections using fixed threshold." );
    }

    public static final String ALL_MICROWELLS = " All ";

    public static final String COMMAND_DISPLAY_NORMALIZED = "display-normalized";
    public static final String COMMAND_DISPLAY_SMOOTHED = "display-smoothed";
    public static final String COMMAND_DISPLAY_THRESHOLD = "display-threshold";
    public static final String COMMAND_DISPLAY_LABELLED = "display-labelled";
    public static final String COMMAND_CREATE_RANGE = "create-range";

    JPanel _segmentationControls;

    JSpinner _segmentationNormalizer;
    JSpinner _segmentationSmoothing;
    JSpinner _segmentationThreshold;
    JSpinner _segmentationAreaPixels;

    JTextField _microwell;
    CtFrameRangeView _frv1;
    CtFrameRangeView _frv2;

    @Override public JComponent panel() {
        _segmentationControls = new JPanel();
        _segmentationControls.setLayout( new BoxLayout( _segmentationControls, BoxLayout.PAGE_AXIS ) );
        _segmentationControls.setBackground( CtConstants.NictaYellow );

        // TAB #2: Segmentation
        ////////////////////////////////////////////////////////////////////////

        JPanel row6 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel row7 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel row8 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel row9 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel row10 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel row11 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );

        row6.setOpaque( false );
        row7.setOpaque( false );
        row8.setOpaque( false );
        row9.setOpaque( false );
        row10.setOpaque( false );
        row11.setOpaque( false );

        _segmentationNormalizer = new JSpinner( new SpinnerNumberModel( 3, 0, 6, 1 ) ); // value, min, max, step
        _segmentationSmoothing = new JSpinner( new SpinnerNumberModel( 3, 0, 9, 1 ) ); // value, min, max, step
        _segmentationThreshold = new JSpinner( new SpinnerNumberModel( 128, 0, 255, 1 ) ); // value, min, max, step
        _segmentationAreaPixels = new JSpinner( new SpinnerNumberModel( 8, 0, 255, 1 ) ); // value, min, max, step

        // c) display diff image
        row6.add( new JLabel( "Threshold Std.Dev." ) );
        row6.add( _segmentationNormalizer );
        JButton b3 = new JButton( "Show" );
        b3.setToolTipText( "Show the normalized image" );
        b3.setActionCommand( COMMAND_DISPLAY_NORMALIZED );
        b3.addActionListener( this );
        row6.add( b3 );

        // d) display gaussian image
        row7.add( new JLabel( "Blur (rad. pixels)" ) );
        row7.add( _segmentationSmoothing );
        JButton b4 = new JButton( "Show" );
        b4.setToolTipText( "Show the smoothed foreground image" );
        b4.setActionCommand( COMMAND_DISPLAY_SMOOTHED );
        b4.addActionListener( this );
        row7.add( b4 );

        // e) display threshold image
        row8.add( new JLabel( "Min. Threshold" ) );
        row8.add( _segmentationThreshold );
        JButton b5 = new JButton( "Show" );
        b5.setToolTipText( "Show the thresholded foreground image" );
        b5.setActionCommand( COMMAND_DISPLAY_THRESHOLD );
        b5.addActionListener( this );
        row8.add( b5 );

        // f) display distance tx image
        // g) display labelled image (from watershed)
        row9.add( new JLabel( "Min. area (pixels)" ) );
        row9.add( _segmentationAreaPixels );
        JButton b6 = new JButton( "Show" );
        b6.setToolTipText( "Show the labelled foreground components image" );
        b6.setActionCommand( COMMAND_DISPLAY_LABELLED );
        b6.addActionListener( this );
        row9.add( b6 );

        _microwell = new JTextField();
        _microwell.setText( ALL_MICROWELLS );
        JLabel blank = new JLabel();
        blank.setOpaque( false );
        blank.setPreferredSize( b6.getPreferredSize() );
        row10.add( new JLabel( "Microwell[s]" ) );
        row10.add( _microwell );
        row10.add( blank ); // padding to align the other controls

        // h) create range / all
        _frv2 = new CtFrameRangeView(); // tied to global time coord.
        row11.add( _frv2 );
        JButton b7 = new JButton( "Create" );
        b7.setToolTipText( "Create detections in specified images (slow)" );
        b7.setActionCommand( COMMAND_CREATE_RANGE );
        b7.addActionListener( this );
        row11.add( b7 );

        _segmentationControls.add( row6 );
        _segmentationControls.add( row7 );
        _segmentationControls.add( row8 );
        _segmentationControls.add( row9 );
        _segmentationControls.add( row10 );
        _segmentationControls.add( row11 );
        
        // END TABS
        ////////////////////////////////////////////////////////////////////////
        JTabbedPane tp = new JTabbedPane();

        tp.addTab( "Segmentation", _segmentationControls );

        return tp;
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        String s = ae.getActionCommand();

        if( s.equals( COMMAND_DISPLAY_NORMALIZED ) ) {
//            CtImageResult fg = getForegroundImageResult();
//
//            if( fg == null ) {
//                return;
//            }
//
//            CtImageResultDialog.show( "Foreground (difference image)", fg );
//            int index1 = _frv2.getIndex1();
//            int index2 = _frv2.getIndex2();
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtImageSequenceModel ism = cc.getImageSequenceModel();
            int index1 = ism.getIndex();
            int index2 = index1; // show current image
            CtThresholdDetectionProcess fdp = createThresholdDetectionProcess( index1, index2, 0, false );
            fdp.doStep( 0 );//ism.getIndex()-ism.getMinIndex() );
//            fdp.enqueue();
        }
        else if( s.equals( COMMAND_DISPLAY_SMOOTHED ) ) {

//            CtImageResult fg = getForegroundImageResult();
//
//            if( fg == null ) {
//                return;
//            }
//
//            CtFrame.showWaitCursor();
//            CtImageOperation io1 = getBlurOp( fg );
//            CtImageResult gb = io1.getDst();
//            gb.refresh();
//            CtFrame.showDefaultCursor();
//            CtImageResultDialog.show( "Smoothed foreground image", gb );
//            int index1 = _frv2.getIndex1();
//            int index2 = _frv2.getIndex2();
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtImageSequenceModel ism = cc.getImageSequenceModel();
            int index1 = ism.getIndex();
            int index2 = index1; // show current image

            CtThresholdDetectionProcess fdp = createThresholdDetectionProcess( index1, index2, 1, false );
//            CtCoordinatesController cc = CtCoordinatesController.get();
//            CtImageSequenceModel ism = cc.getImageSequenceModel();
//            fdp.doStep( ism.getIndex() );
            fdp.doStep( 0 );//ism.getIndex()-ism.getMinIndex() );
        }
        else if( s.equals( COMMAND_DISPLAY_THRESHOLD ) ) {

//            CtImageResult fg = getForegroundImageResult();
//
//            if( fg == null ) {
//                return;
//            }
//
//            CtFrame.showWaitCursor();
//            CtImageOperation io1 = getBlurOp( fg );
//            CtImageResult gb = io1.getDst();
//            CtImageOperation io2 = getClipOp( gb );
//            CtImageResult th = io2.getDst();
//            th.refresh();
//            CtFrame.showDefaultCursor();
//            CtImageResultDialog.show( "Thresholded foreground image", th );
//            int index1 = _frv2.getIndex1();
//            int index2 = _frv2.getIndex2();
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtImageSequenceModel ism = cc.getImageSequenceModel();
            int index1 = ism.getIndex();
            int index2 = index1; // show current image

            CtThresholdDetectionProcess fdp = createThresholdDetectionProcess( index1, index2, 2, false );
//            CtCoordinatesController cc = CtCoordinatesController.get();
//            CtImageSequenceModel ism = cc.getImageSequenceModel();
//            fdp.doStep( ism.getIndex() );
            fdp.doStep( 0 );//ism.getIndex()-ism.getMinIndex() );
//            fdp.enqueue();
        }
        else if( s.equals( COMMAND_DISPLAY_LABELLED ) ) {

//            CtImageResult fg = getForegroundImageResult();
//
//            if( fg == null ) {
//                return;
//            }
//
//            CtFrame.showWaitCursor();
//            CtImageOperation io1 = getBlurOp( fg );
//            CtImageResult gb = io1.getDst();
//            CtImageOperation io2 = getClipOp( gb );
//            CtImageResult th = io2.getDst();
//            CtImageOperation io3 = getWatershedOp( th );
//            CtImageResult la = io3.getDst();
//            la.refresh();
//            CtFrame.showDefaultCursor();
//            CtImageResultDialog.show( "Labelled foreground components", la );
//            int index1 = _frv2.getIndex1();
//            int index2 = _frv2.getIndex2();
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtImageSequenceModel ism = cc.getImageSequenceModel();
            int index1 = ism.getIndex();
            int index2 = index1; // show current image

            CtThresholdDetectionProcess fdp = createThresholdDetectionProcess( index1, index2, 3, false );
//            CtCoordinatesController cc = CtCoordinatesController.get();
//            CtImageSequenceModel ism = cc.getImageSequenceModel();
            fdp.doStep( 0 );//ism.getIndex() );
        }
        else if( s.equals( COMMAND_CREATE_RANGE ) ) {
            int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to generate detections in all specified images? This may take some time.", "Create Detections", JOptionPane.YES_NO_OPTION );
            if( n != JOptionPane.YES_OPTION ) {
                return;
            }

            int index1 = _frv2.getIndex1();
            int index2 = _frv2.getIndex2();
            // get master coord image sequence.. do a specified process on each image in the sequence.
            CtThresholdDetectionProcess fdp = createThresholdDetectionProcess( index1, index2, 3, true );

            String wells = _microwell.getText().trim();

            if( !wells.equalsIgnoreCase( ALL_MICROWELLS.trim() ) ) {
                CtMicrowellsController mc = (CtMicrowellsController)CtObjectDirectory.get( CtMicrowellsController.name() );
                CtMicrowellsModel mm = mc.getMicrowellsModel();
                CtMicrowell m = mm.find( wells );

                if( m != null ) {
                    fdp.setRegion( m );
                }
            }

            fdp.enqueue();
        }
    }

    protected CtThresholdDetectionProcess createThresholdDetectionProcess( int index1, int index2, int stage, boolean createDetections ) {

        double normalizerStdDev = (double)((SpinnerNumberModel)_segmentationSmoothing.getModel()).getNumber().intValue();
        int radius   = ((SpinnerNumberModel)_segmentationSmoothing.getModel()).getNumber().intValue();
        int minValue = ((SpinnerNumberModel)_segmentationThreshold.getModel()).getNumber().intValue();
        int minArea  = ((SpinnerNumberModel)_segmentationAreaPixels.getModel()).getNumber().intValue();

        double maxSpawnRate = 2.0; // doesn't need user attention, merely a sanity test

        try {
            CtThresholdDetectionProcess fdp = new CtThresholdDetectionProcess(
                index1,
                index2,
                normalizerStdDev,
                radius,
                minValue,
                minArea,
                null,//CtRegion roi, TODO
                maxSpawnRate,
                createDetections );

            fdp.setDebugStage( stage );
            return fdp;
        }
        catch( IOException ioe ) {
            JOptionPane.showMessageDialog(
                CtPageFrame.find(),
                "No background model available",
                "Error", JOptionPane.ERROR_MESSAGE );
            return null;
        }
    }

}
