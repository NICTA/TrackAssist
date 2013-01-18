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

package au.com.nicta.ct.solution.tracking;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.pages.CtSelectSolutionPage;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author Alan
 */
public class CtSolutionManagerGui implements ActionListener {

    public static final String COPY = "copy";
    public static final String RENAME = "rename";
    public static final String DELETE = "delete";

    CtSelectSolutionPage solutionPage;

    public CtSolutionManagerGui( CtSelectSolutionPage solutionPage ) {
        this.solutionPage = solutionPage;
    }

    public void actionPerformed( ActionEvent e ) {
        String action = e.getActionCommand();

        if( action.compareTo( COPY ) == 0 ) {
            copy();
        }
        else if( action.compareTo( RENAME ) == 0 ) {
            rename();
        }
        else if( action.compareTo( DELETE ) == 0 ) {
            delete();
        }
    }

    protected int getSingleRow() throws IllegalArgumentException {
        // can select only at to copy at a time
        int[] rows = solutionPage._tv._tv.getSelectedRows();
        String msg = null;
        if( rows.length == 0 ) {
            msg = "Please select a solution.";
        }
        else
        if( rows.length != 1 ) {
            msg = "Please select only 1 solution at a time.";
        }

        if( msg != null ) {
            JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.ERROR_MESSAGE );
            throw new IllegalArgumentException(msg);
        }

        return rows[0];
    }

    public void delete() {
        try { 
            int row = getSingleRow();

            int solutionPk = solutionPage._tv._tm.pk( row );
            CtSolutions s = (CtSolutions) CtSession.getObject( CtSolutions.class, solutionPk );

            // ask for confirmation
            int reply = JOptionPane.showConfirmDialog( null, "Delete solution: " + s.getName(), "Delete", JOptionPane.OK_CANCEL_OPTION );
            if( reply == JOptionPane.OK_OPTION ) {
                CtSolutionManager.delete( solutionPk );
                // repaint the solutions page
                solutionPage.refreshSolutionsTable();
                solutionPage.repaint();
            }
        }
        catch( IllegalArgumentException e ) {
            // nothing
        }
    }

    public void rename() {
        try {
            int row = getSingleRow();

            // ask for name
            int solutionPk = solutionPage._tv._tm.pk( row );
            CtSolutions s = (CtSolutions) CtSession.getObject( CtSolutions.class, solutionPk );
            String initialName = s.getName();
            String name = JOptionPane.showInputDialog( null, "Rename solution to: ", initialName );

            if( name != null ) {
                CtSolutionManager.rename( s, name );
            }
            
            // repaint the solutions page
            solutionPage.refreshSolutionsTable();
            solutionPage.repaint();
        }
        catch( IllegalArgumentException e ) {
            // nothing
        }
    }

    public void copy() {
        try {
            int row = getSingleRow();

            // ask for name
            int solutionPk = solutionPage._tv._tm.pk( row );
            CtSolutions s = (CtSolutions) CtSession.getObject( CtSolutions.class, solutionPk );
            String initialName = s.getName() + "_copy";
            String name = JOptionPane.showInputDialog( null, "New solution name: ", initialName );

            CtSolutionManager.copy( solutionPk, name );

            // repaint the solutions page
            solutionPage.refreshSolutionsTable();
            solutionPage.repaint();
        }
        catch( IllegalArgumentException e ) {
            // nothing
        }
    }
    

}
