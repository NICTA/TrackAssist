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

import au.com.nicta.ct.orm.mvc.change.CtChangeModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author davidjr
 */
public class CtExportModel extends CtChangeModel {

    public SortedMap< String, CtDefaultExportProcess > _eps = new TreeMap< String, CtDefaultExportProcess >();
    
    public CtExportModel() {
        super( null );
    }

    public Collection< String > getNames() {
        return _eps.keySet();
    }

    public CtDefaultExportProcess getProcess( String name ) {
        return _eps.get( name );
    }

    public void addProcess( CtDefaultExportProcess ep ) {
        _eps.put( ep.getName(), ep );
    }

}
