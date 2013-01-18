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

package au.com.nicta.ct.graphics.canvas.brushes;

import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

/**
 * Was: CtCursor in alan's code
 * @author davidjr
 */
public abstract class CtBrush {

//    public int _unitsPerNaturalPixel = 5; // how many units is contained per real pixel, controls sub-pixel resolution
    public double _size = 1.0;
    public Polygon _p;
    public Stroke _s = CtStyle.THICK_STROKE;
    public Color _c = CtStyle.BRIGHT_GREEN_TRANSLUCENT;

    public CtBrush( ) {

    }

    public void setPolygon( Polygon p ) {
        _p = new Polygon(
            Arrays.copyOf( p.xpoints, p.npoints ),
            Arrays.copyOf( p.ypoints, p.npoints ),
            p.npoints );
    }

    void paint( Graphics2D g, CtViewpointZoomCanvas zc, int mouseX, int mouseY ) {

        if( _p == null ) {
            return;
        }

        AffineTransform old = g.getTransform();

        g.setColor( _c );
        g.setStroke( _s );

        g.translate( mouseX, mouseY );
        g.scale( zc.getZoomScale(), zc.getZoomScale() );
        g.scale( 1.0/CtSubPixelResolution.unitsPerNaturalPixel,
                 1.0/CtSubPixelResolution.unitsPerNaturalPixel );
        g.draw( _p );

        g.setTransform(old);
    }

    public abstract void resize( double size );

// davE: put here?
//    Area getCursorArea() {
//        if( ( _b == null ) || ( _b._p == null ) ) {
//            return null;
//        }
//
//        Area a = new Area( _b._p );
//
//        int x = (int)Math.rint( _bl._zc.toNaturalX(_bl._mouseX) * _b.unitsPerNaturalPixel );
//        int y = (int)Math.rint( _bl._zc.toNaturalY(_bl._mouseY) * _b.unitsPerNaturalPixel );
//
//        affTemp.setToTranslation(x, y);
//        a.transform(affTemp);
//
//        return a;
//    }

}
