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

package au.com.nicta.ct.experiment.graphics.canvas.composite;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.db.hibernate.CtCoordinates;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.db.hibernate.CtExperimentsAxes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtImagesCoordinates;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtCoordinateComboBox;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointListener;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.graphics.canvas.images.CtImageResultTool;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import au.com.nicta.ct.orm.mvc.images.CtCachedImages;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import ij.ImagePlus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class CtCompositeTools extends CtImageResultTool /*JPanel*/ implements ActionListener, CtChangeListener {//CtCoordinatesListener {

    public static final String IMAGE_RESULT_KEY_COMPOSITE = "Composite";

    String[] _colorCodes = { CtCompositeController._red, CtCompositeController._red };

//    CtImageResultsCanvasLayer _ircl;
//    CtZoomCanvas       _zc;
//    CtImageResult      _ir;
//    CtCanvasLayer      _cl;

    CtViewpointController _vc;
    CtCompositeController _ctc;

    JPanel _p;
    JComboBox _compositeAxis;
//    JComboBox _selectedAxisValue;
    CtCoordinateComboBox _selectedAxisValue;
    JComboBox _color;
    JButton _add;
    JButton _remove;

    private CtCompositeTableModel _collationInfoTableModel;
    private JTable _collationInfoTable;
    
    public CtCompositeTools( CtZoomCanvasPanel zcp ) {//CtZoomCanvas zc ) {//, CtMicrowellsModel mm ) {
        this( zcp, CtCoordinatesModel.COORDINATE_TYPE_TIME );
    }

    public CtCompositeTools( CtZoomCanvasPanel zcp, String sequenceAxis ) {//, CtMicrowellsModel mm ) {
        super( zcp, IMAGE_RESULT_KEY_COMPOSITE );

//        CtExperiments e = (CtExperiments)CtObjectDirectory.get( "experiment" );
//        _ir = new CtImageResult();

        _ircl.setImageResult( IMAGE_RESULT_KEY_COMPOSITE, new CtImageResult() );

//        try {
//            _cl = _zc.createLayer( "Composite" );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }

        CtViewpointController vc = zcp.getViewpointController();

        _vc = vc;
        _ctc = new CtCompositeController( sequenceAxis );

        _p = new JPanel();
//        _selectedRow       = new JComboBox();
//        _selectedCol       = new JComboBox();
        _compositeAxis      = new JComboBox();
        _selectedAxisValue = new CtCoordinateComboBox( vc, sequenceAxis );//new JComboBox();
        _selectedAxisValue.setActive( false );
        _color             = new JComboBox();

        _add    = new JButton( "Add"    );
        _remove = new JButton( "Remove" );

        _compositeAxis     .addActionListener( this );
   //     _selectedAxisValue.addActionListener( this );
        _add              .addActionListener( this );
        _remove           .addActionListener( this );

        _collationInfoTableModel = new CtCompositeTableModel( this, "" );
        _collationInfoTable      = new JTable( _collationInfoTableModel );

        populateAxisCombo();
        populateAxisValues();
        populateColorCombo();
        placeComponents();

//        CtImageListener.setupListener( this );
//        CtCoordinatesController.addCoordinatesListener( this );
        _vc.addListener( this );

        // call onImageChanged() when visible
        _p.addComponentListener( new ComponentListener() {
            public void componentResized(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {
                onImageChanged();
            }
            public void componentHidden(ComponentEvent e) {}
        });

        // revalidate to fix the layout when undock any toolbar
// DAVE: Removed, I suspect is not needed as we fixed this centrally??
//        this.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                getRootPane().revalidate();
//            }
//        });
    }

    public JComponent getComponent() {
        return _p;
    }
//    public CtCanvasLayer getCanvasLayer() {
//        return _cl;
//    }
//
//    public CtImageResult getImageResult(){ DAVE: Move to internal not requiring external setup
//        return _ir;
//    }

    @Override public void actionPerformed( ActionEvent ae ) {

        if( ae.getSource().equals( _compositeAxis ) ) {
            populateAxisValues();
            setOriginalInfo();
//            onImageChanged(); // rerender
//            _ircl.repaint();
            return;
        }

//        if( ae.getSource().equals( _selectedAxisValue ) ){
//            return; // do nothing (yet).
//        }

        // now only these two to deal with
        if( ae.getSource().equals( _add ) ){
            
            if( !_ctc._ctm.getAxisName().equals( _compositeAxis.getSelectedItem().toString() ) ){
                _ctc.clearSequences();
                _collationInfoTableModel.resetModel( _compositeAxis.getSelectedItem().toString() );
                setOriginalInfo();
            }

//            String selectedAxis =  _compositeAxis.getSelectedItem().toString();
            int selectedAxisValue = selectedAxisValue();//Integer.valueOf( _selectedAxisValue.getSelectedItem().toString() );
            String updatedMessage = _compositeAxis.getSelectedItem().toString()+":"+_selectedAxisValue.getComboBox().getSelectedItem().toString();
            
            String colorCode = "";
            if( _color.getSelectedItem().toString().equals( CtCompositeController._green ) ){
                colorCode = CtCompositeController._green;
            }
            else if( _color.getSelectedItem().toString().equals( CtCompositeController._red ) ){
                colorCode = CtCompositeController._red;
            }
            else if( _color.getSelectedItem().toString().equals( CtCompositeController._blue ) ){
                colorCode = CtCompositeController._blue;
            }

//            _ctc.addNewSequence( selectedAxis, selectedAxisValue, colorCode, _ism );
            _ctc._ctm.addNewSequence( selectedAxisValue, colorCode );
            _collationInfoTableModel.replaceRow( colorCode, updatedMessage );
            setOriginalInfo();
//            fireImageResultProcessChanged();

            onImageChanged(); // rerender
//            _ircl.repaint();
            return;
        }
        
        if( ae.getSource().equals( _remove ) ){

            int selectedAxisValue = selectedAxisValue();//Integer.valueOf( _selectedAxisValue.getSelectedItem().toString() );
            int[] _selectedRows = _collationInfoTable.getSelectedRows();
            for( int _selectedRowIndex = 0 ; _selectedRowIndex < _selectedRows.length ; _selectedRowIndex++ ){

                int row = _selectedRows[ _selectedRowIndex ];
                _ctc.deleteSequence( _compositeAxis.getSelectedItem().toString(), selectedAxisValue, (String) _collationInfoTableModel.getValueAt( row, 0 ) );
                _collationInfoTableModel.replaceRow( (String) _collationInfoTableModel.getValueAt( row, 0 ), CtCompositeTableModel._notUsed  );
            }

//            fireImageResultProcessChanged();

            onImageChanged(); // rerender
//            _ircl.repaint();
            return;
        }
    }

    int selectedAxisValue() {
        int selectedAxisValue = Integer.valueOf( _selectedAxisValue.getComboBox().getSelectedItem().toString() );
        selectedAxisValue -= 1; // as they're not zero based
        return selectedAxisValue;
    }

    // DAVE: TODO move to viewpoint listening.
    @Override public void propertyChange( PropertyChangeEvent evt ) { // viewpoint change
//        fireImageResultProcessChanged();
        String s = evt.getPropertyName();

        if(    s.equals( CtViewpointListener.EVT_ORDINATES_CHANGED )
            || s.equals( CtViewpointListener.EVT_IMAGE_CHANGED ) ) {
            onImageChanged();
        }

        super.propertyChange( evt );
    }
//    public void onModelChanged() {
//        _ctc.clearSequences();
//        _collationInfoTableModel.resetModel( "" );
//        onImageChanged();
//    }
//    public void onRangeChanged() {
//        _ctc.clearSequences();
//        _collationInfoTableModel.resetModel( "" );
//        onImageChanged();
//    }
//    public void onIndexChanged() {
//        onImageChanged();
//    }

//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {
//        CtExperimentModel em = CtExperimentModel.get();
//        CtImageSequenceModel ism = em._isf.getModel();
    public void onImageChanged() {

//        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();
//
//        if( ism == null ) {
//            return;
//        }
////        CtImageSequenceModel _ismOriginal = _isf.getModel();
//        CtImages ci = ism.current();

        CtImages i = _vc.getViewpointModel().getImage();
        setOriginalInfo( i );

        try {
//            ImagePlus imo = CtCachedImages.Get( i );
//
//            if( imo == null ) {
//                return;
//            }

            // only add collate when its visible
            CtImageResult ir = getImageResult();

//            if( _p.isVisible() ) {
            if( _ircl.isSelected( _imageResultKey ) ) {
                ir.setIP( _ctc.collateImages( _vc, i ) );//, ism.currentIndex() ) );
            }
//            else {
//                ir.setIP( imo.getProcessor() );
//            }
            fireImageResultProcessChanged();
//            _ircl.onImageResultChanged( _imageResultKey );
//            _ir.refresh();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
//        _zc.repaint();
    }

//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
////    @Override public void onControllerChanged( CtImageSequenceController isc ){
//
////        System.out.println( " On Controller changed " );
//       _ctc.clearSequences();
//       _collationInfoTableModel.resetModel( "" );
//
//       onImageChanged( isf );
//    }

    private void populateAxisValues() {

//        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtExperimentModel em = CtExperimentModel.get();
        
        String coordinateType = ( _compositeAxis.getSelectedItem() ).toString();

        _selectedAxisValue.setCoordinatesTypes( coordinateType );
//        CtZoomCanvas zc = (CtZoomCanvas)_ircl.getParent();
//        CtCoordinateComboBox axisValuesCombo = new CtCoordinateComboBox( zc.getViewpointController(), coordinateType );//cc, coordinateType );
//        axisValuesCombo.setActive( false );
//
        // DAVE: WTF? Why are you copying the model out of this control?
//        JComboBox axisValues= axisValuesCombo.getComboBox();
//        ComboBoxModel values = axisValues.getModel();
//        _selectedAxisValue.setModel( values );
//        _selectedAxisValue.setSelectedIndex( 0 );

        _ctc._ctm.setCompositeAxisName( coordinateType );
    }

    private void populateAxisCombo() {

        CtCoordinatesModel cm = CtCoordinatesController.getModel();
//        CtExperimentModel em = CtExperimentModel.get();
        
        DefaultComboBoxModel allCoordinates = new DefaultComboBoxModel();
        for( CtExperimentsAxes ea : cm._am.axes() ) {
            allCoordinates.addElement( ea.getCtCoordinatesByFkCoordinate1().getCtCoordinatesTypes().getName() );
        }
        _compositeAxis.setModel( allCoordinates );
        _compositeAxis.setSelectedIndex( 0 );
    }

    private void setOriginalInfo(){

//        CtExperimentModel em = CtExperimentModel.get();
// DAVE: what image is this?
//        try{
//            CtCoordinatesController cc = CtCoordinatesController.get();
//            CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();
////            CtImageSequenceModel ism = em._isf.getModel();
//            CtImages ci = ism.get( 0 );
//
//            setOriginalInfo( ci );
//        }
//        catch( Exception e ){
//            e.printStackTrace();
//        }
        CtImages i = _vc.getViewpointModel().getImage();
        setOriginalInfo( i );
    }

    private void setOriginalInfo( CtImages ci ){

        try {
           
           Set< CtImagesCoordinates > ics = ci.getCtImagesCoordinateses();

           Iterator i = ics.iterator();

           while( i.hasNext() ) {
                CtImagesCoordinates ic = (CtImagesCoordinates)i.next();
                CtCoordinates c_i = ic.getCtCoordinates();
                CtCoordinatesTypes ct_i = c_i.getCtCoordinatesTypes();

                String s = ct_i.getName();
                if( s.equals( _compositeAxis.getSelectedItem().toString() ) ){
                    _collationInfoTableModel.resetOriginalAxis( s + ":" + c_i.getValue().toString() );
                    break;
                }
           }
        }
        catch( Exception e ) {
//            e.printStackTrace();
            // validly occurs when changing experiments, we get a call on model changed before the new image data is available
        }
    }

    private void populateColorCombo(){
        
        DefaultComboBoxModel cmbModel = new DefaultComboBoxModel();
        cmbModel.addElement( CtCompositeController._green );
        cmbModel.addElement( CtCompositeController._red );
        cmbModel.addElement( CtCompositeController._blue );

        _color.setModel( cmbModel );
    }

    private void placeComponents() {
        
        _p.setLayout( new BorderLayout() );
        _p.setBackground( CtConstants.NictaYellow );

        JPanel fullPanel = new JPanel();
        BoxLayout bl = new BoxLayout( fullPanel, BoxLayout.Y_AXIS );
        fullPanel.setLayout( bl );
        fullPanel.setOpaque( false );

        JPanel top = new JPanel( new FlowLayout() );
        top.setOpaque( false );
        top.add( new JLabel( "Axis" ) );
        top.add( _compositeAxis );
        top.add( _selectedAxisValue );

        JPanel top2 = new JPanel( new FlowLayout() );
        top2.setOpaque( false );
        top2.add( new JLabel( "Colour" ) );
        top2.add( _color );
        top2.add( _add );

        JPanel labelPanel = new JPanel( new FlowLayout() );
        labelPanel.setOpaque( false );
        labelPanel.add( new JLabel( "Compositing Information" ) );

        JPanel infoPanel = new JPanel( new FlowLayout() );
        infoPanel.setOpaque( false );
        infoPanel.add( _collationInfoTable );

        JPanel bottom = new JPanel( new FlowLayout() );
        bottom.setOpaque( false );
        bottom.add( _remove );

        top       .setAlignmentX( Component.CENTER_ALIGNMENT );
        top2      .setAlignmentX( Component.CENTER_ALIGNMENT );
        labelPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
        infoPanel .setAlignmentX( Component.CENTER_ALIGNMENT );
        bottom    .setAlignmentX( Component.CENTER_ALIGNMENT );

        fullPanel.add( top        );
        fullPanel.add( top2       );
        fullPanel.add( labelPanel );
        fullPanel.add( infoPanel  );
        fullPanel.add( bottom     );

        _p.add( fullPanel, BorderLayout.NORTH );
    }

}