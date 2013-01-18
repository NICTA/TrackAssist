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

package au.com.nicta.ct.ui.style;

import au.com.nicta.ct.ui.swing.util.CtIcons;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * To get a consistent theme for UI components, have centralized instantiation
 * and styling / appearance changes.
 * @author davidjr
 */
public class CtComponentStyle {

    public static JToggleButton createToggleButton( String filename, String toolTip, boolean selected ) {
        ImageIcon ii = CtIcons.loadIcon( filename );
        return createToggleButton( ii, toolTip, selected );
    }

    public static JToggleButton createToggleButton( ImageIcon ii, String toolTip, boolean selected ) {
        JToggleButton b = new JToggleButton( ii, selected );
        styleButton( b, toolTip );
        return b;
    }

    public static JButton createButton( String filename, String toolTip ) {
        ImageIcon ii = CtIcons.loadIcon( filename );
        return createButton( ii, toolTip );
    }

    public static JButton createButton( ImageIcon ii, String toolTip ) {
        JButton b = new JButton( ii );
        styleButton( b, toolTip );
        return b;
    }

    public static void styleButton( AbstractButton b, String toolTip ) {
//        b.setOpaque( false );
        b.setToolTipText( toolTip );
    }

    public static void styleButton( AbstractButton b ) {
//        b.setOpaque( false );
    }

}
