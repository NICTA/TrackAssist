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

import au.com.nicta.ct.ui.swing.util.CtComponentHierarchy;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.awt.GridLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A combination of a desktop-environment (e.g. like a bunch of internal frames
 * within a bounded fully-fledged frame). Each panel in the grid is a toolbar
 *
 * @author davidjr
 */
public class CtDockableWindowGrid extends JPanel implements HierarchyListener {

    CtChangeSupport _cs = new CtChangeSupport( this );
    GridLayout _gl;
    int _itemCount = 0;

    public HashSet< CtWindowContentFactory > _cwcf = new HashSet< CtWindowContentFactory >();
    public HashMap< String, CtDockableWindow > _grid = new HashMap< String, CtDockableWindow >(); // what is docked where

    public static final String EVT_GRID_ITEM_ADDED   = "grid-item-added";
    public static final String EVT_GRID_ITEM_REMOVED = "grid-item-removed";

    public CtDockableWindowGrid( int rows, int cols ) {

        _gl = new GridLayout( rows, cols );

        setLayout( _gl );
        setOpaque( false );

        addNotify();
    }

    public void addListener( CtChangeListener cl ) {
        _cs.addListener( cl );
    }
    
    public void addListener( CtChangeListener cl, String event ) {
        _cs.addListener( event, cl );
    }

    public void addWindowContentFactory( CtWindowContentFactory wcf ) {
        _cwcf.add( wcf );
//        refreshSubMenus();
    }

    public void closeWindows() {
        Set< Entry< String, CtDockableWindow > > s = _grid.entrySet();

        HashSet< String > windowNames = new HashSet< String >(); // avoid concurrent mod. excep.

        Iterator i = s.iterator();

        while( i.hasNext() ) {
            Entry< String, CtDockableWindow > e = (Entry< String, CtDockableWindow >)i.next();
            final String windowName = e.getKey();

            windowNames.add( windowName );
        }

        for( String windowName : windowNames ) {
            closeWindow( windowName );
        }
    }

    public void closeWindow( String windowName ) {
        CtDockableWindow dw = getWindow( windowName );
        String type = dw.getType();

        for( CtWindowContentFactory wcf : _cwcf ) {
            String s = wcf.getWindowType();

            if( s.equals( type ) ) {
                wcf.onWindowClosing( dw.getContent() );
            }
        }

        remove( windowName );
    }

    public void openWindow( String windowType ) {
        for( CtWindowContentFactory wcf : _cwcf ) {
            String s = wcf.getWindowType();

            if( s.equals( windowType ) ) {

                JComponent c = wcf.createComponent();

                String windowName = add( windowType, c );

                CtDockableWindow dw = getWindow( windowName );

                wcf.onWindowOpening( dw, c );

                return;
            }
        }
    }

    public CtDockableWindow getWindow( String windowName ) {
        return _grid.get( windowName );
    }
    
    public String getType( String windowName ) {
        CtDockableWindow ddtb = _grid.get( windowName );
        return ddtb.getType();
    }

    public Collection< CtDockableWindow > findWindowsOfType( Collection< String > windowTypes ) {

        ArrayList< CtDockableWindow > al = new ArrayList< CtDockableWindow >();

        Set< Entry< String, CtDockableWindow > > es = _grid.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtDockableWindow > e = (Entry< String, CtDockableWindow >)i.next();

            CtDockableWindow dw = e.getValue();

            for( String s : windowTypes ) {
                if( s.equals( dw.getType() ) ) {
                    al.add( dw );
                    break;
                }
            }
        }

        return al;
    }

    public void remove( String windowName ) {
        CtDockableWindow ddtb = _grid.remove( windowName );

        if( ddtb == null ) {
            return;
        }

//        --_itemCount; never forget, as might redock something.
        
        _cs.fire( EVT_GRID_ITEM_REMOVED );

        if( !ddtb.isDocked() ) {
            ddtb.close();
        }
        else {
            remove( ddtb );
            revalidate();
            repaint();
        }
    }

    public String add( String type, JComponent c ) {

        // all start off docked..
        String title = "Window "+String.valueOf( _itemCount+1 )+ ": "+type;

        return add( title, type, c );
    }
    
    public String add( String title, String type, JComponent c ) {

        // all start off docked..
        CtDockableWindow ddtb = createGridItem( title, type, c );

        String name = ddtb.getName();

        add( ddtb );

        _grid.put( name, ddtb );

        ++_itemCount;

 //       ddtb.add( c );
//        addContentTo( ddtb, name, c );

        add( ddtb ); // will get last position
        revalidate();

        _cs.fire( EVT_GRID_ITEM_ADDED );

        return name;
    }

//    protected void addContentTo( JToolBar tb, String title, JComponent content ) {
//        // override this to change the style
//        tb.setLayout( new BorderLayout() );
//        JLabel l = new JLabel( title );
//        l.setBackground( Color.WHITE );
//        tb.add( l, BorderLayout.NORTH );
//        tb.add( content, BorderLayout.CENTER );
//    }

    protected CtDockableWindow createGridItem( String title, String type, JComponent c ) {

        CtDockableWindow ddtb = new CtDockableWindow( title, type, c );

        return ddtb;
    }

    @Override public void addNotify() {
        super.addNotify();
        addHierarchyListener( this );
    }

    @Override public void removeNotify() {
        removeHierarchyListener( this );
        super.removeNotify();
    }

    public void hierarchyChanged( HierarchyEvent he ) {
        if( !CtComponentHierarchy.isComponentSubtreeVisible( this ) ) {
            closeWindows();
        }
    }

}
