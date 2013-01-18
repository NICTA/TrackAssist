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

package au.com.nicta.ct.solution.graphics.canvas.tools.detections;

import au.com.nicta.ct.db.hibernate.CtDetections;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.brushes.CtBrushSet;
import au.com.nicta.ct.graphics.canvas.brushes.CtCircleBrush;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtTrackingModel;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtQuantityConstraint;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtSubDetectionTool extends CtDetectionBrushTool {

    public CtSubDetectionTool( CtToolsModel tm ){//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "sub-detection", "sub", new CtCircleBrush() );//, dc );

        // enable when ONE detection is SELECTED
        _qisc._states.add( CtItemState.SELECTED );
        _qisc._quantity = CtQuantityConstraint.ONE;

        applyOnMouseClicked( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
    }

    protected String iconFile() {
        return new String( "detection_sub.png" );
    }

    protected String toolTip() {
        return new String( "Subtract from detection" );
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
        CtTrackingController tc = CtTrackingController.get();//dc;

        if( tc == null ) {
            return false;
        }

        CtTrackingModel tm = tc.getTrackingModel();

        if( tm == null ) {
            return false;
        }

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        int x = CtSubPixelResolution.getSubPixelNaturalX( zc, e.getX() );//(int)Math.rint( _bl._zc.toNaturalX( e.getX() ) * CtSubPixelResolution.unitsPerNaturalPixel );
        int y = CtSubPixelResolution.getSubPixelNaturalY( zc, e.getY() );//(int)Math.rint( _bl._zc.toNaturalY( e.getY() ) * CtSubPixelResolution.unitsPerNaturalPixel );

        Collection< CtDetections > matching = tm.getDetectionsWithStatesInWindow( _qisc._states );

        if( matching.isEmpty() ) {
            return false;
        }

        CtDetections d = matching.iterator().next();

        CtZoomPolygon zp = tm.getBoundary( d );

        CtZoomPolygon zp2 = new CtZoomPolygon( _b._p );
        zp2.translate( x, y );
        zp.sub( zp2 );

        tc.setBoundary( d, zp );
//        _cl.repaint();
        return true;
    }
}
