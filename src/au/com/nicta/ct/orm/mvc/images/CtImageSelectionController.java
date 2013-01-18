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

/**
 *
 * @author davidjr
 */
public class CtImageSelectionController extends CtImageSequenceController {

    public static final String FIRST_SELECTED = "FirstSelected";
    public static final String  LAST_SELECTED = "LastSelected";
    public static final String NEXT_LOOP_SELECTED = "NextLoopSelected";
    public static final String NEXT_SELECTED = "NextSelected";
    public static final String CLEAR_SELECTED = "ClearSelected";
    
    public CtImageSelectionController() {
        this( new CtImageSelectionModel() );
    }

    public CtImageSelectionController( CtImageSelectionModel ism ) {
        super( ism );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        
        String s = ae.getActionCommand();

             if( s.equals( FIRST_SELECTED ) ) firstSelected();
        else if( s.equals( LAST_SELECTED ) ) lastSelected();
        else if( s.equals( NEXT_LOOP_SELECTED ) ) nextLoopSelected();
        else if( s.equals( NEXT_SELECTED ) ) nextSelected();
        else if( s.equals( CLEAR_SELECTED ) ) clearSelectedRange();

        super.actionPerformed( ae );
    }

    public void nextLoopSelected() {
        nextSelected( true );
    }
    public void nextSelected() {
        nextSelected( false );
    }

    protected void nextSelected( boolean loop ) {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;

        // if no selected range, play all:
        if( !ism.hasSelection() ) {
            if( !loop ) {
                next();
            }
            else {
                nextLoop();
            }
            return;
        }

        // see if I'm at the end of the selected range:
        int current = ism.getIndex();

        if( !loop ) {
            if( current == ism.selectedIndex2() ) {
                return; // no more available
            }
        }
        
        // jump to selected range
        if( !ism.insideSelectedRange() ) {
            setCurrentIndex( ism.selectedIndex1() ); // jump to selected range
            return;
        }

        // here: am in the selected range, more available, advance one, will stop at end of range or sequence
        if( !loop ) {
            next();
        }
        else {
            nextLoop();
        }

        // jump to selected range
        if( !ism.insideSelectedRange() ) {
            setCurrentIndex( ism.selectedIndex1() ); // jump to selected range
//            return;
        }
    }

    public int getFirstSelected() {

        if( _ism == null ) {
            return CtImageSelectionModel.INVALID_INDEX;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        int index = ism.selectedIndex1();
        return index;
    }

    public void firstSelected() {

        setCurrentIndex( getFirstSelected() );
    }

    public int getLastSelected() {

        if( _ism == null ) {
            return CtImageSelectionModel.INVALID_INDEX;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        int index = ism.selectedIndex2();
        return index;
    }

    public void lastSelected() {
        setCurrentIndex( getLastSelected() );
    }

    public void setSelectedRange( int frameIndex ) {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;

        int s1 = ism.selectedIndex1();
        int s2 = ism.selectedIndex2();

        // move the closest to the new index:
        int d1 = Math.abs( s1 - frameIndex );
        int d2 = Math.abs( s2 - frameIndex );

        if( d1 < d2 ) {
            setSelectedRange( frameIndex, s2 );
        }
        else {
            setSelectedRange( frameIndex, s1 );
        }
    }
    
    public void setSelectedRange( int frameIndex1, int frameIndex2 ) {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        ism.setSelectedRange( frameIndex1, frameIndex2 );
//        fireModelChanged();
    }

    public void clearSelectedRange() {

        if( _ism == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)_ism;
        ism.clearSelection();
//        fireModelChanged();
    }
}
