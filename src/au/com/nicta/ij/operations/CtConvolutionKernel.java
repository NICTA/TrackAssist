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

package au.com.nicta.ij.operations;

import ij.process.FloatProcessor;

/**
 *
 * @author davidjr
 */
public class CtConvolutionKernel {

    public int _wKernel;
    public int _hKernel;
    public float[] _kernel;

    public CtConvolutionKernel() {

    }

//    public static void postConvolution( FloatProcessor fp, float minValue, float maxValue ) {
//
//        float min = Float.MAX_VALUE;
//        float max = Float.MIN_VALUE;
//        int w = fp.getWidth();
//        int h = fp.getHeight();
//
//        for( int j = 0; j < h; ++j ) {
//            for( int i = 0; i < w; ++i ) {
//                float r = fp.get( i, j );
//                if( r < min ) min = r;
//                if( r > max ) max = r;
//            }
//        }
//
//        //System.out.println( "min="+min+" max="+max );
//
//        double idealRange = maxValue - minValue +1.0f;
//        double range = max - min;
//        double reciprocal = 1.0 / range;
//
//        for( int j = 0; j < h; ++j ) {
//            for( int i = 0; i < w; ++i ) {
//                float r = fp.get( i, j );
//                r -= min;
//                r *= reciprocal;
//                r *= idealRange;
//                r += minValue;
//                r = (float)Math.min( maxValue, r );
//                r = (float)Math.max( minValue, r );
//                r = (float)( maxValue - r ); // something weird about imageJ convolution!? Need to do this
//                fp.setf( i,j, r );
//            }
//        }
//
//    }

    public void gaussianRingKernel( double radialOffset, double sigma ) {

        int gSize = gaussianKernelSize( sigma );
        int kSize = ( gSize >> 1 ) + (int)radialOffset;

        if( ( kSize % 2 ) == 0 ) {
            --kSize;
        }

        _wKernel = kSize;
        _hKernel = kSize;
        _kernel = new float[ kSize * kSize ];

        double c = (double)( ( kSize / 2 ) -1 );
        double r1 = (double)( ( 1.0 / ( 2.0 * Math.PI * ( sigma*sigma ) ) ) );
        double r2 = (double)(           2.0 *           ( sigma*sigma ) );

        for( int j = 0; j < kSize; ++j ) {
            for( int i = 0; i < kSize; ++i ) {

              double dx = ( (double)( i ) ) - c -1.0;
              double dy = ( (double)( j ) ) - c -1.0;
              double dSq = ( (dx*dx) + (dy*dy) );
              double d = Math.sqrt( dSq );
              double dM = Math.abs( d - radialOffset );//dSq -= radialOffset; //

              double r3 = -( dM / r2 );
                     r3 = Math.pow( Math.E, r3 ); // or exp( r3 )

              double g = r1 * r3;

              _kernel[ j * kSize + i ] = (float)g;
            }
        }
    }

    public void inverseDoGKernel( float[] g1, float[] g2 ) {

        assert( g1.length == g2.length );

        _kernel = new float[ g1.length ];
        _wKernel = (int)Math.sqrt( g1.length );
        _hKernel = _wKernel;

        assert( ( _wKernel * _hKernel ) == g1.length );

        for( int n = 0; n < g1.length; ++n ) {

            _kernel[ n ] = 1.0f - ( g1[ n ] - g2[ n ] );
        }
    }

    public void gausssianKernel( int size, double sigma ) {
        _kernel = new float[ size * size ];
        _wKernel = size;
        _hKernel = size;

        double c = (double)( ( size / 2 ) -1 );
        double r1 = (double)( ( 1.0 / ( 2.0 * Math.PI * ( sigma*sigma ) ) ) );
        double r2 = (double)(           2.0 *           ( sigma*sigma ) );

        for( int j = 0; j < size; ++j ) {
            for( int i = 0; i < size; ++i ) {

              double x = ( (double)( i ) ) - c -1.0;
              double y = ( (double)( j ) ) - c -1.0;

              double r3 = -( ( (x*x) + (y*y) ) / r2 );
                     r3 = Math.pow( Math.E, r3 ); // or exp( r3 )

              double g = r1 * r3;

              _kernel[ j * size + i ] = (float)g;
            }
        }
    }

    public static int gaussianKernelSize( double sigma ) {
	// From "Image Processing, Analysis and Machine Vision", pp. 84:
	//   'Pixels more distant from the center of the operator have smaller
	//   influence, and pixels farther than 3 \sigma from the center have
	//   neglible influence.'
        // Also, the kernel should (ideally) have odd dimension.
        return( 1 + 2 * ( (int)( 3.0 * sigma ) ) );
    }
}
