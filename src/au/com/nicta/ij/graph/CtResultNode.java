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

package au.com.nicta.ij.graph;

import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.lang.ref.WeakReference;
import javax.naming.OperationNotSupportedException;

/**
 *
 * @author Alan
 */
public class CtResultNode {

    public static String EVT_CHANGED = "CtResultNodeChanged";

    public CtChangeSupport cs = new CtChangeSupport(this);

    CtOperationNode op;

    protected CtResultNode() {
        this( new CtNop() );
    }

    protected CtResultNode(CtOperationNode op) {
        this.op = op;
        this.op.outputs.add( new WeakReference<CtResultNode>(this) );
    }

    public void addListener(CtChangeListener l) {
        cs.addListener(l);
    }

    public void removeListener(CtChangeListener l) {
        cs.removeListener(l);
    }


    public void refresh() {
        op.refresh();
    }

    public void setChanged() {
        op.setChanged();
    }

    protected int getVersion() {
        return op.getVersion();
    }

    void fireChange() {
        cs.fire( EVT_CHANGED );
    }
}


//class CtResultNode
//{
//    boolean changed = true;
//    List<CtResultNode> parents = new ArrayList<CtResultNode>();
//    List< WeakReference<CtResultNode> > children = new ArrayList< WeakReference<CtResultNode> >();
//    CtOperationNode op = null;
//
//    CtResultNode(CtOperationNode op) {
//        this.op = op;
//    }
//
//    CtResultNode(CtOperationNode op, CtResultNode parent) {
//       this(op);
//       dependsOn(parent);
//    }
//
//    CtResultNode(CtOperationNode op, CtResultNode parent1, CtResultNode parent2) {
//        this(op);
//        dependsOn(parent1);
//        dependsOn(parent2);
//    }
//
//    void dependsOn(CtResultNode parent) {
//        parents.add(parent);
//        parent.children.add(new WeakReference<CtResultNode>(this) );
//    }
//
//    void setChanged() {
//        changed = true;
//
//        boolean containsDeadChildren = false;
//        // remove all dead references
//        for( WeakReference<CtResultNode> w : children ) {
//            CtResultNode r = w.get();
//            if( r == null ) {
//                containsDeadChildren = true;
//                continue;
//            }
//            r.setChanged();
//        }
//
//        // This will not happen very often, so not going to hit performance
//        if( containsDeadChildren ) {
//              removeDeadChildren();
//        }
//    }
//
//    void removeDeadChildren() {
//        ArrayList< WeakReference<CtResultNode> > removeThese = new ArrayList< WeakReference<CtResultNode> >();
//        for( WeakReference<CtResultNode> r : children ) {
//            if( r.get() == null ) {
//                removeThese.add( r );
//            }
//        }
//        children.removeAll( removeThese );
//    }
//
//
//    void setProcessor( CtOperationNode op )
//    {
//        this.op = op;
//    }
//
//    CtOperationNode getOperation()
//    {
//        return op;
//    }
//
//    void refresh() {
//        if(    op == null // nothing to do, probably a root
//            || !changed ) { // no parents changed, we can used exising value
//            return;
//        }
//
//        // refresh all parents
//        for( CtResultNode p : parents ) {
//            p.refresh();
//        }
//
//        op.run(CtResultNode output);
//        changed = false;
//    }
//
//}