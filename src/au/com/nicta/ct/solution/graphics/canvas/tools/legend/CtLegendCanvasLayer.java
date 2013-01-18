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

package au.com.nicta.ct.solution.graphics.canvas.tools.legend;

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtSubPixelResolution;
import au.com.nicta.ct.graphics.canvas.zoom.polygons.CtZoomPolygon;
import au.com.nicta.ct.solution.tracking.CtTrackingCanvasLayer;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import au.com.nicta.ct.solution.tracking.CtZoomTrackPainter;
import au.com.nicta.ct.orm.interactive.CtInteractions.CtItemState;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 *
 * @author alan
 */
public class CtLegendCanvasLayer extends CtCanvasLayer {

    public static final String CANVAS_LAYER_NAME = "legend-canvas-layer";
//    CtCanvas c;
//    CtCanvasLayer l = new CtCanvasLayer();
    AffineTransform toScreen = new AffineTransform();
//    public static final double SCALE = 1.0/CtSubPixelResolution.unitsPerNaturalPixel * 2;
    public static final double DEFAULT_ZOOM_FACTOR = 5;
    double zoomFactor;

    public static final String LAYER_NAME = "legend";
    public static final int DETECTION_RADIUS = 40;
    static final int DETECTION_SEPARATION = (int) (DETECTION_RADIUS * 3.0);
    static final int ROW_SEPARATION = (int) (DETECTION_RADIUS * 4);
    static final int BOUNDARY_MARGIN_X = 30;
    static final int BOUNDARY_MARGIN_Y = 30;
    static final int TOOL_TIP_SCREEN_EDGE_MARGIN_X = 15;
    static final int TOOL_TIP_SCREEN_EDGE_MARGIN_Y = 15;
    static final int BOUNDBOX_EDGE_GAP_PIXELS = 5;
    static final Point TOOL_TIP_CURSOR_OFFSET = new Point(15, 25);


    public CtLegendCanvasLayer() {
        super( CANVAS_LAYER_NAME );
//        addPainter( new CtCanvasPainter() {
//            public void paint(Graphics2D g, CtCanvasLayer cl) {
//                CtLegendLayer.this.paint(g, cl);
//            }
//        });
        addMouseMotionListener( new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                CtLegendCanvasLayer.this.mouseMoved(e);
            }
        });

        doSetZoomFactor( DEFAULT_ZOOM_FACTOR );
    }

//    public CtLegendLayer(CtCanvas c) {
//        this();
//        c.addLayer( l, LAYER_NAME);
//    }

    public final void doSetZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;

        double scale = 1.0/CtSubPixelResolution.unitsPerNaturalPixel * zoomFactor;
        toScreen.setToIdentity();
        toScreen.scale(scale, scale);
        addDetections();
        addTracks();
        repaint();
    }

    public void setZoomFactor( double zoomFactor ) {
        doSetZoomFactor(zoomFactor);
    }
    
    @Override public void setEnabled( boolean b ) {
        super.setEnabled( b );
        repaint();
    }

