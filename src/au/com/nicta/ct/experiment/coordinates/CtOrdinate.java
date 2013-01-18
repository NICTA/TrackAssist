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

package au.com.nicta.ct.experiment.coordinates;

import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;

/**
 *
 * @author davidjr
 */
public class CtOrdinate {

    public enum CtValueType {
        VALUE_TYPE_RELATIVE,
        VALUE_TYPE_ABSOLUTE
    }

    public String _coordinateTypeName = CtCoordinatesModel.COORDINATE_TYPE_TIME;
    public int _coordinateValue = 0;
    public CtValueType _valueType = CtValueType.VALUE_TYPE_ABSOLUTE;

    public CtOrdinate() {

    }

    public CtOrdinate( String coordinateTypeName, int coordinateValue, CtValueType vt ) {
        this._coordinateTypeName = coordinateTypeName;
        this._coordinateValue = coordinateValue;
        this._valueType = vt;
    }


    public int getValue() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        return getValue( cm );
    }

    public int getValue( CtCoordinatesModel cm ) {

        if( _valueType == CtValueType.VALUE_TYPE_ABSOLUTE ) {
            return _coordinateValue;
        }

        int value = cm.getOrdinate( _coordinateTypeName );

        int relative = value + _coordinateValue;
        return relative;
    }
}
