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
import au.com.nicta.ct.db.hibernate.CtEntityPropertiesTypes;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.solution.CtSolutionController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class CtEntityPropertyTypesManager {

    Session s = CtSession.Current();
    CtEntityPropertyTypesModel typesModel = new CtEntityPropertyTypesModel();
    JList typesList = new JList(typesModel);

    Class entity;

    JDialog dialog = new JDialog(CtPageFrame.find());
    JPanel panel = new JPanel();

    public CtEntityPropertyTypesManager() {
        dialog.setTitle("Property Manager");
        dialog.setModal(true);
        dialog.setContentPane(panel);
        dialog.setAlwaysOnTop(true);
//        dialog.setLocationRelativeTo(null);

        panel.setMinimumSize(new Dimension(300, 300));
        panel.setPreferredSize(new Dimension(300, 300));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // add lable at top
        {
            JPanel p = new JPanel(new BorderLayout());
            p.add(new JLabel("Properties:"), BorderLayout.WEST);
            panel.add(p);
        }

        // add property list
        JScrollPane scrollPane = new JScrollPane(typesList);
        scrollPane.setPreferredSize(new Dimension(100, 300));
        panel.add(scrollPane);

        JPanel bottomRow = new JPanel(new BorderLayout());

        // add new, delete etc. to the left
        JButton delete = new JButton("Delete");
        JButton create = new JButton("New");
        {
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttons.add(delete);
            buttons.add(create);
            bottomRow.add(buttons, BorderLayout.WEST);
        }

        // add OK to the right
        JButton ok = new JButton("OK");
        {
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.add(ok);
            bottomRow.add(buttons, BorderLayout.EAST);
        }

        // add bottom row of buttons
        panel.add(bottomRow);

        create.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                create();
            }
        });

        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });

        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(null);
    }

    public void delete() {

        List<CtEntityProperties> l = new ArrayList<CtEntityProperties>();

        Object[] selected = typesList.getSelectedValues();
        for( Object sel : selected ) {
            l.addAll( CtEntityPropertiesUtil.find(s, CtSolutionController.getSolutions(), entity, null, (String) sel ) );
        }

        s.beginTransaction();
        for( CtEntityProperties ep : l ) {
            s.delete(ep);
        }
        s.getTransaction().commit();

        int[] idx = typesList.getSelectedIndices();
        for( int i : idx ) {
            typesModel.remove(i);
        }

        typesModel.reload();
    }

    public void create() {
        CtEntityPropertyTypesCreate create = new CtEntityPropertyTypesCreate( dialog, typesModel );
        CtEntityPropertiesTypes ret = create.show();
        if( ret == null ) {
            return;
        }

        // persist to db
        s.beginTransaction();

        CtEntityPropertiesTypes ept = new CtEntityPropertiesTypes();
        ept.setEntityName( CtEntityPropertiesUtil.getClassName(entity) );
        ept.setName( ret.getName() );
        ept.setType( ret.getType() );

        s.saveOrUpdate(ept);
        s.getTransaction().commit();

        typesModel.load(entity);
    }

    public void ok() {
        dialog.setVisible(false);
    }

    public void show( Component dialogParent, Class entity ) {
        this.entity = entity;
        
        // populate the table
        typesModel.load(entity);
        dialog.setVisible(true);
    }
}
