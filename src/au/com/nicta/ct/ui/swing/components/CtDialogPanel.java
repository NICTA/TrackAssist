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

package au.com.nicta.ct.ui.swing.components;

import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel has the look of a wizard dialog - ie some stuff centred in the middle,
 * finite fixed width as % of screen width.
 * @author davidjr
 */
public class CtDialogPanel {

    public static class CtComponentResizePolicy {

        JComponent _c;
        boolean _allowVerticalResize = true;

        public CtComponentResizePolicy( JComponent c, boolean allowVerticalResize ) {
            _c = c;
            _allowVerticalResize = allowVerticalResize;
        }
    }

    public static final double DIALOG_WIDTH = 0.4;

    public static JPanel create( Collection< CtComponentResizePolicy > c ) {

        int heightResizableComponents = 0;
        
        for( CtComponentResizePolicy crp : c ) {
            if( crp._allowVerticalResize ) {
                ++heightResizableComponents;
            }
        }

        JPanel dialog = new JPanel();// new BorderLayout() );
//        dialog.setBackground(Color.blue );
        dialog.setLayout( new GridBagLayout() );
        dialog.setOpaque( false );
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel blank0 = new JPanel( new FlowLayout() );
        blank0.setOpaque( false );
//        blank0.add( new JLabel("blah0") );
        JPanel blank2 = new JPanel( new FlowLayout() );
        blank2.setOpaque( false );
//        blank2.add( new JLabel("blah0") );
//        blank0.setBackground(Color.pink );
//        blank2.setBackground(Color.magenta );
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.gridheight = c.size();//heightresizableComponents;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = ( 1.0 - DIALOG_WIDTH ) * 0.5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        dialog.add( blank0, gbc );
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        dialog.add( blank2, gbc );
        gbc.weightx = DIALOG_WIDTH;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;

        gbc.anchor = GridBagConstraints.PAGE_START;
        // for-each( component ):
        int yComponent = 0;
        double resizableWeightY = 1.0 / (double)heightResizableComponents;

        for( CtComponentResizePolicy crp : c ) {
            gbc.gridy = yComponent;

            ++yComponent;
            
            if( crp._allowVerticalResize ) {
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weighty = resizableWeightY;
            }
            else {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 0.0;
            }
            dialog.add( crp._c, gbc );
        }
        
        return dialog;
    }
}
