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

package au.com.nicta.ct.solution.export;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import javax.swing.JFileChooser;

/**
 *
 * @author davidjr
 */
public class CtExportController {

    CtExportModel _em;

    public static CtExportController get() {
        CtExportController ec = (CtExportController)CtObjectDirectory.get( CtExportController.name() );

        if( ec == null ) {
            ec = new CtExportController();
            CtObjectDirectory.put( CtExportController.name(), ec );
        }

        return ec;
    }

    public CtExportController() {
        this( new CtExportModel() );
    }

    public CtExportController( CtExportModel em ) {
        this._em = em;
        CtObjectDirectory.put( name(), this );
    }

    public void add( CtDefaultExportProcess ep ) {
        _em.addProcess( ep );
        _em.fireModelChanged();
    }

    public static String name() {
        return "export-controller";
    }

    public CtExportModel getModel() {
        return _em;
    }
    
    public JFileChooser getFileChooser( String exportProcess ) {
        return _em.getProcess( exportProcess ).getFileChooser();
    }

    public String apply( CtSolutions s, String exportProcess, String filePath ) {
        CtDefaultExportProcess ep = _em.getProcess( exportProcess );
        return ep.apply( s, filePath );
    }
}
