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

package au.com.nicta.ct.solution.graphics.canvas.tools.annotations;

import au.com.nicta.ct.graphics.canvas.tools.CtTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author alan
 */
public class CtDeleteAnnotationTool extends CtTool {

    public CtAnnotationsController ac;

    JComboBox typesCombo;

    public CtDeleteAnnotationTool( CtToolsModel tm, String name, CtAnnotationsController ac ) {
        super(tm, name);
        this.ac = ac;
    }

    @Override protected String iconFile() {
        return "annotation_delete.png";
    }

    @Override protected String toolTip() {
        return "Delete annotation";
    }

    @Override public JComponent panel() {
        JPanel empty = new JPanel(); // empty panel
        empty.setOpaque( false );
        empty.add( new JLabel( "Click annotations to delete." ) );
        empty.setPreferredSize( empty.getMinimumSize() );
        return empty;
    }

    @Override public void activate() {
        super.activate();
        ac.setMode( CtAnnotationsController.MODE_DELETE );
    }

}
