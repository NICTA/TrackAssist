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

package au.com.nicta.ct.experiment.graphics.canvas.contrast;

import au.com.nicta.ct.ui.style.CtColorPalette;
import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasPainter;
import au.com.nicta.ct.ui.swing.graphics.CtAffine;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointListener;
import au.com.nicta.ij.operations.CtHistogramOperation;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ij.operations.CtLutOperation;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultTool;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 *
 * @author Alan
 */
public class CtGrayscaleMapCanvas extends CtCanvas implements CtChangeListener { //CtCoordinatesListener { //CtImageChangeListener {

    public CtXYMultiSlider slider = null;
    public CtHistogramOperation hist = null;
    public CtLutOperation lutOp = null;

    CtXYMultiSlider localSlider = null;
    CtHistogramOperation localHist = null;
    CtLutOperation localLutOp = null;

    CtCanvasLayer curveLayer;
    CtCanvasLayer sliderLayer;

    double[] portion;

    CtViewpointController _vc;
    CtImageResultTool _irt;
    CtImageResult _src;
    
    public CtGrayscaleMapCurve curve;
    int[] lut;
    Timer timer;
    boolean adjusting = false;

    boolean mirror = false;
    CtAffine.QuadrantAngle rotation = CtAffine.QuadrantAngle.DEG_90;

    Color background = CtColorPalette.NICTA_LIGHT_YELLOW;
    
    CtChangeListener sliderChanged = new CtChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if( slider.getValueIsAdjusting() ) { // entering adjusting mode
                if( !adjusting ) {
                    adjusting = true;
                    timer.start();
                }
            }
            else {
                if( adjusting ) {
                    adjusting = false;
                    timer.stop();
                    applyLut(); // final redraw?
                }
            }
        }
    };

    public CtGrayscaleMapCanvas( CtViewpointController vc, CtImageResultTool irt, CtImageResult src ) {
        // create layers
//        _src = src;

        _vc = vc;
        _irt = irt;
        
        curveLayer  = new CtCanvasLayer();
        sliderLayer = new CtCanvasLayer();

        // setup timer
        timer = new Timer(100, null);
        timer.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyLut();
            }
        });

        setBackground( background );
        setBorder( new LineBorder( Color.LIGHT_GRAY ) );

        setSrc( src );

        _vc.addListener( this );
