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

import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.graphics.canvas.zoom.CtImageResultZoomCanvas;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ij.operations.CtImageResult;
import au.com.nicta.ct.ui.swing.components.CtBackgroundImagePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Component to show a single image in a zoom canvas..
 *
 * @author davidjr
 */
public class CtImageResultDialog extends JDialog implements ActionListener {

    public static final String COMMAND_CLOSE = "close";

    CtImageResultZoomCanvas _zc;

    public CtImageResultDialog( String title, CtImageResult ir, int w, int h, boolean modal ) {
        super( CtPageFrame.find(), title, modal );

        _zc = new CtImageResultZoomCanvas();
        _zc.setNaturalSize( w, h );
        _zc.setPreferredSize( new Dimension( w,h ) );
        _zc.setOpaque( false );
        _zc.setImageResult( ir );

        try {
            JPanel all = new CtBackgroundImagePanel();
            all.setLayout( new BorderLayout() );
            JPanel centre = new JPanel( new BorderLayout() );
            centre.setOpaque( false );
            centre.add( _zc, BorderLayout.CENTER );
            centre.add( _zc.getScrollBarHor(), BorderLayout.SOUTH );
            centre.add( _zc.getScrollBarVer(), BorderLayout.EAST );

            JButton close = new JButton( "Close" );
            close.setActionCommand( COMMAND_CLOSE );
            close.addActionListener( this );
            JPanel south = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
            south.setBackground( CtConstants.NictaYellow );
    //            south.setOpaque( false );
            south.add( close );

            all.add( centre, BorderLayout.CENTER );
            all.add( south, BorderLayout.SOUTH );
        //        zcd.setLayout( new BorderLayout() );
            add( all );//, BorderLayout.CENTER );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }

        setSize( w, h );
        setLocationRelativeTo( null );
        pack();
    }

    public static void show( String title, CtImageResult ir ) {

        int w = 640;
        int h = 480;

        CtImageResultDialog zcd = new CtImageResultDialog( title, ir, w, h, true );

        zcd.setVisible( true );
    }

    @Override public void actionPerformed( ActionEvent ae ) {
        dispose();
    }
}
