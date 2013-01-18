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

package au.com.nicta.ct.experiment.coordinates;

import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;

/**
 *
 * @author davidjr
 */
public class CtCoordinatesLoader extends CtEventDispatchThreadProgress {

    CtCoordinatesModel _cm;
    CtExperiments _e;
    String _rangeCoordinateType;

    public CtCoordinatesLoader( CtCoordinatesModel cm, CtExperiments e, String rangeCoordinateType ) {
        super( "Loading all images... ", "coordinates-loader" );

        _cm = cm;
        _e = e;
        _rangeCoordinateType = rangeCoordinateType;
    }

    @Override public int getLength() {
        if( _cm == null ) {
            return 0;
        }

//        int length = _cd.size() + _ct.size() +1;
        return 6;
    }

    @Override public void doStep( int step ) {

        if( _cm == null ) {
            return;
        }

        if( _e == null ) {
            return;
        }
// 1
        if( step == 0 ) {
            _cm._l = new CtLimits();
            _cm._l.updateLimits( _e );
            return;
        }

// 2
        if( step == 1 ) {
            _cm._am = new CtAxesModel();
            _cm._am.create( _e );
            return;
        }
// 3
        if( step == 2 ) {
            _cm.createCoordinates();
            return;
        }

// 4
        if( step == 3 ) {
//            _cm.createRanges();
            _cm.createImages();
            return;
        }

// 5
        if( step == 4 ) {
            _cm.setRange( _rangeCoordinateType );
            return;
        } // ie time

// 6
        if( step == 5 ) {
            _cm.fireModelChanged();
        }
    }
}
