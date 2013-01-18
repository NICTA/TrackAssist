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
import au.com.nicta.ct.orm.interactive.CtQuantityItemStateConstraint;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 * single or multi select options?
 * some things enabled on single-select, others on multi... option for select tool is single or multi
 * @author davidjr
 */
public abstract class CtSelectedDetectionTool extends CtTool {

//    CtQuantityConstraint _quantity = CtQuantityConstraint.ONE_OR_MORE;
//    ArrayList< CtItemState > _states = new ArrayList< CtItemState >();
    CtQuantityItemStateConstraint _qisc = new CtQuantityItemStateConstraint();

    public CtSelectedDetectionTool( CtToolsModel tm, String name ) {//, CtDetectionsController dc ) {
        super( tm, name );

        CtTrackingController tc = CtTrackingController.get();
        tc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        tc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

        _qisc._states.add( CtItemState.SELECTED );
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    @Override public void updateEnabled() {

        CtTrackingModel tm = CtTrackingController.getModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }

        if( _qisc == null ) {
            setEnabled( false );
            return;
        }

        Collection< CtDetections > cd = tm.getDetectionsWithStatesInWindow( _qisc._states );

        int quantity = 0;

        if( cd != null ) {
            quantity = cd.size();
        }

        boolean valid = _qisc.isSatisfied( quantity );

        setEnabled( valid );
    }

}

    