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

import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author alan
 */
public class CtFrameRangeView extends JComponent {

    public static final int SEQUENCE_STRING_LENGTH = 4;
    public static final int MODE_CURRENT_INDEX = 0;
    public static final int MODE_MIN_MAX= 1;

    public CtViewpointController _vc;
    public int _mode = MODE_CURRENT_INDEX;

    public JLabel index1Label = new JLabel("Images");
    public JLabel index2Label = new JLabel("to");
    public JTextField index1 = new JTextField();
    public JTextField index2 = new JTextField();

    public enum CtFrameRangeLayout {
        HORIZONTAL,
        VERTICAL
    }

    public void setMode( int mode ) {
        _mode = mode;
        updateIndices();
    }

    public int getMode() {
        return _mode;
    }

    class CtFrameRangeCoordinatesListener implements CtCoordinatesListener {
        public void onModelChanged() {
            onIndexChanged();
        }

        public void onRangeChanged() {
            onIndexChanged();
        }

        public void onIndexChanged() {
            updateIndices();
        }
    }

    public void updateIndices() {

        String s1 = null;
        String s2 = null;

        int mode = getMode();

        if( mode == MODE_CURRENT_INDEX ) {
            s1 = currentIndex2String();
            s2 = s1;
        }
        else if( mode == MODE_MIN_MAX ) {
            s1 = minIndex2String();
            s2 = maxIndex2String();
        }

        index1.setText( s1 );
        index2.setText( s2 );
    }
    
    class CtFrameRangeViewpointListener implements CtChangeListener {
        @Override public void propertyChange( PropertyChangeEvent evt ) {

            if( getMode() != MODE_CURRENT_INDEX ) {
                return;
            }

            String s = evt.getPropertyName();
            
            if(    s.equals( CtViewpointListener.EVT_ORDINATES_CHANGED )
                || s.equals( CtViewpointListener.EVT_IMAGE_CHANGED ) ) {

                String index = currentIndex2String();
                index1.setText( index );
                index2.setText( index );
            }
        }
    }

    public CtFrameRangeView() { // for manually adding gui components
        this( CtFrameRangeLayout.HORIZONTAL );
    }
    
    public CtFrameRangeView( CtFrameRangeLayout layout ) { // for manually adding gui components
        createComponents( layout );
        CtCoordinatesListener cl = new CtFrameRangeCoordinatesListener();
        CtCoordinatesController.addCoordinatesListener( cl );
        cl.onModelChanged();
    }

    public CtFrameRangeView( CtViewpointController vc, CtFrameRangeLayout layout ) { // for manually adding gui components
        _vc = vc;
        createComponents( layout );
        CtFrameRangeViewpointListener cl = new CtFrameRangeViewpointListener();
        vc.addListener( cl );
        cl.propertyChange( new PropertyChangeEvent( vc, CtViewpointListener.EVT_IMAGE_CHANGED, null, null ) );
    }

    public void createComponents( CtFrameRangeLayout layout ) {
        switch( layout ) {
            case HORIZONTAL:
                horizontalLayout( this );
                break;
            case VERTICAL:
                verticalLayout( this );
                break;
        }
    }

    final void horizontalLayout(JComponent component) {
        component.setLayout(new FlowLayout(FlowLayout.CENTER));
        component.add( index1Label );
        component.add( index1 );
        component.add( index2Label );
        component.add( index2 );
    }

    final void verticalLayout(JComponent component) {
        component.setLayout(new GridBagLayout());

        final int padding = 5;

        GridBagConstraints bgc = new GridBagConstraints();
        bgc.ipady = 0;       //reset to default
        bgc.anchor = GridBagConstraints.LINE_END; //bottom of space
        bgc.gridx = 0;       //aligned with button 2
        bgc.gridy = 0;       //third row
        bgc.gridwidth = 1;   //2 columns wide
        bgc.insets = new Insets(padding,padding,padding,padding);
        component.add(index1Label, bgc);

        bgc.ipady = 0;       //reset to default
        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
        bgc.gridx = 1;       //aligned with button 2
        bgc.gridy = 0;       //third row
        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(0,0,0,paddingBetweenComponents);
        component.add(index1, bgc);

        bgc.ipady = 0;       //reset to default
        bgc.anchor = GridBagConstraints.LINE_END; //bottom of space
        bgc.gridx = 0;       //aligned with button 2
        bgc.gridy = 1;       //third row
        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(10,0,10,0);
        component.add(index2Label, bgc);

        bgc.ipady = 0;       //reset to default
        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
        bgc.gridx = 1;       //aligned with button 2
        bgc.gridy = 1;       //third row
        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(10,0,10,0);
        component.add(index2, bgc);
    }

    String currentIndex2String() {
        int index = 0;

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();

        if( _vc != null ) {
            index = _vc.getTimeOrdinate();
        }
        else {
//            index = cc.getCoordinatesModel().getOrdinate( CtCoordinatesModel.COORDINATE_TYPE_TIME );
            index = ism.getIndex();// +1;
        }

        String s = int2String( index );

        return s;
    }

    String minIndex2String() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();

        int index = ism.getMinIndex();// +1;

        String s = int2String( index );

        return s;
    }

    String maxIndex2String() {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceModel ism = cc.getImageSequenceModel(); //isf.getModel();//_il.getImageSequenceModel();

        int index = ism.getMaxIndex();// +1;

        String s = int2String( index );

        return s;
    }

    String int2String( int index ) {
        String s = String.valueOf( index );

        while( s.length() < SEQUENCE_STRING_LENGTH ) {
            s = "0"+s;
        }

        return s;
    }

    public int getIndex1() {
        return getIndex( index1 );
    }

    public int getIndex2() {
        return getIndex( index2 );
    }

    public int getIndex(JTextField t) {
        return Integer.valueOf( t.getText().trim() );
    }
}
