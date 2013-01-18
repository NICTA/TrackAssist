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

package au.com.nicta.ct.solution;

import au.com.nicta.ct.experiment.CtExperimentController;
import au.com.nicta.ct.experiment.CtExperimentListener;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import java.util.ArrayList;

/**
 *
 * @author davidjr
 */
public class CtSolutionController implements CtExperimentListener, CtSolutionListener {

//    public static void addSolutionListener( CtSolutionListener sl ) {
//        CtSolutionController ec = CtSolutionController.get();
//        ec.addSolutionListener( sl );
//    }

    public static CtSolutionController get() {
        CtSolutionController sc = (CtSolutionController)CtObjectDirectory.get( CtSolutionController.name() );
        if( sc == null ) {
            sc = new CtSolutionController();
        }
        return sc;
    }

    public static CtSolutions getSolutions() {
        CtSolutions s = (CtSolutions)CtObjectDirectory.get( "solution" );
        return s;
    }

    public static void set( CtSolutions s ) {

        CtSolutionController sc = get();

        sc.onSolutionChanged( s );
    }

    protected CtSolutionController() {
        CtExperimentController ec = CtExperimentController.get();
        ec.addExperimentListener( this );
        CtObjectDirectory.put( CtSolutionController.name(), this );
    }

    public void addSolutionListener( CtSolutionListener sl ) {
        _sls.add( sl );
        sl.onSolutionChanged( getSolutions() );
    }

    public static String name() {
        return "solution-controller";
    }

    @Override public void onExperimentChanged( CtExperiments e ) {//CtExperimentModel em ) {
        onSolutionChanged( null );
    }

    @Override public void onSolutionChanged( CtSolutions s ) {
        CtObjectDirectory.put( "solution", s );

        for( CtSolutionListener sl : _sls ) {
            sl.onSolutionChanged( s );
        }
    }

    protected ArrayList< CtSolutionListener > _sls = new ArrayList< CtSolutionListener >();

}