//    public JToggleButton getButton() {
//        final JToggleButton b = new JToggleButton("Legend");
//        b.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                l.setEnabled( b.isSelected() );
//                l.repaint();
//            }
//        });
//        return b;
//    }

    Rectangle boundingBox = new Rectangle();

    @Override public void paint( Graphics2D g ) {

        super.paint( g );
        
        boundingBox.width  = (int)( zoomFactor * 75 );
        boundingBox.height = (int)( zoomFactor * 93 );
        boundingBox.x = getWidth() - boundingBox.width - BOUNDBOX_EDGE_GAP_PIXELS;
        boundingBox.y = BOUNDBOX_EDGE_GAP_PIXELS;

        AffineTransform old = g.getTransform();
        g.translate(boundingBox.x, boundingBox.y);

        paintBackground(g, boundingBox.width, boundingBox.height);
        for(Detection d : detections) {
            d.draw( g, this );
        }
        for(Track t : tracks) {
            t.draw( g, this );
        }

        g.setTransform(old);
    }

    public void paintBackground(Graphics2D g, int width, int height) {
        g.setColor(new Color(0,0,0,128));
        g.fillRect(0, 0, width, height);
    }

    ManualToolTip currentTip = null;

    ManualToolTip getToolTip(MouseEvent e) {
        for(Detection d : detections) {
            ManualToolTip mtt = d.mouseMoved(e);
            if( mtt != null ) {
                return mtt;
            }
        }
        for(Track t : tracks) {
            ManualToolTip mtt = t.mouseMoved(e);
            if( mtt != null ) {
                return mtt;
            }
        }
        
        return null;
    }

    void mouseMoved(MouseEvent e) {

        MouseEvent me = new MouseEvent(
                (Component)e.getSource(),
                e.getID(),
                e.getWhen(),
                e.getModifiers(),
                e.getX() - boundingBox.x,
                e.getY() - boundingBox.y,
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton() );
        
        ManualToolTip mtt = getToolTip(me);
        if( currentTip != mtt ) { // tip has changed, maybe to no tip
            if( currentTip != null ) {
                currentTip.hide();
            }
            currentTip = mtt;
            if( currentTip != null ) {
                currentTip.show(e);
            }
        }
        if( me.isConsumed() ) {
            e.consume();
        }
    }

    class ManualToolTip {
        private PopupFactory popupFactory = PopupFactory.getSharedInstance();
        private Popup popup;
        public String tip = "Unknown";

        public Point cursorOffset = TOOL_TIP_CURSOR_OFFSET;

        public void show(MouseEvent e) {
            if( popup != null ) {
                return;
            }
            JComponent src = (JComponent) e.getSource();
            JToolTip toolTip = src.createToolTip();
            toolTip.setTipText(tip);
            int x = e.getXOnScreen() + cursorOffset.x;
            int y = e.getYOnScreen() + cursorOffset.y;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            popup = popupFactory.getPopup(src, toolTip, x, y);
            popup.show();
            // check to see if it's outside the screen.
            if( x + toolTip.getWidth() + TOOL_TIP_SCREEN_EDGE_MARGIN_X > screenSize.width ) {
                x = screenSize.width - toolTip.getWidth() - TOOL_TIP_SCREEN_EDGE_MARGIN_X;
            }
            if( y + toolTip.getHeight() + TOOL_TIP_SCREEN_EDGE_MARGIN_Y > screenSize.height ) {
                y = screenSize.height - toolTip.getHeight() - TOOL_TIP_SCREEN_EDGE_MARGIN_Y;
            }
            if(    toolTip.getX() != x
                || toolTip.getY() != y ) {
                popup.hide();
                popup = popupFactory.getPopup(src, toolTip, x, y);
                popup.show();
            }
        }

        public void hide() {
            if( popup != null ) {
                popup.hide();
                popup = null;
            }
        }
    }

    class Detection {

        public int centreX;
        public int centreY;
        public int radius;
        public int currentIdx;
        public int detectionIdx;
        public boolean isOrphan;
        public boolean isStart;
        public boolean isEnd;
        public  CtItemState state;

        // programatically created
        public CtZoomPolygon zp;

        public Detection(
                int x,
                int y,
                int radius,
                int currentIdx,
                int detectionIdx,
                boolean isOrphan,
                boolean isStart,
                boolean isEnd,
                CtItemState state ) {
            this.centreX = x;
            this.centreY = y;
            this.radius = radius;
            this.currentIdx = currentIdx;
            this.detectionIdx = detectionIdx;
            this.isOrphan = isOrphan;
            this.isStart = isStart;
            this.isEnd = isEnd;
            this.state = state;

            int[] xs = {-radius, +radius, +radius, -radius};
            int[] ys = {-radius, -radius, +radius, +radius};
            Polygon p = new Polygon(xs, ys, xs.length);
            p.translate(x, y);

            zp = new CtZoomPolygon( p );
        }

        public boolean isInside(double naturalX, double naturalY) {
            return zp.containsNaturalCoord(naturalX, naturalY);
        }

        int cnt = 0;

        public ManualToolTip mtt = new ManualToolTip();

        public ManualToolTip mouseMoved(MouseEvent e) {
            double naturalX = e.getX()/zoomFactor;
            double naturalY = e.getY()/zoomFactor;

            if( isInside(naturalX, naturalY) ) {
                return mtt;
            }
            return null;
        }


        public void draw( Graphics2D g, CtCanvasLayer cl ) {
            CtCanvasLayer cl2 = cl.getParent().getLayer( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );

            if( cl2 == null ) {
                return;
            }

            CtTrackingCanvasLayer tv = (CtTrackingCanvasLayer)cl2;//tc.getTrackingView();

            CtZoomTrackPainter ztp = tv.getZoomTrackPainter();//CtTrackingController.get().getTrackingView().getZoomTrackPainter();
            ztp.paintDetectionCircle(g, toScreen, cl, zp, state, currentIdx, detectionIdx, isOrphan, isStart, isEnd );
        }

    }

    List<Detection> detections = new ArrayList<Detection>();

    public final void addDetections() {
        detections.clear();

        int centreX, centreY, radius, currentIdx, detectionIdx;
        boolean isOrphan, isStart, isEnd;
        CtItemState state;
        Detection d;

        centreY = BOUNDARY_MARGIN_Y + DETECTION_RADIUS;
        radius = DETECTION_RADIUS;
        currentIdx = 0;
        detectionIdx = 0;
        state = CtItemState.NORMAL;

        centreX = BOUNDARY_MARGIN_X + DETECTION_RADIUS;
        isOrphan = false;
        isStart = false;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Detection in track at current time";
        detections.add(d);

        centreX += DETECTION_SEPARATION;
        isOrphan = false;
        isStart = false;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx+1, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Past/future detection in track";
        detections.add(d);

        centreX += DETECTION_SEPARATION;
        isOrphan = true;
        isStart = false;
        isEnd = false;
        state = CtItemState.NORMAL;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Orphan (untracked) detection";
        detections.add(d);
    }

    class Track {
        List<Detection> detections = new ArrayList<Detection>();
        int currentIdx;
        CtItemState state;

        public Track( int currentIdx, CtItemState state ) {
            this.currentIdx = currentIdx;
            this.state = state;
        }

        public void draw(Graphics2D g, CtCanvasLayer cl) {
            CtCanvasLayer cl2 = cl.getParent().getLayer( CtTrackingCanvasLayer.CANVAS_LAYER_NAME );

            if( cl2 == null ) {
                return;
            }

            CtTrackingCanvasLayer tv = (CtTrackingCanvasLayer)cl2;//tc.getTrackingView();

            CtZoomTrackPainter ztp = tv.getZoomTrackPainter();//CtTrackingController.get().getTrackingView().getZoomTrackPainter();

//            CtZoomTrackPainter ztp = CtTrackingController.get().getTrackingView().getZoomTrackPainter();
            for(int i = 0; i < detections.size()-1; ++i) {
                Detection d1 = detections.get(i);
                Detection d2 = detections.get(i+1);
                ztp.paintTrackTuple(
                        g,
                        toScreen,
                        cl,
                        currentIdx,
                        state,
                        null,
                        0,
                        d1.zp,
                        d1.detectionIdx,
                        d2.zp,
                        d2.detectionIdx,
                        d1.isStart,
                        d1.isEnd );
            }

            Detection d1 = detections.get(detections.size()-1);
            ztp.paintTrackTuple(
                    g,
                    toScreen,
                    cl,
                    currentIdx,
                    state,
                    null,
                    0,
                    d1.zp,
                    d1.detectionIdx,
                    null,
                    0,
                    d1.isStart,
                    d1.isEnd );
        }

        public ManualToolTip mtt = new ManualToolTip();

        public ManualToolTip mouseMoved(MouseEvent e) {
            // see if the detections are handling this event
            for( Detection d : detections ) {
                ManualToolTip t = d.mouseMoved(e);
                if( t != null ) {
                    return t;
                }
            }

            return null;

//            double naturalX = e.getX()/ZOOM_FACTOR;
//            double naturalY = e.getY()/ZOOM_FACTOR;
//
//            if( isInside(naturalX, naturalY) ) {
//                return mtt;
//            }
//            return null;
        }

    }


    List<Track> tracks = new ArrayList<Track>();

    public final void addTracks() {
        tracks.clear();
        
        int centreX, centreY, radius, currentIdx, detectionIdx;
        boolean isOrphan, isStart, isEnd;
        CtItemState state;
        Detection d;
        Track t;

        // Track 1 ------------------------------------------------------------
        t = new Track(1, CtItemState.NORMAL);

        centreY = BOUNDARY_MARGIN_Y + ROW_SEPARATION;
        radius = DETECTION_RADIUS;
        currentIdx = 0;
        state = CtItemState.NORMAL;

        centreX = BOUNDARY_MARGIN_X + DETECTION_RADIUS;
        detectionIdx = 0;
        isOrphan = false;
        isStart = true;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "First detection in track";
        t.detections.add(d);

        centreX += DETECTION_SEPARATION;
//        centreY += 30;
        detectionIdx = 1;
        isOrphan = false;
        isStart = false;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Detection in track at current time";
        t.detections.add(d);

        centreX += DETECTION_SEPARATION;
//        centreY -= 30;
        detectionIdx = 2;
        isOrphan = false;
        isStart = false;
        isEnd = true;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Last detection in track";
        t.detections.add(d);
        
        tracks.add(t);

        centreY = BOUNDARY_MARGIN_Y + (int) (ROW_SEPARATION * 2);

        // Track 2.1 ------------------------------------------------------------
        t = new Track(1, CtItemState.NORMAL);
        centreX = BOUNDARY_MARGIN_X + DETECTION_RADIUS;
        detectionIdx = 0;
        isOrphan = false;
        isStart = true;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "First detection in track";
        t.detections.add(d);

        centreX += DETECTION_SEPARATION;
        detectionIdx = 1;
        isOrphan = false;
        isStart = false;
        isEnd = true;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Track split";
        t.detections.add(d);

        tracks.add(t);

        // Track 2.2 ------------------------------------------------------------
        t = new Track(1, CtItemState.NORMAL);

        centreY = BOUNDARY_MARGIN_Y + (int) (ROW_SEPARATION * 2);

        detectionIdx = 1;
        isOrphan = false;
        isStart = true;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
//        d.mtt.tip = "Track split";
        d.mtt = null; // disable tooltip it
        t.detections.add(d);

        detectionIdx = 2;
        isOrphan = false;
        isStart = false;
        isEnd = true;
        d = new Detection( centreX+DETECTION_SEPARATION, centreY-50, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Last detection in track";
        t.detections.add(d);

        tracks.add(t);

        // Track 2.3 ------------------------------------------------------------
        t = new Track(1, CtItemState.NORMAL);

        detectionIdx = 1;
        isOrphan = false;
        isStart = true;
        isEnd = false;
        d = new Detection( centreX, centreY, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Track split";
        d.mtt = null; // disable tooltip it
        t.detections.add(d);

        detectionIdx = 2;
        isOrphan = false;
        isStart = false;
        isEnd = true;
        d = new Detection( centreX+DETECTION_SEPARATION, centreY+50, radius, currentIdx, detectionIdx, isOrphan, isStart, isEnd, state );
        d.mtt.tip = "Last detection in track";
        t.detections.add(d);

        tracks.add(t);
    }
    
}
