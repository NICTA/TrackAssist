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

package au.com.nicta.ij.operations;

import au.com.nicta.ij.graph.CtResultNode;
import au.com.nicta.ij.graph.CtOperationNode;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * wrapper for an IP
 * @author Alan
 */
public class CtImageResult extends CtResultNode {

    private ImageProcessor ip;
    private BufferedImage bufferedImage;
    private int           bufferedImageVersion = 0;
    private Image image;
    private int   imageVersion = 0;

    public CtImageResult() {
        // nothing
    }
    
    public CtImageResult(ImageProcessor ip) {
        this.ip = ip;
    }

    public CtImageResult(CtOperationNode op) {
       super(op);
    }

    public ImageProcessor getIP() {
        refresh();
        return ip;
    }

    public void setIP(ImageProcessor ip) {
        this.ip = ip;
        setChanged();
    }

    public Image getImage() {
        refresh();
        if( imageVersion != getVersion() ) {
            imageVersion  = getVersion();
            image = ip.createImage();
        }
        else {
            System.out.println("Returning cache");
        }
        return image;
    }

    public BufferedImage getBufferedImage() {
        if( ip == null ) {
            return null;
        }

        if( bufferedImage == null ) {
            refresh();
        }
        
        if( bufferedImageVersion != getVersion() ) {
            bufferedImageVersion  = getVersion();
            bufferedImage = ip.getBufferedImage();
        }
        else {
            System.out.println("Returning cache");
        }
        return bufferedImage;
    }

}
