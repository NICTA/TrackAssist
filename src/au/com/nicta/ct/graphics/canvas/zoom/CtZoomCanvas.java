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

package au.com.nicta.ct.graphics.canvas.zoom;

import au.com.nicta.ct.ui.swing.util.CtTransientListener;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtPanZoom;
import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import javax.swing.JScrollBar;

/**
 *
 * @author Alan
 */
public abstract class CtZoomCanvas extends CtCanvas implements CtTransientListener, CtChangeListener {


    /**
     * The size of the canvas at zoom level 0
     */
    protected double naturalWidth; // height of the image, unzoomed
    protected double naturalHeight; //
    protected double oldZoomScale = 1.0;

    protected JScrollBar scrollHorUI;
    protected JScrollBar scrollVerUI;

    public CtZoomCanvas() {
        setupScrollBars();
        addComponentListeners();
        addMouseListeners();
        setOpaque( false );
    }

    public abstract CtPanZoom getPanZoom();

    @Override public void stopListening() {
        for( CtCanvasLayer cl : layers ) {
            cl.stopListening();
        }
//        _vc.stopListening(); managed by creator of VC
    }

    protected void addComponentListeners() {
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e)
            {
//                zoom( zoomLevelModel.getLevel(), 0, 0 );
                CtPanZoom pz = getPanZoom();
                if( pz != null ) {
                    zoom( pz.getLevel(), getWidth()/2, getHeight()/2, getZoomScale() );
                }
            }
        });
    }

    protected void addMouseListeners() {
        CtZoomCanvasMouseListener ml = new CtZoomCanvasMouseListener( this );
        addMouseListener( ml );
        addMouseWheelListener( ml );
        addMouseMotionListener( ml );
    }

    public double getNaturalWidth() {
        return naturalWidth;
    }

    public double getNaturalHeight() {
        return naturalHeight;
    }

    public void setNaturalSize(double width, double height) {
        this.naturalWidth  = width;
        this.naturalHeight = height;
    }

    public JScrollBar getScrollBarHor() {
        return scrollHorUI;
    }

    public JScrollBar getScrollBarVer() {
        return scrollVerUI;
    }

