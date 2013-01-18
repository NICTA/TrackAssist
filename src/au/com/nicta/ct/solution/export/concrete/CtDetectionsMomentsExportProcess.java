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
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.export.CtExportCSV;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * All the Hu moments, which describe the shape and position of a contour.
 *
 * @author davidjr
 */
public class CtDetectionsMomentsExportProcess extends CtDefaultExportProcess {

    CtExportCSV ecsv = new CtExportCSV();

    public String getName() {
        return "Detection statistics (.csv)";
    }

    public String apply( CtSolutions s, String filePath ) {
        ecsv.clear();

        // make list of events, ordered by time.
        ecsv.addHeader( getFields() );

//        CtCoordinatesController cc = CtCoordinatesController.get();

        CtTrackingModel tm = CtTrackingController.get().getTrackingModel();
        Set< CtDetections > cd = s.getCtDetectionses();

        Point2D.Double centroid = new Point2D.Double(); // reuse object
        double[] secondMoment = new double[3]; // reuse object

        for( CtDetections d : cd ) {
            ArrayList< String > fields = new ArrayList< String >();

            CtZoomPolygon zp = tm.getBoundary( d );
            double area  = CtPolygonStats.area        (zp.polygon);
            centroid     = CtPolygonStats.centroid    (zp.polygon, area, centroid);
            secondMoment = CtPolygonStats.secondMoment(zp.polygon, secondMoment);
            Rectangle r = zp.polygon.getBounds();

            // scale everything from subpixel resolution to pixel resolution.
            double u = CtSubPixelResolution.unitsPerNaturalPixel;

            area /= u*u;
            centroid.x /= u;
            centroid.y /= u;
            secondMoment[0] /= u*u;
            secondMoment[1] /= u*u;
            secondMoment[2] /= u*u;
            Rectangle.Double bb = new Rectangle.Double(
                    r.x/u,
                    r.y/u,
                    r.width/u,
                    r.height/u );

            fields.add( String.valueOf( d.getPkDetection() ) );
            fields.add( String.valueOf( area ) );
            fields.add( String.valueOf( centroid.x ) );
            fields.add( String.valueOf( centroid.y ) );
            fields.add( String.valueOf( secondMoment[0] ) );
            fields.add( String.valueOf( secondMoment[1] ) );
            fields.add( String.valueOf( secondMoment[2] ) );
            fields.add( String.valueOf( bb.x ) );
            fields.add( String.valueOf( bb.y ) );
            fields.add( String.valueOf( bb.width ) );
            fields.add( String.valueOf( bb.height ) );

            ecsv.addRow(fields);
        }

        // save events to file..
        String result = ecsv.write( filePath );
        return result;
    }

    public Collection< String > getFields() {
        ArrayList< String > fields = new ArrayList< String >();

        fields.add( "pk_detection" );
        fields.add( "area" );
        fields.add( "centroid_x" );
        fields.add( "centroid_y" );
        fields.add( "second_moment_about_x" );
        fields.add( "second_moment_about_y" );
        fields.add( "second_moment_Ixy" );
        fields.add( "bounding_box_x" );
        fields.add( "bounding_box_y" );
        fields.add( "bounding_box_width" );
        fields.add( "bounding_box_height" );

        return fields;
    }

}
