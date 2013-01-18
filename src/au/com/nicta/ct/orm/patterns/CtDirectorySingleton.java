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

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;

/**
 * Usage e.g.:
 * (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
 * @author davidjr
 */
public class CtDirectorySingleton {

    public static Object get( Class<?> c, String key ) {
        Object o = CtObjectDirectory.get( key );

        if( o != null ) {
            return o;
        }

        try {
            o = c.newInstance();

            CtObjectDirectory.put( key, o );

            return o;
        }
        catch( InstantiationException ie ) {
            return null;
        }
        catch( IllegalAccessException iae ) {
            return null;
        }
    }
//    private T _t;
//
//    private CtDirectorySingleton< T >( T t ) {
//        _t = t;
//    }
//
//    private T getT() {
//        return _t;
//    }
//
//    public static T get( String key, Class< T > c ) {
//        return null;
//    }
//
//    public static <T> CtDirectorySingleton<T> get( String key, Class< T > c ) {
//
//        Object o = CtObjectDirectory.get( key );
//
//        if( o != null ) {
//            return (T)o;
//        }
//
//        try {
//            T t = c.newInstance();
//
//            CtObjectDirectory.put( key, t );
//
//            return t;
//        }
//        catch( InstantiationException ie ) {
//            return null;
//        }
//        catch( IllegalAccessException iae ) {
//            return null;
//        }
//    }

}
