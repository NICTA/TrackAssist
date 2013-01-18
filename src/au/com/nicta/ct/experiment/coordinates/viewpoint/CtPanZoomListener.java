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
import au.com.nicta.ct.orm.mvc.change.concrete.CtDiscreteScalingModel;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author davidjr
 */
public abstract class CtPanZoomListener extends CtChangeModel implements CtChangeListener {

    public abstract void onPanChanged();
    public abstract void onZoomChanged();

    String _key;

    public CtPanZoomListener() {
        super( null );
        CtPanZoomController pzc = CtPanZoomController.get();
        pzc.addListener( this );
//        _key = pzc.addListener( this );
    }

    public String getPanZoomKey() {
        return _key;
    }

    public void setPanZoomKey( String key ) {
        _key = key;
    }

    public CtPanZoom getPanZoom() {
        CtPanZoomController pzc = CtPanZoomController.get();
        return pzc.getPanZoom( this );
    }

    public boolean isAttached() {
        CtPanZoomController pzc = CtPanZoomController.get();
        return pzc.isAttached( this );
    }

    public void detach() {
        CtPanZoomController pzc = CtPanZoomController.get();
        pzc.detachListener( this );
//        _key = pzc.detachListener( this );
    }

    public void attach() {
        CtPanZoomController pzc = CtPanZoomController.get();
        pzc.attachListener( this );
//        _key = pzc.attachListener( this );
    }

    public void onPanZoomChanged( PropertyChangeEvent evt ) {
        String s = evt.getPropertyName();

        if( s.equals( CtDiscreteScalingModel.EVT_LEVEL_CHANGED ) ) {
            onZoomChanged();
        }
        else if( s.equals( CtDiscreteScalingModel.EVT_FACTOR_CHANGED ) ) {
            onZoomChanged();
        }
        else if( s.equals( CtPanZoom.EVT_OFFSET_CHANGED ) ) {
            onPanChanged();
        }
    }
}
