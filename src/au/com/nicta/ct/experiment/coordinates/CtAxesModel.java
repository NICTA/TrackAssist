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
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtExperimentsAxes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * say I have channels, time, and position.
 * A sequence is holding all axes fixed and varying one.
 *
 * experiment has a number of axes.
 * Any axis can be changed, but only one axis is a range.
 * 
 * @author davidjr
 */
public class CtAxesModel {

    protected CtExperiments _e;
    protected ArrayList< CtExperimentsAxes > _axes = new ArrayList< CtExperimentsAxes >();
//    protected CtExperimentsAxes _range; // this axis is represented as a range

    public CtAxesModel() {
        
    }

    public CtExperimentsAxes find( CtCoordinatesTypes ct ) {
        for( CtExperimentsAxes ea : _axes ) {
            CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
            CtCoordinatesTypes ct1 = c1.getCtCoordinatesTypes();

            if( ct.getPkCoordinateType() == ct1.getPkCoordinateType() ) {
                return ea;
            }
        }

        return null;
    }

    public CtExperimentsAxes find( String coordinateType ) {
        for( CtExperimentsAxes ea : _axes ) {
            CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
            CtCoordinatesTypes ct1 = c1.getCtCoordinatesTypes();

            String name = ct1.getName();

            if( name.equals( coordinateType ) ) {
                return ea;
            }
        }

        return null;
    }

    public void create( CtExperiments e ) {
        _e = e;
        String query = " from CtExperimentsAxes where ctExperiments ="+e.getPkExperiment();
        createAxes( query );
    }

    protected String createQuery() {
        if( _e == null ) return null;
        String query = " from CtExperimentsAxes where ctExperiments ="+_e.getPkExperiment();
        return query;
    }

//
//    public void createWith( int pkExperiment ) {
//        String query = " from CtExperimentsAxes where ctExperiments ="+pkExperiment;
//        create( e, query );
//    }

    public void createAxes( String query ) {

        if( query == null ) {
            return;
        }
        
        Session s = CtSession.Current();
        s.beginTransaction();

        Query q = s.createQuery( query );

        List results = q.list();

        createAxes( results );

        s.getTransaction().commit();
    }

    public CtExperiments experiment() {
        return _e;
    }

    public void clear() {
        _e = null;
        _axes.clear();
//        _range = null;
    }

    protected void createAxes( List queryResults ) {
//        clear();
        _axes.clear();

        if( queryResults.isEmpty() ) {
            return;
        }

        Iterator i = queryResults.iterator();

        while( i.hasNext() ) {

            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();

            _axes.add( ea );
        }
    }

    public Collection< CtExperimentsAxes > axes() {
        return _axes;
    }

    public Collection< String > axesCoordinatesTypes() {
        ArrayList< String > al = new ArrayList< String >();

        for( CtExperimentsAxes ea : _axes ) {

            String coordinateType = ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes().getName();

            al.add( coordinateType );
        }

        return al;
    }
//    public CtExperimentsAxes range() {
//        return _range;
//    }
}
