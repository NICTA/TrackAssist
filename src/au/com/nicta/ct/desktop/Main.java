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

package au.com.nicta.ct.desktop;

import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.experiment.coordinates.CtCoordinatesController;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import au.com.nicta.ct.orm.mvc.images.CtImageSequenceFactory;
import au.com.nicta.ct.orm.mvc.pages.CtPageController;
import au.com.nicta.ct.orm.mvc.pages.CtPages;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtLoginPage;
import au.com.nicta.ct.orm.mvc.pages.concrete.CtPageFactories;
import au.com.nicta.ct.solution.lineage.CtLineageController;
import au.com.nicta.ct.solution.tracking.CtTrackingController;
import org.hibernate.Session;

/**
 * Entrypoint for program as a desktop application. (As opposed to web based
 * application)
 * 
 * @author davidjr
 */

public class Main {

    public static void main( String[] args ) {

        Session s = CtSession.Current();
        s.beginTransaction();

        CtPageFrame f = new CtPageFrame();
        CtImageSequenceFactory isf = new CtImageSequenceFactory(); // TODO fix pattern not consistent
        CtCoordinatesController cc = CtCoordinatesController.get( isf );
        CtTrackingController tc = CtTrackingController.get();
        CtLineageController lc = CtLineageController.get();
        CtPageController pc = new CtPageController();
        pc.addModelListener( f ); // the frame listens to page changes in the page-model, and displays them

        // Add the set of pages we support in this app:
        CtPages.put( "login", CtPageFactories.loginPageFactory() );
        CtPages.put( "logout", CtPageFactories.logoutPageFactory() );
        CtPages.put( "error", CtPageFactories.errorPageFactory() );
        CtPages.put( "import-images", CtPageFactories.importImagesPageFactory() );
        CtPages.put( "parse-filenames", CtPageFactories.parseFilenamesPageFactory() );
        CtPages.put( "create-experiment", CtPageFactories.createExperimentPageFactory() );
        CtPages.put( "select-experiment", CtPageFactories.selectExperimentPageFactory() );
        CtPages.put( "select-solution", CtPageFactories.selectSolutionPageFactory() );
        CtPages.put( "display-solution", CtPageFactories.displaySolutionPageFactory() );
        CtPages.put( "lineage-page", CtPageFactories.lineagePageFactory() );
        CtPages.put( "export", CtPageFactories.exportPageFactory() );
        CtPages.put( "edit-table", CtPageFactories.tableEditorPageFactory() );

        // HACK: Get rid of the login page as most people don't want it anymore:
        CtLoginPage.doLogin( "admin", "admin" );

        pc.transition(); // tell the page controller to go to the next page, which updates the frame... etc.
    }

}

