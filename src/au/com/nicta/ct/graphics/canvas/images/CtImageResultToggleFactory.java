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

package au.com.nicta.ct.graphics.canvas.images;

import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.graphics.canvas.images.toggle.CtComponentVisibilityToggle;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 *
 * @author davidjr
 */
public abstract class CtImageResultToggleFactory {

    public abstract CtComponentVisibilityToggle createImageResultToggle( CtImageResultPanelFactory irpf, CtZoomCanvasPanel zcp );
//    public abstract String getControlsTitle();

//    public void addImageResultToggle( CtImageResultPanelFactory irpf, CtZoomCanvasPanel zcp ) {
//        CtComponentVisibilityToggle irt = createImageResultToggle( irpf, zcp );
//        JToolBar tb = zcp.getToolBar();
//        irt.addButtonTo( tb, false );
//
//        JComponent controls = irt.getControls();
//
//        if( controls == null ) {
//            return;
//        }
//
//        String edge = irt.getEdge(); //getToolBarStackSide();
//        String title = irt.getName();//getControlsTitle();
//
//        CtToolBarStack tbs = zcp.getToolBarStack( edge );
//        tbs.addTool( controls, title );
//    }
//
//    public String getToolBarStackSide() {
//        return BorderLayout.SOUTH;
//    }

}
