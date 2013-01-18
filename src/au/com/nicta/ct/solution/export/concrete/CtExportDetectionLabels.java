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

package au.com.nicta.ct.solution.export.concrete;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
//import au.com.nicta.ct.mvc.images.CtCachedImages;
import au.com.nicta.ct.orm.mvc.images.CtCachedImages;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeMap;
import org.omg.CORBA.UShortSeqHelper;

/**
 *
 * @author alan
 */
public class CtExportDetectionLabels {

//    public static int getFirstImageIdx() {
//        return 0;
//    }
//
//    public static int getLastImageIdx() {
//        return 3;
//    }

    public static void export( String exportDir ) {
//        export( exportDir, getFirstImageIdx(), getLastImageIdx() );
//    }
//
//    public static void export(
//            String exportDir,
//            int timeIdxSta,
//            int timeIdxEnd ) {

        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            throw new RuntimeException("Can't find CtTrackingController singleton");
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            throw new RuntimeException("Can't find CtTrackingModel singleton");
        }

        if( !tm.valid() ) {
            throw new RuntimeException("CtTrackingModel is not valid.");
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        int timeIdxSta = cm.getMinOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );
        int timeIdxEnd = cm.getMaxOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );
//        class Info {
//            public String imageUri;
//            public TreeMap<Integer, CtDetections> detections; // sorted according to PK
//        }


        String[] uri = new String[ timeIdxEnd - timeIdxSta + 1 ];

        // fill the image URIs
        for( int i = 0; i < uri.length; ++i ) {
            try {
                uri[i] = tm._ism.get( i + timeIdxSta ).getUri();
            }
            catch( IOException e ) {
                throw new RuntimeException("Can't load image at idx: " + (i + timeIdxSta) );
            }
        }

        TreeMap<Integer, DetectionsSortedByPk> timeSorted = sortDetectionsByTime(tm);

        // fill the detections
//        Collection< CtDetections > cd = tm._s.getCtDetectionses();
//        for( CtDetections d : cd ) {
//            int detectionIndex = tm.getTimeOrdinate( d );
//
//            if(    detectionIndex < timeIdxSta
//                || detectionIndex > timeIdxEnd ) {
//                continue;
//            }
//
//            Info info = timeSorted[detectionIndex - timeIdxSta];
//            if( info.detections == null ) {
//                info.detections = new TreeMap<Integer, CtDetections>();
//            }
//            info.detections.put( d.getPkDetection(), d );
//        }

        // Find the image dimensions
        ImagePlus firstImage;
        try {
            firstImage = CtCachedImages.Get( tm._ism.get( timeIdxSta ) );
        }
        catch( IOException e ) {
            throw new RuntimeException("Can't load image");
        }

        int imageWidth  = firstImage.getWidth();
        int imageHeight = firstImage.getHeight();


        // Draw and output
//        const int scale = 10;
        for( int i = 0; i < uri.length; ++i ) {

            BufferedImage labelImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_USHORT_GRAY);
            DetectionsSortedByPk detections = timeSorted.get(i+timeIdxSta);

            if( detections != null ) {
                drawPolygonLabel(detections.map.values(), tm, labelImage);
            }

            String fileName = uri[i];
            String[] fileNameParts = fileName.split("[\\\\/]");
            if( fileNameParts != null ) { // strip dir from file name if required
                fileName = fileNameParts[ fileNameParts.length - 1 ];
            }
            String dst = exportDir + "/" + fileName + "_detections.tif";

            ImagePlus ip = new ImagePlus("Labelled", labelImage );
            FileSaver fs = new FileSaver( ip );
            boolean b = fs.saveAsTiff( dst );

            if( !b ) {
                throw new RuntimeException( "ERROR: Can't save image to path: "+dst );
            }
        }
    }

    public static void drawTest(BufferedImage dst) {
        final short[] pixels = ((DataBufferUShort) dst.getRaster().getDataBuffer()).getData();
        for( int r = 0; r < dst.getHeight(); ++r ) {
            int i = r * dst.getWidth();
            for( int c = 0; c < dst.getWidth(); ++c, ++i ) {
                pixels[i] = 1;
            }
        }
    }

    public static void drawPolygonLabel(Collection<CtDetections> l, CtTrackingModel tm, BufferedImage dst) {
        final short[] pixels = ((DataBufferUShort) dst.getRaster().getDataBuffer()).getData();
        short label = 1;
        for( CtDetections d : l ) {
            CtZoomPolygon zp = tm.getBoundary( d );
            // get bounding box so we can test each pixel inside it
            Rectangle bb = zp.getBoundingBox().getBounds(); // in sub pixel resolution
            bb.x      /= CtSubPixelResolution.unitsPerNaturalPixel;
            bb.y      /= CtSubPixelResolution.unitsPerNaturalPixel;
            bb.width  /= CtSubPixelResolution.unitsPerNaturalPixel;
            bb.height /= CtSubPixelResolution.unitsPerNaturalPixel;

            for( int y = bb.y; y < bb.y + bb.height; ++y ) {
                int row = y * dst.getWidth();
                for( int x = bb.x; x < bb.x + bb.width; ++x ) {
                    if( zp.containsNaturalCoord(x, y) ) {
                        pixels[row + x] = label;
                    }
                }
            }
            ++label;
        }
    }

    public static class DetectionsSortedByPk {
        TreeMap<Integer, CtDetections> map = new TreeMap<Integer, CtDetections>(); // needs to be sorted
    }

    public static TreeMap<Integer, DetectionsSortedByPk> sortDetectionsByTime(CtTrackingModel tm) {
        // fill the detections
        TreeMap<Integer, DetectionsSortedByPk> sortedByTime = new TreeMap<Integer, DetectionsSortedByPk>();

        Collection< CtDetections > cd = tm._s.getCtDetectionses();
        for( CtDetections d : cd ) {
            int detectionIndex = tm.getTimeOrdinate( d );

            DetectionsSortedByPk pkMap = sortedByTime.get( detectionIndex );
            if( pkMap == null ) {
                pkMap = new DetectionsSortedByPk();
                sortedByTime.put( detectionIndex, pkMap );
            }
            pkMap.map.put(d.getPkDetection(), d);
        }

        return sortedByTime;
    }
}

