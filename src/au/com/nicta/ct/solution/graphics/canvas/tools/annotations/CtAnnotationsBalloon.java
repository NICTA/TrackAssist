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

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtAnnotationsTypes;
import au.com.nicta.ct.db.hibernate.CtProperties;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author alan
 */
public class CtAnnotationsBalloon {

//    public Point tick = new Point();
    public Point origin = new Point();
    public int stemLength = 50;
    Set<CtAnnotations> annotSet;
    List<Balloon> balloons = new ArrayList<Balloon>();

    // property names, set these in the DB
    public static final String RADIUS_PROPERTY = "radius";
    public static final String NORMAL_COLOR_PROPERTY = "normal-color-argb-hex";

    static int cnt = 0;

    class Balloon {
        // defaults
        int radius = 10;
        Color normalColor = CtConstants.NictaYellow;

        private Point centre = new Point();

        Balloon(CtAnnotationsTypes type) {
            findProperties(type);
        }
        
        final void findProperties(CtAnnotationsTypes type) {
            CtManualFlush mf = new CtManualFlush(CtSession.Current());
            doFindProperties(type);
            mf.restore();
        }

        final void doFindProperties(CtAnnotationsTypes type) {
            CtProperties p;

            int typePk = type.getPkAnnotationType();

            // radius
            p = CtKeyValueProperties.find(CtAnnotationsTypes.class.getSimpleName(), RADIUS_PROPERTY, typePk );
            if( p != null ) {
                radius = Integer.parseInt( p.getValue() );
                System.out.println( "radius: " + radius );
            }

            // normal color
            p = CtKeyValueProperties.find(CtAnnotationsTypes.class.getSimpleName(), NORMAL_COLOR_PROPERTY, typePk );
            if( p != null ) {
                int rgba = (int) Long.parseLong( p.getValue(), 16 );
                System.out.println( "Color: " + Integer.toHexString(rgba) );
                normalColor = new Color( rgba, true );
            }

        }

        boolean isInside(Point p) {
            getCentre(centre);
            int dx = (p.x - centre.x);
            int dy = (p.y - centre.y);
            System.out.println("(dx,dy): " + dx + " " + dy + "cnt: " + ++cnt );
            return( dx*dx + dy*dy < radius * radius );
        }

        void getCentre(Point dst) {
            dst.x = origin.x;
            dst.y = origin.y + stemLength;
        }
        
        void paint(Graphics2D g2d) {
            int diameter = radius * 2;
            getCentre(centre);
            int xa = centre.x - radius;
            int ya = centre.y - radius;

            g2d.drawLine( origin.x, origin.y, centre.x, centre.y-radius );

            g2d.setColor( normalColor );
            g2d.fillOval( xa, ya, diameter, diameter);
            g2d.setColor( Color.DARK_GRAY );
            g2d.drawOval( xa, ya, diameter, diameter);
        }
    }

    
    public CtAnnotationsBalloon(Set<CtAnnotations> annotSet) {
        this.annotSet = annotSet;
        for( CtAnnotations a : annotSet ) {
            balloons.add( new Balloon(a.getCtAnnotationsTypes()) );
        }
    }

    public Set<CtAnnotations> getAnnotations() {
        return annotSet;
    }

    public boolean isInside(Point p) {
        for( Balloon b: balloons ) {
            if( b.isInside(p) ) {
                return true;
            }
        }
        return false;
    }

    public void paint(Graphics2D g2d) {
        for( Balloon b : balloons ) {
            b.paint(g2d);
        }
    }

}
