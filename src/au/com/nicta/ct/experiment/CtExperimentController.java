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

package au.com.nicta.ct.experiment;

import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.hibernate.CtExperiments;
import java.util.ArrayList;

/**
 *
 * @author davidjr
 */
public class CtExperimentController implements CtExperimentListener {

//    public static void addExperimentListener( CtExperimentListener el ) {
//        CtExperimentController ec = CtExperimentController.get();
//        ec.addExperimentListener( el );
//    }

    public static void set( CtExperiments e ) {
        CtExperimentController ec = get();

//        CtExperimentModel em = new CtExperimentModel( e, axis );

        ec.onExperimentChanged( e );

//        return em;
    }

    public static CtExperiments getExperiments() {
        CtExperiments s = (CtExperiments)CtObjectDirectory.get( "experiment" );
        return s;
    }

    public static CtExperimentController get() {
        CtExperimentController ec = (CtExperimentController)CtObjectDirectory.get( CtExperimentController.name() );
        if( ec == null ) {
            ec = new CtExperimentController();
        }
        return ec;
    }

//    public static CtExperimentModel getModel() {
//        return CtExperimentModel.get();
//    }

    protected CtExperimentController() {
        CtObjectDirectory.put( CtExperimentController.name(), this );
    }

    public void addExperimentListener( CtExperimentListener el ) {
        _els.add( el );
        el.onExperimentChanged( getExperiments() );
    }

    public static String name() {
        return "experiment-controller";
    }

    @Override public void onExperimentChanged( CtExperiments e ) {//Model em ) {

//        CtObjectDirectory.put( CtExperimentModel.name(), em );
        CtObjectDirectory.put( "experiment", e );//em._e );

        for( CtExperimentListener el : _els ) {
            el.onExperimentChanged( e );
        }
    }

    protected ArrayList< CtExperimentListener > _els = new ArrayList< CtExperimentListener >();

}
