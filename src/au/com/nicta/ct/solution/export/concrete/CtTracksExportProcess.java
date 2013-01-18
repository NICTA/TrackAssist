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

import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.solution.export.CtDefaultExportProcess;
import au.com.nicta.ct.solution.export.CtExportCSV;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Export all the data defining every track in the solution.
 * @author davidjr
 */
public class CtTracksExportProcess extends CtDefaultExportProcess {

    CtExportCSV ecsv = new CtExportCSV();

    public String getName() {
        return "Tracks (.csv)";
    }

    public String apply(CtSolutions s, String filePath) {

        ecsv.clear();

        ecsv.addHeader( getFields() );


        Set<CtTracks> tracks = (Set<CtTracks>) s.getCtTrackses();

        for( CtTracks t : tracks ) {

            int pkTrack = t.getPkTrack();

            Set<CtTracksDetections> tds = (Set<CtTracksDetections>) t.getCtTracksDetectionses();
            for( CtTracksDetections td : tds ) {
                ArrayList< String > fields = new ArrayList< String >();

                fields.add( String.valueOf( td.getPkTrackDetection() ) );
                fields.add( String.valueOf( pkTrack ) );
                fields.add( String.valueOf( td.getCtDetections().getPkDetection() ) );

                ecsv.addRow(fields);
            }
        }


        // save events to file..
        String result = ecsv.write( filePath );
        return result;
    }

    public static Collection< String > getFields() {
        ArrayList< String > fields = new ArrayList< String >();

        fields.add( "pk_track_detection" );
        fields.add( "pk_track" );
        fields.add( "fk_detection" );

        return fields;
    }


}
