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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections;

import au.com.nicta.ct.db.entities.CtEntityPropertyTable;
import au.com.nicta.ct.db.entities.CtEntityPropertiesUtil;
import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.tools.CtCanvasTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsView;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author davidjr
 */
public class CtDetectionPropertiesTool extends CtCanvasTool implements TableModelListener {

    ArrayList< CtAbstractPair< CtItemState, CtItemState > > _mappings = new ArrayList< CtAbstractPair< CtItemState, CtItemState > >();

    JPanel _c ;
    CtEntityPropertyTable _ept;
    DefaultTableModel _dtm;
    DefaultTableModel _dtm2; // my hidden backup copy so I can spot changes..!
//    int _pkDetection = 0;
    CtDetections _d;
    boolean _addBlankRowOnTableChanged = false;

    public CtDetectionPropertiesTool( CtToolsModel tm ){
        super( tm, "detection-properties" );

        // enable when ONE detection is SELECTED
        CtTrackingController tc = CtTrackingController.get();
        tc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        tc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
//                if( isActive() ) {
                    updateEnabled();
//                }
            }
        });

        _mappings.add( new CtAbstractPair< CtItemState, CtItemState >( CtItemState.SELECTED, CtItemState.NORMAL ) );
        _mappings.add( new CtAbstractPair< CtItemState, CtItemState >( CtItemState.NORMAL, CtItemState.SELECTED ) );

        applyOnMouseClicked( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME ); // change detection selection state
        updateEnabled();

        createPanel();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    protected String iconFile() {
        return new String( "select_detection.png" );
//        return new String( "detection_properties.png" );
    }

    protected String toolTip() {
//        return new String( "Edit detection properties" );
        return new String( "Select detection[s]" );
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
        CtTrackingController tc = CtTrackingController.get();//(CtDetectionsController)CtObjectDirectory.get( CtDetectionsController.name() );

        if( tc == null ) {
            return false;
        }

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        int x = (int)Math.rint( zc.toNaturalX( e.getX() ) );
        int y = (int)Math.rint( zc.toNaturalY( e.getY() ) );

        Point2D p2d = new Point2D.Float( x, y );

//        dc.setDetectionsStatesAt( p2d, CtItemState.SELECTED );
//        tc.setDetectionsStatesInWindowAt( _mappings, p2d );
        boolean matchingDetections = tc.setDetectionsStatesInWindowAt( _mappings, p2d );
        return matchingDetections;
    }

    @Override public void updateEnabled() {

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel tm = tc.getModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }

        CtDetections d = null;

        boolean enabled = true;

        Collection< CtDetections > cd = tm.getDetectionsInWindow();

        if( cd.isEmpty() ) {
            enabled = false;
        }

        setEnabled( enabled ); // some detections to select

        // now update table properties:
        updateTable( tm );
    }

    void updateTable( CtTrackingModel tm ) {

        if( tm == null ) {
            clearTable();
            disableTable();
            return;
        }

        Collection< CtDetections > cd = tm.getDetectionsWithStateInWindow( CtItemState.SELECTED );

        if( cd.size() != 1 ) {
            clearTable();
            disableTable();
            return;
        }

//        clearTable();
        enableTable();

        CtDetections d = cd.iterator().next();

        _d = d;

//        List results = CtKeyValueProperties.findAllWithPojo( d, d.getPkDetection() );
//        List<CtEntityProperties> results = CtEntityPropertiesUtil.find(
//                d.getClass(),
//                d.getPkDetection(),
//                null );
//
//        if( results == null ) {
//            return;
//        }

//        String suffix = CtKeyValueProperties.getSuffixOfPojo( d, d.getPkDetection() );
        _ept.load( d.getClass(), d.getPkDetection() );

//        JOptionPane.showMessageDialog(null, _c, "Test", JOptionPane.PLAIN_MESSAGE);


//        _addBlankRowOnTableChanged = false;
//
//        Iterator<CtEntityProperties> i = results.iterator();
//
//        while( i.hasNext() ) {
//            CtEntityProperties p = i.next();
//
////            String fullKey = p.getName();
////            int index = fullKey.lastIndexOf( suffix );
////            String key = fullKey.substring( 0, index );
//
//            Vector v = new Vector();
//            v.add( p.getName() );
//            v.add( p.getValue() );
//
//            _dtm .insertRow( 0, v );
////            _dtm2.addRow( v ); added due to change listening
//        }
//
//        _addBlankRowOnTableChanged = true;
    }

    void clearTable() {
       if( _ept == null ) {
           _ept = new CtEntityPropertyTable();
       }
       _ept.clear();

//        _d = null;
//
//        if( _dtm == null ) {
//            return;
//        }
//
//        _dtm.setNumRows( 0 );
//        _dtm.setNumRows( 1 ); // default row
//
//        _dtm2.setNumRows( 0 );
//        _dtm2.setNumRows( 1 ); // default row
    }

    void enableTable() {
//        _c.setEnabled( true );
        _ept.setEnabled( true );
    }

    void disableTable() {
//        _c.setEnabled( false );
        _ept.setEnabled( false );
    }

    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        int rows = _dtm.getRowCount();

        // save any edited rows
        // delete any blank rows except last row.
        try {
            String key   = (String)_dtm.getValueAt( row, 0 );
            String value = (String)_dtm.getValueAt( row, 1 );

            key.trim(); // don't allow whitespace

            boolean removed = false;

            if(    ( rows > 1 )
                && ( row < (rows-1) ) ) {
                if( rowIsEmpty( key, value ) ) {
                    _dtm .removeRow( row );
                    _dtm2.removeRow( row );

                    removed = true;
                }
            }

            if( !removed ) {
                // if row has changed either key or value, save it
                String key2   = (String)_dtm2.getValueAt( row, 0 );
                String value2 = (String)_dtm2.getValueAt( row, 1 );

                if( column == 0 ) { // == key
                    if(    (  key2 != null       )
                        && ( !key2.isEmpty()     ) // old key was valid
                        && ( !key2.equals( key ) ) ) { // key has changed
//                            String fullKey = CtKeyValueProperties.key( _d, key2, _d.getPkDetection() );
//                            CtKeyValueProperties.delete( fullKey ); // erase old key
                              CtEntityPropertiesUtil.delete( _d.getClass(), _d.getPkDetection(), key2 );
                    }
    //                            else {
//                    String fullKey = CtKeyValueProperties.key( _d, key, _d.getPkDetection() );
//                    CtKeyValueProperties.persist( fullKey, value );  // save with new key
                    CtEntityPropertiesUtil.persist( _d.getClass(), _d.getPkDetection(), key, value );
    //                            }
                }
                else if( column == 1 ) { // == 1 == value
                    if(    ( value2 == null )
                        || ( value  == null )
                        || ( !value2.equals( value ) ) ) { // value has changed
                        if(    (  key != null )
                            && ( !key.isEmpty() ) ) { // key is valid
//                            String fullKey = CtKeyValueProperties.key( _d, key, _d.getPkDetection() );
                            CtEntityPropertiesUtil.setValue( _d.getClass(), _d.getPkDetection(), key, value );
                        }
                    }
                }
                else if( column == -1 ) {
                    

                }

                _dtm2.setValueAt( key,   row, 0 );
                _dtm2.setValueAt( value, row, 1 );
            }
        }
        catch( Exception ex ) {
//            if( rows > 0 ) {
//                if( row != (rows-1) ) {
//                    _dtm.removeRow( row );
//                }
//            }
        }
