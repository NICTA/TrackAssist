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

package au.com.nicta.ct.orm.mvc.images;

import au.com.nicta.ct.db.CtApplication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import au.com.nicta.ct.db.hibernate.CtImages;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.io.File;

/**
 * Manages access to filesystem resources associated with database keys
 * @author davidjr
 */

public class CtCachedImages {

    static protected CtCachedImages instance = new CtCachedImages();

    protected CtCachedImages() {
        // add a default set of readers
    }

    public enum BufferingPolicy {
        FIFO,
        OLDEST_USED
    };

    class Element {
        int pk;
        ImagePlus image;
        int idx;

        Element(int pk, ImagePlus image, int idx) {
            this.pk = pk;
            this.image = image;
            this.idx = idx;
        }
    }

    List<CtImageReader> readers = new ArrayList<CtImageReader>();
    int bufferSize = 50;
    BufferingPolicy bufferingPolicy = BufferingPolicy.OLDEST_USED;
    int currIdx = 0;

    protected HashMap< Integer, Element > images = new HashMap< Integer, Element >();
    LinkedList< Element > imageQueue = new LinkedList< Element >();

    public static void SetBufferSize(int size) {
        instance.setBufferSize(size);
    }

    public static int GetBufferSize() {
        return instance.getBufferSize();
    }

    static public ImagePlus Get( CtImages i ) throws IOException {
        if( i == null ) return null;
        return Get( i.getPkImage(), i.getUri() );
    }

    static public ImagePlus Get( int pk, String uri ) throws IOException {
        return instance.get( pk, uri );
    }

    static public ImagePlus Get( String uri ) throws IOException {
        return instance.load( uri, false ); // uncached
    }

    static public List<CtImageReader> GetReaders() {
        return instance.readers;
    }

    public void setBufferSize(int size) {
        assert size > 0 : "Invalid buffer size.";
        bufferSize = size;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public List<CtImageReader> getReaders() {
        return readers;
    }

    public ImagePlus get( int pk, String uri ) throws IOException {

        ImagePlus img = null;

        switch( bufferingPolicy ) {
            case OLDEST_USED:
                img = usePolicyOldestUsed(pk, uri);
            case FIFO:
                img = usePolicyOldestUsed(pk, uri);
                break;
            default:
                throw new IOException( "Unsupported buffering policy" );
        }

        return img;
    }

    public ImagePlus load( String uri, boolean loadPlaceholder ) throws IOException {
        // get extention
        String extension = uri.substring( uri.lastIndexOf(".") + 1 );

        for( CtImageReader r : readers ) {
            if( r.handles(extension) ) {
                ImagePlus img = r.decode( uri );
                if( img != null ) {
                    return img;
                }
                else if( loadPlaceholder ) {
                    //if the image cannot be loaded, a default missing image will be displayed
                    img = r.decode( CtApplication.dataPath()+File.separator+"missing_image_message.png" );
                    return img;
                }
                else {
                    return null;
                }
            }
        }

        // use default image reader
        ImagePlus imp = IJ.openImage( uri );
        if( imp != null ) {
            // DR 5-Jan-12 add mandatory conversion to greyscale 16 bit after discussion with AZ
            int nChannels = imp.getNChannels();
            ImageProcessor ip = imp.getProcessor();

            if(    ( nChannels > 1 )
                || ( ip instanceof ColorProcessor ) ) {
                ImageProcessor ip2 = ip.convertToShort( true ); // arg does linear scaling
                ImagePlus imp2 = imp.createImagePlus(); // create copy with same attrs. but no image data.
                imp2.setProcessor( ip2 );

                return imp2;
            }
            return imp;
        }
        else if( loadPlaceholder ) {
            //if the image cannot be loaded, a default missing image will be displayed
            return IJ.openImage( CtApplication.dataPath()+File.separator+"missing_image_message.png" );
        }

        return null;
        //return IJ.openImage(uri);
    }

    ImagePlus usePolicyOldestUsed(int pk, String uri) throws IOException {

        Element e = images.get( new Integer( pk ) );

        if( e != null ) {
            e.idx = currIdx; // idx indicates when this element was last used
            ++currIdx;
            return e.image;
        }

        return insertNewImage(pk, uri);
    }
    
    ImagePlus usePolicyFIFO(int pk, String uri) throws IOException {

        Element e = images.get( new Integer( pk ) );

        if( e != null ) {
            return e.image;
        }

        return insertNewImage(pk, uri);
    }

    ImagePlus insertNewImage(int pk, String uri) throws IOException {

        while( images.size() >= bufferSize ) {
            removeSmallestIdx();
        }

        ImagePlus img = load( uri, true );

        Element e = new Element( pk, img, currIdx );
        ++currIdx;
        images.put( new Integer(pk), e );

        return img;
    }
    
    void removeSmallestIdx() {
        // Remove the element that was used the longest time ago
        Iterator< Map.Entry<Integer,Element> > it = images.entrySet().iterator();

        // Set first as the oldest
        Map.Entry<Integer,Element> pair = it.next();
        int     smallestIdx = pair.getValue().idx;
        Integer smallestKey = pair.getKey();

        // Go throught all
        while( it.hasNext() ) {
            pair = it.next();
            if( smallestIdx > pair.getValue().idx ) {
                smallestIdx = pair.getValue().idx;
                smallestKey = pair.getKey();
            }
        }

        images.remove( smallestKey );
    }

}
