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

package au.com.nicta.ct.experiment.graphics.canvas.microwells;

import au.com.nicta.ct.experiment.graphics.canvas.microwells.concrete.CtHexWell;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.concrete.CtSquareWell;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtMicrowellsFactory {

    public static final int WELLS_GRID_SIZE = 9;

    public enum CtMicrowellsTypes {
        SQUARE,
        HEX,
    }

    public CtMicrowellsFactory() {

    }

    public Collection< String > getWellTypeDescriptions() {
        ArrayList< String > al = new ArrayList< String >();
        al.add( "Square" );
        al.add( "Hexagon" );
        return al;
    }

    public CtMicrowellsTypes getDefaultWellType() {
        return CtMicrowellsTypes.SQUARE;
    }

    public CtMicrowellsTypes getWellType( String description ) {
        if( description.equals( "Square" ) ) {
            return CtMicrowellsTypes.SQUARE;
        }
        else if( description.equals( "Hexagon" ) ) {
            return CtMicrowellsTypes.HEX;
        }
        else return null;
    }

    public String getWellTypeDescription( CtMicrowellsTypes mt ) {
        if( mt == CtMicrowellsTypes.SQUARE ) {
            return "Square";
        }
        else return "Hexagon";
    }

    public CtMicrowellsModel createModel( CtMicrowellsTypes mt, int rows, int cols ) {
//        if( mt == CtMicrowellsTypes.SQUARE ) {
//            return new CtSquareWellsModel( rows, cols );// CtMicrowellsTypes.SQUARE );
//        }
//        else {
//            return new CtHexWellsModel( rows, cols ); //CtMicrowellsTypes.HEX );
//        }
        return new CtMicrowellsModel( this, mt, rows, cols );
    }

    public CtMicrowell createMicrowell( CtMicrowellsTypes mt ) {
        if( mt == CtMicrowellsTypes.SQUARE ) {
            return new CtSquareWell();
        }
        else return new CtHexWell();
    }
//
//    public CtMicrowellsCanvasLayer createView( CtMicrowellsTypes mt, CtZoomCanvas zc, CtMicrowellsModel mm ) {
//
//        if( mt == CtMicrowellsTypes.SQUARE ) {
//            return new CtSquareWellsCanvasLayer( zc, (CtSquareWellsModel)mm );
//        }
//        else return new CtHexWellsCanvasLayer( zc, (CtHexWellsModel)mm );
//    }

}