//    void zoomChanged() {
//        updateScrollBarHor();
//        updateScrollBarVer();
//        repaint();
//    }


    void setupScrollBars() {
        scrollHorUI = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollHorUI.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                scrollBarHorValueChanged();
            }
        });

        scrollVerUI = new JScrollBar(JScrollBar.VERTICAL);
        scrollVerUI.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                scrollBarVerValueChanged();
            }
        });
    }

    protected void scrollBarHorValueChanged() {
        getPanZoom().setOffsetX( scrollHorUI.getValue() );
    }

    protected void scrollBarVerValueChanged() {
        getPanZoom().setOffsetY( scrollVerUI.getValue() );
    }

    public int getZoomLevel() {
        return getPanZoom().getLevel();
    }

    public double getZoomScale() {
        try {
            double zoomScale = getPanZoom().getScale();
            oldZoomScale = zoomScale; // need to remember to compute correct natural coords in the event of external zoom scale.
            return zoomScale;
        }
        catch( NullPointerException npe ) {
            getPanZoom().getScale();
            return 0.0;
        }
    }

    public Point2D.Double getOffset() {
//        return zoomLevelModel.getOffset();
        return getPanZoom().getOffset();
    }

    public AffineTransform getAffineToScreen() {
//        return zoomLevelModel.getAffineToScreen( new AffineTransform() );
        return getPanZoom().getAffineToScreen( new AffineTransform() );
    }

    public AffineTransform getAffineToScreen(AffineTransform aff) {
//        double scale = zoomLevelModel.getScale();
        double scale = getPanZoom().getScale();
        // Last applied will be applied first.
//        aff.setToTranslation(-zoomLevelModel.getOffsetX(), -zoomLevelModel.getOffsetY()); // offset is AFTER scaling
        aff.setToTranslation(-getPanZoom().getOffsetX(), -getPanZoom().getOffsetY()); // offset is AFTER scaling
        aff.scale(scale, scale);
        return aff;
    }

    public AffineTransform getAffineToNatural() {
//        return zoomLevelModel.getAffineToNatural(new AffineTransform());
        return getPanZoom().getAffineToNatural( new AffineTransform() );
    }

    public AffineTransform getAffineToNatural(AffineTransform aff) {
//        double scale = 1.0 / zoomLevelModel.getScale();
        double scale = 1.0 / getPanZoom().getScale();
        // Last applied will be applied first.
//        aff.translate(zoomLevelModel.getOffsetX(), zoomLevelModel.getOffsetY());
        aff.translate(getPanZoom().getOffsetX(), getPanZoom().getOffsetY());
        aff.setToScale(scale, scale);
        return aff;
    }

    public double toNaturalScale( double screen ) {
        return screen / getZoomScale();
    }

    public double toScreenScale( double natural ) {
        return natural * getZoomScale();
    }

    public double toNaturalX( double screenX ) {
//        double screenScaleModelOffset = (screenX + zoomLevelModel.getOffsetX()); // could even be negative
//        double screenScaleModelOffset = (screenX + getPanZoom().getOffsetX()); // could even be negative
//        return screenScaleModelOffset / getZoomScale();
        return toNaturalX( screenX, getZoomScale() );
    }

    public double toNaturalX( double screenX, double oldZoomScale ) {
        // screen is starting at offsetX and we clicked screenX
        // this point is scaled to pixels coords.
        // convert to natural coords by dividing by zoom scale
        double screenScaleModelOffset = (screenX + getPanZoom().getOffsetX()); // could even be negative
        return screenScaleModelOffset / oldZoomScale;
    }

    public double toNaturalY( double screenY ) {
        return toNaturalY( screenY, getZoomScale() );
    }
    
    public double toNaturalY( double screenY, double oldZoomScale ) {
//        return (screenY + zoomLevelModel.getOffsetY()) / getZoomScale();
        return (screenY + getPanZoom().getOffsetY()) / oldZoomScale;
    }

    public double toScreenX( double naturalX ) {
//        return ( naturalX * getZoomScale() ) - zoomLevelModel.getOffsetX();
        return ( naturalX * getZoomScale() ) - getPanZoom().getOffsetX();
    }

    public double toScreenY( double naturalY ) {
//        return naturalY * getZoomScale() - zoomLevelModel.getOffsetY();
        return naturalY * getZoomScale() - getPanZoom().getOffsetY();
    }

    public Path2D.Double toScreen( Path2D.Double path ) {
        double scale = getZoomScale();
        double x = - getPanZoom().getOffsetX();
        double y = - getPanZoom().getOffsetY();

        AffineTransform at = new AffineTransform( scale, 0.0, 0.0, scale, x, y );
        PathIterator pi = path.getPathIterator( at );

        Path2D.Double path2 = new Path2D.Double();

        path2.append( pi, false );

        return path2;
    }
//m00 - the X coordinate scaling element of the 3x3 matrix
//m10 - the Y coordinate shearing element of the 3x3 matrix
//m01 - the X coordinate shearing element of the 3x3 matrix
//m11 - the Y coordinate scaling element of the 3x3 matrix
//m02 - the X coordinate translation element of the 3x3 matrix
//m12 - the Y coordinate translation element of the 3x3 matrix

