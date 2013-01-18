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

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtExperimentsAxes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.orm.mvc.images.CtCachedImages;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceFactory;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import ij.ImagePlus;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtCoordinatesModel extends CtChangeModel implements CtChangeListener {

    public static final String COORDINATE_TYPE_TIME = "time";
    public static final String EVT_RANGE_CHANGED = "range-changed";
    public static final String EVT_COORDINATES_CHANGED = "coordinates-changed";

    protected CtImageSequenceFactory _isf;
    protected CtImageSequenceController _isc;
//    protected CtImageSequenceModel _ism;
    protected CtExperimentsAxes _ea;
    protected CtLimits _l;
    public CtAxesModel _am;
//    protected CtImageSequenceModel _ism;

    // currently viewing a set position in all axes, indicated by coordinates
    protected HashMap< CtExperimentsAxes, Integer > _axesCoordinates = new HashMap< CtExperimentsAxes, Integer >();
//    protected HashMap< CtExperimentsAxes, CtCoordinates > _axesCoordinates = new HashMap< CtExperimentsAxes, CtCoordinates >();
//    protected HashMap< CtExperimentsAxes, CtImageSequenceModel > _axesRanges = new HashMap< CtExperimentsAxes, CtImageSequenceModel >();

    protected HashMap< String, HashMap< Integer, Integer > > _imagesOrdinatesCache = new HashMap< String, HashMap< Integer, Integer > >(); // key= image key, a composite of its coords.

////////////////////////////////////////////////////////////////////////////////
//
// Assume all CtImages objects get preloaded into various containers.
// Coords. Model defines the current image, a position in all dimensions.
// Test cases are: Get next image in sequence, and get image with N specific positions or offsets.
// This upgrade should make playback and navigation much faster. With e.g. 3-5 channels
// and 600-1200 images we're talking 1800-6000 images. The actual image DATA must
// be cached separately, as it's too big.
    public CtImages getNextImage() { // specific positions in N axes where it differs from current position
//        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();
//
//        CtImageSequenceModel ism = getImageSequenceModel(); // varying axis
//
//        int currentIndex = ism.currentIndex();
//        int nextIndex = currentIndex +1;
//
//        al.add( new CtAbstractPair< String, Integer >( COORDINATE_TYPE_TIME, 1 ) );
//
//        return getImage( al );
        return getOffsetImage( COORDINATE_TYPE_TIME, 1 );
    }

    public CtImages getImage() {
        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();
        return getImage( al ); // empty list = current coords.
    }

    public CtImages getOffsetImage( String coordinateTypeName, int coordinateValue ) { // RELATIVE positions in N axes where it differs from current position
        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();

        al.add( new CtAbstractPair< String, Integer >( coordinateTypeName, coordinateValue ) );

        return getOffsetImage( al );
    }

    public CtImages getImage( CtCoordinatesTypes ct, int coordinateValue ) {
        return getImage( ct.getName(), coordinateValue );
    }

    public CtImages getImage( String coordinateTypeName, int coordinateValue ) { // specific positions in N axes where it differs from current position
        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();

        al.add( new CtAbstractPair< String, Integer >( coordinateTypeName, coordinateValue ) );

        return getImage( al );
    }

    public CtCoordinatesTypes getCoordinatesTypes( CtExperimentsAxes ea ) {
        CtCoordinatesTypes ct = ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes();
        return ct;
    }

    public CtImages getOffsetImage( Collection< CtAbstractPair< String, Integer > > cea ) { // RELATIVE positions in N axes where it differs from current position
        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();

        for( CtAbstractPair< String, Integer > ap : cea ) {

            CtExperimentsAxes ea = _am.find( ap._first );
            
            if( ea == null ) {
                return null; // error in input
            }

//            CtCoordinates c = _axesCoordinates.get( ea ); // this is the default, ie the current position.
//            int value = c.getValue();
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
            CtCoordinatesTypes ct = getCoordinatesTypes( ea );
            int value = _axesCoordinates.get( ea );

            String name = ct.getName();
            int offsetValue = value + ap._second;

            al.add( new CtAbstractPair< String, Integer >( name, offsetValue ) );
        }

        return getImage( al );
    }

    public CtImages getImageWithOrdinates( Collection< CtOrdinate > co ) { // RELATIVE positions in N axes where it differs from current position
        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();

        for( CtOrdinate o : co ) {

            CtExperimentsAxes ea = _am.find( o._coordinateTypeName );

            if( ea == null ) {
                return null; // error in input
            }

//            CtCoordinates c = _axesCoordinates.get( ea ); // this is the default, ie the current position.
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
            CtCoordinatesTypes ct = getCoordinatesTypes( ea );
            String name = ct.getName();

            int ordinateValue = o.getValue( this );
//            int value = c.getValue();
//            int ordinateValue = 0;//value;//value + ap._second;
//
//            if( o._valueType == CtOrdinate.CtValueType.VALUE_TYPE_ABSOLUTE ) {
//                ordinateValue = o._coordinateValue;
//            }
//            else { // relative:
//                ordinateValue = value + o._coordinateValue;
//            }

            al.add( new CtAbstractPair< String, Integer >( name, ordinateValue ) );
        }

        return getImage( al );
    }

    public CtImages getImage( Collection< CtAbstractPair< String, Integer > > cea ) { // specific positions in N axes where it differs from current position
        String key = getImageKey( cea );
        CtImages i = _cachedImages.get( key );
        return i;
    }
    
    public String getImageKey( Collection< CtAbstractPair< String, Integer > > cea ) { // specific positions in N axes where it differs from current position

        String key = "";

        // always enumerate ALL axes.. in fixed order (for this expt).
        Set< CtExperimentsAxes > axes = _axesCoordinates.keySet();

        Iterator i = axes.iterator();

        while( i.hasNext() ) {

            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();
//            CtCoordinates c = _axesCoordinates.get( ea ); // this is the default, ie the current position.
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
            CtCoordinatesTypes ct = getCoordinatesTypes( ea );
            String name = ct.getName();
//            int value = c.getValue();
            int value = _axesCoordinates.get( ea );

            for( CtAbstractPair< String, Integer > coordinateTypeValue : cea ) {
                if( name.equals( coordinateTypeValue._first ) ) {
                    value = coordinateTypeValue._second;
                    break;
                }
            }

            key = key + "," + name + "=" + value;
        }

        return key;
    }

    HashMap< String, CtImages > _cachedImages = new HashMap< String, CtImages >(); // probably very big
            
////////////////////////////////////////////////////////////////////////////////

    public CtCoordinatesModel( CtImageSequenceFactory isf ) {
        super( null );
//        default all coordinates to first of each type
//                does this mean each dim needs a sequence number?
        this._isf = isf;
    }

    public int getOrdinate( CtCoordinatesTypes ct ) {
        return getOrdinate( ct.getName() );
    }

    public int getOrdinate( String coordinateType ) {
//        CtCoordinates c = getOrdinate( findAxis( coordinateType ) );
//        if( c == null ) {
//            return -1;
//        }
//        return c.getValue();
        return getOrdinate( findAxis( coordinateType ) );
    }

    public int getOrdinate( CtExperimentsAxes ea ) {
        try {
            return _axesCoordinates.get( ea );
        }
        catch( NullPointerException npe ) {
            return -1;
        }
    }

    public int getRangeTime() {
        return getRangeOrdinates( CtCoordinatesModel.COORDINATE_TYPE_TIME );
    }

    public int getRangeOrdinates( CtCoordinatesTypes ct ) { // ie how many positions in this axis
        return getRangeOrdinates( ct.getName() );
    }

    public int getRangeOrdinates( String coordinateType ) { // ie how many positions in this axis
        CtExperimentsAxes ea = findAxis( coordinateType );
        CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
        CtCoordinates c2 = ea.getCtCoordinatesByFkCoordinate2();

        if(    ( c1 == null )
            || ( c2 == null ) ) {
            return -1;
        }

        int value1 = c1.getValue();
        int value2 = c2.getValue();
        int range = value2 - value1 +1;
        return range;
    }

    public int getMinOrdinate( String coordinateType ) {
        CtExperimentsAxes ea = findAxis( coordinateType );
        CtCoordinates c = ea.getCtCoordinatesByFkCoordinate1();
        if( c == null ) {
            return -1;
        }
        return c.getValue();
    }

    public int getMaxOrdinate( String coordinateType ) {
        CtExperimentsAxes ea = findAxis( coordinateType );
        CtCoordinates c = ea.getCtCoordinatesByFkCoordinate2();
        if( c == null ) {
            return -1;
        }
        return c.getValue();
    }

//    public CtCoordinates get( CtCoordinatesTypes ct ) {
//        return get( ct.getName() );
//    }
//
//    public CtCoordinates get( String coordinateType ) {
//        return get( findAxis( coordinateType ) );
//    }
//
//    public CtCoordinates get( CtExperimentsAxes ea ) {
//        if( ea == null ) {
//            return null;
//        }
//
//        return _axesCoordinates.get( ea );
//    }

    public void setOrdinate( CtCoordinatesTypes ct, int coordinateValue ) {// CtCoordinates c ) {
        setOrdinate( findAxis( ct.getName() ), coordinateValue );
    }
    
    public void setOrdinate( String coordinateType, int coordinateValue ) {// CtCoordinates c ) {
        setOrdinate( findAxis( coordinateType ), coordinateValue );
    }

    public void setOrdinate( CtExperimentsAxes ea, int coordinateValue ) {// CtCoordinates c ) {
        if( ea == null ) {
            return;
        }

        // check limits:
        CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
        CtCoordinates c2 = ea.getCtCoordinatesByFkCoordinate2();

//        if( c1 == null ) { shouldnt ever happen, more useful to just bomb.
//            return;
//        }
        int value1 = c1.getValue();
        int value2 = c2.getValue();

        if( coordinateValue < value1 ) {
            return;
        }

        if( coordinateValue > value2 ) {
            return;
        }

        // check for no change:
        Integer n = _axesCoordinates.get( ea );

        if( n != null ) {
            if( n.intValue() == coordinateValue ) {
                return; // unchanged
            }
        }

        // change the image sequence model to reflect we are at this coordinate
        _axesCoordinates.put( ea, coordinateValue );

        fireModelChanged();
//        _axesRanges.clear();
//        createRanges();
    }

    public void addOrdinate( String coordinateType, int coordinateValue ) {
        addOrdinate( findAxis( coordinateType ), coordinateValue );
    }

    public void addOrdinate( CtExperimentsAxes ea, int coordinateValue ) {// CtCoordinates c ) {
        if( ea == null ) {
            return;
        }

        int oldValue = 0;

        Integer n = _axesCoordinates.get( ea );

        if( n != null ) {
            oldValue = n;
        }

        int newValue = oldValue + coordinateValue;

        setOrdinate( ea, newValue );
    }
    
//    public void start(); // move to the start in all axes...

    public CtImageSequenceController getImageSequenceController() {
        if( _isc == null ) {
            _isc = _isf.createController();

            final CtImageSequenceModel ism = _isc.getModel();

            ism.addListener( new CtChangeListener() {
                @Override public void propertyChange( PropertyChangeEvent evt ) {
                    CtCoordinatesController cc = CtCoordinatesController.get();
                    cc.set( ism.getRange(), ism.getIndex() );
                }
            } );

            this.addListener( ism ); // make it listen to other sources of coord changes.
//            ism.setCurrentIndex( getOrdinate( ism.getRange() ) ); // set initial value
        }
        return _isc;
    }

    public CtImageSequenceModel getImageSequenceModel() {
        CtImageSequenceController isc = getImageSequenceController();
        return isc.getModel();
    }

//    public ImagePlus getSpecificImage( String coordinateType, int index ) throws IOException {
////        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
////        CtExperimentsAxes ea = findAxis( coordinateType );
////        CtImageSequenceModel ism = getImageSequenceModel(); // varying axis
////
////        int varyingIndex = ism.currentIndex() +1;
////
////        CtImages i = createSpecificImage( _ea, varyingIndex, ea, index );
//        ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >
//
//        CtImages i = createSpecificImage(  );
//    }
    
    public ImagePlus getImagePlus( CtImages i ) throws IOException {
        return CtCachedImages.Get( i );
    }

    public void clear() {
        doClear( true );
    }

    protected void doClear( boolean fireModelChanged ) {
        _isc = null;
        _ea = null;
        _l = null;
        _am = null;

        _axesCoordinates.clear();
//        _axesRanges.clear();
        _cachedImages.clear();
//        _range = null;
//        _limit1 = null;
//        _limit2 = null;

        if( fireModelChanged ) {
            fireModelChanged();
        }
    }

    public void refresh( CtExperiments e, String rangeCoordinateType, boolean showProgress ) {

        doClear( false );

        if( e == null ) {
            fireModelChanged();
            return;/// not expt to use
        }

        if( showProgress ) {
            CtCoordinatesLoader tl = new CtCoordinatesLoader( this, e, rangeCoordinateType );
            tl.enqueue();//start();
            return;
        }

        // else: do it normally:
// 1
        _l = new CtLimits();
        _l.updateLimits( e );

// 2
        _am = new CtAxesModel();
        _am.create( e );

// 3
        createCoordinates();

// 4
//        createRanges();
        createImages();

// 5
        if( e != null ) {
            setRange( rangeCoordinateType );
        } // ie time

// 6
        fireModelChanged();
    }

    public void createCoordinates() {
        Collection< CtExperimentsAxes > axes = _am.axes();

//        if( axes.size() == 0 ) {
//            return;
//        }
        
        Iterator i = axes.iterator();

        while( i.hasNext() ) {
            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();

            CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
//            CtCoordinates c2 = ea.getCtCoordinatesByFkCoordinate2();

            CtCoordinatesTypes ct1 = c1.getCtCoordinatesTypes();

//            String name = ct1.getName();

//            if( name.equals( rangeCoordinateType ) ) {
////                _range = ea;
////                _limit1 = c1;
////                _limit2 = c2;
//            }
//            else { // fixed:
                _axesCoordinates.put( ea, c1.getValue() );//c1 );
//            }
        }
    }

    public CtExperimentsAxes findAxis( CtCoordinatesTypes ct ) {
        return _am.find( ct );
    }

    public CtExperimentsAxes findAxis( String coordinateType ) {
        return _am.find( coordinateType );
    }

    public String getRangeType() {
        if( _ea == null ) {
            return null;
        }
        return _ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes().getName();
    }

//    public CtImageSequenceModel getRange( CtCoordinatesTypes ct ) {
//        CtExperimentsAxes ea = _am.find( ct );
//
//        if( ea == null ) {
//            return null;
//        }
//
//        CtImageSequenceModel ism = _axesRanges.get( ea );
//
//        return ism;
//    }
//
//    public CtImageSequenceModel getRange( String coordinateType ) {
//        CtExperimentsAxes ea = _am.find( coordinateType );
//
//        if( ea == null ) {
//            return null;
//        }
//
//        CtImageSequenceModel ism = _axesRanges.get( ea );
//
//        return ism;
//    }

    public void setRange( String coordinateType ) {
        CtExperimentsAxes ea = findAxis( coordinateType );

        setRange( ea );
    }

    public void setRange( CtExperimentsAxes ea ) {
        if(    ( _ea != null )
            && ( _ea.getPkExperimentAxis() == ea.getPkExperimentAxis() ) ) {
            return; // unchanged
        }

        _ea = ea;

        CtImageSequenceController isc = getImageSequenceController();

        isc.setRange( getCoordinatesTypes( ea ).getName() );

        fireRangeChanged();
//        updateRange();
    }

//    public void updateRange() {
//        if( _ea == null ) {
//            return; // unchanged
//        }
//
////        CtCoordinatesModel cm = (CtCoordinatesModel)_m;
//        CtImageSequenceModel ism = getRange( _ea );//_isf.createModel();
//        CtImageSequenceController isc = getImageSequenceController();//cm._isf.createController( _ism );creates new contorller when range changes.. why not keep same
//        CtImageSequenceModel ism2 = getImageSequenceModel();
//
//        if( ism2 == ism ) {
//            return; // unchanged
//        }
//
//        isc.setModel( ism );
//        fireRangeChanged();
////        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
//    }

    protected void fireRangeChanged() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        cc.onRangeChanged();
    }

    protected void fireIndexChanged() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        cc.onIndexChanged();
    }

    @Override public void fireModelChanged() {
        super.fireModelChanged();
        CtCoordinatesController cc = CtCoordinatesController.get();
        cc.onModelChanged();
    }

