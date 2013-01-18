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

import au.com.nicta.ct.ui.style.CtComponentStyle;
import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointListener;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 2 parts: a combo box to choose the dimension (default to first)
         and a spinner to hold the value. Spinner listens to changes in the viewpoint (where relevant) and makes changes to viewpoint.
         combo makes changes to spinner.

                 Actions:
         on experiment change, dispose all
         on viewpoint close, dispose all
         on coordinate change, update spinner( if relevant )
         on combo change, load relevant values and selected pos for viewpoint.

 * @author davidjr
 */
public class CtViewpointComponents implements CtChangeListener, ActionListener, ChangeListener {

    CtViewpointController _vc;
//    String _coordinateType;
    JComboBox _cb;
    JSpinner _s;
    JToggleButton _tbZoom;
    JToggleButton _tbImage;
    JButton _zoomIn;
    JButton _zoomOut;

    public static void addComponentsTo( CtViewpointController vc, JComponent c ) {
        Collection< JComponent > cc = createComponents( vc );

        for( JComponent c_i : cc ) {
            c.add( c_i );
        }
    }

    public static Collection< JComponent > createComponents( CtViewpointController vc ) {

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        Collection< String > axesCoordinatesTypes = cm._am.axesCoordinatesTypes();

        if( axesCoordinatesTypes.isEmpty() ) {
            return new ArrayList< JComponent >(); // no components
        }

        CtViewpointComponents vcm = new CtViewpointComponents( vc );

        return vcm.getComponents();
    }

    public CtViewpointComponents( final CtViewpointController vc ) {
        super();

        _vc = vc;
        
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        Collection< String > axesCoordinatesTypes = cm._am.axesCoordinatesTypes();

        int size = axesCoordinatesTypes.size(); // must not be zero!

        String[] coordinatesTypes = new String[ size ];

        int n = 0;

        for( String s : axesCoordinatesTypes ) {
            coordinatesTypes[ n ] = s;  ++n;
        }
//        String[] coordinatesTypes = (String[])axesCoordinatesTypes.toArray();

        boolean zoomLocked = !vc.getPanZoomLock();
        boolean imageLocked = vc.getCoordinateLock( coordinatesTypes[ 0 ] );

        _zoomIn  = CtComponentStyle.createButton( CtApplication.datafile( "icon_zoom_in.png" ), "Zoom (+)" );
        _zoomOut = CtComponentStyle.createButton( CtApplication.datafile( "icon_zoom_out.png" ), "Zoom (-)" );
        _tbZoom  = CtComponentStyle.createToggleButton( CtApplication.datafile( "icon_zoom_lock.png" ), "Lock zoom & pan", zoomLocked );
        _tbImage = CtComponentStyle.createToggleButton( CtApplication.datafile( "icon_image_lock.png" ), "Lock image coordinate", imageLocked );

        _zoomIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vc.addZoomLevel( 1 ); // div 2 cos the space is always
            }
        });

        _zoomOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vc.addZoomLevel( -1 ); // div 2 cos the space is always
            }
        });

        _tbZoom.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vc.setPanZoomLock( !_tbZoom.isSelected() );
            }
        });
        
//        DefaultComboBoxModel cbm = new DefaultComboBoxModel( coordinatesTypes );
        _cb = new JComboBox( coordinatesTypes );
        _s = new JSpinner();
//        _cb.setModel( cbm );
        onCoordinateTypeChanged();
        _s.addChangeListener( this );
        _cb.addActionListener( this );
        _vc.addListener( this );
        _tbImage.addActionListener( this );
    }

    public Collection< JComponent > getComponents() {

        ArrayList< JComponent > al = new ArrayList< JComponent >();

        JPanel p = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        p.setOpaque( false );
        // component ordering:
        p.add( _zoomIn );
        p.add( _zoomOut );
        p.add( _tbZoom );
        p.add( new JSeparator( SwingConstants.VERTICAL ) );
        p.add( new JLabel( "Axis" ) );
        p.add( _cb ); // which dimension (coord. type)
        p.add( new JLabel( "Position" ) );
        p.add( _s ); // position in this dimension
        p.add( _tbImage );

        al.add( p );
        
        return al;
    }
//    public JSpinner getSpinner() {
//        return _s;
//    }
//
//    public JToggleButton getToggleButton() {
//        return _tb;
//    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        // this is called when the viewpoint has been changed..
        // the only possible effects/actions are:
        //    JComboBox _cb; - no change.
        //    JSpinner _s; - if the property change is change image, then change the selected value displayed
        //    JToggleButton _tbZoom; - no change
        //    JToggleButton _tbImage; - no change
        //    JButton _zoomIn; - no change
        //    JButton _zoomOut; - no change

        // possible viewpoint events:
//    public static final String EVT_VIEWPOINT_CHANGED = "evt-viewpoint-changed"; // called on ANY change to viewpoint, including all the below:
//
//    public static final String EVT_ORDINATES_CHANGED = "evt-ordinates-changed";
//    public static final String EVT_IMAGE_CHANGED = "evt-image-changed";
//    public static final String EVT_PAN_CHANGED = "evt-pan-changed";
//    public static final String EVT_ZOOM_CHANGED = "evt-zoom-changed";
        String s = evt.getPropertyName();

        if( s.equals( CtViewpointListener.EVT_IMAGE_CHANGED ) ) {
            onCoordinateTypeChanged(); // cause update of the
        }
    }

//    public void onCoordinatesChanged() {
//
//    }
    
    @Override public void stateChanged( ChangeEvent e ) {  // called by the spinner data changes
        SpinnerNumberModel sm = (SpinnerNumberModel)_s.getModel();
        int value = (Integer)sm.getValue();

        String coordinateType = (String)_cb.getSelectedItem();
        _vc.setOrdinate( coordinateType, value ); // move the view to this pos, regardless of whether it's locked
    }

    @Override public void actionPerformed( ActionEvent e ) { // only called by combo data changes OR toggle button changes
        Object o = e.getSource();

        if( o == _cb ) {
            onCoordinateTypeChanged();
        }

        if( o == _tbImage ) {
            onCoordinateLockChanged();
        }
    }

    public void onCoordinateLockChanged() {
        boolean isSelected = _tbImage.isSelected();
        String coordinateType = (String)_cb.getSelectedItem();
        _vc.setCoordinateLock( coordinateType, isSelected );
    }

    public void onCoordinateTypeChanged() {
        String coordinateType = (String)_cb.getSelectedItem();

        //_coordinateType = coordinateType;
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        int min   = cm.getMinOrdinate( coordinateType );
        int max   = cm.getMaxOrdinate( coordinateType );
        int value = _vc.getOrdinate( coordinateType );

        SpinnerNumberModel sm = new SpinnerNumberModel( value, min, max, 1 );

        _s.setModel( sm );

        boolean isLocked = _vc.getCoordinateLock( coordinateType );
        boolean isSelected = _tbImage.isSelected();

        if( isLocked != isSelected ) {
            _tbImage.setSelected( isLocked );
        }
    }
}
