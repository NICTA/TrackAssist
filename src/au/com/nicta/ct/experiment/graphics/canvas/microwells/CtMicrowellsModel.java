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

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.graphics.canvas.control.CtControlPoint;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsFactory.CtMicrowellsTypes;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.orm.patterns.CtSerializable;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import ij.ImagePlus;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author davidjr
 */
public class CtMicrowellsModel extends CtChangeModel implements CtSerializable { // CtAbstractFactory< CtMicrowell >, 

    public static final String EVT_TYPE_CHANGED = "microwells-types-changed";
    public static final double ANCHOR_CP_HANDLE_OFFSET_PIXELS = 20;

    protected ArrayList< ArrayList< CtMicrowell > > _wells = new ArrayList< ArrayList< CtMicrowell > >();
    protected CtMicrowell prototype;
    protected CtMicrowellsTypes _mt;// = CtMicrowellsTypes.SQUARE;
    protected CtMicrowellsFactory _mf;// = CtMicrowellsTypes.SQUARE;

    public double minDistanceBetweenWellWalls = 0;
    protected CtControlPoint[] anchorCPs = new CtControlPoint[2];
    protected CtControlPoint separationCP;
    protected ArrayList< CtControlPoint > allCPs = new ArrayList< CtControlPoint >();

//    protected CtControlPointsMouseAdapter controlPoints; // this is the mouse UI for the control points

    // from mwell view:
    protected int anchorWellIndexRow;
    protected int anchorWellIndexCol;
    protected int separationWellIndexRow;
    protected int separationWellIndexCol;
    protected int anchorVertex0, anchorVertex1;
    protected int midPointWall;

    public CtMicrowellsModel(
        CtMicrowellsFactory mf,
        CtMicrowellsTypes mt,
        int rows,
        int cols ) {
        super( null );
        refresh( mf, mt, rows, cols );
    }

    public void setMicrowellType( CtMicrowellsTypes mt ) {
        int rows = rows();
        int cols = cols();
//        int anchorVertex0 = getAnchorVertex( 0 );
//        int anchorVertex1 = getAnchorVertex( 1 );
//        int midPointWall = getMidPointWall();
        
        refresh( _mf, mt, rows, cols );//, anchorVertex0, anchorVertex1, midPointWall );

        fireTypeChanged();
    }

    public void fireTypeChanged() {
        this.fire( EVT_TYPE_CHANGED );
    }
    
    public void refresh(
        CtMicrowellsFactory mf,
        CtMicrowellsTypes mt,
        int rows,
        int cols ) {
//        int anchorVertex0,
//        int anchorVertex1,
//        int midPointWall ) {
        _mf = mf;
        _mt = mt;

        CtMicrowell mw = _mf.createMicrowell( mt );
        setPrototype( mw );

        int anchorVertex0 = mw.getAnchorVertex0();
        int anchorVertex1 = mw.getAnchorVertex1();
        int midPointWall = mw.getMidPointWall();

        setAnchorVertices( anchorVertex0, anchorVertex1 ); // OK
        createWells( rows, cols );
        setAnchorWellIndexCol( cols()/2 );
        setAnchorWellIndexRow( rows()/2 );

        mw.updateWells( this, mw, getAnchorWellIndexRow(), getAnchorWellIndexCol(), getPrototype().radius/3 );

//        views = createViews( mm );
//        model.setAnchorVertices( anchorVertex0, anchorVertex1 );
        setmidPointWall( midPointWall );
        createAnchorPoints();
        createSeparationPoint();

//        ( getAnchorWell() ).setStyleStrokePaint( Color.GREEN );

        updateAnchorCPs();
        updateSeparationCP();

//        prepareControlPoints( this );

        // Align microwell grid to center of image
        try {
            CtCoordinatesController cc = CtCoordinatesController.get();
            CtCoordinatesModel cm = cc.getCoordinatesModel();
            CtImages i = cm.getImage();
            ImagePlus ip = cm.getImagePlus( i );

            int w = ip.getWidth();  // natural size of image
            int h = ip.getHeight(); // natural size of image
            double offsetX = w / 2;
            double offsetY = h / 2;
//            double offsetX = _zc.getNaturalWidth()/2;
//            double offsetY = _zc.getNaturalHeight()/2;
            anchorCPs[0].setLocation( anchorCPs[0].getX() + offsetX, anchorCPs[0].getY() + offsetY );
            anchorCPs[1].setLocation( anchorCPs[1].getX() + offsetX, anchorCPs[1].getY() + offsetY );
            anyCPMoved( true, true );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        fireModelChanged();
    }

    public void createAnchorPoints() {
        anchorCPs[0] = new CtControlPoint( getAnchorVertex0() );
        anchorCPs[0].defaultStyle.radius = 8;
        anchorCPs[0].mouseOverStyle.radius = 8;
        anchorCPs[0].selectedStyle.radius = 9;

        anchorCPs[1] = new CtControlPoint( getAnchorVertex1() );
        anchorCPs[1].defaultStyle.radius = 8;
        anchorCPs[1].mouseOverStyle.radius = 8;
        anchorCPs[1].selectedStyle.radius = 9;

        // add event listeners
        anchorCPs[0].changeSupport.addListener( new CtChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                anchorCPMoved( evt );
            }
        });

