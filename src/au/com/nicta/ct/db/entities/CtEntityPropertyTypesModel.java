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

import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtEntityPropertiesTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class CtEntityPropertyTypesModel extends DefaultComboBoxModel {

    Session s = CtSession.Current();
    List<String> permanentItems = new ArrayList<String>();
    public Class entity;
    CtEntityPropertyTableModel tableModel;
    List<CtEntityPropertiesTypes> visibleTypes = new ArrayList<CtEntityPropertiesTypes>();
//    List<CtEntityPropertiesTypes> allTypes = Collections.EMPTY_LIST;

    public CtEntityPropertyTypesModel() {
        super();
    }

    boolean eventsEnabled = true;

    void disableEvents() {
        eventsEnabled = false;
    }

    void enableEvents() {
        eventsEnabled = true;
    }

    @Override
    protected void fireContentsChanged(Object source, int index0, int index1) {
        if( !eventsEnabled ) {
            return;
        }
        super.fireContentsChanged(source, index0, index1);
    }


    public void filterWith(CtEntityPropertyTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public boolean isDuplicate( String name ) {
        for( int i = 0; i < getSize(); ++i ) {
            if( getElementAt(i).equals(name) ) {
                return true;
            }
        }
        return false;
    }

    public void reload() {

        CtManualFlush mf = new CtManualFlush(s);
        s.beginTransaction();

        List<CtEntityPropertiesTypes> allTypes = CtEntityPropertiesTypesUtil.find(s, entity, null );
        
        visibleTypes.clear();
        for( CtEntityPropertiesTypes ept : allTypes ) {
            if(     tableModel == null
                || !tableModel.isNameDuplicate(ept.getName()) ) {
                    visibleTypes.add( ept );
            }
        }

        s.getTransaction().commit();
        mf.restore();

        // sort
        Collections.sort(visibleTypes, CtEntityPropertiesTypesUtil.COMPARE_NAME );
        
        // add items
        disableEvents();
        removeAllElements(); // these will fire events

        for( CtEntityPropertiesTypes i : visibleTypes ) {
            addElement( i.getName() );
        }
        for( String i : permanentItems ) {
            addElement( i );
        }

        setSelectedItem(null);
        enableEvents();
    }

    public void load( Class entity ) {
        this.entity = entity;
        reload();
    }

    public void addPermanentItem(String option) {
        permanentItems.add(option);
    }

    public void remove(int idx) {
        String name = (String) getElementAt( idx );
        List<CtEntityPropertiesTypes> ept = CtEntityPropertiesTypesUtil.find(s, entity, name);

        s.beginTransaction();
        s.delete( ept.get(0) );
        s.getTransaction().commit();

        reload();
    }
}
