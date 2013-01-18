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
import au.com.nicta.ct.graphics.canvas.tools.CtToolsView;
import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtDeSelectAllTool;
import au.com.nicta.ct.solution.graphics.canvas.tools.legend.CtLegendTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtAnnotatedTrackingTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtAssociateDetectionsTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtCreateTracksTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtDeleteTrackTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtDeleteTracksTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtForkTrackTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtSelectTrackTool;
import au.com.nicta.ct.solution.tracking.graphics.canvas.tools.CtSeparateDetectionsTool;
import javax.swing.JPanel;

/**
 * Editing of whole solution ie track manipulation
 * @author davidjr
 */
public class CtTrackingTools extends JPanel { //implements ActionListener {

//    public CtZoomCanvas _zc;
//    public CtBrushLayer _brushLayer;
//    public CtSolutionController _sc;
//    public CtCanvasLayer _detectionLayer; // detec
    public CtToolsModel _tm;
    public CtToolsView _tv;
//    private CtLegendTool _legend;
//    JButton   _test;
//    JPanel _cards;

    public CtTrackingTools() {// CtZoomCanvas zc ) {//, CtExperimentModel em ) {//, CtCoordinatesController cc, CtImageSequenceFactory isf ) {
        super();

//        CtTrackingController sc = CtTrackingController.get();// new CtSolutionController( zc, em );//cc, isf );
//
//        try {
//            _brushLayer = new CtBrushLayer( zc );
//        }
//        catch( CtCanvasException ce ) {
//            System.err.print( ce );
//        }
//
//        CtCanvasLayer dl = sc.getView().getCanvasLayer();

        _tm = new CtToolsModel( "solution-tools" ); // todo: put in OD or something..
        CtTool t0 = new CtSelectTrackTool( _tm );//, zc );//, _sc );
        new CtAssociateDetectionsTool( _tm );//, _sc );
        new CtSeparateDetectionsTool( _tm );//, _sc );
        new CtForkTrackTool( _tm );//, _sc );
        new CtDeleteTrackTool( _tm );//, _sc );
        new CtDeleteTracksTool( _tm );//, _sc );
        new CtCreateTracksTool( _tm );//, _sc );
        new CtAnnotatedTrackingTool( _tm );
        new CtDeSelectAllTool( _tm );
//        _legend = new CtLegendTool( _tm );
//        CtLegendTool.get( _tm ); DAVE 2 ALAN: This doesn't work on 2nd or more experiments, as it returns the OLD one tied to wrong tools model!!
        new CtLegendTool( _tm );

        _tm.setDefault( t0.name() );

        int wGrid = 3;
        _tv = new CtToolsView( this, _tm, wGrid );

//        // revalidate to fix the layout when undock any toolbar
//        this.addPropertyChangeListener( new PropertyChangeListener() {
//
//            public void propertyChange( PropertyChangeEvent evt ) {
//                getRootPane().revalidate();
//            }
//        });
    }

//    public List<CtCanvasLayer> getCanvasLayers() {
//        List<CtCanvasLayer> l = new ArrayList<CtCanvasLayer>();
//        l.add( CtTrackingController.get().getTrackingView().getCanvasLayer() );
////        l.add( _legend.getLayer() );
//        return l;
//    }
}
