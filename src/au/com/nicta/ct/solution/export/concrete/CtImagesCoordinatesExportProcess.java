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
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.export.CtExportCSV;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Coordinates of every image, CSV. Also a complete unique list of image PKs.
 * @author davidjr
 */
public class CtImagesCoordinatesExportProcess extends CtDefaultExportProcess {

    CtExportCSV ecsv = new CtExportCSV();

    public String getName() {
        return "Image coordinates (.csv)";
    }

    public String apply(CtSolutions sol, String filePath) {

        ecsv.clear();
        ecsv.addHeader(getFields());

        Session s = CtSession.Current();
        CtManualFlush mf = new CtManualFlush(s);
        
        Query q = s.createQuery(
                " SELECT ctIC"
              + " FROM CtImagesCoordinates as ctIC" );

        List<CtImagesCoordinates> l = (List<CtImagesCoordinates>) q.list();
        
        int currentExperiment = sol.getCtExperiments().getPkExperiment();
        for( CtImagesCoordinates ic : l ) {
            // skip if not in current experiment
            if( ic.getCtImages().getCtExperiments().getPkExperiment() != currentExperiment ) {
                continue;
            }

            ArrayList< String > fields = new ArrayList< String >();

            fields.add( String.valueOf( ic.getPkImageCoordinate() ) );
            fields.add( String.valueOf( ic.getCtImages().getPkImage() ) );
            fields.add( String.valueOf( ic.getCtImages().getUri() ) );
            fields.add( String.valueOf( ic.getCtCoordinates().getCtCoordinatesTypes().getName() ) );
            fields.add( String.valueOf( ic.getCtCoordinates().getValue() ) );
            fields.add( String.valueOf( ic.getCtCoordinates().getName() ) );

            ecsv.addRow(fields);
        }

        mf.restore();

        String result = ecsv.write(filePath);
        return result;
    }


    public Collection< String > getFields() {
        ArrayList< String > fields = new ArrayList< String >();
        
        fields.add( "pk_image_coordinate" );
        fields.add( "pk_image" );
        fields.add( "image_uri" );
        fields.add( "coordinate_type" );
        fields.add( "coordinate_value" );
        fields.add( "coordinate_name" );

        return fields;
    }
    
}








