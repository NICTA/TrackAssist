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

import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeSupport;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Graphics tools for interacting with canvases..
 * @author Alan
 */
public abstract class CtTool implements CtZoomCanvasLayerListener {

    protected abstract String iconFile();
    protected abstract String toolTip();
    protected          String toolDescription() {
        return name();
    }

    public void   activate(){};
    public void deactivate(){};

    public Collection< String > getCanvasLayerNames() {
        return new ArrayList< String >();
    }

    public void onCreateCanvasLayer( CtCanvasLayer cl ){} // tells the tool about canvases it may need to respond to, as these are all graphical tools
    public void onDeleteCanvasLayer( CtCanvasLayer cl ){}

    public AbstractButton button() {
        ImageIcon ii = new ImageIcon( CtApplication.datafile( iconFile() ) );
        JToggleButton b = new JToggleButton( ii );
        b.setToolTipText( toolTip() );
        b.setMaximumSize( b.getPreferredSize() );
        addButton( b );
        return b;
//        p.add( b );
    }

    public JComponent panel() {
        JPanel empty = new JPanel(); // empty panel
        empty.setOpaque( false );
        empty.add( new JLabel( toolDescription() ) );
        empty.setPreferredSize( empty.getMinimumSize() );
        return empty;
    }

    public String name() {
        return _name;
    }
//    static final BasicStroke CURSOR_STROKE = new BasicStroke(0);

//    protected String enabledEvent() {
//        return _name + "-enabled";
//    }
//
//    protected String activeEvent() {
//        return _name + "-active";
//    }

    public static final String EVT_ENABLE = "tool-enable";
    public static final String EVT_ACTIVE = "tool-active";

    CtChangeSupport changeSupport = new CtChangeSupport( this );

    // External passed in
//    public CtZoomCanvas _zc;
    protected String _name;
//    private CtMode _mode;
    protected CtToolsModel _tm;
    
    // states
    boolean enabled = true;
    boolean active = false;

    public CtTool( /*CtZoomCanvas zc, CtMode mode,*/ CtToolsModel tm, String name ) {
//        this._zc = zc;
//        this._mode = mode;
        this._name = name;
        this._tm = tm;

        updateEnabled();
        setActive( false );
        tm.add( this );
    }

    public void updateEnabled() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled( boolean newValue ) {
        if( enabled == newValue ) {
            return;
        }
        enabled = newValue;

        if( !enabled ) {
            setActive( false );
        }
//        changeSupport.fire(EVT_ENABLED, !newValue, newValue);
        changeSupport.fire( EVT_ENABLE, !newValue, newValue );
    }

    public boolean isActive() {
        return active;
    }

    protected void setActive( boolean newValue ) {

        // don't change needlessly.
        if( active == newValue ) {
            return;
        }

        // don't activate if disabled:
        if( newValue ) {
            if( !enabled ) {
                return;
            }
        }

        // activate exclusively:
        if( newValue ) { // try to set active
//            _tm.enableDefault( false );
//System.out.println( name()+" DEactivating all" );
            _tm.deactivateAll(); // get rid of all the others
//            _tm.enableDefault( true );
//System.out.println( name()+" Activating" );
            active = newValue;
            activate();
        }
        else {
            active = newValue;
            deactivate();
        }

//System.out.println( "fire "+name()+" active="+newValue );
        changeSupport.fire( EVT_ACTIVE, !newValue, newValue );
//        changeSupport.fire(EVT_ACTIVE, !newValue, newValue);
//        changeSupport.fire( activeEvent(), !newValue, newValue);
    }

    protected void updateButton( JToggleButton button ) {
//        button.setEnabled( _mode.isEnabled() && this.isEnabled() );
        button.setEnabled( this.isEnabled() );
        button.setSelected( this.isActive() );
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * This is non-public so that the derived class can choose to delegate
     * @param button
     */
    public void addButton( final JToggleButton button ) {
        // When user click, set the tool active.
        button.addItemListener( new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setActive( button.isSelected() );
            }
        });

        // Disable button when the mode is disabled
//        _mode.changeSupport.addListener(CtMode.EVT_ENABLED, new CtChangeListener() {
//        _mode.changeSupport.addListener(CtMode.EVT_ENABLED, new CtChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                updateButton(button);
//            }
//        });

        // Make button's enabled property track the enabled property of this tool
        changeSupport.addListener( EVT_ENABLE, new CtChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateButton(button);
            }
        });

        // Make button's selected property track the active property of this tool
        changeSupport.addListener( EVT_ACTIVE, new CtChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateButton(button);
            }
        });

        updateButton(button);
    }

}
