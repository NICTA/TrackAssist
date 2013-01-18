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

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtFrameRangeView;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtDeleteAllDetectionsTool extends CtTool implements ActionListener {//, CtCoordinatesListener {

//    JTextField _index1;
//    JTextField _index2;
    CtFrameRangeView _frv;

    public static final String DELETE_VISIBLE_COMMAND = "DeleteVisible";
    public static final String DELETE_RANGE_COMMAND = "DeleteRange";

    public CtDeleteAllDetectionsTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "delete-all-detection" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

//        CtCoordinatesController.addCoordinatesListener( this );
//        onModelChanged();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

//    @Override public void onModelChanged() {
//        onIndexChanged();
//    }
//
//    @Override public void onRangeChanged() {
//        onIndexChanged();
//    }
//
//    @Override public void onIndexChanged() {
//
//        if( _index1 == null ) {
//            return;
//        }
//
//        String s = currentIndex2String();
//
//       _index1.setText( s );
//       _index2.setText( s );
//    }

    @Override public void updateEnabled() {

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            setEnabled( false );
            return;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            setEnabled( false );
            return;
        }
        
        Collection< CtDetections > cd = tm.getDetectionsInWindow();

        if( cd.isEmpty() ) {
            setEnabled( false );
        }

        setEnabled( true );
    }

    @Override protected String iconFile() {
        return new String( "detection_delete_all.png" );
    }

    @Override protected String toolTip() {
        return new String( "Delete all detections (this image)" );
    }

    @Override public void activate(){
    }

    @Override protected void updateButton( JToggleButton button ) {
//        button.setEnabled( _mode.isEnabled() && this.isEnabled() );
        boolean selected1 = button.isSelected();

        button.setEnabled( this.isEnabled() );
        button.setSelected( this.isActive() );

        boolean selected2 = button.isSelected();

        if( selected2 & (!selected1) ) { // if changed to selected:
            setActive( false );
        }
    }

//    String currentIndex2String() {
//        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();
//        int index = ism.getIndex() +1;
//
//        String s = int2String( index );
//
//        return s;
//    }
//    String int2String( int index ) {
//        String s = String.valueOf( index );
//
//        while( s.length() < 4 ) {
//            s = "0"+s;
//        }
//
//        return s;
//    }

    @Override public JComponent panel() {
        // blur:
        JPanel p6 = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
               p6.setOpaque( false );

        _frv = new CtFrameRangeView(); // tied to global time coord.
        p6.add( _frv );
//               String s = currentIndex2String();
//
//               _index1 = new JTextField( s );
//               _index2 = new JTextField( s );
//
//               p6.add( new JLabel( "Images" ) );
//               p6.add( _index1 );
//               p6.add( new JLabel( "to" ) );
//               p6.add( _index2 );
//               p6.add( apply );

        JPanel p7 = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
               p7.setOpaque( false );
               p7.add( new JLabel( "Delete" ) );

               JButton apply1 = new JButton( "Visible" );
                       apply1.setActionCommand( DELETE_VISIBLE_COMMAND );//DO_ONE_COMMAND );
                       apply1.addActionListener( this );

               JButton apply2 = new JButton( "Range" );
                       apply2.setActionCommand( DELETE_RANGE_COMMAND );//DO_ONE_COMMAND );
                       apply2.addActionListener( this );

               p7.add( apply1 );
               p7.add( apply2 );

        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );//new BoxLayout( p, BoxLayout.PAGE_AXIS ) );
        p.setOpaque( false );
        JPanel p2 = new JPanel();
        p2.setOpaque( false );
        p2.setLayout( new BoxLayout( p2, BoxLayout.PAGE_AXIS ) );
        p2.add( p6 );
        p2.add( p7 );
        p.add( p2, BorderLayout.NORTH );
        return p;
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return;
        }

        String s = ae.getActionCommand();

        if( s.equals( DELETE_VISIBLE_COMMAND ) ) {

            int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to delete all visible detections in the selected time-window?", "Delete All Detections", JOptionPane.YES_NO_OPTION );
            if( n != JOptionPane.YES_OPTION ) {
                return;
            }

            tc.deleteDetectionsInWindow();
        }

        if( s.equals( DELETE_RANGE_COMMAND ) ) {

            int n = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Are you sure you want to delete all the detections in the specified range of images?", "Delete All Detections", JOptionPane.YES_NO_OPTION );
            if( n != JOptionPane.YES_OPTION ) {
                return;
            }

            int index1 = _frv.getIndex1();
            int index2 = _frv.getIndex2();
//            String images1 = _index1.getText().trim();
//            String images2 = _index2.getText().trim();
//
//            int index1 = 0;
//            int index2 = 0;
//
//            try{
//                index1 = Integer.valueOf( images1 ) -1; // convert from 1 based to zero based indexing
//                index2 = Integer.valueOf( images2 ) -1;
//            }
//            catch( Exception e ) {
//                JOptionPane.showMessageDialog( CtFrame.find(), "Can't create detections: Image range not valid." );
//                return;
//            }

            tc.deleteDetectionsInRange( index1, index2 );
        }
    }
}
