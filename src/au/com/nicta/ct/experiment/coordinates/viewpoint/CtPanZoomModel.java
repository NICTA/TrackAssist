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

package au.com.nicta.ct.experiment.coordinates.viewpoint;

import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Has a finite set of pan zoom objects, which may each have multiple listeners
 * (observers). Listeners can detach (meaning they get their own PZ objs) or
 * attach (meaning they listen to the default).
 *
 * TODO: clear up old PZ objs when no listeners remain
 * @author davidjr
 */
public class CtPanZoomModel extends CtChangeModel {

    public static final String LISTENER_DEFAULT_KEY = "default";

    HashMap< String, CtPanZoom > _kpz = new HashMap< String, CtPanZoom >();
//    ArrayList< CtPanZoom > _cpz = new ArrayList< CtPanZoom >(); // collection of pan/zoom objs
//    HashMap< String, Integer > _keyPanZoomIndices = new HashMap< String, Integer >(); // for each string, here's the pan-zoom object.
//    HashMap< String, HashSet< CtPanZoomListener > > _keyListeners = new HashMap< String, CtPanZoomListener >(); // for each string, here's the pan-zoom object.
//    HashMap< CtPanZoomListener, String > _listenerKeys = new HashMap< CtPanZoomListener, String >(); // for each string, here's the pan-zoom object.
    HashSet< CtPanZoomListener > _listeners = new HashSet< CtPanZoomListener >();

    private class CtPanZoomChangeListener implements CtChangeListener {
        public CtPanZoomModel _pzm;
//        public int _index = 0;
        public String _key;
        public CtPanZoomChangeListener( CtPanZoomModel pzm, String key, /*int index,*/ CtPanZoom pz ) {
            this._pzm = pzm;
//            this._index = index;
            this._key = key;
            pz.addListener( this );
        }

        @Override public void propertyChange( PropertyChangeEvent evt ) {
            _pzm.onPanZoomChanged( _key, evt );//_index, evt );
        }
    }
    
    public CtPanZoomModel() {
        super( null );

        addKey( LISTENER_DEFAULT_KEY, null );
    }

    public void clear() {
//        _cpz.clear();
//        _keyPanZoomIndices.clear();
        _kpz.clear();
//        _listenerKeys.clear();
        _listeners.clear();
        
        addKey( LISTENER_DEFAULT_KEY, null );
    }

    public void addKey( String newKey, String oldKey ) {
        CtPanZoom pz = null;

//        if( oldKey != null ) {
        try {
            CtPanZoom pz0 = getPanZoom( oldKey );
            pz = (CtPanZoom)pz0.clone();
        }
        catch( Exception e ) {
//        else {
            pz = new CtPanZoom();

        }

        _kpz.put( newKey, pz );
//        _cpz.add( pz );
//        int index = _cpz.size() -1;
//
//        _keyPanZoomIndices.put( newKey, index );

        CtPanZoomChangeListener pzcl = new CtPanZoomChangeListener( this, newKey, pz );//index, pz ); // registers for events
    }

    public void onPanZoomChanged( String key, /*int index,*/ PropertyChangeEvent evt ) {
        // find the listeners to this PZ object; then call the relevant method based on type of event
//        if( ( index < 0 ) || ( index >= _cpz.size() ) ) {
        if( key == null ) {
            System.out.println("WARNING: Received a strange pan/zoom event about a non-existent pan/zoom object." );
            return;
        }

        CtPanZoom pz = _kpz.get( key );//_cpz.get( index );

        // there can be many keys? for one PZ?
        for( CtPanZoomListener pzl : _listeners ) {

            String pzk = pzl.getPanZoomKey();

            if( pzk.equals( key ) ) {
                pzl.onPanZoomChanged( evt );
            }
        }
    }

    public CtPanZoom getPanZoom( CtPanZoomListener pzl ) {
//        String key = //_listenerKeys.get( pzl );
        return getPanZoom( pzl.getPanZoomKey() );
    }

    public CtPanZoom getPanZoom( String key ) {
        return _kpz.get( key );
//        Integer n = _keyPanZoomIndices.get( key );
//
//        if( n == null ) {
//            return null;
//        }
//
//        return _cpz.get( n );
    }

    public String detachListener( CtPanZoomListener pzl ) {
//        String oldKey = _listenerKeys.get( pzl );//pzl._key;
        String oldKey = pzl.getPanZoomKey();
        String newKey = String.valueOf( Math.random() );

        addKey( newKey, oldKey ); // copies the PZ settings

        setListenerKey( pzl, newKey );//, oldKey );

        return newKey;
    }

    public String attachListener( CtPanZoomListener pzl ) {
        String key = LISTENER_DEFAULT_KEY;

        setListenerKey( pzl, key );//, oldKey );

        return key;
    }

    public boolean isAttached( CtPanZoomListener pzl ) {
        String key = pzl.getPanZoomKey();

        if( key.equals( LISTENER_DEFAULT_KEY ) ) {
            return true;
        }

        return false;
    }

    public String addListener( CtPanZoomListener pzl ) {
        String key = LISTENER_DEFAULT_KEY;

        setListenerKey( pzl, key );//, null );

        return key;
    }

    public void removeListener( CtPanZoomListener pzl ) {
        _listeners.remove( pzl );
    }

    public void setListenerKey( CtPanZoomListener pzl, String newKey ) {//, String oldKey ) {

        _listeners.add( pzl ); // if not already

        pzl.setPanZoomKey( newKey );
//        if( oldKey != null ) {
//            _keyListeners.remove( oldKey );
//            _listenerKeys.remove( pzl ); redundant
//        }

//        _keyListeners.put( newKey, pzl );
//        _listenerKeys.put( pzl, newKey );
    }
}
