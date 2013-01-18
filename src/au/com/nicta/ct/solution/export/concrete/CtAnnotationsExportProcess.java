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

import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.export.CtExportCSV;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtAnnotationsExportProcess extends CtDefaultExportProcess {

    CtExportCSV ecsv = new CtExportCSV();

    public String getName() {
        return "Annotations (.csv)";
    }

    public String apply(CtSolutions sol, String filePath) {

        ecsv.clear();

        // make list of events, ordered by time.
        ecsv.addHeader( getFields() );

        Session s = CtSession.Current();
        CtManualFlush mf = new CtManualFlush(s);

        Query q = s.createQuery(
                " SELECT ctA"
              + " FROM CtAnnotations as ctA"
              + " WHERE ctA.ctSolutions = :solution" );

        q.setInteger( "solution", sol.getPkSolution() );

        List<CtAnnotations> l = (List<CtAnnotations>) q.list();
        for( CtAnnotations a : l ) {
            ArrayList< String > fields = new ArrayList< String >();

            fields.add( String.valueOf( a.getPkAnnotation() ) );
            fields.add( String.valueOf( a.getCtAnnotationsTypes().getValue() ) );
            fields.add( String.valueOf( a.getCtSolutions().getPkSolution() ) );
            fields.add( String.valueOf( a.getCtImages().getPkImage() ) );
            fields.add( String.valueOf( a.getCtImages().getUri() ) );
            fields.add( String.valueOf( a.getValue() ) );
            fields.add( String.valueOf( a.getX() ) );
            fields.add( String.valueOf( a.getY() ) );

            ecsv.addRow(fields);
        }

        mf.restore();

        // save events to file..
        String result = ecsv.write( filePath );
        return result;
    }

    public Collection< String > getFields() {
        ArrayList< String > fields = new ArrayList< String >();

        fields.add( "pk_annotation" );
        fields.add( "type" );
        fields.add( "fk_solution" );
        fields.add( "pk_image" );
        fields.add( "image_uri");
        fields.add( "value" );
        fields.add( "x" );
        fields.add( "y" );

        return fields;
    }





}
