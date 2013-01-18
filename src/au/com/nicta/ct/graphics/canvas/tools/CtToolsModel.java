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

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.graphics.canvas.brushes.CtBrushLayer;
import au.com.nicta.ct.graphics.canvas.brushes.CtBrushSet;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * One tool exclusively active at all times.
 * Tools are only sometimes enabled to be made active.
 * Default tool in event of no active tool.
 * @author davidjr
 */
public class CtToolsModel extends CtChangeModel implements CtZoomCanvasLayerListener, CtAbstractFactory< CtCanvasLayer > {

    public HashMap< String, CtTool > _tools = new HashMap< String, CtTool >();
    public CtBrushSet _bs = new CtBrushSet();

    String _name;
    String _default;
    boolean _defaultEnabled = true;

    public CtToolsModel( String name ) {
        super( null );

        _name = name;
    }

    public String getCanvasLayerName() {
        return _name + "-" + CtBrushLayer.CANVAS_LAYER_NAME;
    }

    @Override public CtCanvasLayer create() {
        return new CtBrushLayer( getCanvasLayerName(), _bs );
    }

    public Collection< String > getCanvasLayerNames() {
        HashSet< String > hs = new HashSet< String >();

        hs.add( getCanvasLayerName() );

        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();

            Collection< String > cs = t.getCanvasLayerNames();

            for( String s : cs ) {
                hs.add( s );
            }
        }

        return hs;
    }

    public void onCreateCanvasLayer( CtCanvasLayer cl ) {
        // allow all tools a chance to register as listeners to this canvas layer.
        // they will then get events when there are e.g. clicks in the layers..
        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();

            t.onCreateCanvasLayer( cl );
        }
    }

    public void onDeleteCanvasLayer( CtCanvasLayer cl ) {
        // default tool does nothing, because I (my tools) can remain a listener without preventing
        // garbage collection of the layer because of the direction of the references..
        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();

            t.onDeleteCanvasLayer( cl );
        }
    }

    public CtTool get( String name ) {
        return _tools.get( name );
    }
    
    public boolean setDefault( String name ) {
        CtTool t = _tools.get( name );

        if( t != null ) {
            _default = name;
            return true;
        }

        return false;
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        if( !evt.getPropertyName().equals( CtTool.EVT_ACTIVE ) ) {
            return;
        }

//System.out.println( "onchange "+evt.getPropertyName()+" from "+evt.getOldValue()+" to "+evt.getNewValue() );

        Object o = evt.getNewValue();
        if( (Boolean)o == true ) {
            return;
        }

        // new value= not active:
        if( !_defaultEnabled ) {
            return;
        }

        // if( none active ):
//        boolean oneActive = false;
//
//        Set< Entry< String, CtTool > > es = _tools.entrySet();
//
//        Iterator i = es.iterator();
//
//        while( i.hasNext() ) {
//
//            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();
//
//            CtTool t = entry.getValue();
//
//            if( t.isActive() ) {
//                oneActive = true;
//                break;
//            }
//        }
//
//        if( oneActive ) {
//            return;
//        }
//System.out.println( "onchange activating default" );

        activateDefault();
    }

    public void add( CtTool t ) {
        _tools.put( t.name(), t );
        t.changeSupport.addListener( CtTool.EVT_ACTIVE, this ); // tell me when a tool changes status
    }

//    public void setActive( CtTool t, boolean active ) {
//        // exclusive:
//        if( t.isActive() == active ) {
//            return;
//        }
//
//        if( active == false ) {
//            t.setActive( false );
//        }
//        else {
//            if( t.isEnabled() ) {
//                deactivate(); // deactivate any active tool[s]
//                t.setActive( true );
//            }
//        }
//    }


    public void activateDefault() {
        if( _default == null ) {
            return;
        }

        CtTool t = _tools.get( _default );

        if( t != null ) {
            t.setActive( true );
        }
    }

    public void deactivateAll() {

        _defaultEnabled = false;

        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();
            
            t.setActive( false );
        }

        _defaultEnabled = true;
    }

    public Collection< CtTool > getTools() {
        return _tools.values();
    }

    public CtTool getActive() {
        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();

            if( t.isActive() ) {
                return t;
            }
        }

        return null;
    }

    public void setEnabled( boolean enabled ) {
        Set< Entry< String, CtTool > > es = _tools.entrySet();

        Iterator i = es.iterator();

        while( i.hasNext() ) {

            Entry< String, CtTool > entry = (Entry< String, CtTool >)i.next();

            CtTool t = entry.getValue();

            t.setEnabled( enabled );
        }
    }

}
