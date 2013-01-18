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
import au.com.nicta.ct.db.CtApplication;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.orm.patterns.CtDirectorySingleton;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesListener;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceModel;
import au.com.nicta.ct.orm.mvc.images.CtImageSelectionController;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceController;
import au.com.nicta.ct.orm.mvc.pages.util.CtBreadcrumbPanel;
import au.com.nicta.ct.experiment.coordinates.time.windows.CtTimeWindowController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author davidjr
 */
public class CtTimeControlPanel extends JPanel implements ActionListener, ChangeListener, CtCoordinatesListener {//CtImageChangeListener {//, AdjustmentListenerCtImageSequenceListener,  {

    JButton _first;
    JButton _prevSlow;
    JButton _playPause;
    JButton _nextFast;
    JButton _last;
    JToggleButton _loop;

//    Timer _t;
//    double _acceleration = 1.0;
//    boolean _playing = false;

    ImageIcon _iiPlay;
    ImageIcon _iiPause;
    ImageIcon _iiSlower;
    ImageIcon _iiFaster;
    ImageIcon _iiPrevious;
    ImageIcon _iiNext;

    public static final String COMMAND_ZOOM_IN  = "++";
    public static final String COMMAND_ZOOM_OUT = "--";

//    public static final String COMMAND_PLAYBACK_PLAY     = "PLAYBACK_PLAY";
//    public static final String COMMAND_PLAYBACK_PAUSE    = "PLAYBACK_PAUSE";
//    public static final String COMMAND_PLAYBACK_LOOP     = "PLAYBACK_LOOP";
////    public static final String COMMAND_PLAYBACK_STOP     = "PLAYBACK_STOP";
////    public static final String COMMAND_PLAYBACK_FIRST    = "PLAYBACK_FIRST";
////    public static final String COMMAND_PLAYBACK_FINAL    = "PLAYBACK_FINAL";
////    public static final String COMMAND_PLAYBACK_PREVIOUS = "PLAYBACK_PREVIOUS";
//    public static final String COMMAND_PLAYBACK_NEXT     = "PLAYBACK_NEXT";
//    public static final String COMMAND_PLAYBACK_SLOWER   = "PLAYBACK_SLOWER";
//    public static final String COMMAND_PLAYBACK_FASTER   = "PLAYBACK_FASTER";

    public CtTimeInfoPanel _tip;
    public CtTimeLinePanel _tlp;
    public JSlider _slider;
    public JPanel zoomControls;
//    public JScrollBar _sb;

//    protected CtImageSelectionController _isc;
    protected CtTimeWindowController _twc;

    public JPanel getZoomControls() {
        return zoomControls;
    }

    public void setZoomControls(JPanel zoomControls) {
        this.zoomControls = zoomControls;
    }

    public CtTimeControlPanel( JPanel pageOptions, CtDockableWindowGrid dwg, Collection< String > windowTypes ) {
        super( new BorderLayout() );

        initComponents( pageOptions, dwg, windowTypes );
    }

//    public void addAdjustmentListener( AdjustmentListener al ) {
////        _sb.addAdjustmentListener( al );
//    }

