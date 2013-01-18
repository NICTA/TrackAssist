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

package au.com.nicta.ct.solution.tracking;

import au.com.nicta.ct.db.entities.CtEntityPropertiesUtil;
import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.db.hibernate.CtProperties;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Alan
 */
public class CtSolutionManager {

    public static void rename( CtSolutions s, String name ) {
        Session session = CtSession.Current();
        session.beginTransaction();
        s.setName( name );
        session.save( s );
        session.flush();
        session.getTransaction().commit();
    }

    //! Returns the PK of the new solution
    public static int copy( int oldSolutionPk, String newSolutionName ) {
        CtSolutionManager p = new CtSolutionManager();
        return p.copyImpl( oldSolutionPk, newSolutionName );
    }

    //! Returns the PK of the new solution
    public static void delete( int solutionPk ) {
        CtSolutionManager p = new CtSolutionManager();
        p.deleteImpl( solutionPk );
    }

    Session session;
    CtSolutions oldSolution;
    CtSolutions newSolution;

    protected void deleteImpl( int solutionPk ) {
        session = CtSession.Current();
        session.beginTransaction();

        // delete properties first because they are easier to find this way
        deleteProperties( solutionPk);
        deleteEntityProperties( solutionPk );

        // delete detections, tracks etc.
        deleteFromTables( solutionPk );

        deleteAnnotations(solutionPk);

        session.flush();
        session.getTransaction().commit();
    }

