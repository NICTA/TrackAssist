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

import au.com.nicta.ct.experiment.coordinates.viewpoint.CtPanZoom;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author davidjr
 */
public class CtImageResultZoomCanvas extends CtZoomCanvas implements CtChangeListener {

    CtPanZoom _pz;
    CtImageResultCanvasLayer _ircl;

    public CtImageResultZoomCanvas() {
        super();

        _pz = new CtPanZoom();
        _pz.addListener( this );

        _ircl = new CtImageResultCanvasLayer();
        addLayer( _ircl );
//        _ircl.setParent( this );
    }

    @Override protected void addMouseListeners() {
        CtZoomCanvasMouseListener ml = new CtZoomCanvasMouseListener( this );
        ml.mouseWheelMode = CtZoomCanvasMouseListener.MOUSE_WHEEL_MODE_ZOOM;
        addMouseListener( ml );
        addMouseWheelListener( ml );
        addMouseMotionListener( ml );
    }

    public CtImageResult getImageResult() {
        return _ircl._ir;
    }

    public void setImageResult( CtImageResult ir ) {
        _ircl.setImageResult( ir );
    }
    
    public CtPanZoom getPanZoom() {
        return _pz;
    }
    
    @Override public void propertyChange( PropertyChangeEvent evt ) { // viewpoint change
        String s = evt.getPropertyName();

        if( s.equals( CtPanZoom.EVT_OFFSET_CHANGED ) ) {
            updateScrollBarHor();
            updateScrollBarVer();
            repaint();
        }
        else if(    s.equals( CtPanZoom.EVT_LEVEL_CHANGED  )
                 || s.equals( CtPanZoom.EVT_FACTOR_CHANGED ) ) {
            updateScrollBarHor();
            updateScrollBarVer();
            repaint();
        }
    }
    
}
