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

package au.com.nicta.tk;

import au.com.nicta.tk.tree.TkTree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The idea here is to separate data management and algorithms that operate
 * on the data.
 *
 * @author Alan
 */
public class TkTrack extends TkTree {

    public enum Status {
        ACTIVE,
        TERMINATED
    }

    private static AtomicInteger trackGUID = new AtomicInteger();

    public static int nextTrackGUID() {
        return trackGUID.addAndGet(1);
    }

    public ArrayList<TkTrackElement> elements = new ArrayList<TkTrackElement>();
    public int guid;
    Status status;
    HashSet<String> flags = new HashSet<String>();


    TkTrack( Status s ) {
        this.status = s;
        this.guid = nextTrackGUID();
    }

    TkTrack() {
        this( Status.ACTIVE );
    }

    public void setState( Status s ) {
        status = s;
    }

    public Status getState() {
        return status;
    }

    public TkDetection getLastNonNullDetection() {
        for( int i = elements.size()-1; i >= 0; --i ) {
            TkDetection d = elements.get(i).det;
            if( d != null ) {
                return d;
            }
        }
        return null;
    }

    public TkTrackElement getLast() {
        return elements.get( elements.size() - 1 );
    }

    public TkTrackElement getFirst() {
        return elements.get(0);
    }

    public synchronized void mergeToParent() {
        TkTrack parent = (TkTrack) getParent();
        if( parent == null ) {
            throw new RuntimeException("Parent is null");
        }
        parent.elements.addAll(elements);
        
        // link the children of current node to the parent of current node
        for( TkTree t : getCopyOfChildren() ) {
            t.setParent(parent);
        }

        resetParent(); // unlink current node from parent
    }

}
