    protected void setPlaybackState( boolean playing ) {
        _first.setActionCommand( CtImageSelectionController.FIRST );
         _last.setActionCommand( CtImageSelectionController.LAST  );

        if( playing ) {
            _prevSlow.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_SLOWER );
            _nextFast.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_FASTER );
            _playPause.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_PAUSE );
            _prevSlow.setIcon( _iiSlower );
            _nextFast.setIcon( _iiFaster );
            _playPause.setIcon( _iiPause );
        }
        else { // not playing
            _prevSlow.setActionCommand( CtImageSelectionController.PREVIOUS );
            _nextFast.setActionCommand( CtImageSelectionController.NEXT );
            _playPause.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_PLAY );
            _prevSlow.setIcon( _iiPrevious );
            _nextFast.setIcon( _iiNext );
            _playPause.setIcon( _iiPlay );
        }

        _loop.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_LOOP );
    }

    protected void initComponents( JPanel pageOptions, CtDockableWindowGrid dwg, Collection< String > windowTypes ) {

//        _t = new Timer( 0, this );
//        _t.setActionCommand( COMMAND_PLAYBACK_NEXT );
////        _t.addActionListener( this );
//        _t.setCoalesce( true );

        _iiPlay  = new ImageIcon( CtApplication.datafile( "icon_play.png" ) );//"./artwork/nicta/icon_play.png" );
        _iiPause = new ImageIcon( CtApplication.datafile( "icon_pause.png" ) );//"./artwork/nicta/icon_pause.png" );
        _iiSlower = new ImageIcon( CtApplication.datafile( "icon_slower.png" ) );//"./artwork/nicta/icon_prev.png" );
        _iiFaster = new ImageIcon( CtApplication.datafile( "icon_faster.png" ) );//"./artwork/nicta/icon_next.png" );
        _iiPrevious = new ImageIcon( CtApplication.datafile( "icon_l.png" ) );//"./artwork/nicta/icon_l.png" );
        _iiNext     = new ImageIcon( CtApplication.datafile( "icon_r.png" ) );//"./artwork/nicta/icon_r.png" );

        _first     = new JButton( new ImageIcon( CtApplication.datafile( "icon_start.png" ) ) );//"./artwork/nicta/icon_start.png" ) );//"|<<");
        _prevSlow  = new JButton( _iiPrevious );//"</w");
        _playPause = new JButton( _iiPlay );//">/||");
        _nextFast  = new JButton( _iiNext );//">/^^");
        _last       = new JButton( new ImageIcon( CtApplication.datafile( "icon_end.png" ) ) );//"./artwork/nicta/icon_end.png" ) );//">>|");
        _loop      = new JToggleButton( new ImageIcon( CtApplication.datafile( "icon_loop.png" ) ) );//"./artwork/nicta/icon_loop.png" ), false );//"loop");

        JPanel playbackButtons = new JPanel( new FlowLayout() );
        playbackButtons.add( _first );
        playbackButtons.add( _prevSlow );
        playbackButtons.add( _playPause );
        playbackButtons.add( _nextFast );
        playbackButtons.add( _last );
        playbackButtons.add( new JSeparator() ); // separate button set from content card
        playbackButtons.add( _loop );
        playbackButtons.setBackground( Color.WHITE );
        playbackButtons.setOpaque( true );
//        playbackButtons.setOpaque( false );

        _prevSlow .addActionListener( this );
        _playPause.addActionListener( this );
        _nextFast .addActionListener( this );
        _loop     .addActionListener( this );

        _first.addActionListener( this );
//        _prevSlow.addActionListener( this );
//        _nextFast.addActionListener( this );
        _last.addActionListener( this );
//        _first.addActionListener( this );

        _tlp = new CtTimeLinePanel( dwg, windowTypes );
        _tip = new CtTimeInfoPanel();

//        CtTimeWindowModel twm = new CtTimeWindowModel(); // TODO defer or externally create, allow diff models
//        _twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
//        //CtDirectorySingleton< CtTimeWindowController >.get( CtTimeWindowController.name(), CtTimeWindowController.class );
//                //(CtTimeWindowController)CtObjectDirectory.get( CtTimeWindowController.name() );//new CtTimeWindowController( twm, null );
//
//        setTimeWindowController( _twc );

//        _sb = new JScrollBar( Adjustable.HORIZONTAL, 0, 0, 0, 255 );
//        _sb.addAdjustmentListener( this );

        JButton zoomIn  = new JButton( new ImageIcon( CtApplication.datafile( "icon_expand.png" ) ) );//"<html><p><b>+</b></p></html>");
        JButton zoomOut = new JButton( new ImageIcon( CtApplication.datafile( "icon_reduce.png" ) ) );//"<html><p><b>-</b></p></html>");
        zoomIn .addActionListener( this );
        zoomOut.addActionListener( this );

        zoomIn .setActionCommand( COMMAND_ZOOM_IN );
        zoomOut.setActionCommand( COMMAND_ZOOM_OUT );

        zoomOut.setMinimumSize( zoomIn.getMinimumSize() );
        zoomOut.setPreferredSize( zoomIn.getPreferredSize() );
        zoomOut.setMaximumSize( zoomIn.getMaximumSize() );

//        JPanel zoomControls = new JPanel( new FlowLayout() );
//        zoomControls.add( new JSeparator( JSeparator.VERTICAL ) ); // separate button set from content card
//        zoomControls.add( zoomIn );
//        zoomControls.add( zoomOut );
//        zoomControls.setBackground( Color.WHITE );
//        zoomControls.setOpaque( true );
//        zoomControls.setOpaque( false );
//        zoomControls.setBorder( new LineBorder( Color.LIGHT_GRAY ) );

//        Collection< CtAbstractPair< String, String > > options = CtSolutionPages.getOptions();
//
        JPanel west = new CtBreadcrumbPanel();
        JPanel east = pageOptions;//new CtPageNavigationPanel( options );

        JPanel buttonBar = new JPanel( new BorderLayout() );
        buttonBar.add( playbackButtons, BorderLayout.CENTER );
//        buttonBar.add( zoomControls, BorderLayout.EAST );
        buttonBar.add( west, BorderLayout.WEST );
        if( east != null ) {
            buttonBar.add( east, BorderLayout.EAST );
        }
        buttonBar.setOpaque( false );


//        JPanel zoomBar = new JPanel( new BorderLayout() );
//        zoomBar.add( _sb, BorderLayout.CENTER );
//        zoomBar.add( new JSeparator(), BorderLayout.SOUTH ); // separate button set from content card
//        zoomBar.add( zoomControls, BorderLayout.EAST );

//        buttonBar.add( zoomBar, BorderLayout.NORTH );

//        JLabel info = new JLabel( "<html><p>&nbsp;&nbsp;Frame 012345 of 999999 [-5:+6], showing a window of 35 frames (70 seconds)</p></html>" );

//        buttonBar.add( info, BorderLayout.WEST );
//        add( p, BorderLayout.CENTER );
//        add( _tlp, BorderLayout.CENTER );
        JPanel p2 = new JPanel( new BorderLayout() );
//        p2.setBackground( Color.BLUE );
        p2.setOpaque( false );
        p2.add( _tlp, BorderLayout.CENTER );
        zoomControls = new JPanel( new GridLayout( 2,1 ) );//BorderLayout() );
        zoomControls.setBackground( CtConstants.NictaYellow );
        zoomControls.setOpaque( true );
        zoomControls.setBorder( new EmptyBorder( 4,4,4,4 ) );
        zoomControls.add( zoomIn );//,  BorderLayout.NORTH );
        zoomControls.add( zoomOut );//, BorderLayout.CENTER );
        JPanel p4 = new JPanel( new BorderLayout() );//BorderLayout() );
        p4.setBorder( new LineBorder( Color.LIGHT_GRAY ) );
        p4.add( zoomControls, BorderLayout.CENTER );
//        p2.add( p3, BorderLayout.EAST );
        p2.add( p4, BorderLayout.EAST );
        p2.add( _tlp, BorderLayout.CENTER );
        add( p2, BorderLayout.CENTER );
        add( buttonBar, BorderLayout.SOUTH );
//        add( _tip, BorderLayout.NORTH );

        _slider = new JSlider();
        _slider.setBackground( Color.WHITE );
        _slider.addChangeListener( this );

        JPanel p1 = new JPanel( new BorderLayout() );
        p1.setBackground( CtConstants.NictaYellow );
        p1.setBorder( new LineBorder( Color.LIGHT_GRAY ) );
        p1.add( _tip, BorderLayout.EAST );
        p1.add( _slider, BorderLayout.CENTER );
        add( p1, BorderLayout.NORTH );

        setOpaque( false );

        _twc = (CtTimeWindowController)CtDirectorySingleton.get( CtTimeWindowController.class, CtTimeWindowController.name() );
        _twc.addModelListener( this );
//            new ActionListener() {
//                @Override public void actionPerformed( ActionEvent ae ) {
//                    this.onTimeWindowChanged();
//                }
//            }
//        );
//
//        setTimeWindowController( _twc );

        CtPlaybackModel pm = CtPlaybackModel.get();
        pm.setActionListener( this );

//        CtImageListener.setupListener( this );
        CtCoordinatesController.addCoordinatesListener( this );
        onModelChanged();
//
//        CtExperimentModel em = CtExperimentModel.get();
//        em._isf.addImageSequenceListener( this );
//        onControllerChanged( em._isf.getController() );

        pause();
    }

    public void onModelChanged() {
        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtCoordinatesModel cm = cc.getCoordinatesModel();
//        CtImageSequenceController isc = cc.getImageSequenceController();
        CtImageSequenceModel ism = cc.getImageSequenceModel();
//        _tlp.setImageSequenceController( (CtImageSelectionController)isc );
        _tip.setImageSequenceModel( ism );

        updateTimeSlider();
        repaint();
    }

    public void onRangeChanged() {
        onModelChanged();
    }
    public void onIndexChanged() {
        updateTimeSlider();
        repaint();
    }

    public void onTimeWindowChanged() {
        repaint();
    }
