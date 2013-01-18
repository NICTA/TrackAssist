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

import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.util.HashMap;

/**
 *
 * @author davidjr
 */
public class CtWatershed {

//    public static class CtBackgroundMask {
//        public CtBackgroundMask(){}
//        public ImageProcessor mask;
//        public int backgroundMaskValue = 0;
//        public int backgroundLabelValue = 0;
//    }
    
    public static CtLabelledImage watershed( ImageProcessor gradient, ImageProcessor mask, int maskedValue ) throws IllegalArgumentException {

        int backgroundLabel = 0;
        CtLabelledImage lm = findLocalMinima( gradient, mask, maskedValue, backgroundLabel );

        int w = gradient.getWidth();
        int h = gradient.getHeight();
        int iMax = w -1;
        int jMax = h -1;

        lm.segmented = new ShortProcessor( w,h );
        lm.backgroundLabel = backgroundLabel;
        
//        CtBackgroundMask bm = new CtBackgroundMask();
//        bm.mask = mask;
//        bm.backgroundMaskValue = backgroundMask;
//        bm.backgroundLabelValue = backgroundLabel;
        
        for( int j = 1; j < jMax; ++j ) {
            for( int i = 1; i < iMax; ++i ) {
                label( i,j, lm );
            }
        }

        return lm;
    }


    private static int label( int i, int j, CtLabelledImage lm ) {//, CtBackgroundMask bm ) {

        // if masked out, return mask value:
//        int maskValue = bm.mask.get( i,j );
//
//        if( maskValue == bm.backgroundMaskValue ) {
//            lm.segmented.set( i,j, bm.backgroundLabelValue );
//            return bm.backgroundLabelValue;
//        }

        // if already determined, return value:
        int segmentedValue = lm.segmented.get( i, j );//(*iRandomSegmented);

        if( segmentedValue != 0 ) {
            return segmentedValue;
        }

        // otherwise, follow the gradient:
        int nMinima = lm.directions.get( i, j );//(*iRandomMinima); // follow the gradient

        switch( nMinima )
        {
            case 0 : segmentedValue = 0; break;
            case 1 : segmentedValue = label( i -1, j -1, lm ); break;
            case 2 : segmentedValue = label( i,    j -1, lm ); break;
            case 3 : segmentedValue = label( i +1, j -1, lm ); break;
            case 4 : segmentedValue = label( i -1, j,    lm ); break;
            case 5 : //iRandomLabelled = iBeginLabelled + nOffset; // ... until we find a label!
                     segmentedValue = lm.labels.get( i,j ); break;//(*iRandomLabelled); break;
            case 6 : segmentedValue = label( i +1, j,    lm ); break;
            case 7 : segmentedValue = label( i -1, j +1, lm ); break;
            case 8 : segmentedValue = label( i,    j +1, lm ); break;
            case 9 : segmentedValue = label( i +1, j +1, lm ); break;
        }

        lm.segmented.set( i,j, segmentedValue ); //(*iRandomSegmented) = nSegmentedValue;

        int count = 1;

        Integer c = lm.labelCounts.get( segmentedValue );

        if( c != null ) {
            count += c;
        }

        lm.labelCounts.put( segmentedValue, count );

        return segmentedValue;
    }
    
    public static CtLabelledImage findLocalMinima( ImageProcessor gradient, ImageProcessor mask, int maskValue, int maskLabel ) throws IllegalArgumentException {
        int w = gradient.getWidth();
        int h = gradient.getHeight();

        ShortProcessor labelled = new ShortProcessor( w,h );
        ByteProcessor direction = new ByteProcessor( w,h );
//        labelled.setValue( 0 );

        int nextLabel = 1;

        // 1) Look at the neighbours of each pixel in the gradient image; if it is
        //    the lowest gradient, make it a local minima.
        //
        //    If the current (central) pixel is not a local minima, make it point to
        //    the lowest pixel in the surrounding 8-neighbourhood.
        //
        //   1 2 3
        //   \ | /
        //  4- 5 -6    0 = none
        //   / | \
        //   7 8 9
        //___________________________________________________________________________
        int m1 = maskValue -1;
        int m2 = maskValue -1;
        int m3 = maskValue -1;
        int m4 = maskValue -1;
        int m5 = maskValue -1;
        int m6 = maskValue -1;
        int m7 = maskValue -1;
        int m8 = maskValue -1;
        int m9 = maskValue -1;

        int iMax = w -1;
        int jMax = h -1;

        int[] minima = new int[ 2 ];

        for( int j = 1; j < jMax; ++j ) {
            for( int i = 1; i < iMax; ++i ) {
                int g1 = gradient.get( i-1, j-1 );
                int g2 = gradient.get( i  , j-1 );
                int g3 = gradient.get( i+1, j-1 );
                int g4 = gradient.get( i-1, j   );
                int g5 = gradient.get( i  , j   );
                int g6 = gradient.get( i+1, j   );
                int g7 = gradient.get( i-1, j+1 );
                int g8 = gradient.get( i  , j+1 );
                int g9 = gradient.get( i+1, j+1 );

                if( mask != null ) {
                    m1 = mask.get( i-1, j-1 );
                    m2 = mask.get( i  , j-1 );
                    m3 = mask.get( i+1, j-1 );
                    m4 = mask.get( i-1, j   );
                    m5 = mask.get( i  , j   );
                    m6 = mask.get( i+1, j   );
                    m7 = mask.get( i-1, j+1 );
                    m8 = mask.get( i  , j+1 );
                    m9 = mask.get( i+1, j+1 );
                }

                minima[ 0 ] = g5;
                minima[ 1 ] = 5;

                testMinima( minima, g1, 1, m1, maskValue ); // won't consider maskValue as possible descent
                testMinima( minima, g2, 2, m2, maskValue );
                testMinima( minima, g3, 3, m3, maskValue );
                testMinima( minima, g4, 4, m4, maskValue );
                testMinima( minima, g6, 6, m6, maskValue );
                testMinima( minima, g7, 7, m7, maskValue );
                testMinima( minima, g8, 8, m8, maskValue );
                testMinima( minima, g9, 9, m9, maskValue );

                direction.set( i,j, minima[ 1 ] );

                int thisLabel = 0;

                if( m5 == maskValue ) {
                    thisLabel = maskLabel;
                }
                else {
                    if( minima[ 1 ] == 5 ) { // if( local Minima )
                        thisLabel = nextLabel;

                        ++nextLabel;
                    }
                }
                
                labelled.set( i, j, thisLabel );
            }
        }

        CtLabelledImage lm = new CtLabelledImage();
        lm.nextLabel = nextLabel;
        lm.labels = labelled;
        lm.directions = direction;

        return lm;
    }

    private static void testMinima( int[] minima, int gradient, int direction, int mask, int maskValue ) {
        if( mask == maskValue ) {
            return; // ignore this one
        }

        int gMin = minima[ 0 ];

        if( gradient > gMin ) {
            return;
        }

        int gDir = minima[ 1 ];

        if(    ( gradient  < gMin )     // if less than, or equal AND dir is higher
            || ( direction > gDir ) ) { // 
            minima[ 0 ] = gradient;
            minima[ 1 ] = direction;
        }
    }
}

