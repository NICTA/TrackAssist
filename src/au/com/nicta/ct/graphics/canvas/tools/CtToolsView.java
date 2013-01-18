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

package au.com.nicta.ct.graphics.canvas.tools;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtToolsView extends CtChangeModel implements ActionListener {

    public static final String TOOLBAR_PANEL_MAX_W_PIXELS_PROPERTY_KEY = "toolbar-max-width-pixels";
    public static final String TOOLBAR_PANEL_MAX_H_PIXELS_PROPERTY_KEY = "toolbar-max-height-pixels";

    CtToolsModel _tm;

    JPanel _cards;
    HashMap< CtTool, AbstractButton > _toolsButtons = new HashMap< CtTool, AbstractButton >();
//    HashMap< CtTool, CtToolsViewActionListener > _toolsListeners = new HashMap< CtTool, CtToolsViewActionListener >();

    public static class CardLayout2 extends CardLayout { // make cardlayout resizable

        @Override public Dimension preferredLayoutSize(Container parent) {
            int w = 0;
            for (Component comp : parent.getComponents()) {
                System.out.println(w);
                if ( w < comp.getPreferredSize().width ) {
                    w = comp.getPreferredSize().width;
                }
            }
            Component current = findCurrentComponent(parent);
            if (current != null) {
                Insets insets = parent.getInsets();
                Dimension pref = current.getPreferredSize();
                pref.width = w + insets.left + insets.right;
                pref.height += insets.top + insets.bottom;
                return pref;
            }
            return super.preferredLayoutSize(parent);
        }

        public Component findCurrentComponent(Container parent) {
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }

    public CtToolsView( final JPanel p, CtToolsModel tm, int wGrid ) {

        super( null );
        
        _tm = tm;
//        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.setBackground( CtConstants.NictaYellow );
//        p.addPropertyChangeListener( new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                p.getRootPane().revalidate();
//            }
//        }); // revalidate to fix the layout when undock any toolbar

        Collection< CtTool > tools = tm.getTools();

//        int wGrid = 2;
        int hGrid = tools.size() / wGrid;

        if( ( wGrid * hGrid ) < tools.size() ) {
            ++hGrid;
        }

//        JPanel upper = new JPanel(); // tool buttons
        JPanel upper = new JPanel( new GridLayout( hGrid, wGrid ) ); // tool buttons
//        GridLayout bl1 =  upper, // BoxLayout.Y_AXIS );//BoxLayout.PAGE_AXIS );
//        upper.setLayout( bl1 );
        upper.setOpaque( false );

        JPanel lower = new JPanel( new CardLayout2() ); // tool cards
        lower.setOpaque( false );

//        int wMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_W_PIXELS_PROPERTY_KEY ).getValue() );
//        int hMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_H_PIXELS_PROPERTY_KEY ).getValue() );

        for( CtTool t : tools ) {
            AbstractButton b = t.button();
//            JPanel card = t.panel();
            JComponent card = t.panel();

            b.setActionCommand( t.name() );
//            CtToolsViewActionListener tval = new CtToolsViewActionListener( lower );
//            b.addActionListener( tval );
            b.addActionListener( this );
            upper.add( b );
            lower.add( card, b.getActionCommand() );

            _toolsButtons.put( t, b );
//            _toolsListeners.put( t, tval );

            t.changeSupport.addListener( CtTool.EVT_ACTIVE, this ); // tell me when a tool changes status
        }

        _cards = lower;
        
        p.add( upper, BorderLayout.NORTH );
        p.add( lower, BorderLayout.CENTER );

//        p.setMinimumSize( new Dimension( wMax, hMax ) );
//        p.setMaximumSize( new Dimension( wMax, hMax ) );
//        p.validate();

    }

    public static Dimension preferredMaximumSize() {
        int wMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_W_PIXELS_PROPERTY_KEY ).getValue() );
        int hMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_H_PIXELS_PROPERTY_KEY ).getValue() );
        return new Dimension( wMax, hMax );
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        if( !evt.getPropertyName().equals( CtTool.EVT_ACTIVE ) ) {
            return;
        }
        // when the default tool is enabled:
//        String s = ae.getActionCommand();

        Collection< CtTool > c = _tm.getTools();

        for( CtTool t : c ) {
            AbstractButton ab = _toolsButtons.get( t );

            boolean active = t.isActive();

            if( ab.isSelected() != active ) {
                ab.setSelected( active );

//                CtToolsViewActionListener tval = _toolsListeners.get( t );
//                tval.actionPerformed( new ActionEvent( t.name() ) );
                if( active ) {
                    showCard( t.name() );
                }
            }
        }
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();
        showCard( s );
    }

    public void showCard( String s ) {
        CardLayout cl = (CardLayout)( _cards.getLayout() );
        cl.show( _cards, s );
//        int wMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_W_PIXELS_PROPERTY_KEY ).getValue() );
//        int hMax = Integer.valueOf( CtKeyValueProperties.find( TOOLBAR_PANEL_MAX_H_PIXELS_PROPERTY_KEY ).getValue() );
//
//        _cards.setSize( new Dimension( wMax, hMax ) );
//        _cards.setMinimumSize( new Dimension( wMax, hMax ) );
//        _cards.setMaximumSize( new Dimension( wMax, hMax ) );
//        p.validate();
    }
    
}

//class CtToolsViewActionListener implements ActionListener {
//
//    JPanel _p;
//
//    public CtToolsViewActionListener( JPanel p ) {
//        _p = p;
//    }
//
//    @Override public void actionPerformed( ActionEvent ae ) {
//        String s = ae.getActionCommand();
//    }
//
//    public void show
//        CardLayout cl = (CardLayout)( _p.getLayout() );
//        cl.show( _p, s );
//    }
//}
