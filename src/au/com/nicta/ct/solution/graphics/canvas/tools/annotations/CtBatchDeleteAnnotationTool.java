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
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtFrameRangeView;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author alan
 */
public class CtBatchDeleteAnnotationTool extends CtTool {

    public CtAnnotationsController ac;

    JComboBox typesCombo;
    CtFrameRangeView ctFrameRangeView;
    JButton apply;

    public CtBatchDeleteAnnotationTool( CtToolsModel tm, String name, CtAnnotationsController ac ) {
        super(tm, name);
        this.ac = ac;
    }

    @Override protected String iconFile() {
        return "annotation_delete_all.png";
    }

    @Override protected String toolTip() {
        return "Delete annotations by type / time";
    }

    @Override public void activate() {
        super.activate();
        // default mode
        ac.setMode( CtAnnotationsController.MODE_SELECT );
    }

    @Override public JComponent panel() {
        typesCombo = ac.createTypesCombo();

        JPanel p3 = new JPanel();
        p3.setOpaque(false);
        p3.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        p3.add( new JLabel( "Delete annotations." ) );

        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        p1.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        p1.add( new JLabel("Type:") );
        p1.add( typesCombo );

        ctFrameRangeView = new CtFrameRangeView(CtFrameRangeView.CtFrameRangeLayout.HORIZONTAL);
        
        apply = new JButton( "Delete" );
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAll( ctFrameRangeView.getIndex1(), ctFrameRangeView.getIndex2() );
            }
        });

        JPanel p5 = new JPanel();
        p5.setOpaque(false);
        p5.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        p5.add( apply );

        JPanel p4 = new JPanel();
        p4.setOpaque(false);
        p4.setLayout( new BoxLayout(p4, BoxLayout.PAGE_AXIS) );
        p4.add( p3 );
        p4.add( p1 );
        p4.add( ctFrameRangeView );
        p4.add( p5 );
        
        return p4;
    }

    public void deleteAll( int startFrameIdx, int endFrameIdx ) {
        CtAnnotationsController ac = CtAnnotationsController.get();
        CtAnnotationsTypes at = ac.getCurrentType();

        String msg =
                  "Delete Annotation of type '"
                + at.getValue()
                + "' from frame " + (startFrameIdx+1) + " to " + (endFrameIdx+1);
        int result = JOptionPane.showConfirmDialog( CtPageFrame.find(), msg, "Are you sure?", JOptionPane.YES_NO_OPTION );
        if( result == JOptionPane.YES_OPTION ) {
            ac.removeRangeType( startFrameIdx, endFrameIdx, at );
//            _am.remove( startFrameIdx, endFrameIdx, at );
//            _am.fireModelChanged();
        }
    }
}
