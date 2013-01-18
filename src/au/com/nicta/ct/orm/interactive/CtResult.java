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

package au.com.nicta.ct.orm.interactive;

import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author davidjr
 */
public class CtResult {

    public String _message;
    public CtStatus _status = CtStatus.SUCCESS;

    public enum CtStatus {
        UNCHANGED,
        FAILURE,
        PARTIAL,
        SUCCESS
    }

    public CtResult( String message, CtStatus status ) {
        _message = message;
        _status = status;
    }

    public void showDialogExceptSuccess() {
        showDialog( false );
    }

    public void showDialog( boolean ignoreSuccess ) {

        String message = _message;

        if( message == null ) {
            message = "No information.";
        }

        switch( _status ) {
            case UNCHANGED: JOptionPane.showMessageDialog( CtPageFrame.find(), message, "No change", JOptionPane.WARNING_MESSAGE ); break;
            case FAILURE:   JOptionPane.showMessageDialog( CtPageFrame.find(), message, "Failed", JOptionPane.ERROR_MESSAGE ); break;
            case PARTIAL:   JOptionPane.showMessageDialog( CtPageFrame.find(), message, "Partial Success", JOptionPane.WARNING_MESSAGE ); break;
            case SUCCESS:   if( !ignoreSuccess ) JOptionPane.showMessageDialog( CtPageFrame.find(), message ); break;
        }
    }

    public static CtStatus outcome( CtStatus s1, CtStatus s2 ) {
        // generally, change any unchanged to a change if any happened.
        // change any success to a partial and partial to failure.
        if( s1 == null ) return s2;
        if( s2 == null ) return s1;

        switch( s1 ) {
            case UNCHANGED: {
                return s2;
            }
            case FAILURE: {
                return CtStatus.FAILURE; // can't get any worse
            }
            case PARTIAL: {
                switch( s2 ) {
                    case UNCHANGED:
                        return CtStatus.PARTIAL;
                    case FAILURE:
                        return CtStatus.FAILURE;
                    case PARTIAL:
                        return CtStatus.PARTIAL;
                    case SUCCESS:
                        return CtStatus.PARTIAL;
                }
                break;
            }
            case SUCCESS: {
                switch( s2 ) {
                    case UNCHANGED:
                        return CtStatus.PARTIAL;
                    case FAILURE:
                        return CtStatus.FAILURE;
                    case PARTIAL:
                        return CtStatus.PARTIAL;
                    case SUCCESS:
                        return CtStatus.SUCCESS;
                }
                break;
            }
        }
        return null;
    }

    public static CtResult combine( CtResult r1, CtResult r2 ) {
        if( r1 == null ) return r2;
        if( r2 == null ) return r1;

        CtStatus status = outcome( r1._status, r2._status );
        String message = r1._message + "," + r2._message;

        return new CtResult( message, status );
    }

    public static CtResult unchanged( String message ) {
        return new CtResult( message, CtStatus.UNCHANGED );
    }

    public static CtResult failure( String message ) {
        return new CtResult( message, CtStatus.FAILURE );
    }

    public static CtResult partial( String message ) {
        return new CtResult( message, CtStatus.PARTIAL );
    }

    public static CtResult success( String message ) {
        return new CtResult( message, CtStatus.SUCCESS );
    }

}
