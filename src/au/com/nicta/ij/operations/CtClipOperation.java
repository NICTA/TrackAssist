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

import ij.plugin.Thresholder;
import ij.process.ImageProcessor;

/**
 *
 * @author Alan
 */
public class CtClipOperation extends CtImageOperation {

    CtImageResult src;
    int threshold = 0;
    int clipped = 0;
    boolean clipLessThan = true;
    boolean scaleResult = true;

    public CtClipOperation(CtImageResult src) {
        super(src);
        this.src = src;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
        setChanged();
    }

    public void setClipped(int clipped) {
        this.clipped = clipped;
        setChanged();
    }

    public void setClipLessThan( boolean clipLessThan ) {
        this.clipLessThan = clipLessThan;
        setChanged();
    }

    public void setScaleResult( boolean scaleResult ) {
        this.scaleResult = scaleResult;
        setChanged();
    }

    @Override
    protected void run() {
        ImageProcessor ip = src.getIP().duplicate();

        if( clipLessThan ) {
            clipLessThan( ip, this.clipped );
        }
        else {
            clipMoreThan( ip, this.clipped );
        }

        if( scaleResult ) { // TODO: separate IP?
            ip.setMinAndMax(0, 255);
        }
        
        dst.setIP(ip);
    }

    void clipMoreThan( ImageProcessor ip, int clipped ) {

        for( int y = 0; y < ip.getHeight(); ++y ) {
            for( int x = 0; x < ip.getWidth(); ++x ) {
                int p = ip.get( x,y );
                if( p > threshold ) { // if zero, mask out
                    ip.set( x,y, clipped );
                }
            }
        }

    }

    void clipLessThan( ImageProcessor ip, int clipped ) {

        for( int y = 0; y < ip.getHeight(); ++y ) {
            for( int x = 0; x < ip.getWidth(); ++x ) {
                int p = ip.get( x,y );
                if( p < threshold ) { // if zero, mask out
                    ip.set( x,y, clipped );
                }
            }
        }

    }

}
