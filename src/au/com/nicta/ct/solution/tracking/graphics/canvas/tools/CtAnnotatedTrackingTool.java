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

package au.com.nicta.ct.solution.tracking.graphics.canvas.tools;

import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.solution.tracking.annotated.CtAnnotatedTracker;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author davidjr
 */
public class CtAnnotatedTrackingTool extends CtTool implements ActionListener {

    public static final String APPLY_COMMAND = "APPLY";
    
    JComponent _c;
    JTextField _id;
    JTextField _parent;

    public CtAnnotatedTrackingTool( CtToolsModel tm ) {
        super( tm, "create-tracks-annotated" );

        CtTrackingController sc = CtTrackingController.get();
        sc.addModelChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });
        sc.addAppearanceChangeListener( new CtChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateEnabled();
            }
        });

        createPanel();
        updateEnabled();
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    protected String iconFile() {
        return new String( "track_create_annotated.png" );
    }

    protected String toolTip() {
        return new String( "Create tracks from annotated detections" );
    }

    @Override public void updateEnabled() {

        CtTrackingController sc = CtTrackingController.get();

        if( sc == null ) {
            setEnabled( false );
            return;
        }

        setEnabled( true );
    }

    private void createPanel() {

        JLabel l1 = new JLabel( "Identity property" );
        JLabel l2 = new JLabel( "Parent property" );

        _id     = new JTextField( "id" );
        _parent = new JTextField( "parent" );

        _id    .setSize( new Dimension( 50, _id.getSize().height ) );
        _parent.setSize( new Dimension( 50, _parent.getSize().height ) );
        
       JButton apply = new JButton( "Apply" );
       apply.setActionCommand( APPLY_COMMAND );
       apply.addActionListener( this );

        JPanel pg = new JPanel();
        GroupLayout gl = new GroupLayout( pg );
        pg.setLayout( gl );
        pg.setOpaque( false );
        gl.setAutoCreateGaps( true );
        gl.setAutoCreateContainerGaps( true );

        gl.setHorizontalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent( l1)
                   .addComponent( l2 ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.LEADING )
                   .addComponent( _id )
                   .addComponent( _parent ) )
        );

        gl.setVerticalGroup(
           gl.createSequentialGroup()
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l1 )
                   .addComponent( _id ) )
              .addGroup( gl.createParallelGroup( GroupLayout.Alignment.BASELINE )
                   .addComponent( l2 )
                   .addComponent( _parent ) )
        );

        JPanel p3 = new JPanel( new FlowLayout( FlowLayout.TRAILING ) );
               p3.setOpaque( false );
               p3.add( apply );

        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.PAGE_AXIS ) );
        p.setOpaque( false );
        p.add( pg );
        p.add( p3 );
        _c = p;
    }

    @Override public JComponent panel() {
        return _c;
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        String id = _id.getText().trim();
        String parent = _parent.getText().trim();

        CtAnnotatedTracker.apply( id, parent );
    }
}