//    public Rectangle toScreen( Rectangle natural ) {
//        return new Rectangle(
//                toScreenX(natural.x),
//                toScreenY(natural.y),
//                (int)(natural.width  * getZoomScale()),
//                (int)(natural.height * getZoomScale()) );
//    }

    public double findOffsetNaturalToScreenX( double naturalX, double screenX ) {
        double scale = getZoomScale();
        return naturalX * scale - screenX ;
    }

    public double findOffsetNaturalToScreenY( double naturalY, double screenY ) {
        double scale = getZoomScale();
        return naturalY * scale - screenY;
    }

    public int getBorderSizeX() {
        return getWidth() / 2;
    }

    public int getBorderSizeY() {
        return getHeight() / 2;
    }

    public void updatePanOnZoom() { // zoom has externally changed
        CtPanZoom pz = getPanZoom();
        if( pz != null ) {
            zoom( pz.getLevel(), getWidth()/2,getHeight()/2, oldZoomScale );
        }
    }

    protected void updateScrollBarHor() {
        int value, extent, min, max;
        double scale = getZoomScale();
        int border = getBorderSizeX(); // this should be half the canvas size in pixels, regardless of image size.
        int screenWidth = getWidth();
        int  imageWidth = (int)( (double)naturalWidth*scale);
//System.out.println( "nat.w="+naturalWidth+" scale="+getZoomScale()+" screenWidth="+imageWidth );

        if( imageWidth > screenWidth ) {// getWidth() ) { // if zoomed in
            value = (int)(getPanZoom().getOffsetX()); // position in pixels within image
            extent = screenWidth;// + border; //getWidth(); // canvas size
            min = -border;//getBorderSizeX();
            max = imageWidth + border;//(int)(naturalWidth*getZoomScale() + (2*border) );//getBorderSizeX());
        }
        else { // zoomed out or natural size. No borders cos showing whole image
            value = (int)(getPanZoom().getOffsetX()); // is this scaled or not IT IS SCALED IN PX
            extent = screenWidth;//getWidth(); ie canvas width, ie how much is visible.
            min = 0;
            max = imageWidth;// (int)( naturalWidth*getZoomScale() );
        }

        scrollHorUI.setValues(value, extent, min, max); // (int newValue, int newExtent, int newMin, int newMax)
        // So this is all in pixels.
        // minimum <= value <= value+extent <= maximum
//System.out.println( "cw="+canvasWidth+" imgw="+imageWidth+" v="+value+" ext="+extent+" min="+min+" max="+max );
//System.out.println( "bar v="+scrollHorUI.getModel().getValue()+" ext="+scrollHorUI.getModel().getExtent()+" min="+scrollHorUI.getModel().getMinimum()+" max="+scrollHorUI.getModel().getMaximum() );
    }
