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

import au.com.nicta.ct.db.hibernate.CtEntityPropertiesTypes;
import java.util.Comparator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class CtEntityPropertiesTypesUtil {

    // stateless comparator
    public static final Comparator<CtEntityPropertiesTypes> COMPARE_NAME
            = new Comparator<CtEntityPropertiesTypes>() {
        public int compare( CtEntityPropertiesTypes a, CtEntityPropertiesTypes b) {
            return a.getName().compareTo( b.getName() );
        }
    };

    public static List<CtEntityPropertiesTypes> find(
            Session s,
            Class entityName ) {

        return find( s, entityName );
    }

    public static List<CtEntityPropertiesTypes> find(
            Session s,
            Class entityName,
            String propertyName ) {

        String qs =
                " SELECT ctEPT"
              + " FROM CtEntityPropertiesTypes as ctEPT"
              + " WHERE";

        String prefix = "";
        if( entityName != null ) {
            qs += prefix + " ctEPT.entityName = :entityName";
            prefix = " AND";
        }
        if( propertyName != null ) {
            qs += prefix + " ctEPT.name = :name";
            prefix = " AND";
        }

        Query q = s.createQuery(qs);
        
        if( entityName != null ) {
            q.setString( "entityName", CtEntityPropertiesUtil.getClassName(entityName) );
        }
        if( propertyName != null ) {
            q.setString( "name", propertyName );
        }

        List<CtEntityPropertiesTypes> l = (List<CtEntityPropertiesTypes>) q.list();

        return l;
    }

}
