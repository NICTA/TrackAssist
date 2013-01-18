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

package au.com.nicta.ct.experiment.graphics.canvas.microwells;

import au.com.nicta.ct.ui.style.CtStyle;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Alan
 */
public class CtMicrowellPainter {

    public CtMicrowell model;

    CtStyle style = new CtStyle();

    private Point2D.Double point = new Point2D.Double();

    public CtMicrowellPainter( CtMicrowell model ) {
        this.model = model;
    }

    public void paint( Graphics2D g, CtViewpointZoomCanvas zc ) {
        Path2D.Double path = model.getPath();
        Path2D.Double pathOnScreen = zc.toScreen( path );

        Point2D.Double centre = new Point2D.Double();

        point = model.getVertex(0, point);
        pathOnScreen.moveTo( zc.toScreenX(point.x),
                    zc.toScreenY(point.y) );

        centre.x += pathOnScreen.getCurrentPoint().getX();
        centre.y += pathOnScreen.getCurrentPoint().getY();

        for( int i = 1; i < model.sides; ++i) {
            point = model.getVertex(i, point);
            pathOnScreen.lineTo( zc.toScreenX(point.x),
                        zc.toScreenY(point.y) );

            centre.x += pathOnScreen.getCurrentPoint().getX();
            centre.y += pathOnScreen.getCurrentPoint().getY();
        }
        pathOnScreen.closePath();

        double reciprocal = 1.0 / model.sides;
        centre.x *= reciprocal;
        centre.y *= reciprocal;

        g.setPaint(style.strokePaint);
        g.setStroke(style.stroke);
        g.draw( pathOnScreen );
        g.setColor( CtConstants.NictaYellow );
        g.setStroke( new BasicStroke( 3 ) );
        g.drawString( model.getName(), (int)centre.x, (int)centre.y );
    }

    public Stroke getStyleStroke() {
        return style.stroke;
    }

    public Paint getStyleStrokePaint() {
        return style.strokePaint;
    }

    public void setStyleStroke( Stroke stroke ) {
        style.stroke = stroke;
    }

    public void setStyleStrokePaint( Color color ) {
        style.strokePaint = color;
    }

}
