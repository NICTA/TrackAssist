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

package au.com.nicta.ct.experiment.coordinates.time.windows;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.orm.mvc.CtController;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.orm.mvc.CtView;

/**
 *
 * @author davidjr
 */
public class CtTimeWindowController extends CtController {

    public static String name() {
        return "time-window-controller";
    }

    public CtTimeWindowController() {
        this( new CtTimeWindowModel(), null );
    }
    
    public CtTimeWindowController( CtModel m, CtView v ) {
        super( m, v );

        CtObjectDirectory.put( name(), this );
    }

    public int getHistory() {
        CtTimeWindowModel twm = (CtTimeWindowModel)_m;

        return twm._history;
    }

    public int getFuture() {
        CtTimeWindowModel twm = (CtTimeWindowModel)_m;

        return twm._future;
    }
    
    public void setHistory( int frames ) {

        CtTimeWindowModel twm = (CtTimeWindowModel)_m;

        if( frames > 0 ) {
            return;
        }

        if( Math.abs( frames ) > twm._max ) {
            return;
        }

        twm._history = frames;

        fireModelChanged();
    }

    public void setFuture( int frames ) {

        CtTimeWindowModel twm = (CtTimeWindowModel)_m;

        if( frames < 0 ) {
            return;
        }

        if( Math.abs( frames ) > twm._max ) {
            return;
        }

        twm._future = frames;

        fireModelChanged();
    }
}
