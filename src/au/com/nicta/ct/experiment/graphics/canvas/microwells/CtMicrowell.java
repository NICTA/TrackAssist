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

import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtRegion;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author davidjr
 */
public abstract class CtMicrowell implements CtRegion {

    String _name;
    public Point2D.Double _p = new Point2D.Double();

    protected double radius;
    public double angle;
    public double orientation;
    public double sides;
    public double PER_SIDE_RADIANS;

    private Point2D.Double point = new Point2D.Double(); // cached instance to avoid repeated allocation


    public CtMicrowell() {
        
    }

    public CtMicrowell( String name, double x, double y ) {
        _name = name;
    }

    public abstract int getAnchorVertex0();
    public abstract int getAnchorVertex1();
    public abstract int getMidPointWall();
    public abstract void updateWells(
        CtMicrowellsModel mm,
        CtMicrowell prototype,
        int prototypeRow,
        int prototypeCol,
        double minDistanceBetweenWellWalls );

    public void setName( String name ) {
        _name = name;
    }
    
    public String getName() {
        return _name;
    }

    public void setCentre( double x, double y ) {
        _p.x = x;
        _p.y = y;
    }

    public Point2D.Double getCentre() {
        return _p;
    }
    
    public Rectangle2D getBoundingBox() {
    
        Path2D.Double path = getPath();

        Rectangle2D r = path.getBounds2D();

        return r;
    }

    public void copy( CtMicrowell w ) {
        _p.x        = w._p.x;
        _p.y        = w._p.y;
        radius      = w.radius;
        angle       = w.angle;
        orientation = w.orientation;
        sides       = w.sides;

        PER_SIDE_RADIANS = w.PER_SIDE_RADIANS;
    }


    public Path2D.Double getPath() {
        Path2D.Double hex = new Path2D.Double();

        point = getVertex(0, point);

        hex.moveTo( point.x,
                    point.y );

        for( int i = 1; i < sides ; ++i) {
            point = getVertex(i, point);
            hex.lineTo( point.x, point.y );
        }

        hex.closePath();

        return hex;
    }

    public Point2D.Double getVertex(int vertexIndex, Point2D.Double p) {

        double th = angle + vertexIndex * PER_SIDE_RADIANS + orientation;
        p.setLocation(
                _p.x + radius * Math.cos( th ),
                _p.y + radius * Math.sin( th )  );
        return p;
    }

    public Point2D.Double getVertex(int vertexIndex) {
        return getVertex(vertexIndex, new Point2D.Double());
    }

    public double getPerpendicularDistanceToWall() {
        return radius * Math.cos(PER_SIDE_RADIANS/2);
    }

    public Point2D.Double getMidPointOnWall(int vertexIndex, Point2D.Double p) {
        double th = angle + vertexIndex * PER_SIDE_RADIANS + PER_SIDE_RADIANS/2 + orientation;
        double d = getPerpendicularDistanceToWall();
        p.setLocation( _p.x + d * Math.cos( th ),
                       _p.y + d * Math.sin( th )  );
        return p;
    }

    public Point2D.Double getMidPointOnWall(int vertexIndex ) {
        return getMidPointOnWall(vertexIndex, new Point2D.Double() );
    }
}
