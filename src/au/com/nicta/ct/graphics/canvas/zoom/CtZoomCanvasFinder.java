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

package au.com.nicta.ct.graphics.canvas.zoom;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindow;
import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;

/**
 * Finds windows containing Zoom Canvases by enumerating all dockable windows of
 * a particular type, and assuming if their content panel is a ZoomCanvasPanel
 * then we're interested in them.
 *
 * @author davidjr
 */
public class CtZoomCanvasFinder {

    public static Collection< CtZoomCanvasPanel > find( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {

        ArrayList< CtZoomCanvasPanel > al = new ArrayList< CtZoomCanvasPanel >();

        Collection< CtDockableWindow > cdw = dwg.findWindowsOfType( windowTypes ); // e.g. all imaging windows

        for( CtDockableWindow dw : cdw ) {
            JComponent c = dw.getContent();

            if( c instanceof CtZoomCanvasPanel ) {
                al.add( (CtZoomCanvasPanel)c );
            }
        }
        
        return al;
    }


}