//    public void createRanges() {
//        // 2nd pass to build the ranges:
//        Collection< CtExperimentsAxes > axes = _am.axes();
//
//        Iterator i = axes.iterator();
//
//        while( i.hasNext() ) {
//
//            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();
//            CtImageSequenceModel ism = createRange( ea );
//
//            _axesRanges.put( ea, ism );
//        }
//    }

//    public int getOrdinate( CtImages i, String coordinateType ) {
//        return getOrdinate( i.getPkImage(), coordinateType );
//    }

    public int getTimeOrdinate( CtImages i ) {
        return getOrdinate( i, "time" );
    }

    public int getOrdinate( CtImages i, String coordinateType ) {
        int pkImage = i.getPkImage();
        HashMap< Integer, Integer > hm = _imagesOrdinatesCache.get( coordinateType );

        if( hm == null ) {
            hm = new HashMap< Integer, Integer >();
            _imagesOrdinatesCache.put( coordinateType, hm );
        }

        Integer n = hm.get( pkImage );

        if( n != null ) {
            return n;
        }

        int value = -1;

        Set< CtImagesCoordinates > s = i.getCtImagesCoordinateses();

        Iterator i_ic = s.iterator();

        while( i_ic.hasNext() ) {
            CtImagesCoordinates ic = (CtImagesCoordinates)i_ic.next();
            CtCoordinates c2 = ic.getCtCoordinates();
            CtCoordinatesTypes ct2 = c2.getCtCoordinatesTypes();

            String coordinateType2 = ct2.getName();

            if( coordinateType2.equals( coordinateType ) ) {
                value = c2.getValue();// -1;
                break;
            }
        }

        if( value >= 0 ) {
            hm.put( pkImage, value );
        }
        else { //        if( value == -1 ) {
            System.out.println( "Bad coordinate value: -1" );
        }

        return value;
    }

    public void setCoordinates( CtImages i ) {

        clear();

        // i will have a diff coord to one of ours..
        //therefore tells us the position to rebuild from.
        Collection< CtExperimentsAxes > axes = _am.axes();

        Iterator i_ea = axes.iterator();

        while( i_ea.hasNext() ) { // for each axis
            CtExperimentsAxes ea = (CtExperimentsAxes)i_ea.next();
            CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
            CtCoordinatesTypes ct1 = c1.getCtCoordinatesTypes();

            Set< CtImagesCoordinates > s = i.getCtImagesCoordinateses();

            Iterator i_ic = s.iterator();

            while( i_ic.hasNext() ) {
                CtImagesCoordinates ic = (CtImagesCoordinates)i_ic.next();
                CtCoordinates c2 = ic.getCtCoordinates();
                CtCoordinatesTypes ct2 = c2.getCtCoordinatesTypes();

                if( ct2.equals( ct1 ) ) {
                    _axesCoordinates.put( ea, c2.getValue() );//c2 );
                }
            }
        }
        
//        createRanges();
    }

