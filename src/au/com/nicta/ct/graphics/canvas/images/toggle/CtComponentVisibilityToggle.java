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

package au.com.nicta.ct.graphics.canvas.images.toggle;

import au.com.nicta.ct.ui.style.CtComponentStyle;
import au.com.nicta.ct.ui.swing.util.CtIcons;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

/**
 *
 * @author davidjr
 */
public class CtComponentVisibilityToggle implements CtChangeListener {

    protected String _edge = BorderLayout.SOUTH;
    protected String _name;
    protected String _iconFile;
    protected String _toolTip;

//    protected JToggleButton _tb;
    protected AbstractAction _a;
    protected JComponent _controls;

//    public static void addToggleButton( JComponent parent, JComponent controls, String title, boolean enable ) {
//        CtComponentVisibilityToggle tt = new CtComponentVisibilityToggle( title, controls );
//        tt.addToggleButtonTo( parent, enable );
//    }
//
    public CtComponentVisibilityToggle( String actionName, String iconFile, String toolTip ) {
        this( actionName, iconFile, toolTip, null );
    }

    public CtComponentVisibilityToggle( String actionName, String iconFile, String toolTip, JComponent controls ) {
        _name = actionName;
        _controls = controls;
        _iconFile = iconFile;
        _toolTip = toolTip;
        getAction();
//        setComponentVisible( getSelected() );
    }

    public String getEdge() {
        return _edge;
    }

    public String setEdge( String edge ) {
        return _edge = edge;
    }

    public String getName() {
        return _name;
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        JComponent controls = getComponent();

        if( controls != null ) {
            controls.getParent().repaint();
        }
    }

//    public void addToggleButtonTo( JComponent parent, boolean enable ) {
//        JToggleButton b = createToggleButton();
//
//        parent.add( b );
//
//        setSelected( enable );
//    }
//
    public JToggleButton createToggleButton() {
        JToggleButton tb = new JToggleButton( getAction() );
        
        if( _toolTip != null ) {
            CtComponentStyle.styleButton( tb, _toolTip );
        }
        else {
            CtComponentStyle.styleButton( tb );
        }

        if( _iconFile != null ) {
            ImageIcon ii = CtIcons.loadIcon( _iconFile );
            tb.setIcon( ii );
        }

        return tb;
    }

    public JComponent getComponent() {
        return _controls;
    }

    public Action getAction() {
        if( _a == null ) {
            final AbstractAction a = new AbstractAction( _name ) {
    //        a.addPropertyChangeListener( new PropertyChangeListener() {
                public void actionPerformed( ActionEvent e ) {
                    boolean selected = getSelected();
                    setComponentVisible( selected );
//                    Boolean isSelected = (Boolean)getValue( Action.SELECTED_KEY );
//
//                    if( isSelected == null ) {
//                        isSelected = false;
//                    }
//                    toggleSelected();
//                    setSelected( isSelected );
//                    setComponentVisible( isSelected );
                }
            };

            _a = a;
        }
        
        return _a;
    }

    public void toggleSelected() {
        setSelected( !getSelected() );
    }
    
    public boolean getSelected() {
        assert( _a != null );

        Boolean isSelected = (Boolean)_a.getValue( Action.SELECTED_KEY );

        if( isSelected == null ) {
            _a.putValue( Action.SELECTED_KEY, false );

            return false;
        }

        return isSelected;
    }

    public void setSelected( boolean selected ) {

        setComponentVisible( selected );
        
        assert( _a != null );

        Boolean isSelected = (Boolean)_a.getValue( Action.SELECTED_KEY );

        if( isSelected != null ) {
            if( isSelected == selected ) {
                return;
            }
        }

        _a.putValue( Action.SELECTED_KEY, selected );
        
//        setComponentVisible( selected );
    }
//    public CtAbstractFactory< JComponent > createCheckableMenuItemFactory() {
//        Action a = getAction();
//    }

    protected void setComponentVisible( boolean b ) {

        JComponent controls = getComponent();

        if( controls != null ) {
            if( controls.isVisible() != b ) {
                controls.setVisible( b );
    //            tools.getParent().repaint();
                controls.repaint();
            }
        }

    }

}
