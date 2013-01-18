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

package au.com.nicta.ct.db.entities;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityProperties;
import au.com.nicta.ct.solution.CtSolutionController;
import com.friedrich.kelvin.strings.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class CtIdPropertyModel implements ComboBoxModel {
    DefaultComboBoxModel m = new DefaultComboBoxModel(); // exposing a subset of this class
    String propertyName;
    Session s = CtSession.Current();
    Class entity = null;
    int nextID = 0;

    static final String NEW_ID = "New ID";

    public CtIdPropertyModel( String propertyName ) {
        this.propertyName = propertyName;
    }

    public void load(Class entity) {
        this.entity = entity;
        reload();
    }

    public void reload() {
        List<CtEntityProperties> properties = CtEntityPropertiesUtil.find( s, CtSolutionController.getSolutions(), entity, null, propertyName );

        // get unique values
        Set<String> uniqueValues = new TreeSet<String>();
        for( CtEntityProperties ep : properties ) {
            String v = ep.getValue();
            if( !v.isEmpty() ) {
                uniqueValues.add( v );
            }
        }

        // sort in natural ordering: i.e. 1, 2, 3, 10, 11, as opposed to lexicographical: 1, 10, 11, 2, 3,
        List<String> sortedValues = new ArrayList<String>(uniqueValues);
        Collections.sort(sortedValues, Strings.getNaturalComparator());

        // add to combox box
        m.removeAllElements();
        nextID = 0;
        for( String v : sortedValues ) {
            m.addElement(v);
        }
    }


    // Forwarding backing model
    public void addListDataListener(ListDataListener l) {
        m.addListDataListener(l);
    }

    public void removeListDataListener(ListDataListener l) {
        m.removeListDataListener(l);
    }

    public void setSelectedItem(Object anItem) {
        m.setSelectedItem(anItem);
    }

    public Object getSelectedItem() {
        System.out.println( "Selected: " + m.getSelectedItem() );
        return m.getSelectedItem();
    }

    public int getSize() {
        return m.getSize();
    }

    public Object getElementAt(int index) {
        return m.getElementAt(index);
    }
}
