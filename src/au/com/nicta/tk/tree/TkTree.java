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

package au.com.nicta.tk.tree;

import java.util.ArrayList;

/**
 *
 * @author Alan
 */
public class TkTree extends TkObjectDirectory {
    
    private TkTree parent;

    protected ArrayList<TkTree> children = new ArrayList<TkTree>();

    public String name;

    public TkTree(String name) {
        this.name = name;
    }

    public TkTree() {
        this((String)null);
    }

    public TkTree(String name, TkTree parent) {
        this(name);
        parent.addChild(this);
    }

    public TkTree(TkTree parent) {
        this((String)null, parent);
    }

    public synchronized ArrayList<TkTree> getCopyOfChildren() {
        return new ArrayList<TkTree>( children );
    }

    public synchronized boolean isRoot() {
        return parent == null;
    }

    public synchronized boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Only need to ensure <code>parent</code> is valid, which is guaranteed through the <code>volatile</code> keyword.
     * @param <T>
     * @param key
     * @return
     */
    public synchronized <T> T findUp( TkStringKey<T> key ) {
        T ret = super.find( key );

        if( ret != null ) {
            return ret;
        }

        if( isRoot() ) {
            return null;
        }

        return parent.find( key );
    }

    /**
     * synchronized to ensure consistent state, in this case, the children list
     * and child's parent pointer should always be consistent.
     * Non publicly accessible
     */
    synchronized void addChild( TkTree child ) {
        child.parent = this;
        children.add( child );
    }

    /**
     * returns true when remove is successful
     * Non publicly accessible
     * @param child
     * @return
     */
    synchronized void removeChild( TkTree child ) {
        if( !children.remove( child ) ) {
            throw new RuntimeException("Failed to remove child.");
        }
        child.parent = null;
    }

    public TkTree getParent() {
        return parent;
    }

    /**
     * The only public way to set a parent
     *
     * @param newParent
     */
    public synchronized void setParent( TkTree newParent ) {
        resetParent();
        newParent.addChild(this);
    }

    /**
     * Remove the current node from the tree
     * @param newParent
     */
    public synchronized void resetParent() {
        if( parent != null ) {
            parent.removeChild(this);
        }
        parent = null;
    }

//    public void persist( int nPersistId )
//    {
//        initPersist();
//
//        if(  persistIdSet.add( nPersistId )  )
//        {
//            // nPersistId not already added
//            // persist the whole branch than ends at this node
//            TkTree node = this;
//            while( node.parent != null ) { // can't persist root node
//                node.parent.initPersist();
//
//                if( node.persistCount == 0 ) {
//                    node.parent.persistChildren.add( node );
//                }
//                node.persistCount += 1;
//                node = node.parent;
//            }
//        }
//    }
//
//    public void unpersist( int nPersistId )
//    {
//        initPersist();
//
//        if(  !persistIdSet.remove( nPersistId )  ) {
//            System.out.println( "PersistID not found, unpersist has no effect" );
//            return;
//        }
//
//        TkTree node = this;
//        while( node.parent != null ) { // can't persist root node
//            assert node.persistCount > 0;
//
//            node.persistCount -= 1;
//
//            if( node.persistCount == 0 ) {
//                boolean removed = node.parent.persistChildren.remove( node );
//                assert removed;
//            }
//
//            node = node.parent;
//        }
//    }

}
