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
import au.com.nicta.ct.orm.mvc.wizard.*;

import java.util.List;
import java.util.ArrayList;



public class CtSetupModel extends CtWizardModel {

    public static final String  _imageSourcePageTitle    = "<html><h2>&nbsp; Experiment Set up : Step 1</h2></html>";
    public static final String  _imageNameParsePageTitle = "<html><h2>&nbsp; Experiment Set up : Step 2</h2></html>";
    public static final String  _experimentNamePageTitle = "<html><h2>&nbsp; Experiment Set up : Step 3</h2></html>";
    

    public String  experimentName;
    public int     experimentKey;
    public String  imageSourceDirectory;
    public String  experimentURI;

    public ArrayList< String >          imageFileNames;
    public List< CtImageFileNameParts > imageNameParts;

    public CtFileNameParsingTableModel fnptm;

    public boolean imageSourceDirectoryChanged;

    public CtSetupModel() {
        super();
        imageFileNames   = new ArrayList< String >();
        imageNameParts   = new ArrayList< CtImageFileNameParts >();
        fnptm            = null;
        
        imageSourceDirectory = "";
        imageSourceDirectoryChanged = true;

        experimentName = "";
    }

    @Override
    public boolean isDone() {
        if(getState()==CtWizardModel.STATE_DONE) {
            return true;
        }
        else{
           return false;
        }
    }

    @Override
    public boolean canCancel() {
        return true;
    }
}
