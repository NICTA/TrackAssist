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

package au.com.nicta.ct.solution.lineage;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtLineageModel extends CtChangeModel {

    CtImageSequenceModel _ism;
    CtCoordinatesController _cc;
    CtSolutions _s;

    int _rowCount = 0;

    HashMap< CtDetections, Integer > _detectionsCols = new HashMap< CtDetections, Integer >();    
    HashMap< CtTracks, Integer > _tracksRows = new HashMap< CtTracks, Integer >();
    HashSet< CtTracks > _rootTracks = new HashSet< CtTracks >();
    HashMap< CtTracks, CtTracks > _tracksParents = new HashMap< CtTracks, CtTracks >();
    HashMap< CtTracks, String > _tracksIdentities = new HashMap< CtTracks, String >();

    public CtLineageModel() {
        super( null );
    }

    public void setImageSequenceModel( CtImageSequenceModel ism ) {
        this._ism = ism;
    }
//    public void setCoordinatesController( CtCoordinatesController cc ) {
//        this._cc = cc;
//    }
//    public void refresh( CtCoordinatesController cc, CtImageSequenceModel ism ) {
//        this._ism = ism;
//        this._cc = cc;
//    }

    public CtSolutions getSolution() {
        return _s;
    }

    public String getIdentity( CtTracks t ) {
        return _tracksIdentities.get( t );
    }
    
    public boolean isRoot( CtTracks t ) {
        if( _rootTracks.contains( t ) ) {
            return true;
        }

        return false;
    }

    public CtTracks getParent( CtTracks t ) {
        return _tracksParents.get( t );
    }

    public int getX( CtDetections d ) {
//        if( _ism == null ) {
//            return 0;
//        }
//
//        CtImages i = d.getCtImages();
//        int index = _ism.index( i );
//        return index;
        Integer x = _detectionsCols.get( d );
        if( x == null ) {
            return 0;
        }
        return x;
//        try {
//            return _detectionsCols.get( d );
//        }
//        catch( Exception e ) {
//            int g = 0;
//            return -2;
//        }
    }

    public int getY( CtTracks t ) {
        Integer n = _tracksRows.get( t );

        if( n == null ) {
            return 0;
        }

        return n.intValue();
    }

    public CtTracks getTrack( int y ) {
        Set< Entry< CtTracks, Integer > > es = _tracksRows.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {
            Entry< CtTracks, Integer > e = (Entry< CtTracks, Integer >)i.next();

            if( e.getValue() == y ) {
                return e.getKey();
            }
        }

        return null;
    }

//    public int getIndex( int x ) {
//        return x;
//    }

    public int getWidth() {
        if( _ism == null ) {
            return 1;
        }
        return _ism.size();
    }

    public int getHeight() {
        return _rowCount;
    }

    static ArrayList< CtAbstractPair< CtTracks, CtTracks > > findIntersectingTracks( CtSolutions s ) {

// working sql:
//with tracks( pk_track, fk_detection ) AS
//(
// select t1.pk_track, td1.fk_detection from ct_tracks t1
// inner join ct_tracks_detections td1 on td1.fk_track = t1.pk_track
// where t1.fk_solution = 11
//)
//select distinct t1.pk_track, t2.pk_track
//from tracks t1, tracks t2
//where ( t2.pk_track <> t1.pk_track )
//and ( t2.fk_detection = t1.fk_detection )
//order by t1.pk_track, t2.pk_track
        int pkSolution = s.getPkSolution();

//        String hql = " with tracks1( pkTrack, ctDetection ) as "
//                   + "( "
//                   + " select t1.pkTrack, td1.ctDetection from ctTracks t1 "
//                   + " inner join t1.ctTracksDetectionses td1 "
//                   + " inner join t1.ctSolutions s "
//                   + " where s.pkSolution = " + pkSolution + " "
//                   + ") "
//                   + "select x1.pkTrack, x2.pkTrack "
//                   + " from tracks1 x1, tracks1 x2 "// inner join tracks1 x2 on x1.ctDetection = x2.ctDetection "
//                   + "where ( x2.pkTrack <> x1.pkTrack ) "
////                   + "and ( t2.fk_detection = t1.fk_detection ) "
//                   + "order by x1.pkTrack, x2.pkTrack ";

        String sql = " select t1.pk_track as dave, t2.pk_track as matt"
                   + " from ct_tracks t1 "
                   + " inner join ct_tracks_detections td1 on td1.fk_track = t1.pk_track "
                   + " inner join ( "
                   + " ct_tracks t2 "
                   + " inner join ct_tracks_detections td2 on td2.fk_track = t2.pk_track "
                   + " ) on td1.fk_detection = td2.fk_detection "
                   + " where t1.fk_solution = "+pkSolution+" "
                   + " and t2.fk_solution = "+pkSolution+" "
                   + " and t2.pk_track <> t1.pk_track "
                   + "order by t1.pk_track, t2.pk_track ";

/*        String sql = " with tracks1( pk_track, fk_detection ) as "
                   + "( "
                   + " select t1.pk_track, td1.fk_detection from ct_tracks t1 "
                   + " inner join ct_tracks_detections td1 on td1.fk_track = t1.pk_track "
                   + " where t1.fk_solution = "+pkSolution+" "
                   + "),"
                   + " tracks2( pk_track, fk_detection ) as "
                   + "( "
                   + " select t1.pk_track, td1.fk_detection from ct_tracks t1 "
                   + " inner join ct_tracks_detections td1 on td1.fk_track = t1.pk_track "
                   + " where t1.fk_solution = "+pkSolution+" "
                   + ") "
                   + "select t1.pk_track, t2.pk_track "
//                   + "from tracks t1, tracks t2 "
                   + " from tracks1 t1 inner join tracks2 t2 on t1.fk_detection = t2.fk_detection "
                   + "where ( t2.pk_track <> t1.pk_track ) "
//                   + "and ( t2.fk_detection = t1.fk_detection ) "
                   + "order by t1.pk_track, t2.pk_track ";*/
//        String hql = "select t1.pkTrack,t2.pkTrack from CtTracks t1 "
//                + " inner join t1.ctTracksDetectionses td1 "
//                + " inner join t1.ctSolutions s "
//                + " where s.pkSolution = '" + s.getPkSolution() + "'"
//                + " and exists( "
//                + " select t2.pkTrack from CtTracks t2 "
//                + " inner join t2.ctTracksDetectionses td2 "
//                + " inner join t2.ctSolutions s "
//                + " where s.pkSolution = '" + s.getPkSolution() + "'"
//                + " and t2.pkTrack <> t1.pkTrack "
//                + " and td2.ctDetections = td1.ctDetections "
//                + " ) ";

        Session session = CtSession.Current();
        session.beginTransaction();
//        Query q = session.createQuery( hql );
        SQLQuery q = session.createSQLQuery( sql );
        List results = q.list();
        session.getTransaction().commit();

        ArrayList< CtAbstractPair< CtTracks, CtTracks > > al = new ArrayList< CtAbstractPair< CtTracks, CtTracks > >();

        Iterator i = results.iterator();

        while( i.hasNext() ) {
            Object[] os = (Object[])i.next();
            Integer n1 = (Integer)os[ 0 ];
            Integer n2 = (Integer)os[ 1 ];
//System.out.println( "n1="+n1+" n2="+n2);
            CtTracks t1 = (CtTracks)CtSession.getObject( CtTracks.class, n1 );
            CtTracks t2 = (CtTracks)CtSession.getObject( CtTracks.class, n2 );
            CtAbstractPair< CtTracks, CtTracks > ap = new CtAbstractPair< CtTracks, CtTracks >( t1, t2 );
            al.add( ap );
        }

        return al;
    }

    public void clear() {
        this._s = null;
        this._ism = null;
        this._cc = null;
        _detectionsCols.clear();
        _tracksRows.clear();
        _rootTracks.clear();
        _tracksParents.clear();
        _tracksIdentities.clear();

//        fireModelChanged();
    }

    public HashMap< CtTracks, Integer > refresh1() {
        Set< CtTracks > ct = _s.getCtTrackses(); // dammit need to remove this too..

        HashMap< CtTracks, Integer > tracksMinIndices = new HashMap< CtTracks, Integer >();

        int minIndex = _ism.getMinIndex();

        for( CtTracks t : ct ) {

            int trackMinIndex = _ism.getMaxIndex();//_ism.size();

            Set< CtTracksDetections > tds = t.getCtTracksDetectionses();

            for( CtTracksDetections td : tds ) {
                CtDetections d = td.getCtDetections();
                CtImages i = d.getCtImages();

                try {
                    int index = _cc.getTimeOrdinate( i );//_ism.index( i );

                    int detectionIndex = index - minIndex; // make zero based

                    if( detectionIndex < trackMinIndex ) {
                        trackMinIndex = detectionIndex;
                    }

                    _detectionsCols.put( d, detectionIndex );
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }

            tracksMinIndices.put( t, trackMinIndex );

            _rootTracks.add( t ); // initially, all tracks. Then we prune.
        }

        return tracksMinIndices;
    }

    public HashMap< CtTracks, HashSet< CtTracks > > refresh2( HashMap< CtTracks, Integer > tracksMinIndices ) {
        HashMap< CtTracks, HashSet< CtTracks > > tracksRelatives = new HashMap< CtTracks, HashSet< CtTracks > >();

        ArrayList< CtAbstractPair< CtTracks, CtTracks > > al = findIntersectingTracks( _s );

        for( CtAbstractPair< CtTracks, CtTracks > ap : al ) {
            CtTracks t1 = ap._first;
            CtTracks t2 = ap._second;
//System.out.println( "i: t1="+t1.getPkTrack()+" t2="+t2.getPkTrack() );
            // ok track overlap (rare event): now work out which track is the "child"? do I care?
            HashSet< CtTracks > hs1 = tracksRelatives.get( t1 );
            if( hs1 == null ) {
                hs1 = new HashSet< CtTracks >();
                tracksRelatives.put( t1, hs1 );
            }
            hs1.add( t2 );

            HashSet< CtTracks > hs2 = tracksRelatives.get( t2 );
            if( hs2 == null ) {
                hs2 = new HashSet< CtTracks >();
                tracksRelatives.put( t2, hs2 );
            }
            hs2.add( t1 );

            // now work out which track is the parent - actually all we care about is whether it's a root
            int minIndex1 = tracksMinIndices.get( t1 );
            int minIndex2 = tracksMinIndices.get( t2 );

            if( minIndex1 < minIndex2 ) {
                _rootTracks.remove( t2 );
                _tracksParents.put( t2, t1 ); // t2's parent is t1
            }
            if( minIndex2 < minIndex1 ) {
                _rootTracks.remove( t1 );
                _tracksParents.put( t1, t2 ); // t1's parent is t2
            }
        }

        return tracksRelatives;
    }

    public void refresh3( HashMap< CtTracks, HashSet< CtTracks > > tracksRelatives ) {
        int rowStart = 0;
        int identity = 0;

        for( CtTracks t1 : _rootTracks ) {

            int rowCount = countRelativesRows( null, t1, null, tracksRelatives, rowStart, _tracksRows ); // recursive

            rowStart += rowCount;

            ++identity;
        }

        this._rowCount = rowStart;
    }

    public void refresh4() {
        setTracksIdentities();
        fireModelChanged();
    }

    public void refresh( CtSolutions s, CtCoordinatesController cc, CtImageSequenceModel ism, boolean showProgress ) {
        clear();
        
        this._s = s;
        this._ism = ism;
        this._cc = cc;

        if( showProgress ) {
            CtLineageLoader ll = new CtLineageLoader( this );
            ll.enqueue();//start();
            return;
        }

        HashMap< CtTracks, Integer > tracksMinIndices            = refresh1();
        HashMap< CtTracks, HashSet< CtTracks > > tracksRelatives = refresh2( tracksMinIndices );
                                                                   refresh3( tracksRelatives );
                                                                   refresh4();
    }

////////////////////////////////////////////////////////////////////////////////
    protected void setTracksIdentities() {

        CtLineageNamingData lnd = new CtLineageNamingData();

        Set< CtTracks > ct = _s.getCtTrackses(); // dammit need to remove this too..

        for( CtTracks t : ct ) {
            findNameOf( t, lnd );
        }

        this._tracksIdentities.clear();
        this._tracksIdentities.putAll( lnd._tracksIdentities );
    }

    public static final String SEPARATOR = ".";

    private final class CtLineageNamingData {
        int _nbrRoots = 0;
        HashMap< CtTracks, String > _tracksIdentities = new HashMap< CtTracks, String >();
        HashMap< CtTracks, Integer > _tracksNamedChildren = new HashMap< CtTracks, Integer >();
    }

    protected String findNameOf( CtTracks t, CtLineageNamingData lnd ) {//int nbrRoots, HashMap< CtTracks, Integer > tracksNamedChildren  ) {

        // did I already work it out?
        String s = lnd._tracksIdentities.get( t );

        if( s != null ) {
            return s;
        }

        CtTracks parent = _tracksParents.get( t );

        if( parent == null ) {
            s = String.valueOf( lnd._nbrRoots );// + SEPARATOR;
            ++lnd._nbrRoots;
        }
        else {

            s = findNameOf( parent, lnd );

            int n = 0;

            Integer children = lnd._tracksNamedChildren.get( parent );

            if( children != null ) {
                n = children +1;
            }

            lnd._tracksNamedChildren.put( parent, n );

            s += SEPARATOR;
            s += n;
        }

        lnd._tracksIdentities.put( t, s );

        return s;
    }
////////////////////////////////////////////////////////////////////////////////

    protected int countRelativesRows( CtTracks t0, CtTracks t1, HashSet< CtTracks > siblings, HashMap< CtTracks, HashSet< CtTracks > > tracksRelatives, int rowStart, HashMap< CtTracks, Integer > tracksRowStarts ) {

//        System.out.println( "EVALUATING t1="+t1.getPkTrack() );
//        if( t0 != null ) System.out.println( "t0="+t0.getPkTrack() );
//        else System.out.println( "t0=null" );

        HashSet< CtTracks > hs1 = tracksRelatives.get( t1 );
        if( hs1 == null ) {
//            System.out.println( "has no relative tracks." );
//            System.out.println( "Putting row of "+rowStart+" for t="+t1.getPkTrack() );
            tracksRowStarts.put( t1, rowStart );
            return 1; // separate track, not joined to anything.
        }
//        System.out.println( "Expand relatives:" );

        int totalCount = 1; // this track; should it be zero to draw inline if continuation of other track?
        int offset = 0;
        boolean parentIncluded = false;
        int parentRowStart = 0;

        for( CtTracks t2 : hs1 ) {
//            System.out.println( " Relative t2="+t2.getPkTrack() );

            if( t0 != null ) {
                if( t2.getPkTrack() == t0.getPkTrack() ) {
                    continue; // this was the origin, hence don't explore/recurse again
                }
            }

            if( siblings != null ) {
                if( siblings.contains( t2 ) ) {
//                    System.out.println( "Siblings of "+t1.getPkTrack()+" includes "+t2.getPkTrack() );
                    continue;
                }
            }
            else {
//                System.out.println( "No siblings defined." );
            }

            int count = countRelativesRows( t1, t2, hs1, tracksRelatives, rowStart+offset, tracksRowStarts );

            offset += count;
            totalCount += count;

            if( parentIncluded == false ) {
                parentIncluded = true;
                parentRowStart = rowStart + offset;
                offset += 1; // ie the parent.
            }
        }

        if( parentIncluded == false ) {
            parentRowStart = rowStart;
        }

//System.out.println( "Putting row of "+parentRowStart+" for t="+t1.getPkTrack() );
        tracksRowStarts.put( t1, parentRowStart );

        return totalCount; // may be 1 if nothing except root/t0?
    }
}
