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
public class CtInvertOperation extends CtImageOperation {

    CtImageResult src;
    double newMin;
    double newMax;

    public CtInvertOperation(CtImageResult src, double newMin, double newMax ) {
        super(src);
        this.src = src;
        this.newMin = newMin;
        this.newMax = newMax;
    }

    @Override protected void run() {
        ImageProcessor dstIP = src.getIP().duplicate();

        invert( dstIP, newMin, newMax );

        dst.setIP( dstIP );
    }

    protected void invert( ImageProcessor dst, double newMin, double newMax ) {

        double min = dst.getMin();
        double max = dst.getMax();
        double scale = -1.0 * ( newMax - newMin ) / ( max - min );
        double shift = newMax - min * scale;

        System.out.println( "InvertOp.scale: " + (scale) );
        System.out.println( "InvertOp.shift: " + (shift) );

        CtScaleOperation.scale( dst, scale, shift );
//        CtScaleOperation.scale( dst, -1, max );
    }

}
