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
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
//import au.com.nicta.ct.images.tools.microwells.CtMicrowellsController;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsController;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.solution.export.CtExportCSV;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.lineage.CtLineageController;
import au.com.nicta.ct.solution.lineage.CtLineageModel;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Return cell count over time, all wells, ONLY tracked detections - others ignored.
 * 
 * @author davidjr
 */
public class CtTrackedDetectionCountExportProcess extends CtDefaultExportProcess {

    CtExportCSV _ecsv = new CtExportCSV();

    public CtTrackedDetectionCountExportProcess() {
        
    }

    public String getName() {
        return "Tracked Detection Counts (.csv)";
    }

    public String apply( CtSolutions s, String filePath ) {

        // make list of events, ordered by time.
        createEvents( s );

        // save events to file..
        String result = _ecsv.write( filePath );
        return result;
    }

    protected void createEvents( CtSolutions s ) {

//        CtExperimentModel em = CtExperimentModel.get();
//        CtImageSequenceModel ism = em._isf.getModel();
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtMicrowellsController mc = CtMicrowellsController.get();
        CtLineageController lc = CtLineageController.get();// _em._cc, _em._isf );
        CtTrackingController tc = CtTrackingController.get();
//        CtSolutionController sc = CtSolutionController.get();
//        CtSolutionModel sm = sc.getModel();

 //       Collection< CtTracks > ct = sm.getTracks();
        Collection< CtTracks > ct = s.getCtTrackses(); /// ALL tracks in solution.
        // TODO: list orphan events?

        _ecsv.clear();

        ArrayList< String > fields = new ArrayList< String >();
        
        fields.add( "Time" );
        fields.add( "Count" );

        _ecsv.addHeader( fields );

        int t1 = cc.getCoordinatesModel().getMinOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );
        int t2 = cc.getCoordinatesModel().getMaxOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );

        HashSet< CtDetections > exclude = new HashSet< CtDetections >(); // cos detections appear 3x at forks in tracks, but should be counted once.

        HashMap< Integer, Integer > timesCounts = new HashMap< Integer, Integer >();

        for( CtTracks t : ct ) {

            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

            for( CtTracksDetections td : tds ) {

                CtDetections d = td.getCtDetections();

                if( exclude.contains( d ) ) {
                    continue;
                }

                CtImages i = d.getCtImages();
                int time = cc.getTimeOrdinate( i );//.//ism.index( i );

                Integer n = timesCounts.get( time );

                int count = 1;

                if( n != null ) {
                    count = count + n;
                }

                timesCounts.put( time, count );

                exclude.add( d );
            }
        }

        for( int t = t1; t <= t2; ++t ) {

            int count = 0;

            Integer n = timesCounts.get( t );

            if( n != null ) {
                count = n;
            }

            addEvent( t, count, _ecsv );
        }
    }

    protected void addEvent( int time, int count, CtExportCSV ecsv ) {
        ArrayList< String > al = new ArrayList< String >();
        al.add( String.valueOf( time ) );
        al.add( String.valueOf( count ) );
        ecsv.addRow( time, al );
    }

}
