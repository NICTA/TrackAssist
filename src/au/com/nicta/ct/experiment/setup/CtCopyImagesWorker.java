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

package au.com.nicta.ct.experiment.setup;


import au.com.nicta.ct.experiment.setup.util.CtImageFileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;



/**
 *
 * @author rch
 */

//Thread to copy image files.
//Image copying may take a significant time, especially
//over the network,
//hence a separate thread should help.

public class CtCopyImagesWorker implements Runnable {

    static final int _buffsize = 100000;
    static final byte[] buffer = new byte[ _buffsize ];

    private String destDir; 
    private String sourceDir;
    private int    fileCount;
    private int    totalFile = 0;
    private String fileNames[] = null;

    //setting parameters
    public void setDestDir( String dir ) {
        destDir=dir;
    }
    public void setSourceDir( String dir ) {
        sourceDir=dir;
        setFiles();
        totalFile = this.getTotalFileCount();
    }

    public int getTotalFileCount() {
        return totalFile;
    }

    public void setFiles() {

        if( destDir.equals( "" ) || sourceDir.equals( "" ) ) {
            return;
        }

        CtImageFileFilter iNF = new CtImageFileFilter();

        if( sourceDir.charAt( sourceDir.length() -1 ) != File.separatorChar ) {
            sourceDir += File.separator;
        }

        if( destDir.charAt( destDir.length( ) -1 )!= File.separatorChar ) {
            destDir   += File.separator;
        }

        File file = new File( sourceDir );
        fileNames = file.list( iNF );
        totalFile = ( this.fileNames ).length;
    }

    public int getFileCount() {
        return fileCount;
    }

    public CtCopyImagesWorker() {
        fileCount=0;
    }

    //implementation of run method of Runnable
    public void run() {

        if( fileNames.length > 0 ) {

            String fromFile;
            String toFile;

            try {
                for( int f=0; f < fileNames.length; f++ ) {

                    fromFile  =  sourceDir + fileNames[ f ];
                    toFile    =  destDir + fileNames[ f ];
                    
                    copy(fromFile, toFile);
                    fileCount ++;
                }
            }
            catch(IOException ie) {
            }
        }
    }

    //implementation of run method of Runnable
    public int copyAllImages() {

        if( this.fileCount >= this.totalFile ) {
            return this.totalFile;
        }
        try {
            String fromFile  =  sourceDir+fileNames [fileCount];
            String toFile    =  destDir  +fileNames [fileCount];
            copy( fromFile, toFile );
            fileCount ++;
        }
        catch( IOException ie ) {
            return fileCount++;
        }
        return fileCount;
    }

    private void copy( String from, String to ) throws IOException {

        InputStream in   = null;
        OutputStream out = null;
		
        try {
            in  = new FileInputStream ( from );
            out = new FileOutputStream( to   );
			
            while( true ) {
                synchronized( buffer ) {

                    int amountRead  = in.read( buffer );
                    if( amountRead == -1 ) {
                        break;
                    }
                    out.write( buffer, 0, amountRead );
                }
            }
         }
         finally {
             if( in != null ) {
                 in.close();
             }
             if( out != null ) {
                 out.close();
             }
        }
    }
}
