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

package au.com.nicta.ct.solution.tracking.jipda;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowell;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsController;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsModel;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtRegion;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.CtSolutionController;
import au.com.nicta.tk.TkCell;
import au.com.nicta.tk.TkDetection;
import au.com.nicta.tk.TkDetections;
import au.com.nicta.tk.TkDetectionsSeries;
import au.com.nicta.tk.TkHandleDivisions;
import au.com.nicta.tk.TkKalmanFilter;
import au.com.nicta.tk.TkLJIPDAForcedAssociator;
import au.com.nicta.tk.TkLJIPDAState;
import au.com.nicta.tk.TkLJIPDATracker;
import au.com.nicta.tk.TkPruneTracks;
import au.com.nicta.tk.tree.TkStringKey;
import au.com.nicta.tk.TkTrack;
import au.com.nicta.tk.TkTrackElement;
import au.com.nicta.tk.TkTracks;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

/**
 *
 * @author Alan
 */
public class CtTkDBTracker {

    static final TkStringKey<String> detectionDescriptionKey = TkStringKey.newInstance("Description");
    static final TkStringKey<Integer> DETECTION_PK = TkStringKey.newInstance( "detection-pk" );

    HashMap< Integer, String > _stepsMessages = new HashMap< Integer, String >();
    CtTrackerState _ts = new CtTrackerState();
    TkPruneTracks pruneTracks = new TkPruneTracks();
    
    CtParaTracker _para;
    String microWellName;

    public CtTkDBTracker( CtParaTracker para ) {
        _para = para;
        _stepsMessages.put( 0, "Loading detections" );
        _stepsMessages.put( 1, "Forcing associations" );
        _stepsMessages.put( 2, "Creating tracker" );
        _stepsMessages.put( 3, "Running tracker" );
        _stepsMessages.put( 4, "Handling divisions" );
        _stepsMessages.put( 5, "Saving tracks" );        
    }
    
    public void setMicroWellName( String s ) {
        microWellName = s;
    }

    public String getMicroWellName() {
        return microWellName;
    }

    public String findMessage( int step ) {
        return _stepsMessages.get( step );
    }

    public int steps() {
        return 8;
    }

//    // progress state {
//    int _startTimeIdxInclusive = -1;//120;
//    int   _endTimeIdxInclusive = -1;//130;
//    TkDetectionsSeries _series;
//    TkLJIPDATracker _tracker;
//    TkTracks _tracks;
//    // }

    public void doAllSteps() {
        int steps = steps();

        for( int n = 0; n < steps; ++n ) {
            doStep( n );
        }
    }

