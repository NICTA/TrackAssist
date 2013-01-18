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

package au.com.nicta.ct.solution.export.concrete;

import au.com.nicta.ct.db.entities.CtEntityPropertiesUtil;
import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.export.CtExportCSV;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class CtEntityPropertiesExport {

    public static void apply(CtSolutions s, CtExportCSV ecsv, Class entityClass ) {

        ecsv.clear();

        // make list of events, ordered by time.
        ecsv.addHeader( getFields() );

        Session session = CtSession.Current();
        CtManualFlush mf = new CtManualFlush(session);
        session.beginTransaction();

        List<CtEntityProperties> properties = CtEntityPropertiesUtil.find(
                session,
                s,
                entityClass,
                null,
                null );

        for( CtEntityProperties ep : properties ) {

            ArrayList< String > fields = new ArrayList< String >();

            fields.add( String.valueOf( ep.getPkEntityProperty() ) );
            fields.add( String.valueOf( ep.getCtSolutions().getPkSolution() ) );
            fields.add( String.valueOf( ep.getCtSolutions().getName() ) );
            fields.add( String.valueOf( ep.getEntityName() ) );
            fields.add( String.valueOf( ep.getEntityPk() ) );
            fields.add( String.valueOf( ep.getName() ) );
            fields.add( String.valueOf( ep.getValue() ) );

            ecsv.addRow(fields);
        }

        session.getTransaction().commit();
        mf.restore();
    }

    public static Collection< String > getFields() {
        ArrayList< String > fields = new ArrayList< String >();

        fields.add( "pk_entity_property" );
        fields.add( "pk_solution" );
        fields.add( "solution_name" );
        fields.add( "entity_name" );
        fields.add( "entity_pk" );
        fields.add( "name" );
        fields.add( "value" );

        return fields;
    }

}
