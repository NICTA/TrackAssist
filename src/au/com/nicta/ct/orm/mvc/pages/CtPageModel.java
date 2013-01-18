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

package au.com.nicta.ct.orm.mvc.pages;

import au.com.nicta.ct.db.CtKeyValueProperties;
import au.com.nicta.ct.orm.mvc.CtModel;
import au.com.nicta.ct.ui.swing.components.CtPageFrame;

/**
 *
 * @author davidjr
 */
public class CtPageModel extends CtModel {

    public CtPageGraph _pg;
    public CtPage _p;
    public CtPageFrame _f;

    public CtPageModel() {
        _pg = new CtPageGraph();
    }

    public CtPage getPage() {
        return _p;
    }
    
    public void transition( String key ) { // go direct to this page
        if( _p != null ) {
            _p.onExit();
        }

        _p = _pg.transition( key );
    }

    public void transition() {
        if( _p == null ) {
            String key = CtKeyValueProperties.getValue( CtPageGraph.DEFAULT_PAGE_PROPERTY_KEY );
            _p = _pg.transition( key );
        }
        else {
            _p.onExit();
            _p = _pg.transition( _p );
        }

        if( _p == null ) {
            String key = CtKeyValueProperties.getValue( CtPageGraph.ERROR_PAGE_PROPERTY_KEY );
            _p = _pg.transition( key );
        }
    }

}
