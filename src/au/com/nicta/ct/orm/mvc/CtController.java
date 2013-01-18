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

package au.com.nicta.ct.orm.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

/**
 * Actions from the view are received by the controller.
 * The controller manipulates the (passive) model and generates events that
 * are received by the view, telling it how/what to repaint.
 * @author davidjr
 */
public abstract class CtController implements ActionListener {

    protected HashSet< ActionListener > _modelListeners = new HashSet< ActionListener >();

    protected CtModel _m;
    protected CtView _v;

//    protected ArrayList< CtView > _views = new ArrayList< CtView >();

    public CtController( CtModel m, CtView v ) {
        _m = m;
        _v = v;
    }
//
//    public CtController( CtModel m ) {
//        _m = m;
//    }

    public CtModel getModel() {
        return _m;
    }

    public void addModelListener( ActionListener al ) {
        if( _modelListeners.contains( al ) ) {
            return;
        }
        _modelListeners.add( al );
    }
    
    public void fireModelChanged() {
        createActionEvent( CtModel.ACTION_MODEL_CHANGED );
    }

    protected void createActionEvent( String event ) {
        ActionEvent ae = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, event );
//        _v.actionPerformed( actionEvent );
        notify( ae );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        notify( ae );
    }

    protected void notify( ActionEvent ae ) {
        for( ActionListener al : _modelListeners ) {
            al.actionPerformed( ae );
        }
    }
}
