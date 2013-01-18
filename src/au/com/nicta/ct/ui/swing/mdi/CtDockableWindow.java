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

import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.ui.swing.components.CtToolBarStack;
import au.com.nicta.ct.ui.swing.components.CtBackgroundImagePanel;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import com.sun.java.swing.plaf.windows.WindowsToolBarUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ToolBarUI;

/**
 * Wrapper for undockable window components .. since the toolbars themselves are
 * transient depending on docked state?
 * 
 * @author davidjr
 */
public class CtDockableWindow extends JToolBar implements AncestorListener {
        
    protected String _type;
    protected JToolBar _north;
    protected CtToolBarStack _west; // created on demand, not floatable
    protected CtToolBarStack _east; // created on demand, not floatable
    protected CtToolBarStack _south; // created on demand, not floatable
    protected JPanel _centre;
    protected JComponent _content;

    public CtDockableWindow( String title, String type, JComponent content ) {
        super( title, JToolBar.HORIZONTAL );

        _type = type;
        
        addAncestorListener( this );
        setFloatable( true );
        setResizableUI();
        setBackground( CtConstants.NictaYellow );
        addContents( title, content );
    }

    public String getType() {
        return _type;
    }

    public JComponent getContent() {
        return _content;
    }
    
    protected void addContents( String title, JComponent content ) {
        // override this to change the style
        _content = content;

        setLayout( new BorderLayout() );

        try {
            _centre = new CtBackgroundImagePanel();
            _centre.setLayout( new BorderLayout() );
//            _centre.setBorder( new LineBorder( Color.LIGHT_GRAY ) );

            add( _centre, BorderLayout.CENTER );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit( -1 );
        }
        
        _north = new JToolBar( JToolBar.HORIZONTAL );
        _north.setFloatable( false );
        _north.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        _north.setAlignmentX( LEFT_ALIGNMENT );
        _north.setBackground( CtConstants.NictaYellow );

//        _tb.setBackground( Color.WHITE );
//        _tb.setBorder( new LineBorder( Color.LIGHT_GRAY ) );

        if( title != null ) {
            JLabel l = new JLabel( title );//CtApplication.h3( title+" &nbsp;&nbsp;&nbsp; " ) );

            _north.add( l );
            _north.addSeparator();
        }

        _content.setBorder( new LineBorder( Color.LIGHT_GRAY ) );

        _centre.add( _north, BorderLayout.NORTH );
        _centre.add( _content, BorderLayout.CENTER );
    }

    public JToolBar getToolBar() {
        return _north;
    }

    public CtToolBarStack getToolBarStack( String side ) {
        if( side.equals( BorderLayout.EAST ) ) {
            if( _east == null ) {
                _east = createToolBarStack();
                add( _east, side );
            }
            return _east;
        }

        if( side.equals( BorderLayout.SOUTH ) ) {
            if( _south == null ) {
                _south = createToolBarStack();
                add( _south, side );
            }
            return _south;
        }

        if( side.equals( BorderLayout.WEST ) ) {
            if( _west == null ) {
                _west = createToolBarStack();
                add( _west, side );
            }
            return _west;
        }

        return null;
    }

    protected CtToolBarStack createToolBarStack() {
        CtToolBarStack tbs = new CtToolBarStack();
        tbs.setFloatable( false );
        return tbs;
    }

    public void setResizableUI() {
        ToolBarUI tbui = getUI();

        if( tbui instanceof WindowsToolBarUI ) {
            setUI( new CtResizableWindowsToolBarUI() );
        }
        else {
            throw new ClassCastException( "ERROR: Don't have a toolbar UI delegate for non-Windows platforms (TODO)." );
        }
    }

    public boolean isDocked() {
        Container c = getTopLevelAncestor();

        if( c == null ) {
            return false;
        }

        if( c instanceof JDialog ) {
            return false;
        }

        if( c instanceof JFrame ) {
            return true;
        }

        return true;
    }

    public void close() {
        Container c = getTopLevelAncestor();

        if( c instanceof JDialog ) {
            JDialog d = (JDialog)c;
            d.dispose();
        }
    }

    @Override public void ancestorAdded( AncestorEvent event ) {}
    @Override public void ancestorMoved( AncestorEvent event ) {}

    // Called when the source or one of its ancestors is made invisible either by setVisible(false) being called or by its being remove from the component hierarchy.throws
    @Override public void ancestorRemoved( AncestorEvent event ) {
        Container c = getTopLevelAncestor();

        if( c == null ) {
            return;
        }

        CtPageFrame f = CtPageFrame.find();

        if( c != f ) {
//            c.removeAll();
            c.setVisible( false );
        }
   }
}
