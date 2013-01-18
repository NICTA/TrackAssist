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

package au.com.nicta.ct.db.entities;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.CtSolutionController;
import java.util.Comparator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Alan
 */
public class CtEntityPropertiesUtil {

    // stateless comparator
    public static final Comparator<CtEntityProperties> COMPARE_NAME
            = new Comparator<CtEntityProperties>() {
        public int compare( CtEntityProperties a, CtEntityProperties b) {
            return a.getName().compareTo( b.getName() );
        }
    };

    // stateless comparator
    public static final Comparator<CtEntityProperties> COMPARE_VALUE
            = new Comparator<CtEntityProperties>() {
        public int compare( CtEntityProperties a, CtEntityProperties b) {
            return a.getValue().compareTo( b.getValue() );
        }
    };


    public static String getClassName( Class cls ) {
        return cls.getSimpleName();
    }

    public static List<CtEntityProperties> find(
            Class entityName,
            Integer entityPk ) {

        return find(CtSolutionController.getSolutions(), entityName, entityPk );
    }

    public static List<CtEntityProperties> find(
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk ) {
        return find(ctSolutions, entityName, entityPk, null);
    }

    public static List<CtEntityProperties> find(
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName ) {

        Session s = CtSession.Current();
        s.beginTransaction();

        List<CtEntityProperties> r = find( s, ctSolutions, entityName, entityPk, propertyName );

        s.getTransaction().commit();
        
        return r;
    }

    public static List<CtEntityProperties> find(
            Session s,
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName ) {
        List<CtEntityProperties> l = find( s, ctSolutions.getPkSolution(), entityName, entityPk, propertyName );

        return l;
    }

    public static List<CtEntityProperties> find(
            Session s,
            Integer solutionPk,
            Class entityName,
            Integer entityPk,
            String propertyName ) {

        String qs =
                " SELECT ctEP"
              + " FROM CtEntityProperties as ctEP"
              + " WHERE";

        String prefix = "";
        if( solutionPk != null ) {
            qs += prefix + " ctEP.ctSolutions = :ctSolutions";
            prefix = " AND";
        }
        if( entityName != null ) {
            qs += prefix + " ctEP.entityName = :entityName";
            prefix = " AND";
        }
        if( entityPk != null ) {
            qs += prefix + " ctEP.entityPk = :entityPk";
            prefix = " AND";
        }
        if( propertyName != null ) {
            qs += prefix + " ctEP.name = :name";
            prefix = " AND";
        }

        Query q = s.createQuery(qs);

        if( solutionPk != null ) {
            q.setInteger( "ctSolutions", solutionPk );
//            System.out.println( solutionPk );
        }
        if( entityName != null ) {
            q.setString( "entityName", getClassName(entityName) );
//            System.out.println( getClassName(entityName) );
        }
        if( entityPk != null ) {
            q.setInteger( "entityPk", entityPk );
//            System.out.println( entityPk );
        }
        if( propertyName != null ) {
            q.setString( "name", propertyName );
//            System.out.println( propertyName );
        }
        List<CtEntityProperties> l = (List<CtEntityProperties>)q.list();

//        long end = System.nanoTime();
//        System.out.println( "Time(ms): " + (end-sta)/1000000.0 );

        return l;
    }
    
    public static void delete(
            Class entityName,
            Integer entityPk,
            String propertyName ) {

        delete( CtSolutionController.getSolutions(), entityName, entityPk, propertyName );
    }

    public static void delete(
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName ) {

        Session s = CtSession.Current();
        s.beginTransaction();

        delete( s, ctSolutions, entityName, entityPk, propertyName );

        s.getTransaction().commit();
    }

    public static void delete(
            Session s,
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName ) {

        List<CtEntityProperties> l = find( s, ctSolutions, entityName, entityPk, propertyName );
        if( l == null ) {
            return;
        }

        for( CtEntityProperties ep : l ) {
            s.delete(ep);
        }
    }

    public static CtEntityProperties persist(
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        return persist( CtSolutionController.getSolutions(), entityName, entityPk, propertyName, propertyValue );
    }


    public static CtEntityProperties persist(
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        Session s = CtSession.Current();
        s.beginTransaction();

        CtEntityProperties ep = persist( s, ctSolutions, entityName, entityPk, propertyName, propertyValue );

        s.getTransaction().commit();

        return ep;
    }

    public static void duplicatePropertyError( String tableName, int entityPk, String propertyName ) {
        throw new Error(
                  "Duplicate entity properties entry, "
                + " Table: " + tableName
                + " Row: " + entityPk
                + " Property name: " + propertyName );
    }


    public static CtEntityProperties persist(
            Session s,
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        List<CtEntityProperties> l = find( s, ctSolutions, entityName, entityPk, propertyName );
        if( !l.isEmpty() ) {
            duplicatePropertyError( getClassName(entityName), entityPk, propertyName );
        }

        CtEntityProperties ep = new CtEntityProperties(
                0,
                ctSolutions,
                getClassName(entityName),
                entityPk,
                propertyName,
                propertyValue );

        s.save( ep );

        return ep;
    }

    public static void setValue(
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        setValue( CtSolutionController.getSolutions(),  entityName, entityPk, propertyName, propertyValue );
    }

    public static void setValue(
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        Session s = CtSession.Current();
        s.beginTransaction();

        setValue(s, ctSolutions, entityName, entityPk, propertyName, propertyValue);

        s.getTransaction().commit();
    }

    public static void setValue(
            Session s,
            CtSolutions ctSolutions,
            Class entityName,
            Integer entityPk,
            String propertyName,
            String propertyValue ) {

        List<CtEntityProperties> l = find( s, ctSolutions, entityName, entityPk, propertyName );

        if( l.isEmpty() ) {
            persist( s, ctSolutions, entityName, entityPk, propertyName, propertyValue );
            return;
        }

        if( l.size() != 1 ) {
            for( CtEntityProperties ep : l ) {
                System.out.println("ep.getName(): " + (ep.getName()) );
            }
            throw new Error(
                      "Duplicate entity properties entry, "
                    + " Table: " + getClassName(entityName)
                    + " Row: " + entityPk
                    + " Property name: " + propertyName );
        }

        CtEntityProperties ep = l.get(0);

        ep.setValue( propertyValue );
        s.saveOrUpdate( ep );
    }

}