//    public CtImageSequenceModel getRange( String coordinatesTypesName ) {
//
//        Set< Entry< CtExperimentsAxes, CtImageSequenceModel > > s = _axesRanges.entrySet();
//
//        for( Entry< CtExperimentsAxes, CtImageSequenceModel > e : s ) {
//            CtExperimentsAxes ea = (CtExperimentsAxes)e.getKey();
//
//            CtCoordinates c1 = ea.getCtCoordinatesByFkCoordinate1();
//            CtCoordinatesTypes ct1 = c1.getCtCoordinatesTypes();
//
//            String name = ct1.getName();
//
//            if( name.equals( coordinatesTypesName ) ) {
//                return _axesRanges.get( ea );
//            }
//        }
//
//        return null;
//    }
//
//    public CtImageSequenceModel getRange( CtExperimentsAxes ea ) {
//        return _axesRanges.get( ea );
//    }
//
//    public CtImageSequenceModel createRange( CtExperimentsAxes varying ) {
//        String nativeSQLQuery = createRangeNativeSQLQuery( varying ); // given current coordinates
//
//        if( nativeSQLQuery == null ) {
//            return null;
//        }
//
//        Session session = CtSession.Current();
//
//        // 2 queries: first is a native one which is tuned to efficiently get
//        // the PKs of the sequence, in order..
//        Query query = session.createSQLQuery( nativeSQLQuery )
//                        .addEntity( CtImages.class )
//                        .addScalar( "ordering" );
//
////        query.executeUpdate();
//
////        _ism.clear();
//        CtImageSequenceModel ism = _isf.createModel();
//
//        List l = query.list();
//
////System.out.println( "list results="+l.size());
//        Iterator i = l.iterator();
//
//        while( i.hasNext() ) {
//
//            Object[] o = (Object[])i.next();
//
//            CtImages image = (CtImages)o[ 0 ];
//
//            try {
////                System.out.println( "URI="+image.getUri());
//                ism.add( image );
//            }
//            catch( IOException ioe ) {
//                System.err.println( "Can't load image expected as part of sequence. " );
//                System.err.println( ioe );
//            }
//        }
//
//        ////////////////////////////////////////////////////////////////////////
//        // add self as sole listener to image sequences:
//        ////////////////////////////////////////////////////////////////////////
//        ism.addModelChangeListener(
//            new CtChangeListener() {
//                @Override public void propertyChange( PropertyChangeEvent evt ) {
//                    CtCoordinatesController cc = CtCoordinatesController.get();
////                    CtCoordinatesModel cm = cc.getCoordinatesModel();
//                    cc.onIndexChanged();
//                }
//            }
//        );
//
//        return ism;
//    }
//
//    public String createRangeNativeSQLQuery( CtExperimentsAxes varying ) {
//
//        CtExperiments e = _am.experiment();
//
//        if( e == null ) {
//            return null;
//        }
//
//        int pkExperiment = e.getPkExperiment();
////        Collection< CtExperimentsAxes > axes = _am.axes();
//
//        // get the range of values given we are at the specified coordinates
//        String query1 =
////            "SELECT i.fk_image, uri, c.value AS ordering FROM "
//            "SELECT i2.*, c.value AS ordering FROM "
//          + "(SELECT fk_image, uri FROM ct_images_coordinates ic1 "
//          + "INNER JOIN ct_images i1 ON ic1.fk_image = i1.pk_image "
//          + "INNER JOIN ct_coordinates c1 ON ic1.fk_coordinate = c1.pk_coordinate "
//          + "WHERE ( i1.fk_experiment = " + pkExperiment + " ) " //-- filter parameter
//          + " AND ( ";
//
//        Set< CtExperimentsAxes > axes = _axesCoordinates.keySet();
//
//        int dimensions = axes.size();
//
////        if( dimensions == 0 ) {
////            return;
////        }
//
//        boolean first = true;
//
//        Iterator i = axes.iterator();
//
//        while( i.hasNext() ) {
//
//            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();
//
//            if( ea.equals( varying ) ) {
//                continue;
//            }
//
//            if( !first ) {
//                query1 += " OR ";
//            }
//            else {
//                first = false;
//            }
//
//            CtCoordinates c = _axesCoordinates.get( ea );
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
//
//            int pkCoordinateType = ct.getPkCoordinateType();
//            int value = c.getValue();
//
//            query1 += "( ( c1.fk_coordinate_type = ";
//            query1 += pkCoordinateType;
//            query1 += " ) AND ( c1.value = ";
//            query1 += value;
//            query1 += " ) ) ";
//        }
//
//        if( !first ) {
//            query1 += " OR ";
//            first = false;
//        }
//
////        CtCoordinatesTypes ct1 = _limit1.getCtCoordinatesTypes();
//        CtCoordinatesTypes ct1 = varying.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes();
//        int pkCoordinateType = ct1.getPkCoordinateType();
//
//        query1 += "( c1.fk_coordinate_type = ";
//        query1 +=  pkCoordinateType;
//        query1 += " ) ) ";
//
//        String query2 =
////            "     ) "
////          + "AND (    ( ( c1.fk_coordinate_type = 1 ) AND ( c1.value = 1 ) ) " // --x this is a specified position in a dimension
////          + "      OR ( ( c1.fk_coordinate_type = 2 ) AND ( c1.value = 1 ) ) " // --y this is a specified position in a dimension
////          + "      OR ( ( c1.fk_coordinate_type = 3 ) AND ( c1.value = 1 ) ) " // --z this is a specified position in a dimension
////          + "      OR ( ( c1.fk_coordinate_type = 4 ) AND ( c1.value = 2 ) ) " // --c this is a specified position in a dimension
////          + "      OR ( ( c1.fk_coordinate_type = 5 ) ) " // -- this is the variable dimension/range of the sequence
////          + "    ) " // -- each line programmatically added from the axes defined
//            " GROUP BY fk_image, uri "
//          + " HAVING COUNT( * ) > " + (dimensions-1) +" ) AS i" // -- because eg (5-1)=4 dimensions fixed, 1 variable
//          + " INNER JOIN ct_images_coordinates ic ON i.fk_image = ic.fk_image "
//          + " INNER JOIN ct_coordinates c ON ic.fk_coordinate = c.pk_coordinate "
//          + " INNER JOIN ct_images i2 on i.fk_image = i2.pk_image "
//          + " WHERE c.fk_coordinate_type = " + pkCoordinateType // type of the varying coordinate
//          + " ORDER BY c.value ASC "; // ensure they're in order
//
//          String query = query1 + query2;
//
//          return query;
//    }

    public void createImages() {
        _cachedImages.clear();

        HashSet< CtImages > completed = new HashSet< CtImages >();
        
        Session s = CtSession.Current();
        s.beginTransaction();

        String hql = "FROM CtImages i "
                   + "INNER JOIN FETCH i.ctImagesCoordinateses ic "
                   + "INNER JOIN FETCH ic.ctCoordinates c " // put into hibernate cache, ie i obj should be complete
                   + "INNER JOIN FETCH c.ctCoordinatesTypes ct "
                   + "WHERE i.ctExperiments = :experiment "
                   + "ORDER BY i, ct";

        Query q = s.createQuery( hql );

        q.setParameter( "experiment", _am._e );
        List< Object[] > l = (List< Object[] >)q.list(); // all data should be accessed once, here.

        Iterator i = l.iterator();

        while( i.hasNext() ) {

            CtImages image = (CtImages)i.next();
//            Object[] os = (Object[])i.next();
//
//            CtImages image = (CtImages)os[ 0 ];

            if( completed.contains( image ) ) {
                continue; // already loaded this one.
            }

            ArrayList< CtAbstractPair< String, Integer > > al = new ArrayList< CtAbstractPair< String, Integer > >();

            // hibernate should've loaded the fetches objects, so:
//            CtImages image = (CtImages)           os[ 0 ];
//            CtImagesCoordinates ic = (CtImagesCoordinates)os[ 1 ];
//            CtCoordinates       c  = (CtCoordinates)      os[ 2 ];
//            CtCoordinatesTypes  ct = (CtCoordinatesTypes) os[ 3 ];

            // each image has multiple coordinates/types. build a list of them all.
            Set< CtImagesCoordinates > sic = image.getCtImagesCoordinateses(); // TODO: Check this is auto preloaded and not generating further DB hits..

            for( CtImagesCoordinates ic : sic ) {
                CtCoordinates c = ic.getCtCoordinates();
                CtCoordinatesTypes ct = c.getCtCoordinatesTypes();

                CtAbstractPair< String, Integer > ap = new CtAbstractPair< String, Integer >();

                ap._first  = ct.getName();
                ap._second = c.getValue();

                al.add( ap );
            }

            String key = getImageKey( al );

            _cachedImages.put( key, image );
            completed.add( image );
        }

        s.getTransaction().commit();
    }

