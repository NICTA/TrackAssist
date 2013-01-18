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
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtExperimentsAxes;
import java.util.Iterator;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtLimits {

    public void updateLimits( CtExperiments e ) {

        deleteLimits( e );
        insertLimits( e );
    }

    protected void insertLimits( CtExperiments e ) {

        String sql = 
                " SELECT c.fk_coordinate_type, MIN( c.value ) as limit1, MAX( c.value ) as limit2 "
              + " FROM ct_images_coordinates ic "
              + " INNER JOIN ct_coordinates c ON ic.fk_coordinate = c.pk_coordinate"
              + " INNER JOIN ct_images i ON ic.fk_image = i.pk_image"
              + " WHERE fk_experiment = "+e.getPkExperiment() //--- for efficiency only update specific expt
              + " GROUP BY i.fk_experiment, fk_coordinate_type"
              + " ORDER BY i.fk_experiment, fk_coordinate_type";

        Session session = CtSession.Current();

        // 2 queries: first is a native one which is tuned to efficiently get
        // the PKs of the sequence, in order..
        // Sorry - 1 + 3n queries where n is number of dimensions...
        Query query = session.createSQLQuery( sql );

        Iterator i = query.list().iterator();

        while( i.hasNext() ) {

            Object[] o = (Object[])i.next();

            int fkCoordinateType = ((Integer)o[ 0 ]).intValue();
            int limit1           = ((Integer)o[ 1 ]).intValue();
            int limit2           = ((Integer)o[ 2 ]).intValue();

            sql = " SELECT min( pk_coordinate ), value "
                + " FROM ct_images_coordinates ic "
                + " INNER JOIN ct_coordinates c ON ic.fk_coordinate = c.pk_coordinate "
                + " INNER JOIN ct_images i on ic.fk_image = i.pk_image"
                + " WHERE fk_experiment = " +e.getPkExperiment()// --- for efficiency only update specific expt
                + " AND c.fk_coordinate_type = "+ fkCoordinateType
                + " AND (c.value = "+limit1+" OR c.value = "+limit2+" ) "
                + " GROUP BY c.value "
                + " ORDER BY c.value ";

            Query query2 = session.createSQLQuery( sql );

            Iterator i2 = query2.list().iterator();

            int pkCoordinate1 = -1;
            int pkCoordinate2 = -1;

            // should be 2 rows:
            int row = 0;

            while( i2.hasNext() ) {
                Object[] o2 = (Object[])i2.next();
                
                int pkCoordinate = ((Integer)o2[ 0 ]).intValue();

                if( row == 0 ) {
                    pkCoordinate1 = pkCoordinate;
                }
                else {
                    pkCoordinate2 = pkCoordinate;
                }

                ++row;
            }

            CtCoordinates c1 = (CtCoordinates)session.get( CtCoordinates.class, pkCoordinate1 );
            CtCoordinates c2 = null;

            if( limit1 == limit2 ) {
                c2 = c1;
            }
            else {
                c2 = (CtCoordinates) session.get(CtCoordinates.class, pkCoordinate2 );
            }
                
            CtExperimentsAxes ea = new CtExperimentsAxes();

            ea.setCtExperiments( e );
            ea.setCtCoordinatesByFkCoordinate1( c1 );
            ea.setCtCoordinatesByFkCoordinate2( c2 );

            Transaction t = session.beginTransaction();
            session.save( ea );
//            session.persist( ea );
            t.commit(); //you might even want to wrap this in another try/catch block.
//            session.delete( ea );
//            session.save( ea );
//            session.getTransaction().commit();
        }

//        session.getTransaction().commit();
    }

    protected void deleteLimits( CtExperiments e ) {

        String sql = " delete from ct_experiments_axes where fk_experiment = "+e.getPkExperiment();

        Session session = CtSession.Current();

// *****************************************************************************
        session.beginTransaction();
// *****************************************************************************

        // 2 queries: first is a native one which is tuned to efficiently get
        // the PKs of the sequence, in order..
        Query query = session.createSQLQuery( sql );

        query.executeUpdate();

        session.getTransaction().commit();
    }

}
