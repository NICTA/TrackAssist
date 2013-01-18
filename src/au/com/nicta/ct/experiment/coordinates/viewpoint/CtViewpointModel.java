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
import au.com.nicta.ct.experiment.coordinates.CtOrdinate;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * ViewPoint has many VIEWS of different types. All views share
 *
 * @author davidjr
 */
public class CtViewpointModel extends CtPanZoomListener implements CtCoordinatesListener, CtTransientListener {

//    CtPanZoom _pz; //how to link these between images?
    HashMap< String, CtOrdinate > _privateOrdinates = new HashMap< String, CtOrdinate >(); // key=coord type name, value = ordinate

//    CtChangeSupport _cs = new CtChangeSupport( this );
//    CtCoordinatesModel:: but extend to N offsets:
//    public String createSingleNativeSQLQuery( CtExperimentsAxes varying, int varyingValue, CtExperimentsAxes offset, int offsetValue ) {

    // just have some listeners... don't need view interface
//    HashMap< String, CtViewpointView > _
    public CtViewpointModel() {
//        super( null );
        CtCoordinatesController.addCoordinatesListener( this );
        onModelChanged(); // TODO how to unregister when viewpoints are destroyed?
    }

    @Override public void stopListening() {
        CtCoordinatesController.removeCoordinatesListener( this );
    }

    public CtImages getImage( CtOrdinate o ) {
        ArrayList< CtOrdinate > al = new ArrayList< CtOrdinate >();

        al.add( o );

        return getImage( al );
    }

    public CtImages getImage( Collection< CtOrdinate > co ) {

        Collection< CtOrdinate > co2 = _privateOrdinates.values();

        ArrayList< CtOrdinate > al = new ArrayList< CtOrdinate >();

        for( CtOrdinate o : co ) {
            al.add( o );
        }

        for( CtOrdinate o2 : co2 ) {

            boolean specified = false;

            for( CtOrdinate o1 : al ) {
                if( o1._coordinateTypeName.equals( o1._coordinateTypeName ) ) {
                    specified = true;
                    break; // will ignore this ordinate o2
                }
            }
            
            if( !specified ) {
                al.add( o2 );
            }
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        CtImages i = cm.getImageWithOrdinates( al );
        return i;
    }

    public CtImages getImage() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        CtImages i = cm.getImageWithOrdinates( _privateOrdinates.values() );
        return i;
    }

    public int getOrdinate( String dimension ) {

        CtOrdinate o = _privateOrdinates.get( dimension );

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        if( o != null ) {
            int value = o.getValue( cm );

            return value;
        }

        int value = cm.getOrdinate( dimension );

        return value;
    }

    public boolean hasOrdinate( String dimension ) {
        return _privateOrdinates.containsKey( dimension );
    }

    public void addOrdinate( String dimension ) { // default to current position
        addOrdinate( dimension, 0 ); // current position +0
    }

    public void addOrdinate( String dimension, int offsetValue ) { // default to current position

        int oldValue = 0;

        if( hasOrdinate( dimension ) ) {
            oldValue = getOrdinate( dimension );
        }
        else {
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtCoordinatesModel cm = cc.getCoordinatesModel();

            oldValue = cm.getOrdinate( dimension );
        }

        int newValue = oldValue + offsetValue;

        CtOrdinate o = new CtOrdinate( dimension, newValue, CtOrdinate.CtValueType.VALUE_TYPE_ABSOLUTE );

        insertOrdinate( o );
//        _privateOrdinates.put( dimension, o );
    }

    public void insertOrdinate( String dimension, int absoluteValue ) { // default to current position
        CtOrdinate o = new CtOrdinate( dimension, absoluteValue, CtOrdinate.CtValueType.VALUE_TYPE_ABSOLUTE );
        insertOrdinate( o );
    }

    public void insertOrdinate( CtOrdinate o ) {

        CtOrdinate o2 = _privateOrdinates.get( o._coordinateTypeName );

        int value = o.getValue();

        if( o2 != null ) {

            int value2 = o2.getValue();

            if( o2._valueType == o._valueType ) {
                if( value == value2 ) {
                    return; // no change.
                }
            }
        }

        // check bounds:
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        int minValue = cm.getMinOrdinate( o._coordinateTypeName );
        int maxValue = cm.getMaxOrdinate( o._coordinateTypeName );

        if(    ( value < minValue ) 
            || ( value > maxValue ) ) {
            return;
        }

        // no existing coord, or different coord values:
        _privateOrdinates.put( o._coordinateTypeName, o );

        onOrdinatesChanged();
    }

    public void removeOrdinate( String dimension ) {

        boolean valueChanged = false;

        if( _privateOrdinates.containsKey( dimension ) ) {
            valueChanged = true;
        }

        _privateOrdinates.remove( dimension );

        if( valueChanged ) {
            onOrdinatesChanged();
        }
    }
//    public CtPanZoom getPanZoom() {
//        return _pz;
//    }

    @Override public void onModelChanged() {
//        if( _lv != null ) {
//            _lv.repaint();
//        }
        onImageChanged();
    }

    @Override public void onRangeChanged() {
//        CtCoordinatesController cc = CtCoordinatesController.get();//em._cc;
//        CtImageSequenceModel ism = cc.getImageSequenceModel();
        onImageChanged();
    }

    @Override public void onIndexChanged() {
        onImageChanged();
    }


    public void onImageChanged() {
        fire( CtViewpointListener.EVT_IMAGE_CHANGED );
        onViewpointChanged();
    }

    public void onOrdinatesChanged() {
        fire( CtViewpointListener.EVT_ORDINATES_CHANGED );
        onViewpointChanged();
    }

    @Override public void onPanChanged() {
        fire( CtViewpointListener.EVT_PAN_CHANGED );
        onViewpointChanged();
    }

    @Override public void onZoomChanged() {
        fire( CtViewpointListener.EVT_ZOOM_CHANGED );
        onViewpointChanged();
    }

    public void onViewpointChanged() {
        fire( CtViewpointListener.EVT_VIEWPOINT_CHANGED );
    }
}
