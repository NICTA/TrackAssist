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

package au.com.nicta.ct.graphics.canvas.zoom;

import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomImagePainter;
import java.awt.Graphics2D;

/**
 * Automatically gets repainted on viewpoint change, but also picks up changes
 * in viewpoint original image and passes these to listeners, so
 * @author davidjr
 */
public class CtImageResultCanvasLayer extends CtCanvasLayer {// implements CtCoordinatesListener {//, AncestorListener { //CtImageChangeListener {//AdjustmentListener, ActionListener, CtImageSequenceListener, CtChangeListener {//, CtImageResultView {

    public static final String CANVAS_LAYER_NAME = "image-result-canvas-layer";

    protected CtZoomImagePainter _zip;
    protected CtImageResult _ir;
    
    public CtImageResultCanvasLayer() {
        super( CANVAS_LAYER_NAME );
    }

    @Override public void setParent( CtCanvas c ) {
        super.setParent( c );

        _zip = new CtZoomImagePainter( (CtZoomCanvas)c, null );
    }

    @Override public void paint( Graphics2D g ) {//, CtCanvasLayer l ) {
        _zip.paint( g, this );
    }

    public void setImageResult( CtImageResult ir ) {

        _ir = ir;
        
        if( _zip != null ) {
            if( _ir != null ) {
                _zip.setImage( _ir ); // can set to null..
            }
        }

        repaint();
    }

}
