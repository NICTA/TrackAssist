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

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.experiment.CtExperimentController;
import au.com.nicta.ct.experiment.CtExperimentListener;

/**
 * Relays messages between pan-zoom objects via central hub.
 * All pan-zoom objects are stored/managed here.
 *
 * One persistent singleton object stored in OD. Created on demand.
 * 
 * @author davidjr
 */
public class CtPanZoomController implements CtExperimentListener {

    CtPanZoomModel _pzm;
//    public static CtCoordinatesModel getModel() {
//        CtPanZoomController cc = get();
//        if( cc != null ) {
//            return cc.getModel();
//        }
//        return null;
//    }
//
    public static CtPanZoomController get() {
        CtPanZoomController tc = (CtPanZoomController)CtObjectDirectory.get( CtPanZoomController.name() );
        if( tc == null ) {
            tc = new CtPanZoomController();
        }
        return tc;
    }

    public static String name() {
        return "pan-zoom-controller";
    }

    protected CtPanZoomController() {
        this( new CtPanZoomModel() );
    }
    
    protected CtPanZoomController( CtPanZoomModel pzm ) {

        _pzm = pzm;
        CtObjectDirectory.put( CtPanZoomController.name(), this );

        CtExperimentController ec = CtExperimentController.get();
        ec.addExperimentListener( this );
    }

//    public String addListener( CtPanZoomListener pzl ) {
//        return _pzm.addListener( pzl );
    public void addListener( CtPanZoomListener pzl ) {
        _pzm.addListener( pzl );
    }

    public void removeListener( CtPanZoomListener pzl ) {
        _pzm.removeListener( pzl );
    }
//    public String detachListener( CtPanZoomListener pzl ) {
//        return _pzm.detachListener( pzl );
    public void detachListener( CtPanZoomListener pzl ) {
        _pzm.detachListener( pzl );
    }

//    public String attachListener( CtPanZoomListener pzl ) {
//        return _pzm.attachListener( pzl );
    public void attachListener( CtPanZoomListener pzl ) {
        _pzm.attachListener( pzl );
    }

    public boolean isAttached( CtPanZoomListener pzl ) {
        return _pzm.isAttached( pzl );
    }

    public CtPanZoom getPanZoom( CtPanZoomListener pzl ) {
        return _pzm.getPanZoom( pzl );
    }

    @Override public void onExperimentChanged( CtExperiments e ) {
        _pzm.clear();
    }


}