        anchorCPs[1].changeSupport.addListener( new CtChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                anchorCPMoved( evt );
            }
        });

        allCPs.clear();
        allCPs.add( anchorCPs[ 0 ] );
        allCPs.add( anchorCPs[ 1 ] );
    }

    public void createSeparationPoint() {
        separationCP = new CtControlPoint( getSeparationWell().getCentre() ); 
        separationCP.defaultStyle.radius = 8;
        separationCP.mouseOverStyle.radius = 8;
        separationCP.selectedStyle.radius = 9;
        separationCP.changeSupport.addListener( new CtChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                separationCPMoved( evt );
            }
        });

        allCPs.add( separationCP );
    }

    public String serialize() {
        String s = new String();

        for( CtControlPoint cp : allCPs ) {
//            cp.p (x,y)
            s += cp.p.x;
            s += ',';
            s += cp.p.y;
            s += ',';
        }

        return s;
    }

    public void deserialize( String s ) {
        String[] numbers = s.split( "," );

        int values = numbers.length;
        int coords = values >> 1;

//        for( CtControlPoint cp : points ) {
        for( int p = 0; p < coords; ++p ) {

            CtControlPoint cp = allCPs.get( p );

            double x = Double.valueOf( numbers[ (p*2)   ] );
            double y = Double.valueOf( numbers[ (p*2)+1 ] );

            cp.setLocation( x, y, false );
        }

        anyCPMoved( true, true );
        fireModelChanged();
    }

    public CtMicrowellsTypes getType() {
        return _mt;
    }

    public CtMicrowell getPrototype() {
        return prototype;
    }

    protected void setPrototype( CtMicrowell p ) {
        prototype = p;
        prototype._p.x = 0;//zc.getNaturalWith()  /2;
        prototype._p.y = 0;//zc.getNaturalHeight()/2;
        prototype.radius = 80;
        prototype.angle = 0;
    }

//    @Override public String serialize() {
//        return controlPoints.serialize();
//    }
//
//    @Override public void deserialize( String s ) {
//        controlPoints.deserialize( s );
//    }

    public int rows() {
        return _wells.size();
    }

    public int cols() {
        if( _wells.size() < 1 ) {
            return 0;
        }
        return _wells.get( 0 ).size();
    }

    public CtMicrowell get( int row, int col ) {
        try {
            return _wells.get( row ).get( col );
        }
        catch( Exception e ) {
            return null;
        }
    }

    public CtMicrowell find( String name ) {
        for( ArrayList< CtMicrowell > l : _wells ) {
            for( CtMicrowell m : l ) {
                if( m._name.equalsIgnoreCase( name ) ) {
                    return m;
                }
            }
        }
        return null;
    }

    public int getMidPointWall() {
        return midPointWall;
    }

    public int getAnchorWellIndexRow() {
        return anchorWellIndexRow;
    }

    public int getAnchorWellIndexCol() {
        return anchorWellIndexCol;
    }

    public int getSeparationWellIndexRow() {
        return separationWellIndexRow;
    }

    public int getSeparationWellIndexCol() {
        return separationWellIndexCol;
    }

    public void setAnchorWellIndexRow( int anchorWellIndexRow ) {
        this.anchorWellIndexRow = anchorWellIndexRow;
        this.separationWellIndexRow = this.anchorWellIndexRow + 1;
    }

    public void setAnchorWellIndexCol( int anchorWellIndexCol ) {
        this.anchorWellIndexCol = anchorWellIndexCol;
        this.separationWellIndexCol = this.anchorWellIndexCol;
    }

    public int getAnchorVertex( int anchor ) {
        if( anchor == 0 ) {
            return this.anchorVertex0;
        }
        else if( anchor == 1 ) {
            return this.anchorVertex1;
        }
        return -1;
    }
    
    public void setAnchorVertices( int anchorVertex0, int anchorVertex1 ) {
        this.anchorVertex0 = anchorVertex0 ;
        this.anchorVertex1 = anchorVertex1 ;
    }

    public void setmidPointWall( int midPointWall ) {
        this.midPointWall = midPointWall;
    }

    public CtMicrowell getSeparationWell() {
        return get( this.separationWellIndexRow, this.separationWellIndexCol );
    }

    public CtMicrowell getAnchorWell() {
        return get( this.anchorWellIndexRow, this.anchorWellIndexCol );
    }
    
    public Point2D.Double getAnchorVertex0() {
        return getAnchorWell().getVertex( anchorVertex0 );
    }

    public Point2D.Double getAnchorVertex1() {
        return getAnchorWell().getVertex( anchorVertex1 );
    }

    public CtControlPoint getAnchorCP0() {
        return anchorCPs[0];
    }

    public CtControlPoint getAnchorCP1() {
        return anchorCPs[1];
    }

    public CtControlPoint getSeparationCP() {
        return separationCP;
    }

