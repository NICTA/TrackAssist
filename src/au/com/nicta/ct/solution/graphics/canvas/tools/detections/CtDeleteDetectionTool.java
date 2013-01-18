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
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtDeleteDetectionTool extends CtSelectedDetectionTool {

    public CtDeleteDetectionTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "delete-detection" );//, dc );
    }

    @Override protected String iconFile() {
        return new String( "detection_delete.png" );
    }

    @Override protected String toolTip() {
        return new String( "Delete detection" );
    }

    @Override protected String toolDescription() {
        return "Delete selected detections.";
    }

    @Override public void activate(){
        int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to delete selected detections?", "Delete Detections", JOptionPane.YES_NO_OPTION );
        if( n != JOptionPane.YES_OPTION ) {
            return;
        }

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return;
        }

        tc.deleteSelectedDetections();
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