    public void doStep( int step ) {
System.out.println( "DOING STEP #"+step );
//  try {
//          Thread.sleep( 800 );
//        }catch ( Exception e ){
//            e.printStackTrace();
//            System.exit( -1 );
//        }
//        return;

        // *** 1 ***
        if( step == 0 ) {
            _ts._startTimeIdxInclusive = -1;//120;
    //        int   endTimeIdxInclusive = Integer.MAX_VALUE;
            _ts._endTimeIdxInclusive = -1;//130;
            _ts._series = loadDetections( _ts._startTimeIdxInclusive, _ts._endTimeIdxInclusive );
            return;
        }

        // find the first and last time idx
        // *** 2 ***
        if( step == 1 ) {
            TkDetections first = _ts._series.get(0);
            _ts._startTimeIdxInclusive = first.get(0).timeIdx;

            TkDetections last = _ts._series.get( _ts._series.size() - 1 );
            _ts._endTimeIdxInclusive = last.get( last.size() - 1 ).timeIdx;

            // force any manual associations
//            forceAssociate( _ts._series );
            return;
        }

        // run tracker
        // *** 3 ***
        if( step == 2 ) {
            _ts._tracker = createTracker();
            return;
        }
        
        // *** 4 ***
        if( step == 3 ) {
            _ts._tracks = runTracker( _ts._tracker, _ts._series );
            return;
        }

        // post process tracks to remove some sperious tracks
        // *** 5 ***
        if( step == 4 ) {
//            pruneTracks.minNumDetectionsPerFinalTrack = 0; // want to not filter anything on this criteria
            _ts._tracks = pruneTracks.process( _ts._tracks );
            return;
        }

        // *** 6 ***
        if( step == 5 ) {
            TkHandleDivisions handleDivisions = new TkHandleDivisions( _ts._tracker.stateKey );
            handleDivisions.process( _ts._tracks );

            for( int i = 0; i < _ts._tracks.size(); ++i ) {
                if( _ts._tracks.get(i).elements.get(0).det == null ) {
                    throw new Error( "Null detection not allowed as first detection of track." );
                }
            }
            return;
        }

        // post process tracks to remove some sperious tracks
        // *** 7 ***
        if( step == 6 ) {
//            pruneTracks.minNumDetectionsPerFinalTrack = 0;
            _ts._tracks = pruneTracks.process( _ts._tracks );
            return;
        }

        // *** 8 ***
        if( step == 7 ) {
            saveTracks( _ts._tracks, _ts._startTimeIdxInclusive, _ts._endTimeIdxInclusive );
        }
    }

/*    public static void forceAssociate( TkDetectionsSeries series ) {
        // force associate detections between time 0 and 1
        
//        int cnt = Math.min( series.get(0).size(), series.get(1).size() );

        // Force cross tracks
        // --------------------------------------------------------------------
//        for( int i = 0; i < cnt; ++i ) {
//            TkDetection t0 = series.get(0).get( (i+0)%cnt );
//            TkDetection t1 = series.get(1).get( (i+1)%cnt );
//
//            TkDetectionAssocInfo info;
//
//            info = new TkDetectionAssocInfo();
//            info.prev = null;
//            info.next = t1;
//            t0.add( TkDetectionAssocInfo.name, info );
//
//            info = new TkDetectionAssocInfo();
//            info.prev = t0;
//            info.next = null;
//            t1.add( TkDetectionAssocInfo.name, info );
//        }

        // Force track termination
        // --------------------------------------------------------------------
//        {
//            TkDetection d = series.get(2).get(0);
//
//            TkDetectionAssocInfo info;
//
//            info = new TkDetectionAssocInfo();
//            info.terminateTrack = true;
//            d.add( TkDetectionAssocInfo.name, info );
//        }

        // Force missed detection, not supported.
        // --------------------------------------------------------------------
//        {
//            TkDetection t0 = series.get(1).get(0);
//            TkDetection t2 = series.get(3).get(0);
//
//            TkDetectionAssocInfo info;
//
//            info = new TkDetectionAssocInfo();
//            info.prev = null;
//            info.next = t2;
//            t0.add( TkDetectionAssocInfo.name, info );
//
//            info = new TkDetectionAssocInfo();
//            info.prev = t0;
//            info.next = null;
//            t2.add( TkDetectionAssocInfo.name, info );
//        }
    }*/

