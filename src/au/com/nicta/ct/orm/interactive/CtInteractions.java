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

package au.com.nicta.ct.orm.interactive;

import java.util.HashSet;

/**
 *
 * @author davidjr
 */
public class CtInteractions {

    public enum CtQuantityConstraint {
        NONE,
        ONE,
        TWO,
        ZERO_OR_MORE,
        ONE_OR_MORE,
        TWO_OR_MORE
    }

    public enum CtItemState {
        DISABLED, // e.g. not confirmed, uncertain, weak, tentative
        IGNORE, // e.g. not confirmed, uncertain, weak, tentative
        NORMAL, // ordinary view
        FOCUS, // e.g. on mouseover
        SELECTED, // deliberately selected individually or in group
        ATTENTION // e.g. should draw attention
    }

    public static HashSet< CtItemState > getAllItemStates() {
        if( allItemStates == null ) {
            allItemStates = new HashSet< CtItemState >();
            allItemStates.add( CtItemState.DISABLED );
            allItemStates.add( CtItemState.IGNORE );
            allItemStates.add( CtItemState.NORMAL );
            allItemStates.add( CtItemState.FOCUS );
            allItemStates.add( CtItemState.SELECTED );
            allItemStates.add( CtItemState.ATTENTION );
        }

        return allItemStates;
    }
    private static HashSet< CtItemState > allItemStates;// = new HashSet< CtItemState >();

//    public static boolean satisfied( CtQuantityConstraint qc, int quantity ) {//ArrayList< CtItemState > states ) {
//
////        if( states.isEmpty() ) {
////            return true;
////        }
////
////        Collection< CtDetections > matching = getDetectionsWithStates( states );
////
////        int matches = matching.size();
//
//        boolean satisfied = false;
//
//        switch( qc ) {
//            case NONE: if( quantity == 0 ) satisfied = true; break;
//            case ONE: if( quantity == 1 ) satisfied = true; break;
//            case TWO: if( quantity == 2 ) satisfied = true; break;
//            case ZERO_OR_MORE: satisfied = true; break;
//            case ONE_OR_MORE: if( quantity >= 1 ) satisfied = true; break;
//            case TWO_OR_MORE: if( quantity >= 2 ) satisfied = true; break;
//        }
//
//        return satisfied;
//    }

}
