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

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.db.hibernate.CtTracksDetections;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtAssociateDetectionsTool extends CtTool {

//    CtSolutionController _sc;

    public CtAssociateDetectionsTool( CtToolsModel tm ) {//, CtSolutionController sc ) {//, final CtEditMode editMode) {
        super( tm, "associate-detections" );

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

//        this._sc = CtSolutionController.get();//sc;
//
//        CtSolutionModel sm = this._sc.getModel();
//        sm.addModelChangedListener( new CtChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                onSolutionModelChanged();
//            }
//        });

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

//    public void onSolutionModelChanged() {
//        updateEnabled();
//    }
//
    @Override public void updateEnabled() {

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel sm = sc.getTrackingModel();

        Collection< CtTracks > ct = sm.getTracksWithState( CtItemState.SELECTED );
        Collection< CtDetections > cd = sm.getDetectionsWithState( CtItemState.SELECTED );//sm.getOrphans();

        int tSize = ct.size();
        int dSize = cd.size();
//System.out.println( "d.sel="+dSize+" t.sel="+tSize );

        if( ( dSize + tSize ) < 2 ) {
            setEnabled( false );
            return;
        }
//        if( tSize > 1 ) {
//            setEnabled( false );
//System.out.println( "DISABLED cos >1 track" );
//            return;
//        }

        if( sm.simultaneousDetectionsIn( cd, ct ) ) {
            setEnabled( false );
            return;
        }
        
        if( tSize == 1 ) {

            int trackedDetections = 0;

            Set< CtTracksDetections > tds = ct.iterator().next().getCtTracksDetectionses();

            for( CtTracksDetections td : tds ) {
                CtDetections d1 = td.getCtDetections();
//                CtImages i = d1.getCtImages();
//                int index = _sc._sm._ism.index( i );
//
//                indices.add( index );

                for( CtDetections d2 : cd ) {

                    if( d1.getPkDetection() == d2.getPkDetection() ) {
                        ++trackedDetections;
                    }
                }
            }

//            Set< Integer > indices = new HashSet< Integer >();
//
//            for( CtDetections d2 : cd ) {
//                CtImages i = d2.getCtImages();
//                int index = _sc._sm._ism.index( i );
//
//                if( indices.contains( index ) ) {
//                    setEnabled( false );
//                    return; // would cause a fork
//                }
//                indices.add( index );
//            }

            if( dSize <= trackedDetections ) {
                setEnabled( false );
                return;
            }
        }

        setEnabled( true );
        //else : no tracks case
        // trackSize==0

        // check all the detections are at different times:
//        Set< Integer > indices = new HashSet< Integer >();
//
//        for( CtDetections d : cd ) {
//            CtImages i = d.getCtImages();
//            int index = _sc._sm._ism.index( i );
//            if( indices.contains( index ) ) {
//                setEnabled( false );
//                return;
//            }
//            indices.add( index );
//        }

//        if( dSize < 1 ) {
//            setEnabled( false );
//            return;
//        }

//        if( _sc._sm.overlappingDetectionsIn( cd ) ) {
//            setEnabled( false );
//            return;
//        }
    }

    @Override protected String iconFile() {
        return new String( "track_join.png" );
    }

    @Override protected String toolTip() {
        return new String( "Associate detections as track" );
    }

    @Override protected String toolDescription() {
        return "Merge";// selected detections and tracks into track.";
    }

    @Override public void activate(){
        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            return;
        }

        sc.associateSelected();
        sc.setState( CtItemState.NORMAL );
//        updateEnabled(); dont need to as event is recvd
//        setActive( false );
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
