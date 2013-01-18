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

package au.com.nicta.ct.solution.lineage;

import au.com.nicta.ct.db.hibernate.CtTracks;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author davidjr
 */
public class CtLineageLoader extends CtEventDispatchThreadProgress {

    CtLineageModel _lm;

    public CtLineageLoader( CtLineageModel lm ) {
        super( "Reconstructing lineages... ", "lineage-loader" );

        _lm = lm;
    }

    @Override public int getLength() {
        return 4;
    }

    private HashMap< CtTracks, Integer > tracksMinIndices;
    private HashMap< CtTracks, HashSet< CtTracks > > tracksRelatives;

    @Override public void doStep( int step ) {

        if( _lm == null ) {
            return;
        }

        if( step == 0 ) {
            tracksMinIndices = _lm.refresh1();                      return;
        }
        if( step == 1 ) {
            tracksRelatives  = _lm.refresh2( tracksMinIndices );    return;
        }
        if( step == 2 ) {
                               _lm.refresh3( tracksRelatives );     return;
        }
        if( step == 3 ) {
                               _lm.refresh4();
        }
    }
}
