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

package au.com.nicta.ct.solution.lineage;

import au.com.nicta.ct.orm.patterns.CtDirectorySingleton;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowController;
import au.com.nicta.ct.solution.CtSolutionListener;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author davidjr
 */
public class CtLineageController implements CtCoordinatesListener, CtSolutionListener { // CtImageChangeListener

//    CtCoordinatesController _cc;
//    public CtImageSequenceFactory _isf;
    public CtLineageModel _lm;
//    public CtLineageCanvasLayer _lv;
        
    public static CtLineageController get() {
        CtLineageController lc = (CtLineageController)CtObjectDirectory.get( CtLineageController.name() );

        if( lc == null ) {
            lc = new CtLineageController();// zc );
        }
//        else {
//            lc.setZoomCanvas( zc );
//        }

        return lc;
    }

    public static CtLineageModel getModel() {
        CtLineageController tc = get();
        if( tc != null ) {
            return tc.getLineageModel();
        }
        return null;
    }

    public CtLineageController() {// CtZoomCanvas zc ) {

        CtObjectDirectory.put( name(), this );

        this._lm = new CtLineageModel();
//        this._lv = new CtLineageView( zc );

        this._lm.addModelChangeListener(
            new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtLineageController.get().onLineageModelChanged();
                }
            }
        );

        CtCoordinatesController.addCoordinatesListener( this );
        onModelChanged();

        CtTimeWindowController twc =
            (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );

        twc.addModelListener(
            new ActionListener() {
                @Override public void actionPerformed( ActionEvent ae ) {
                    CtLineageController.get().onTimeWindowChanged();
                }
            }
        );

        CtTrackingController.get().addModelChangeListener(
            new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtLineageController.get().onTrackingModelChanged( evt );
                }
            }
        );
        CtTrackingController.get().addAppearanceChangeListener(
            new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtLineageController.get().onTrackingAppearanceChanged();
                }
            }
        );

        onTrackingModelChanged( null );
    }

//    public void setView( CtLineageView lv ) {
//        this._lv = lv;
//        this._lv.setModel( _lm );
//    }

    public static String name() {
        return "lineage-controller";
    }

    public CtLineageModel getLineageModel() {
        return _lm;
    }

//    public CtLineageCanvasLayer getLineageView() {
//        return _lv;
//    }
//
//    public void setZoomCanvas( CtZoomCanvas zc ) {
//        if( _lv == null ) {
//            _lv = new CtLineageCanvasLayer( zc );
//        }
//        else {
//            _lv.setZoomCanvas( zc );
//        }
//    }

//    @Override public void actionPerformed( ActionEvent ae ) {
////        CtExperimentModel em = CtExperimentModel.get();
////        onImageChanged( em._isf );
//    }

    public void onTrackingModelChanged( PropertyChangeEvent evt ) {
//        CtExperimentModel em = CtExperimentModel.get();
        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;
//        CtCoordinatesModel cm = cc.getCoordinatesModel();
        CtImageSequenceModel ism = cc.getImageSequenceModel();
//        CtImageSequenceController isc = em._isf.getController();
//        CtImageSequenceModel ism = (CtImageSequenceModel)isc.getModel();
//        CtImageSequenceModel ism = isf.getModel();

//        _lm.setCoordinatesController( _cc );
//        _lm.setImageSequenceModel( ism );
//        _lm.refresh( _cc, ism );

        CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );

        if( s == null ) {
            return;
        }

        boolean refresh = true;
        boolean showProgress = true;
        
        if( evt != null ) {
            if( evt.getPropertyName().equals( CtTrackingModel.EVT_APPEARANCE_CHANGED ) ) {
                refresh = false;
            }
        }

        if( refresh ) {
            _lm.refresh( s, cc, ism, showProgress ); // will later generate a lineage model changed event
        }
        else { // there will be no change to the lineage model, just repaint it (the view content may have changed)
//            if( _lv != null ) {
//                _lv.repaint();
//            }
        }
    }

    public void onTrackingAppearanceChanged() {
//        if( _lv != null ) {
//            _lv.repaint();
//        }
    }

    public void onLineageModelChanged() {
//        if( _lv != null ) {
//            _lv.repaint();
//        }
    }

    public void onTimeWindowChanged() {

    }
    
    @Override public void onSolutionChanged( CtSolutions s ) {
//        CtExperimentModel em = CtExperimentModel.get();
//        onImageChanged( em._isf );
    }

    public void onModelChanged() {
//        if( _lv != null ) {
//            _lv.repaint();
//        }
    }
    public void onRangeChanged() {
        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;
        CtImageSequenceModel ism = cc.getImageSequenceModel();
        _lm.setImageSequenceModel( ism );
    }
    
    public void onIndexChanged() {
//        if( _lv != null ) {
//            _lv.repaint(); // not sure if this is a wasted (double) repaint in some circumstances
//        }
    }

//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
////        onImageChanged( isf );
//    }
//
//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {
////        onModelChanged();
////    }
////
////    protected void onModelChanged() {
////        CtImageSequenceController isc = _isf.getController();
////
////        if( isc == null ) {
////            return;
////        }
////
////        CtImageSequenceModel ism = (CtImageSequenceModel)isc.getModel();
//    }

}
