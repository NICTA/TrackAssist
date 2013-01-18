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

package au.com.nicta.ct.graphics.canvas.images;

import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.graphics.canvas.CtCanvas;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomImagePainter;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointListener;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointModel;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesModel;
import ij.process.ImageProcessor;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Automatically gets repainted on viewpoint change, but also picks up changes
 * in viewpoint original image and passes these to listeners, so
 * @author davidjr
 */
public class CtImageResultsCanvasLayer extends CtViewpointCanvasLayer {// implements CtCoordinatesListener {//, AncestorListener { //CtImageChangeListener {//AdjustmentListener, ActionListener, CtImageSequenceListener, CtChangeListener {//, CtImageResultView {

    public static final String CANVAS_LAYER_NAME = "image-resultS-canvas-layer";
    public static final String IMAGE_RESULT_ORIGINAL = "Original Image";
    public static final String EVT_IMAGE_RESULT_SELECTION_CHANGED = "selected-image-result-changed";
    public static final String EVT_ORIGINAL_IMAGE_RESULT_CHANGED = "original-image-result-changed";

    protected CtZoomImagePainter _zip;
    protected CtChangeModel _cm = new CtChangeModel( null );

//    protected CtImageResult original;
//    protected CtImageResult modified;

    protected String _selected = IMAGE_RESULT_ORIGINAL;
    protected HashMap< String, CtImageResult > _imageResults = new HashMap<String, CtImageResult>(); // contains modified images of each layer
    
    public CtImageResultsCanvasLayer() {// CtZoomCanvas zc ) {

        super( CANVAS_LAYER_NAME );
//
//        layers ;
//
//        original = new CtImageResult();
//        modified = original;
//
        setImageResult( IMAGE_RESULT_ORIGINAL, new CtImageResult() );
//        try {
//            _cli = _zc.createLayer( "Layer Image" ); // was: newLayer
//            _zip = new CtZoomImagePainter( _zc, null );
//            _zip.setImage( original );
//            _cl.addPainter( _zip );
//        }
//        catch( CtCanvasException cce ) {
//            System.err.println( "ERROR: Zoom Canvas setup problem." );
//        }
//
//        add( _zc, BorderLayout.CENTER );
//        add( _zc.getScrollBarHor(), BorderLayout.SOUTH );
//        add( _zc.getScrollBarVer(), BorderLayout.EAST );
//
//        _zc.setOpaque( false );
//
//        CtExperimentModel em = CtExperimentModel.get();
//        new CtImageListener( em._cc, em._isf, this ); // will call onImageChanged()
//        CtImageListener.setupListener( this );
//        CtCoordinatesController.addCoordinatesListener( this );
//        onModelChanged();
//        addAncestorListener( this );
    }

    @Override public void setParent( CtCanvas c ) {
        super.setParent( c );

        _zip = new CtZoomImagePainter( _zc, null );

        onImageChanged();
    }

    public static CtImageResultsCanvasLayer get( CtViewpointZoomCanvas zc ) {
        CtCanvasLayer cl = zc.getLayer( CtImageResultsCanvasLayer.CANVAS_LAYER_NAME );
        CtImageResultsCanvasLayer ircl = (CtImageResultsCanvasLayer)cl;
        return ircl;
    }
    
    public void addOriginalImageResultsChangedListener( CtChangeListener cl ) {
        _cm.addListener( EVT_ORIGINAL_IMAGE_RESULT_CHANGED, cl );
    }

    public void removeOriginalImageResultsChangedListener( CtChangeListener cl ) {
        _cm.removeListener( EVT_ORIGINAL_IMAGE_RESULT_CHANGED, cl );
    }

    public void addImageResultsSelectionChangedListener( CtChangeListener cl ) {
        _cm.addListener( EVT_IMAGE_RESULT_SELECTION_CHANGED, cl );
    }

    public void removeImageResultsSelectionChangedListener( CtChangeListener cl ) {
        _cm.removeListener( EVT_IMAGE_RESULT_SELECTION_CHANGED, cl );
    }
    
    @Override public void paint( Graphics2D g ) {//, CtCanvasLayer l ) {
        _zip.paint( g, this );
    }
//    // Called when the source or one of its ancestors is made visible either by setVisible(true) being called or by its being added to the component hierarchy.
//    public void ancestorAdded( AncestorEvent event ) {
//        _tb.setVisible( true );
//    }
//
//    // Called when either the source or one of its ancestors is moved.
//    public void ancestorMoved( AncestorEvent event ) {}
//
//    // Called when the source or one of its ancestors is made invisible either by setVisible(false) being called or by its being remove from the component hierarchy.throws
//    public void ancestorRemoved( AncestorEvent event ) {
//        _tb.setVisible( false );
//    }

//    public void addImageResult( String key, CtImageResult ir ) {
//        layers.put( key, ir );
//    }
//add change support to this for the tool to control it

    public String getSelectedKey() {
        return _selected;
    }

