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

package au.com.nicta.tk;

import au.com.nicta.tk.tree.TkStringKey;
import java.util.List;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

/**
 *
 * @author Alan
 */
public class TkCell {

    public static TkStringKey<TkCell> name = TkStringKey.newInstance("CtCell");

    // centroid
    public double cx;
    public double cy;
    public double radius;

    public static Matrix getCentroidMatrix( List<TkCell> cells ) {
        if( cells.isEmpty() ) {
            return MatrixFactory.EMPTYMATRIX;
        }

        Matrix m = MatrixFactory.dense( ValueType.DOUBLE, 2, cells.size() );

        int i = 0;
        for( TkCell c : cells ) {
            m.setAsDouble( c.cx, 0, i );
            m.setAsDouble( c.cy, 1, i );
            ++i;
        }

        return m;
    }

}