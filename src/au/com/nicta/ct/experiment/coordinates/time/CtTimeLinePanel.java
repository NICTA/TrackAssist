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

package au.com.nicta.ct.experiment.coordinates.time;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.orm.mvc.images.CtImageSelectionModel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowController;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowModel;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtDirectorySingleton;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.ui.style.CtStyle;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationsBalloon;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationsController;
import au.com.nicta.ct.solution.graphics.canvas.tools.annotations.CtAnnotationsModel;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.orm.mvc.images.CtImageSelectionController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtTimeLinePanel extends JPanel implements MouseMotionListener, MouseListener, /*CtImageSequenceListener,*/ ActionListener, CtChangeListener {

    public static final int MIN_HEIGHT_PX = 100;
    public static final int TICK_SPACING_PX = 10;
    public static final double ZOOM_SCALING = 2.0;

    protected static final int WINDOW_CONTROL_NONE = 0;
    protected static final int WINDOW_CONTROL_HISTORY = -1;
    protected static final int WINDOW_CONTROL_FUTURE = 1;

    protected boolean _dragging = false;
    protected int _frameIndex0 = 0;
    protected int _history0 = 0;
    protected int _future0 = 0;
    protected int _xMousePressed = 0;
    protected int _yMousePressed = 0;
    protected int _windowControl = WINDOW_CONTROL_NONE;

//    protected CtTimeWindowController _twc;
//    protected CtImageSelectionController _isc;
//    protected CtAnnotationsController _ac;
//    protected CtImageSelectionModel _is;
//    public int    _frameIndex = 0;
//    public int _minFrameIndex = 0;
//    public int _maxFrameIndex = 0;
    public double _zoom = 1.0;
    public Color _invalid = new Color( 200,200,200,100 );

    // caching annotation balloons
    TreeMap<Integer, CtAnnotationsBalloon> annotBalloons = new TreeMap<Integer, CtAnnotationsBalloon>();

    CtDockableWindowGrid _dwg;
    Collection< String > _windowTypes;
//    CtZoomCanvasPanelFactory _zcpf;

    public CtTimeLinePanel( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {

        super();

        _dwg = dwg;
        _windowTypes = windowTypes;
        
        Dimension minSize = getMinimumSize();
        Dimension prefSize = getMinimumSize();
        
        minSize.height = MIN_HEIGHT_PX;
        prefSize.height = minSize.height;

        setMinimumSize( minSize );
        setPreferredSize( prefSize );

        addMouseListener( this );
        addMouseMotionListener( this );

        setOpaque( false );

        CtObjectDirectory.put( name(), this );

        CtAnnotationsController ac = CtAnnotationsController.get();
        ac.getAnnotationsModel().addListener( this );
//        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
//
//        twc.addModelListener( this );
    }

    public static final String name() {
        return "time-line-panel";
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        // this is called when annotations model has changed..
        onAnnotationsModelChanged();
    }

    public void onAnnotationsModelChanged() {
        repaint();
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        String s = ae.getActionCommand();

        if( s.equals( CtModel.ACTION_MODEL_CHANGED ) ) {
            repaint();
        }
    }
//    public void setFrameIndex( int frameIndex ) {
//        if( frameIndex < _minFrameIndex ) {
//            return;
//        }
//        if( frameIndex > _maxFrameIndex ) {
//            return;
//        }
//
//        _frameIndex = frameIndex;
//    }
//
//    public void setFrameIndexRange( int frameIndex, int minFrameIndex, int maxFrameIndex ) {
//           _frameIndex =    frameIndex;
//        _minFrameIndex = minFrameIndex;
//        _maxFrameIndex = maxFrameIndex;
//    }

    protected static final int MIN_TICK_WIDTH = 2;
    
    protected int wTick() {
        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
        int resolvedTickSpacing = (int)zoomedTickSpacing;
        if( resolvedTickSpacing < MIN_TICK_WIDTH ) resolvedTickSpacing = MIN_TICK_WIDTH;
        return resolvedTickSpacing;
    }

    public void increaseZoom() {
        _zoom *= ZOOM_SCALING;
        repaint();
    }

    public void decreaseZoom() {
        if( wTick() <= MIN_TICK_WIDTH ) {
            return;
        }

        _zoom /= ZOOM_SCALING;
        repaint();
    }

    public void setZoom( double zoom ) {
        _zoom = 1.0;
    }

//    public void onControllerChanged( CtImageSequenceController isc ) {
//        setImageSequenceController( (CtImageSelectionController)isc );
//    }
    
//    public void setImageSequenceController( CtImageSelectionController isc ) {
//        _isc = isc;
//        repaint();
//    }
//
//    public void setTimeWindowController( CtTimeWindowController twc ) {
//        _twc = twc;
//        repaint();
//    }

//    public void setAnnotationsController( CtAnnotationsController ac ) {
//        _ac = ac;
////        _ac.addModelListener( this );
//        repaint();
//    }

//    public void setImageSequence( CtImageSelectionModel is ) {
//        _is = is;
//        repaint();
//    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse dragged"+ e);
        if( !_dragging ) {
            return;
        }

        // compute the positions etc of the tickmarks:
        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
        int wTick = (int)zoomedTickSpacing;
        int xMouse = e.getX();
        int xDelta = xMouse - _xMousePressed;

        if( wTick == 0 ) {
            return;
        }

        int xDeltaFrames = xDelta / wTick;

//        if( xDeltaFrames == 0 ) {
//            return;
//        }

        if( _windowControl != WINDOW_CONTROL_NONE ) {
            CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );

            if( twc == null ) {
                return; // error?
            }

            if( _windowControl == WINDOW_CONTROL_HISTORY ) {
                twc.setHistory( _history0 + xDeltaFrames );
            }
            else {
                twc.setFuture( _future0 + xDeltaFrames );
            }

            return;
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceController isc = cc.getImageSequenceController();

        if( isc == null ) {
            return;
        }
//this is still broken .. this one is an actionlistener of two controllers
//        CtImageSelectionModel ism = (CtImageSelectionModel)isc.getModel();//(CtImageSelectionModel)_isc.getModel();

//        int frameIndex1 = ism.selectedIndex();
//        int minFrameIndex = 0;
//        int maxFrameIndex = ism.size();
//
//        Dimension size = getSize();

        int frameIndex = _frameIndex0 - xDeltaFrames;
   //     System.out.println( "frameIndex="+frameIndex );
        
        isc.setCurrentIndex( frameIndex );
    }

    protected void showPopupMenu( Component parent, int x, int y ) {
        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceController isc = cc.getImageSequenceController();

        if( isc == null ) {
            return;
        }
        
        CtTimePopupMenu tpm = new CtTimePopupMenu( (CtImageSelectionController)isc );
        tpm.show( parent, x, y );
    }

    protected boolean selectionEvent( MouseEvent e ) {
        if( e.getButton() != MouseEvent.BUTTON1 ) {
            return true;
        }
        return false;
    }

    public void mousePressed(MouseEvent e) {
        _dragging = false;

        if( selectionEvent( e ) ) {
            return;
        }
//        if( e.isPopupTrigger() ) { // other OS
//            if( e.)
//            showPopupMenu( e.getComponent(), e.getX(), e.getY() );
//            return;
//        }
        
        _dragging = true;
        _xMousePressed = e.getX();
        _yMousePressed = e.getY();

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceController isc = cc.getImageSequenceController();

        if( isc != null ) {
            CtImageSelectionModel ism = (CtImageSelectionModel)isc.getModel();

            _frameIndex0 = ism.getIndex();
        }

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
        
        if( twc != null ) {
            _history0 = twc.getHistory();
            _future0 = twc.getFuture();
            _windowControl = windowControl( _xMousePressed, _yMousePressed );
        }
    }

    public void mouseReleased(MouseEvent e) {
//        if( e.isPopupTrigger() ) {
//            _dragging = false;
//
//            showPopupMenu( e.getComponent(), e.getX(), e.getY() );
//            return;
//        }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {
//        System.out.println("Mouse clicked"+ e);

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSelectionController isc = (CtImageSelectionController)cc.getImageSequenceController();

        if( isc == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)isc.getModel();

//        if( e.isPopupTrigger() ) {
//        System.out.println("Mouse clicked popup T"+ e);
//            return;
//        }

        int    frameIndex = ism.getIndex();
        int minFrameIndex = ism.getMinIndex();
        int maxFrameIndex = ism.getMaxIndex();
//        int maxFrameIndex = ism.size() -1;

        Dimension size = getSize();

        // compute the positions etc of the tickmarks:
//        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
//        int wTick = (int)zoomedTickSpacing;
        int wTick = wTick();
        double visibleTicks = (double)size.width / (double)wTick;//zoomedTickSpacing;
        int radiusTicks = (int)( visibleTicks * 0.5 );

        int xNow = (int)( (double)size.width * 0.5 );
        int xTick0 = xNow - (radiusTicks * wTick);
//        int ticks = (int)visibleTicks;

        if( wTick == 0 ) {
            return;
        }

        int xClick = e.getX();

        // see if clicked on an annotation balloon
        // iterate in reverse because the balloons are painted from left to right
        // so the last balloons cover the first ones.
        Point mousePosition = e.getPoint();

        List<CtAnnotationsBalloon> l = new ArrayList<CtAnnotationsBalloon>( annotBalloons.values() );
        ListIterator<CtAnnotationsBalloon> li = l.listIterator(l.size());
        while(li.hasPrevious()) {
            CtAnnotationsBalloon ab = li.previous();
            if( ab != null && ab.isInside(mousePosition) ) {
                xClick = ab.origin.x; // over ride so that the time lines moves to the selected balloon
// TODO: Fix this somehow
                CtAnnotationsController ac = CtAnnotationsController.get();
                CtAnnotationsModel am = ac.getAnnotationsModel();
                Rectangle2D boundingBox = am.getBoundingBox( ab.getAnnotations() );

                Collection< CtZoomCanvasPanel > czcp = CtViewpointZoomCanvasPanelFactory.find( _dwg, _windowTypes );

                for( CtZoomCanvasPanel zcp : czcp ) {
                    CtZoomCanvas zc = zcp.getZoomCanvas();
                    zc.zoomNaturalWindowAround( boundingBox, 0.5 );
                }
//                CtAnnotationsController.get().centreAnnonations( ab.getAnnotations() ); moved to a canvas layer
                break;
            }
        }

        int radiusTick = wTick >> 1;
        int xClickTick = ( xClick - xTick0 + radiusTick ) / wTick;

        int frameIndex0 = frameIndex - radiusTicks;
        int deltaIndex = frameIndex0 + xClickTick;
        int frameIndexClicked = /*frameIndex +*/ deltaIndex;

        if( frameIndexClicked < minFrameIndex ) {
            frameIndexClicked = minFrameIndex;
        }
        if( frameIndexClicked > maxFrameIndex ) {
            frameIndexClicked = maxFrameIndex;
        }

        if( selectionEvent( e ) ) {
            if( e.getClickCount() > 1 ) {
                isc.clearSelectedRange();
            }
            else {
                isc.setSelectedRange( frameIndexClicked );
            }
        }
        else {
            isc.setCurrentIndex( frameIndexClicked );
        }
    }

    protected int timeLineHeight() {
        Dimension size = getSize();
        return timeLineHeight( size.height );
    }

    protected int timeLineHeight( int totalHeight ) {
        int yLine  = (int)( (double)totalHeight * 0.34 );
        return yLine;
    }

    protected int tickerHeight( int totalHeight ) {
        int yLine = timeLineHeight( totalHeight );
        return yLine * 2;
    }

    protected int tickerHeight() {
        Dimension size = getSize();
        return tickerHeight( size.height );
    }

    @Override public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();

        int xNow   = (int)( (double)size.width * 0.5 );
        int yLine  = timeLineHeight();//(int)( (double)size.height * 0.34 );
        int yLine2 = tickerHeight();

//        g2d.setBackground( new Color( 0,0,0,255 ) );
//        g2d.setBackground( Color.WHITE );
//        g2d.clearRect( 0, 0, size.width, yLine2 );//size.height );
        g2d.setColor( Color.WHITE );
        g2d.fillRect( 0, 0, size.width, yLine2 );//size.height );
        paintSelection( g2d, xNow, yLine, 0, 0, size.width, yLine2 );
        g2d.setStroke( new BasicStroke( 3 ) );
        g2d.setColor( Color.LIGHT_GRAY );
        g2d.drawLine( 0, yLine, size.width, yLine );
        g2d.drawLine( xNow, 0, xNow, yLine2 );
        g2d.setStroke( new BasicStroke( 1 ) );
        g2d.drawLine( 0, 0, size.width, 0 );
        g2d.drawLine( 0, yLine2, size.width, yLine2 );
        g2d.setColor( Color.BLACK );
        paintTicks( g2d, xNow, yLine, 0, 0, size.width, yLine2, size.height-1 );
        g2d.setColor( Color.LIGHT_GRAY );
        g2d.drawLine( 0, size.height-1, size.width, size.height-1 );
        paintWindow( g2d, xNow, yLine, 0,0, size.width, yLine2 );
    }

    protected int windowControl( int x, int y ) {

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );

        if( twc == null ) {
            return WINDOW_CONTROL_NONE;
        }
        
        CtTimeWindowModel twm = (CtTimeWindowModel)twc.getModel();

        int y1 = tickerHeight();
        int radius   = (int)( (double)y1 * 0.2 );
        int diameter = radius * 2;

        int yc = (int)( (double)y1 * 0.5  );
        int yh = (int)( (double)y1 * 0.25 ) - radius;
        int yf = (int)( (double)y1 * 0.75 ) - radius;

        int yh2 = yh + diameter;
        int yf2 = yf + diameter;
        
        Dimension size = getSize();

        int xc = (int)( (double)( size.width - 0 ) * 0.5 );
        int xh = xc - radius;
        int xf = xh;

