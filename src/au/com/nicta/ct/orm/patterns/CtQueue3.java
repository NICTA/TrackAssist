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

/**
 * 3 items of same type, implied order. A tuple.
 *
 * @author davidjr
 */
public class CtQueue3< T > {

    public T _a;
    public T _b;
    public T _c;

    public CtQueue3() {
        super();
        _a = null;
        _b = null;
        _c = null;
    }

    public CtQueue3( T a, T b, T c ) {
        super();
        this._a = a;
        this._b = b;
        this._c = c;
    }

    public CtQueue3( CtQueue3< T > t ) {
        super();
        this._a = t._a;
        this._b = t._b;
        this._c = t._c;
    }

    public void rotateAB( boolean wrap ) {
        T temp = null;

        if( wrap ) {
            temp = _a;
        }

        _a = _b;
        _b = _c;
        _c = temp;
    }

    public void rotateBA( boolean wrap ) {
        T temp = null;

        if( wrap ) {
            temp = _c;
        }

        _c = _b;
        _b = _a;
        _a = temp;
    }

    public boolean anyNotNull() {
        if( _a != null ) return true;
        if( _b != null ) return true;
        if( _c != null ) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hashA = 0;
        int hashB = 0;
        int hashC = 0;

        if( _a != null ) hashA = _a.hashCode();
        if( _b != null ) hashB = _b.hashCode();
        if( _c != null ) hashC = _c.hashCode();

        return (hashA + hashB + hashC) * (hashC + hashB + hashA );
    }

    @Override
    public boolean equals( Object o ) {
        if( !( o instanceof CtQueue3 ) ) {
            return false;
        }

        CtQueue3 ap = (CtQueue3)o;

        // evaluate _first:
        if( _a == null ) {
            if( ap._a != null ) {
                return false;
            }
        }
        else { // not null
            if( ap._a == null ) {
                return false;
            }
            // both non-null:
            if( !_a.equals( ap._a ) ) {
                return false;
            }
        }

        // evaluate _second:
        if( _b == null ) {
            if( ap._b != null ) {
                return false;
            }
        }
        else { // not null
            if( ap._b == null ) {
                return false;
            }
            // both non-null:
            if( !_b.equals( ap._b ) ) {
                return false;
            }
        }

        if( _c == null ) {
            if( ap._c != null ) {
                return false;
            }
        }
        else { // not null
            if( ap._c == null ) {
                return false;
            }
            // both non-null:
            if( !_c.equals( ap._c ) ) {
                return false;
            }
        }

        return true;
    }
}
