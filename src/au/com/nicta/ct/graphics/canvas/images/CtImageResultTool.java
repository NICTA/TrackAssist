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

package au.com.nicta.ct.graphics.canvas.images;

import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author davidjr
 */
public abstract class CtImageResultTool implements CtChangeListener {

    public static final String EVT_IMAGE_RESULT_PROCESS_CHANGED = "image-result-process-changed";

    protected CtImageResultsCanvasLayer _ircl;
    protected String _imageResultKey;
    protected CtChangeSupport _cs;// = new CtChangeSupport( this );

    public void addImageResultProcessListener( CtChangeListener cl ) {
        _cs.addListener( EVT_IMAGE_RESULT_PROCESS_CHANGED, cl );
    }

    public void fireImageResultProcessChanged() {
        _cs.fire( EVT_IMAGE_RESULT_PROCESS_CHANGED );
    }

    public CtImageResultTool( CtZoomCanvasPanel zcp, String imageResultKey ) {//CtZoomCanvas zc ) {//, CtMicrowellsModel mm ) {
        this( CtImageResultPanelFactory.getImageResultsCanvasLayer( zcp.getZoomCanvas() ), imageResultKey );
    }

    protected CtImageResultTool( CtImageResultsCanvasLayer ircl, String imageResultKey ) {//CtZoomCanvas zc ) {//, CtMicrowellsModel mm ) {
        super();

        _ircl = ircl;
        _imageResultKey = imageResultKey;
        _ircl.addOriginalImageResultsChangedListener( this );

        _cs = new CtChangeSupport( this );
        addImageResultProcessListener( this );
//        _ircl.setImageResult( _imageResultKey, new CtImageResult() ); // adds the new result
    }

    public CtImageResult getOriginalImageResult() {
        return _ircl.getOriginal();
    }

    public CtImageResult getImageResult() {
        return _ircl.getImageResult( _imageResultKey );
    }

//    public abstract JComponent getComponent();

    public void repaintImageResult() {
        refreshImageResult();
        _ircl.repaint();
    }

    public void refreshImageResult() {
        CtImageResult ir = getImageResult();

        ir.refresh();
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {

        if( evt.getPropertyName().equals( EVT_IMAGE_RESULT_PROCESS_CHANGED ) ) {
            repaintImageResult();
            return;
        }

        if( !evt.getPropertyName().equals( CtImageResultsCanvasLayer.EVT_ORIGINAL_IMAGE_RESULT_CHANGED ) ) {
            return;
        }

        String key = _ircl.getSelectedKey();

        if( key == null ) {
            return;
        }

        if( !key.equals( _imageResultKey ) ) {
            // I'm not being shown, don't recompute
            return;
        }

        refreshImageResult();
    }

}
