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

import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;

/**
 *
 * @author Alan
 */
public abstract class CtGaussianBlurOperation extends CtImageOperation {

    protected abstract void blur( ImageProcessor dst, GaussianBlur gb );
    
    CtImageResult src;
    GaussianBlur gb = new GaussianBlur();

    public CtGaussianBlurOperation(CtImageResult src) {
        super(src);
        this.src = src;
    }

    @Override
    protected void run() {
        ImageProcessor dstIP = src.getIP().duplicate();
        
        blur( dstIP, gb );

        dst.setIP(dstIP);
    }

}