//    @Override public void onSequenceChanged( CtImageSequenceFactory isf ) {
////    @Override public void onControllerChanged( CtImageSequenceController isc ) {
////        setImageSequenceController( (CtImageSelectionController)isc );
////    }
////
////    public void setImageSequenceController( CtImageSelectionController isc ) {
////        _isc = isc;
////        _isc.addModelListener( this );
////        setImageSequence( (CtImageSequenceModel)_isc.model() );
////        _isp.play( _isc );
//        CtImageSelectionController isc = (CtImageSelectionController)isf.getController();
//
//        isc.addModelListener( this );
//
//        _tlp.setImageSequenceController( isc );
//        _tip.setImageSequenceModel( (CtImageSequenceModel)isc.getModel() );
//
////doesnt remove old listeners
////        _first.addActionListener( isc );
////        _prevSlow.addActionListener( isc );
////        _nextFast.addActionListener( isc );
////        _last.addActionListener( isc );
////        _first.addActionListener( isc );
//
////        this._isc = isc;
////        _t.setDelay( delay() );
//
//        repaint();
//    }

//    @Override public void onImageChanged( CtImageSequenceFactory isf ) {
//
//    }

//    public void setTimeWindowController( CtTimeWindowController twc ) {
//        _twc = twc;
////        setImageSequence( (CtImageSequenceModel)_isc.model() );
////        _isp.play( _isc );
//        _tlp.setTimeWindowController( twc );
//        _tip.setTimeWindowModel( (CtTimeWindowModel)twc.getModel() );
////        _twc.addModelListener( this );
//
//        repaint();
//    }
    
