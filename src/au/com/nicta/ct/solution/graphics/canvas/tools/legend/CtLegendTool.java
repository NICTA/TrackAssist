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

package au.com.nicta.ct.solution.graphics.canvas.tools.legend;

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Singleton.
 *
 * Listener to canvas layer addition/removal; automatically adds LegendLayers to
 * all tracking canvas layers (as a sublayer). Maintains list of these legend
 * layers so they can be adjusted.
 * 
 * @author davidjr
 */
public class CtLegendTool extends CtTool {// implements CtAbstractFactory< CtCanvasLayer > {
    
//    public static final String APPLY_COMMAND = "APPLY";
    public HashMap< CtCanvasLayer, CtLegendCanvasLayer > _legendLayers = new HashMap< CtCanvasLayer, CtLegendCanvasLayer >();

    public static final String name = "legend-tool";
    static final double ZOOM_STEP = Math.pow(2.0, 1.0/5.0);

//    CtLegendLayer legend;

    JComponent c;
    JCheckBox enabledCBox;
    JSpinner sizeSpinner;

//    public static CtLegendTool get( CtToolsModel tm ) {
//        CtLegendTool lt = (CtLegendTool)CtObjectDirectory.get( CtLegendTool.name );
//
//        if( lt == null ) {
//            lt = new CtLegendTool( tm );// zc );
//        }
////        else {
////            lc.setZoomCanvas( zc );
////        }
//
//        return lt;
//    }

    public CtLegendTool( CtToolsModel tm ) {
        super( tm, name );

//        CtObjectDirectory.put( name, this );

//        legend = new CtLegendLayer(CtTrackingController.get().getTrackingView().getCanvasLayer().getParent());
//        legend = new CtLegendLayer();
//        CtTrackingController.get().getTrackingView().getCanvasLayer().addLayer( legend );

//        CtTrackingController sc = CtTrackingController.get();
//        sc.addModelChangeListener( new CtChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                updateEnabled();
//            }
//        });
//        sc.addAppearanceChangeListener( new CtChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                updateEnabled();
//            }
//        });

        createPanel();
        updateEnabled();
    }

//    @Override public CtCanvasLayer create() {
//        return new CtLegendCanvasLayer();
//    }

    @Override public void onCreateCanvasLayer( CtCanvasLayer cl ) {
        if( cl instanceof CtTrackingCanvasLayer ) {
            CtLegendCanvasLayer ll = new CtLegendCanvasLayer();
            _legendLayers.put( cl, ll );
            cl.addLayer( ll ); // add as sub-layer of any tracking layer
            ll.setEnabled( enabledCBox.isSelected() );
            ll.setZoomFactor( Math.pow( ZOOM_STEP, (Integer) sizeSpinner.getValue() ) );
        }
//        if( cl instanceof CtLegendLayer ) {
//            _legendLayers.add( (CtLegendLayer)cl );
//        }
    } // tells the tool about canvases it may need to respond to, as these are all graphical tools
    
    @Override public void onDeleteCanvasLayer( CtCanvasLayer cl ) {
        if( cl instanceof CtTrackingCanvasLayer ) {
            _legendLayers.remove( cl ); // remove any with this key
        }
//        if( cl instanceof CtLegendLayer ) {
//            _legendLayers.remove( (CtLegendLayer)cl );
//        }
    }

//    public CtCanvasLayer getLayer() {
//        return legend.l;
//    }

    protected String iconFile() {
        return "track_legend.png";
    }

    protected String toolTip() {
        return "Show legend";
    }

//    @Override
//    public void updateEnabled() {
//
//        CtTrackingController sc = CtTrackingController.get();
//
//        if( sc == null ) {
//            setEnabled( false );
//            return;
//        }
//
//        setEnabled( true );
//    }

