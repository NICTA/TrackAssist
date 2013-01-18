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

import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Menu actions/commands for the MDI components..
 *
 * Window
 *    - Add (creates a new window)
 *    - Close
 *         - Window XXXX
 *         - Window XXXX
 *
 * TODO: Add window arrangement (e.g. tiling) options... (sticky grid sizing)
 * 
 * @author davidjr
 */
public class CtWindowMenu extends JMenu implements CtChangeListener {

    CtDockableWindowGrid _ddg;

    public JMenu _add;
    public JMenu _remove;

    public CtWindowMenu( CtDockableWindowGrid ddg ) {
        super( "Window" );
        _ddg = ddg;
        _ddg.addListener( this );

        addSubMenus();
    }

    public void addToFrame() {
        CtPageFrame f = CtPageFrame.find();
        JMenuBar mb = f.getOrCreateMenuBar();
        mb.add( this );
    }

//    public void addWindowContentFactory( CtWindowContentFactory wcf ) {
//        _cwcf.add( wcf );
////        refreshSubMenus();
//    }

    public void addSubMenus() {
        _add    = new JMenu( "Add" );
        _remove = new JMenu( "Remove" );

        this.add( _add );
        this.add( _remove );
        
        refreshSubMenus();
    }

    public void refreshSubMenus() {
        _add.removeAll();
        _remove.removeAll();

        // allow option to create each type of window
        for( CtWindowContentFactory wcf : _ddg._cwcf ) {
            final String s = wcf.getWindowType();
            JMenuItem mi = new JMenuItem( s+" window" );
            mi.addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent ae ) {
                    _ddg.openWindow( s );
                }
            } );

            _add.add( mi );
        }

        // allow option to remove each window by name:
        Set< Entry< String, CtDockableWindow > > s = _ddg._grid.entrySet();

        Iterator i = s.iterator();

        while( i.hasNext() ) {
            Entry< String, CtDockableWindow > e = (Entry< String, CtDockableWindow >)i.next();
            final String windowName = e.getKey();
            JMenuItem mi = new JMenuItem( windowName );
            mi.addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent ae ) {
                    _ddg.closeWindow( windowName );
                }
            } );

            _remove.add( mi );
        }
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        String s = evt.getPropertyName();

        if( s.equals( CtDockableWindowGrid.EVT_GRID_ITEM_ADDED ) ) {
            refreshSubMenus();
        }
        else if( s.equals( CtDockableWindowGrid.EVT_GRID_ITEM_REMOVED ) ) {
            refreshSubMenus();
        }
    }

}
