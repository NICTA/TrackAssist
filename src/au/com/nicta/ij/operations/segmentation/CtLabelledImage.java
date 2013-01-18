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

package au.com.nicta.ij.operations.segmentation;

import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author davidjr
 */
public class CtLabelledImage {

    public int nextLabel;
    public int backgroundLabel;
    public ShortProcessor labels;
    public ByteProcessor directions;
    public ShortProcessor segmented;
    public HashMap< Integer, Integer > labelCounts = new HashMap< Integer, Integer >();

    public CtLabelledImage() {

    }

    public int getNextLabel() {
        int label = nextLabel;
        ++nextLabel;
        return label;
    }

    public void filterComponentsWithMask( ImageProcessor mask, int retainThreshold ) {

        // 2 passes: first, work out which components to filter. second, remove those components.
        HashSet< Integer > retainedLabels = new HashSet< Integer >();

        int w = segmented.getWidth();
        int h = segmented.getHeight();

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int maskValue = mask.get( i,j );

                if( maskValue < retainThreshold ) {
                    continue;
                }

                int label = segmented.get( i,j );

                retainedLabels.add( label );
            }
        }

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int label = segmented.get( i,j );

                if( retainedLabels.contains( label ) ) {
                    continue;
                }

                segmented.set( i,j, backgroundLabel );  // filter this component
            }
        }

    }

    public void filterSmallComponents( int minSize ) {
        int w = segmented.getWidth();
        int h = segmented.getHeight();

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int label = segmented.get( i,j );
                int count = labelCounts.get( label );

                if( count < minSize ) {
                    segmented.set( i,j, backgroundLabel );
                }

            }
        }
    }

    public void mergeAdjacentMinima( int minDist ) {
        int w = segmented.getWidth();
        int h = segmented.getHeight();

        // 1. build adjacency list by finding minima positions:
        HashMap< Integer, CtAbstractPair< Integer, Integer > > minimaCoordinates = new HashMap< Integer, CtAbstractPair< Integer, Integer > >();

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int n = labels.get( i,j );

                if( n == 0 ) continue; // background

                assert( minimaCoordinates.get( n ) == null );

                minimaCoordinates.put( n, new CtAbstractPair< Integer, Integer >( i,j ) );
            }
        }

        // Now compute distances between minima:
        HashMap< Integer, Integer > minimaLabelMappings = new HashMap< Integer, Integer >();

        for( int n1 = 1; n1 < nextLabel; ++n1 ) {

            CtAbstractPair< Integer, Integer > ap1 = minimaCoordinates.get( n1 );

            if( ap1 == null ) continue;

            for( int n2 = 1; n2 < nextLabel; ++n2 ) {

                if( n2 == n1 ) continue;

                CtAbstractPair< Integer, Integer > ap2 = minimaCoordinates.get( n2 );

                if( ap2 == null ) continue;

                int dx = Math.abs( ap1._first  - ap2._first );
                int dy = Math.abs( ap1._second - ap2._second );

                if( dx > minDist ) continue;
                if( dy > minDist ) continue;

                // ok these minima are too close and are not the same minima.
                if( n1 < n2 ) {
                    minimaLabelMappings.put( n2, n1 ); // map to lowest label
                }
                else {
                    minimaLabelMappings.put( n1, n2 );
                }
            }
        }

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int label = segmented.get( i,j );

                if( label == 0 ) continue; // ignore & avoid lots of hash lookups

                Integer mapping = minimaLabelMappings.get( label );

                if( mapping == null ) continue; // unchanged

                segmented.set( i,j, mapping );
            }
        }
    }

    public void showSegmented( CtImageResult ir ) {

        ByteProcessor bp = (ByteProcessor)labels.convertToByte( false );
        
        double x = 1.0 / (double)nextLabel; // e.g. if nextlabel = 42, 255/42 = 6.07 = 6. 42*6 = 252  255/1740=
               x *= 255.0;

        int w = segmented.getWidth();
        int h = segmented.getHeight();

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {
                int n = segmented.get( i,j );
                double r = (double)n * x;
                bp.set( i,j, (int)r ); // 28 = highest int that fits into byte
//                if( n > 100 ) {
//                    int g = 0;
//                }
            }
        }

        ir.setIP( bp );
    }

    public void showMinima( CtImageResult ir ) {

        double x = 1.0 / (double)nextLabel; // e.g. if nextlabel = 42, 255/42 = 6.07 = 6. 42*6 = 252  255/1740=
               x *= 255.0;

        ByteProcessor bp = (ByteProcessor)labels.convertToByte( false );

        int w = labels.getWidth();
        int h = labels.getHeight();

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {
                int n = labels.get( i,j );
                double r = (double)n * x;
                bp.set( i,j, (int)r ); // 28 = highest int that fits into byte
            }
        }

        ir.setIP( bp );
    }

    public void showDirections( CtImageResult ir ) {

        ByteProcessor bp = (ByteProcessor)directions.convertToByte( false );

        int w = directions.getWidth();
        int h = directions.getHeight();
//int dMin = 10;
//int dMax = 0;
        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {
                int n = directions.get( i,j );
//if( n < dMin ) dMin = n;
//if( n > dMax ) dMax = n;
                bp.set( i,j, n * 28 ); // 28 = highest int that fits into byte
            }
        }
//System.out.println( "dmin= "+dMin + " dmax="+dMax );
        ir.setIP( bp );
    }

}