//    protected void setImageSequence( CtImageSequenceModel is ) {
//
////        _is = is;
//
////        int minFrameIndex = 0;
////        int maxFrameIndex = is.size() -1;
////
////        _sb.setMinimum( minFrameIndex );
////        _sb.setMaximum( maxFrameIndex );
////
////        int frameIndex = 0;
////        _sb.getModel().setValue( frameIndex );
////        _tlp.setFrameIndexRange( frameIndex, minFrameIndex, maxFrameIndex );
////        _tlp.setImageSequence( is );
//
//        repaint();
//    }

//    public int delay() {
//        if( _isc == null ) {
//            return( 1000 * 1000 );
//        }
//
//        CtImageSequenceModel ism = (CtImageSequenceModel)_isc.getModel();
//
//        int delay = (int)( (double)ism._interFrameInterval * _acceleration );
//        return delay;
//    }

    public boolean looping() {
        return _loop.isSelected();
    }

    public boolean paused() { return !playing(); }
    public boolean playing() {
        if( _playPause.getActionCommand().equals( CtPlaybackModel.COMMAND_PLAYBACK_PLAY ) ) {
            return false;
        }
        return true;
    }

    public void play() {
        if( playing() ) {
            return;
        }

        CtPlaybackModel pm = CtPlaybackModel.get();
        pm.start();

        setPlaybackState( true );
    }

    public void pause() {
        if( paused() ) {
            return;
        }

        CtPlaybackModel pm = CtPlaybackModel.get();
        pm.stop();

        setPlaybackState( false );
    }

    // ChangeListener, for time slider: (called when time slider value changed)
    @Override public void stateChanged( ChangeEvent e ) {

        if( _bSliderPassive ) {
//            _bSliderPassive = false;
            return;
        }

        if( _slider.getValueIsAdjusting() ) {
            return;
        }

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSequenceController isc = cc.getImageSequenceController();
        CtImageSequenceModel ism = cc.getImageSequenceModel();
//        CtImageSequenceModel ism = (CtImageSequenceModel)_isc.getModel();

        int index = ism.getIndex();
        int length = ism.size();

        int sliderMin = _slider.getMinimum();
        int sliderMax = _slider.getMaximum();
        int sliderRange = sliderMax - sliderMin;
        int sliderPosition = (int)_slider.getValue();
        sliderPosition -= sliderMin;

        if( sliderRange < 1 ) {
            return;
        }

        double relative = (double)sliderPosition / (double)sliderRange;

        relative *= length;

        int minIndex = isc.getModel().getMinIndex();
        int index2 = ( (int)relative ) +minIndex; // if not zero

        isc.setCurrentIndex( index2 );
    }

