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

package au.com.nicta.ct.solution.tracking;

import au.com.nicta.ct.orm.patterns.CtDirectorySingleton;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowController;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowModel;
import au.com.nicta.ct.solution.CtSolutionController;
import au.com.nicta.ct.solution.CtSolutionListener;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import au.com.nicta.ct.orm.interactive.CtResult;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.Collection;

/**
 * MVC pattern.
 * Controller tells View when to view. View reads state from Model directly.
 * Model is updated by fn calls from Controller ONLY.
 * Model can be accessed for read-only use (not enforced) via Controller.
 * When model changes, event broadcast to Controller ONLY then controller re-
 * broadcasts to its own listeners. This is to allow controller to batch updates
 * to reduce e.g. number of repaints.
 * Controller handles response to external events e.g. change in image sequence,
 *
 * @author davidjr
 */
public class CtTrackingController extends CtChangeModel implements CtCoordinatesListener, CtSolutionListener {

    // externals:
//    CtExperiments _e;
//    CtExperimentModel _em;
//    CtSolutions _s;

    // internals:
//    CtDetectionsController _dc;
    CtDetectionsCanvasLayer _dv;
    CtTrackingModel _tm;
    CtTrackingCanvasLayer _tv;
//    CtCoordinatesController _cc;
//    CtImageSequenceFactory _isf;
//    CtTimeWindowModel _twm;

//    CtChangeModel _detectionsChangeModel = new CtChangeModel( null );
//    CtChangeModel  _solutionChangeModel = new CtChangeModel( null );

    public static CtTrackingModel getModel() {
        CtTrackingController tc = get();
        if( tc != null ) {
            return tc.getTrackingModel();
        }
        return null;
    }

    public static CtTrackingController get() {
        CtTrackingController tc = (CtTrackingController)CtObjectDirectory.get( CtTrackingController.name() );
        
        if( tc == null ) {
            tc = new CtTrackingController();
        }
//        else {
//            tc.setZoomCanvas( zc );
//        }

        return tc;
    }

    public static String name() {
        return "tracking-controller";
    }

    protected CtTrackingController() {//, CtCoordinatesController cc, CtImageSequenceFactory isf ) {
        super( null );

        CtObjectDirectory.put( CtTrackingController.name(), this );
        this._tm = new CtTrackingModel();//tm;
//        this._tv = new CtTrackingView( zc );//sv;
//        this._dv = new CtDetectionsView( zc );//dv;

        CtCoordinatesController.addCoordinatesListener( this );
        onModelChanged();

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
        twc.addModelListener( 
            new ActionListener() {
                @Override public void actionPerformed( ActionEvent ae ) {
                    CtTrackingController.get().onTimeWindowChanged();
                }
            }
        );

        _tm.addModelChangeListener(
            new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtTrackingController.get().onTrackingModelChanged();
                }
            }
        );
        _tm.addAppearanceChangeListener(
            new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtTrackingController.get().onTrackingAppearanceChanged();
                }
            }
        );

        CtSolutionController sc = CtSolutionController.get();
        sc.addSolutionListener( this );
    }

    public CtTrackingModel getTrackingModel() {
        return _tm;
    }

//    public CtTrackingCanvasLayer getTrackingView() {
//        return _tv;
//    }
//
//    public CtDetectionsCanvasLayer getDetectionsView() {
//        return _dv;
//    }
//
//    public void setZoomCanvas( CtZoomCanvas zc ) {
//        if( _dv == null ) {
//            _dv = new CtDetectionsCanvasLayer( zc );//sv;
//        }
//        else {
//            _dv.setZoomCanvas( zc );
//        }
//
//        if( _tv == null ) {
//            _tv = new CtTrackingCanvasLayer( zc );//sv;
//        }
//        else {
//            _tv.setZoomCanvas( zc );
//        }
//    }
    
//    @Override public void actionPerformed( ActionEvent ae ) {
//
//        // possible events:
//        // image sequence controller changed (everything invalid)
//        // detections controller changed (detections on current frame invalid)
//        // time window model changed (some images detections invalid/missing)
//        // image sequence model changed (some images detections invalid/missing)
//
////        if( ae.getActionCommand().equals( "blah" ) ) {
//            // separate for efficiency changes to different things.. cache each frame's detections separately.
//            onModelChanged();
////        }
//    }

    public void onTimeWindowChanged() {
//        CtExperimentModel em = CtExperimentModel.get();
//        onImageChanged( em._isf );
        onImageChanged();
    }

    public void onModelChanged() {
        onImageChanged();
    }
    public void onRangeChanged() {
        onImageChanged();
    }
    public void onIndexChanged() {
        onImageChanged();
    }

