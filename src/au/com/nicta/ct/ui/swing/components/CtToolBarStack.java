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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * A set of undockable panels.
 * 
 * Dynamically expandable stack of swing panels each representing some sort of
 * undockable toolbar. The user only needs to provide the panels containing the
 * tool components, all the undockable stuff is done by this class. 
 *
 * @author davidjr
 */
public class CtToolBarStack extends JPanel implements AncestorListener {

    protected boolean _floatable = true;
    protected JPanel _stack;
    protected ArrayList< JToolBar > _tbs = new ArrayList< JToolBar >();
    
    public CtToolBarStack() {
        this( Color.WHITE );
    }

    public CtToolBarStack( Color background ) {
        super( new BorderLayout() );

        addAncestorListener( this );

        _stack = this;

        setBorder( new LineBorder( Color.LIGHT_GRAY ) );
        setOpaque( true );
        setBackground( background );
    }

    public boolean getFloatable() {
        return _floatable;
    }

    public void setFloatable( boolean floatable ) {
        _floatable = floatable; // in case we have no widgets
        for( JToolBar tb : _tbs ) {
            tb.setFloatable( _floatable );
        }
    }
    // Called when the source or one of its ancestors is made visible either by setVisible(true) being called or by its being added to the component hierarchy.
    public void ancestorAdded( AncestorEvent event ) {
        
    }

    // Called when either the source or one of its ancestors is moved.
    public void ancestorMoved( AncestorEvent event ) {

    }

    // Called when the source or one of its ancestors is made invisible either by setVisible(false) being called or by its being remove from the component hierarchy.throws
    public void ancestorRemoved( AncestorEvent event ) {
        closeToolBars();
    }

    public void closeToolBars() {
        _stack.removeAll();

        if( !_floatable ) {
            return;
        }
        
        CtPageFrame f = CtPageFrame.find();

        for( JToolBar tb : _tbs ) {
            tb.removeAll();
            Container c = tb.getTopLevelAncestor();

            if( c != f ) {
                c.removeAll();
                c.setVisible( false );
//                if( c instanceof JFrame ) {
//                    JFrame frame = (JFrame)c;
//
//                    frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
//
//                    frame.dispose();
////                    frame.close();
//                }
            }
        }
    }

    public void addTool( Component c, String title ) {
        final JToolBar tb = new JToolBar( title );

        tb.setFloatable( _floatable );

        _tbs.add( tb );

        tb.setBackground( Color.WHITE );//CtConstants.NictaYellow );

        JPanel p = new JPanel( new BorderLayout() );
        JLabel l = new CtToolBarStackLabel( "<html><b>"+title+"</b></html>", c );//JLabel( title );
        JLabel l2 = new JLabel();
        l2.setOpaque( true );
        l2.setBackground( Color.WHITE );

        p.setOpaque( false );
        p.setMinimumSize( new Dimension( 0,0 ) );
        l.setMinimumSize( new Dimension( 0,0 ) );
        l2.setMinimumSize( new Dimension( 5,5 ) );
        l2.setPreferredSize( new Dimension( 5,5 ) );
 //       p.setBackground( Color.WHITE );

        p.add( l, BorderLayout.NORTH );
        p.add( c, BorderLayout.CENTER );
        p.add( l2, BorderLayout.SOUTH );

        tb.add( p );
//        tb.addSeparator();

        _stack.add( tb, BorderLayout.NORTH );

        push();

        // revalidate to fix the layout when undock any toolbar
        tb.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                JRootPane rp = tb.getRootPane();
                if( rp != null ) rp.revalidate();
            }
        });
    }

    protected void push() {
        JPanel inner = new JPanel( new BorderLayout() );
        inner.setOpaque( false );
 //       inner.setBackground( Color.WHITE );
        _stack.add( inner, BorderLayout.CENTER );
        _stack = inner;
    }

}
