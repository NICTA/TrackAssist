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

package au.com.nicta.ct.ui.swing.graphics;

import java.awt.Polygon;

/**
 *
 * @author Alan
 */
public class CtRaster {

    public enum Connectedness {
        FOUR  { @Override public String toString() { return "Connectedness.FOUR"; } },
        EIGHT { @Override public String toString() { return "Connectedness.EIGHT"; } },

    };

    public static Polygon circle( int centreX, int centreY, int radius ) {
        return circle(centreX, centreY, radius, Connectedness.FOUR);
    }

    public static Polygon circle( int centreX, int centreY, int radius, Connectedness connectedness ) {
        int n;

        switch( connectedness ) {
        case FOUR:
            n = radius*8;
            break;
        case EIGHT:
            n = radius*2*8;
            break;
        default:
            throw new UnsupportedOperationException(connectedness.toString());
        }

        int[] x = new int[n];
        int[] y = new int[n];

        int cnt = circleOne4th( radius, x, y, connectedness == Connectedness.FOUR );

        int idx = cnt;

        // 2nd quarter
        for( int i = 0; i < cnt; ++i, ++idx ) {
            x[idx] = -y[i];
            y[idx] =  x[i];
        }
        // 3rd quarter
        for( int i = 0; i < cnt; ++i, ++idx ) {
            x[idx] = -x[i];
            y[idx] = -y[i];
        }
        // 4th quarter
        for( int i = 0; i < cnt; ++i, ++idx ) {
            x[idx] =  y[i];
            y[idx] = -x[i];
        }

        Polygon p = new Polygon( x, y, idx );
        p.translate( centreX, centreY );
        
        return p;
//        return new Polygon(x, y, idx);
    }

    static int circleOne4th( int radius, int[] x, int[] y, boolean fourConnected ) {
        // Dry run 
        int error = -radius;
        int px = radius;
        int py = 0;
        int idx = 0;
        if( fourConnected ) {
            while( px >= py ) {
                x[idx] = px;
                y[idx] = py;

                error += py;
                ++py;
                error += py;

                if( error >= 0 ) {
                  --px;
                  error -= px;
                  error -= px;
                }
                ++idx;
            }
        }
        else {
            while( px >= py ) {
                x[idx] = px;
                y[idx] = py;

                error += py;
                ++py;
                error += py;

                if( error >= 0 ) {
                  --px;
                  error -= px;
                  error -= px;
                  if( px >= py ) {
                      ++idx;
                      x[idx] = px;
                      y[idx] = py-1;
                  }
                }
                ++idx;
            }

            if( x[idx-1] != y[idx-1] ) {
                x[idx] = x[idx-1]-1;
                y[idx] = y[idx-1];
                ++idx;
            }

        }

        int startFrom = idx - 1;
        if( x[startFrom] == y[startFrom] ) {
            startFrom = idx - 2; // last point will duplicate in a reflexion, ignore it
        }

        for( int i = startFrom; i > 0; --i, ++idx ) {
            x[idx] = y[i];
            y[idx] = x[i];
        }

        return idx;
    }

}