//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
//        onImageChanged( isf );
//    }
//
//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {
//
//        CtExperimentModel em = CtExperimentModel.get();
    public void onImageChanged() {

        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
        CtTimeWindowModel twm = (CtTimeWindowModel)twc.getModel();//new CtTimeWindowController( twm, null );

//        CtImageSequenceController isc = //isf.getController();
//        CtImageSequenceModel ism = (CtImageSequenceModel)isc.getModel();
        CtImageSequenceModel ism = cc.getImageSequenceModel();

        _tm.update( cc, ism, twm );

//        _tv.repaint();
//        _dv.repaint();

        // Should I unselect things when the disappear from view?
//        onModelChanged();
//    @Override public void onControllerChanged( CtImageSequenceController isc ) {
//        isc.addModelListener( this );
//        onModelChanged(); // reload everything
    }

// in model:
//    HashMap< CtImages, CtDetectionsModel > _imagesDetections = new HashMap< CtImages, CtDetectionsModel >();
    
    public void onTrackingModelChanged() {
        fireModelChanged(); // broadcast this internal event to all observers, including the views.
//        fireAppearanceChanged();
    }
    public void onTrackingAppearanceChanged() {
        fireAppearanceChanged(); // broadcast this internal event to all observers, including the views.
    }
    public void fireAppearanceChanged() {
        changeSupport.fire( CtTrackingModel.EVT_APPEARANCE_CHANGED );
    }
    public void addAppearanceChangeListener( CtChangeListener cl ) {
        changeSupport.addListener( CtTrackingModel.EVT_APPEARANCE_CHANGED, cl );
    }

    @Override public void onSolutionChanged( CtSolutions s ) {

//        CtProgressTask pt = new CtProgressTask() {
//            @Override public void doTask() {
//                CtTrackingController ct = CtTrackingController.get();
//                ct.refresh( this );
//            }
//        };
//        pt.doTask();
        refresh( true );

//        _dc.setModel( _sm.getDetectionsModel() );
//        CtImages i = ism.current();
        // how to get historic and future images?
//        int future  = _twm._future;
//        int history = _twm._history;
//
//        int index = ism.currentIndex();// - and + is navigatin the current axes
//
//        CtImages i = ism.get( frameIndex );
//
//        onDetectionModelChanged();
//        onSolutionModelChanged();
    }

    public void clear() {
        _tm.clear();
//        onDetectionModelChanged();
//        onSolutionModelChanged();
////        _dc.setModel( null );
    }

    public void refresh( boolean showProgress ) {

        CtSolutions s = CtSolutionController.getSolutions();
//        CtExperimentModel em = CtExperimentModel.get();

//        CtCoordinatesController cc = em._cc;
//
//        CtImageSequenceController isc = em._isf.getController();
//        CtImageSequenceModel ism = (CtImageSequenceModel)isc.getModel();
        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;
        CtImageSequenceModel ism = cc.getImageSequenceModel();

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
        CtTimeWindowModel twm = (CtTimeWindowModel)twc.getModel();//new CtTimeWindowController( twm, null );

        _tm.refresh( cc, ism, twm, s, showProgress ); // will cause onTrackingModelChanged()
//        _dc.setModel( _sm.getDetectonsModel() );
//        onDetectionModelChanged();
//        onSolutionModelChanged();
    }

//    public void setSolutionView( CtSolutionView sv ) {
//        _sv = sv;
//        _sv.setModel( _sm );
////
////        CtCanvasLayer cl = _sv.getCanvasLayer();
//    }

