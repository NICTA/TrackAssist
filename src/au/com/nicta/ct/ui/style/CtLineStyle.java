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

package au.com.nicta.ct.ui.style;

import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 *
 * @author davidjr
 */
public class CtLineStyle {

    public Stroke _s;
    public Color _c;

    public CtLineStyle( Stroke s, Color c ) {
        this._s = s;
        this._c = c;
    }

    public static CtLineStyle faint() {
        int a = 50;
        return new CtLineStyle( new BasicStroke( 2 * CtSubPixelResolution.unitsPerNaturalPixel ), new Color( 255, 255, 0, a ) );
    }
    public static CtLineStyle background() {
        int a = 50;
        return new CtLineStyle( new BasicStroke( 3 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 255, 255, 0, a ) );
    }
    public static CtLineStyle normal() {
        int a = 127;
        return new CtLineStyle( new BasicStroke( 3 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 255, 255, 0, a ) );
    }
    public static CtLineStyle focus() { // colours reversed
        int a = 127;
        return new CtLineStyle( new BasicStroke( 4 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 255, 127, 255, a ) );
    }
    public static CtLineStyle selected() { // colours reversed
        int a = 127;
        return new CtLineStyle( new BasicStroke( 4 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 255, 127, 255, a ) );
    }
    public static CtLineStyle attention() { // colours reversed
        int a = 127;
        return new CtLineStyle( new BasicStroke( 4 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ), new Color( 255, 0, 255 ) );
    }

}
