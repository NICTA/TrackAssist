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

package au.com.nicta.ct.solution.graphics.canvas.tools.annotations;

import au.com.nicta.ct.db.hibernate.CtAnnotationsTypes;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtFrameRangeView;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author alan
 */
public class CtCreateAnnotationTool extends CtTool {
//    public CtZoomCanvas _zc;
//    public CtCanvasLayer cl;
//    public CtAnnotationsModel _am;
    public CtAnnotationsController ac;
//    CtAnnotationsPainter _ap;
//    CtImageSequenceController _isc;

//    JComboBox _selected;
//    JTextField _added;
//    JToggleButton _modeSelect;
//    JToggleButton _modeCreate;
//    JToggleButton _modeDelete;
    JComboBox typesCombo;
//    CtFrameRangeView _ctFrameRangeView;
//    JButton _deleteAll;

    public CtCreateAnnotationTool( CtToolsModel tm, String name, CtAnnotationsController ac ) {
        super(tm, name);
        this.ac = ac;
    }

    @Override protected String iconFile() {
        return "annotation_create.png";
    }

    @Override protected String toolTip() {
        return "Create new annotation";
    }

    @Override public void activate() {
        super.activate();
        ac.setMode( CtAnnotationsController.MODE_CREATE );
    }

    @Override public JComponent panel() {
        typesCombo = ac.createTypesCombo();
        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        p1.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        p1.add( new JLabel("Type:") );
        p1.add( typesCombo );

        JPanel p3 = new JPanel();
        p3.setOpaque(false);
        p3.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        p3.add( new JLabel( "Click to add annotations." ) );

        JPanel p2 = new JPanel();
        p2.setOpaque(false);
        p2.setLayout( new BoxLayout( p2, BoxLayout.PAGE_AXIS ) );
        p2.setOpaque( false );
        p2.add( p3 );
        p2.add( p1 );

        return p2;
    }

}
