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

package au.com.nicta.ct.solution.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author davidjr
 */
public class CtExportCSV {

    public static final String SEPARATOR = ",";
    public static final String NEW_LINE = "\n";

    private TreeMap< Integer, CtConcurrentFileRows > _tm = new TreeMap< Integer, CtConcurrentFileRows >();

    CtFileRow _headerRow;

    public CtExportCSV() {

    }

    public void clear() {
        _headerRow = null;
        _tm.clear();
    }
    
    // internally convert to CSV
    public String write( String filePath ) {
        try {
            FileWriter fw = new FileWriter( filePath );

            if( _headerRow != null ) {
                writeRow( fw, _headerRow );
            }

            Set< Entry< Integer, CtConcurrentFileRows > > es = _tm.entrySet();

            Iterator i = es.iterator();

            while( i.hasNext() ) {

                Entry< Integer, CtConcurrentFileRows > e = (Entry< Integer, CtConcurrentFileRows >)i.next();

                CtConcurrentFileRows cfr = e.getValue();

                for( CtFileRow fr : cfr._rows ) {
                    writeRow( fw, fr );
                }
            }

            fw.flush();
            fw.close();
            
            return null; // no error
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            return "ERROR: Can't export to file "+filePath;
        }
    }

    private void writeRow( FileWriter fw, CtFileRow fr ) throws IOException {
        boolean first = true;

        for( String field : fr._fields ) {

            String s = field;

            if( !first ) {
                s = SEPARATOR+s;
            }

            first = false;

            fw.append( s );
        }
        fw.append( NEW_LINE );
    }

    public void addHeader( Collection< String > fields ) {
        _headerRow = new CtFileRow();
        _headerRow._fields.addAll( fields );
    }

    public void addRow( Collection< String > fields ) {
        int size = _tm.size();
        addRow( size, fields );
    }

    public void addRow( int rowOrdering, Collection< String > fields ) {
        CtConcurrentFileRows cte = _tm.get( rowOrdering );

        if( cte == null ) {
            cte = new CtConcurrentFileRows();
            _tm.put( rowOrdering, cte );
        }

        CtFileRow te = new CtFileRow();
        te._fields.addAll( fields );
        cte._rows.add( te );
    }

}

class CtFileRow {
    ArrayList< String > _fields = new ArrayList< String >();
}

class CtConcurrentFileRows {
    ArrayList< CtFileRow > _rows = new ArrayList< CtFileRow >();
}

