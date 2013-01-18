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

package au.com.nicta.ct.ui.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * SplashScreen for app.
 * @author davidjr
 */
public class CtSplashScreen extends JWindow {

//    protected static final String _image  = "./dist/nicta_splash.png";
//    protected static final int _delay = 1500;
    
    public CtSplashScreen( JFrame f, String image, String notes, boolean timer ) {

        super( f );
        JLabel label = new JLabel( new ImageIcon( image ) );
        label.setBorder( LineBorder.createGrayLineBorder() );
        JLabel south = new JLabel( " "+notes );
        south.setBackground( Color.WHITE );
        south.setOpaque( true );
        south.setBorder( LineBorder.createGrayLineBorder() );
        getContentPane().add( label, BorderLayout.CENTER );
        getContentPane().add( south, BorderLayout.SOUTH );
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = label.getPreferredSize();
        setLocation( screenSize.width /2 - (labelSize.width /2),
                     screenSize.height/2 - (labelSize.height/2) );

        addMouseListener( 
            new MouseAdapter() {
                @Override public void mousePressed( MouseEvent me ) {
                    setVisible( false );
                    dispose();
                }
            }
        );

        Runnable waiter = null;

        if( timer ) {
            final Runnable disposer = new Runnable() {
                public void run() {
                    setVisible( false );
                    dispose();
                }
            };

            waiter = new Runnable() {
                int _delay = 1500;

                public void run()
                {
                    try
                    {
                        Thread.sleep( _delay );
                        SwingUtilities.invokeAndWait( disposer );
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        // can catch InvocationTargetException
                        // can catch InterruptedException
                    }
                }
            };
        }

        setVisible( true );

        if( timer ) {
            Thread splashThread = new Thread( waiter, "SplashThread" );
            splashThread.start();
        }
    }
}