////////////////////////////////////////////////////////////////////////////////
//  MANIPULATION METHODS
////////////////////////////////////////////////////////////////////////////////

    public CtResult createDetection( CtZoomPolygon zp ) {
        return _tm.createDetection( zp );
    }

    public CtResult createDetection( CtZoomPolygon zp, CtImages i ) {
        return _tm.createDetection( zp, i );
    }

    public CtResult createDetections( Collection< CtZoomPolygon > czp, CtImages i ) {
        return _tm.createDetections( czp, i );
    }

    public Collection< CtDetections > getSelectedDetections() {
        return _tm.getDetectionsWithState( CtItemState.SELECTED );
    }

    public void translateSelectedDetections( int x, int y ) {
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        translateDetections( cd, x, y );
    }

    public void translateDetections( Collection< CtDetections > cd, int x, int y ) {
        _tm.translateDetections( cd, x, y );
    }

    public void deleteDetectionsInRange( int index1, int index2 ) {
        Collection< CtDetections > cd = _tm.getDetectionsInRange( index1, index2 );
        _tm.deleteDetections( cd );
    }

    public void deleteDetectionsInWindow() {
        Collection< CtDetections > cd = _tm.getDetectionsInWindow();
        _tm.deleteDetections( cd );
    }

    public void deleteSelectedDetections() {
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        _tm.deleteDetections( cd );
    }

    public void mergeSelectedDetections() {
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        _tm.mergeDetections( cd );
    }

    public void deleteDetections( Collection< CtDetections > cd ) {
        _tm.deleteDetections( cd );
    }

    public void setState( CtItemState is ) {
        _tm.setState( is );
    }

    public void setState( CtDetections d, CtItemState ds ) {
        _tm.setState( d, ds );
    }

    public void setState( CtTracks t, CtItemState ds ) {
        _tm.setState( t, ds );
    }

    public void setDetectionsState( Collection< CtDetections > cd, CtItemState ds ) {
        _tm.setDetectionsState( cd, ds );
    }

    public void setTracksState( Collection< CtTracks > ct, CtItemState ds ) {
        _tm.setTracksState( ct, ds );
    }

    public void setBoundary( CtDetections d, CtZoomPolygon zp ) {
        _tm.setBoundary( d, zp );
    }

    public boolean setDetectionsStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, Point2D p2d ) {
        return _tm.setDetectionsStatesInWindowAt( mappings, p2d );
    }

    public boolean setStatesInWindowAt( Collection< CtAbstractPair< CtItemState, CtItemState > > mappings, Point2D p2d ) {
        return _tm.setStatesInWindowAt( mappings, p2d );
    }

    public boolean selectionIsForkable() {
        Collection< CtDetections > cd = _tm.getDetectionsWithStateInWindow( CtItemState.SELECTED );
        Collection< CtTracks > ct = _tm.getTracksWithState( CtItemState.SELECTED );

        return _tm.collectionsAreForkable( cd, ct );
    }

    public CtResult forkSelected() {
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        Collection< CtTracks > ct = _tm.getTracksWithState( CtItemState.SELECTED );
        return _tm.forkSelected( cd, ct );//.iterator().next() );
    }

//    public CtResult fork(
//        Collection< CtDetections > cd,
//        CtTracks t ) {
//        return _tm.forkTrack( cd, t );
//    }

    public CtResult associateSelected() {
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        Collection< CtTracks > ct = _tm.getTracksWithState( CtItemState.SELECTED );
        return associate( cd, ct );
    }

    public CtResult associate(
        Collection< CtDetections > cd,
        Collection< CtTracks > ct ) {
        return _tm.createTrack( cd, ct );
    }

    public CtResult separateSelected() {
        // separate these detections cd of these tracks ct...
        Collection< CtDetections > cd = _tm.getDetectionsWithState( CtItemState.SELECTED );
        Collection< CtTracks > ct = _tm.getTracksWithState( CtItemState.SELECTED );
        return separate( cd, ct );
    }

    public CtResult separateSelectedInWindow() {
        // separate these detections cd of these tracks ct...
        Collection< CtDetections > cd = _tm.getDetectionsWithStateInWindow( CtItemState.SELECTED );
        Collection< CtTracks > ct = _tm.getTracksWithState( CtItemState.SELECTED );
        return separate( cd, ct );
    }

    public CtResult separate(
        Collection< CtDetections > cd,
        Collection< CtTracks > ct ) {
        return _tm.separate( cd, ct );
    }

//    public String separateSelectedTracks() {
//        // separate these detections cd of these tracks ct...
//        Collection< CtTracks > ct = _sm.getTracksWithState( CtItemState.SELECTED );
//        return separate( ct );
//    }
//
//    public String separate(
//        Collection< CtTracks > ct ) {
//        return _sm.separate( ct );
//    }

    public void deleteTracks( boolean showProgress ) {
        _tm.deleteTracks( showProgress );
    }
}
