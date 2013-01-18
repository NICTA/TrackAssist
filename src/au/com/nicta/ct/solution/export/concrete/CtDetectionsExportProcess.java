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
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.export.CtExportCSV;
import au.com.nicta.ct.solution.export.concrete.CtExportDetectionLabels.DetectionsSortedByPk;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

/**
 * Export all the details of the detections - ie position and contour - of all
 * tracks.
 * @author davidjr
 */
public class CtDetectionsExportProcess extends CtDefaultExportProcess {

    CtExportCSV ecsv = new CtExportCSV();

    public String getName() {
        return "Detections (.csv)";
    }

    public String apply( CtSolutions s, String filePath ) {

        ecsv.clear();
        
        // make list of events, ordered by time.
        ecsv.addHeader( getFields() );

        // Sort the detection by time and them by pk so that we can use 1 based IDs
        // for each tiff image.
        TreeMap<Integer, DetectionsSortedByPk> timeSorted
                = CtExportDetectionLabels.sortDetectionsByTime( CtTrackingController.getModel() );

        CtCoordinatesController cc = CtCoordinatesController.get();

        for( DetectionsSortedByPk pkSorted : timeSorted.values() ) {

            int labelIdx = 1;
            for( CtDetections d : pkSorted.map.values() ) {
                CtImages i = d.getCtImages();
                int time = cc.getTimeOrdinate( i );

                ArrayList< String > fields = new ArrayList< String >();

                fields.add( String.valueOf( d.getPkDetection() ) );
                fields.add( String.valueOf( s.getPkSolution() ) );
                fields.add( String.valueOf( i.getPkImage() ) );
                fields.add( String.valueOf( time ) );
                fields.add( String.valueOf( labelIdx ) );
                fields.add( d.getBoundary() );

                ecsv.addRow(fields);
                ++labelIdx;
            }
        }

        // save events to file..
        String result = ecsv.write( filePath );
        return result;
    }

    public Collection< String > getFields() {
//     private int pkDetection;
//     private CtSolutions ctSolutions;
//     private CtImages ctImages;
//     private String location;
//     private String boundary;
//     private Set ctTracksDetectionses = new HashSet(0);
        ArrayList< String > fields = new ArrayList< String >();

        fields.add( "pk_detection" );
        fields.add( "pk_solution" ); // useful for comparing lists to see which solution it's in
        fields.add( "pk_image" ); // useful for comparing lists to see which solution it's in
        fields.add( "time" ); // handy shortcut, denormalized, assumes we always have a time coordinate.
        fields.add( "label" ); // handy shortcut, denormalized, assumes we always have a time coordinate.
        fields.add( "boundary" ); // detailed contour in pixels

        return fields;
    }

}
