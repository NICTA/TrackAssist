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

package au.com.nicta.ct.orm.mvc.change;

/**
 *
 * @author davidjr
 */
public abstract class CtTransferableChangeListener implements CtChangeListener {

    String _event;
    CtChangeModel _cm;

    public CtTransferableChangeListener( CtChangeModel cm ) {
        listenTo( cm );
    }

    public CtTransferableChangeListener( String event, CtChangeModel cm ) {
        _event = event;

        listenTo( cm );
    }

    public void listenTo( CtChangeModel cm ) {
        if( _cm != null ) {
            if( _event != null ) {
                _cm.removeListener( _event, this );
            }
            else {
                _cm.removeListener( this );
            }
        }

        _cm = cm;

        if( _cm != null ) {
            if( _event != null ) {
                _cm.addListener( _event, this );
            }
            else {
                _cm.addListener( this );
            }
            propertyChange( null );
        }
    }

//    @Override public void propertyChange( PropertyChangeEvent evt ) {
//
//    }

}
