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

package au.com.nicta.ct.experiment.graphics.canvas.microwells;

import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsFactory.CtMicrowellsTypes;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.CtExperimentController;
import au.com.nicta.ct.experiment.CtExperimentListener;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtMicrowellsController implements CtExperimentListener, CtChangeListener {//CtCoordinatesListener {//CtImageSequenceListener {

    public static final String MICROWELLS_TYPE_PROPERTY = "microwells-type";
    public static final String MICROWELLS_DATA_PROPERTY = "microwells-data";

    CtMicrowellsFactory _mf;
    CtMicrowellsModel _mm;

    public static CtMicrowellsController get() {
        CtMicrowellsController mc = (CtMicrowellsController)CtObjectDirectory.get( CtMicrowellsController.name() );

        if( mc == null ) {
            mc = new CtMicrowellsController( new CtMicrowellsFactory() );// _em._cc, _em._isf );
        }

        return mc;
    }

    public CtMicrowellsController( CtMicrowellsFactory mf ) {
        this._mf = mf;

        CtExperimentController ec = CtExperimentController.get();
        ec.addExperimentListener( this ); // because microwells tied to experiment NOT solution

        CtObjectDirectory.put( name(), this );

        getMicrowellsModel();
    }

    public static String name() {
        return "microwells-controller";
    }

    public CtMicrowellsFactory getMicrowellsFactory() {
        return _mf;
    }

    public CtMicrowellsModel getMicrowellsModel() {
        if( _mm == null ) {
//            setMicrowellType( _mf.getDefaultWellType() );
            createModel();
        }

        assert( _mm != null );

        return _mm;
    }

    protected void createModel() {
        CtMicrowellsTypes mt = _mf.getDefaultWellType();
        _mm = _mf.createModel( mt, CtMicrowellsFactory.WELLS_GRID_SIZE, CtMicrowellsFactory.WELLS_GRID_SIZE );
        _mm.addModelChangeListener( this );
        _mm.fireModelChanged();
    }

    public void setMicrowellType( CtMicrowellsTypes mt ) {

        CtMicrowellsModel mm = getMicrowellsModel();

        String type0 = _mf.getWellTypeDescription( mm.getType() );
        String type1 = _mf.getWellTypeDescription( mt );

        if( type0.equals( type1 ) ) { // this is wrong.
            return;
        }

        mm.setMicrowellType( mt );
//        _mm.fireModelChanged();

//        onMicrowellsModelChanged();
//        _mm.addChangeListener( new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                onMicrowellsModelChanged();
//            }
//        }); DAVE: Not clear if I need this
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        onMicrowellsModelChanged();
    }
    
    public void onMicrowellsModelChanged() {
        CtMicrowellsModel mm = getMicrowellsModel();
        CtMicrowellsTypes mt = mm.getType();
        String typeValue = _mf.getWellTypeDescription( mt );
        String dataValue = mm.serialize();

        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );
        CtKeyValueProperties.setValue( CtKeyValueProperties.key( e, MICROWELLS_TYPE_PROPERTY, e.getPkExperiment() ), typeValue );
        CtKeyValueProperties.setValue( CtKeyValueProperties.key( e, MICROWELLS_DATA_PROPERTY, e.getPkExperiment() ), dataValue );
    }

    @Override public void onExperimentChanged( CtExperiments e ) {

        String typeValue = CtKeyValueProperties.getValue( CtKeyValueProperties.key( e, MICROWELLS_TYPE_PROPERTY, e.getPkExperiment() ) );
        String dataValue = CtKeyValueProperties.getValue( CtKeyValueProperties.key( e, MICROWELLS_DATA_PROPERTY, e.getPkExperiment() ) );

        CtMicrowellsModel mm = getMicrowellsModel();
        CtMicrowellsTypes mt = null;

        if( typeValue == null ) {
            mt = _mf.getDefaultWellType();
            typeValue = _mf.getWellTypeDescription( CtMicrowellsTypes.SQUARE );
//            CtKeyValueProperties.setValue( CtKeyValueProperties.key( e, MICROWELLS_TYPE_PROPERTY, e.getPkExperiment() ), typeValue );
        }
        else {
            mt = _mf.getWellType( typeValue );
        }

        setMicrowellType( mt );
//        _mm = _mf.createModel( mt, CtMicrowellsFactory.WELLS_GRID_SIZE, CtMicrowellsFactory.WELLS_GRID_SIZE );
//        _mm.addModelChangeListener( this );

        if( dataValue != null ) {
            mm.deserialize( dataValue ); // calls fireModelChanged
        }
//        else {
//            dataValue = mm.serialize();
//            CtKeyValueProperties.setValue( CtKeyValueProperties.key( e, MICROWELLS_DATA_PROPERTY, e.getPkExperiment() ), dataValue );
//        }

//        _mm.fireModelChanged(); // will cause us to be called to save these new values, even if not changed
    }

    public Collection< String > findMicrowellNames( Polygon p ) {
        Area a = new Area( p );

        return findMicrowellNames( a );
    }

    public Collection< String > findMicrowellNames( Area a0 ) {

        ArrayList< String > cs = new ArrayList< String >();

        for( ArrayList< CtMicrowell > al : _mm._wells ) {
            for( CtMicrowell mw : al ) {
                Area a1 = new Area( a0 );
                Path2D p2d = mw.getPath();
                Area a2 = new Area( p2d );
                a1.intersect( a2 );

                if( !a1.isEmpty() ) {
                    cs.add( mw.getName() );
                }
            }
        }

        return cs;
    }

}
