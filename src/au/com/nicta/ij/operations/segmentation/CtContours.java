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

package au.com.nicta.ij.operations.segmentation;

import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * contour walking algorithm
 * @author davidjr
 */
public class CtContours {

    public static Collection< Polygon > findContours( ShortProcessor labelled, int background ) {

        ArrayList< Polygon > polygons = new ArrayList< Polygon >();

        HashMap< Integer, ArrayList< CtPixel > > contours = findContourPixels( labelled, background );

        Set< Entry< Integer, ArrayList< CtPixel > > > es = contours.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {
            Entry< Integer, ArrayList< CtPixel > > e = (Entry< Integer, ArrayList< CtPixel > >)i.next();

            Collection< CtPixel > contour = e.getValue();

            Polygon p = CtPixel.polygon( contour );

            polygons.add( p );
        }

        return polygons;
    }

    public static CtPixel getAnyBackgroundNeighbour( ImageProcessor labelled, int x, int y, int background ) {

        for( int j = y-1; j < (y+2); ++j ) {
            for( int i = x-1; i < (x+2); ++i ) {

                if(    ( i == x )
                    && ( j == y ) ) {
                    continue;
                }

                int label = labelled.get( i, j );

                if( label == background ) {
                    return new CtPixel( i,j );
                } // if any neighbour is
            }
        }

        return null;
    }

// Moore contour algorithm
        public static HashMap< Integer, ArrayList< CtPixel > > findContourPixels( ShortProcessor labelled, int background ) {

        HashMap< Integer, ArrayList< CtPixel > > contours = new HashMap< Integer, ArrayList< CtPixel > >();

        int w = labelled.getWidth();
        int h = labelled.getHeight();


        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

                int label = labelled.get( i, j );//dbm.getElem( index );
                if( label == background ) continue; // not part of a foreground region/component

                // don't walk shapes more than once:
                if( contours.containsKey( label ) ) {
                    continue;
                }

                // OK walk this one:
                ArrayList< CtPixel > contour = new ArrayList< CtPixel >();
                HashSet< CtPixel > marked = new HashSet< CtPixel >();

                CtPixel start = new CtPixel( i,j );
                CtPixel centre = start;
                contour.add( start );
                int d0 = 8;
                CtPixel backTrack = getNeighbour( start,  d0); // the pixel from which start was entered during the image scan.
                int d = getNextDirection(d0);
                CtPixel neighbour = getNeighbour( start,  d); // the next clockwise pixel from backtrack

                assert( backTrack != null );

                int flag = 1;
                do
                {
                    int label_n = labelled.get( neighbour.x, neighbour.y ); //= neighbour.getLabel();

                    if( label_n == label ) {
                        centre = neighbour;
                        marked.add( centre );
                        contour.add( centre );
                        d = directionOf( centre, backTrack );
                        d = getNextDirection( d );
                        neighbour = getNeighbour( centre, d );

                        // This flag fix the bug when at the beginning start is always equal to center,
                        // so the loop check condition ( centre != start ) always false if the 1st neighbor
                        // of start is background
                        if( flag == 1 ) {
                            flag = 0;
                        }
                    }
                    else { // advance neighbour
                        backTrack = neighbour;
                        d = getNextDirection( d );
                        neighbour = getNeighbour( centre, d );
                    }
                } 
                while( ( flag == 1 ) || ( centre.x != start.x ) || (centre.y != start.y));

                contours.put( label, contour );
            }
        }

        return contours;
    }

    public static int getNextDirection( int d ) {
        ++d;
        if( d > 8 ) d = 1;
        return d;
    }

    public static boolean perimeter( ImageProcessor labelled, int x, int y, int background ) {

        int label0 = labelled.get( x,y );

        for( int j = y-1; j < (y+2); ++j ) {
            for( int i = x-1; i < (x+2); ++i ) {

                if(    ( i == x )
                    && ( j == y ) ) {
                    continue;
                }

                int label = labelled.get( i,j );

//                if( label == background ) return true; // if any neighbour is
                if( label != label0 ) {
                    return true;
                }
            }
        }

        return false;
    }


    public static CtPixel getAnyExternalNeighbour( ImageProcessor labelled, int x, int y, int background ) {

        int label0 = labelled.get( x,y );

        for( int j = y-1; j < (y+2); ++j ) {
            for( int i = x-1; i < (x+2); ++i ) {

                if(    ( i == x )
                    && ( j == y ) ) {
                    continue;
                }

                int label = labelled.get( i, j );

                if( label != label0 ) {
                    return new CtPixel( i,j );
                }
//                if( label == background ) {
//                    return new CtPixel( i,j );
//                } // if any neighbour is
            }
        }

        return null;
    }

    public static int directionOf( CtPixel p, CtPixel n ) {
        // 1 2 3
        // 8 * 4
        // 7 6 5
        if( n.x < p.x ) {
            if( n.y < p.y ) {
                return 1;
            }
            else if( n.y > p.y ) {
                return 7;
            }
            else { // same
                return 8;
            }
        }
        else if( n.x > p.x ) {
            if( n.y < p.y ) {
                return 3;
            }
            else if( n.y > p.y ) {
                return 5;
            }
            else { // same
                return 4;
            }
        }
        else { // x same
            if( n.y < p.y ) {
                return 2;
            }
            else if( n.y > p.y ) {
                return 6;
            }
            else { // same
                return 0;
            }
        }
    }

    public static CtPixel getNeighbour( CtPixel p, int d ) {
        // 1 2 3
        // 8 * 4
        // 7 6 5
        switch( d ) {
            case 1: return new CtPixel( p.x-1, p.y-1 );
            case 2: return new CtPixel( p.x  , p.y-1 );
            case 3: return new CtPixel( p.x+1, p.y-1 );
            case 4: return new CtPixel( p.x+1, p.y );
            case 5: return new CtPixel( p.x+1, p.y+1 );
            case 6: return new CtPixel( p.x  , p.y+1 );
            case 7: return new CtPixel( p.x-1, p.y+1 );
            case 8: return new CtPixel( p.x-1, p.y );
        }

        return null;
    }

    public static class CtPixel {

        int x;
        int y;

        public CtPixel() {
            this.x = 0;
            this.y = 0;
        }

        public CtPixel( int x, int y ) {
            this.x = x;
            this.y = y;
        }

        public static Polygon polygon( Collection< CtPixel > c ) {
            Polygon p = new Polygon();

            for( CtPixel px : c ) {
                p.addPoint( px.x, px.y );
            }

            return p;
        }

       @Override public int hashCode() {
           final int PRIME = 31;
           int result = 1;
           result = PRIME * result + ( x * y );
           return result;
       }

       @Override public boolean equals( Object o ) {
           if( this == o ) return true;
           if( o == null ) return false;
           if( getClass() != o.getClass() ) return false;

           final CtPixel p = (CtPixel)o;
           if(    ( p.x != this.x )
               || ( p.y != this.y ) ) {
               return false;
           }

           return true;
       }
    }

}
