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

package au.com.nicta.ct.solution.tracking.annotated;

import au.com.nicta.ct.db.entities.CtEntityPropertiesUtil;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.hibernate.Session;

/**
 * Builds tracks based on annotations.
 * @author davidjr
 */
public class CtAnnotatedTracker {

    public static void apply( String identityPropertyKey, String parentPropertyKey ) {

        CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );

        CtTrackingController tc = CtTrackingController.get();

        tc.deleteTracks( true );
//        tc.clear(); unnecessary

        CtPageFrame.showWaitCursor();

        buildAnnotatedTracks( s, identityPropertyKey, parentPropertyKey );

        CtPageFrame.showDefaultCursor();

        tc.refresh( true );
    }

//        public static List<CtEntityProperties> find(
//            CtSolutions ctSolutions,
//            Class entityName,
//            Integer entityPk,
//            String propertyName ) {

    private static void buildAnnotatedTracks( CtSolutions s, String identityPropertyKey, String parentPropertyKey ) {

        // build a pk-searchable container of detections:
        Set< CtDetections > cd = s.getCtDetectionses();

        if( cd.isEmpty() ) {
            return;
        }

        Session session = CtSession.Current();
//        session.beginTransaction();

        CtDetections pojo = cd.iterator().next();
        Class c = pojo.getClass();

//        String classname = pojo.getClass().getSimpleName();
//        String identityPrefix = identityPropertyKey + "-" + classname;
//        String   parentPrefix =   parentPropertyKey + "-" + classname;

        HashMap< Integer, CtDetections > hm = new HashMap< Integer, CtDetections >(); // need to search by PK

        for( CtDetections d : cd ) {
            hm.put( d.getPkDetection(), d );
        }

        // go through all these and put them with the relevant detections.
        // static HashMap< Integer, String > findEntityProperties( CtSolutions s, Class c, String key, HashMap< Integer, CtDetections > detections ) {
        HashMap< Integer, String > detectionsIdentities = findEntityProperties( s, c, identityPropertyKey, hm );
        HashMap< Integer, String > detectionsParents    = findEntityProperties( s, c,   parentPropertyKey, hm );

        // ok now tracking logic.
        // 1. for-each detection, add to or create a track with all the detections with common identities.
        // 2. for-each detection, add to or create a track with all the parents IDs.
        HashMap< String, CtTracks > identitiesTracks = new HashMap< String, CtTracks >();

        Set< Entry< Integer, CtDetections > > es = hm.entrySet();

        // this first loop builds the tracks, without overlaps.
        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< Integer, CtDetections > e = (Entry< Integer, CtDetections >)i.next();

            int pk = e.getKey();
            CtDetections d   = e.getValue();

            String identity = detectionsIdentities.get( pk );

            if( identity == null ) {
                continue; // detection was not identified, this is ok.
            }

            associate( s, d, identitiesTracks, identity, session );
        }

        // this second loop associates tracks with their parents.
        CtCoordinatesController cc = CtCoordinatesController.get();

        i = es.iterator();

        while( i.hasNext() ) {

            Entry< Integer, CtDetections > e = (Entry< Integer, CtDetections >)i.next();

            int pk = e.getKey();
            CtDetections d   = e.getValue();

            String identity = detectionsIdentities.get( pk );

            if( identity == null ) {
                continue; // detection was not identified, this is ok.
            }

            String parent = detectionsParents.get( pk );

            if( parent == null ) {
                continue; // detection is spontaneously generated, this is ok.
            }

            CtTracks tParent = identitiesTracks.get( parent );
            CtDetections dParent = findLastDetectionIn( tParent, cc );

            associate( s, dParent, identitiesTracks, identity, session ); // associate parent's last detection with this track.
        }

//        session.getTransaction().commit();
        session.flush();
    }
