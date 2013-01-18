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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author alan
 */
public class CtImageRegistration {

    public static class Result {
        public Point shift = new Point();
        public int width = 0;
        public int height = 0;
        public ShortProcessor scaled;

        public Rectangle getRoiA() {
            Rectangle roiA = new Rectangle();
            OverLap(
                width,
                height,
                shift.x,
                shift.y,
                roiA,
                null );
            return roiA;
        }

        public Rectangle getRoiB() {
            Rectangle roiB = new Rectangle();
            OverLap(
                width,
                height,
                shift.x,
                shift.y,
                null,
                roiB );
            return roiB;
        }

        public void serialiseToFile(String fileName) throws IOException {
             FileWriter fstream = new FileWriter(fileName);
             BufferedWriter out = new BufferedWriter(fstream);
             serialiseToFile(out);
             out.close();
        }

        public void serialiseToFile(BufferedWriter out) throws IOException {
            out.write( String.valueOf(shift.x) );
            out.newLine();
            out.write( String.valueOf(shift.y) );
            out.newLine();
            out.write( String.valueOf(width) );
            out.newLine();
            out.write( String.valueOf(height) );
            out.newLine();
        }

        public void deserialiseFromFile(String fileName) throws IOException {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            deserialiseFromFile(br);
            fstream.close();
        }

        public void deserialiseFromFile(BufferedReader br) throws IOException {
            String strLine;
            //Read File Line By Line
            shift.x  = Integer.parseInt( br.readLine() );
            shift.y  = Integer.parseInt( br.readLine() );
            width  = Integer.parseInt( br.readLine() );
            height = Integer.parseInt( br.readLine() );
        }
    }

    public class Param {
        public int scaleUp = 1;
        public int[] rangeX = {-1, 1};
        public int[] rangeY = {-1, 1};
    }

    Param param = new Param(); // default
    ShortProcessor refImage;

    public Param getParam() {
        return param;
    }

    public void setParam(Param p) {
        param = p;
    }

    public void setRefImage(ShortProcessor img) {
        refImage = ScaleUp(img, param.scaleUp);
    }

    public ShortProcessor getRefImage() {
        return refImage;
    }

    public static ShortProcessor ScaleUp(ShortProcessor img, int scaleUp) {
        int old = img.getInterpolationMethod();
        img.setInterpolationMethod(ImageProcessor.BILINEAR);
        ShortProcessor ret = (ShortProcessor) img.resize(img.getWidth()*scaleUp);
        return ret;
    }

    public Result register( ShortProcessor img ) {

        Result result = new Result();
        result.scaled = ScaleUp(img, param.scaleUp);
        result.width  = result.scaled.getWidth();
        result.height = result.scaled.getHeight();
        result.shift = Register(refImage, result.scaled, param.rangeX, param.rangeY);

        return result;
    }


    public static Point Register(
            ShortProcessor a,
            ShortProcessor b,
            int[] rangeX,
            int[] rangeY ) {

        Point minScoreLoc = new Point();
        double minScore = Double.MAX_VALUE;

        Rectangle roiA = new Rectangle();
        Rectangle roiB = new Rectangle();

        for( int shiftY = rangeY[0]; shiftY <= rangeY[1]; ++shiftY ) {

            for( int shiftX = rangeX[0]; shiftX <= rangeX[1]; ++shiftX ) {

                OverLap( a.getWidth(), a.getHeight(), shiftX, shiftY, roiA, roiB );

        if(    ( roiA.width  != roiB.width  )
            || ( roiA.height != roiB.height ) ) {
            System.out.println( "registration roiA.x,y,w,h=" + roiA.x + "," + roiA.y + "," + roiA.width + "," + roiA.height );
            System.out.println( "registration roiB.x,y,w,h=" + roiB.x + "," + roiB.y + "," + roiB.width + "," + roiB.height );
            throw new RuntimeException();
        }
//                a.resetRoi();
//                b.resetRoi();
//                a.reset(a.getMask());
//                b.reset(b.getMask());
                a.setRoi(roiA);
                b.setRoi(roiB);

                double s = SumAbsDiff(a, b);
                System.out.println( "(shift_x, shift_y, s): " + shiftX + " " + shiftY + " " + s );

                if( minScore > s ) {
                    minScore = s;
                    minScoreLoc.x = shiftX;
                    minScoreLoc.y = shiftY;
                }
            }
        }

        a.resetRoi();
        b.resetRoi();

        return minScoreLoc;
    }

    public static double SumAbsDiff(ShortProcessor a, ShortProcessor b) {

        Rectangle roiA = a.getRoi();
        Rectangle roiB = b.getRoi();

        if(    ( roiA.width  != roiB.width  )
            || ( roiA.height != roiB.height ) ) {
            System.out.println( "registration roiA.x,y,w,h=" + roiA.x + "," + roiA.y + "," + roiA.width + "," + roiA.height );
            System.out.println( "registration roiB.x,y,w,h=" + roiB.x + "," + roiB.y + "," + roiB.width + "," + roiB.height );
            throw new RuntimeException();
        }
 
        int w = roiA.width;
        int h = roiA.height;

        double sum = 0;
        int xa = roiA.x;
        int ya = roiA.y;

        int xb = roiB.x;
        int yb = roiB.y;

        for( int y = 0; y < h; ++y ) {
            for( int x = 0; x < w; ++x ) {
                sum += Math.abs( a.get(x+xa, y+ya) - b.get(x+xb, y+yb) );
            }
        }

        return sum/(h*w);
    }

    public static ShortProcessor AbsDiff(ShortProcessor a, ShortProcessor b) {
        Rectangle roiA = a.getRoi();
        Rectangle roiB = b.getRoi();

        System.out.println( "roiA: " + roiA );
        System.out.println( "roiB: " + roiB );

        if( roiA.width != roiB.width ) {
            throw new RuntimeException();
        }
        if( roiA.height != roiB.height ) {
            throw new RuntimeException();
        }

        int w = roiA.width;
        int h = roiA.height;

        double sum = 0;
        int xa = roiA.x;
        int ya = roiA.y;

        int xb = roiB.x;
        int yb = roiB.y;

        ShortProcessor dst = new ShortProcessor(w, h);

        for( int y = 0; y < h; ++y ) {
            for( int x = 0; x < w; ++x ) {
                int d = Math.abs( a.get(x+xa, y+ya) - b.get(x+xb, y+yb) );
                dst.set( x, y, d );
            }
        }

        dst.setMinAndMax(0, a.getMax());

        return dst;
    }


    public static ImageProcessor crop(ImageProcessor ip) {
    	ip.setRoi(832,5,265,219);
        return ip.crop();
    }

    public static ImagePlus crop(ImagePlus img) {
        return new ImagePlus( "", crop(img.getProcessor()) );
    }

    public static void OverLap(
            int width,
            int height,
            int shiftX,
            int shiftY,
            Rectangle roiA,
            Rectangle roiB ) {

        int w = width  - Math.abs(shiftX);
        int h = height - Math.abs(shiftY);

        if( roiA != null ) {
            roiA.width  = w;
            roiA.height = h;
            roiA.x = shiftX >= 0 ? shiftX : 0;
            roiA.y = shiftY >= 0 ? shiftY : 0;
        }

        if( roiB != null ) {
            roiB.width  = w;
            roiB.height = h;
            roiB.x = shiftX <= 0 ? -shiftX : 0;
            roiB.y = shiftY <= 0 ? -shiftY : 0;
        }
    }

}