//cw=1859 imgw=2621 v=-929 ext=1859 min=-929 max=3550
//2621 + 1859 = 4480 - 3551

    protected void updateScrollBarVer() {
        int value, min, max;
        int extent = getHeight();
        int border = getBorderSizeY(); // this should be half the canvas size in pixels, regardless of image size.
        int canvasHeight = getHeight();
        int  imageHeight = (int)( (double)naturalHeight*getZoomScale());

//        if( naturalHeight*getZoomScale() > getHeight() ) {
        if( imageHeight > canvasHeight ) {
            value = (int)(getPanZoom().getOffsetY());
            min = -border;//-getBorderSizeY();
            max = imageHeight +border;//(int)(naturalHeight*getZoomScale() +getBorderSizeY());
        }
        else { // no borders cos showing whole image
            value = (int)(getPanZoom().getOffsetY());
            min = 0;
            max = canvasHeight;//(int)( naturalHeight*getZoomScale() );
        }
        scrollVerUI.setValues(value, extent, min, max);
    }

    public void zoomNaturalWindowAround( Rectangle2D boundingBox, double padFractionNaturalSize ) {

        double x = boundingBox.getX();
        double y = boundingBox.getY();
        double w = boundingBox.getWidth();
        double h = boundingBox.getHeight();

        double xPad = ( naturalWidth  - w ) * padFractionNaturalSize;
        double yPad = ( naturalHeight - h ) * padFractionNaturalSize;

        double x2 = x - ( xPad * 0.5 );
        double y2 = y - ( yPad * 0.5 );
        double w2 = w + xPad;
        double h2 = h + yPad;
        
        zoomNaturalWindow( x2,y2, w2,h2 );
    }

    public void zoomNaturalWindow( Rectangle2D boundingBox ) {
        zoomNaturalWindow( boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight() );
    }

    public void zoomNaturalWindow( double naturalX, double naturalY, double naturalW, double naturalH ) {

        // find the zoom level that will optimally present this window:
    //public double toScreenScale( double natural ) {
        double visibleScreenW = getWidth();
        double visibleScreenH = getHeight();

        // find the zoom level that optimally presents this window on the screen:
        int levelRange = 100; // TODO - param
        int levelMin = -levelRange;
        int levelMax =  levelRange;
        int levelBest = 0;

        double dMinBest = Double.MAX_VALUE;

        for( int level = levelMin; level <= levelMax; ++level ) {
            double scale = getPanZoom().scale( level );

            double screenW = naturalW * scale;
            double screenH = naturalH * scale;

            // scaled screen dims MUST BE be LESS THAN visibleScreen[w/h] AND
            // SHOULD BE as close to visibleScreen[w/h] as possible.
            if( screenW > visibleScreenW ) continue;
            if( screenH > visibleScreenH ) continue;

            double dx = Math.abs( screenW - visibleScreenW );
            double dy = Math.abs( screenW - visibleScreenW );
            double dMin = Math.min( dx, dy );

            if( dMin < dMinBest ) {
                dMinBest = dMin;
                levelBest = level;
            }
        }

        double scaleBest = getPanZoom().scale( levelBest );

        double screenX = naturalX * scaleBest;
        double screenY = naturalY * scaleBest;
        double screenW = naturalW * scaleBest;
        double screenH = naturalH * scaleBest;

        // also centre the area being viewed:
        screenX -= ((visibleScreenW-screenW) /2 );
        screenY -= ((visibleScreenH-screenH) /2 );

        getPanZoom().setLevel( levelBest );
        getPanZoom().setOffset( screenX, screenY );
    }

    /**
     * //TODO: add some code to check if zoom level is supported
     * @param focusScreenX
     * @param focusScreenY
     * @param zoomLevelModel
     * @return true if no error, eg. zoom level is supported
     */
    public boolean zoom(int level, double focusScreenX, double focusScreenY, double oldZoomScale ) {

//zoomNaturalWindow( 279,46,330-279,85-46); works

        // we want to change the view around the screen coords x,y in pixels, rel to canvas
        // procedure: first compute the logical position we're viewing.
        // then compute the new zoom and offset to get to that position

        // find the natural coordiate where we want the zoom to centre around, natural
        // coordinates are not affected by zoom level or offset.
        double focusNaturalX = toNaturalX(focusScreenX, oldZoomScale ); // params are either centre of canvas of mouse clicked coords in pixels, rel to canvas
        double focusNaturalY = toNaturalY(focusScreenY, oldZoomScale ); // answer is scaled pixels coord from

//        System.out.println("focusNaturalX: " + focusNaturalX);
//        System.out.println("focusNaturalY: " + focusNaturalY);

        // change to new zoom level
        getPanZoom().setLevel(level); // changes zoomScale - AFTER A EXTERNAL ZOOM EVENT THE LEVEL IS UNCHANGED
//        System.out.println("this.zoomLevel.getZoomScale(): " + this.zoomLevelModel.getZoomScale());
//        double offsetX = 0.0;
//        double offsetY = 0.0;

        // set the offset such that the natural coordinate maps to the screen coordinate
        double offsetX = findOffsetNaturalToScreenX(focusNaturalX, focusScreenX); // calls getZoomScale()
        double offsetY = findOffsetNaturalToScreenY(focusNaturalY, focusScreenY);

        double scaledWidth = naturalWidth * getZoomScale();
        double screenWidth = getWidth();

        double scaledHeight = naturalHeight * getZoomScale();
        double screenHeight = getHeight();

        // centre the image if zoomed out enough
        if( scaledWidth < screenWidth ) {
            // Image is zoomed out to the point that is smaller than the canvas
            offsetX = findOffsetNaturalToScreenX( naturalWidth/2, getWidth()/2 );
        }
        // clamp to -border
        else if( offsetX < -getBorderSizeX() ) {
            // leave some padding around the sides so that we can centre some
            // region near the edge
            offsetX = -getBorderSizeX();
        }
//        else if( toScreenX(naturalWidth) < getWidth() - getBorderSizeX() ) {
//            offsetX = findOffsetNaturalToScreenX( naturalWidth, getWidth() - getBorderSizeX() );
//        }

        if( scaledHeight < screenHeight ) {
            offsetY = findOffsetNaturalToScreenY( naturalHeight/2, getHeight()/2 );
        }
        else if( offsetY < -getBorderSizeY() ) {
            // leave some padding around the sides so that we can centre some
            // region near the edge
            offsetY = -getBorderSizeY();
        }
//        else if(toScreenY(naturalHeight) < getHeight() - getBorderSizeY() ) {
//            offsetY = findOffsetNaturalToScreenX(naturalHeight, getHeight() - getBorderSizeY() );
//        }

        getPanZoom().setOffset( offsetX, offsetY );

//        System.out.println("offset: " + zoomLevelModel.getOffset());

        return true;
    }
}
