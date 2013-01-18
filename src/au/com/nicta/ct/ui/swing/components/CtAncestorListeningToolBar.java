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

package au.com.nicta.ct.ui.swing.components;

import java.awt.FlowLayout;
import javax.swing.JToolBar;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author davidjr
 */
public class CtAncestorListeningToolBar extends JToolBar implements AncestorListener {

    public CtAncestorListeningToolBar( String name ) {
        super( name, JToolBar.HORIZONTAL );
        setLayout( new FlowLayout() );
        addAncestorListener( this );
    }

    public CtAncestorListeningToolBar( String name, int orientation ) {
        super( name, orientation );
        addAncestorListener( this );
    }

    // Called when the source or one of its ancestors is made visible either by setVisible(true) being called or by its being added to the component hierarchy.
    public void ancestorAdded( AncestorEvent event ) {
        setVisible( true );
    }

    // Called when either the source or one of its ancestors is moved.
    public void ancestorMoved( AncestorEvent event ) {}

    // Called when the source or one of its ancestors is made invisible either by setVisible(false) being called or by its being remove from the component hierarchy.throws
    public void ancestorRemoved( AncestorEvent event ) {
        setVisible( false );
    }

}