//    public CtImages createSpecificImage( Collection< CtAbstractPair< String, Integer > > cea ) {
//        String nativeSQLQuery = createSpecificImageNativeSQLQuery( cea ); // given current coordinates
//
//        if( nativeSQLQuery == null ) {
//            return null;
//        }
//
//        Session session = CtSession.Current();
//
//        // 2 queries: first is a native one which is tuned to efficiently get
//        // the PKs of the sequence, in order..
//        Query query = session.createSQLQuery( nativeSQLQuery )
//                        .addEntity( CtImages.class );
//
//        List l = query.list();
//
////System.out.println( "list results="+l.size());
//        Iterator i = l.iterator();
//
//        while( i.hasNext() ) {
//
////            Object[] o = (Object[])i.next();
//
//            CtImages image = (CtImages)i.next();//o[ 0 ];
//
//            return image;
//        }
//
//        return null;
//    }
//
//    public String createSpecificImageNativeSQLQuery( Collection< CtAbstractPair< String, Integer > > cea ) { // specific positions in N axes where it differs from current position
//
//        CtExperiments e = _am.experiment();
//
//        if( e == null ) {
//            return null;
//        }
//
//        int pkExperiment = e.getPkExperiment();
//
//        Set< CtExperimentsAxes > axes = _axesCoordinates.keySet();
//
//        // get the range of values given we are at the specified coordinates
//        String query1 = "SELECT * FROM ct_images i ";
//        String query2 = "WHERE i.fk_experiment = " + pkExperiment + " AND ( ";
//
//        int d = 0;
//
//        Iterator i = axes.iterator();
//
//        while( i.hasNext() ) {
//
//            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();
//            CtCoordinates c = _axesCoordinates.get( ea ); // this is the default, ie the current position.
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
//            int pkCoordinateType = ct.getPkCoordinateType();
//            int value = c.getValue();
//            String name = ct.getName();
//
//            for( CtAbstractPair< String, Integer > coordinateTypeValue : cea ) {
//                if( name.equals( coordinateTypeValue._first ) ) {
//                    value = coordinateTypeValue._second;
//                    break;
//                }
//            }
////            if( ea.equals( offset ) ) {
////                value = offsetValue;
////            }
////            else if( ea.equals( varying ) ) {
////                value = varyingValue;
////            }
//
//            query1 += "INNER JOIN ct_images_coordinates ic"+d+" ON ic"+d+".fk_image = i.pk_image ";
//            query1 += "INNER JOIN ct_coordinates c"+d+" ON ic"+d+".fk_coordinate = c"+d+".pk_coordinate ";
//
//            if( d != 0 ) query2 += " AND ";
//
//            query2 += " ( c"+d+".fk_coordinate_type = "+pkCoordinateType+" AND c"+d+".value = "+value+" ) ";
//
//            ++d;
//        }
//
//        String query = query1 + query2 + " ) ";
//
//        return query;
//    }
    