//        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
//        int resolvedTickSpacing = (int)zoomedTickSpacing;
//        if( resolvedTickSpacing < 2 ) resolvedTickSpacing = 2;
//        int wTick = resolvedTickSpacing;//(int)zoomedTickSpacing;
        int wTick = wTick();
        
        xh = xh + (twm._history * wTick);
        xf = xf + (twm._future  * wTick);

        int xh2 = xh + diameter;
        int xf2 = xf + diameter;

        if(    ( x >= xh  )
            && ( x <= xh2 )
            && ( y >= yh  )
            && ( y <= yh2 ) ) {
            return WINDOW_CONTROL_HISTORY;
        }

        if(    ( x >= xf  )
            && ( x <= xf2 )
            && ( y >= yf  )
            && ( y <= yf2 ) ) {
            return WINDOW_CONTROL_FUTURE;
        }

        return WINDOW_CONTROL_NONE;
    }

    protected void paintSelection( Graphics2D g2d, int x, int y, int x0, int y0, int x1, int y1 ) {

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSelectionController isc = (CtImageSelectionController)cc.getImageSequenceController();

        if( isc == null ) {
            return;
        }

        CtImageSelectionModel ism = (CtImageSelectionModel)isc.getModel();

        if( !ism.hasSelection() ) {
            return;
        }

        int frameIndex  = ism.getIndex();
        int frameIndex1 = ism.selectedIndex1();
        int frameIndex2 = ism.selectedIndex2();

//        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
//        int wTick = (int)zoomedTickSpacing;
        int wTick = wTick();
        int rTick = wTick >> 1;
        int xs1 = x + ( ( frameIndex1 - frameIndex ) * wTick ) - rTick;
        int xs2 = x + ( ( frameIndex2 - frameIndex ) * wTick ) + rTick;

//        Color c = new Color( CtConstants.NictaHighlight.getRed(),
//                             CtConstants.NictaHighlight.getGreen(),
//                             CtConstants.NictaHighlight.getBlue(), 127 );
//        Color c = new Color( CtConstants.NictaYellow.getRed(),
//                             CtConstants.NictaYellow.getGreen(),
//                             CtConstants.NictaYellow.getBlue(), 127 );
//        g2d.setColor( c );
        g2d.setColor( CtConstants.NictaYellow );
        g2d.fillRect( xs1,y0, xs2-xs1,y1-y0 );
    }

    protected void paintWindow( Graphics2D g2d, int x, int y, int x0, int y0, int x1, int y1 ) {

        CtTimeWindowController twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );

        if( twc == null ) {
            return;
        }

        CtTimeWindowModel twm = (CtTimeWindowModel)twc.getModel();

        int radius   = (int)( (double)y1 * 0.2 );
        int diameter = radius * 2;

        int yc = (int)( (double)y1 * 0.5  );
        int yh = (int)( (double)y1 * 0.25 ) - radius;
        int yf = (int)( (double)y1 * 0.75 ) - radius;

        int xc = (int)( (double)( x1 - x0 ) * 0.5 );
        int xh = xc - radius;
        int xf = xh;

        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
        int wTick = (int)zoomedTickSpacing;

        xh = xh + (twm._history * wTick);
        xf = xf + (twm._future  * wTick);
        
