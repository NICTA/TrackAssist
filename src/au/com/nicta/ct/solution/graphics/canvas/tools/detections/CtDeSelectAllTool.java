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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtDeSelectAllTool extends CtTool {

    public CtDeSelectAllTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "de-select-all" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override public void updateEnabled() {

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }

        Collection< CtDetections > cd = tm.getDetectionsInWindow();

        if( cd.isEmpty() ) {
            setEnabled( false );
        }

        setEnabled( true );
    }

    @Override protected String iconFile() {
        return new String( "icon_de_select.png" );
    }

    @Override protected String toolTip() {
        return new String( "De-select all detections & tracks" );
    }

    @Override protected String toolDescription() {
        return "De-select all detections.";
    }

    @Override public void activate(){
        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return;
        }

        tc.setState( CtItemState.NORMAL );
    }

    @Override protected void updateButton( JToggleButton button ) {
//        button.setEnabled( _mode.isEnabled() && this.isEnabled() );
        boolean selected1 = button.isSelected();

        button.setEnabled( this.isEnabled() );
        button.setSelected( this.isActive() );

        boolean selected2 = button.isSelected();

        if( selected2 & (!selected1) ) { // if changed to selected:
            setActive( false );
        }
    }
}