    protected void deleteFromTables( int solutionPk ) {
        // delete from CtTracksDetections
        Query q = session.createQuery(
                " SELECT ctTD"
              + " FROM CtTracksDetections as ctTD"
              + " JOIN ctTD.ctTracks as ctT"
              + " WHERE ctT.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteRows( q.list() );

        // delete from CtTracks
        q = session.createQuery(
                " SELECT ctT"
              + " FROM CtTracks as ctT"
              + " WHERE ctT.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteRows( q.list() );

        // delete from CtDetections
        q = session.createQuery(
                " SELECT ctD"
              + " FROM CtDetections as ctD"
              + " WHERE ctD.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteRows( q.list() );

        // delete from CtSolutions
        session.delete( session.load( CtSolutions.class, solutionPk ) );
    }

    protected void deleteRows( List rows ) {
        for( Object o : rows ) {
            session.delete( o );
        }
    }

    /**
     * TODO: rewrite so that any property without a valid fk in other tables are
     * removed.
     * @param solutionPk
     */
    protected void deleteProperties(int solutionPk) {
        // delete properties of CtDetections
        Query q = session.createQuery(
                " SELECT ctD.pkDetection"
              + " FROM CtDetections as ctD"
              + " WHERE ctD.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteProperties( CtDetections.class, q.list() );

        // delete properties of CtTracks
        q = session.createQuery(
                " SELECT ctT.pkTrack"
              + " FROM CtTracks as ctT"
              + " WHERE ctT.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteProperties( CtTracks.class, q.list() );

        // delete properties of CtTracksDetections
        q = session.createQuery(
                " SELECT ctTD.pkTrackDetection"
              + " FROM CtTracksDetections as ctTD"
              + " JOIN ctTD.ctTracks as ctT"
              + " WHERE ctT.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        deleteProperties( CtTracksDetections.class, q.list() );

        // delete entity properties
        q = session.createQuery(
                " DELETE "
              + " FROM CtEntityProperties as ctEP"
              + " WHERE ctEP.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );
        q.executeUpdate();
    }


    /**
     * Delete all properties associated with the rows of the
     *
     * @param tableCls
     * @param rows
     */
    protected void deleteProperties( Class tableCls, List<Integer> pkList ) {
        // get all properties
        Query q = session.createQuery( "FROM CtProperties ctProperties" );
        List<CtProperties> allProperties = (List<CtProperties>) q.list();

        for( int pk : pkList ) {
            String keySuffix = CtKeyValueProperties.getSuffixOfClass( tableCls, pk );
//            findProperties(allProperties, keySuffix);
//            Query q = CtKeyValueProperties.queryLike( "%" + keySuffix, session );

            for( CtProperties p : findProperties(allProperties, keySuffix) ) {
//                System.out.println( "Name: " + p.getName() + "  Value: " + p.getValue() );

                // delete it
                session.delete( p );
            }
        }
    }

    protected void deleteEntityProperties(int solutionk) {
        List<CtEntityProperties> l = CtEntityPropertiesUtil.find(session, solutionk, null, null, null);
        for( CtEntityProperties ep : l ) {
            session.delete( ep );
        }
    }


    protected int copyImpl( int oldSolutionPk, String newSolutionName ) {
        session = CtSession.Current();
        session.beginTransaction();

        Map<Integer, CtDetections> detectionsMap             = new HashMap<Integer, CtDetections>();
        Map<Integer, CtTracks> tracksMap                     = new HashMap<Integer, CtTracks>();
        Map<Integer, CtTracksDetections> tracksDetectionsMap = new HashMap<Integer, CtTracksDetections>();
        Map<Integer, Integer> propertiesMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> entityPropertiesMap = new HashMap<Integer, Integer>();

        newSolution( oldSolutionPk, newSolutionName );
        copyDetections( detectionsMap );
        copyTracks( tracksMap );
        copyTracksDetections( tracksMap, detectionsMap, tracksDetectionsMap );

        copyProperties      ( tracksMap, detectionsMap, tracksDetectionsMap,       propertiesMap ); // this code starts another
        copyEntityProperties( tracksMap, detectionsMap, tracksDetectionsMap, entityPropertiesMap ); // this code starts another

        copyAnnotations();

        session.flush();
        session.getTransaction().commit();

        // Must do refresh after all fk's have been saved to the DB.
        refresh( detectionsMap.values() );
        refresh( tracksMap.values() );
        refresh( tracksDetectionsMap.values() );
        session.refresh(newSolution);

        return newSolution.getPkSolution();
    }

    protected void refresh( Collection<?> objects ) {
        for( Object o : objects ) {
            session.refresh(o);
        }
    }


    protected void newSolution( int pkSolutionOld, String newSolutionName ) {
        oldSolution = (CtSolutions) session.load( CtSolutions.class, new Integer(pkSolutionOld) );
        newSolution = new CtSolutions(0, oldSolution.getCtExperiments(), newSolutionName, null, null);
        session.save(newSolution);
    }


    protected void copyDetections( Map<Integer, CtDetections> detectionsMap ) {

        for( CtDetections d : (Set<CtDetections>) oldSolution.getCtDetectionses() ) {
//            public CtDetections(int pkDetection, CtSolutions ctSolutions, CtImages ctImages, String location, String boundary, Set ctTracksDetectionses) {
            CtDetections d2 = new CtDetections( 0, newSolution, d.getCtImages(), d.getLocation(), d.getBoundary(), null );
            session.save( d2 );
            detectionsMap.put( d.getPkDetection(), d2 );
        }
    }

    protected void copyTracks( Map<Integer, CtTracks> tracksMap ) {

        for( CtTracks t : (Set<CtTracks>) oldSolution.getCtTrackses() ) {
//            public CtTracks(int pkTrack, CtSolutions ctSolutions, Set ctTracksDetectionses) {
            CtTracks t2 = new CtTracks( 0, newSolution, null );
            session.save( t2 );
            int oldPk = t .getPkTrack();
            int newPk = t2.getPkTrack();
            tracksMap.put( oldPk, t2 );
            assert oldPk != newPk;
        }
    }

    protected void copyTracksDetections(
            Map<Integer, CtTracks> tracksMap,
            Map<Integer, CtDetections> detectionsMap,
            Map<Integer, CtTracksDetections> tracksDetectionsMap ) {

        Query q = session.createQuery(
                " SELECT ctTD"
              + " FROM CtTracksDetections as ctTD"
              + " JOIN ctTD.ctTracks as ctT"
              + " WHERE ctT.ctSolutions = :solutionPk" );

        q.setInteger( "solutionPk", oldSolution.getPkSolution() );

        for( CtTracksDetections td : (List<CtTracksDetections>) q.list() ) {
            CtTracksDetections td2 = new CtTracksDetections();

            CtTracks     newT = tracksMap    .get( td.getCtTracks()    .getPkTrack() );
            CtDetections newD = detectionsMap.get( td.getCtDetections().getPkDetection() );
            td2.setCtTracks    ( newT );
            td2.setCtDetections( newD );
            session.save( td2 );
            int oldPk = td .getPkTrackDetection();
            int newPk = td2.getPkTrackDetection();
            tracksDetectionsMap.put( oldPk, td2 );
            assert oldPk != newPk;
//            session.evict(t);
//            session.evict(d);
//            session.get(CtTracks    .class, newPkT);
//            session.get(CtDetections.class, newPkD);
        }
    }
    
    protected void copyAnnotations() {
        Query q = session.createQuery(
                " SELECT ctA"
              + " FROM CtAnnotations as ctA"
              + " WHERE ctA.ctSolutions = :solutionPk" );

        q.setInteger( "solutionPk", oldSolution.getPkSolution() );

        for( CtAnnotations a : (List<CtAnnotations>) q.list() ) {
            CtAnnotations a2 = new CtAnnotations(
                    0,
                    a.getCtAnnotationsTypes(),
                    a.getCtImages(),
                    newSolution,
                    a.getValue(),
                    a.getX(),
                    a.getY() );
            session.saveOrUpdate( a2 );
        }
    }

    protected void deleteAnnotations(int solutionPk) {
        Query q = session.createQuery(
                " SELECT ctA"
              + " FROM CtAnnotations as ctA"
              + " WHERE ctA.ctSolutions = :solutionPk" );
        q.setInteger( "solutionPk", solutionPk );

        for( CtAnnotations a : (List<CtAnnotations>) q.list() ) {
            session.delete( a );
        }
    }

    protected void copyProperties(
            Map<Integer, CtTracks> tracksMap,
            Map<Integer, CtDetections> detectionsMap,
            Map<Integer, CtTracksDetections> tracksDetectionsMap,
            Map<Integer, Integer> propertiesMap ) {
        
        copyProperties( detectionsMap, CtDetections.class, propertiesMap );
        copyProperties( tracksMap, CtTracks.class, propertiesMap );
        copyProperties( tracksDetectionsMap, CtTracksDetections.class, propertiesMap );
    }

    protected List<CtProperties> findProperties(List<CtProperties> all, String subString) {
        List<CtProperties> l = new ArrayList<CtProperties>();

        for( CtProperties p : all ) {
            if( p.getName().contains(subString) ) {
                l.add(p);
            }
        }

        return l;
    }



    protected void copyProperties(
            Map<Integer, ?> oldToNewMap,
            Class tableCls,
            Map<Integer, Integer> propertiesMap ) {

        // get all properties
        Query q = session.createQuery( "FROM CtProperties ctProperties" );
        List<CtProperties> allProperties = (List<CtProperties>) q.list();

        for( Map.Entry<Integer, ?> e : oldToNewMap.entrySet() ) {
            Integer oldObjPk = e.getKey();
            Integer newObjPk = getPk(e.getValue());

//            System.out.println( "A: " + oldObjPk );

            String keySuffix = CtKeyValueProperties.getSuffixOfClass( tableCls, oldObjPk );
            
//            Query q = CtKeyValueProperties.queryLike( "%" + keySuffix, session );
//            List<CtProperties> l = (List<CtProperties>) q.list();

            for( CtProperties p : findProperties(allProperties, keySuffix) ) {
//                System.out.println( "Name: " + p.getName() + "  Value: " + p.getValue() );

                // replace the PK part of the property name
                String name =   CtKeyValueProperties.getNameToken ( p.getName() )
                              + "-"
                              + CtKeyValueProperties.getClassToken( p.getName() )
                              + "-"
                              + newObjPk;
//                System.out.println( "New name: " + name );

                CtProperties p2 = new CtProperties();
                p2.setCtPropertiesTypes( p.getCtPropertiesTypes() );
                p2.setName( name );
                p2.setValue( p.getValue() );
               
                session.save( p2 );
                int oldPk = p.getPkProperty();
                int newPk = p2.getPkProperty();
                propertiesMap.put(oldPk, newPk );
                assert oldPk != newPk;
//                session.evict( p2 );
            }
        }
    }

    protected void copyEntityProperties(
            Map<Integer, CtTracks> tracksMap,
            Map<Integer, CtDetections> detectionsMap,
            Map<Integer, CtTracksDetections> tracksDetectionsMap,
            Map<Integer, Integer> enitityPropertiesMap ) {

        copyEntityProperties( detectionsMap,       CtDetections.class,       enitityPropertiesMap );
        copyEntityProperties( tracksMap,           CtTracks.class,           enitityPropertiesMap );
        copyEntityProperties( tracksDetectionsMap, CtTracksDetections.class, enitityPropertiesMap );
    }

    protected List<CtEntityProperties> findEntityProperties(
            List<CtEntityProperties> all,
            Integer entityPk ) {

        List<CtEntityProperties> l = new ArrayList<CtEntityProperties>();

        for( CtEntityProperties ep : all ) {
            if( ep.getEntityPk().equals(entityPk) ) {
                l.add(ep);
            }
        }

        return l;
    }

    protected void copyEntityProperties(
            Map<Integer, ?> oldToNewMap,
            Class tableCls,
            Map<Integer, Integer> entityPropertiesMap ) {

        // get all properties
        Query q = session.createQuery(
                "FROM CtEntityProperties ctEntityProperties"
                + " WHERE ctEntityProperties.ctSolutions = :pkSolution"
                + " AND ctEntityProperties.entityName = :entityName" );

        q.setInteger( "pkSolution", oldSolution.getPkSolution() );
        q.setString( "entityName", CtEntityPropertiesUtil.getClassName(tableCls) );
        List<CtEntityProperties> allEntityProperties = (List<CtEntityProperties>) q.list();

        for( Map.Entry<Integer, ?> e : oldToNewMap.entrySet() ) {

            Integer oldObjPk = e.getKey();
            Integer newObjPk = getPk(e.getValue());

//            System.out.println("oldPk: " + (oldObjPk) );

//            List<CtEntityProperties> l = CtEntityPropertiesUtil.find(session, oldSolution, tableCls, oldObjPk, null);
            for( CtEntityProperties ep : findEntityProperties(allEntityProperties, oldObjPk) ) {
//                System.out.println( "Name: " + ep.getName() + "  Value: " + ep.getValue() );

                CtEntityProperties ep2 = new CtEntityProperties();
                ep2.setCtSolutions(newSolution);
                ep2.setEntityName(ep.getEntityName());
                ep2.setEntityPk(newObjPk);
                ep2.setName(ep.getName());
                ep2.setValue(ep.getValue());

                session.save( ep2 );
                int oldPk = ep .getPkEntityProperty();
                int newPk = ep2.getPkEntityProperty();
                entityPropertiesMap.put( oldPk, newPk );
                assert oldPk != newPk;
//                session.evict( ep2 );
            }
        }
   }

    protected int getPk(Object o) {
        if( o.getClass() == CtDetections.class ) {
            return ( (CtDetections)o ).getPkDetection();
        }
        else if( o.getClass() == CtTracks.class ) {
            return ( (CtTracks)o ).getPkTrack();
        }
        else if( o.getClass() == CtTracksDetections.class ) {
            return ( (CtTracksDetections)o ).getPkTrackDetection();
        }
        else {
            throw new IllegalArgumentException("Unknow object");
        }
    }

}