//    private static void buildAnnotatedTracks( CtSolutions s, String identityPropertyKey, String parentPropertyKey ) {
//
//        // build a pk-searchable container of detections:
//        Set< CtDetections > cd = s.getCtDetectionses();
//
//        if( cd.isEmpty() ) {
//            return;
//        }
//
//        Session session = CtSession.Current();
////        session.beginTransaction();
//
//        CtDetections pojo = cd.iterator().next();
//        String classname = pojo.getClass().getSimpleName();
//        String identityPrefix = identityPropertyKey + "-" + classname;
//        String   parentPrefix =   parentPropertyKey + "-" + classname;
//
//        HashMap< Integer, CtDetections > hm = new HashMap< Integer, CtDetections >(); // need to search by PK
//
//        for( CtDetections d : cd ) {
//            hm.put( d.getPkDetection(), d );
//        }
//
//        // go through all these and put them with the relevant detections.
//        HashMap< Integer, String > detectionsIdentities = findFilteredProperties( identityPrefix, hm );
//        HashMap< Integer, String > detectionsParents    = findFilteredProperties(   parentPrefix, hm );
//
//        // ok now tracking logic.
//        // 1. for-each detection, add to or create a track with all the detections with common identities.
//        // 2. for-each detection, add to or create a track with all the parents IDs.
//        HashMap< String, CtTracks > identitiesTracks = new HashMap< String, CtTracks >();
//
//        Set< Entry< Integer, CtDetections > > es = hm.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< Integer, CtDetections > e = (Entry< Integer, CtDetections >)i.next();
//
//            int pk = e.getKey();
//            CtDetections d   = e.getValue();
//
//            String identity = detectionsIdentities.get( pk );
//
//            if( identity == null ) {
//                continue; // detection was not identified, this is ok.
//            }
//
//            associate( s, d, identitiesTracks, identity, session );
//
//            String parent = detectionsParents.get( pk );
//
//            if( parent == null ) {
//                continue; // detection is spontaneously generated, this is ok.
//            }
//
//            associate( s, d, identitiesTracks, parent, session );
//        }
//
////        session.getTransaction().commit();
//        session.flush();
//    }

    static CtDetections findLastDetectionIn( CtTracks t, CtCoordinatesController cc ) {

        int maxIndex = 0;
        CtDetections d = null;

        Set< CtTracksDetections > s = t.getCtTracksDetectionses();
        
        for( CtTracksDetections td : s ) {
            CtDetections d_n = td.getCtDetections();
            CtImages i = d_n.getCtImages();
            
            int index = cc.getTimeOrdinate( i );

            if(    ( d == null )
                || ( index >= maxIndex ) ) {
                d = d_n;
                maxIndex = index;
            }
        }

        return d;
    }

    static void associate( CtSolutions s, CtDetections d, HashMap< String, CtTracks > identitiesTracks, String identity, Session session ) {

        CtTracks t = identitiesTracks.get( identity );

        if( t == null ) {
            t = new CtTracks();
            t.setCtSolutions( s );
            s.getCtTrackses().add( t );

            session.save( t );
            session.update( s );

            identitiesTracks.put( identity, t );
        }

        CtTracksDetections td = new CtTracksDetections();
        td.setCtTracks( t );
        td.setCtDetections( d );
        t.getCtTracksDetectionses().add( td );
        d.getCtTracksDetectionses().add( td );

        session.save( td );
        session.update( t );
        session.update( d );
    }

    static HashMap< Integer, String > findEntityProperties( CtSolutions s, Class c, String key, HashMap< Integer, CtDetections > detections ) {

        HashMap< Integer, String > filteredProperties = new HashMap< Integer, String >();

        List< CtEntityProperties > l = CtEntityPropertiesUtil.find( s, c, null, key ); // find all properties of pojos class c in solution s with key

        Iterator i = l.iterator();

        while( i.hasNext() ) {

            CtEntityProperties ep = (CtEntityProperties)i.next();

            Integer pk = ep.getEntityPk();

            CtDetections d = detections.get( pk );

            if( d == null ) { // filter by list of detections in this solution
                continue; // error? No.  Not in this solution perhaps.
            }

            String value = ep.getValue();

            filteredProperties.put( pk, value );
        }

        return filteredProperties;
    }


//    static HashMap< Integer, String > findFilteredProperties( String prefix, HashMap< Integer, CtDetections > detections ) {
//
//        HashMap< Integer, String > filteredProperties = new HashMap< Integer, String >();
//
//        int length = prefix.length();
//
//        List l = CtKeyValueProperties.findAllWithPrefix( prefix ); // nb these lists are for ALL detections in ALL solutions!
//
//        Iterator i = l.iterator();
//
//        while( i.hasNext() ) {
//
//            CtProperties p = (CtProperties)i.next();
//
//            String suffix = p.getName().substring( length+1 ); // +1 for the '-'
//
//            Integer pk = Integer.valueOf( suffix );
//
//            CtDetections d = detections.get( pk );
//
//            if( d == null ) { // filter by list of detections in this solution
//                continue; // error? No.  Not in this solution perhaps.
//            }
//
//            String value = p.getValue();
//
//            filteredProperties.put( pk, value );
//        }
//
//        return filteredProperties;
//    }
}
