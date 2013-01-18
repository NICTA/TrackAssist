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

import au.com.nicta.tk.TkArray;
import java.util.Arrays;



/**
 *
 * @author Alan
 */
public class TkHungarianAlgorithm {
    
    private static final boolean DEBUG = false;

    enum Mode {
        MAXIMIZE,
        MINIMIZE
    }

    final int HUNGARIAN_NOT_ASSIGNED = 0;
    final int HUNGARIAN_ASSIGNED = 1;


    double[][] cost;
    public boolean[][] assigned;

    double columMax( double[][] a, int col ) {
        double m = Double.NEGATIVE_INFINITY;
        for( int r = 0; r < a.length; ++r ) {
            if( m < a[ r ][ col ] ) {
                m = a[ r ][ col ];
            }
        }
        return m;
    }

    double columMin( double[][] a, int col ) {
        double m = Double.POSITIVE_INFINITY;
        for( int r = 0; r < a.length; ++r ) {
            if( m > a[ r ][ col ] ) {
                m = a[ r ][ col ];
            }
        }
        return m;
    }

    void run( double[][] inputScore, Mode mode ) {

        // is the number of cols  not equal to number of rows ?
        // if cols != rows, padd with zeros
        int inputRows = inputScore.length;
        int inputCols = inputScore[0].length;

        int size = Math.max( inputRows, inputCols );

        assigned = new boolean[size][size]; // all init to false

        double padding, scale, shift;

        double max = TkArray.max( inputScore );
        padding = max * 10;

        if( mode == Mode.MINIMIZE ) { // then negate all scores
            scale = 1.0f;
            shift = 0;
        }
        else if( mode == Mode.MAXIMIZE ) { // then turn it into a minimization problem
            scale = -1.0f;
            shift = max;
        }
        else {
            throw new IllegalArgumentException( "unsupported mode: " + mode );
        }

        cost = new double[size][size];
        for( int r = 0; r < size; ++r ) {
            for( int c = 0; c < size; ++c ) {
                if(    r < inputRows
                    && c < inputCols ) {
                    cost[r][c] = inputScore[r][c] * scale  +  shift;
                }
                else {
                    cost[r][c] = padding;
                }
            }
        }

        if( DEBUG ) {
            System.out.println("cost: " + Arrays.deepToString(inputScore) );
            System.out.println("cost: " + Arrays.deepToString(cost) );
        }




//        int i, j, m, n, k, l, t, q, unmatched;
//        double s,cost_internal;
//        int* colMate;
//        int* rowMate;
//        int* parentRow;
//        int* unchosenRow;
//        double* rowDec=0;
//        double* colInc=0;
//        double* slack=0;
//        int* slackRow=0;
        
        int numRows = size;
        int numCols = size;

        int[] colMate = new int[numRows];
        int[] unchosenRow = new int[numRows];
        double[] rowDec = new double[numRows];
        int[] slackRow = new int[numRows];

        int[] rowMate = new int[numCols];
        int[] parentRow = new int[numCols];
        double[] colInc = new double[numCols];
        double[] slack = new double[numCols];
  
        double internalCost = 0.0;

        // Begin subtract column minima in order to start with lots of zeroes 12
        //if (verbose)
        //  fprintf(stderr, "Using heuristic\n");
        for( int c = 0; c < numCols; ++c ) {
            double min = columMin( cost, c );
            internalCost += min;
            if( min != 0 ) {
                for( int r = 0; r < numRows; ++r ) {
                    cost[r][c] -= min;
                }
            }
        }

        if( DEBUG ) {
            System.out.println( "Cost after subtract min: " + Arrays.deepToString(cost) );
        }

        // Begin initial state 16
        Arrays.fill( rowMate, -1 );
        Arrays.fill( parentRow, -1 );
        Arrays.fill( colInc, 0.0 );
        Arrays.fill( slack, Double.POSITIVE_INFINITY );

//        System.out.println( "rowMate: " + Arrays.toString(rowMate) );

        int m = numRows;
        int n = numCols;

        int t, k, l, q, j, unmatched;
        double s;

        t=0;

        row_done: for (k=0;k<m;k++) {
            s=cost[k][0];
            for (l=1;l<n;l++) {
                if (cost[k][l]<s) {
                    s=cost[k][l];
                }
            }

            rowDec[ k ] = s;
            for (l=0;l<n;l++) {
                if (s==cost[k][l] && rowMate[l]<0) {
                  colMate[k] = l;
                  rowMate[l] = k;
                  //if (verbose)
                  //  fprintf(stderr, "matching col %d==row %d\n",l,k);
                  continue row_done;
                }
            }

            colMate[ k ] = -1;
            //if (verbose)
            //fprintf(stderr, "node %d: unmatched row %d\n",t,k);
            unchosenRow[ t++ ]=k;
        }

        // Begin Hungarian algorithm 18
        done: {
            if (t==0) {
                break done;
            }

            unmatched = t;
            while( true ) {
                //if (verbose)
                //fprintf(stderr, "Matched %d rows.\n",m-t);
                q = 0;
                breakthru: while( true ) {
                    while (q<t) {
                        // Begin explore node q of the forest 19
                        {
                            k = unchosenRow[ q ];
                            s = rowDec[ k ];
                            for (l=0;l<n;l++) {
                                if (slack[l] != 0) {
                                    double del;
                                    del = cost[k][l] - s + colInc[l];
                                    if (del<slack[l]) {
                                        if (del==0) {
                                            if (rowMate[l]<0) {
                                                break breakthru;
                                            }
                                            slack[l]=0;
                                            parentRow[l]=k;
                                            //if (verbose)
                                            //fprintf(stderr, "node %d: row %d==col %d--row %d\n",t,row_mate[l],l,k);
                                            unchosenRow[t++] = rowMate[l];
                                        }
                                        else {
                                            slack[l]=del;
                                            slackRow[l]=k;
                                        }
                                    }
                                }
                            }
                        }
                        // End explore node q of the forest 19
                        q++;
                    }

                    // Begin introduce a new zero into the matrix 21
                    s = Double.POSITIVE_INFINITY;
                    for (l=0;l<n;l++) {
                        if (slack[l] != 0 && slack[l]<s) {
                            s=slack[l];
                        }
                    }
                    for (q=0;q<t;q++) {
                        rowDec[ unchosenRow[q] ] += s;
                    }
                    for (l=0;l<n;l++) {
                        if( slack[l] != 0 ) {
                            slack[l] -= s;
                            if( slack[l] == 0 ) {
                                // Begin look at a new zero 22
                                k = slackRow[l];
                                //if (verbose)
                                //  fprintf(stderr, "Decreasing uncovered elements by %d produces zero at [%d,%d]\n",s,k,l);
                                if (rowMate[l]<0) {
                                    for (j=l+1;j<n;j++) {
                                        if (slack[j]==0) {
                                            colInc[j] += s;
                                        }
                                    }
                                    break breakthru;
                                }
                                else {
                                    parentRow[l] = k;
                                    //if (verbose)
                                    //  fprintf(stderr, "node %d: row %d==col %d--row %d\n",t,row_mate[l],l,k);
                                    unchosenRow[ t++ ] = rowMate[l];
                                }
                                // End look at a new zero 22
                            }
                        }
                        else {
                            colInc[l] += s;
                        }
                    }
                    // End introduce a new zero into the matrix 21
                }

                // Begin update the matching 20
                //if (verbose)
                //fprintf(stderr, "Breakthrough at node %d of %d!\n",q,t);
                while (true) {
                    j=colMate[k];
                    colMate[k]=l;
                    rowMate[l]=k;
                    //if (verbose)
                    //  fprintf(stderr, "rematching col %d==row %d\n",l,k);
                    if (j<0) {
                        break;
                    }
                    k=parentRow[j];
                    l=j;
                }
                // End update the matching 20

                if( --unmatched == 0 ) {
                    break done;
                }

                // Begin get ready for another stage 17
                t=0;
                for (l=0;l<n;l++) {
                    parentRow[l]= -1;
                    slack[l] = Double.POSITIVE_INFINITY;
                }
                for (k=0;k<m;k++) {
                    if (colMate[k]<0)
                    {
                        //if (verbose)
                        //  fprintf(stderr, "node %d: unmatched row %d\n",t,k);
                        unchosenRow[t++]=k;
                    }
                }
                // End get ready for another stage 17
            }
        }

        // Begin doublecheck the solution 23
//        for (k=0;k<m;k++) {
//            for (l=0;l<n;l++) {
//                if (cost[k][l]<row_dec[k]-col_inc[l]) {
//                    exit(0);
//                }
//            }
//            for (k=0;k<m;k++) {
//                l=colMate[k];
//                if (l<0 || cost[k][l]!=row_dec[k]-col_inc[l]) {
//                    exit(0);
//                }
//            }
//            k=0;
//            for (l=0;l<n;l++) {
//                if (col_inc[l]) {
//                    k++;
//                }
//                if (k>m) {
//                    exit(0);
//                }
//            }
//        }
        // End doublecheck the solution 23*/
        // End Hungarian algorithm 18

        for (int i=0;i<m;++i) {
            assigned[i][colMate[i]] = true;
        }

    }
    
}
