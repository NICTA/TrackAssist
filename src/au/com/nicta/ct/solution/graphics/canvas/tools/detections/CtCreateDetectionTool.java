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

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.brushes.CtCircleBrush;
import au.com.nicta.ct.graphics.canvas.tools.CtBrushTool;
import au.com.nicta.ct.graphics.canvas.tools.CtToolsModel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointModel;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.tracking.CtDetectionsCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtCreateDetectionTool extends CtBrushTool {

    public CtCreateDetectionTool( CtToolsModel tm ) {//, CtDetectionsController dc ) {//CtZoomCanvas zc,  ) {//, final CtEditMode editMode) {
        super( tm, "create-detection", "create", new CtCircleBrush() );

        applyOnMouseClicked( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
    }

    @Override public Collection< String > getCanvasLayerNames() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( CtDetectionsCanvasLayer.CANVAS_LAYER_NAME );
        return al;
    }

    protected String iconFile() {
        return new String( "detection_create.png" );
    }

    protected String toolTip() {
        return new String( "Create detection" );
    }

    @Override public boolean doApply( MouseEvent e, CtCanvasLayer cl ) {
        CtTrackingController tc = CtTrackingController.get();

        if( tc == null ) {
            return false;
        }

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)cl.getParent();

        int x = CtSubPixelResolution.getSubPixelNaturalX( zc, e.getX() );//(int)Math.rint( _bl._zc.toNaturalX( e.getX() ) * CtSubPixelResolution.unitsPerNaturalPixel );
        int y = CtSubPixelResolution.getSubPixelNaturalY( zc, e.getY() );//(int)Math.rint( _bl._zc.toNaturalY( e.getY() ) * CtSubPixelResolution.unitsPerNaturalPixel );

//        CtDetections d = new CtDetections();
        CtZoomPolygon zp = new CtZoomPolygon( _b._p, 1.0 );

        zp.translate( x, y );

//        String s = zp.serialize();
//        d.setBoundary( s );
        CtViewpointController vc = zc.getViewpointController();
        CtViewpointModel vm = vc.getViewpointModel();
        CtImages i = vm.getImage();

//        dc.addDetection( d );
        tc.createDetection( zp, i );

        return true;
    }

}
