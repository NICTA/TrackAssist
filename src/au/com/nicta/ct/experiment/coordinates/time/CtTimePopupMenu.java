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

package au.com.nicta.ct.experiment.coordinates.time;

import au.com.nicta.ct.orm.mvc.images.CtImageSelectionController;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *    Show
 *    -------------------
 *    Add annotation
 *    Edit annotation[s]
 *    Clear annotation[s]
 *    -------------------
 *    Mark start
 *    Mark end
 *
 * @author davidjr
 */
public class CtTimePopupMenu extends JPopupMenu { //implements MouseListener {

//    JMenuItem _show;
//    JMenuItem _addAnnotation;
//    JMenuItem _editAnnotation;
//    JMenuItem _clearAnnotation;
    JMenuItem _select1;
    JMenuItem _select2;
    JMenuItem _selectC;

    public CtTimePopupMenu( CtImageSelectionController isc ){
//        _show = new JMenuItem( "Show" );
//        _addAnnotation = new JMenuItem( "Add annotation" );
//        _editAnnotation = new JMenuItem( "Edit annotation" );
//        _clearAnnotation = new JMenuItem( "Clear annotation" );
        _select1 = new JMenuItem( "Select start" );
        _select2 = new JMenuItem( "Select end" );
        _selectC = new JMenuItem( "Clear selection" );

//        add( _show );
//        addSeparator();
//        add( _addAnnotation );
//        add( _editAnnotation );
//        add( _clearAnnotation );
//        addSeparator();
        add( _select1 );
        add( _select2 );
        addSeparator();
        add( _selectC );

//        _show.addActionListener( isc );
//        _addAnnotation.addActionListener( isc );
//        _editAnnotation.addActionListener( isc );
//        _clearAnnotation.addActionListener( isc );

        _select1.addActionListener( isc );
        _select2.addActionListener( isc );
        _selectC.addActionListener( isc );
    }

//    public void mousePressed(MouseEvent e) {
//        System.out.println( "presseded"+e) ;
//    }
//    public void mouseReleased(MouseEvent e) {}
//    public void mouseEntered(MouseEvent e) {}
//    public void mouseExited(MouseEvent e) {}
//    public void mouseClicked(MouseEvent e) {
//        System.out.println( "clicked"+e) ;
//    }
}
