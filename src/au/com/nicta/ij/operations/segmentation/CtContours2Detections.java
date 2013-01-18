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

package au.com.nicta.ij.operations.segmentation;

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtRegion;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import ij.process.ShortProcessor;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtContours2Detections {

    public int _previousDetectionCount = 0;
    public double _maxSpawnRate = 2.0;
    public CtRegion _roi = null;
    
    public CtContours2Detections() {
        
    }

    public CtContours2Detections( int previousDetectionCount, double maxSpawnRate, CtRegion roi ) {
        _previousDetectionCount = previousDetectionCount;
        _maxSpawnRate = maxSpawnRate;
        _roi = roi;
    }

    public Collection< CtZoomPolygon > contours2ZoomPolygons( ShortProcessor labelledImage ) {

        int backgroundLabel = 0;
        Collection< Polygon > contours = CtContours.findContours( labelledImage, backgroundLabel );


        // Filter 1. Look for insane spawning rate indicating a problem:
        // ---------------------------------------------------------------------
        boolean filter = false;

        int detectionCount = contours.size();

        if( _previousDetectionCount > 0 ) {
            // e.g. this=3 prev=3 = 3/3=1x this=6 prev=2 rate=6/2=3x
            double spawnRate = (double)detectionCount / (double)_previousDetectionCount;

            if( spawnRate > _maxSpawnRate ) {
                filter = true;
            }
        }

        _previousDetectionCount = detectionCount;

        if( filter ) {
//            return null; seems to be causing problems
        }


        // Filter 2. By region of interest.
        // ---------------------------------------------------------------------
        contours = CtContourFilters.filter( contours, _roi );


        // Ok actually create the detections.
        // ---------------------------------------------------------------------
        ArrayList< CtZoomPolygon > al = new ArrayList< CtZoomPolygon >();

        for( Polygon p : contours ) {
            CtZoomPolygon zp = new CtZoomPolygon( p, CtSubPixelResolution.unitsPerNaturalPixel );

            zp.translate( CtSubPixelResolution.unitsPerNaturalPixel >> 1, CtSubPixelResolution.unitsPerNaturalPixel >> 1 );

            al.add( zp );
        }

        return al;
    }

    public Collection< CtZoomPolygon > contours2Detections( ShortProcessor labelledImage, CtImages i ) {
        Collection< CtZoomPolygon > czp = contours2ZoomPolygons( labelledImage );
        CtTrackingController tc = CtTrackingController.get();
        tc.createDetections( czp, i );
        return czp;
    }
}
