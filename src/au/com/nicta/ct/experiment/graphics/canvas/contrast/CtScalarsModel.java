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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Alan
 */
public class CtScalarsModel extends CtChangeModel {

    public static final String EVT_CHANGED = "CtScalarsModelChanged";

    final boolean ordered;
    boolean fixed = false;
    double min = 0;
    double max = 0;

    class Value {
        double v = 0;
        boolean fixed = false;
    }

    List<Value> values = new LinkedList<Value>();

    CtScalarsModel(double min, double max, boolean ordered) {
        super(null); // not forwarding any events from other models
        this.ordered = ordered;
        setMinMax(min, max);
    }

    void setMinMax(double min, double max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        changeSupport.fire( EVT_CHANGED );
    }

    void setFixed(int idx, boolean b) {
        values.get(idx).fixed = b;
    }

    boolean isFixed(int idx) {
        return values.get(idx).fixed;
    }

    double getMin() {
        return min;
    }

    double getMax() {
        return max;
    }

    double getRange() {
        return max - min;
    }
    
    double getValue(int idx) {
        return values.get(idx).v;
    }

    int getNumValues() {
        return values.size();
    }

    boolean isOrdered() {
        return ordered;
    }

    void addValue(int idx, double value) {
        values.add( idx, new Value() );
        setValue( idx, value );
    }

    void setValue(int idx, double value) {
        if(  doSetValue(idx, value)  ) {
            changeSupport.fire( EVT_CHANGED );
        }
    }
    
    /**
     *
     * @param idx
     * @param value
     * @return true if values changed
     */
    boolean doSetValue(int idx, double value) {
        if( fixed ) {
            return false;
        }

        if( values.get(idx).v == value ) {
            return false;
        }

        if( value < min ) {
            value = min;
        }
        else
        if( value > max ) {
            value = max;
        }
        
        if( !ordered ) {
            values.get(idx).v = value;
            return true;
        }

        // moving left
        if( value < getValue(idx) ) {
            // make sure the new value is not set past a fixed value
            for( int i = idx; i >= 0; --i ) {
                if( isFixed(i) ) {
                    value = Math.max( value, getValue(i) );
                    break;
                }
            }
            // move all values to the left
            for( int i = idx; i >= 0; --i ) {
                if( getValue(i) > value ) {
                    values.get(i).v = value;
                }
                else {
                    break; // ordered, so shouldn't have any thing else that needs moving
                }
            }
        }
        else { // moving right
            // make sure the new value is not set past a fixed value
            for( int i = idx; i < values.size(); ++i ) {
                if( isFixed(i) ) {
                    value = Math.min( value, getValue(i) );
                    break;
                }
            }
            // move all values to the right
            for( int i = idx; i < values.size(); ++i ) {
                if( getValue(i) < value ) {
                    values.get(i).v = value;
                }
                else {
                    break; // ordered, so shouldn't have any thing else that needs moving
                }
            }
        }

        values.get(idx).v = value;
        return true;
    }
}