    private void createPanel() {

        enabledCBox = new JCheckBox();
        enabledCBox.setSelected(false);
        enabledCBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                boolean isSelected = enabledCBox.isSelected();
                Set< Entry< CtCanvasLayer, CtLegendCanvasLayer > > es = _legendLayers.entrySet();
                for( Entry< CtCanvasLayer, CtLegendCanvasLayer > cl_ll : es ) {
                    cl_ll.getValue().setEnabled( isSelected );
                }
            }
        });

        sizeSpinner = new JSpinner( new SpinnerNumberModel(5, 1, 10, 1) );
        sizeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Set< Entry< CtCanvasLayer, CtLegendCanvasLayer > > es = _legendLayers.entrySet();
                for( Entry< CtCanvasLayer, CtLegendCanvasLayer > cl_ll : es ) {
                    cl_ll.getValue().setZoomFactor( Math.pow( ZOOM_STEP, (Integer) sizeSpinner.getValue() ) );
                }
                enabledCBox.setSelected(true);
            }
        });

        Set< Entry< CtCanvasLayer, CtLegendCanvasLayer > > es = _legendLayers.entrySet();
        for( Entry< CtCanvasLayer, CtLegendCanvasLayer > cl_ll : es ) {
            cl_ll.getValue().setEnabled(false);
            cl_ll.getValue().setZoomFactor(Math.pow( ZOOM_STEP, 5 ));
        }

//        JPanel pg = new JPanel();
//        GridBagLayout layout = new GridBagLayout();
//        pg.setLayout( layout );
//        pg.setOpaque( false );
//        layout.setAutoCreateGaps( true );
//        layout.setAutoCreateContainerGaps( true );

        JLabel titleLabel = new JLabel( "Legend appearance" );
        JLabel enabledLabel = new JLabel( "Visible" );
        JLabel sizeLabel = new JLabel( "Size" );
        JLabel descriptionLabel = new JLabel( "<html><p>Hover mouse over legend<br> for item descriptions.</p></html>" );

        JPanel p1 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JPanel p2 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel p3 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        JPanel p4 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );

        p1.setOpaque( false );
        p2.setOpaque( false );
        p3.setOpaque( false );
        p4.setOpaque( false );

        p1.add( titleLabel );
        p2.add( enabledLabel );
        p2.add( enabledCBox );
        p3.add( sizeLabel );
        p3.add( sizeSpinner );
        p4.add( descriptionLabel );
//        GridBagConstraints bgc = new GridBagConstraints();
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 0;       //aligned with button 2
//        bgc.gridy = 0;       //third row
//        bgc.gridwidth = 2;   //2 columns wide
//        bgc.insets = new Insets(10,0,10,0);
//        pg.add(titleLable, bgc);
//
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 0;       //aligned with button 2
//        bgc.gridy = 1;       //third row
//        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(0,0,10,20);
//        pg.add(enabledLabel, bgc);
//
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 1;       //aligned with button 2
//        bgc.gridy = 1;       //third row
//        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(0,0,10,0);
//        pg.add(enabledCBox, bgc);
//
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 0;       //aligned with button 2
//        bgc.gridy = 2;       //third row
//        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(0,0,10,20);
//        pg.add(sizeLabel, bgc);
//
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 1;       //aligned with button 2
//        bgc.gridy = 2;       //third row
//        bgc.gridwidth = 1;   //2 columns wide
//        bgc.insets = new Insets(0,0,10,0);
//        pg.add(sizeSpinner, bgc);
//
//        bgc.ipady = 0;       //reset to default
//        bgc.anchor = GridBagConstraints.LINE_START; //bottom of space
//        bgc.gridx = 0;       //aligned with button 2
//        bgc.gridy = 3;       //third row
//        bgc.gridwidth = 2;   //2 columns wide
//        bgc.insets = new Insets(0,0,10,0);
//        pg.add(descriotionLable, bgc);


        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.PAGE_AXIS ) );
        p.setOpaque( false );
//        p.add( pg );
        p.add( p1 );
        p.add( p2 );
        p.add( p3 );
        p.add( p4 );
        c = p;
    }

    @Override
    public JComponent panel() {
        return c;
    }

//    @Override
//    public void actionPerformed( ActionEvent ae ) {
//
//        String id = _id.getText().trim();
//        String parent = _parent.getText().trim();
//
//        CtAnnotatedTracker.apply( id, parent );
//    }

}
