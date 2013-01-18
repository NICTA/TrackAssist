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

package au.com.nicta.ct.ui.swing.progress;

import au.com.nicta.ct.orm.patterns.CtDirectorySingleton;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.SwingUtilities;

/**
 * Queue of sequential non-overlapping jobs to be run in event dispatch thread.
 * @author davidjr
 */
public class CtEventDispatchThreadQueue implements Runnable {

    public static String name() {
        return "event-dispatch-thread-queue";
    }

    public static CtEventDispatchThreadQueue get() {
        return (CtEventDispatchThreadQueue)CtDirectorySingleton.get( CtEventDispatchThreadQueue.class, CtEventDispatchThreadQueue.name() );
    }

    private ConcurrentLinkedQueue< CtEventDispatchThreadProgress > _queue = new ConcurrentLinkedQueue< CtEventDispatchThreadProgress >();
    private CtEventDispatchThreadProgress _current = null;

    public static void start( CtEventDispatchThreadProgress edtp ) {
        CtEventDispatchThreadQueue edtq = get();
        edtq.enqueue( edtp );
    }

    public void enqueue( CtEventDispatchThreadProgress edtp ) {

        if( _current != null ) {

            // check for duplicates and filter accordingly;
            // since order may be important, only DON'T add IFF the TAIL
            // of the queue is the same object.
            boolean enqueue = true;

            Object[] q = _queue.toArray(); // array is in queue order.
            int tail = q.length -1;
            if( tail >= 0 ) {
                CtEventDispatchThreadProgress edtp2 = (CtEventDispatchThreadProgress)q[ tail ];

                if( edtp.getID().equals( edtp2.getID() ) ) {//edtp == q[ tail ] ) {
                    enqueue = false;
                }
            }

            if( enqueue ) {
                _queue.add( edtp );
            }
        }
        else { // run immediately
            _current = edtp;
            _current.start();
        }

        run();
    }

    @Override public void run() {

        //        only run one item at once
        if( _current == null ) {
            return; // stop everything
        }

        if( _current.isComplete() ) {
            if( !_queue.isEmpty() ) {
                _current = _queue.remove();
                _current.start();
            }
            else {
                _current = null;
            }
        }

        if( _current == null ) { // never null unless queue is empty and current is complete
            return; // stop everything
        }

        SwingUtilities.invokeLater( this ); // ask to be called again
    }
}
