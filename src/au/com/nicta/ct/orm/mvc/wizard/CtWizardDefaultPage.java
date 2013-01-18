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

package au.com.nicta.ct.orm.mvc.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Has a banner at the top
 * @author davidjr
 */
public class CtWizardDefaultPage extends JPanel {

//    protected CtImagePanel _bannerPanel;
////    protected JPanel _bannerPanel;
//    protected JPanel _body;
//
//    pagelayout.Column _column;
//    pagelayout.Row _headRow;
//    pagelayout.Row _bodyRow;


    public CtWizardDefaultPage( String title ) {
        super( new BorderLayout() );


      //  setBorder(BorderFactory.createTitledBorder( "arrgh" ) );
    }

    public void initComponents() {
        setOpaque( false );

////        JLabel head = new JLabel( "<html><h1>head</h1></html>" ); // TODO get HTML from database
//        JLabel foot = new JLabel( "<html><h1>CellTrack foot</h1></html>" );
//        foot.setIcon( new ImageIcon( "./artwork/nicta/nicta_icon.png" ) );
//        JLabel body = new JLabel( "body" );
//
//        foot.setBackground( Color.WHITE );
//
////        head.setOpaque( true );
//        foot.setOpaque( true );

        JComponent head = initHead();
        JComponent foot = initFoot();
        JComponent body = initBody();

        add( head, BorderLayout.NORTH );
        add( body, BorderLayout.CENTER );
        add( foot, BorderLayout.SOUTH );
    }
    
    protected JComponent initHead() {
        JLabel head = new JLabel( "<html><h1>head</h1></html>" ); // TODO get HTML from database
        head.setBackground( Color.WHITE );
        head.setOpaque( true );
        return head;
    }

    protected JComponent initFoot() {
        JLabel foot = new JLabel( "<html><h1>CellTrack foot</h1></html>" );
        foot.setBackground( Color.WHITE );
        foot.setOpaque( true );
        foot.setIcon( new ImageIcon( "./artwork/nicta/nicta_icon.png" ) );
        return foot;
    }

    protected JComponent initBody() {
        JLabel body = new JLabel( "body" );
        return body;
    }
}

//        super( new BorderLayout() );
//        super( new GridBagLayout() );
//
//        GridBagConstraints gbc = new GridBagConstraints();
//
//        gbc.gridwidth = 2;
//        gbc.gridheight = 2;
        
//        try {
//            _bannerPanel = new CtImagePanel( image, "banner panel head" );
//_bannerPanel.setBorder(BorderFactory.createTitledBorder( "arrgh" ) );
//            add( _bannerPanel, java.awt.BorderLayout.NORTH );
////            gbc.gridx = 0;
////            gbc.gridy = 0;
////            add( _bannerPanel, gbc );
////            add( _bannerPanel, gbc );
////            add( _bannerPanel._html, gbc );
//        }
//        catch( IOException ioe ) {
//            System.err.println( "Couldn't load banner image." );
//            System.err.println( ioe );
//        }
//
//        JLabel l = new JLabel( "banner panel body" );
////        gbc.gridx = 1;
////        gbc.gridy = 1;
//
//        add( l, java.awt.BorderLayout.CENTER );
//        add( l, gbc );

//        _headRow = new Row( Row.NO_ALIGNMENT, Row.CENTER, _bannerPanel );
//        _bodyRow = new Row( Row.CENTER, Row.NO_ALIGNMENT );
//
//        _bodyRow.linkHeight( _bannerPanel,0.2,this );
//
// // Create the PageLayout and set it to be the container's layout.
//        _column = new Column( _headRow, _bodyRow );
//        _column.createLayout( this );
//    }
//
//
//}
