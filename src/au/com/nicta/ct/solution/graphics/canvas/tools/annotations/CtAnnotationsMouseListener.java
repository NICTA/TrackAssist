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

package au.com.nicta.ct.solution.graphics.canvas.tools.annotations;

import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtAnnotationsTypes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvas;
import au.com.nicta.ct.experiment.coordinates.viewpoint.CtViewpointController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author davidjr
 */
public class CtAnnotationsMouseListener implements MouseListener, MouseMotionListener {

    protected CtAnnotationsCanvasLayer _acl;
    protected CtAnnotations _selected;

    protected int _xMousePressed = 0;
    protected int _yMousePressed = 0;
    protected int _dxScreen = 0;
    protected int _dyScreen = 0;

    public CtAnnotationsMouseListener( CtAnnotationsCanvasLayer acl ) {
        _acl = acl;
        _acl.addMouseListener( this );
        _acl.addMouseMotionListener( this );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Mouse listener stuff from controller:
    ////////////////////////////////////////////////////////////////////////////
    @Override public void mouseEntered( MouseEvent e ) {}
    @Override public void mouseExited( MouseEvent e ) {}
    @Override public void mouseMoved( MouseEvent e ) {}

    @Override public void mouseReleased( MouseEvent e ) {
        CtAnnotationsController ac = CtAnnotationsController.get();

        int mode = ac.getMode();

        if( mode == CtAnnotationsController.MODE_SELECT ) {
            ac.save( _selected );
        }
    }

    @Override public void mousePressed( MouseEvent e ) {

        int ex = e.getX();
        int ey = e.getY();

        _xMousePressed = ex;
        _yMousePressed = ey;

        CtAnnotations a = _acl.findAnnotationAt( ex, ey );

        _selected = a;

        if( a != null ) {
            CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)_acl.getParent();
            _dxScreen = _xMousePressed - (int)zc.toScreenX( a.getX() );
            _dyScreen = _yMousePressed - (int)zc.toScreenY( a.getY() );
        }
        else {
            _dxScreen = 0;
            _dyScreen = 0;
        }
//
//        if( a != null ) {
//            System.out.println( "clicked annotation" );
//        }
    }

    @Override public void mouseDragged( MouseEvent e ) {
        CtAnnotationsController ac = CtAnnotationsController.get();
        CtAnnotations a = _selected;

        if( a == null ) {
            return;
        }

        e.consume();

        int ex = e.getX();
        int ey = e.getY();

//        int dx = ex - _xMousePressed;
//        int dy = ey - _yMousePressed;

        _xMousePressed = ex;
        _yMousePressed = ey;

        CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)_acl.getParent();
        double x = zc.toNaturalX( (double)ex - _dxScreen );
        double y = zc.toNaturalY( (double)ey - _dyScreen );

        a.setX( x ); // saved later, on mouse released
        a.setY( y );

        _acl.repaint();
    }

    @Override public void mouseClicked( MouseEvent e ) {
        CtAnnotationsController ac = CtAnnotationsController.get();

        int mode = ac.getMode();

        switch( mode ) {
            case CtAnnotationsController.MODE_SELECT: select( e ); break;
            case CtAnnotationsController.MODE_CREATE: create( e ); break;
            case CtAnnotationsController.MODE_DELETE: delete( e ); break;
        }
    }

    protected void select( MouseEvent e ) {
        CtAnnotationsController ac = CtAnnotationsController.get();
        CtAnnotations a = _selected;

        if( a == null ) {
            return;
        }

        e.consume();

        String s = a.getValue();
        String input = (String)JOptionPane.showInputDialog( CtPageFrame.find(), "Comment:", "Edit Annotation", JOptionPane.PLAIN_MESSAGE, null, null, s );

        if( input == null ) {
            return;
        }

        a.setValue( input );
        ac.save( _selected );
    }

    protected void delete( MouseEvent e ) {
        CtAnnotationsController ac = CtAnnotationsController.get();
        CtAnnotations a = _selected;

        if( a == null ) {
            return;
        }

        e.consume();

        int result = JOptionPane.showConfirmDialog( CtPageFrame.find(), "Delete Annotation", "Are you sure?", JOptionPane.YES_NO_OPTION );
        if( result == JOptionPane.YES_OPTION ) {
            ac.remove( _selected );
        }
    }

    protected void create( MouseEvent e ) {
        if( _selected != null ) {
            return; // clicked on existing one
        }

        e.consume();

        String input = (String)JOptionPane.showInputDialog( CtPageFrame.find(), "Comment:", "Edit Annotation", JOptionPane.PLAIN_MESSAGE, null, null, "Type comment here" );

        if( input != null ) {
            int ex = e.getX();
            int ey = e.getY();

            CtViewpointZoomCanvas zc = (CtViewpointZoomCanvas)_acl.getParent();
            CtViewpointController vc = zc.getViewpointController();
            CtImages i = vc.getViewpointModel().getImage();
            CtAnnotationsController ac = CtAnnotationsController.get();
            CtAnnotationsTypes at = ac.getCurrentType();

            int x = (int)zc.toNaturalX( (double)ex );
            int y = (int)zc.toNaturalY( (double)ey );

            ac.create( i, x,y, input, at );
//            _am.add( i, x, y, input, at );
//            _am.fireModelChanged();
        }
    }

}