protected boolean _bSliderPassive = false;

    public void updateTimeSlider() {
//        if( _isc == null ) return;
        CtCoordinatesController cc = CtCoordinatesController.get();
//        CtImageSequenceController isc = cc.getImageSequenceController();
        CtImageSequenceModel ism = cc.getImageSequenceModel();
//        CtImageSequenceModel ism = (CtImageSequenceModel)_isc.getModel();
//        if( ism == null ) return;
//System.out.println( "TCP: ism="+ism+" idx="+ism.currentIndex() )            ;

        int index = ism.getIndex();
        int length = ism.size();
        int minIndex = ism.getMinIndex();

        int sliderMin = _slider.getMinimum();
        int sliderMax = _slider.getMaximum();
        int sliderRange = sliderMax - sliderMin;
        int sliderPosition = sliderMin + (sliderRange >> 1);

        if( length > 1 ) {
            int index0 = index - minIndex;
            double relative = (double)index0 / (double)( length -1 );
            sliderPosition = (int)( (double)sliderRange * relative ) + sliderMin;
        }

        _bSliderPassive = true;
        _slider.setValue( sliderPosition );
        _bSliderPassive = false;
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        CtCoordinatesController cc = CtCoordinatesController.get();
        CtImageSelectionController isc = (CtImageSelectionController)cc.getImageSequenceController();
        CtImageSequenceModel ism = cc.getImageSequenceModel();

        String s = ae.getActionCommand();

        if( s.equals( COMMAND_ZOOM_IN ) ) {
            _tlp.increaseZoom();
        }
        else if( s.equals( COMMAND_ZOOM_OUT ) ) {
            _tlp.decreaseZoom();
        }

        // relay thru my copy of isc:
//        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_SLOWER ) ) {
//            if( _isc == null ) return;
//            _isc.actionPerformed( ae );
//        }
//        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_FASTER ) ) {
//            if( _isc == null ) return;
//            _isc.actionPerformed( ae );
//        }
        else if( s.equals( CtImageSelectionController.FIRST ) ) {
            if( isc == null ) return;
            isc.first();
        }
        else if( s.equals( CtImageSelectionController.LAST ) ) {
            if( isc == null ) return;
            isc.last();
        }
        else if( s.equals( CtImageSelectionController.PREVIOUS ) ) {
            if( isc == null ) return;
            isc.previous();
        }
        else if( s.equals( CtImageSelectionController.NEXT ) ) {
            if( isc == null ) return;
            isc.next();
        }
//        _first.setActionCommand( CtImageSelectionController.FIRST );
//         _last.setActionCommand( CtImageSelectionController.LAST  );
//            _prevSlow.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_SLOWER );
//            _nextFast.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_FASTER );
//            _playPause.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_PAUSE );
//            _prevSlow.setActionCommand( CtImageSelectionController.PREVIOUS );
//            _nextFast.setActionCommand( CtImageSelectionController.NEXT );
//            _playPause.setActionCommand( CtPlaybackModel.COMMAND_PLAYBACK_PLAY );
//        _first.addActionListener( isc );
//        _last.addActionListener( isc );
//        _prevSlow.addActionListener( isc );
//        _nextFast.addActionListener( isc );

        else if( s.equals( CtModel.ACTION_MODEL_CHANGED ) ) {
            onTimeWindowChanged();
////            int frameIndex = ((CtImageSequenceModel)_isc.model()).currentIndex();
////            _sb.getModel().setValue( frameIndex );
//            updateTimeSlider();
//            repaint();
        }
        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_PLAY ) ) {
            if( isc == null ) return;
            if( playing() ) {
                pause();
            }
            else {
//                int frameIndex1 = _isc.getFirstSelected();
//                int frameIndex2 = _isc.getLastSelected();
//
//                if( frameIndex1 == frameIndex2 ) {
//                    _isc.first(); // not sure about this
//                }

                play();
            }
        }
        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_PAUSE ) ) {
            pause();
        }
        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_NEXT ) ) {
//System.out.println("event");
            if( isc == null ) return;
            if( !playing() ) return;

//            CtImageSequenceModel ism = (CtImageSequenceModel)_isc.getModel();

            int frameIndex1 = ism.getIndex();

            if( looping() ) {
                isc.nextLoopSelected();
            }
            else {
                isc.nextSelected();
            }

            int frameIndex2 = ism.getIndex();

            if( frameIndex1 == frameIndex2 ) { // if I fail to advance, stop playing
                pause();
            }
        }

