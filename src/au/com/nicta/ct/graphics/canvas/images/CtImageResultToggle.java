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

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.graphics.canvas.images.toggle.CtComponentVisibilityToggle;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.JComponent;

/**
 *
 * @author davidjr
 */
public class CtImageResultToggle extends CtComponentVisibilityToggle implements CtZoomCanvasLayerListener {

//    CtZoomCanvasPanel _irp; // thing that shows the results
//    CtImageResult _ir; // configurable tool
//    CtImageResultsCanvasLayer _ircl;
    protected HashSet< CtImageResultsCanvasLayer > _hs = new HashSet< CtImageResultsCanvasLayer >();
    public String _key;

    public CtImageResultToggle( String name, JComponent tools, CtViewpointZoomCanvasPanelFactory zcpf, String key ) { //CtZoomCanvasPanel irp, CtImageResult ir ) {
        this( name, null, null, tools, zcpf, key );
    }
    
    public CtImageResultToggle( String name, String iconFile, String toolTip, JComponent tools, CtViewpointZoomCanvasPanelFactory zcpf, String key ) { //CtZoomCanvasPanel irp, CtImageResult ir ) {
        super( name, iconFile, toolTip, tools );
//        _ircl = ircl;
        _key = key;
//        _irp = irp;
//        _ir = ir;
//        _ircl.addImageResultsChangedListener( this );
        zcpf._canvasLayerListeners.add( this );
    }

    @Override public void onCreateCanvasLayer( CtCanvasLayer cl ) {
        if( cl instanceof CtImageResultsCanvasLayer ) {
            CtImageResultsCanvasLayer ircl = (CtImageResultsCanvasLayer)cl;
            ircl.addImageResultsSelectionChangedListener( this );
            _hs.add( ircl );
        }

        refreshToggleButton();
    }

    @Override public void onDeleteCanvasLayer( CtCanvasLayer cl ) {

        if( cl instanceof CtImageResultsCanvasLayer ) {
            CtImageResultsCanvasLayer ircl = (CtImageResultsCanvasLayer)cl;
            ircl.removeImageResultsSelectionChangedListener( this );
            _hs.remove( ircl );
        }

        refreshToggleButton();
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        super.propertyChange( evt );

        refreshToggleButton();
    }

    public void refreshToggleButton() {
        
        boolean selected = false;

        for( CtImageResultsCanvasLayer ircl : _hs ) {
            selected |= ircl.isSelected( _key );
        }

        setSelected( selected );
//        _a.putValue( AbstractAction.SELECTED_KEY, selected );
//        _tb.setSelected( selected );//_ircl.isSelected( _key ) );
    }

    @Override public void setComponentVisible( boolean b ) {
//        _irp.setModified( null );
        super.setComponentVisible( b );

        if( b ) {
            for( CtImageResultsCanvasLayer ircl : _hs ) {
                ircl.selectImageResult( _key );
            }
        }
        else {
            for( CtImageResultsCanvasLayer ircl : _hs ) {
                ircl.deselectImageResult( _key );
            }
        }
    }
//        if( b ) {
//            for ( Component c:_irp.getToolBar().getComponents() ) {
//                if ( c instanceof CtImageResultSelectionTool ) {
//                    CtImageResultSelectionTool list = ( CtImageResultSelectionTool ) c;
//                    JComboBox cb = list.getList();
//                    for (int i=0; i<cb.getItemCount(); i++) {
//                        Object item = cb.getItemAt(i);
//                        if ( item.toString().compareTo( this._name ) == 0 ) {
//                            cb.setSelectedIndex( i );
//                            break;
//                        }
//                    }
//                }
//            }
//            _irp.setLayer( this._name, _ir );
//            _irp.setModified( _ir ); // maybe add/remove operation to a stack?
//        }
//        else {
//            _irp.setModified( null );
//            for ( Component c:_irp.getToolBar().getComponents() ) {
//                if ( c instanceof CtImageResultSelectionTool ) {
//                    CtImageResultSelectionTool list = ( CtImageResultSelectionTool ) c;
//                    JComboBox cb = list.getList();
//                    cb.setSelectedIndex( 0 );
//                    break;
//                }
//            }
//        }
//        _irp.repaint();
//    }
}
