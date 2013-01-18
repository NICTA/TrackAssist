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

import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.orm.mvc.images.CtCachedImages;
import au.com.nicta.ct.solution.CtSolutionController;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author alan
 */
public class CtBackgroundModel {

    public static final String BACKGROUND_FILE_NAME = "bg.tiff";
    
    public ShortProcessor _sp;
//    int width;
//    int height;

    public CtBackgroundModel() {

    }

//    public CtBackgroundModel( int width, int height ) {
//        this.width  = width;
//        this.height = height;
//    }
//
//    public CtBackgroundModel( ShortProcessor img ) {
//        this(img.getWidth(), img.getHeight());
//        addImage(img);
//    }

    public static String getBackgroundFileName() {
        CtSolutions s = CtSolutionController.getSolutions();
        String uri = getBackgroundFileName( s );
        return uri;
    }

    public static String getBackgroundFileName( CtSolutions s ) {
        return CtApplication.solutionPath( s ) + "_" + BACKGROUND_FILE_NAME;
    }

    public ImagePlus loadImagePlus( String uri ) throws IOException {

        ImagePlus ip = CtCachedImages.Get( uri );
        return ip;
    }

    public void load() throws IOException {

        reset();
        
        ImagePlus ip = loadImagePlus( getBackgroundFileName() );
        
        if( ip == null ) {
            throw new IOException( "Image not found or loaded successfully." );
        }

        ImageProcessor ip2 = ip.getProcessor();
        _sp = (ShortProcessor)ip2.convertToShort( false ); // assume the image is ALREADY normalized.
//        width = ip.getWidth();
//        height = ip.getHeight();
    }

    public void save() { //throws IOException {
        String uri = getBackgroundFileName();
        ImagePlus ip = new ImagePlus( "", _sp );
        IJ.saveAs( ip, "tif", uri );
    }

    public boolean isReady() {
        if( _sp == null ) {
            return false;
        }
        return true;
    }
    public void reset() {
        _sp = null;
        _ba = null;
    }

    public void addImage( ShortProcessor img ) {
        int w = img.getWidth();
        int h = img.getHeight();
        addImage( new Rectangle( w, h ), new Rectangle( w, h ), img ); // add entire image a as background.
    }

    public void addImage( Rectangle backgroundRoi, Rectangle imgRoi, ShortProcessor img ) {
        if( _ba == null ) {
            _ba = new CtBackgroundAccumulator( backgroundRoi.width, backgroundRoi.height );
        }

        addImage( backgroundRoi, imgRoi, img, _ba );
    }

    public void finish() {
        _sp = createShortProcessor( _ba );
    }

    class CtBackgroundAccumulator {
        int w;
        int h;
        double[] sum;
//        int[] imageCount;
        int imageCount = 0;

        public CtBackgroundAccumulator( int width, int height ) {
            reset( width, height );
        }

        public void reset( int width, int height ) {
            w = width;
            h = height;
            sum = new double[ width * height ];
            imageCount = 0;
//            imageCount = new int[ width * height ];
        }
    }

    private CtBackgroundAccumulator _ba;

    private void addImage( Rectangle backgroundRoi, Rectangle imgRoi, ShortProcessor img, CtBackgroundAccumulator ba ) {

        if( imgRoi.width == 0 ) {
            imgRoi.x = 0;
            imgRoi.y = 0;
            imgRoi.width  = img.getWidth();
            imgRoi.height = img.getHeight();
        }
        if(    imgRoi.width == 0
            || imgRoi.width  != backgroundRoi.width
            || imgRoi.height != backgroundRoi.height ) {
            System.out.println( "imgRoi.width: " + imgRoi.width );
            System.out.println( "imgRoi.height: " + imgRoi.height );
            System.out.println( "backgroundRoi.width: " + backgroundRoi.width );
            System.out.println( "backgroundRoi.height: " + backgroundRoi.height );
            throw new RuntimeException("Bad roi");
        }

        for( int y = 0; y < imgRoi.height; ++y ) {
            for( int x = 0; x < imgRoi.width; ++x ) {
                int sx = x + backgroundRoi.x;
                int sy = y + backgroundRoi.y;
                int ix = x + imgRoi.x;
                int iy = y + imgRoi.y;

                int sIdx = sy * ba.w + sx;
                ba.sum[sIdx] += img.get( ix,iy );
//                ++ba.imageCount;//[sIdx];
            }
        }
        ++ba.imageCount;//[sIdx];
    }

    private ShortProcessor createShortProcessor( CtBackgroundAccumulator ba ) {
        ShortProcessor sp = new ShortProcessor( ba.w, ba.h );

        if( ba.imageCount == 0 ) {
            return sp;
        }

        double reciprocal = 1.0 / (double)ba.imageCount;

        for( int y = 0; y < ba.h; ++y ) {
            for( int x = 0; x < ba.w; ++x ) {
                int idx = y * ba.w + x;
                double sum = ba.sum[ idx ];
                sp.set( x, y, (int)( sum * reciprocal ) );
            }
        }

        return sp;
    }

}
