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

/**
 * Define several styles for painting polygons:
 *  - faint (less obvious view, still visible but not distracting)
 *  - background (ordinary view)
 *  - focus (a transiently highlighted state)
 *  - selected (in a selected state, a subset of other polygons)
 *  - attention (really in your face, bright)
 * @author davidjr
 */
public class CtPolygonStyle {

    public enum CtPolygonStroke {
        THIN,
        MED,
        THICK
    }

    public static BasicStroke THIN  = new BasicStroke(1 * CtSubPixelResolution.unitsPerNaturalPixel);  // saves on the number of objects
    public static BasicStroke MED   = new BasicStroke(3 * CtSubPixelResolution.unitsPerNaturalPixel );  // saves on the number of objects
    public static BasicStroke THICK = new BasicStroke(5 * CtSubPixelResolution.unitsPerNaturalPixel, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND );  // saves on the number of objects

    public BasicStroke borderStroke;
    public Paint borderPaint;
    public Paint fillPaint;

    public static CtPolygonStyle faint() {
        int a = 50;
        return new CtPolygonStyle( CtPolygonStroke.THIN, new Color( 0, 255, 0, a ), new Color( 255, 255, 0, a ) );
    }

    public static CtPolygonStyle background() {
        int a = 127;
        return new CtPolygonStyle( CtPolygonStroke.THIN, new Color( 0, 255, 0, a ), new Color( 255, 255, 0, a ) );
    }

    public static CtPolygonStyle normal() {
        int a = 127;
        return new CtPolygonStyle( CtPolygonStroke.MED, new Color( 0, 255, 0, a ), new Color( 255, 255, 0, a ) );
    }

    public static CtPolygonStyle focus() { // colours reversed
        int a = 127;
        return new CtPolygonStyle( CtPolygonStroke.THICK, new Color( 255, 255, 0, a ), new Color( 0, 255, 0, a ) );
    }

    public static CtPolygonStyle selected() {
        int a = 180;
        return new CtPolygonStyle( CtPolygonStroke.MED, new Color( 0, 255, 0, a ), new Color( 255, 255, 0, a ) );
    }

    public static CtPolygonStyle attention() {
        int a = 180;
        return new CtPolygonStyle( CtPolygonStroke.THICK, new Color( 255, 255, 0 ), new Color( 255, 255, 0, a ) );
    }

    public CtPolygonStyle( CtPolygonStroke ps, Color border, Color fill ) {
        if( ps == CtPolygonStroke.THIN ) {
            borderStroke = CtPolygonStyle.THIN;
        }
        else if( ps == CtPolygonStroke.MED ) {
            borderStroke = CtPolygonStyle.MED;
        }
        else {
            borderStroke = CtPolygonStyle.THICK;
        }
        
        fillPaint = fill;
        borderPaint = border;
    }

}