    public Collection< String > getImageResultKeys() {
        return _imageResults.keySet();
    }

    public CtImageResult getImageResult( String key ) {
        return _imageResults.get( key );
    }

    public void onImageResultChanged( String key ) {
        if( key.equals( _selected ) ) {
            selectImageResult( key, true ); // force repaint
        }
    }

    public void setImageResult( String key, CtImageResult ir ) {

        boolean existingKey = false;
        if( _imageResults.containsKey( key ) ) {
            existingKey = true;
        }

        _imageResults.put( key, ir );

        if( key.equals( _selected ) ) {
            selectImageResult( key, true ); // force repaint
//            _zip.setImage( ir );
//            repaint();
        }

        if( !existingKey ) {
            _cm.fire( EVT_IMAGE_RESULT_SELECTION_CHANGED );
        }
//        else {
//            _cm.fireModelChanged();
//        }
    }

//    public JToolBar getToolBar() {
//        if( _tb == null ) {
//            _tb = new CtTitleToolBar( null );// JToolBar();
////            _tb.setBackground( Color.WHITE );
//            add( _tb, BorderLayout.NORTH );
//        }
//        return _tb;
//    }
//
//    public CtZoomCanvas getZoomCanvas() {
//        return _zc;
//    }
    
//    public void onControllerChanged( CtImageSequenceController isc ) {
//        setImageSequenceController( (CtImageSelectionController)isc );
//    }
//
//    public void setImageSequenceController( CtImageSelectionController isc ) {
//        setImageSequence( (CtImageSequenceModel)isc.getModel() );
//        isc.addModelListener( this );
//    }
//
//    public void setImageSequence( CtImageSequenceModel is ) {
//        _is = is;
//        onOriginalChanged();
//    }
//
//    @Override public void adjustmentValueChanged( AdjustmentEvent ae ){
//        onOriginalChanged();
//    }
//
//    @Override public void actionPerformed( ActionEvent ae ){
//        onOriginalChanged();
//    }

    public CtImageResult getOriginal() {
        return _imageResults.get( IMAGE_RESULT_ORIGINAL );
//        return original;
    }

    public CtImageResult getSelected() {
        return _imageResults.get( _selected );
//        return modified;
    }

    public boolean isSelected( String key ) {
        return _selected.equals( key );
    }

    public void deselectImageResult( String key ) {
        selectImageResult( IMAGE_RESULT_ORIGINAL ); // TODO - remember history?
    }

    public void selectImageResult( String key ) {
        selectImageResult( key, false );
    }
    
    public void selectImageResult( String key, boolean repaint ) {


        if( key == null ) {
            return;
        }
        
        CtImageResult ir = getImageResult( key );

        if( !key.equals( _selected ) ) {
            if( ir != null ) {
                _selected = key;
                repaint = true;
            }
        }

        if( repaint ) {
            if( _zip != null ) {
                if( ir != null ) {
                    _zip.setImage( ir ); // can set to null..
                }
            }
            repaint();
            _cm.fire( EVT_IMAGE_RESULT_SELECTION_CHANGED );
//            _cm.fireModelChanged();
        }
    }

//    public void onOriginalChanged() {
//    public void onModelChanged() {
//        onImageChanged();
//    }
//    public void onRangeChanged() {
//        onImageChanged();
//    }
//    public void onIndexChanged() {
//        onImageChanged();
//    }

//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
//        onImageChanged( isf );
//    }
//
//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {
    @Override public void propertyChange( PropertyChangeEvent evt ) { // ie viewpoint changed

        if( evt.getPropertyName().equals( CtViewpointListener.EVT_ORDINATES_CHANGED ) ) {
            onImageChanged();
        }
        else if(evt.getPropertyName().equals(CtViewpointListener.EVT_IMAGE_CHANGED)) {
            onImageChanged();
        }
        else {
            super.propertyChange( evt );
        }
    }

    public void onImageChanged() {

        CtViewpointController vc = _zc.getViewpointController();
        CtViewpointModel vm = vc.getViewpointModel();
        CtImages i = vm.getImage();

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtCoordinatesModel cm = cc.getCoordinatesModel();

        CtImageResult ir = getImageResult( IMAGE_RESULT_ORIGINAL );
        assert( ir != null );

        try {
            ImageProcessor ip = cm.getImagePlus( i ).getProcessor();
            ir.setIP( ip );
//            ir.refresh();
        }
        catch( NullPointerException npe ) {
            // nothing, just set everything to null and don't display anything
        }
        catch( IOException ioe ) {
            System.err.println( "ERROR: Selected image data not found, can't display" );
            System.err.print( ioe );
        }

        _cm.fire( EVT_ORIGINAL_IMAGE_RESULT_CHANGED );

        Collection< CtImageResult > cir = _imageResults.values();

        for( CtImageResult ir2 : cir ) {
            ir2.refresh(); // including original.
        }

        selectImageResult( _selected, true ); // repaint
    }
}
