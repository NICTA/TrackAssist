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

package au.com.nicta.ct.experiment.graphics.canvas.microwells.concrete;

import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowell;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsModel;


/**
 *
 * @author Alan
 */
public class CtSquareWell extends CtMicrowell {

    public CtSquareWell() {
        radius      = 30;
        angle       = 0;
        orientation = Math.PI / 4;
        sides       = 4;
        PER_SIDE_RADIANS = 2 * Math.PI / sides;
    }

    @Override public int getAnchorVertex0() {
        return 0;
    }

    @Override public int getAnchorVertex1() {
        return 2;
    }

    @Override public int getMidPointWall() {
        return 0;
    }

    @Override public void updateWells(
            CtMicrowellsModel mm,
            CtMicrowell prototype,
            int prototypeRow,
            int prototypeCol,
            double minDistanceBetweenWellWalls ) {

        mm.minDistanceBetweenWellWalls = minDistanceBetweenWellWalls;
        double distanceBetweenWellCentres = minDistanceBetweenWellWalls + prototype.getPerpendicularDistanceToWall()*2;
        double th = prototype.angle;

        int rows = mm.rows();
        int cols = mm.cols();

        for( int c = 0; c < cols; ++c ) {
            int cc = c - prototypeCol;
            double dx = (distanceBetweenWellCentres/2) * (cc * 2);

            for( int r = 0; r < rows; ++r ) {
                int rr = r - prototypeRow;
                double dy = (distanceBetweenWellCentres/2) * ( rr * 2);
                CtSquareWell w = (CtSquareWell)mm.get( r, c );
                w.copy(prototype);
                w._p.x += dx * Math.cos(th) - dy * Math.sin(th);
                w._p.y += dx * Math.sin(th) + dy * Math.cos(th);
            }
        }
    }
}
