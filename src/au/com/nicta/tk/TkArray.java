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

import java.util.Arrays;

/**
 *
 * @author Alan
 */
public class TkArray {

    public static double max( double[] a ) {
        double m = Double.NEGATIVE_INFINITY;
        for( int i = 0; i < a.length; ++i ) {
            if( m < a[i] ) {
                m = a[i];
            }
        }
        return m;
    }

    public static double min( double[] a ) {
        double m = Double.POSITIVE_INFINITY;
        for( int i = 0; i < a.length; ++i ) {
            if( m > a[i] ) {
                m = a[i];
            }
        }
        return m;
    }

    public static double max( double[][] a ) {
        double m = Double.NEGATIVE_INFINITY;
        for( int i = 0; i < a.length; ++i ) {
            double mRow = max( a[i] );
            if( m < mRow ) {
                m = mRow;
            }
        }
        return m;
    }

    public static double min( double[][] a ) {
        double m = Double.POSITIVE_INFINITY;
        for( int i = 0; i < a.length; ++i ) {
            double mRow = min( a[i] );
            if( m > mRow ) {
                m = mRow;
            }
        }
        return m;
    }

    public static void fill( double[][] a, double value ) {
        for( int i = 0; i < a.length; ++i ) {
            Arrays.fill( a[i], value );
        }
    }

    /**
     * Start from firstIdx (inclusive), end at lastIdx (exclusive)
     *
     * [ firstIdx, lastIdx ) += value
     *
     * @param a
     * @param beginIdx
     * @param endIdx
     * @param value
     */
    public static void add( double[] a, int firstIdx, int lastIdx, double value ) {
        for( int i = firstIdx; i < lastIdx; ++i ) {
            a[ i ] += value;
        }
    }

    /**
     * dst[.][.] = if( test[.][.] == testValue ),  dstValue
     *             else unchanged
     * @param dst
     * @param dstValue
     * @param test
     * @param testValue
     */
    public static void setIf( double[][] dst, double dstValue, int[][] test, int testValue ) {
        for( int j = 0; j < dst.length; ++j ) {
            for( int i = 0; i < dst[j].length; ++i ) {
                if( test[j][i] == testValue ) {
                    dst[j][i] = dstValue;
                }
            }
        }
    }

    public static void replace( double[][] dst, double search, double replacement ) {
        for( int j = 0; j < dst.length; ++j ) {
            for( int i = 0; i < dst[j].length; ++i ) {
                if( dst[j][i] == search ) {
                    dst[j][i] = replacement;
                }
            }
        }
    }

}



