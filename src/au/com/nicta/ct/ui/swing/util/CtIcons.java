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

package au.com.nicta.ct.ui.swing.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author davidjr
 */
public class CtIcons {

    public static final int DEFAULT_ICON_SIZE_PIXELS = 30;

    public static ImageIcon loadIcon( String filename ) {
        return loadIcon( filename, DEFAULT_ICON_SIZE_PIXELS );
    }
    
    public static ImageIcon loadIcon( String filename, int iconSizePixels ) {
        try {
            Image i = loadScaledImage( filename, iconSizePixels, iconSizePixels );

            return new ImageIcon( i );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static Image loadScaledImage( String filename, int width, int height) throws IOException {
        System.out.println("Loading icon from file: " + ( filename ) );
        return ImageIO.read( new File( filename ) ).getScaledInstance( width, height, java.awt.Image.SCALE_SMOOTH );
    }

}
