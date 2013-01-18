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

package au.com.nicta.ct.experiment.graphics.canvas.composite;

import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;

/**
 *
 * @author Rajib
 */
public class CtAxisValueColour {

//    private CtImageSequenceModel _ism;
//    String _axisName;
    public int    _axisValue;
    public String _colorCode;

    public CtAxisValueColour( String colorCode ){
        //_ism = new CtImageSequenceModel();
//        _axisName = null;
        _colorCode  = colorCode;
        _axisValue  = -1;
    }

    public void reset(){
        //_ism = null;
//        _axisName = null;
        _axisValue  = -1;
    }

//    public void setImageSequence( String axisName ) {//CtImageSequenceModel ism ){
////        _ism = ism;
////        _axisName = axisName;
//    }

    public void setAxisValue( int axisValue ){
        _axisValue = axisValue;
    }
}
