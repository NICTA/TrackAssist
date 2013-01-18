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

package au.com.nicta.ct.ui.swing.util;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Alan
 */
public class CtSpinnerQuantisedNumberModel extends SpinnerNumberModel {

    @Override
    public void setValue(Object value)
    {
        double step = getStepSize().doubleValue();
        Number min = (Number)getMinimum();
        Number max = (Number)getMaximum();

        Number num = (Number) value;

        double quantised = ( (int)Math.rint( num.doubleValue() / step ) ) * step;

        if(    min != null
            && quantised < min.doubleValue() )
        {
            super.setValue(min);
            return;
        }
        if(    max != null
            && quantised > max.doubleValue() )
        {
            super.setValue(max);
            return;
        }
        if( quantised == num.doubleValue() ) {
            super.setValue(num);
            return;
        }
        super.setValue(new Double(quantised));
    }
}
