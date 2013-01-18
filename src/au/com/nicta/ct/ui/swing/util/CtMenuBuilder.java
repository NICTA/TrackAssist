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

package au.com.nicta.ct.ui.swing.util;

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author davidjr
 */
public class CtMenuBuilder {

//    public static CtSubMenuFactories get() {
//        CtSubMenuFactories smf = (CtSubMenuFactories)CtObjectDirectory.get( CtSubMenuFactories.name() );
//        if( smf == null ) {
//            smf = new CtSubMenuFactories();
//        }
//        return smf;
//    }

    protected HashMap< String, ArrayList< CtAbstractFactory< JComponent > > > _subMenuFactories = new HashMap< String, ArrayList< CtAbstractFactory< JComponent > > >();
    protected HashMap< Integer, String > _subMenuOrdering = new HashMap< Integer, String >();

    public CtMenuBuilder() {

    }

//    public static String name() {
//        return "sub-menu-factories";
//    }

    public Collection< String > getMenus() {
        return _subMenuFactories.keySet();
    }

    public void clear() {
        _subMenuFactories.clear();
        _subMenuOrdering.clear();
    }

    public void addCheckableMenuItem(
        String menu,
        final Action a ) {
//        final String text, // on screen menu item text
//        final String property, // property listened for / actioned
//        final PropertyChangeListener pcl, // event sink ie menu item updates this
//        final PropertyChangeSupport pcs ) { // event source (ie this updates the checkable menu item
        CtAbstractFactory< JComponent > af = new CtAbstractFactory< JComponent >() {
            @Override public JComponent create() {
                JCheckBoxMenuItem mi = new JCheckBoxMenuItem( a );
                return mi;
            }
        };

        add( menu, af );
    }

    public void addMenuItem( String menu, final String text, final String command, final ActionListener al ) {
        CtAbstractFactory< JComponent > af = new CtAbstractFactory< JComponent >() {
            @Override public JComponent create() {
                JMenuItem mi = new JMenuItem( text );
                mi.setActionCommand( command );
                mi.addActionListener( al );
                return mi;
            }
        };

        add( menu, af );
    }

    public void addMenuItem( String menu, final Action a ) {
        CtAbstractFactory< JComponent > af = new CtAbstractFactory< JComponent >() {
            @Override public JComponent create() {
                JMenuItem mi = new JMenuItem( a );
                return mi;
            }
        };

        add( menu, af );
    }

    public void addSeparator( String menu ) {
        CtAbstractFactory< JComponent > af = new CtAbstractFactory< JComponent >() {
            @Override public JComponent create() {
                JSeparator s = new JSeparator();
                return s;
            }
        };

        add( menu, af );
    }
    
    public void add( String menu, CtAbstractFactory< JComponent > subMenuFactory ) {
        ArrayList< CtAbstractFactory< JComponent > > al = _subMenuFactories.get( menu );

        if( al == null ) {
            al = new ArrayList< CtAbstractFactory< JComponent > >();

            _subMenuFactories.put( menu, al );
//            _subMenuOrdering.put( menuOrdering, menu );
        }

        al.add( subMenuFactory );
    }

    public void setMenuOrder( String menu, int menuOrdering ) {
        ArrayList< CtAbstractFactory< JComponent > > al = _subMenuFactories.get( menu ); // insert empty list if reqd

        if( al == null ) {
            al = new ArrayList< CtAbstractFactory< JComponent > >();

            _subMenuFactories.put( menu, al );
        }

        _subMenuOrdering.put( menuOrdering, menu );
    }

    public void addMenusToFrame() {
        CtPageFrame f = CtPageFrame.find();
        JMenuBar mb = f.getOrCreateMenuBar();
        addMenusTo( mb );
    }
    
    public void addMenusTo( JMenuBar mb ) {

        Collection< Integer > ci = _subMenuOrdering.keySet();

        for( Integer n : ci ) {

            String s = _subMenuOrdering.get( n );
            
            JMenu m = create( s );

//            m.setBackground( CtConstants.NictaYellow );
//            m.setText( s );
//
            mb.add( m );

            ArrayList< CtAbstractFactory< JComponent > > al = _subMenuFactories.get( s );

            for( CtAbstractFactory< JComponent > af : al ) {
                JComponent c = af.create();
//                c.setBackground(Color.red); // menu styling doesn't work in Windows L&F
                m.add( c );
            }
        }
    }

    public JMenu create( String s ) {
        return new JMenu( s );
    }
}