// DAVE: Moved to microwell subclasses
//    public abstract void updateWells(
//            CtMicrowell prototype,
//            int prototypeRow,
//            int prototypeCol,
//            double minDistanceBetweenWellWalls ); // propagate central (master) well position to the rest of the grid. Depends on geometry

////////////////////////////////////////////////////////////////////////////////
// DANGEROUS BELOW
////////////////////////////////////////////////////////////////////////////////

    public void createWells( int rows, int cols ) {
        resize( rows, cols );

        for( int r = 0; r < rows; ++r ) {
            for( int c = 0; c < cols; ++c ) {
                CtMicrowell w = (CtMicrowell)get( r, c );
                w.setCentre( r * w.radius * 2, r * w.radius * 2 );
            }
        }

        // from view:
        setAnchorWellIndexCol( cols()/2 );
        setAnchorWellIndexRow( rows()/2 );
        setAnchorVertices( anchorVertex0, anchorVertex1 );
    }

    // Change annotation for microwell, letters represent rows
    public void resize( int rows, int cols ) {
        _wells.clear();

        char letter = 'A';

        for( int r = 0; r < rows; ++ r ) {

            _wells.add( new ArrayList< CtMicrowell >() );

            for( int c = 0; c < cols; ++c ) {
                CtMicrowell m = _mf.createMicrowell( _mt );

                m.setName( letter + String.valueOf( c + 1 ) );
                _wells.get( r ).add( m );
            }

            ++letter;
        }
    }

    protected void anchorCPMoved( PropertyChangeEvent e ) {
        anyCPMoved( true, false );
        updateSeparationCP();
        fireModelChanged();
//        repaint();
    }

    public void separationCPMoved(PropertyChangeEvent e) {
        anyCPMoved( false, true );
        updateAnchorCPs();
        updateSeparationCP(); // this will enforce the limits
        fireModelChanged();
//        repaint();
    }

    protected void updateAnchorCPs() {
        Point2D.Double p;

        double th = getAnchorWell().angle + getAnchorWell().orientation ;
        p = getAnchorVertex0();
        p.x += -ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.cos(th);// / _zc.getZoomScale(); // DAVE: painting will have to deal with scale itself.
        p.y += -ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.sin(th);// / _zc.getZoomScale();
        anchorCPs[0].setLocation(p, false); // do not dispatch move events, otherwise we get infinite loop.

        p = getAnchorVertex1();
        p.x += ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.cos(th);// / _zc.getZoomScale();
        p.y += ANCHOR_CP_HANDLE_OFFSET_PIXELS * Math.sin(th);// / _zc.getZoomScale();
        anchorCPs[1].setLocation(p, false); // do not dispatch move events, otherwise we get infinite loop.
    }

    protected void updateSeparationCP() {
        separationCP.setLocation( getSeparationWell().getCentre(), false ); // do not dispatch move events, otherwise we get infinite loop.
    }

    protected void anyCPMoved( boolean anchorCPMoved, boolean separationCPMoved ) {

        CtMicrowell prototype = getPrototype();

        double distBetweenWellWalls = minDistanceBetweenWellWalls;

        if( anchorCPMoved ) {
            prototype._p.x = (anchorCPs[0].getX() + anchorCPs[1].getX()) / 2;
            prototype._p.y = (anchorCPs[0].getY() + anchorCPs[1].getY()) / 2;
            prototype.radius = anchorCPs[0].p.distance( anchorCPs[1].p ) / 2 + ANCHOR_CP_HANDLE_OFFSET_PIXELS;// / _zc.getZoomScale();
            prototype.angle = Math.atan2(
                    anchorCPs[0].getY() - prototype._p.y,
                    anchorCPs[0].getX() - prototype._p.x ) - getAnchorWell().orientation;

//            mmodel.updateWells(prototype, model.getAnchorWellIndexRow(), model.getAnchorWellIndexCol(), mmodel.minDistanceBetweenWellWalls);
        }

        if( separationCPMoved ) {
            prototype.angle = Math.atan2(
                    separationCP.p.y - prototype._p.y,
                    separationCP.p.x - prototype._p.x ) - Math.PI/2;

            // Update interwell separation
            distBetweenWellWalls = separationCP.p.distance( prototype.getCentre() );
            distBetweenWellWalls = distBetweenWellWalls - prototype.getPerpendicularDistanceToWall()*2;
            distBetweenWellWalls = Math.max( distBetweenWellWalls, 0 ); // clips to 0
        }

        prototype.updateWells( this, prototype, getAnchorWellIndexRow(), getAnchorWellIndexCol(), distBetweenWellWalls );
    }


}
