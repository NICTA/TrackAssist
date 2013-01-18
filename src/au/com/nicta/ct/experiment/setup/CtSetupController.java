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

package au.com.nicta.ct.experiment.setup;

import au.com.nicta.ct.experiment.setup.util.CtImageFileNameParts;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtCoordinatesTypes;
import au.com.nicta.ct.orm.mvc.CtController;

import java.util.Collection;
import java.util.List;

public class CtSetupController extends CtController {

    protected CtSetupController() {
        super( new CtSetupModel(), null );
    }

    protected static CtSetupController _sc;

    public static CtSetupController instance() {
        if( _sc == null ) {
            _sc = new CtSetupController();
        }
        return _sc;
    }

    public void clear() {
        _m = new CtSetupModel(); // forget everything
    }

    public boolean createImageNameParts( CtFileNameParsingTableModel parsingTableModel ) {

        CtSetupModel sm = ( CtSetupModel )_m;
        
        List< CtCoordinatesTypes > coordinateTypes = CtSession.getObjects( "from CtCoordinatesTypes" );
        if( coordinateTypes.isEmpty() ) {
            parsingTableModel.showError(  "Error in retrieving coordinate types from database" );
            return false;
        }

        int tableRows = parsingTableModel.getRowCount();
        if( tableRows <= 0 ) {
            return false;
        }

        sm.imageNameParts.clear();

        for( int rowIndex=0; rowIndex < tableRows ; rowIndex++ ) {

            CtImageFileNameParts imCP = new CtImageFileNameParts();

            imCP.coordinateName       = ( String )  parsingTableModel.coordinates        .get( rowIndex );
            imCP.coordinatePartString = ( String )  parsingTableModel.parts              .get( rowIndex );
            imCP.dataType             = ( String )  parsingTableModel.coordinateDataTypes.get( rowIndex );
            imCP.isFixedWidth         = ( Boolean ) parsingTableModel.choiceFixedWidth   .get( rowIndex );
            imCP.widthDelimeterString = ( String )  parsingTableModel.widthDelimeter     .get( rowIndex );

            if( imCP.isFixedWidth ) {
                if( Integer.parseInt( imCP.widthDelimeterString ) > ( imCP.coordinatePartString ).length() ) {
                    imCP.widthDelimeterString = Integer.toString( ( imCP.coordinatePartString ).length() );
                }
            }
            sm.imageNameParts.add( imCP );
        }
        return true;
    }

    public String getSourceDirectory() {
        CtSetupModel sm = ( CtSetupModel ) _m;
        return sm.imageSourceDirectory;
    }

    public void setSourceDirectory( String s ) {
        CtSetupModel sm = ( CtSetupModel ) _m;

        if( ( sm.imageSourceDirectory ).equals( s ) ) {
            setImageSourceDirectoryChanged( false );
        }
        else {
            setImageSourceDirectoryChanged( true );
        }
        
        sm.imageSourceDirectory = s;
    }

    public void setExperimentName( String s ) {
        CtSetupModel sm = ( CtSetupModel ) _m;
        sm.experimentName = s;
    }

    public String getExperimentName() {
        CtSetupModel sm = ( CtSetupModel ) _m;
        return sm.experimentName;
    }

    public void setFileNames( Collection< String > filenames ) {
        CtSetupModel sm = ( CtSetupModel ) _m;
        sm.imageFileNames.clear();
        sm.imageFileNames.addAll( filenames );
    }

    public Collection< String > getFileNames() {
        CtSetupModel sm = ( CtSetupModel ) _m;
        return sm.imageFileNames;
    }

    public void setParsingTableModel( CtFileNameParsingTableModel fnptm )
    {
        CtSetupModel sm = ( CtSetupModel ) _m;
        sm.fnptm = fnptm;
    }

    public CtFileNameParsingTableModel getParsingTableModel( )
    {
        CtSetupModel sm = ( CtSetupModel ) _m;
        return sm.fnptm;
    }

    public boolean isImageSourceDirectoryChanged() {
        CtSetupModel sm = ( CtSetupModel ) _m;
        return sm.imageSourceDirectoryChanged;
    }

    public void setImageSourceDirectoryChanged( boolean c) {
        CtSetupModel sm = ( CtSetupModel ) _m;
        sm.imageSourceDirectoryChanged = c;
    }

}