//        finally {
//            if( rows > 1 ) {
//                if( rowIsEmpty( key, value ) ) {
//                    _dtm.removeRow( row );
//                }
//            }
//        }
//        // if there is no longer a blank row, add a blank row:
//        // NO: better rule is: if last row is not blank, add a new row.
        if( !_addBlankRowOnTableChanged ) {
            return;
        }

        int finalRows = _dtm.getRowCount();

        try {
            String lastKey   = (String)_dtm.getValueAt( finalRows-1, 0 );
            String lastValue = (String)_dtm.getValueAt( finalRows-1, 1 );

            if( !rowIsEmpty( lastKey, lastValue ) ) {
                _dtm .setRowCount( finalRows+1 );
                _dtm2.setRowCount( finalRows+1 );
            }
        }
        catch( Exception ex ) {
//            _dtm.setRowCount( finalRows+1 );
        }
    }

    boolean rowIsEmpty( String key, String value ) {
        if(    (    (  key != null   )
                 && ( !key.isEmpty() ) )
            || (    (  value != null   )
                 && ( !value.isEmpty() ) ) ) {
            return false;
        }

        return true;
    }

    private void createPanel() {
        if( _ept == null ) {
            _ept = new CtEntityPropertyTable();
        }

        _c = new JPanel();
        _c.setLayout(new BorderLayout());

        Dimension d = new Dimension( CtToolsView.preferredMaximumSize().width, 120 );

        _ept.setMaximumSize( d );
        _ept.setPreferredSize( d );
        _ept.setOpaque(true);

        _c.setOpaque(false);
        _c.add(_ept, BorderLayout.NORTH );

//        _c.scrollPane.setPreferredSize( d );
//        _c.table.setMaximumSize( d );
//        _c.table.setPreferredSize( d );
    }

    @Override public JComponent panel() {
        return _c;
    }
}



