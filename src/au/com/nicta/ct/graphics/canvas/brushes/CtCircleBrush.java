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

import au.com.nicta.ct.ui.swing.graphics.CtRaster;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Alan
 */
public class CtCircleBrush extends CtBrush {

    // passed in
//    CtNewDetectionMode newDetectionMode;
//    CtSpinnerQuantisedNumberModel cursorSizeSpinnerModel;

    // members
//    CtCursor cursor;

    public CtCircleBrush() {

        resize( 5.0 );
    }

    void cursorSizeChanged(ChangeEvent e) {

    }

    public void createPolygon( double size ) {
//        double radius = cursorSizeSpinnerModel.getNumber().doubleValue();

        if( size < 1.0 ) {
            return;
        }

        _p = CtRaster.circle(
                0,
                0,
                (int)(size * CtSubPixelResolution.unitsPerNaturalPixel),
                CtRaster.Connectedness.EIGHT);
        
//        cursor.repaint();
    }

    public void resize( double size ) {
        createPolygon( size );
    }
    
//    void activate() {
//        cursorSizeChanged(null);
//        cursor.setActive(true);
//    }
//
//    void deactivate() {
//        cursor.setActive(false);
//    }

//    void mousePressed(MouseEvent e) {
////        int x = (int)Math.rint( zc.toNaturalX(e.getX()) * cursor.cursorPolygon.unitsPerNaturalPixel );
////        int y = (int)Math.rint( zc.toNaturalY(e.getY()) * cursor.cursorPolygon.unitsPerNaturalPixel );
////
////        CtDetection detection = new CtDetection();
////        detection.detectionPolygon = new CtDetectionPolygon(cursor.cursorPolygon);
////        detection.detectionPolygon.polygon.translate(x, y);
////
////        newDetectionMode.addDetection(detection);
//        System.out.print
//    }
//
//    @Override public void addButton(final JToggleButton button) {
//        super.addButton(button);
//    }

}













