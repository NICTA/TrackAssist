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

package au.com.nicta.ct.experiment.coordinates.time;

import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowModel;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 *
 * @author davidjr
 */
public class CtTimeInfoPanel extends JLabel {// implements CtImageSequenceListener {

//    protected CtImageSequenceController _isc;
    protected CtImageSequenceModel _ism;
    protected CtTimeWindowModel _twm;

    public CtTimeInfoPanel() {
        super( "---");
    }

//    public void onControllerChanged( CtImageSequenceController isc ) {
//        setImageSequenceModel( (CtImageSequenceModel)isc.getModel() );
//    }

    public void setImageSequenceModel( CtImageSequenceModel ism ) {
        _ism = ism;
        repaint();
    }

    public void setTimeWindowModel( CtTimeWindowModel twm ) {
        _twm = twm;
        repaint();
    }

    @Override public void paintComponent( Graphics g ) {

        String info = new String( " " );

        if( _ism != null ) {
            int    frameIndex = _ism.getIndex();// +1;
            int maxFrameIndex = _ism.size();

            info += "Frame " + frameIndex + " of " + maxFrameIndex + " ";
        }

        if( _twm != null ) {
            int history = _twm._history;
            int future  = _twm._future;
            int window = _twm.window();

            info += " highlighting ["+history+":"+future+"] ";//, a window of "+window+" frames";

//            if( _ism != null ) {
//                if( _ism._interFrameInterval != 0 ) {
//                    int seconds = ( _ism._interFrameInterval * window ) / 1000;
//
//                    info += ( " ("+seconds+" seconds)." );
//                }
//            }
//            else {
//                info += ".";
//            }
        }
        setText( info );
        
        super.paintComponent( g );
    }
}
