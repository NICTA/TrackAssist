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

package au.com.nicta.tk;

import org.ujmp.core.Matrix;

/**
 *
 * @author Alan
 */
public class TkMatrixUtil {

    /**
     * @param m
     * @param rows
     * @param cols
     * @return true if not null and size matches.
     */
    public static boolean isSameSize( Matrix m, int rows, int cols ) {
        return    m != null
               && isSameSizeOrNull( m, rows, cols );
    }

    /**
     *
     * @param m
     * @param rows
     * @param cols
     * @return true if null, or size matches
     */
    public static boolean isSameSizeOrNull( Matrix m, int rows, int cols ) {
        return    m == null
               || (    m.getRowCount() == rows
                    && m.getColumnCount() == cols );
    }

    public static StringBuffer format( String formatSpecifiers, Matrix m  ) {
        StringBuffer buf = new StringBuffer();
        for( int r = 0; r < m.getRowCount(); ++r ) {
            for( int c = 0; c < m.getColumnCount(); ++c ) {
                buf.append( String.format( formatSpecifiers, m.getAsObject(r, c) ) );
                buf.append( "\t" );
            }
            buf.append( "\n" );
        }
        return buf;
    }

    public static TkMatrixCoord min( Matrix m ) {
        TkMatrixCoord ret = new TkMatrixCoord();
        ret.row = 0;
        ret.col = 0;
        double min = m.getAsDouble(0, 0);
        for( int r = 0; r < m.getRowCount(); ++r ) {
            for( int c = 0; c < m.getColumnCount(); ++c ) {
                double v = m.getAsDouble(r, c);
                if( min > v ) {
                    min = v;
                    ret.row = r;
                    ret.col = c;
                }
            }
        }
        return ret;
    }

}





