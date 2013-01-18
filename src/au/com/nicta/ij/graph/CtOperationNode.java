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

import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alan
 */
public abstract class CtOperationNode {

    public static String EVT_CHANGED = "CtOperationNodeChanged";

    public CtChangeSupport cs = new CtChangeSupport(this);

    protected abstract void run();

    class InputWrapper {
        CtResultNode input = null;
        int version = 0;

        InputWrapper(CtResultNode input) {
            this.input = input;
        }
    }

    List<InputWrapper>  inputs = new ArrayList<InputWrapper>();
    List< WeakReference<CtResultNode> > outputs = new ArrayList< WeakReference<CtResultNode> >();
    int version = 0;
    boolean parametersChanged = true;
    boolean refreshing = false;

    protected CtOperationNode() {
        // nothing
    }

    protected void addOperand(CtResultNode input) {
        inputs.add(new InputWrapper(input));
        setChanged();
    }

    protected void clearOperands() {
        inputs.clear();
        setChanged();
    }

    protected void setChanged() {
        parametersChanged = true;
    }

    void nextVersion() {
        ++version;
    }


    int getVersion() {
        return version;
    }

    public void refresh() {
        if( refreshing ) {
            return;
        }
        refreshing = true; // breaks infinite recursion

        boolean rerun = false;

        // refresh all parents
        for( InputWrapper p : inputs ) {
            p.input.refresh();
            if( p.version != p.input.getVersion() ) {
                p.version  = p.input.getVersion();
                rerun = true;
            }
        }

        // first time
        if(    version == 0
            || parametersChanged ) {
            rerun = true;
        }

        if( rerun ) {
            // Update these flags first so that run() can overwrite them
            // if it so wishes.
            nextVersion();
            parametersChanged = false;
            run();
            cs.fire( EVT_CHANGED );
            fireChangeOnOutputs();
        }

        refreshing = false;
    }

    void fireChangeOnOutputs() {

        boolean containsDeadOutput = false;
        for( WeakReference<CtResultNode> node : outputs ) {
            if( node.get() == null ) {
                containsDeadOutput = true;
            }
            else {
                node.get().fireChange();
            }
        }
        // This will not happen very often, so not going to hit performance
        if( containsDeadOutput ) {
            removeDeadOutput();
        }
    }

    void removeDeadOutput() {
        ArrayList< WeakReference<CtResultNode> > removeThese = new ArrayList< WeakReference<CtResultNode> >();
        for( WeakReference<CtResultNode> node : outputs ) {
            if( node.get() == null ) {
                removeThese.add( node );
            }
        }
        outputs.removeAll( removeThese );
    }

}
