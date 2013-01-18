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
public class CtLutOperation extends CtImageOperation {

    CtImageResult src;
    double min = 0;
    double max = 0;
    int[] lut;

    public CtLutOperation(CtImageResult src) {
        super(src);
        this.src = src;
    }

    public void setSrc(CtImageResult src) {
        clearOperands();
        addOperand(src);
        this.src = src;
    }

    public CtImageResult getSrc() {
        return src;
    }

    public void setMinMax(double min, double max) {
        this.min = min;
        this.max = max;
        setChanged();
    }

    public void setLut(int[] lut) {
        this.lut = lut;
        setChanged();
    }

    @Override
    protected void run() {
//        System.out.println("Running: CtLutOperation");
        ImageProcessor ip = src.getIP().duplicate();
        if( lut != null ) {
            ip.applyTable( lut );
        }
        if( !(min==0 && max==0) ) {
            ip.setMinAndMax(min, max);
        }
        dst.setIP(ip);
    }

}












