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

import au.com.nicta.tk.TkArray;

/**
 *
 * @author Alan
 */
public class TkHungarianAlgorithmAssocOptimizer implements TkAssocOptimizer {

    TkHungarianAlgorithm hungAlg = new TkHungarianAlgorithm();

    public TkAssocMatrix getAssocMatrix( TkAssocScoreMatrix assoc ) {

        int numTracks     = (int)assoc.numTracks;
        int numDetections = (int)assoc.numDetections;
        
        TkAssocMatrix assocMatrix = new TkAssocMatrix(numTracks, numDetections);

        if(    numTracks <= 0
            || numDetections <= 0 ) {
            return assocMatrix;
        }

        // score: (tracks x detections)
        double[][] hungAlgScores = assoc.scores.toDoubleArray();

        TkArray.replace( hungAlgScores, Double.NaN, Double.NEGATIVE_INFINITY );

        TkHungarianAlgorithm.Mode mode =   assoc.isDistance
                                         ? TkHungarianAlgorithm.Mode.MINIMIZE
                                         : TkHungarianAlgorithm.Mode.MAXIMIZE;

        hungAlg.run( hungAlgScores, mode );

        boolean[][] assigned = hungAlg.assigned;

        for( int tIdx = 0; tIdx < numTracks; ++tIdx ) {
            int getMatch = -1;
            int dIdx = 0;
            while(    getMatch == -1
                   && dIdx < numDetections ) {

                if(    assigned[ tIdx ][ dIdx ]
                    && assoc.scores.getAsDouble( tIdx, dIdx ) != 0 ) {

                    getMatch = 1;
                    assocMatrix.trackToDetection[ tIdx ] = dIdx;
                    assocMatrix.detectionToTrack[ dIdx ] = tIdx;

                    ++assocMatrix.totalMatched;
                }

                ++dIdx;
            }
        }

        return assocMatrix;
    }

}
