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

package au.com.nicta.ct.orm.mvc.images;

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowModel;
import ij.ImagePlus;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.HashMap;

/**
 * Data class for the sequence of images currently being operated on.
 * @author davidjr
 */
public class CtImageSequenceModel extends CtChangeModel implements CtChangeListener {

    public static final int INVALID_INDEX = -1;
//    protected ArrayList< CtImages > _images = new ArrayList< CtImages >();
//    protected HashMap< CtImages, Integer > _indices = new HashMap< CtImages, Integer >();
    protected String _coordinatesTypes = CtCoordinatesModel.COORDINATE_TYPE_TIME;
    protected int _currentIndex = 0;
    
    public int _interFrameInterval = 1000/30; // defaults to 30FPS in MS TODO make properties.

    public CtImageSequenceModel() {
        super( null );
    }

    public CtImageSequenceModel( String coordinatesTypes ) {
        super( null );
        _coordinatesTypes = coordinatesTypes;
    }

    public CtImageSequenceModel( CtImageSequenceModel ism ) throws IOException {
        super( null );
        _coordinatesTypes = ism._coordinatesTypes;
        _currentIndex = ism._currentIndex;
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        int index = cm.getOrdinate( _coordinatesTypes );

        if( index != _currentIndex ) {
            _currentIndex = index;
        }

        fireModelChanged(); // shouldnt go in circles because will eventually agree
    }

    public String getRange() {
        return _coordinatesTypes;
    }

    public void setRange( String coordinateTypes ) {
        _coordinatesTypes = coordinateTypes;
        reset();
    }

    public CtImages current() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        return cm.getImage( _coordinatesTypes, _currentIndex );
//        return _images.get( _currentIndex );
    }

    public int getIndex() {
        return _currentIndex;
    }

    public ImagePlus currentCachedImage() throws IndexOutOfBoundsException, IOException {
        return getCachedImage( _currentIndex );
    }

    public void reset() {
//        _currentIndex = 0;
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        _currentIndex = cm.getMinOrdinate( _coordinatesTypes );
//        _images.clear();
//        _indices.clear();
    }

    public int getMinIndex() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        return cm.getMinOrdinate( _coordinatesTypes );
    }

    public int getMaxIndex() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        return cm.getMaxOrdinate( _coordinatesTypes );
    }

    public int size() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        int range = cm.getRangeOrdinates( _coordinatesTypes );
        return range;
//        return _images.size();
    }

    public CtImages get( int index ) throws IndexOutOfBoundsException, IOException {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        return cm.getImage( _coordinatesTypes, index );
//        CtImages i = _images.get( index );
//        return i;
    }
    
    public ImagePlus getCachedImage( int index ) throws IndexOutOfBoundsException, IOException {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        CtImages i = cm.getImage( _coordinatesTypes, index );
//        CtImages i = _images.get( index );

        return CtCachedImages.Get( i );
    }

    public int minIndexInWindow( CtTimeWindowModel twm ) {
//        int future  = twm._future;
        int history = Math.abs( twm._history );
        int current = getIndex();// - and + is navigatin the current axes
        int minIndex = (current-history);
        return minIndex;
    }

    public int maxIndexInWindow( CtTimeWindowModel twm ) {
        int future  = twm._future;
//        int history = Math.abs( twm._history );
        int current = getIndex();// - and + is navigatin the current axes
        int maxIndex = (current+future);
        return maxIndex;
    }

    public HashMap< CtImages, Integer > getValidImagesIndices( CtTimeWindowModel twm ) {
        int future  = twm._future;
        int history = Math.abs( twm._history );
        int current = getIndex();// - and + is navigatin the current axes

        HashMap< CtImages, Integer > valid = new HashMap< CtImages, Integer >();

//        try {
        for( int index = (current-history); index <= (current+future); ++index ) {
            try {
//System.out.println( "relative index="+index);
                CtImages i = get( index );
//                int relative = current - index;
                valid.put( i, index );//relative );
            }
            catch( Exception e ){} // when I go past the ends of the video
        }

        return valid;
    }

    public void setCurrentIndex( int frameIndex ) {

        int selected = getIndex();

        if( selected == frameIndex ) {
            return;
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

//        if( frameIndex >= size() ) {
        if( frameIndex > cm.getMaxOrdinate( _coordinatesTypes ) ) {
            return;
        }

//        if( frameIndex < 0 ) {
        if( frameIndex < cm.getMinOrdinate( _coordinatesTypes ) ) {
            return;
        }

//        current( frameIndex );
        _currentIndex = frameIndex;

        fireModelChanged();
    }

}
