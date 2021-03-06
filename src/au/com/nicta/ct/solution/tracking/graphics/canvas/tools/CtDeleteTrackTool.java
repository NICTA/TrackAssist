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

package au.com.nicta.ct.solution.tracking.graphics.canvas.tools;

import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtDeleteTrackTool extends CtTool {

    public CtDeleteTrackTool( CtToolsModel tm ) {//, CtSolutionController sc ) {//, final CtEditMode editMode) {
        super( tm, "delete-track" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        sc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

//        _mappings.add( new CtAbstractPair< CtDetectionState, CtDetectionState >( CtDetectionState.SELECTED, CtDetectionState.NORMAL ) );
//        _mappings.add( new CtAbstractPair< CtDetectionState, CtDetectionState >( CtDetectionState.NORMAL, CtDetectionState.SELECTED ) );

//        applyOnMouseClicked();
        updateEnabled();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    public void onSolutionModelChanged() {
        updateEnabled();
    }

    @Override public void updateEnabled() {

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel sm = sc.getModel();

        Collection< CtTracks > ct = sm.getTracksWithState( CtItemState.SELECTED );

        int tSize = ct.size();

        if( tSize < 1 ) {
            setEnabled( false );
            return;
        }

        setEnabled( true );
    }

    @Override protected String iconFile() {
        return new String( "track_delete.png" );
    }

    @Override protected String toolTip() {
        return new String( "Delete track" );
    }

    @Override protected String toolDescription() {
        return "Delete selected tracks.";
    }

    @Override public void activate(){
        int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to delete selected tracks?", "Delete Tracks", JOptionPane.YES_NO_OPTION );
        if( n != JOptionPane.YES_OPTION ) {
            return;
        }

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        CtPageFrame.showWaitCursor();
        sc.separateSelected();//.separateSelectedTracks();
        CtPageFrame.showDefaultCursor();

        _tm.activateDefault();
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
//
//    @Override public void apply( MouseEvent e ) {
////        CtSolutionController sc = (CtSolutionController)CtObjectDirectory.get( CtSolutionController.name() );
//
//        _sc.associateSelected();
//    }

}
