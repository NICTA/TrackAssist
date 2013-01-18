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

package au.com.nicta.ct.graphics.canvas.images.toggle;

import au.com.nicta.ct.ui.swing.util.CtMenuBuilder;
import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JComponent;

/**
 * Toggles the visibility of a bunsh of canvas layers
 * @author davidjr
 */
public class CtCanvasLayersToggle extends CtComponentVisibilityToggle implements CtZoomCanvasLayerListener {

    public static void add( JComponent parent, JComponent controls, String title, String iconFile, String toolTip, boolean enable, CtViewpointZoomCanvasPanelFactory zcpf, String canvasLayerName ) {
        ArrayList< String > al = new ArrayList< String >();
        al.add( canvasLayerName );
        add( parent, controls, title, iconFile, toolTip, enable, zcpf, al );
    }
    public static void add( JComponent parent, JComponent controls, String title, String iconFile, String toolTip, boolean enable, CtViewpointZoomCanvasPanelFactory zcpf, Collection< String > canvasLayerNames ) {
        CtCanvasLayersToggle tt = new CtCanvasLayersToggle( title, iconFile, toolTip, controls, canvasLayerNames );
        tt.setSelected( enable );
        parent.add( tt.createToggleButton() );
//        tt.addToggleButtonTo( parent, enable );
        zcpf._canvasLayerListeners.add( tt );
    }

    public static void add( CtMenuBuilder mb, String menu, JComponent controls, String title, String iconFile, String toolTip, boolean enable, CtViewpointZoomCanvasPanelFactory zcpf, String canvasLayerName ) {
        ArrayList< String > al = new ArrayList< String >();
        al.add( canvasLayerName );
        add( mb, menu, controls, title, iconFile, toolTip, enable, zcpf, al );
    }
    public static void add( CtMenuBuilder mb, String menu, JComponent controls, String title, String iconFile, String toolTip, boolean enable, CtViewpointZoomCanvasPanelFactory zcpf, Collection< String > canvasLayerNames ) {
        CtCanvasLayersToggle tt = new CtCanvasLayersToggle( title, iconFile, toolTip, controls, canvasLayerNames );
        tt.setSelected( enable );
        mb.addCheckableMenuItem( menu, tt.getAction() );
//        tt.addToggleButtonTo( parent, enable );
        zcpf._canvasLayerListeners.add( tt );
    }
    
    public HashSet< String > _layerNames = new HashSet< String >();
    public HashSet< CtCanvasLayer > _layers = new HashSet< CtCanvasLayer >();

    public CtCanvasLayersToggle( String name, String iconFile, String toolTip, JComponent controls ) {
        super( name, iconFile, toolTip, controls );
    }

    public CtCanvasLayersToggle( String name, String iconFile, String toolTip, JComponent controls, String s ) {
        super( name, iconFile, toolTip, controls );

        _layerNames.add( s );
    }

    public CtCanvasLayersToggle( String name, String iconFile, String toolTip, JComponent controls, Collection< String > cs ) {
        super( name, iconFile, toolTip, controls );

        for( String s : cs ) {
            _layerNames.add( s );
        }
    }

    @Override public void onCreateCanvasLayer( CtCanvasLayer cl ) {
        String name = cl.getName();

        boolean isSelected = getSelected();

        for( String s : _layerNames ) {
            if( s.equals( name ) ) {
                _layers.add( cl );

                cl.setEnabled( isSelected );
            }
        }
    }

    @Override public void onDeleteCanvasLayer( CtCanvasLayer cl ) {
        _layers.remove( cl ); // will silently fail if not present
    }

    @Override protected void setComponentVisible( boolean b ) {
        super.setComponentVisible( b );

        HashSet< CtCanvas > canvases = new HashSet< CtCanvas >();

        for( CtCanvasLayer cl : _layers ) {
            cl.setEnabled( b );
            canvases.add( cl.getParent() );
        }

        for( CtCanvas c : canvases ) {
            if( c != null ) { // might be orphan
                c.repaint();
            }
        }
    }

}
