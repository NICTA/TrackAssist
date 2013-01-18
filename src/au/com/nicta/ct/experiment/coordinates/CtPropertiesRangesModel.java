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

package au.com.nicta.ct.experiment.coordinates;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtProperties;
import au.com.nicta.ct.db.hibernate.CtPropertiesRanges;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * for a given property, compute the key given tablename, pk, (or POJO object!) and property name
 *  manipulate the convex set of ranges and associated coordinate - ref existing objects?
 *
 * In the case where there are duplicate values for a given property name, they
 * must apply over non-overlapping convex ranges within the images' dimensions.
 *
 * <prefix>[-tablename][-pk]
 * property-name[-tablename][-pk]
 * 
 * e.g. area-detections
 * Usage:
 * 
 * a) Get all properties given a prefix and a tablename
 * b) Get all properties associated with a particular row in a table (pk suffix)
 * c) Get all properties associated with a particular row in a table (pk suffix)
 *    regardless of coordinates (ie multiple results)
 * d) Get property associated with a particular row in a table (pk suffix) 
 *    filtered by a set of coordinates 
 *
 * @author davidjr
 */
public class CtPropertiesRangesModel {
/*
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

    public static CtProperties find( String classname, String prefix, int pk ) {
        String key = key( classname, prefix, pk );
        return find( key );
    }

    public static String key( String classname, String prefix ) {
        // * <prefix>[-tablename][-pk]
        // * property-name[-tablename][-pk]
        String join = "-";
        String s = prefix + join + classname;
        return s;
    }

    public static String key( String classname, String prefix, int pk ) {
        // * <prefix>[-tablename][-pk]
        // * property-name[-tablename][-pk]
        String join = "-";
        String s = prefix + join + classname + join + String.valueOf( pk );
        return s;
    }

    public static CtProperties find( String key ) {
        return findAt( key, null );
    }*/

    public static CtProperties findAt( String key, Collection< CtCoordinates > coordinates ) {

        // 1. get ALL the properties matching this key. There will be duplicates where
        // they apply over non-overlapping ranges.
        String hql = " FROM CtProperties p WHERE p.name = '" + key + "'";
        
        List l = CtSession.getObjects( hql );

        int results = l.size();

        if( results == 0 ) {
            return null;
        }

        if(    ( l.size() == 1 )
            || ( coordinates == null ) ) {
            return (CtProperties)l.get( 0 );
        }

        // OK so we need to choose then:
        // Build a structure to store the coordinates keyed by type:
        HashMap< CtCoordinatesTypes, CtCoordinates > hm = new HashMap< CtCoordinatesTypes, CtCoordinates >();

        for( CtCoordinates c : coordinates ) {
            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
            hm.put( ct, c );
        }
        
        // go through all the properties we found matching, and see which has the correct ranges:
        CtProperties applicable = null;

        Iterator i = l.iterator();
        
        while( i.hasNext() ) {
            CtProperties p = (CtProperties)i.next();

            // test to see if coordinates match p's:
            Set< CtPropertiesRanges > ranges = p.getCtPropertiesRangeses();

            int matching = 0;

            for( CtPropertiesRanges range : ranges ) {
                CtCoordinates c1 = range.getCtCoordinatesByFkCoordinate1();
                CtCoordinates c2 = range.getCtCoordinatesByFkCoordinate1();
                CtCoordinatesTypes ct = c1.getCtCoordinatesTypes();

                CtCoordinates c3 = hm.get( ct ); // get the specified coord of same type.

                int n1 = c1.getValue();
                int n2 = c2.getValue();
                int n3 = c3.getValue();

                if( n3 < n1 ) break; // failure to match any single dim means this isn't the right property
                if( n3 > n2 ) break;

                ++matching;
            }

            int nbrRanges = ranges.size();
            if( matching == nbrRanges ) {
                applicable = p;
                break; // we found it already
            }
        }

        return applicable; // may be null
    }
    
}
