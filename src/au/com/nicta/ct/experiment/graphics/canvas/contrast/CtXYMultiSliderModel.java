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

import au.com.nicta.ct.orm.mvc.change.CtChangeModel;

/**
 *
 * @author Alan
 */
public class CtXYMultiSliderModel extends CtChangeModel {

    public static final String EVT_CHANGED = "CtXYMultiSliderModelChanged";

    CtScalarsModel modelX;
    CtScalarsModel modelY;

    CtXYMultiSliderModel(double minX, double maxX, double minY, double maxY, boolean orderedX, boolean orderedY) {
        super(null); // not forwarding any events from other models
        modelX = new CtScalarsModel( minX, maxX, orderedX );
        modelY = new CtScalarsModel( minY, maxY, orderedY );
    }

    double getValueX(int idx) {
        return modelX.getValue(idx);
    }

    double getValueY(int idx) {
        return modelY.getValue(idx);
    }

    double getMinX() {
        return modelX.getMin();
    }

    double getMaxX() {
        return modelX.getMax();
    }

    double getMinY() {
        return modelY.getMin();
    }

    double getMaxY() {
        return modelY.getMax();
    }

    double getRangeX() {
        return getMaxX() - getMinX();
    }

    double getRangeY() {
        return getMaxY() - getMinY();
    }

    boolean isFixedX(int idx) {
        return modelX.isFixed(idx);
    }

    boolean isFixedY(int idx) {
        return modelY.isFixed(idx);
    }

    void setMinMax(double minX, double maxX, double minY, double maxY) {
        modelX.setMinMax(minX, maxX);
        modelY.setMinMax(minY, maxY);
    }

    void setFixedX(int idx, boolean b) {
        modelX.setFixed(idx, b);
    }

    void setFixedY(int idx, boolean b) {
        modelY.setFixed(idx, b);
    }


    int getNumPoints() {
        return modelX.getNumValues();
    }

    boolean isOrderedX() {
        return modelX.isOrdered();
    }

    boolean isOrderedY() {
        return modelY.isOrdered();
    }

    void addValue(int idx, double x, double y) {
        modelX.addValue(idx, x);
        modelY.addValue(idx, y);
        changeSupport.fire( EVT_CHANGED );
    }

    void setValue(int idx, double x, double y) {
        double oldX = modelX.getValue(idx);
        double oldY = modelY.getValue(idx);
        modelX.setValue(idx, x);
        modelY.setValue(idx, y);

        if(    oldX != modelX.getValue(idx)
            || oldY != modelY.getValue(idx) ) {    
            changeSupport.fire( EVT_CHANGED );
        }
    }

}
