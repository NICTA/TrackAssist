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

package au.com.nicta.ct.experiment.graphics.canvas.microwells;

import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.control.CtControlPoint;
import au.com.nicta.ct.graphics.canvas.control.CtControlPointsMouseAdapter;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;

public class CtMicrowellsCanvasLayer extends CtViewpointCanvasLayer {

    public static final String CANVAS_LAYER_NAME = "microwells-canvas-layer";

    public static void addFactoryTo( CtViewpointZoomCanvasPanelFactory zcpf ) {
        zcpf._canvasLayerFactories.add( new CtAbstractFactory< CtCanvasLayer >() {
            @Override public CtCanvasLayer create() {
                return new CtMicrowellsCanvasLayer();
            }
        } );
    }

    // my copy of the graphical CP objects
//    public CtControlPoint[] anchorCPs = new CtControlPoint[2];
//    public CtControlPoint separationCP;
    public CtControlPointsMouseAdapter controlPoints; // this is the mouse UI for the control points
    public CtMicrowellPainter[][] microwellPainters;

//    int midPointWall;

    public CtMicrowellsCanvasLayer() {
        super( CANVAS_LAYER_NAME );

        CtMicrowellsModel mm = getModel();

        createPainters( mm );

        mm.addListener( this ); // bug - only listening to the ORIGINAL model not NEW models..! Fix:
        // listen to the microwells model for change events.
        // Note the default viewpoint handler for the
        // events is fine, cos it calls repaint()
    }

