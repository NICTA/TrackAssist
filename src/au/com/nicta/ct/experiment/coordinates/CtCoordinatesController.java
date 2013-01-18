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

package au.com.nicta.ct.experiment.coordinates;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtExperimentsAxes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceFactory;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.experiment.CtExperimentController;
import au.com.nicta.ct.experiment.CtExperimentListener;
import java.util.HashSet;

/**
 *
 * @author davidjr
 */
public class CtCoordinatesController implements CtExperimentListener, CtCoordinatesListener {

//    protected String _rangeAxis;
    public String _defaultRange = CtCoordinatesModel.COORDINATE_TYPE_TIME;//"time";
    protected CtImageSequenceFactory _isf;
    protected CtCoordinatesModel _cm;

    HashSet< CtCoordinatesListener > _coordinatesListeners = new HashSet< CtCoordinatesListener >();

//    protected CtImageSequenceModel _ism;
//    protected CtImageSequenceController _isc;
//    protected CtExperimentsAxes _ea;

    public static void addCoordinatesListener( CtCoordinatesListener cl ) {
        CtCoordinatesController cc = CtCoordinatesController.get();
        cc._coordinatesListeners.add( cl );
    }

    public static void removeCoordinatesListener( CtCoordinatesListener cl ) {
        CtCoordinatesController cc = CtCoordinatesController.get();
        cc._coordinatesListeners.remove( cl );
    }
    
    public static CtCoordinatesModel getModel() {
        CtCoordinatesController cc = get();
        if( cc != null ) {
            return cc.getCoordinatesModel();
        }
        return null;
    }

    public static CtCoordinatesController get() {
        CtCoordinatesController tc = (CtCoordinatesController)CtObjectDirectory.get( CtCoordinatesController.name() );
        return tc;
    }

    // set: Work with this solution of this experiment on this canvas
    public static CtCoordinatesController get( CtImageSequenceFactory isf ) {
        CtCoordinatesController tc = (CtCoordinatesController)CtObjectDirectory.get( CtCoordinatesController.name() );

        if( tc == null ) {
            tc = new CtCoordinatesController( isf );
        }

        return tc;
    }

    public static String name() {
        return "coordinates-controller";
    }

    public CtCoordinatesController( CtImageSequenceFactory isf ) {
//        super( null, null );

        CtObjectDirectory.put( CtCoordinatesController.name(), this );

        this._isf = isf;
        this._cm = new CtCoordinatesModel( isf );

        CtExperimentController ec = CtExperimentController.get();
        ec.addExperimentListener( this );
    }

    @Override public void onExperimentChanged( CtExperiments e ) {
        boolean showProgress = false;
        _cm.refresh( e, _defaultRange, showProgress ); // will fire a model changed event..
    }

    @Override public void onModelChanged() {
        // called by _cm when range is changed.
        for( CtCoordinatesListener cl : _coordinatesListeners ) {
            cl.onModelChanged();
        }
    }

    @Override public void onRangeChanged() {
        // called by _cm when range is changed.
        for( CtCoordinatesListener cl : _coordinatesListeners ) {
            cl.onRangeChanged();
        }
    }

    @Override public void onIndexChanged() {
        // called by _cm when current _cm._ism changes (index or selection)
        for( CtCoordinatesListener cl : _coordinatesListeners ) {
            cl.onIndexChanged();
        }
    }

    public CtImageSequenceController getImageSequenceController() {
        if( _cm == null ) return null;
        return _cm.getImageSequenceController();
    }

    public CtImageSequenceModel getImageSequenceModel() {
        if( _cm == null ) return null;
        return _cm.getImageSequenceModel();
    }

