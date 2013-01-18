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

package au.com.nicta.ij.operations.segmentation;

import au.com.nicta.ij.operations.CtImageOperation;
import au.com.nicta.ij.operations.CtImageResult;
import ij.process.ImageProcessor;

/**
 *
 * @author Alan
 */
public class CtWatershedOperation extends CtImageOperation {

    CtImageResult src;
    CtImageResult mask;
    CtImageResult filter;
    CtLabelledImage lm;

    int maskValue = 0;
    int filterThreshold = 0;
//    int background = 0;
    int minSize = 1;
    int minDist = 2;

    public CtWatershedOperation( CtImageResult src, int minSize ) {//int background, int minSize ) {
        super(src);
        this.src = src;
//        this.background = background;
        this.minSize = minSize;
    }

    public void setMask( CtImageResult mask, int maskValue ) {
        this.mask = mask;
        this.maskValue = maskValue;
    }

    public void setFilter( CtImageResult filter, int threshold ) {
        this.filter = filter;
        this.filterThreshold = threshold;
    }

    public CtLabelledImage getLabelledImage() {
        return lm;
    }

    @Override protected void run() {
        ImageProcessor srcIP = src.getIP();

        ImageProcessor maskIP = null;

        if( mask != null ) {
            maskIP = mask.getIP();
        }

        lm = CtWatershed.watershed( srcIP, maskIP, maskValue );//background );
        lm.mergeAdjacentMinima  ( minDist );
        lm.filterSmallComponents( minSize );

        if( filter != null ) {
            lm.filterComponentsWithMask( filter.getIP(), filterThreshold );
        }
        
        dst.setIP( lm.segmented );
//        lm.showDirections( dst );
//        lm.showMinima    ( dst );
//        lm.showSegmented ( dst );
    }
}
