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

package au.com.nicta.ct.graphics.canvas.zoom.polygons;

import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import java.awt.geom.AffineTransform;

/**
 *
 * @author davidjr
 */
public class CtSubPixelResolution {

    public static final int unitsPerNaturalPixel = 5; // how many units is contained per real pixel, controls sub-pixel resolution

    public static AffineTransform getAffineToScreen( CtViewpointZoomCanvas zc, AffineTransform returnValue ) {
        returnValue = zc.getAffineToScreen( returnValue );
        returnValue.scale( 1.0/unitsPerNaturalPixel,
                           1.0/unitsPerNaturalPixel  );
        return returnValue;
    }

    public static AffineTransform getAffineToNatural( AffineTransform returnValue ) {
        returnValue.scale( 1.0/unitsPerNaturalPixel,
                           1.0/unitsPerNaturalPixel  );
        return returnValue;
    }

    public static int getSubPixelNaturalX( CtViewpointZoomCanvas zc, int x0 ) {
        int x = (int)Math.rint( zc.toNaturalX( x0 ) * CtSubPixelResolution.unitsPerNaturalPixel );
        return x;
    }

    public static int getSubPixelNaturalY( CtViewpointZoomCanvas zc, int y0 ) {
        int y = (int)Math.rint( zc.toNaturalY( y0 ) * CtSubPixelResolution.unitsPerNaturalPixel );
        return y;
    }
}
