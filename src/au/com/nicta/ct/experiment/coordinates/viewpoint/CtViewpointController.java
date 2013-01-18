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

package au.com.nicta.ct.experiment.coordinates.viewpoint;

import au.com.nicta.ct.ui.swing.util.CtTransientListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;

/**
 * A viewpoint has pan, zoom, and a full set of image coordinates. The image
 * coordinates may be the global set, or may be offset or absolutely positioned.
 * Manages a single viewpoint, that may be in some ways linked to other viewpoints.
 * Pan and zoom are linked by one mechanism, image another.
 *
 * Objects that display a viewpoint within the
 * @author davidjr
 */
public class CtViewpointController implements CtTransientListener {//extends CtChangeModel {

    CtViewpointModel _vm;
//    CtViewpointComponents _vc;
    
    public CtViewpointController() {
        this( new CtViewpointModel() );
    }

    public CtViewpointController( CtViewpointModel vm ) {//, CtViewpointComponents vc ) {
//        super( null );
        _vm = vm;
//        _vc = vc;
    }

    public CtViewpointModel getViewpointModel() {
        return _vm;
    }

    public void addListener( CtChangeListener cl ) {
        _vm.addListener( cl );
    }

    @Override public void stopListening() {
        // detach listeners..
        _vm.stopListening();
    }

//    public CtViewpointModel getViewpointModel() {
//        return _vm;
//    }

    public CtPanZoom getPanZoom() {
        return _vm.getPanZoom();
    }

    public void addZoomLevel( int z ) {
        CtPanZoom pz = _vm.getPanZoom();
        int level = pz.getLevel();
        pz.setLevel( level +z );
    }

    public boolean getPanZoomLock() {
        return _vm.isAttached();
    }

    public void setPanZoomLock( boolean lock ) {
        if( lock ) {
            _vm.attach();
        }
        else {
            _vm.detach();
        }
    }

    public boolean getCoordinateLock( String coordinatesType ) {
        return _vm.hasOrdinate( coordinatesType );
    }

    public void setCoordinateLock( String coordinatesType, boolean lock ) {

        boolean isLocked = getCoordinateLock( coordinatesType );

        if( lock ) {
            if( isLocked ) { // locked already, no change
                return;
            }

            // lock our viewpoint to current global coords.
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtCoordinatesModel cm = cc.getCoordinatesModel();
            int value = cm.getOrdinate( coordinatesType );

            insertOrdinate( coordinatesType, value );
//            addOrdinate( coordinatesType, value );
//            setOrdinate( coordinatesType, value );
        }
        else {  // make it unlocked
            if( !isLocked ) { // locked already, no change
                return;
            }
            
            // remove our ordinate, set viewpoint to global coords
//            CtCoordinatesController cc = CtCoordinatesController.get();
//            CtCoordinatesModel cm = cc.getCoordinatesModel();
//            int value = cm.getOrdinate( coordinatesType );

            removeOrdinate( coordinatesType );
        }

    }

    public void addTime( int t ) {
        addOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME, t );
    }

    public void addOrdinate( String coordinatesType, int value ) {

        // if locked, do this view only. Otherwise, do the main view
        if( _vm.hasOrdinate( coordinatesType ) ) {
            _vm.addOrdinate( coordinatesType, value );
        }
        else {
            CtCoordinatesController cc = CtCoordinatesController.get();
            cc.add( coordinatesType, value );
        }
    }
    
    public void setOrdinate( String coordinatesType, int value ) {

        if( _vm.hasOrdinate( coordinatesType ) ) {
            _vm.insertOrdinate( coordinatesType, value );
        }
        else {
            CtCoordinatesController cc = CtCoordinatesController.get();
            cc.set( coordinatesType, value );
        }

//        CtOrdinate o = new CtOrdinate( coordinatesType, value, CtValueType.VALUE_TYPE_ABSOLUTE );
//
//        _vm.setOrdinate( o );
    }

    public int getTimeOrdinate() {
        return _vm.getOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );
    }
    
    public int getOrdinate( String coordinatesType ) {
        return _vm.getOrdinate( coordinatesType );
    }

    public void insertOrdinate( String coordinatesType, int value ) {
        _vm.insertOrdinate( coordinatesType, value );
    }
    
    public void removeOrdinate( String coordinatesType ) {
        _vm.removeOrdinate( coordinatesType );
    }
}
