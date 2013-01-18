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

import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtAddDetectionTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtCreateDetectionTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDeleteDetectionTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtMoveDetectionTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtSubDetectionTool;
import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsView;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDeSelectAllTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDeleteAllDetectionsTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDetectionPropertiesTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtMergeDetectionsTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.CtThresholdDetectionTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.auto.bgfg.CtForegroundDetectionTool;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtDetectionTools extends JPanel { //implements ActionListener {

//    public CtZoomCanvas _zc;
//    public CtBrushLayer _brushLayer;
//    public CtDetectionsController _dc;
//    public CtCanvasLayer _detectionLayer; // detec
    public CtToolsModel _tm;
    public CtToolsView _tv;

//    JButton   _test;
//    JPanel _cards;

    public CtDetectionTools() {//, CtGrayscaleMapCanvas gmc ) {
        super();

  //      _zc = zc;

//        _dc = new CtDetectionsController( zc, cc, isf );
        
//        try {
//            _brushLayer = new CtBrushLayer( zc );
////            _detectionLayer = new CtCanvasLayer();
//
////            _zc.addLayer( _detectionLayer, "DetectionLayer" );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }

//        _bl._bs.add( "circle", new CtCircleBrush() );
//        _bl._bs.setDefault( "circle" );

//        CtCanvasLayer dl = _dc.getView().getCanvasLayer();

//        CtSelectDetectionMode sdm = new CtSelectDetectionMode( tm, zc, dl );
//        CtEditDetectionMode edm = new CtEditDetectionMode( tm, zc, dl );

        _tm = new CtToolsModel( "detection-tools" ); // todo: put in OD or something..
        new CtCreateDetectionTool( _tm );//, _dc );
        new CtAddDetectionTool( _tm );//, _dc );
        new CtSubDetectionTool( _tm );//, _dc );
        new CtMergeDetectionsTool( _tm );//, _dc );
        new CtDeleteDetectionTool( _tm );//, _dc );
        new CtDeleteAllDetectionsTool( _tm );//, _dc );
//        CtTool t0 = new CtSelectDetectionTool( _tm, zc );//, _dc );
        new CtMoveDetectionTool( _tm );//, _dc );
//        new CtWatershedDetectionTool( _tm );//, gmc );
//        new CtEdgeDetectionTool( _tm );//, gmc );
        CtTool t0 = new CtDetectionPropertiesTool( _tm );//, gmc );
        new CtDeSelectAllTool( _tm );//, _dc );
        new CtForegroundDetectionTool( _tm );
        new CtThresholdDetectionTool( _tm );
        _tm.setDefault( t0.name() );

        int wGrid = 3;
        _tv = new CtToolsView( this, _tm, wGrid );

//        // revalidate to fix the layout when undock any toolbar
//        this.addPropertyChangeListener(new PropertyChangeListener() {
//
//            public void propertyChange( PropertyChangeEvent evt ) {
//                getRootPane().revalidate();
//            }
//        });
    }

//    public Collection< CtCanvasLayer > getCanvasLayers() {
//        ArrayList< CtCanvasLayer > al = new ArrayList< CtCanvasLayer >();
//
//        al.add( CtTrackingController.get().getDetectionsView().getCanvasLayer() );
//        al.add( _brushLayer._cl );
//
//        return al;
//    }

////////////////////////////////////////////////////////////////////////////////
//        setLayout( new BorderLayout() );
//        setBackground( CtConstants.NictaYellow );
//
//        JPanel upper = new JPanel(); // tool buttons
//        BoxLayout bl1 = new BoxLayout( upper, BoxLayout.Y_AXIS );//BoxLayout.PAGE_AXIS );
//        upper.setLayout( bl1 );
//        upper.setOpaque( false );
//
//        JPanel lower = new JPanel( new CardLayout() ); // tool cards
////        BoxLayout bl2 = new BoxLayout( lower, BoxLayout.Y_AXIS );//BoxLayout.PAGE_AXIS );
////        lower.setLayout( bl2 );
//        lower.setOpaque( false );
//
//        _cards = lower;
//
//        Collection< CtTool > tools = _tm.getTools();
//
//        for( CtTool t : tools ) {
//            AbstractButton b = t.button();
//            JPanel p = t.panel();
//            b.setActionCommand( t.name() );
//            b.addActionListener( this );
//            upper.add( b );
//            lower.add( p, b.getActionCommand() );
//        }
//
////        _test .setAlignmentX( Component.CENTER_ALIGNMENT );
////        upper.add( _test );
////        _test.addActionListener( this );
//
//
////        addButton( upper, "detection_create.png", "Create detection" );
////        addButton( upper, "detection_wand.png", "Create detection" );
////        addButton( upper, "detection_select.png", "Create detection" );
////        addButton( upper, "detection_delete.png", "Create detection" );
////        addButton( upper, "detection_add.png", "Create detection" );
////        addButton( upper, "detection_sub.png", "Create detection" );
////        addButton( upper, "detection_move.png", "Create detection" );
//
//        add( upper, BorderLayout.NORTH );
//        add( lower, BorderLayout.CENTER );
//
//    }

//    protected void addButton( JPanel p, String file, String tip ) {
//        ImageIcon ii = new ImageIcon( CtApplication.datafile( file ) );
//        JButton b = new JButton( ii );
//        b.setToolTipText( tip );
//        p.add( b );
//    }

//    @Override public void actionPerformed( ActionEvent ae ) {
//
//        String s = ae.getActionCommand();
//
//        CardLayout cl = (CardLayout)(_cards.getLayout());
//
//        cl.show( _cards, s );
//    }
}
