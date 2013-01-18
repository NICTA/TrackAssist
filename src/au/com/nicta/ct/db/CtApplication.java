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

package au.com.nicta.ct.db;

import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import java.io.File;
import java.io.IOException;

/**
 * Wrapper class to hold constants concerning this application (program) and
 * some utility functions that are used throughout the codebase
 * @author davidjr
 */
public class CtApplication {

    public static final String VERSION = "2.2.2";
    public static final String TITLE = "NICTA TrackAssist";
    public static final String AUTHOR = "NICTA";
    public static final String NAME = "TrackAssist";

    public static final String DELIMITER = "/"; // java makes it platform independent?
    public static final String KEY_APPLICATION_PATH = "application-path"; // java makes it platform independent?
    public static final String KEY_EXPERIMENTS_PATH = "experiments-path"; // java makes it platform independent?
    public static final String KEY_DATA = "data"; // java makes it platform independent?

    public static final String PROPERTY_TYPE_SYSTEM = "system";
    
    public static String createApplicationPath() {
        String uri = experimentsPath();
        File f = new File( uri );

        try {
            if( !f.exists() ) {
                if( !f.mkdir() ) {
                    throw new IOException( "Can't create directory: "+uri );
                }
            }
        }
        catch( Exception e ) {
            System.err.println( "ERROR: Can't create missing experiments folder." );
            return null;
        }

        return uri;
    }

    public static String applicationPath() { return CtKeyValueProperties.getValue( KEY_APPLICATION_PATH ); }
    public static String experimentsPath() { return CtKeyValueProperties.getValue( KEY_EXPERIMENTS_PATH ); }

    public static String experimentPath( CtExperiments e ) {
        return e.getUri();
    }

    public static String solutionPath( CtSolutions s ) {
        String s1 = experimentPath( s.getCtExperiments() );
        String s2 = s1 + File.separator + s.getName();
        return s2;
    }

    public static String datafile( String filename ) {
        return dataPath() + DELIMITER + filename;
    }

    public static String dataPath() {
        String s = applicationPath() + DELIMITER + CtKeyValueProperties.getValue( KEY_DATA );
        return s;
    }
    
}
