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

package au.com.nicta.ct.solution.tracking.jipda;

import au.com.nicta.ct.solution.graphics.canvas.tools.detections.CtCellEditorTable;
import au.com.nicta.tk.TkAssocResult;
import au.com.nicta.tk.TkAssocScoreMatrix;
import au.com.nicta.tk.TkCell;
import au.com.nicta.tk.TkDetection;
import au.com.nicta.tk.TkDetections;
import au.com.nicta.tk.TkLJIPDAForcedAssociator;
import au.com.nicta.tk.TkLJIPDAState;
import au.com.nicta.tk.TkMatrixUtil;
import au.com.nicta.tk.TkTrack;
import au.com.nicta.tk.TkTrackElement;
import au.com.nicta.tk.TkTracks;
import au.com.nicta.tk.tree.TkStringKey;
import javax.swing.CellEditor;
import org.ujmp.core.doublematrix.calculation.entrywise.basic.Power;

/**
 *
 * @author alan
 */
public class CtLJIPDAAssociator extends TkLJIPDAForcedAssociator {

    public CtLJIPDAAssociator(TkStringKey<TkLJIPDAState> keyState) {
        super(keyState);
    }

    @Override
    public TkAssocResult associate(TkTracks tracks, TkDetections detections) {
        TkAssocScoreMatrix scores = getAssocScoreMatrix( tracks, detections );
        System.out.println( "scores: \n" + TkMatrixUtil.format( "%f ", scores.scores ) );

        // add cell size into likelihood measurements
//        addCellSizeLikelihood(scores, tracks, detections);

        return associate(tracks, detections, scores);
    }

    public void addCellSizeLikelihood(TkAssocScoreMatrix scores, TkTracks tracks, TkDetections detections) {

        final double STD = 4;

        for( int i = 0; i < tracks.size(); ++i ) {
            TkTrack t = tracks.get(i);
            TkDetection d = t.getLastNonNullDetection();
            TkCell c = d.find(TkCell.name);
//            if( c == null ) {
//                throw new RuntimeException();
//            }
            double d1Size = c.radius;

            for( int j = 0; j < detections.size(); ++j ) {
                TkDetection d2 = detections.get(j);
                double d2Size = d2.find(TkCell.name).radius;
                double sizeDiff = d1Size - d2Size;
                double w = Math.exp( -1 * (sizeDiff/STD) * (sizeDiff/STD)  );
                double s = scores.scores.getAsDouble(i, j);
                scores.scores.setAsDouble(s*w, i, j);
            }
        }
        
    }
}
