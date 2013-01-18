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
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtZoomCanvasPanel extends JPanel implements CtTransientListener { //implements CtCoordinatesListener, AncestorListener { //CtImageChangeListener {//AdjustmentListener, ActionListener, CtImageSequenceListener, CtChangeListener {//, CtImageResultView {

    public CtViewpointController _vc;
    public CtViewpointZoomCanvas _zc;// = new CtZoomCanvas();
    public JPanel _centre;
    
    public CtZoomCanvasPanel() {

        super();

        setLayout( new BorderLayout() );
        setOpaque( false );

        int w = 640;
        int h = 480;

        _vc = new CtViewpointController();
        _zc = new CtViewpointZoomCanvas( _vc );
        _zc.setNaturalSize( w, h );
        _zc.setPreferredSize( new Dimension( w,h ) );
        _zc.setOpaque( false );

        _centre = new JPanel();
        _centre.setLayout( new BorderLayout() );
        _centre.setOpaque( false );
        _centre.add( _zc, BorderLayout.CENTER );
//        add( _tb, BorderLayout.NORTH );
        _centre.add( _zc.getScrollBarHor(), BorderLayout.SOUTH );
        _centre.add( _zc.getScrollBarVer(), BorderLayout.EAST );

        add( _centre, BorderLayout.CENTER );

    }

    @Override public void stopListening() {
        _zc.stopListening(); // passes message onto all layers
        _vc.stopListening();
    }

    public CtViewpointZoomCanvas getZoomCanvas() {
        return _zc;
    }

    public CtViewpointController getViewpointController() {
        return _vc;
    }

}
