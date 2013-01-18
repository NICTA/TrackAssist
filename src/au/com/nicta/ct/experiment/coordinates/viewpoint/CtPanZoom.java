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

package au.com.nicta.ct.experiment.coordinates.viewpoint;

import au.com.nicta.ct.orm.mvc.change.concrete.CtDiscreteScalingModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author Alan
 */
public class CtPanZoom extends CtDiscreteScalingModel implements Cloneable {//CtChangeModel {

    public static final String EVT_OFFSET_CHANGED = "panOffsetChanged";

    private Point2D.Double offset = new Point2D.Double(0, 0);

    public CtPanZoom() {
        super();
    }

    @Override protected Object clone() throws CloneNotSupportedException {

        CtPanZoom pz = (CtPanZoom)super.clone();

        pz.offset = new Point2D.Double( this.offset.x, this.offset.y );
//        pz.changeSupport = new
        return pz;
    }

    public AffineTransform getAffineToScreen(AffineTransform aff) {
//        double scale = getZoomScale();
        // Last applied will be applied first.
        aff.setToTranslation(-getOffsetX(), -getOffsetY()); // offset is AFTER scaling
        aff.scale(scale, scale);
        return aff;
    }

    public AffineTransform getAffineToNatural(AffineTransform aff) {
        double scale = 1.0 / getScale();
        // Last applied will be applied first.
        aff.translate(getOffsetX(), getOffsetY());
        aff.setToScale(scale, scale);
        return aff;
    }

    public void setOffsetX(double x) {
        setOffset( x, offset.y );
    }

    public void setOffsetY(double y) {
        setOffset( offset.x, y );
    }

    public void setOffset(double x, double y) {
        if(    offset.x != x
            || offset.y != y ) {

            offset.x = x;
            offset.y = y;
        }

        changeSupport.fire( EVT_OFFSET_CHANGED );
    }

    public Point2D.Double getOffset() {
        return new Point2D.Double(offset.x, offset.y);
    }

    public double getOffsetX() {
        return offset.x;
    }

    public double getOffsetY() {
        return offset.y;
    }
}
