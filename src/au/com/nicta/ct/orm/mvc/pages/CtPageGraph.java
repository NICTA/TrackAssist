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

package au.com.nicta.ct.orm.mvc.pages;

import au.com.nicta.ct.db.CtKeyValueProperties;

/**
 *
 * @author davidjr
 */
public class CtPageGraph {

    public static final String DEFAULT_PAGE_PROPERTY_KEY = "default-page";
    public static final String ERROR_PAGE_PROPERTY_KEY = "error-page";
//    public static final String HELP_PAGE_PROPERTY_KEY = "help-page";
//    public static final String ABOUT_PAGE_PROPERTY_KEY = "about-page";

    public CtPageGraph() {
        
    }

    public CtPage transition( String key ) { // go direct to this page
        CtPage p = (CtPage)CtPages.get( key );
        return p;
    }

    public CtPage transition( CtPage p1 ) {

        String key1 = p1.key();
System.out.print( " key1="+key1 );
        String state1 = p1.state();
System.out.print( " state1="+state1 );
        String edge12 = edge( key1, state1 );
System.out.print( " edge="+edge12 );
        String key2 = CtKeyValueProperties.getValue( edge12 );

        if( key2 == null ) {
System.out.println( " key2=null" );
            return null;
        }
System.out.println( " key2="+key2 );

        CtPage p2 = (CtPage)CtPages.get( key2 );

        return p2;
    }

    public String edge( String vertexKey, String vertexState ) {
        String s = vertexKey + "," + vertexState;
        return s;
    }
}
