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

package au.com.nicta.ct.orm.mvc.pages.concrete;

import au.com.nicta.ct.experiment.setup.pages.CtCreateExperimentPage;
import au.com.nicta.ct.experiment.setup.pages.CtImportImagesPage;
import au.com.nicta.ct.experiment.setup.pages.CtParseFilenamesPage;
import au.com.nicta.ct.experiment.CtSelectExperimentPage;
import au.com.nicta.ct.orm.patterns.CtAbstractFactory;
import au.com.nicta.ct.orm.mvc.pages.CtPage;
import au.com.nicta.ct.solution.pages.CtDisplaySolutionPage;
import au.com.nicta.ct.solution.pages.CtSelectSolutionPage;
import au.com.nicta.ct.solution.export.CtExportPage;
import au.com.nicta.ct.solution.lineage.CtLineagePage;

/**
 *
 * @author davidjr
 */
public class CtPageFactories {

    public static CtAbstractFactory< CtPage > errorPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtMessagePage( "error", "404 Page not found", "There is a configuration error and the requested page is not available." );
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > loginPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtLoginPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > logoutPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtLogoutPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > tableEditorPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtTableEditorPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > selectSolutionPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtSelectSolutionPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > selectExperimentPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtSelectExperimentPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > displaySolutionPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtDisplaySolutionPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > createExperimentPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtCreateExperimentPage();
            }
        }
        return new CtPageFactory();
    }
    
    public static CtAbstractFactory< CtPage > importImagesPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtImportImagesPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > parseFilenamesPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtParseFilenamesPage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > lineagePageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtLineagePage();
            }
        }
        return new CtPageFactory();
    }

    public static CtAbstractFactory< CtPage > exportPageFactory() {
        class CtPageFactory implements CtAbstractFactory< CtPage > {
            public CtPage create() {
                return new CtExportPage();
            }
        }
        return new CtPageFactory();
    }
}
