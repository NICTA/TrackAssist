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

import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Originally zoom on mouse scroll, added option to change time on mouse scroll.
 * @author davidjr
 */
public class CtZoomCanvasMouseListener extends MouseAdapter {
    
    public static final int MOUSE_WHEEL_MODE_TIME = 0; // apparently this is used more than zoom changes
    public static final int MOUSE_WHEEL_MODE_ZOOM = 1;
    int mouseWheelMode = MOUSE_WHEEL_MODE_TIME;

    int pressX, pressY;
    CtZoomCanvas zc;

    public CtZoomCanvasMouseListener( CtZoomCanvas zc ) {
        this.zc = zc;
    }

    @Override public void mousePressed(MouseEvent e)
    {
        pressX = e.getX();
        pressY = e.getY();
    }

    @Override public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - pressX;
        int dy = e.getY() - pressY;
        pressX = e.getX();
        pressY = e.getY();
        zc.scrollHorUI.setValue( zc.scrollHorUI.getValue() - dx );
        zc.scrollVerUI.setValue( zc.scrollVerUI.getValue() - dy );
    }

    @Override public void mouseWheelMoved(MouseWheelEvent e) {
        switch( mouseWheelMode ) {
            case MOUSE_WHEEL_MODE_TIME: changeTimeOnMouseWheelMoved( e ); break;
            case MOUSE_WHEEL_MODE_ZOOM: changeZoomOnMouseWheelMoved( e ); break;
        }
    }

    public void changeTimeOnMouseWheelMoved( MouseWheelEvent e ) {
        if( !( zc instanceof CtViewpointZoomCanvas ) ) {
            throw new ClassCastException();
        }

        int rotation = e.getWheelRotation();
        ((CtViewpointZoomCanvas)zc).getViewpointController().addTime( rotation );
    }

    public void changeZoomOnMouseWheelMoved( MouseWheelEvent e ) {
        zc.zoom( zc.getZoomLevel()-e.getWheelRotation(), e.getX(), e.getY(), zc.getZoomScale() );
    }
}
