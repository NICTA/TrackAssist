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

package au.com.nicta.tk.tree;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alan
 */
public class TkObjectDirectory {

    protected Map<String, Object> map;

    public TkObjectDirectory( Map<String, Object> map ) {
        this.map = map;
    }

    public TkObjectDirectory() {
        this( CreateDefaultMap() );
    }

    public static HashMap<String, Object> CreateDefaultMap() {
        return new HashMap<String, Object>();
    }

    /**
     * NOTE: don't break this up into a contains() and find() because
     * we need a single atomic operation.
     */
    public synchronized <T> T tryFind( TkStringKey<T> key ) {
        return (T) map.get( key.getName() );
    }

    public synchronized <T> T find( TkStringKey<T> key ) {
        T t = tryFind( key );
        if( t == null ) {
            throw new Error( "Can't find: " + key );
        }
        return t;
    }

    public synchronized <T> void add( TkStringKey<T> key, T t ) {
        Object existing = map.put( key.getName(), t );
        if( existing != null ) {
            throw new TkObjectDirectoryException( "Duplicate key name: " + key );
        }
    }

    public synchronized Object remove( TkStringKey<?> key ) {
        return map.remove( key.getName() );
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized void serializeToScreen( TkSerializerContext s ) {
        // Serialise object directory's contents to screen
        System.out.println( "Serialising OD to screen" );
        // Serialise all objects
        serializeObjects( s );
    }

    public synchronized void serializeToDB( TkSerializerContext s ) {
        // Serialise object directory's contents to DB
        System.out.println( "Serialising OD to DB" );
        // Serialise all objects
        serializeObjects( s );

        throw new UnsupportedOperationException();
    }

    public synchronized void serializeObjects( TkSerializerContext s ) {
        for( Object o : map.values() ) {
             s.ctSerialize( o );
        }
    }

}








