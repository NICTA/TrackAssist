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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author davidjr
 */
public class CtTrackEventExportProcess extends CtDefaultExportProcess {

    CtExportCSV _ecsv = new CtExportCSV();

    public CtTrackEventExportProcess() {
        
    }

    public String getName() {
        return "Tracking Event List (.csv)";
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
        fields.add( "Microwell" );
        fields.add( "Identity" );
        fields.add( "Event" );

        _ecsv.addHeader( fields );

        for( CtTracks t : ct ) {

            // track start, end, split
            createEvents( s, t, tc, lc, mc, cc, _ecsv );
        }
    }

    protected void addEvent( int time, String microwell, String identity, String event, CtExportCSV ecsv ) {
        ArrayList< String > al = new ArrayList< String >();
        al.add( String.valueOf( time ) );
        al.add( microwell );
        al.add( identity );
        al.add( event );
        ecsv.addRow( time, al );
    }

    protected void createEvents( CtSolutions s, CtTracks t, CtTrackingController tc, CtLineageController lc, CtMicrowellsController mc, CtCoordinatesController cc, CtExportCSV ecsv ) {

        // find time of first detection:
        Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

        CtTrackingModel tm = tc.getTrackingModel();
        CtLineageModel lm = lc.getModel();
        String identity1 = lm.getIdentity( t );

        // assume sequence is time:
        int minIndex = Integer.MAX_VALUE;
        int maxIndex = 0;

        HashSet< String > microwellNames = new HashSet< String >();
        
        for( CtTracksDetections td : tds ) {
            CtDetections d = td.getCtDetections();
            CtImages i = d.getCtImages();
            int index = cc.getTimeOrdinate( i );//.//ism.index( i );

            if( index < minIndex ) {
                minIndex = index;
            }
            if( index > maxIndex ) {
                maxIndex = index;
            }

            CtZoomPolygon zp = tm.getBoundary( d );
            Area a = zp.toArea();
            AffineTransform at = new AffineTransform();
            at = CtSubPixelResolution.getAffineToNatural( at );
            a.transform( at );

            Collection< String > cs = mc.findMicrowellNames( a );// zp.polygon );

            for( String name : cs ) {
                microwellNames.add( name );
            }
        }

        String microwell = "n/a";

        if( !microwellNames.isEmpty() ) {
            microwell = "";
            boolean first = true;

            for( String name : microwellNames ) {
                if( !first ) {
                    microwell = microwell + ",";
                }
                first = false;
                microwell = microwell + name;
            }
        }

        CtTracks t2 = lm.getParent( t );

        if( t2 != null ) {
            String identity2 = lm.getIdentity( t2 );
            addEvent( minIndex, microwell, identity2, EVENT_TRACK_SPLIT, ecsv );
        }

        addEvent( minIndex, microwell, identity1, EVENT_TRACK_START, ecsv );
        addEvent( maxIndex, microwell, identity1, EVENT_TRACK_END, ecsv );

    }

    public static final String EVENT_TRACK_SPLIT = "Track Split";
    public static final String EVENT_TRACK_START = "Track Start";
    public static final String EVENT_TRACK_END = "Track End";

}