    public TkLJIPDATracker createTracker() {
        TkStringKey<TkLJIPDAState> stateKey = TkStringKey.newInstance("state");
        TkLJIPDATracker tracker = new TkLJIPDATracker(2, 2, stateKey);

//        // sensor parameters
//        TkKalmanFilter kf = (TkKalmanFilter) tracker.getKalmanFilter();
//        kf.msurMatrix      = MatrixFactory.eye( ValueType.DOUBLE, tracker.msurDims, tracker.stateDims );
//        kf.msurNoiseCov    = MatrixFactory.eye( ValueType.DOUBLE, tracker.msurDims, tracker.msurDims ).times( 5.0 );
//        kf.processMatrix   = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims );
////        kf.processNoiseCov = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims ).times( 125 );
////        kf.processNoiseCov = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims ).times( 900 );
//        kf.processNoiseCov = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims ).times( 2700 );
//
//        TkLJIPDAForcedAssociator assoc = new TkLJIPDAForcedAssociator( stateKey );
//        tracker.setAssociator( assoc );
//        assoc.gateThreshold = 10;
////        assoc.PD = 0.75;
////        assoc.PG = 0.95;
//        assoc.PD = 0.95;
//        assoc.PG = 0.95;
//
//        tracker.existenceGamma = 0.95;
//        tracker.initExistenceProb = 0.1;
//        tracker.terminateExistenceThresh = 1e-3;
        
        // sensor parameters
        TkKalmanFilter kf = (TkKalmanFilter) tracker.getKalmanFilter();
        kf.msurMatrix      = MatrixFactory.eye( ValueType.DOUBLE, tracker.msurDims, tracker.stateDims );
        kf.msurNoiseCov    = MatrixFactory.eye( ValueType.DOUBLE, tracker.msurDims, tracker.msurDims ).times( 1 );
        kf.processMatrix   = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims );
        kf.processNoiseCov = MatrixFactory.eye( ValueType.DOUBLE, tracker.stateDims, tracker.stateDims ).times( _para.getProcessCov() );;
//        TkLJIPDAForcedAssociator assoc = new TkLJIPDAForcedAssociator( stateKey );
        TkLJIPDAForcedAssociator assoc = new CtLJIPDAAssociator( stateKey );
        tracker.setAssociator( assoc );
        assoc.gateThreshold = 10;
        assoc.PD = 0.95;
        assoc.PG = 0.95;
        tracker.existenceGamma = _para.getGamma();
        tracker.initExistenceProb = _para.getPTS();
        tracker.terminateExistenceThresh = _para.getPTE();
        pruneTracks.maxMissingDetectionRatePerTrack = _para.getMaxMissingDetectionRate();
        pruneTracks.minNumDetectionsPerTrack = _para.getMinDetectionPerTrack();

        return tracker;
    }

    static List<TkDetection> findDetectionsInRange( int startTimeIdxInclusive, int endTimeIdxInclusive ) {
        CtSolutions solution = (CtSolutions) CtObjectDirectory.get( "solution" );

        // Compile a list of detections that are within the time index range
        List<TkDetection> tkDetections = new ArrayList<TkDetection>();

        boolean checkTime = true;

        if(    ( startTimeIdxInclusive < 0 )
            && (   endTimeIdxInclusive < 0 ) ) {
            checkTime = false;
        }

        // for each detection in solution
        for( CtDetections d : (Set<CtDetections>) solution.getCtDetectionses() ) {

            // for each coordinate of detection
            for( CtImagesCoordinates ic : (Set<CtImagesCoordinates>) d.getCtImages().getCtImagesCoordinateses() ) {

                CtCoordinates c = ic.getCtCoordinates();
                CtCoordinatesTypes ct = c.getCtCoordinatesTypes();

                // find the time coordinate
                if( !ct.getName().equals( "time" ) ) {
                    continue;
                }

                // make sure it's in range
                Integer time = c.getValue();
                System.out.println("time: " + (time) );

                if( checkTime ) {
                    if(    time < startTimeIdxInclusive
                        || time > endTimeIdxInclusive ) {
                        continue;
                    }
                }
                
                CtZoomPolygon zp = new CtZoomPolygon( d.getBoundary() );
                Point2D centre = zp.getCenter();
                Rectangle2D bb = zp.getBoundingBox();

                TkCell cell = new TkCell();
                cell.cx = centre.getX();
                cell.cy = centre.getY();
                cell.radius = Math.min( bb.getWidth(), bb.getHeight() ) / 2;

                TkDetection tkDetection = new TkDetection();
                tkDetection.timeIdx = time;
                tkDetection.add( TkCell.name, cell );
                tkDetection.add( DETECTION_PK, d.getPkDetection() );

                tkDetections.add( tkDetection );
            }
        }
        
        return tkDetections;
    }

