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

package au.com.nicta.ct.db;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtProperties;
import au.com.nicta.ct.db.hibernate.CtPropertiesTypes;
import java.util.List;
import java.util.StringTokenizer;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * TODO: Exploit caching
 * @author davidjr
 */
public class CtKeyValueProperties {

    protected static final boolean usePrivateSession = true;
    protected static final boolean useReadCaching = false;
    protected static CtObjectDirectory od = new CtObjectDirectory();
    // drop ct prefixes. force lowercase.
    // ordering in an attempt to make indexing work well:
    // pk-[table]-[property]
    // pk-experiments-[property]
    // e.g. 1044-experiments-microwells

    // Key building functions
    public static String key( String classname, String prefix ) {
        // * <prefix>[-tablename][-pk]
        // * property-name[-tablename][-pk]
        String join = "-";
        String s = prefix + join + classname;
        return s;
    }

    public static String key( Object pojo, String prefix, int pk ) {
        String classname = pojo.getClass().getSimpleName();
        return key( classname, prefix, pk );
    }

    public static String key( String classname, String prefix, int pk ) {
        // * <prefix>[-tablename][-pk]
        // * property-name[-tablename][-pk]
        String join = "-";
        String s = prefix + join + classname + join + String.valueOf( pk );
        return s;
    }

    public static String getNameToken( String fullName ) {
        StringTokenizer t = new StringTokenizer(fullName, "-");
        return t.nextToken();
    }

    public static String getClassToken( String fullName ) {
        StringTokenizer t = new StringTokenizer(fullName, "-");
        t.nextToken();
        return t.nextToken();
    }

    public static String getPkToken( String fullName ) {
        StringTokenizer t = new StringTokenizer(fullName, "-");
        t.nextToken();
        t.nextToken();
        return t.nextToken();
    }


    // Property object access
    public static CtProperties find( Object pojo, String prefix ) { // pojo assumed to have getPK and be CtXXXX?
        String classname = pojo.getClass().getSimpleName();
        return find( classname, prefix );
    }

    public static CtProperties find( Object pojo, String prefix, int pk ) { // pojo assumed to have getPK and be CtXXXX?
        String classname = pojo.getClass().getSimpleName();
        return find( classname, prefix, pk );
    }

    public static CtProperties find( String classname, String prefix ) {
        // this one where there's one per table, not per row in table
        String key = key( classname, prefix );
        return find( key );
    }

    public static void delete( String key ) {
        CtProperties p = find( key );
        delete( p );
    }

    public static void persist( Object pojo, String prefix, int pk, String value ) {
        String classname = pojo.getClass().getSimpleName();
        persist( classname, prefix, pk, value );
    }

    public static void persist( String classname, String prefix, int pk, String value ) {
        String key = key( classname, prefix, pk );
        persist( key, value );
    }

    public static void persist( String key, String value ) {
        String hql = " FROM CtPropertiesTypes pt WHERE pt.name = '" + CtApplication.PROPERTY_TYPE_SYSTEM + "'";
//x        CtPropertiesTypes pt = (CtPropertiesTypes)CtSession.getObject( hql );
        Session s = CtSession.Create();
        CtPropertiesTypes pt = (CtPropertiesTypes)CtSession.getObject( s, hql );

        persist( pt, key, value );
    }

    public static CtProperties find( String classname, String prefix, int pk ) {
        String key = key( classname, prefix, pk );
        return find( key );
    }

//    public static String hql( String key ) {
//        String hql = " FROM CtProperties p WHERE p.name = '" + key + "'";
//        return hql;
//    }
//
//    public static String hqlPrefix( String key ) {
//        String hql = " FROM CtProperties p WHERE p.name like '" + key + "%'";
//        return hql;
//    }
//
//    public static String hqlSuffix( String key ) {
//        String hql = " FROM CtProperties p WHERE p.name like '%" + key + "'";
//        return hql;
//    }

    public static Query queryLike( String key, Session s ) {
        String hql = " FROM CtProperties p WHERE p.name LIKE ?";
        Query q = s.createQuery( hql );
        q.setString( 0, key );
        return q;
    }

