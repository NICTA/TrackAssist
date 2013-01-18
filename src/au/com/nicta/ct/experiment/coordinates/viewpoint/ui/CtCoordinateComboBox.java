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

package au.com.nicta.ct.experiment.coordinates.viewpoint.ui;

import au.com.nicta.ct.ui.swing.util.CtKeyedComboItem;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.experiment.coordinates.CtOrdinate;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointModel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtCoordinateComboBox extends JPanel implements ActionListener {

    protected CtViewpointController _vc;
    protected String _coordinatesTypes;

    protected JComboBox _cb;
    protected JLabel _l;

    protected boolean _active = true;
    protected int _selected = -1;

//make this model do selection of within ranges of a specific axis, set the pos when user selects
//    public CtCoordinateComboBox( CtViewpointController vc, String coordinateType ) {
//        this( cc, (cc.getCoordinatesModel()).findAxis( coordinateType ) );
//    }
//
//    public CtCoordinateComboBox( CtCoordinatesController cc, CtCoordinatesTypes ct ) {
//        this( cc, (cc.getCoordinatesModel()).findAxis( ct ) );
//    }

    public CtCoordinateComboBox( CtViewpointController vc, String coordinatesTypes ) {//CtExperimentsAxes ea ) {
        this( vc, coordinatesTypes, false );
    }

    public CtCoordinateComboBox( CtViewpointController vc, String coordinatesTypes, boolean showLabel ) {//CtExperimentsAxes ea ) {
        super();

        _vc = vc;
        _coordinatesTypes = coordinatesTypes;

        setOpaque( false );
        setLayout( new FlowLayout() );

        if( showLabel ) {
            _l = new JLabel( "-" );
            add( _l );
        }

        _cb = new JComboBox();
        add( _cb );

        refresh();

        _cb.addActionListener( this );
    }

    public void setCoordinatesTypes( String coordinatesTypes ) {
        _coordinatesTypes = coordinatesTypes;

        refresh();
    }

    public void setActive( boolean active ) {
        _active = active;
    }

    public boolean getActive() {
        return _active;
    }

    @Override public void actionPerformed( ActionEvent e ) {

        JComboBox cb = (JComboBox)e.getSource();
        CtKeyedComboItem kci = (CtKeyedComboItem)cb.getSelectedItem();

        if( kci == null ) {
            return;
        }

        _selected = kci._key; //Integer.valueOf( kci._display );//kci._key; // remember regardless of whether we're actually setting the coord.

        if( _active ) {
//            CtCoordinatesTypes ct = _ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes();
//            _cc.set( ct, kci._key );
//System.out.println( " coord. change to "+kci );
            int value = Integer.valueOf( kci._display );
            _vc.setOrdinate( _coordinatesTypes, value );
        }

//        refresh();
    }

    public void refresh() {

//        CtCoordinatesTypes ct = _ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes();
        if( _l != null ) {
            _l.setText( _coordinatesTypes );// ct.getName() );
        }
        
        _cb.removeAllItems();

        if( !_active ) {
            _selected = -1;
        }

        CtViewpointModel vm = _vc.getViewpointModel();
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();
        // CtCoordinates c = cm.get( ct );

        int min = cm.getMinOrdinate( _coordinatesTypes );
        int max = cm.getMaxOrdinate( _coordinatesTypes );
        int current = vm.getOrdinate( _coordinatesTypes );

        for( int n = min; n <= max; ++n ) {

            CtOrdinate o = new CtOrdinate( _coordinatesTypes, n, CtOrdinate.CtValueType.VALUE_TYPE_ABSOLUTE ); // get image @ viewpoint but specify abs. pos. in this dim
            CtImages image = vm.getImage( o );

            if( image == null ) {
                continue; // TODO fix this bug (dave) - null after expt created and opened first time
            }

            CtSession.Current().refresh( image );

            Set< CtImagesCoordinates > ics = image.getCtImagesCoordinateses();

            Iterator i = ics.iterator();

            while( i.hasNext() ) {
                CtImagesCoordinates ic = (CtImagesCoordinates)i.next();
                CtCoordinates c_i = ic.getCtCoordinates();
                CtCoordinatesTypes ct_i = c_i.getCtCoordinatesTypes();

//                if( ct_i.getPkCoordinateType() != ct.getPkCoordinateType() ) {
                if( !ct_i.getName().equals( _coordinatesTypes ) ) {
                    continue;
                }

                String s = c_i.getName();

                if( s == null ) {
                    s = Integer.toString( c_i.getValue() );
                }

                int pk = c_i.getPkCoordinate();

                CtKeyedComboItem kci = new CtKeyedComboItem( pk, s );

                _cb.addItem( kci );

                boolean selected = false;

                if( _active ) {
 //                   if( c_i.getPkCoordinate() == c.getPkCoordinate() ) {
                    if( c_i.getValue() == current ) {
                        selected = true;
                    }
                }
                else {
                    if( c_i.getPkCoordinate() == _selected ) {
//                    if( c_i.getValue() == _selected ) {
                        selected = true;
                    }
                }

                if( selected ) {
                    _cb.setSelectedItem( kci );
                }
            }
        }
    }

    public JComboBox getComboBox(){
        return _cb;
    }


}
