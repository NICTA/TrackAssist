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

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * http://stackoverflow.com/questions/268651/session-management-using-hibernate-in-a-swing-application
 * "Single session. Start transaction when you need to do a set of operations
 * (like update data after dialog box OK button), commit the tx at the end. The
 * connection though is constantly open (since it's the same session), and thus
 * all opportunities for caching can be used by both Hib and RDBMS.
 *
 * It may also be a good idea to implement a transparent session re-open in case
 * the connection went dead -- users tend to leave applications open for
 * extended periods of time, and it should continue to work Monday even if DB
 * server was rebooted on weekend."
 *
 * http://blog.schauderhaft.de/2008/09/28/hibernate-sessions-in-two-tier-rich-client-applications/
 * @author davidjr
 */
public class CtSession {

    protected static SessionFactory _sf;
    protected static Session _s;
    
    public static Session Create() {
        SessionFactory sf = GetSessionFactory();
        Session s = sf.openSession();
        return s;
    }

    public static Session Current() {
        if( _s == null ) {
            _s = Create();
        }
        return _s;
    }

    public static SessionFactory GetSessionFactory() {
        if( _sf == null ) {
            _sf = HibernateUtil.getSessionFactory();
        }
        return _sf;
    }

    public static List getObjects( Class c ) {//, int pk ) {
        String hql = " from " + c.getSimpleName();// + " where pk";
        return getObjects( hql );
    }
    public static List getObjects( Session s, Class c ) {//, int pk ) {
        String hql = " from " + c.getSimpleName();// + " where pk";
        return getObjects( s, hql );
    }

    public static Object getObject( Class c, int pk ) {
        Session s = Current();
        return getObject( s, c, pk );
    }

    public static Object getObject( Session s, Class c, int pk ) {
        return s.get( c, pk );
        // TODO should use:
//EntityManager find() method, as so:
//em.find(Dog.class, myDogPrimaryKeyValue)
    }

    public static Object getObject( Session s, String hql ) {
        List results = getObjects( s, hql );

        if( results.isEmpty() ) {
            return null;
        }

        return results.get( 0 );
    }

    public static Object getObject( String hql ) {
        List results = getObjects( hql );

        if( results.isEmpty() ) {
            return null;
        }

        return results.get( 0 );
    }

    public static List getObjects( String hql ) {
        Session s = Current();
        return getObjects( s, hql );
    }
    
    public static List getObjects( Session s, String hql ) {
        s.beginTransaction();
        Query q = s.createQuery( hql );
        List results = q.list();

        s.getTransaction().commit();

        return results;
    }

}
