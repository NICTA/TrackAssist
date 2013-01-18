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

package au.com.nicta.ij.operations;

import au.com.nicta.ij.graph.CtOperationNode;
import au.com.nicta.ij.graph.CtResultNode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.Arrays;

/**
 *
 * @author Alan
 */
public class CtHistogramResult extends CtResultNode {

    private int[] hist;
    private int[] x;
    private int[] y;
    int min = 0;
    int max = 0;

    public CtHistogramResult(CtOperationNode op) {
       super(op);
    }

    public void setHist(int[] hist) {
        this.hist = hist;
        setChanged();
    }

    public int[] getHist() {
        refresh();
        return hist;
    }

    public void setMinMax(int min, int max) {
        this.min = min;
        this.max = max;
        setChanged();
    }

    int hist2Polygon(
            int histLoIdx,
            int histHiIdx,
            int width,
            int height ) {

        int numPoints = histHiIdx - histLoIdx + 1 + 2; // + 2 for the end points

//        if( x == null || x.length < numPoints ) {
//        if( x == null || ( x.length != numPoints ) ) {
            x = new int[numPoints];
//        }
//        if( y == null || y.length < numPoints ) {
//        if( y == null || ( y.length != numPoints ) ) {
            y = new int[numPoints];
//        }

        int maxCount = 0;
        for( int c : hist ) {
            if( maxCount < c ) {
                maxCount = c;
            }
        }

        float xScale = (float) width  / (histHiIdx - histLoIdx);
        float yScale = (float) height / maxCount;

//System.out.println( "hist minIdx="+histLoIdx+" maxIdx="+histHiIdx+" w="+width+" h="+height);
//System.out.println( "hist xScale="+xScale+" yScale="+yScale );

        int k = 0;
        for( int i = histLoIdx; i <= histHiIdx; ++i, ++k ) {
            x[k] = (int)( (i-histLoIdx) * xScale);
            y[k] = (int)( (float)height - hist[i] * yScale ); // round down
//System.out.print( "["+x[k]+","+y[k]+"] " );

            if( hist[i] > 0 ) {
                y[k] = Math.min( y[k], height-2 );
            }
        }
//System.out.println( "" );

        x[k] = x[k-1];
        y[k] = height;
        x[k+1] = x[0];
        y[k+1] = height;

        return numPoints;
    }

    // get percentage of histogram
    public int getPercentInd( double percent ) {
        int ind = 0;
        int sumHist = 0;

        for( int i = 0; i < hist.length; ++i ) {
            sumHist = sumHist + hist[i];
        }

        int runHist = 0;
        for( int i = 0; i < hist.length-1; ++i ) {
            runHist = runHist + hist[i];

            if (( (double) (runHist/sumHist) <= percent ) && ( (double)(runHist + hist[i+1])/sumHist >= percent )) {
                ind = i;
                break;
            }
        }

        return ind;
    }

    // get percentage of histogram
    public double getPercent( double value ) {
        int sumHist = 0;
        for( int i = 0; i < hist.length; ++i ) {
            sumHist = sumHist + hist[i];
        }

        int runHist = 0;
        for( int i = 0; i <= value; ++i ) {
            runHist = runHist + hist[i];
        }

        double percent = (double) runHist/sumHist;
        return percent;
    }

    public int maxHistInd() {
        int maxVal = hist[0];
        int maxInd = 0;

        for( int i = 1; i < hist.length; ++i ) {
            if( hist[i] > maxVal ) {
                maxVal = hist[i];
                maxInd = i;
            }
        }
        return maxInd;
    }
    
    int doMinNonZeroBin() {
        for( int i = 0; i < hist.length; ++i ) {
            if( hist[i] != 0 ) {
                return i;
            }
        }
        return -1;
    }
    
    int doMaxNonZeroBin() {
        for( int i = hist.length-1; i >= 0; --i ) {
            if( hist[i] != 0 ) {
                return i;
            }
        }
        return -1;
    }

    public int minNonZeroBin() {
        refresh();
        return doMinNonZeroBin();
    }

    public int maxNonZeroBin() {
        refresh();
        return doMaxNonZeroBin();
    }

    public void drawHist(
            Graphics2D g,
            int width,
            int height,
            boolean horizontal,
            boolean mirrowLeftRight,
            boolean mirrowUpDown ) {
        refresh();

        int minIdx, maxIdx;
        if( min != 0 || max != 0 ) { //dave: removed to try to find bug
            minIdx = min;
            maxIdx = max;
        }
        else {
            minIdx = minNonZeroBin();
            maxIdx = maxNonZeroBin();
        }

        int numPoints = hist2Polygon( minIdx, maxIdx, width, height );

//System.out.println( "hist minIdx="+minIdx+" maxIdx="+maxIdx+" w="+width+" h="+height);
///*
        if( !horizontal ) {
            int[] t = x;
            x = y;
            y = t;
        }

        if( mirrowLeftRight ) {
            for( int i = 0; i < numPoints; ++i ) {
                x[i] = width - x[i];
            }
        }

        if( mirrowUpDown ) {
            for( int i = 0; i < numPoints; ++i ) {
                y[i] = height - y[i];
            }
        }
//*/

        BasicStroke stroke = new BasicStroke(0);
        Paint paint = new Color(0,0,0);

        g.setPaint(paint);
        g.setStroke(stroke);
        g.fillPolygon(x, y, numPoints);
//g.setColor( Color.RED );
//g.drawPolygon(x, y, numPoints);
    }
}


