//        Color c = new Color( CtConstants.NictaGreen.getRed(),
//                             CtConstants.NictaGreen.getGreen(),
//                             CtConstants.NictaGreen.getBlue(), 127 );
//        g2d.setColor( c );
        g2d.setColor( CtStyle.BRIGHT_GREEN_TRANSLUCENT );
//        g.setPaint(CtStyle.BRIGHT_GREEN);
//        g.setStroke(CtStyle.THICK_STROKE );


        int w = xf - xh;
        int h = yf - yh;

        if( w > 0 ) {
            g2d.fillRect( xh+radius,yh+radius, w,h );
        }

//        g2d.setColor( CtConstants.NictaGreen );
        g2d.fillOval(xh, yh, diameter, diameter);
//        g2d.setColor( Color.DARK_GRAY );
//        g2d.drawOval(xh, yh, diameter, diameter);
//        g2d.setColor( CtConstants.NictaPurple );
        g2d.fillOval(xf, yf, diameter, diameter);
//        g2d.setColor( Color.DARK_GRAY );
//        g2d.drawOval(xf, yf, diameter, diameter);

//        g2d.setColor( Color.DARK_GRAY );
        g2d.setStroke( CtStyle.THICK_STROKE );
        g2d.setColor( CtStyle.BRIGHT_GREEN );
        g2d.drawLine( xf+radius,yf+radius, xc,yf+radius );
        g2d.drawLine( xf+radius,yf+radius, xf+radius,yc );
        
        g2d.drawLine( xh+radius,yh+radius, xc,yh+radius );
        g2d.drawLine( xh+radius,yh+radius, xh+radius,yc );

    }
    
    protected void paintTicks( Graphics2D g2d, int x, int y, int x0, int y0, int x1, int y1, int y2 ) {

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSelectionController isc = (CtImageSelectionController)cc.getImageSequenceController();

        if( isc == null ) {
            return;
        }
        
        CtImageSelectionModel ism = (CtImageSelectionModel)isc.getModel();
        // configuration:
//        int absolutePosition = 342; // frames
//        int tickSpacing = 10; // px
//        double zoom = 2.7; // x
        Dimension size = getSize(); // px

        // tick parameters:
//        int intervalSml = 1;
        int intervalMed = 5;
        int intervalBig = 10;

        int heightSml = (int)( (double)y * 0.2 );
        int heightMed = (int)( (double)y * 0.4 );
        int heightBig = (int)( (double)y * 0.5 );

        int    frameIndex = ism.getIndex();
        int minFrameIndex = ism.getMinIndex();//0;
        int maxFrameIndex = ism.getMaxIndex();//ism.size();
        
        // compute the positions etc of the tickmarks:
//        double zoomedTickSpacing = TICK_SPACING_PX * _zoom;
//        int resolvedTickSpacing = (int)zoomedTickSpacing;
//        if( resolvedTickSpacing < 2 ) resolvedTickSpacing = 2;
        int wTick = wTick();

        double visibleTicks = (double)size.width / (double)wTick;//resolvedTickSpacing;//zoomedTickSpacing;
        int radiusTicks = (int)( visibleTicks * 0.5 );

//        int wTick = resolvedTickSpacing;//(int)zoomedTickSpacing;
        int xTick0 = x - (radiusTicks * wTick);
        int ticks = (int)visibleTicks;
        int frameIndex0 = frameIndex -minFrameIndex - radiusTicks +1;

        int xMinTick = x0; // draws from x0 to here
        int xMaxTick = x1; // draws from x1 to here

        annotBalloons.clear();

        for( int t = 0; t < ticks; ++t ) {

            int r = heightSml;

            int frameIndexN = frameIndex0 + t;

            if( ( (frameIndexN-1) % intervalMed ) == 0 ) {
                r = heightMed;
            }
            if( ( (frameIndexN-1) % intervalBig ) == 0 ) {
                r = heightBig;
            }

//            int h = r * 2;
            int xTick = xTick0 + ( t * wTick );
            g2d.drawLine( xTick, y-r, xTick, y+r );

            // determine limits of valid area:
            if( frameIndexN < minFrameIndex ) {
                xMinTick = xTick; // keeps advancing
            }
            if( frameIndexN > maxFrameIndex ) {
                if( xTick < xMaxTick ) {
                    xMaxTick = xTick - wTick;
                }
            }

//                paint the annotations here...
//            ArrayList< CtAnnotations > al = getAnnotations( frameIndexN );

            Set<CtAnnotations> annotations = getAnnotations( frameIndexN );
            if( !annotations.isEmpty() ) {
                CtAnnotationsBalloon ab = new CtAnnotationsBalloon(annotations);
                ab.origin.x = xTick;
                ab.origin.y = y;
                ab.paint(g2d);
                annotBalloons.put(frameIndexN, ab);
            }
        }

        xMinTick += (wTick * 0.5);
        xMaxTick += (wTick * 0.5);
        
        g2d.setColor( _invalid );

        if( xMinTick != x0 ) g2d.fillRect( x0      , y0, xMinTick-x0, y1 );
        if( xMaxTick != x1 ) g2d.fillRect( xMaxTick, y0, x1-xMaxTick, y1 );
    }


    protected Set<CtAnnotations> getAnnotations( int frameIndex ) {

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSelectionController isc = (CtImageSelectionController)cc.getImageSequenceController();

        if( isc == null ) {
            return Collections.EMPTY_SET;
        }

        CtAnnotationsController ac = (CtAnnotationsController) CtObjectDirectory.get( CtAnnotationsController.name() );

        if( ac == null ) {
            return Collections.EMPTY_SET;
        }

        CtAnnotationsModel am = ac.getAnnotationsModel();
        CtImageSequenceModel ism = CtCoordinatesController.get().getImageSequenceModel();
        
        try {
            CtImages image = ism.get( frameIndex );
            Set<CtAnnotations> s = am.getByImageTime( image );
//            System.out.println( "getAnnotations: frame: " + frameIndex + " size(): " + s.size() );
            return s;
        }
        catch( Exception e ) {
            // nothing...?
            return Collections.EMPTY_SET;
        }
    }
}