//    static TkDetectionsSeries loadDetections() {
//        return loadDetections( 0, Integer.MAX_VALUE );
//    }

    TkDetectionsSeries loadDetections( int startTimeIdxInclusive, int endTimeIdxInclusive ) {

        if(    ( startTimeIdxInclusive > 0 )
            && (   endTimeIdxInclusive > 0 ) ) { // if valid params selected
            if( startTimeIdxInclusive >= endTimeIdxInclusive  ) { // check ordering
                throw new IllegalArgumentException("failed: startTimeIdxInclusive < endTimeIdxInclusive");
            }
        }
        
        Session session = CtSession.Current();
        session.beginTransaction();

        List<TkDetection> allDetections = findDetectionsInRange( startTimeIdxInclusive, endTimeIdxInclusive );

        // filter out detections not in the uwell
        CtRegion roi = getMicroWellRoi( microWellName );
        if( roi != null ) {
            filterDetectionsByRoi(allDetections, roi);
        }


//        String hql =
//                "SELECT ctDetections"
//              + " FROM CtDetections as ctDetections";
//
//        Query q = session.createQuery( hql );
//        List<Object[]> results = (List<Object[]>) q.list();
//
//        for( Object o : results ) {
//            CtDetections ctDetection = (CtDetections) o;
//            Set s = ctDetection.getCtImages().getCtImagesCoordinateses();
//            Iterator i = s.iterator();
//            while( i.hasNext() ) {
//                CtImagesCoordinates ic = (CtImagesCoordinates)i.next();
//                CtCoordinates c = ic.getCtCoordinates();
//                CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
//                if( !ct.getName().equals( "time" ) ) {
//                    continue;
//                }
//
//                Integer time = c.getValue();
//                System.out.println("time: " + (time) );
//
//                if(    time < startTimeIdxInclusive
//                    || time > endTimeIdxInclusive ) {
//                    continue;
//                }
//
//                CtZoomPolygon zp = new CtZoomPolygon( ctDetection.getBoundary() );
//                Point2D centre = zp.getCenter();
//
//                TkCell cell = new TkCell();
//                cell.cx = centre.getX();
//                cell.cy = centre.getY();
//
//                TkDetection tkDetection = new TkDetection();
//                tkDetection.timeIdx = time;
//                tkDetection.add( TkCell.name, cell );
//                tkDetection.add( DETECTION_PK, ctDetection.getPkDetection() );
//
//                allDetections.add( tkDetection );
//            }
//        }

        // sort according to time
        Collections.sort( allDetections, TkDetection.timeIdxGreaterThan );

        // print detections
        for( TkDetection d: allDetections ) {
            System.out.print( " " + d.timeIdx );
        }

        // print detections
        TkDetectionsSeries series = new TkDetectionsSeries();

        // break detections into blocks with the same time idx
        int timeIdx = allDetections.get(0).timeIdx;

        TkDetections detections = new TkDetections();
        series.add( detections );

        for( TkDetection d: allDetections ) {
            while( timeIdx != d.timeIdx ) {
                ++timeIdx;
                // create new detections
                detections = new TkDetections();
                series.add( detections );
            }

            detections.add( d );
        }

        // print the final results
        for( TkDetections ds: series ) {
            System.out.println( "----" );
            for( TkDetection d: ds ) {
                System.out.print( " " + d.timeIdx );
            }
            System.out.println( "" );
        }

        session.getTransaction().commit();

        return series;
    }

    TkTracks runTracker( TkLJIPDATracker tracker, TkDetectionsSeries series ) {
        TkTracks tracks = new TkTracks();

        for( int step = 0; step < series.size(); ++step ) {

            TkDetections dets = series.get(step);

            if( dets.isEmpty() ) {
                System.out.println( "NO DETECTIONS @ step: "+step );
                continue;
            }
            
            System.out.println( "time idx: " + dets.get(0).timeIdx + " step: "+step );
            System.out.println( "num detections: " + dets.size() );

            tracker.predict( tracks );
            System.out.println( "predict done---------------------------------------------" );

            int numTracksBeforeUpdate = tracks.size();

            tracker.update(tracks, dets);
            System.out.println( "update done---------------------------------------------" );

            // Finalise tracks with consecutive non-detections
            for( TkTrack t : tracks ) {
                if( t.elements.size() < _para.maxConsecMissingDetections ) {
                    continue;
                }
                boolean allMissing = true;
                for( int i = t.elements.size() - _para.maxConsecMissingDetections; i < t.elements.size(); ++i ) {
                    if( t.elements.get(i).det != null ) {
                        allMissing = false;
                        break;
                    }
                }
                if( allMissing ) {
                    t.setState(TkTrack.Status.TERMINATED);
                }
            }


            // Print tracks
            for( int t = 0; t < tracks.size(); ++t ) {
                TkTrack track = tracks.get(t);
                TkLJIPDAState stateLJIPDA = track.getLast().find( tracker.getStateKey() );
                System.out.print("|  ");
                System.out.print( String.format("Track: %02d  ", (t+1) ) );
                System.out.print( String.format("state: %4.3f %4.3f  ", stateLJIPDA.post.state.getAsDouble(0,0), stateLJIPDA.post.state.getAsDouble(1,0) ) );
                System.out.print( String.format("cov: %4.3f %4.3f  ", stateLJIPDA.post.stateCov.getAsDouble(0,0), stateLJIPDA.post.stateCov.getAsDouble(1,0) ) );
                System.out.print( String.format("exist: %1.5f  ", stateLJIPDA.existencePost ) );
            }

        }

        return tracks;
    }

    // pass in session to keep transaction atomic
    static void dropAllExistingTracks( Session session ) {
        // Clear the current solution of all tracks
        String hql = "SELECT ctTracksDetections"
                   + " FROM CtTracksDetections as ctTracksDetections"
                   + " JOIN ctTracksDetections.ctTracks ctTracks"
                   + " WHERE ctTracks.ctSolutions = :solutionPk";

        Query q = session.createQuery( hql );
        q.setInteger( "solutionPk", CtSolutionController.getSolutions().getPkSolution() );
        List<CtTracksDetections> results = q.list();

        for( CtTracksDetections td : results ) {
            session.delete( td );
        }

        hql = "SELECT ctTracks"
            + " FROM CtTracks as ctTracks"
            + " WHERE ctTracks.ctSolutions = :solutionPk";

        q = session.createQuery( hql );
        q.setInteger( "solutionPk", CtSolutionController.getSolutions().getPkSolution() );
        List<CtTracks> results2 = q.list();

        for( CtTracks t : results2 ) {
            session.delete( t );
        }

    }


    // pass in session to keep transaction atomic
    static void dropExistingTracks( Session session, TkTracks tracks, int startTimeIdxInclusive, int endTimeIdxInclusive ) {

        CtSolutions solution = (CtSolutions) CtObjectDirectory.get( "solution" );

        // Drop all existing track detection associations
        int trackCnt = 0;
        for( TkTrack t: tracks ) {
            ++trackCnt;

            for( TkTrackElement e : t.elements ) {
                TkDetection d = e.det;
                if( d == null ) {
                    continue;
                }

                System.out.println( "d.timeIdx: " + (d.timeIdx) );
                System.out.println( "trackCnt: " + (trackCnt) );
                System.out.println( "total no. tracks: " + (trackCnt) );
                if(    d.timeIdx == startTimeIdxInclusive
                    || d.timeIdx == endTimeIdxInclusive) {
                    continue;
                }

                Integer detectionPk = d.find( DETECTION_PK );

                String hql =
                        "SELECT ctTracksDetections"
                      + " FROM CtTracksDetections as ctTracksDetections"
                      + " JOIN ctTracksDetections.ctDetections ctDetections"
                      + " WHERE ctDetections.pkDetection = :detectionPk";

                Query q = session.createQuery( hql );
                q.setInteger( "detectionPk", detectionPk );
                List<CtTracksDetections> results = q.list();

                HashSet< CtTracks > modified = new HashSet< CtTracks >();

                for( CtTracksDetections td: results ) {
                    CtTracks t_n = td.getCtTracks();
                    CtDetections d_n = td.getCtDetections();
                    CtSolutions s = t_n.getCtSolutions();

                    if( s.getPkSolution() == solution.getPkSolution() ) {
//                    if( td.getCtTracks().getCtSolutions().getPkSolution() == solution.getPkSolution() ) {
                        t_n.getCtTracksDetectionses().remove( td );
                        d_n.getCtTracksDetectionses().remove( td );
                        session.delete( td );
                        modified.add( t_n );
                    }
                }

                // now clean up tracks that have no surviving detections:
                for( CtTracks t_n : modified ) {
                    Set< CtTracksDetections > tds = t_n.getCtTracksDetectionses();

                    if( tds.isEmpty() ) {
                        CtSolutions s = t_n.getCtSolutions();
                        s.getCtTrackses().remove( t_n );
                        session.delete( t );
                        session.update( s );
                    }
                }

//                String hql =
//                      "DELETE from CtTracksDetections as ctTracksDetections"
//                    + " WHERE fk_detection = " + detectionPk;
//                System.out.println("hql: " + (hql) );
//
//                Query query = session.createQuery(hql);
//                int row = query.executeUpdate();
//                System.out.println("Rows deleted: " + row);
            }
        }
    }


    static CtTracks findExistingTrackAtStart( Session session, TkTrack t, int startTimeIdxInclusive ) {
//        session.flush();
        
        TkDetection firstDetection = t.elements.get(0).det;
        if( firstDetection == null ) {
            throw new Error("First detection of a track can not be null");
        }

        // join first detection to any existing tracks
        if( firstDetection.timeIdx != startTimeIdxInclusive ) {
            return null;
        }

        CtSolutions solution = (CtSolutions) CtObjectDirectory.get( "solution" );

        Integer detectionPk = firstDetection.find( DETECTION_PK );

        // retrieve the track that contains the detection
        String hql =
                " SELECT ctTD"
              + " FROM CtTracksDetections as ctTD"
              + " JOIN ctTD.ctDetections as ctD"
              + " WHERE ctD.pkDetection = :detectionPk";
        System.out.println("hql: " + (hql) );

//        session.flush();
        Query q = session.createQuery( hql );
        q.setInteger( "detectionPk", detectionPk );
        List<CtTracksDetections> results = q.list();

        // make sure there's only one track associated with this detection
        CtTracks track = null;
        for( CtTracksDetections td: results ) {
            if( td.getCtTracks().getCtSolutions().getPkSolution() == solution.getPkSolution() ) {
                if( track != null ) {
                    throw new Error("Detection associated with more than 1 track,"
                            + "this should not happen when all tracks-detections assoc"
                            + "have been cleaned between start and end time idx.");
                }

                track = td.getCtTracks(); // use existing one
            }
        }

        return track; // may be null if detection is not in a track
    }

    static boolean mergeTracksAtEnd( Session session, TkTrack t, int endTimeIdxInclusive, CtTracks tt ) {
//        session.flush();
        
        TkDetection lastDetection = t.getLast().det;

        if( lastDetection == null ) {
            return false; // no detection associated at track's end.
        }

        // join first detection to any existing tracks
        if( lastDetection.timeIdx != endTimeIdxInclusive ) {
            return false; // no detection on the boundary
        }

        Integer detectionPk = lastDetection.find( DETECTION_PK );

        // retrieve all tracks that contains the detection
        // note that this last detection may be a place for a fork.
        String hql =
                "SELECT ctTracksDetections"
              + " FROM CtTracksDetections as ctTracksDetections"
              + " JOIN ctTracksDetections.ctDetections ctDetections"
              + " WHERE ctDetections.pkDetection = :detectionPk";

        Query q = session.createQuery( hql );
        q.setInteger( "detectionPk", detectionPk );
        List results = q.list();

        if( results.isEmpty() ) {
            return false; // no existing tracks so no need to remap
        }

        if( results.size() > 1 ) {
            // this is a forking detection. When gets here:
            // - all tracks-detection assoc before this detection has been cleared
            // And since this is a fork, the track does not continue past this
            // detection. Since this detection will be associated with the new track
            // we don't have to remap anything.
            return false;
        }

        CtTracksDetections td = (CtTracksDetections) results.get(0);

        Set<CtTracksDetections> remap = td.getCtTracks().getCtTracksDetectionses();

        for( CtTracksDetections td2: remap ) {
            System.out.println( "Remapping: "
                    + td2.getCtTracks().getPkTrack() + " " + td2.getCtDetections().getPkDetection()
                    + " to: "
                    + tt.getPkTrack() + " " + td2.getCtDetections().getPkDetection()
                    );

            if( td2.getCtTracks().getPkTrack() == tt.getPkTrack() ) {
                continue; // no change
            }

            CtTracks tt0 = td2.getCtTracks();
            td2.setCtTracks(tt);
            tt0.getCtTracksDetectionses().remove( td2 );
            tt .getCtTracksDetectionses().add( td2 );
            session.update( td2 );
            session.update( tt );
            session.update( tt0 );
        }
        
//        session.flush();
        return true;
    }

    static void removeEmptyTracks( Session session ) {
        CtSolutions solution = (CtSolutions) CtObjectDirectory.get( "solution" );
        String hql =
                  "SELECT ctTracks"
                + " FROM CtTracks as ctTracks"
                + " JOIN ctTracks.ctSolutions as ctSolutions"
                + " WHERE ctSolutions.pkSolution = :solutionPk";

        Query q = session.createQuery( hql );
        q.setInteger( "solutionPk", solution.getPkSolution() );
        List<CtTracks> results = q.list();

        for( CtTracks t : results ) {
            Set<CtTracksDetections> s = t.getCtTracksDetectionses();
//            System.out.println("t.getPkTrack(): " + (t.getPkTrack()) );
//            System.out.println("t.getCtTracksDetectionses().size(): " + (t.getCtTracksDetectionses().size()) );
//            System.out.println("s.isEmpty(): " + (s.isEmpty()) );;

            if( s.isEmpty() ) {
                // empty track, delete it
                solution.getCtTrackses().remove( t );
                session.update( solution );
                session.delete( t );
            }
        }
    }


    static void saveTracks( TkTracks tracks, int startTimeIdxInclusive, int endTimeIdxInclusive ) {

        Session session = CtSession.Current();
        session.beginTransaction();

        dropAllExistingTracks( session );
//        dropExistingTracks( session, tracks, startTimeIdxInclusive, endTimeIdxInclusive );

//        session.flush();

        CtSolutions solution = (CtSolutions) CtObjectDirectory.get( "solution" );

        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
            TkTrack t = tracks.get( tIdx );

            // can we merge with existing track at the start
            CtTracks tt = findExistingTrackAtStart( session, t, startTimeIdxInclusive );
//            session.flush();

            boolean firstDetectionHasTrack = (tt != null);

            // create a new track if ones does not already exist
            if( tt == null ) {
                tt = new CtTracks();
                tt.setCtSolutions(solution);
                solution.getCtTrackses().add( tt );
                session.save( tt );
            }
//            session.flush();

            // re-associate tracks that joins at the end
            boolean mergedAtEnd = mergeTracksAtEnd( session, t, endTimeIdxInclusive, tt );
//            session.flush();

            System.out.println("tt.getPkTrack(): " + (tt.getPkTrack()) );


            // link up with parent tracks
            if( !t.isRoot() ) {
                // find parent track's last detection
                TkTrack parent = (TkTrack) t.getParent();

                TkDetection d = parent.getLast().det;
                if( d == null ) { // no detection for this time step
                    throw new Error("Parent track's last element can't be null detection" );
                }

                int detectionPK = d.find( DETECTION_PK );

                CtDetections dd = (CtDetections) CtSession.getObject( CtDetections.class, detectionPK );

                // associate detection with track
                CtTracksDetections ttdd = new CtTracksDetections();

                ttdd.setCtTracks(tt);
                ttdd.setCtDetections(dd);
                tt.getCtTracksDetectionses().add( ttdd );
                dd.getCtTracksDetectionses().add( ttdd );

                session.save(ttdd);
            }

            // save detections
            for( int dIdx = 0; dIdx < t.elements.size(); ++dIdx ) {
                // skip persistence of first detection if it is associated
                // with an existing track
                if(    firstDetectionHasTrack
                    && dIdx == 0 ) {
                    continue;
                }

                // skip persistence of last detection if it has been merged with
                // existing track
                if(    mergedAtEnd
                    && dIdx == t.elements.size() - 1 ) {
                   continue;
                }

                TkDetection d = t.elements.get(dIdx).det;
                if( d == null ) { // no detection for this time step
                    continue;
                }
                int detectionPK = d.find( DETECTION_PK );

                System.out.println("detectionPK: " + (detectionPK) );

                CtDetections dd = (CtDetections) CtSession.getObject( CtDetections.class, detectionPK );

                // associate detection with track
                CtTracksDetections ttdd = new CtTracksDetections();

                ttdd.setCtTracks(tt);
                ttdd.setCtDetections(dd);
                tt.getCtTracksDetectionses().add( ttdd );
                dd.getCtTracksDetectionses().add( ttdd );

                session.save(ttdd);
            }
//            session.flush();
        }

        //TODO why can we delete empty tracks?
