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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author davidjr
 */
public class CtImageSequenceController implements ActionListener {

    CtImageSequenceModel _ism;

    public static final String FIRST = "First";
    public static final String  LAST = "Last";

    public static final String PREVIOUS = "Previous";

    public static final String NEXT = "Next";
    public static final String NEXT_LOOP = "NextLoop";

    public CtImageSequenceController() {// CtImageSequenceModel ism ) {
        this( new CtImageSequenceModel() );
    }

    public CtImageSequenceController( CtImageSequenceModel ism ) {// CtImageSequenceModel ism ) {
        _ism = ism;
//        super( ism );
///        super( null, null );
    }

//    @Override public CtImageSequenceModel getModel() {
//        return (CtImageSequenceModel)_m;
//    }

    public CtImageSequenceModel getModel() {
        return _ism;
    }

    public void setRange( String coordinateTypes ) {
        _ism.setRange( coordinateTypes );
    }
//    public void setModel( CtImageSequenceModel ism ) {
//        this._ism = ism;
//    }

    @Override public void actionPerformed( ActionEvent ae ) {

        String s = ae.getActionCommand();

             if( s.equals( FIRST ) ) first();
        else if( s.equals( LAST ) ) last();
        else if( s.equals( PREVIOUS ) ) previous();
        else if( s.equals( NEXT ) ) next();
        else if( s.equals( NEXT_LOOP ) ) nextLoop();

//        super.actionPerformed( ae );
    }

    public void nextLoop() {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        int size = ism.size();
//        int limit1 = is.selectedIndex1();
//        int limit2 = is.selectedIndex1();
//        int range = limit2-limit1;

        int index = ism.getIndex();
        int minIndex = ism.getMinIndex();
        int index0 = index - minIndex;
//        int current = is.getIndex();

        index0 += 1; // next
        index0 %= size; // wrap
        index0 += minIndex; // re-origin

        setCurrentIndex( index0 );
    }

    public void previous() {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        int current = ism.getIndex();
        setCurrentIndex( current -1 );
    }

    public void next() {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        int current = ism.getIndex();
        setCurrentIndex( current +1 );
    }

    public void first() {
        setCurrentIndex( _ism.getMinIndex() );
    }

    public void last() {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
//        int size = ism.size();
        setCurrentIndex( ism.getMaxIndex() );//size -1 );
    }

    public void setCurrentIndex( int frameIndex ) {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;

        ism.setCurrentIndex( frameIndex );
//        int selected = is.currentIndex();
//
//        if( selected == frameIndex ) {
//            return;
//        }
//
//        if( frameIndex >= is.size() ) {
//            return;
//        }
//
//        if( frameIndex < 0 ) {
//            return;
//        }
//
//        is.current( frameIndex );
//
//        fireModelChanged();
    }
}
