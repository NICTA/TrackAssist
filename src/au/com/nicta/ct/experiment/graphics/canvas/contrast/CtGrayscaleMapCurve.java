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

package au.com.nicta.ct.experiment.graphics.canvas.contrast;

import au.com.nicta.ct.ui.style.CtColorPalette;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasPainter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Alan
 */
public class CtGrayscaleMapCurve implements CtCanvasPainter {

    CtCanvasLayer layer;
    CtXYMultiSlider slider;
    Color curveColor = CtColorPalette.NICTA_GREEN;

    CtGrayscaleMapCurve(CtCanvasLayer layer, CtXYMultiSlider slider) {
        this.layer = layer;
        this.slider = slider;
        layer.addPainter(this);
    }

    @Override public void paint(Graphics2D g, CtCanvasLayer l) {
        if( slider.getNumPoints() < 2 ) {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setPaint( curveColor );
        g.setStroke(new BasicStroke(1));

        for( int i = 0; i < slider.getNumPoints()-1; ++i ) {
            g.drawLine(
                    slider.getScreenX(i  ),
                    slider.getScreenY(i  ),
                    slider.getScreenX(i+1),
                    slider.getScreenY(i+1) );
        }
    }

}
