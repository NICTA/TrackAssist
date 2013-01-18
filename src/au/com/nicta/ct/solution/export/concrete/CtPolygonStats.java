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

import java.awt.Polygon;
import java.awt.geom.Point2D;

/**
 *
 * @author alan
 */
public class CtPolygonStats {

    public static double area( Polygon p ) {
        double sum = 0;
        int[] x = p.xpoints;
        int[] y = p.ypoints;
        for( int i = 0; i < p.npoints-1; ++i ) {
            sum += x[i]*y[i+1] - y[i]*x[i+1];
        }
        return sum/2;
    }

    public static Point2D.Double centroid( Polygon p ) {
        return centroid( p, area(p) );
    }

    public static Point2D.Double centroid(
            Polygon p,
            double area )
    {
        return centroid(p, area, new Point2D.Double());
    }

    public static Point2D.Double centroid(
            Polygon p,
            double area,
            Point2D.Double c ) {
        double cx = 0;
        double cy = 0;
        int[] x = p.xpoints;
        int[] y = p.ypoints;
        for( int i = 0; i < p.npoints-1; ++i ) {
            double f = x[i]*y[i+1] - x[i+1]*y[i];
            cx += f * ( x[i] + x[i+1] );
            cy += f * ( y[i] + y[i+1] );
        }

        c.x = cx/(6*area);
        c.y = cy/(6*area);

        return c;
    }

    public static double[] secondMoment(Polygon p) {
        return secondMoment(p, new double[3]);
    }

    // returns double[0] -- Ix, second moment about the x axis, i.e. xdxdy
    // returns double[1] -- Iy, second moment about the y axis, i.e. ydxdy
    // returns double[2] -- Ixy, xydxdy
    public static double[] secondMoment(Polygon p, double[] result) {
        double Ix = 0;
        double Iy = 0;
        double Ixy = 0;
        int[] x = p.xpoints;
        int[] y = p.ypoints;
        for( int i = 0; i < p.npoints-1; ++i ) {
            double f = x[i]*y[i+1] - x[i+1]*y[i];
            Ix += f * ( y[i]*y[i] + y[i]*y[i+1] + y[i+1]*y[i+1]);
            Iy += f * ( x[i]*x[i] + x[i]*x[i+1] + x[i+1]*x[i+1]);
            Ixy += f * ( x[i]*y[i+1] + 2*x[i]*y[i] + x[i+1]*y[i+1] + x[i+1]*y[i]);
        }

        result[0] = Ix  / 12;
        result[1] = Iy  / 12;
        result[2] = Ixy / 24;

        return result;
    }

}
