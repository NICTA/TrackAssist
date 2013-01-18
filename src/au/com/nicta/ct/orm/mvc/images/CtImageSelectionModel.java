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

package au.com.nicta.ct.orm.mvc.images;

/**
 *
 * @author davidjr
 */
public class CtImageSelectionModel extends CtImageSequenceModel {

    public static final int NO_SELECTION = -1;

    protected int _selected1 = NO_SELECTION;
    protected int _selected2 = NO_SELECTION;

    public CtImageSelectionModel() {

    }

    public CtImageSelectionModel( CtImageSelectionModel ism ) throws java.io.IOException {
        super( ism );
        _selected1 = ism._selected1;
        _selected2 = ism._selected2;
    }

    public CtImageSelectionModel( CtImageSequenceModel ism )  throws java.io.IOException {
        super( ism );
    }

    public void clearSelection() {
        _selected1 = NO_SELECTION;
        _selected2 = NO_SELECTION;

        fireModelChanged();
    }

    public boolean insideSelectedRange() {
        return insideSelectedRange( _currentIndex );
    }

    public boolean insideSelectedRange( int index ) {
        if( !hasSelection() ) {
            return false;
        }

        if( index >= _selected1 ) {
            if( index <= _selected2 ) {
                return true;
            }
        }

        return false;
    }

    public void setSelectedRange( int selected1, int selected2 ) {

        int min = Math.min( selected1, selected2 );
        int max = Math.max( selected1, selected2 );

        _selected1 = min;
        _selected2 = max;

        fireModelChanged();
    }

    public boolean hasSelection() {
        if(    ( _selected1 == NO_SELECTION )
            || ( _selected2 == NO_SELECTION ) ) {
            return false;
        }

        return true;
    }

    public int selectedIndex1() {
        if( !hasSelection() ) {
            return _currentIndex;
        }
        return _selected1;
    }

    public int selectedIndex2() {
        if( !hasSelection() ) {
            return _currentIndex;
        }
        return _selected2;
    }


}
