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

import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultTool;
import java.awt.Dimension;

/**
 *
 * @author davidjr
 */
public class CtContrastTool extends CtImageResultTool {

    public static final String IMAGE_RESULT_KEY_CONTRAST = "Contrast";

    public CtGrayscaleMapCanvas _gmc;
    
    public CtContrastTool( CtZoomCanvasPanel zcp ) {
        super( zcp, IMAGE_RESULT_KEY_CONTRAST );

        CtImageResult ir = getOriginalImageResult();

        CtViewpointZoomCanvas vzc = (CtViewpointZoomCanvas)_ircl.getParent();
        CtViewpointController vc = vzc.getViewpointController();

        _gmc = new CtGrayscaleMapCanvas( vc, this, ir );// _ep );
        _gmc.setFlipLeftRight( false );
        _gmc.setFlipUpDown( false );
        _gmc.setRotate90( false );
        _gmc.setPreferredSize( new Dimension( 300, 100 ) );

        _ircl.setImageResult( IMAGE_RESULT_KEY_CONTRAST, _gmc.getDst() );
    }


}
