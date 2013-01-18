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

package au.com.nicta.ct.orm.mvc.change;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * Implements the basic functions of a model.
 * Forwards change events
 *
 * @author Alan
 */
public class CtChangeModel implements CtChangeListener {

    public static final String EVT_MODEL_CHANGED = "model-changed";

    public CtChangeSupport changeSupport = new CtChangeSupport(this);

    public CtChangeModel(CtChangeSupport forwardEventsFromSource ) {
        if( forwardEventsFromSource != null ) {
            forwardEventsFromSource.addListener(this);
        }
    }

    public void addModelChangeListener( CtChangeListener cl ) {
        addListener( EVT_MODEL_CHANGED, cl );
    }

//    public boolean hasListener( String event, CtChangeListener cl ) {
//        PropertyChangeListener[] pcls = changeSupport.pcs.getPropertyChangeListeners( event );
//
//        for( PropertyChangeListener pcl : pcls ) {
//            if( pcl == cl ) {
//                return true;
//            }
//        }
//        return false;
//    }

    public void addListener( String event, CtChangeListener cl ) {
        changeSupport.addListener( event, cl );
    }
    public void addListener( CtChangeListener cl ) {
        changeSupport.addListener( cl );
    }

    public void removeListener( String event, CtChangeListener cl ) {
        changeSupport.removeListener( event, cl );
    }

    public void removeListener( CtChangeListener cl ) {
        changeSupport.removeListener( cl );
    }

    public void fire( String event ) {
        changeSupport.fire( event );
    }
    
    public void fireModelChanged() {
        changeSupport.fire( EVT_MODEL_CHANGED );
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        changeSupport.fire(evt);
    }

}
