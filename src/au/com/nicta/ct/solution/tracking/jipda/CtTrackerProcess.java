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

package au.com.nicta.ct.solution.tracking.jipda;

import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.ui.swing.progress.CtEventDispatchThreadProgress;


/**
 *
 * @author davidjr
 */
public class CtTrackerProcess extends CtEventDispatchThreadProgress {// implements Runnable {

    CtTkDBTracker _tracker;

    public CtTrackerProcess( CtTkDBTracker tracker ) {
        super( "Generating tracks...", "tracker-process" );
        _tracker = tracker;
    }

    public int getLength() {
        return _tracker.steps() +1; // last step is a refresh
    }

    public void doStep( int step ) {

        int steps = _tracker.steps();

        if( step < steps ) {
            _tracker.doStep( step );
        }
        else {
            CtTrackingController tc = CtTrackingController.get();
            tc.refresh( true );
        }
    }


//    CtProgressMonitor _pm;

//    public CtProgressMonitor run( CtTkDBTracker tracker, String title, String message ) {
//        int min = 0;
//        int max = tracker.steps();
//
//        _tracker = tracker;
//        _pm = CtProgressUtil.createModalProgressMonitor( CtFrame.find(), max, false, 1000 );
//
//        Thread t = new Thread( this );
//        t.start();
//
//        return _pm;
//    }
//
//    @Override public void run() {
//
//        if( _tracker == null ) {
//            return;
//        }
//
//        if( _pm == null ) {
//            return;
//        }
//
//        CtFrame.showWaitCursor();
//
//        _pm.start( "Processing tracking ..." );
//        int max = _tracker.steps();
//
//        try {
//            for( int index = 0; index < max; ++index ) {
//
//                if( _pm.getStatus().compareTo( "Cancelling ..." ) == 0 ) {
//                    _pm.setCurrent( null, _pm.getTotal());
//                    return;
//                } // cancel operation
//
//                try {
//                    Thread.sleep( 1000 );
//                    _tracker.doStep( index );
//                }
//                catch( Exception e ) {
//                    e.printStackTrace();
//                } // ignore missing images and continue
//
//                if( _pm.getStatus().compareTo( "Cancelling ..." ) != 0 ) { // cancel operation
//                    _pm.setCurrent( "Processing step " + (index+1) + " of " + max + " ("+_tracker.findMessage( index ) + " ... )", index );
//                }
//            }
//        }
//        finally {
//            // to ensure that progress dlg is closed in case of any exception
//            if( _pm.getCurrent() != _pm.getTotal() ) {
//                _pm.setCurrent( null , _pm.getTotal() );
//            }
//        }
//
//        CtTrackingController sc = CtTrackingController.get();
//        sc.refresh( true );
//
//        CtFrame.showDefaultCursor();
//    }
}
