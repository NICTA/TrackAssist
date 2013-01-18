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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author davidjr
 */
public class CtConnectivity {

    public static class CtLabelledBufferedImage {
        public CtLabelledBufferedImage(){}
        public int nextLabel;
//        public BufferedImage labels;
        public ShortProcessor labels;
    }
    
    /**
     * Does connected component analysis of a byte-greyscale image (type TYPE_BYTE_GRAY)
     * returning a labelled image and the largest label in it. There cannot be
     * more than 65536 distinct labels including 0 (background). If there are,
     * this method will throw an exception.
     *
     * @param mask Image containing foreground/background segmentation
     * @param background Pixel value (byte value ie max 255) indicating background value (any value can be foreground
     * @param b8Way If true, does 8 pixel neighbourhood, else 4 pixel neighbourhood
     * @return
     * @throws IllegalArgumentException
     */
    public static CtLabelledBufferedImage label( ImageProcessor mask, int background, boolean b8Way, int minSize ) throws IllegalArgumentException {
//    public static CtLabelledBufferedImage label( BufferedImage mask, int background, boolean b8Way ) throws IllegalArgumentException {

//        int type = mask.getType();
//
//        if( type != BufferedImage.TYPE_BYTE_GRAY ) {
//            throw new IllegalArgumentException();
//        }

        int w = mask.getWidth();
        int h = mask.getHeight();

        // WARNING: labelled type only allows up to 65K labels!
//        BufferedImage labelled = new BufferedImage( w, h, BufferedImage.TYPE_USHORT_GRAY ); // assume initialized to zero
//
//        DataBufferInt dbl = ((DataBufferInt)labelled.getRaster().getDataBuffer());
//        DataBufferInt dbm = ((DataBufferInt)    mask.getRaster().getDataBuffer());
//        int[] a = db.getData();
//
//        assert( dbm.getNumBanks() == 1 );
        ShortProcessor labelled = new ShortProcessor( w,h );
        labelled.setValue( 0 );

        int nextLabel = 1;

        HashMap< Integer, HashSet< Integer > > e = new HashMap< Integer, HashSet< Integer > >();
        HashMap< Integer, Integer > labelSizes = new HashMap< Integer, Integer >();

//        int offset0 = db.getOffset();
        int jMax = h -1;

        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {

//                int index = j * w + i; // scan line ordering
                int value = mask.get( i, j );//dbm.getElem( index );

                if( value == background ) continue; // not part of a foreground region/component

                /* Neighbour layout
                 | N1 | N2 |    |
                 ------------------
                 | N4 | me |    |
                 ------------------
                 | N3 |    |    |   */

                // 3) Find the 2 (or 4) neighbours of this pixel.
                //_______________________________________________________________________
                int n1 = 0;
                int n2 = 0;
                int n3 = 0;
                int n4 = 0;

                if( j > 0 ) {
                    n2 = labelled.get( i, j-1 );//dbl.getElem( ( (j-1) *w ) +i   );
                }

                if( i > 0 ) {
                    n4 = labelled.get( i-1, j );//dbl.getElem( ( (j  ) *w ) +i-1 );

                    if( b8Way ) {
                        if( j > 0 ) { 
                            n1 = labelled.get( i-1, j-1 );//dbl.getElem( ( (j-1) *w ) +i-1 );
                        } 
                        if( j < jMax ) {
                            n3 = labelled.get( i-1, j+1 );//dbl.getElem( ( (j+1) *w ) +i-1 );
                        }
                    }
                }


                // 4) Find all different non-zero nNeighbours;
                //    these must be recorded as equivalences.
                //_______________________________________________________________________
                int thisLabel = 0;

                if( n4 != 0 ) {
                    thisLabel = n4;
                }

                if( n3 != 0 ) {
                    if( thisLabel == 0 ) {
                        thisLabel = n3;
                    }
                    else if( thisLabel != n3 ) {
                        equivalent( e, thisLabel, n3 );
                    }
                    // else: nThisLabel == nNeighbour3, do nothing
                }

                if( n2 != 0 ) {
                    if(    ( thisLabel != 0 )
                        && ( thisLabel != n2 ) ) {
                        equivalent( e, thisLabel, n2 );
                    }
                    else {
                        thisLabel = n2;
                    }
                }

                if( n1 != 0 ) {
                    if(    ( thisLabel != 0 )
                        && ( thisLabel != n1 ) ) {
                        equivalent( e, thisLabel, n1 );
                    }
                    else {
                        thisLabel = n1;
                    }
                }


                // 5) Finally, see if we found any labels in the neighbours:
                //_______________________________________________________________________
                if( thisLabel == 0 ) { // if( No neighbours )
                    thisLabel = nextLabel;
                    ++nextLabel;
                }

                Integer size = labelSizes.get( thisLabel );

                int newSize = 1;

                if( size != null ) {
                    newSize += size;
                }
                labelSizes.put( thisLabel, newSize );

                labelled.set( i, j, thisLabel );
                //dbl.setElem( index, thisLabel );
            }
        }

