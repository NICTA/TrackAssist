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
 *
 * @author davidjr
 */
public class CtAbstractPair< A,B > {

    public A _first;
    public B _second;

    public CtAbstractPair() {
        super();
        _first  = null;
        _second = null;
    }

    public CtAbstractPair( A first, B second ) {
        super();
        this._first  = first;
        this._second = second;
    }

    @Override
    public int hashCode() {
        int hashFirst  = 0;
        int hashSecond = 0;

        if( _first  != null ) hashFirst  = _first .hashCode();
        if( _second != null ) hashSecond = _second.hashCode();

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @Override
    public boolean equals( Object o ) {
        if( !( o instanceof CtAbstractPair ) ) {
            return false;
        }

        CtAbstractPair ap = (CtAbstractPair)o;

        // evaluate _first:
        if( _first == null ) {
            if( ap._first != null ) {
                return false;
            }
        }
        else { // not null
            if( ap._first == null ) {
                return false;
            }
            // both non-null:
            if( !_first.equals( ap._first ) ) {
                return false;
            }
        }

        // evaluate _second:
        if( _second == null ) {
            if( ap._second != null ) {
                return false;
            }
        }
        else { // not null
            if( ap._second == null ) {
                return false;
            }
            // both non-null:
            if( !_second.equals( ap._second ) ) {
                return false;
            }
        }

        return true;
    }
}
