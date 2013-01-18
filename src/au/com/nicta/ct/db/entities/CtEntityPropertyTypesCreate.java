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
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author alan
 */
public class CtEntityPropertyTypesCreate {

    JDialog dialog = new JDialog(CtPageFrame.find());
    JPanel panel = new JPanel();
    JTextField name = new JTextField(20);
    CtEntityPropertyTypesModel typesModel;
    CtEntityPropertiesTypes ret;

    public CtEntityPropertyTypesCreate(JDialog parent, CtEntityPropertyTypesModel typesModel) {
        this.typesModel = typesModel;

        dialog = new JDialog(parent);
        dialog.setModal(true);
        dialog.setContentPane(panel);
        dialog.setAlwaysOnTop(true);
        dialog.setTitle("New Property");

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionListenerCancel(e);
            }
        });

        JButton ok = new JButton("OK");
        ok.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                actionListenerOK(e);
            }
        });

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Name:"));
            row.add( name );
            panel.add(row);
        }
        {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            row.add(ok);
            row.add(cancel);
            panel.add(row);
        }
        
        dialog.pack();
        dialog.setLocationRelativeTo(null);
    }
    
    protected void actionListenerOK( ActionEvent e ) {
        if( name.getText().isEmpty() ) {
            JOptionPane.showMessageDialog(CtPageFrame.find(), "Invalid name");
            return;
        }

        if( typesModel.isDuplicate( name.getText() ) ) {
            JOptionPane.showMessageDialog(CtPageFrame.find(), "Duplicate name");
            return;
        }
        
        dialog.setVisible(false);

        ret = new CtEntityPropertiesTypes();
        ret.setName(name.getText());
        ret.setType("string");
    }
            
    protected void actionListenerCancel( ActionEvent e ) {
        dialog.setVisible(false);
    }

    public CtEntityPropertiesTypes show() {
        dialog.setVisible(true); // modal, not returning
        return ret;
    }

}
