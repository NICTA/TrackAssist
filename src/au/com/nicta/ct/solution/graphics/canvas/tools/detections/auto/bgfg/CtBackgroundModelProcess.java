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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg;

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;
import ij.ImagePlus;
import ij.process.ShortProcessor;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author davidjr
 */
public class CtBackgroundModelProcess extends CtEventDispatchThreadProgress {

    CtImageNormaliser _in;// = new CtStdDevNormaliser();
    CtImageRegistration _ir;// = new CtImageRegistration();

    CtBackgroundModel _bm;
    int _index1 = 0;
    int _index2 = 0;

    public CtBackgroundModelProcess( int index1, int index2, CtImageNormaliser in, CtImageRegistration ir ) {
        super( "Computing background model... ", "background-model-process" );

        _bm = new CtBackgroundModel();
        _index1 = index1;
        _index2 = index2;

        _in = in;
        _ir = ir;
    }

    @Override public int getLength() {
        return ( _index2 - _index1 +1 );
    }

    @Override public void doStep( int step ) {

        if( _bm == null ) {
            return;
        }

        if( step == 0 ) {
            _bm.reset();
        }

        int index = step + _index1;

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceModel ism = cc.getImageSequenceModel();

        try {
            CtImages i = ism.get( index );
            ImagePlus ip = cc.getCoordinatesModel().getImagePlus( i );
            ShortProcessor sp = (ShortProcessor)ip.getProcessor().duplicate();// clone it to avoid damaging original in memory;

            _in.normalise( sp );

            if( step == 0 ) {
                _ir.setRefImage( sp );

                _bm.addImage( _ir.getRefImage() ); // scaled up
            }
            else { // register against reference image
                CtImageRegistration.Result result = _ir.register( sp );

                _bm.addImage( result.getRoiA(), result.getRoiB(), result.scaled );
            }
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        if( index == _index2 ) {
            _bm.finish();
            _bm.save();
//            JOptionPane.showMessageDialog(
//                CtFrame.find(),
//                "Background modelling completed.",
//                "Background Complete", JOptionPane.INFORMATION_MESSAGE );
        }
    }
}
