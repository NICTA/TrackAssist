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

package au.com.nicta.ct.experiment.setup.util;

import java.io.File;
import java.io.FilenameFilter;


public class CtImageFileFilter implements FilenameFilter {

    //Extension of image file names
    //add more if needed
    public final static String _jpeg = "jpeg";
    public final static String _jpg  = "jpg";
    public final static String _gif  = "gif";
    public final static String _tiff = "tiff";
    public final static String _tif  = "tif";
    public final static String _png  = "png";

    /*
     * Get the extension of a file.
     */
    private String getExtension( String ext ) {

        int i = ext.lastIndexOf( '.' );

        if( i > 0 &&  i < ext.length() - 1 ) {
            ext = ext.substring(i+1).toLowerCase();
        }
        return ext;
    }

    //Accept all directories in which gif, jpg, tiff, or
    // png files reside. Do not accept which has only
    //directories
    public boolean accept( File dir, String name ) {

        //if the selected directory contains directory
        //that is not enough
        //NOTE : This is called iteratively. So one
        //sub-directory will not spoil the story
        //but if the selected directory contains
        //only sub-directories and no image file
        //that is not a valid directory to select
        if( new File( dir,name ).isDirectory() ) {
            return false;
        }

        //checking with the defined extension
        //add more if needed (also need to add
        //the constant extension lists above.s

        String extension = getExtension( name );

        if( extension != null ) {

            if( extension.equals( _tiff )  ||
                 extension.equals( _tif  )  ||
                 extension.equals( _gif  )  ||
                 extension.equals( _jpeg )  ||
                 extension.equals( _jpg  )  ||
                 extension.equals( _png  ) ) {

                    return true;

            }
            else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Just Images";
    }
}


