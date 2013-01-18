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

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author davidjr
 */
public class CtPages {

    public static boolean hasPage( String key ) {
        try {
            Object o = CtObjectDirectory.get( key );
            CtAbstractFactory< CtPage > f = (CtAbstractFactory< CtPage >)o;
        }
        catch( Exception e ) {
            return false;
        }
        return true;
    }

    public static CtPage get( String key ) {
System.out.println( "get page key="+key);
        Object o = CtObjectDirectory.get( key );
        CtAbstractFactory< CtPage > f = (CtAbstractFactory< CtPage >)o;
        CtPage p = f.create();
        return p;
    }

    public static void put( String key, CtAbstractFactory< CtPage > f ) {
        CtObjectDirectory.put( key, f );
    }

    public static final int PAGE_INSET_PIXELS = 20;

    public static void setBorder( JPanel page ) {
        page.setBorder( new EmptyBorder( PAGE_INSET_PIXELS,PAGE_INSET_PIXELS,PAGE_INSET_PIXELS,PAGE_INSET_PIXELS ) );
    }
}