    public static Query query( String key, Session s ) {
        String hql = " FROM CtProperties p WHERE p.name = ?";
        Query q = s.createQuery( hql );
        q.setString( 0, key );
        return q;
    }

// can't have %? in parameters
//    public static Query querySuffix( String keySuffix, Session s ) {
//        String hql = " FROM CtProperties p WHERE p.name LIKE '%?'";
//        Query q = s.createQuery( hql );
//        q.setString( 0, keySuffix );
//        return q;
//    }

//    public static CtProperties findEvicted( String key ) {
//
//        // 1. get ALL the properties matching this key. There will be duplicates where
//        // they apply over non-overlapping ranges.
//        String hql = hql( key );
//
//        CtProperties p = (CtProperties)CtSession.getObject( hql );
//
//        return p;
////        List l = CtSession.getList( hql );
////
////        int results = l.size();
////
////        if( results == 0 ) {
////            return null;
////        }
////
////        return (CtProperties)l.get( 0 );
//    }

    // Property value direct access:
    public static String getValue( String key ) {

        // try to use cache:
        if( useReadCaching ) {
            String s = (String)od.get( key );
            if( s != null ) {
                return s;
            }
        }

        CtProperties p = find( key );

        if( p == null ) {
            if( useReadCaching ) {
                od.remove( key );
            }
            return null;
        }

        String s = p.getValue();

        // update cache:
        if( useReadCaching ) {
            od.put( key, s );
        }

        return s;
    }

    public static void setValue( String key, String value ) {
        CtProperties p = find( key );

        if( p == null ) {
            persist( key, value ); // will cache

            return;
        }

//x        Session s = CtSession.Current();
        Session s = CtSession.Create();

        try {
            p.setValue( value );
            s.saveOrUpdate( p );
            s.flush();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }
        
        if( useReadCaching ) {
            od.put( key, value );
        }
    }

    public static void delete( CtProperties p ) {
//x        Session s = CtSession.Current();
        Session s = CtSession.Create();

        try {
            s.beginTransaction();
            s.delete( p );
            s.getTransaction().commit();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }

        if( useReadCaching ) {
            od.remove( p.getName() );
        }
    }

    public static void persist( CtPropertiesTypes pt, String key, String value ) {

        CtProperties p = new CtProperties();

        p.setCtPropertiesTypes( pt );
        p.setName( key );
        p.setValue( value );

        Session s = CtSession.Create();

        try {
            s.beginTransaction();
            s.save( p );
            s.getTransaction().commit();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }

        if( useReadCaching ) {
            od.put( key, value );
        }
//s.beginTransaction();
//s.merge( p );
//        return p;
    }

//todo: track-concatenation tool, then export tools.. speak to john weds.
//then fix auto-segmentation..
    public static CtProperties find( String key ) {

        List results = null;
        Session s = CtSession.Create();

        try {
            s.beginTransaction();

            Query q = query( key, s );
            results = q.list();

            s.getTransaction().commit();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }

        if( results == null ) return null;
        if( results.size() < 1 ) return null;

        CtProperties p = (CtProperties)results.get( 0 );
        return p;
    }

    public static String getSuffixOfClass( Class c, int pk ) {
        return key( c.getSimpleName(), "", pk );
    }

    public static String getSuffixOfPojo( Object pojo, int pk ) {
        return key( pojo.getClass().getSimpleName(), "", pk );
    }

    public static List findAllWithPojo( Object pojo, int pk ) {
        String keySuffix = getSuffixOfPojo( pojo, pk );
        return findAllWithSuffix( keySuffix );
    }

    public static List findAllWithSuffix( String keySuffix ) {

        List results = null;
        Session s = CtSession.Create();

        try {
            s.beginTransaction();

            Query q = queryLike( "%"+keySuffix, s );
            results = q.list();

            s.getTransaction().commit();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }

        return results;
    }

    public static List findAllWithPrefix( String keyPrefix ) {

        List results = null;
        Session s = CtSession.Create();

        try {
            s.beginTransaction();

            Query q = queryLike( keyPrefix+"%", s );
            results = q.list();

            s.getTransaction().commit();
        }
        catch( Exception e ) {}
        finally {
            s.close();
        }

        return results;
    }

}

//        s.beginTransaction();
//
//        Query q = s.createQuery( query );
//
//        List results = q.list();
//
//        createAxes( results );
//
//        s.getTransaction().commit();
