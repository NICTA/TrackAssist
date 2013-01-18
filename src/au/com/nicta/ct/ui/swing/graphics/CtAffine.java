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

package au.com.nicta.ct.ui.swing.graphics;

import java.awt.geom.AffineTransform;

/**
 *
 * @author Alan
 */
public class CtAffine {

    public enum QuadrantAngle {
        DEG_0,
        DEG_90,
        DEG_180,
        DEG_270
    }


    public static void createQuadrantAffineTransform(
            AffineTransform aff,
            boolean mirrorLeftRight,
            QuadrantAngle rotation,
            int widthAfterRotation,
            int heightAfterRotation ) {

        // remember last added is applied first:
        // aff.xxx(tx)  <==>  aff = aff * Tx

        aff.setToIdentity();

        switch( rotation ) {
            case DEG_0:
                // nothing
                break;
            case DEG_90:
                aff.translate( widthAfterRotation, 0 );
                aff.rotate( 0, 1 ); // rotate 90 degrees
                break;
            case DEG_180:
                aff.translate( widthAfterRotation, heightAfterRotation );
                aff.rotate( -1, 0 ); // rotate 180 degrees
                break;
            case DEG_270:
                aff.translate( 0, heightAfterRotation );
                aff.rotate( 0, -1 ); // rotate 270 degrees
                break;
            default:
                System.out.print("Bad rotation");
        }

        if( mirrorLeftRight ) { // mirror left-right
            aff.translate( widthAfterRotation, 0 );
            aff.scale(-1, 1);
        }
    }

}
