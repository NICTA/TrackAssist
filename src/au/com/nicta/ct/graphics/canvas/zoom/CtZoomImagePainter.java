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

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.CtCanvasPainter;
import au.com.nicta.ij.operations.CtImageResult;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Alan
 */
public class CtZoomImagePainter implements CtCanvasPainter {

    CtImageResult imageResult;
    BufferedImage image;
    CtZoomCanvas z;

    public CtZoomImagePainter(CtZoomCanvas z, BufferedImage image) {
        this.z = z;
        this.image = image;
        if( image != null ) {
            z.setNaturalSize(image.getWidth(), image.getHeight());
        }
    }

    public void setImage( CtImageResult imageResult ) {
        this.imageResult = imageResult;
        this.image = null;
        BufferedImage image = imageResult.getBufferedImage();
        if( image != null ) {
            z.setNaturalSize(image.getWidth(), image.getHeight());
        }
    }

    public void setImage( BufferedImage image ) {
        this.imageResult = null;
        this.image = image;
        if( image != null ) {
            z.setNaturalSize(image.getWidth(), image.getHeight());
        }
    }
    
    public void paint(Graphics2D g, CtCanvasLayer l)
    {
//g.setColor( Color.red );
//g.fillRect(0,0,200,200);

        assert !(image != null && imageResult != null) : "Not allowed";

        if( image != null ) {
            g.drawRenderedImage( image, z.getAffineToScreen() );
        }
        else if( imageResult != null ) {
            g.drawRenderedImage( imageResult.getBufferedImage(), z.getAffineToScreen() );
        }
    }
}