//        else if( s.equals( COMMAND_PLAYBACK_LOOP ) ) {
//            _isp.loop();
//        }
//        else if( s.equals( COMMAND_PLAYBACK_FIRST ) ) {
//
//        }
//        else if( s.equals( COMMAND_PLAYBACK_FINAL ) ) {
//
//        }
//        else if( s.equals( COMMAND_PLAYBACK_PREVIOUS ) ) {
//
//        }
//        else if( s.equals( COMMAND_PLAYBACK_NEXT ) ) {
//
//        }
        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_SLOWER ) ) {
            CtPlaybackModel pm = CtPlaybackModel.get();
            pm.slower();
//            _acceleration *= 2.0;
//            _t.setDelay( delay() );
        }
        else if( s.equals( CtPlaybackModel.COMMAND_PLAYBACK_FASTER ) ) {
            CtPlaybackModel pm = CtPlaybackModel.get();
            pm.faster();
//            _acceleration *= 0.5;
//            _t.setDelay( delay() );
        }
//        else {
//            repaint();
//        }

//        Thread t = new Thread( _isp, "CtImageSequencePlayer" );
//        t.start();
    }

//    @Override public void adjustmentValueChanged( AdjustmentEvent ae ){
//
//        if( _isc == null ) {
//            return;
//        }
//
//        int frameIndex = ae.getValue();
//
////        CtImageSequenceModel ism = (CtImageSequenceModel)_isc.model();
////
//        _isc.setCurrentIndex( frameIndex );
////        _tlp.setFrameIndex( frameIndex );
////        _tlp.repaint();
//    }

    public void showTimeControl() {
        _tip.setVisible( true );
        _tlp.setVisible( true );
        _slider.setVisible( true );
        zoomControls.setVisible( true );
    }

    public void hideTimeControl() {
        _tip.setVisible( false );
        _tlp.setVisible( false );
        _slider.setVisible( false );
        zoomControls.setVisible( false );
    }

    @Override public void setVisible( boolean b ) {
        super.setVisible( b );
        
        if ( b == true ) {
            showTimeControl();
        } else {
            hideTimeControl();
        }
    }
}
