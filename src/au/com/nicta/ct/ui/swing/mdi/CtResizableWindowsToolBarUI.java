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

package au.com.nicta.ct.ui.swing.mdi;

import com.sun.java.swing.plaf.windows.WindowsToolBarUI;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 * TODO impl. Apple and Linux flavours
 * 
 * @author davidjr
 */
public class CtResizableWindowsToolBarUI extends WindowsToolBarUI {

    /**
     * Creates a window which contains the toolbar after it has been
     * dragged out from its container
     *
     * DAVE: Removed the custom root pane stuff as this was preventing user-
     * resize... also removed the setResizable( false ) for same reason.
     * 
     * @return a <code>RootPaneContainer</code> object, containing the toolbar.
     * @since 1.4
     */
    protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
	class ToolBarDialog extends JDialog {
	    public ToolBarDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	    }

	    public ToolBarDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	    }

	    // Override createRootPane() to automatically resize
	    // the frame when contents change
	    protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();// {
//		    private boolean packing = false;
//
//		    public void validate() {
//			super.validate();
//			if (!packing) {
//			    packing = true;
//			    pack();
//			    packing = false;
//			}
//		    }
//		};
		rootPane.setOpaque(true);
		return rootPane;
	    }
	}

	JDialog dialog;
	Window window = SwingUtilities.getWindowAncestor(toolbar);
	if (window instanceof Frame) {
	    dialog = new ToolBarDialog((Frame)window, toolbar.getName(), false);
	} else if (window instanceof Dialog) {
	    dialog = new ToolBarDialog((Dialog)window, toolbar.getName(), false);
	} else {
	    dialog = new ToolBarDialog((Frame)null, toolbar.getName(), false);
	}

        dialog.getRootPane().setName("ToolBar.FloatingWindow");
	dialog.setTitle(toolbar.getName());
//	dialog.setResizable(false);
	WindowListener wl = createFrameListener();
	dialog.addWindowListener(wl);
        return dialog;
    }
}
