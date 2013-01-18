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

package au.com.nicta.ct.orm.patterns;

import java.util.HashMap;

/**
 * Singleton lookup of core top-level objects for transfer between program components.
 * @author davidjr
 */
public class CtObjectDirectory {

    protected HashMap< String, Object > _hm = new HashMap< String, Object >();

    protected static CtObjectDirectory _od;

    public CtObjectDirectory() {
    }

    public static CtObjectDirectory instance() {
        if( _od == null ) {
            _od = new CtObjectDirectory();
        }
        return _od;
    }

    public static Object get( String key ) {
        CtObjectDirectory od = instance();
        Object o = od._hm.get( key );
        return o;
    }

    public static void put( String key, Object o ) {
        CtObjectDirectory od = instance();
        od._hm.put( key, o );
    }

    public static Object remove( String key ) {
        CtObjectDirectory od = instance();
        Object o = od._hm.remove( key );
        return o;
    }
}