//    public String createSingleNativeSQLQuery( CtExperimentsAxes varying, int varyingValue, CtExperimentsAxes offset, int offsetValue ) {
//
//        CtExperiments e = _am.experiment();
//
//        if( e == null ) {
//            return null;
//        }
//
//        int pkExperiment = e.getPkExperiment();
//
//        Set< CtExperimentsAxes > axes = _axesCoordinates.keySet();
//
//        // get the range of values given we are at the specified coordinates
//        String query1 = "SELECT * FROM ct_images i ";
//        String query2 = "WHERE i.fk_experiment = " + pkExperiment + " AND ( ";
//
//        int d = 0;
//
//        Iterator i = axes.iterator();
//
//        while( i.hasNext() ) {
//
//            CtExperimentsAxes ea = (CtExperimentsAxes)i.next();
//            CtCoordinates c = _axesCoordinates.get( ea );
//            CtCoordinatesTypes ct = c.getCtCoordinatesTypes();
//
//            int pkCoordinateType = ct.getPkCoordinateType();
//            int value = c.getValue();
//
//            if( ea.equals( offset ) ) {
//                value = offsetValue;
//            }
//            else if( ea.equals( varying ) ) {
//                value = varyingValue;
//            }
//
//            query1 += "INNER JOIN ct_images_coordinates ic"+d+" ON ic"+d+".fk_image = i.pk_image ";
//            query1 += "INNER JOIN ct_coordinates c"+d+" ON ic"+d+".fk_coordinate = c"+d+".pk_coordinate ";
//
//            if( d != 0 ) query2 += " AND ";
//
//            query2 += " ( c"+d+".fk_coordinate_type = "+pkCoordinateType+" AND c"+d+".value = "+value+" ) ";
//
//            ++d;
//        }
//
//        String query = query1 + query2 + " ) ";
//
//        return query;
//    }

}