    @Override public void stopListening() {
        super.stopListening();

        CtMicrowellsModel mm = getModel();
        mm.removeListener( this );
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {

        // detect change in microwell size or type, either of which means I need to change the number or type of painters:
        CtMicrowellsModel mm = getModel();
        CtMicrowellPainter[][] v = new CtMicrowellPainter[mm.rows()][mm.cols()];

        String s = evt.getPropertyName();

        if( s.equals( CtMicrowellsModel.EVT_TYPE_CHANGED ) ) {
            createPainters( mm );
            setControlPoints();
        }

        super.propertyChange( evt ); // repaint
    }

    public CtMicrowellsModel getModel() {
        CtMicrowellsController mc = CtMicrowellsController.get();
        CtMicrowellsModel mm = mc.getMicrowellsModel();
        return mm;
    }

    public MouseMotionListener getMouseMotionListener() {
//        return getModel().controlPoints;
        return controlPoints;
    }

    public MouseListener getMouseListener() {
//        return getModel().controlPoints;
        return controlPoints;
    }

//    public CtMicrowellPainter getAnchorWell() {
//        CtMicrowellsModel mm = getModel();
//        return views[mm.getAnchorWellIndexRow()][mm.getAnchorWellIndexCol()];
//    }
//
//    public CtMicrowellPainter getSeparationWell() {
//        CtMicrowellsModel mm = getModel();
//        return views[mm.getSeparationWellIndexRow()][mm.getSeparationWellIndexCol()];
//    }

    public CtControlPointsMouseAdapter getCPs() {
//        CtMicrowellsModel mm = getModel();
//        return mm.controlPoints;
        return controlPoints;
    }

////////////////////////////////////////////////////////////////////////////////
// DANGEROUS BELOW
////////////////////////////////////////////////////////////////////////////////


//    public void setup(
//        int rows,
//        int cols,
//        int anchorVertex0,
//        int anchorVertex1,
//        int midPointWall ) {
//
//
//        CtMicrowellsModel mm = getModel();
////
////        mm.setAnchorVertices( anchorVertex0, anchorVertex1 );
////        mm.createWells( rows, cols );
////        setAnchorWellIndexCol( model.cols()/2 );
////        setAnchorWellIndexRow( model.rows()/2 );
////
////        mm.updateWells( mm.getPrototype(), mm.getAnchorWellIndexRow(), mm.getAnchorWellIndexCol(), mm.getPrototype().radius/3);
//
//        microwellPainters = createPainters( mm );
////        model.setAnchorVertices( anchorVertex0, anchorVertex1 );
////        setmidPointWall( midPointWall );
////        mm.prepareAnchorPoints( this );
////        mm.prepareSeparationPoint( this );
////
////        CtMicrowellPainter mp = getPainter( mm.getAnchorWellIndexRow(), mm.getAnchorWellIndexCol() );
////        mp.setStyleStrokePaint( Color.GREEN );
////
////        updateAnchorCPs();
////        updateSeparationCP();
////
////        mm.prepareControlPoints( this );
//        CtViewpointZoomCanvas vzc = (CtViewpointZoomCanvas)parent;
//        CtControlPoint cpA0 = mm.getAnchorCP0();
//        CtControlPoint cpA1 = mm.getAnchorCP1();
//        CtControlPoint cpS  = mm.getSeparationCP();
//
//        controlPoints = new CtControlPointsMouseAdapter( vzc );
//        controlPoints.points.add( cpA0 );
//        controlPoints.points.add( cpA1 );
//        controlPoints.points.add( cpS  );
//
//        // Align microwell grid to center of image
//        double offsetX = _zc.getNaturalWidth()/2;
//        double offsetY = _zc.getNaturalHeight()/2;
//        mm.anchorCPs[0].setLocation( mm.anchorCPs[0].getX() + offsetX, mm.anchorCPs[0].getY() + offsetY );
//        mm.anchorCPs[1].setLocation( mm.anchorCPs[1].getX() + offsetX, mm.anchorCPs[1].getY() + offsetY );
//        anyCPMoved( true, true );
//    }

    @Override public void setParent( CtCanvas c ) {
        super.setParent( c );

//        CtViewpointZoomCanvas vzc = (CtViewpointZoomCanvas)c;
        setControlPoints();
    }

    protected void setControlPoints() {

        if( controlPoints == null ) {
            controlPoints = new CtControlPointsMouseAdapter( this );
        }

        CtMicrowellsModel mm = getModel();
        CtControlPoint cpA0 = mm.getAnchorCP0();
        CtControlPoint cpA1 = mm.getAnchorCP1();
        CtControlPoint cpS  = mm.getSeparationCP();

        controlPoints.points.clear();
        controlPoints.points.add( cpA0 );
        controlPoints.points.add( cpA1 );
        controlPoints.points.add( cpS  );
    }

    public CtMicrowellPainter getPainter( int row, int col ) {
        return microwellPainters[ row ][ col ];
    }

    public CtMicrowellPainter[][] createPainters( CtMicrowellsModel mm ) {
        CtMicrowellPainter[][] v = new CtMicrowellPainter[mm.rows()][mm.cols()];
        for( int r = 0; r < mm.rows(); ++r ) {
            for( int c = 0; c < mm.cols(); ++c ) {
                CtMicrowell w = (CtMicrowell)mm.get( r, c ); // painter retains a reference to the microwell so in the event of model change, needs to be rebuilt
                v[r][c] = new CtMicrowellPainter( w );////m.models[r][c] );
            }
        }

        microwellPainters = v;
        
        CtMicrowellPainter mp = getPainter( mm.getAnchorWellIndexRow(), mm.getAnchorWellIndexCol() );
        mp.setStyleStrokePaint( Color.GREEN );

        return v;
    }

    public void show( String sCol, String sRow ) {
        CtMicrowellsModel mm = getModel();
        _zc.zoomNaturalWindow( mm.find( sCol+sRow ).getBoundingBox() );
    }

//    public void addChangeListener( CtChangeListener cl ) {
//        for( CtControlPoint cp : controlPoints.points ) {
//            cp.changeSupport.addListener( cl );
//        }
//    }
//
//    public void prepareControlPoints( CtMicrowellsCanvasLayer mv ) {
//        controlPoints = new CtControlPointsMouseAdapter( mv.zc );
//        controlPoints.points.add(anchorCPs[0]);
//        controlPoints.points.add(anchorCPs[1]);
//        controlPoints.points.add(separationCP);
//    }

//    public void prepareAnchorPoints( final CtMicrowellsCanvasLayer mv ) {
//        anchorCPs[0] = new CtControlPoint( mv.zc, mv.getAnchorVertex0() );
//        anchorCPs[0].defaultStyle.radius = 8;
//        anchorCPs[0].mouseOverStyle.radius = 8;
//        anchorCPs[0].selectedStyle.radius = 9;
//
//        anchorCPs[1] = new CtControlPoint( mv.zc, mv.getAnchorVertex1() );
//        anchorCPs[1].defaultStyle.radius = 8;
//        anchorCPs[1].mouseOverStyle.radius = 8;
//        anchorCPs[1].selectedStyle.radius = 9;
//
//        // add event listeners
//        anchorCPs[0].changeSupport.addListener( new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                mv.anchorCPMoved(evt);
//            }
//        });
//
//        anchorCPs[1].changeSupport.addListener( new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt)
//            {
//                mv.anchorCPMoved(evt);
//            }
//        });
//    }
//
//    public void prepareSeparationPoint( final CtMicrowellsCanvasLayer mv ) {
//        separationCP = new CtControlPoint( mv.zc, mv.getSeparationWell().model.getCentre() ); move CPs to each canvas layer
//        separationCP.defaultStyle.radius = 8;
//        separationCP.mouseOverStyle.radius = 8;
//        separationCP.selectedStyle.radius = 9;
//        separationCP.changeSupport.addListener( new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt)
//            {
//                mv.separationCPMoved(evt);
//            }
//        });
//    }
////////////////////////////////////////////////////////////////////////////////

//    public Point2D.Double getAnchorVertex0() {
//        CtMicrowellsModel mm = getModel();
//        return (getAnchorWell().model).getVertex( mm.anchorVertex0 );
//    }
//
//    public Point2D.Double getAnchorVertex1() {
//        CtMicrowellsModel mm = getModel();
//        return (getAnchorWell().model).getVertex( mm.anchorVertex1 );
//    }
//
//    public CtControlPoint getAnchorCP0() {
//        CtMicrowellsModel mm = getModel();
//        return mm.anchorCPs[0];
//    }
//
//    public CtControlPoint getAnchorCP1() {
//        CtMicrowellsModel mm = getModel();
//        return mm.anchorCPs[1];
//    }
//
//    public CtControlPoint getSeparationCP() {
//        CtMicrowellsModel mm = getModel();
//        return mm.separationCP;
//    }
//
//    public CtCanvasPainter getPainter() {
//        return new CtCanvasPainter() {
//            public void paint(Graphics2D g, CtCanvasLayer l)
//            {
//                //somehow this method does not work without
//                //this bizzarre way of calling things
//                CtMicrowellsCanvasLayer.this.paint( g );
//            }
//        };
//    }

    @Override public void paint( Graphics2D g ) {

        super.paint( g );

//        updateAnchorCPs(); DAVE commented surely isn't needed??!? why would a repaint change the position?

        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );

        for( CtMicrowellPainter[] row : microwellPainters ) {
            for( CtMicrowellPainter col : row ) {
                col.paint( g, _zc );
            }
        }

        g.setPaint( Color.GREEN );
        //g.setStroke(CtMicrowellPainter.DEFAULT_STROKE);

        // From anchor well control points to well vertex
        CtMicrowellsModel mm = getModel();

        g.draw( new Line2D.Double(
                _zc.toScreenX( mm.getAnchorCP0().getX() ),
                _zc.toScreenY( mm.getAnchorCP0().getY() ),
                _zc.toScreenX( mm.getAnchorVertex0().x ),
                _zc.toScreenY( mm.getAnchorVertex0().y ) ));

        g.draw( new Line2D.Double(
                _zc.toScreenX( mm.getAnchorCP1().getX() ),
                _zc.toScreenY( mm.getAnchorCP1().getY() ),
                _zc.toScreenX( mm.getAnchorVertex1().x ),
                _zc.toScreenY( mm.getAnchorVertex1().y ) ));

        // From inter-well separation control point to wall of the anchor well
        int midPointWall = mm.getMidPointWall();
        
        g.draw( new Line2D.Double(
                _zc.toScreenX( mm.getSeparationCP().getX() ),
                _zc.toScreenY( mm.getSeparationCP().getY() ),
                _zc.toScreenX( mm.getAnchorWell().getMidPointOnWall( midPointWall ).x ),
                _zc.toScreenY( mm.getAnchorWell().getMidPointOnWall( midPointWall ).y ) ));

        controlPoints.paint( g );
    }

//    protected void updateAnchorCPs() {
//        CtMicrowellsModel mm = getModel();
//        Point2D.Double p;
//
//        double th = (getAnchorWell().model).angle + (getAnchorWell().model).orientation ;
//        p = getAnchorVertex0();
//        p.x += -ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.cos(th) / _zc.getZoomScale();
//        p.y += -ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.sin(th) / _zc.getZoomScale();
//        mm.anchorCPs[0].setLocation(p, false); // do not dispatch move events, otherwise we get infinite loop.
//
//        p = getAnchorVertex1();
//        p.x += ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.cos(th) / _zc.getZoomScale();
//        p.y += ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.sin(th) / _zc.getZoomScale();
//        mm.anchorCPs[1].setLocation(p, false); // do not dispatch move events, otherwise we get infinite loop.
//    }
//
//    protected void updateSeparationCP()
//    {
//        CtMicrowellsModel mm = getModel();
//        mm.separationCP.setLocation(getSeparationWell().model.getCentre(), false); // do not dispatch move events, otherwise we get infinite loop.
//    }
//
//    protected void anyCPMoved( boolean anchorCPMoved, boolean separationCPMoved ) {
//
//        CtMicrowellsModel mm = getModel();
//        CtMicrowell prototype = mm.getPrototype();
//
//        double distBetweenWellWalls = mm.minDistanceBetweenWellWalls;
//
//        if( anchorCPMoved ) {
//            prototype._p.x = (mm.anchorCPs[0].getX() + mm.anchorCPs[1].getX()) / 2;
//            prototype._p.y = (mm.anchorCPs[0].getY() + mm.anchorCPs[1].getY()) / 2;
//            prototype.radius = mm.anchorCPs[0].p.distance( mm.anchorCPs[1].p ) / 2 + ANCHOR_CP_HANDLE_OFFSET_PIXELS / _zc.getZoomScale();
//            prototype.angle = Math.atan2(
//                    mm.anchorCPs[0].getY() - prototype._p.y,
//                    mm.anchorCPs[0].getX() - prototype._p.x ) - getAnchorWell().model.orientation;
//
////            mmodel.updateWells(prototype, model.getAnchorWellIndexRow(), model.getAnchorWellIndexCol(), mmodel.minDistanceBetweenWellWalls);
//        }
//
//        if( separationCPMoved ) {
//            prototype.angle = Math.atan2(
//                    mm.separationCP.p.y - prototype._p.y,
//                    mm.separationCP.p.x - prototype._p.x ) - Math.PI/2;
//
//            // Update interwell separation
//            distBetweenWellWalls = mm.separationCP.p.distance(prototype.getCentre());
//            distBetweenWellWalls = distBetweenWellWalls - prototype.getPerpendicularDistanceToWall()*2;
//            distBetweenWellWalls = Math.max(distBetweenWellWalls, 0); // clips to 0
//        }
//
//        mm.updateWells( prototype, mm.getAnchorWellIndexRow(), mm.getAnchorWellIndexCol(), distBetweenWellWalls );
//    }
//
//    protected void anchorCPMoved( PropertyChangeEvent e ) {
//        anyCPMoved( true, false );
//
//        updateSeparationCP();
//        repaint();
//    }
//
//    public void separationCPMoved(PropertyChangeEvent e) {
//        anyCPMoved( false, true );
//
//        updateAnchorCPs();
//        updateSeparationCP(); // this will enforce the limits
//        repaint();
//    }

}