    public CtCoordinatesModel getCoordinatesModel() {
        return _cm;
    }

//    public int getTimeSize() {
//        return getSize( CtCoordinatesModel.COORDINATE_TYPE_TIME );
//    }
//
//    public int getSize( String coordinateType ) {
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        return cm.getRangeOrdinates( coordinateType );
////        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
////        CtImageSequenceModel ism = _cm.getRange( coordinateType );
////
////        if( ism == null ) {
////            return 0;
////        }
////
////        return ism.size();
//    }

//    public int getTimeOrdinate( CtImages i ) {
////        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        return _cm.getTimeOrdinate( i );
//    }
//    public int getOrdinate( CtImages i, String coordinateType ) {
////        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        return _cm.getOrdinate( i, coordinateType );
//    }

//    public ImagePlus getImage( String coordinateType, int index ) throws IOException {
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        CtExperimentsAxes ea = cm.findAxis( coordinateType );
//        CtImageSequenceModel ism = getRange(); // varying axis
//
//        int varyingIndex = ism.currentIndex() +1;
//
//        CtImages i = cm.createSingle( _ea, varyingIndex, ea, index );
//
//        if( i == null ) {
//            return null;
//        }
//
//        return CtCachedImages.Get( i );
////        CtImageSequenceModel ism2 = cm.findRange( coordinateType );
////****
////        return ism2.getCachedImage( index );
//    }

//// if you change the range to view a different axis, then invalidate the sequence
//    public CtImageSequenceController getRangeController() {
//        if( _isc == null ) {
//            _isc = _isf.createController();
////            CtCoordinatesModel cm = (CtCoordinatesModel)_m;
////            _ism = cm.getRange( _ea );//_isf.createModel();
////            _isc = cm._isf.createController();creates new contorller when range changes.. why not keep same
//        }
//        return _isc;
//    }
//
//    public CtImageSequenceModel getRange() {
//        CtImageSequence
////        return _ism;
//    }

//    public String getRangeType() {
//        if( _ea == null ) {
//            return null;
//        }
//        return _ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes().getName();
//    }

    public void setRange( String coordinateType ) {
//        _rangeAxis = coordinateType;
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
        CtExperimentsAxes ea = _cm.findAxis( coordinateType );
        setRange( ea );
    }

    public void setRange( CtCoordinatesTypes ct ) {
//        _rangeAxis = ct.getName();
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
        CtExperimentsAxes ea = _cm.findAxis( ct );
        setRange( ea );
    }

    public void setRange( CtExperimentsAxes ea ) {
        _cm.setRange( ea );
    }

//    public void setRange( CtExperimentsAxes ea ) {
//        if(    ( _ea != null )
//            && ( _ea.getPkExperimentAxis() == ea.getPkExperimentAxis() ) ) {
//            return; // unchanged
//        }
//
//        _ea = ea;
//
//        updateRange();
//    }

//    public void updateRange() {
//        if( _ea == null ) {
//            return; // unchanged
//        }
//
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        _ism = cm.getRange( _ea );//_isf.createModel();
//        _isc = cm._isf.createController( _ism );creates new contorller when range changes.. why not keep same
//        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
//    }

//    public void set( String coordinateType, int value ) {
//
//    }
//
//    public void set( CtCoordinatesTypes ct, int pkCoordinate ) {
//        CtCoordinates c = (CtCoordinates)( CtSession.Current().get( CtCoordinates.class, pkCoordinate ) );
//        set( ct, c );
//    }

    public void set( CtCoordinatesTypes ct, int coordinateValue ) {//CtCoordinates c ) {
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
        _cm.setOrdinate( ct, coordinateValue );
//        _cm.updateRange();
//        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
    }

    public void set( String coordinateType, int coordinateValue ) {//CtCoordinates c ) {
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
        _cm.setOrdinate( coordinateType, coordinateValue );
//        _cm.updateRange();
//        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
    }

    public void set( CtExperimentsAxes ea, int coordinateValue ) {//CtCoordinates c ) {
//        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
        _cm.setOrdinate( ea, coordinateValue );
//        _cm.updateRange();
//        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
    }

    public void add( CtCoordinatesTypes ct, int coordinateValue ) {
        _cm.addOrdinate( ct.getName(), coordinateValue );
    }

    public void add( String coordinateType, int coordinateValue ) {
        _cm.addOrdinate( coordinateType, coordinateValue );
    }

    public int getTimeOrdinate( CtImages i ) { // utility method, required so many places..
        return _cm.getOrdinate( i, CtCoordinatesModel.COORDINATE_TYPE_TIME );
    }
//    public void add( CtExperimentsAxes ea, int coordinateValue ) {
//        _cm.add( ea, coordinateValue );
//    }

}
