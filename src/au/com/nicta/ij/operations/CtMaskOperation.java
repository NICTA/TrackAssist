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

import ij.process.ImageProcessor;

/**
 *
 * @author Alan
 */
public class CtMaskOperation extends CtImageOperation {

    CtImageResult src;
    CtImageResult mask;

    public CtMaskOperation(CtImageResult src, CtImageResult mask) {
        super(src);
        this.src = src;
        this.mask = mask;
    }

    @Override
    protected void run() {
        ImageProcessor dstIP = src.getIP().duplicate();

        applyMask(dstIP, mask.getIP());

        dst.setIP(dstIP);
    }

    protected void applyMask(ImageProcessor dst, ImageProcessor mask) {
        assert dst.getWidth()  == mask.getWidth();
        assert dst.getHeight() == mask.getHeight();

        for( int y = 0; y < dst.getHeight(); ++y ) {
            for( int x = 0; x < dst.getWidth(); ++x ) {
                if( mask.get(x, y) == 0 ) { // if zero, mask out
                    dst.set(x, y, 0);
                }
            }
        }

    }


}