//        session.flush();
//        session.getTransaction().commit();
//        removeEmptyTracks( session );

        session.flush();
        session.getTransaction().commit();
    }


//    public static void putTest() {
//        TkTracks tracks = newTracks();
//
//        Session session = CtSession.Current();
//        session.beginTransaction();
//
//        for( int tIdx = 0; tIdx < tracks.size(); ++tIdx ) {
//            TkTrack t = tracks.find(tIdx);
//
//            CtTracks tt = new CtTracks();
//            session.save(tt);
//
//            for( int dIdx = 0; dIdx < t.elements.size(); ++dIdx ) {
//                TkDetection d = t.elements.find(dIdx).det;
//                String desc = d.find(detectionDescriptionKey);
//
//                CtDetections dd = new CtDetections();
//                dd.setBoundary( desc ); // dummy
//
//                session.save(dd);
//
//                // associate detection with track
//                CtTracksDetections ttdd = new CtTracksDetections();
//
//                ttdd.setCtTracks(tt);
//                ttdd.setCtDetections(dd);
//
//                session.save(ttdd);
//            }
//
//        }
//
//        session.getTransaction().commit();
//
//    }

//    static TkTracks newTracks() {
//        TkTracks tracks = new TkTracks();
//
//        int detectionCnt = 0;
//
//        for( int i = 0; i < 1; ++i ) {
//            TkTrack t = new TkTrack();
//
//            for( int j = 0; j < 2; ++j, ++detectionCnt ) {
//                TkDetection d = new TkDetection();
//                d.add( detectionDescriptionKey, "Detection: " + detectionCnt );
//
//                TkTrackElement e = new TkTrackElement( d );
//                t.elements.add( e );
//            }
//
//            tracks.add( t );
//        }
//
//        return tracks;
//    }

    public static CtRegion getMicroWellRoi( String microWellName ) {
        CtMicrowellsController mc = (CtMicrowellsController) CtObjectDirectory.get( CtMicrowellsController.name() );
        CtMicrowellsModel mm = mc.getMicrowellsModel();
        CtMicrowell m = mm.find( microWellName );
        return m;
    }

    public static void filterDetectionsByRoi( List<TkDetection> detections, CtRegion roi ) {
        Rectangle2D r = roi.getBoundingBox();

        ArrayList<TkDetection> result = new ArrayList<TkDetection>();

        for( TkDetection d : detections ) {
            TkCell c = d.find(TkCell.name);
            if( r.contains( c.cx/CtSubPixelResolution.unitsPerNaturalPixel,
                            c.cy/CtSubPixelResolution.unitsPerNaturalPixel) ) {
                result.add(d);
            }
        }

        // NOTE: since we don't know that implementation of List the user might want
        // and we don't care what input List implementation we want either, it's best
        // to just use the implementation the user has provided in the input List.
        // Hence we're not returning a new List but modifying the original.
        detections.clear();
        detections.addAll(result);
    }

}