//
//        CtCoordinatesController.addCoordinatesListener( this );
//
//        onModelChanged();
    }

    @Override public void propertyChange( PropertyChangeEvent e ) {
        String s = e.getPropertyName();

        if( s.equals( CtViewpointListener.EVT_IMAGE_CHANGED ) ) {
            onIndexChanged();
        }
    }

    CtXYMultiSlider createSlider( int histMin, int histMax, int numThumbs ) {
        CtAbstractPair< int[], int[] > ap = hist2xy( histMin, histMax, numThumbs );
        return createSlider( ap._first, ap._second );
    }

    CtAbstractPair< int[], int[] > hist2xy( int histMin, int histMax, int numThumbs ) {
        assert numThumbs >= 2 : "Num thumbs must be at least 2";

        final int minY = 0;
        final int maxY = 255;

        int[] x = new int[numThumbs];
        int[] y = new int[numThumbs];

        for( int i = 0; i < numThumbs; ++i ) {
            x[i] = (histMax - histMin) * i / (numThumbs-1)  +  histMin;
            y[i] = (maxY - minY   ) * i / (numThumbs-1)  +  minY;
        }

        CtAbstractPair< int[], int[] > ap = new CtAbstractPair< int[], int[] >( x,y );

        return ap;
    }

    // scale the position of the thumbs to the full range of the histogram
    CtXYMultiSlider createScaledSlider( CtXYMultiSlider old, int newMin, int newMax ) {
        CtAbstractPair< int[], int[] > ap = hist2xyScaled( old, newMin, newMax );
        return createSlider( ap._first, ap._second );
    }
    
    CtAbstractPair< int[], int[] > hist2xyScaled( CtXYMultiSlider old, int newMin, int newMax ) {

        int oldMin = localSlider.getMinX();
        int oldMax = localSlider.getMaxX();

        // scale the thumbs
        int[] x = new int[ localSlider.getNumPoints() ];
        int[] y = new int[ localSlider.getNumPoints() ];

        for( int i = 0; i < x.length; ++i ) {
            x[i] =    ( localSlider.getValueX(i) - oldMin )
                    * ( newMax - newMin )
                    / ( oldMax - oldMin )
                    + newMin;
            y[i] = localSlider.getValueY(i);
        }

        CtAbstractPair< int[], int[] > ap = new CtAbstractPair< int[], int[] >( x,y );

        return ap;
    }

    CtXYMultiSlider createSlider( int[] x, int[] y ) {
        CtXYMultiSlider s = new CtXYMultiSlider(
                sliderLayer,
                x[0], x[x.length-1],
                y[0], y[y.length-1],
                true, false );

        for( int i = 0; i < x.length; ++i ) {
            s.addThumb( x[i], y[i] );
        }

        s.setFixed( 0,                  true, false ); // fix first
        s.setFixed( s.getNumPoints()-1, true, false ); // fix last

        return s;
    }

    void updateSlider( CtXYMultiSlider s, int[] x, int[] y ) {

        int minX = x[ 0 ];
        int maxX = x[x.length-1];
        int minY = y[0];
        int maxY = y[y.length-1];
        
        s.setMinMax( minX, maxX, minY, maxY );
    }

    public void onModelChanged() {
        portion = null;     
        localHist.setSrc( _src );
        int numThumbs = localSlider.getNumPoints();
        localSlider = createSlider(
                localHist.getDst().minNonZeroBin(),
                localHist.getDst().maxNonZeroBin(),
                numThumbs );
        setup( localSlider, localHist, localLutOp );
        applyLut();
        repaint();
    }

    public void onRangeChanged() {
        onModelChanged();
    }
    public void onIndexChanged() {
//        onImageChanged();
//    }
//
//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
//
////        onImageChanged( isf );
//        int numThumbs = localSlider.getNumPoints();
//        localSlider = createSlider(
//                localHist.getDst().minNonZeroBin(),
//                localHist.getDst().maxNonZeroBin(),
//                numThumbs );
//        setup( localSlider, localHist, localLutOp );
//        applyLut();
//        repaint();
//    }
//
//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {

        int numThumbs = localSlider.getNumPoints();

        int i;
        double[] sliderX;
        sliderX = new double[localSlider.model.modelX.values.size()];
        double[] sliderY;
        sliderY = new double[localSlider.model.modelY.values.size()];
        double[] percentX;
        percentX = new double[localSlider.model.modelY.values.size()];

        for (i=0; i<localSlider.model.modelX.values.size(); i++)
        {
            sliderX[i] = localSlider.model.modelX.values.get(i).v;
            percentX[i] = localHist.getDst().getPercent( sliderX[i] );
            sliderY[i] = localSlider.model.modelY.values.get(i).v;
        }

        if ( portion == null) {
            portion = Arrays.copyOf( percentX, percentX.length );
        } else {
            if ( localSlider.isValueChanged ) {
                portion = Arrays.copyOf( percentX, percentX.length );
                localSlider.setIsValueChanged( false );
            }
        }

        for (i=0; i<localSlider.model.modelX.values.size(); i++)
        {
            sliderY[i] = localSlider.model.modelY.values.get(i).v;
        }

        localSlider = createSlider(
                localHist.getDst().minNonZeroBin(),
                localHist.getDst().maxNonZeroBin(),
                numThumbs );
        setup( localSlider, localHist, localLutOp );
        
        for (i=1; i<localSlider.model.modelX.values.size()-1; i++)
        {
            localSlider.model.modelX.values.get(i).v = localHist.getDst().getPercentInd( portion[i] );
            localSlider.model.modelY.values.get(i).v = sliderY[i];
        }

        applyLut();
        repaint();

//        // update the histogram when the image changes
//        CtImageResult src = original;
//
//        localHist.setSrc( original ); // dave: added
//        localLutOp.setSrc( original ); // dave: added
//
//        int numThumbs = localSlider.getNumPoints();
//
//        int histMin = localHist.getDst().minNonZeroBin();
//        int histMax = localHist.getDst().maxNonZeroBin();
//
//        CtAbstractPair< int[], int[] > ap = hist2xy( histMin, histMax, numThumbs );
//
//        updateSlider( localSlider, ap._first, ap._second );
//
//        localHist.refresh();
//        applyLut();
//        repaint();
    }
    
    public CtImageResult getDst() {
        return localLutOp.getDst();
    }

    public void setSrc( CtImageResult src ) {
//    public void setup() {//CtImageResult src ) {

//        CtImageResult src = irp.getOriginal();
        _src = src;

        if( localHist == null ) {
            localHist = new CtHistogramOperation( _src );
        }
        else {
            localHist.setSrc( _src );
        }

        if( localLutOp == null ) {
            localLutOp = new CtLutOperation( _src );
        }
        else {
            localLutOp.setSrc( _src );
        }

        if( localSlider == null ) {
            final int numThumbs = 5;
            localSlider = createSlider(
                    localHist.getDst().minNonZeroBin(),
                    localHist.getDst().maxNonZeroBin(),
                    numThumbs );

        }
        else {
            localSlider = createScaledSlider(
                    localSlider,
                    localHist.getDst().minNonZeroBin(),
                    localHist.getDst().maxNonZeroBin() );
        }

//        System.out.println("localSlider.getValueX(0): " + (localSlider.getValueX(0)) );;
//        System.out.println("localSlider.getValueX(1): " + (localSlider.getValueX(1)) );;
//        System.out.println("localSlider.getValueX(2): " + (localSlider.getValueX(2)) );;
//        System.out.println("localSlider.getValueX(3): " + (localSlider.getValueX(3)) );;
//        System.out.println("localSlider.getValueX(4): " + (localSlider.getValueX(4)) );;

        setup( localSlider, localHist, localLutOp );
    }

    public void setup(
            CtXYMultiSlider sliderT,
            CtHistogramOperation histT,
            CtLutOperation lutOpT ) {

        this.slider = sliderT;
        this.hist = histT;
        this.lutOp = lutOpT;

        sliderLayer.clearPainters();
        sliderLayer.addPainter(slider);
        
        slider.setShowHorizontalLine(false);
        slider.setShowVerticalLine(true);
        slider.addListener( sliderChanged );

//        System.out.println("slider.getValueX(0): " + (slider.getValueX(0)) );;
//        System.out.println("slider.getValueX(1): " + (slider.getValueX(1)) );;
//        System.out.println("slider.getValueX(2): " + (slider.getValueX(2)) );;
//        System.out.println("slider.getValueX(3): " + (slider.getValueX(3)) );;
//        System.out.println("slider.getValueX(4): " + (slider.getValueX(4)) );;

        curveLayer.clearPainters();
        curve = new CtGrayscaleMapCurve(curveLayer, slider);
       

        // add to different layers
        try {
            clearLayers();
            addLayer( getHistLayer(), "hist layer" );
            addLayer( curveLayer, "curve layer" );
            addLayer( sliderLayer, "slider layer" );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        repaint();
    }

    public JPanel panel() {
        return this;
    }

    public CtCanvasLayer getHistLayer() {

        CtCanvasLayer layer = new CtCanvasLayer();

        layer.addPainter( new CtCanvasPainter() {
            public void paint(Graphics2D g, CtCanvasLayer l) {
                CtGrayscaleMapCanvas.this.hist.getDst().drawHist( g, l.getPreTransformWidth(), l.getPreTransformHeight(), true, false, false );
            }
        });

        return layer;
    }

    public void applyLut() {
        fillLut();
        lutOp.setLut(lut);
        lutOp.setMinMax(0, 255);
        lutOp.refresh();

        _irt.fireImageResultProcessChanged();
    }

    void fillLut() {
        ImageProcessor ip = lutOp.getSrc().getIP();

        int pixelDepthBytes = 0;
        if( ip instanceof ShortProcessor ) {
            pixelDepthBytes = 2;
        }
        else if( ip instanceof ByteProcessor ) {
            pixelDepthBytes = 1;
        }
//        else if( ip instanceof ColorProcessor ) {
//            ColorProcessor cp = (ColorProcessor)ip;
//            ColorModel cm = cp.getColorModel();
//            cm.
//        }
        else {
            System.out.println("Image type not supported");
        }

        int lutLength = (int)Math.pow(2, pixelDepthBytes*8);
        if(    lut == null
            || lut.length != lutLength ) {
            lut = new int[lutLength];
        }

        int minY = slider.getMinY();
        int maxY = slider.getMaxY();
        int x = slider.getValueX(0);
        for( int i = 0; i < x; ++i ) {
            lut[i] = minY;
        }

        for( int k = 0; k < slider.getNumPoints()-1; ++k ) {
            int x0 = slider.getValueX(k);
            int y0 = slider.getValueY(k);
            int x1 = slider.getValueX(k+1);
            int y1 = slider.getValueY(k+1);
            for( int i = x0; i < x1; ++i ) {
                lut[i] = (i-x0) * (y1-y0) / (x1-x0) + y0;
            }
        }

        x = slider.getValueX( slider.getNumPoints() - 1 );
        for( int i = x; i < lut.length; ++i ) {
            lut[i] = maxY;
        }
    }

}
