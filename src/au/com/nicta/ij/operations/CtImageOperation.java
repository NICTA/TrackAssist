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

package au.com.nicta.ij.operations;

import au.com.nicta.ij.graph.CtResultNode;
import au.com.nicta.ij.graph.CtOperationNode;

/**
 *
 * @author Alan
 */
public abstract class CtImageOperation extends CtOperationNode {

    protected CtImageResult dst = new CtImageResult(this);

    protected CtImageOperation() {
        // nothing
    }

    protected CtImageOperation(CtResultNode input1) {
        addOperand(input1);
    }

    protected CtImageOperation(CtResultNode input1, CtResultNode input2) {
        addOperand(input1);
        addOperand(input2);
    }

    protected CtImageOperation(CtResultNode input1, CtResultNode input2, CtResultNode input3) {
        addOperand(input1);
        addOperand(input2);
        addOperand(input3);
    }

    public CtImageResult getDst() {
        return dst;
    }
}
