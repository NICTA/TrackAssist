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

import au.com.nicta.ct.db.CtApplication;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtBackgroundImagePanel extends JPanel { //JComponent {

    protected BufferedImage _bi;
    protected boolean _tile = true;

    public CtBackgroundImagePanel() throws IOException {
        this( CtApplication.datafile( CtPageFrame.BACKGROUND ), true );
    }
    
    public CtBackgroundImagePanel( String imageFilePath, boolean tile ) throws IOException {

        // this.getClass().getResource("snow.png")); ???
        _tile = tile;
        _bi = ImageIO.read( new File( imageFilePath ) );
    }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent( g );

        int cw = getWidth ();
        int ch = getHeight();

        if( !_tile ) { // stretch
            g.drawImage( _bi, 0, 0, cw, ch, this );
        }
        else { // tile
            int iw = _bi.getWidth ( this );
            int ih = _bi.getHeight( this );

            if(    ( iw <= 0 )
                || ( ih <= 0 ) ) {
                return;
            }

            for( int x = 0; x < cw; x += iw ) {
                for (int y = 0; y < ch; y += ih ) {
                    g.drawImage( _bi, x, y, iw, ih, this );
                }
            }
        }
    }

}
