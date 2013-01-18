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

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import java.util.HashMap;

/**
 *
 * @author davidjr
 */
public class CtTimeWindowModel extends CtModel {

    public int _max = 10; // for performance, don't allow more than this
    public int _history = 0;
    public int _future = 0;

    public int window() {
        int window = Math.abs( _history ) + 1 + _future;
        return window;
    }

    public boolean isInWindow( int currentIndex, int index ) {
        int future  = _future;
        int history = Math.abs( _history );
        int current = currentIndex;// - and + is navigatin the current axes

//        for( int index = (current-history); index <= (current+future); ++index ) {
        int min = (current-history);
        int max = (current+future);

        if( index < min ) {
            return false;
        }
        if( index > max ) {
            return false;
        }

        return true;
    }

}
