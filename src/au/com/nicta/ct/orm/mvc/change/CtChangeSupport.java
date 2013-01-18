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
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Alan
 */
public class CtChangeSupport {

    private PropertyChangeSupport pcs;
    private HashSet< CtChangeListener > listeners = new HashSet< CtChangeListener >();
    private HashMap< String, HashSet< CtChangeListener > > propertyListeners = new HashMap< String, HashSet< CtChangeListener > >();

    public CtChangeSupport(Object source) {
        pcs = new PropertyChangeSupport(source);
    }

    // Convenience pass throughs
    public void fire(String name) {
        pcs.firePropertyChange(name, null, null);
    }

    public void fire(String name, Object oldValue, Object newValue ) {
        pcs.firePropertyChange(name, oldValue, newValue);
    }

    public void fire(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    public void addListener(CtChangeListener listener) {
        if( hasListener( listener ) ) {
            return;
        }
        pcs.addPropertyChangeListener(listener);
        System.out.println("pcs.getPropertyChangeListeners().length: " + (pcs.getPropertyChangeListeners().length) );
    }

    public boolean hasListener( CtChangeListener listener ) {
        if( listeners.contains( listener ) ) {
            return true;
        }
        return false;
    }
    
    public boolean hasListener( String property, CtChangeListener listener ) {
        if( listeners.contains( listener ) ) {
            return true;
        }
        HashSet< CtChangeListener > hs = propertyListeners.get( property );

        if( hs == null ) {
            return false;
        }

        if( hs.contains( listener ) ) {
            return true;
        }

        return false;
    }

    public void addListener(String propertyName, CtChangeListener listener) {
        if( hasListener( propertyName, listener ) ) {
            return;
        }
        pcs.addPropertyChangeListener(propertyName, listener);
        System.out.println("pcs.getPropertyChangeListeners().length: " + (pcs.getPropertyChangeListeners().length) );
    }

    public void removeListener(CtChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removeListener( String propertyName, CtChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

}