        // 5) Reformat the list of equivalences, so that for each label there is a
        //    mapping to the lowest equivalent label.
        //___________________________________________________________________________
        HashMap< Integer, Integer > hm = new HashMap< Integer, Integer >();

        createEquivalenceMappings( e, hm, labelSizes, nextLabel );


        // 4) Now explore the image again, merging labels that are equivalent by
        //    reducing all labels to the lowest equivalent value.
        //
        //    Use the regionSizes vector to accumulate the size of each region.
        //___________________________________________________________________________
        for( int j = 0; j < h; ++j ) {
            for( int i = 0; i < w; ++i ) {
//                int index = j * w + i; // scan line ordering
                int thisLabel = labelled.get( i, j );//dbl.getElem( index );

                if( thisLabel == 0 ) continue; // background

                Integer newLabel = hm.get( thisLabel );

                int size = 0;

                if( newLabel == null ) {
                    size = labelSizes.get( thisLabel );
System.out.println( "label "+thisLabel+" sz="+size );
                    if( size < minSize ) {
                        labelled.set( i, j, 0 );
                    }
                }
                else {
                    size = labelSizes.get( newLabel );

System.out.println( "label "+newLabel+" sz="+size );
                    if( size < minSize ) {
                        labelled.set( i, j, 0 );
                    }
                    else {
                        labelled.set( i, j, newLabel );
                    }
                }
            }
        }


        // 5) return structure
        //___________________________________________________________________________
        CtLabelledBufferedImage lbi = new CtLabelledBufferedImage();
        lbi.nextLabel = nextLabel;
        lbi.labels = labelled;

        return lbi;
    }

    private static void equivalent( HashMap< Integer, HashSet< Integer > > equivalences, int label1, int label2 ) {

        HashSet< Integer > set1 = equivalences.get( label1 );
        HashSet< Integer > set2 = equivalences.get( label2 );

        if( set1 == null ) {
            set1 = new HashSet< Integer >();
            equivalences.put( label1, set1 );
        }
        set1.add( label2 ); // will prevent duplicates

        if( set2 == null ) {
            set2 = new HashSet< Integer >();
            equivalences.put( label2, set2 );
        }
        set2.add( label1 ); // will prevent duplicates
    }

    private static void createEquivalenceMappings( 
        HashMap< Integer, HashSet< Integer > > equivalences,
        HashMap< Integer, Integer > mappings,
        HashMap< Integer, Integer > sizes,
        int nextLabel ) {

//        HashMap< Integer, HashSet< Integer > > merged = new HashMap< Integer, HashSet< Integer > >();
        
        for( int label = 1; label < nextLabel; ++label ) {

            HashSet< Integer > equivalent = equivalences.get( label );

            if( equivalent == null ) {
                continue; // no equivalences
            }

            if( mappings.containsKey( label ) ) {
                continue; // I already merged this set
            }

            // so there are some equivalents, and i didn't merge them yet:
            HashSet< Integer > merged = new HashSet< Integer >();

            recursivelyMerge( equivalences, merged, sizes, label );

            Iterator i = merged.iterator();

            int min = (Integer)i.next();

            while( i.hasNext() ) {
                int n = (Integer)i.next();

                assert( !mappings.containsKey( n ) );

                mappings.put( n, min );
            }
        }
    }

    private static void recursivelyMerge(
        HashMap< Integer, HashSet< Integer > > equivalences,
        HashSet< Integer > merged,
        HashMap< Integer, Integer > sizes,
        int label ) {

        if( merged.contains( label ) ) {
            return;
        }

        merged.add( label );

        HashSet< Integer > equivalent = equivalences.get( label );

        if( equivalent == null ) {
            return;
        }

        for( Integer n : equivalent ) {
//            HashSet< Integer > e = equivalences.get( n );

            Integer size1 = sizes.get( label );
            Integer size2 = sizes.get( n );

            sizes.put( label, size1+size2 );

            recursivelyMerge( equivalences, merged, sizes, n );
        }
    }
}
