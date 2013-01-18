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

package au.com.nicta.ct.experiment.graphics.canvas.microwells;

import au.com.nicta.ct.ui.swing.mdi.CtDockableWindowGrid;
import au.com.nicta.ct.ui.style.CtConstants;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvas;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.experiment.graphics.canvas.microwells.CtMicrowellsFactory.CtMicrowellsTypes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author davidjr
 */
public class CtMicrowellTools extends JPanel implements ActionListener {

    JComboBox _selectedRow;
    JComboBox _selectedCol;
    JComboBox _wellTypes;

    JButton   _show;

    CtDockableWindowGrid _dwg;
    Collection< String > _windowTypes;

    public CtMicrowellTools( CtDockableWindowGrid dwg, Collection< String > windowTypes ) {// CtZoomCanvas zc ) {//, CtMicrowellsModel mm ) {
        super();

        _dwg = dwg;
        _windowTypes = windowTypes;

        CtMicrowellsController mc = CtMicrowellsController.get();
        CtMicrowellsFactory mf = mc.getMicrowellsFactory();
        Collection< String > cs = mf.getWellTypeDescriptions();

        _selectedRow = new JComboBox();
        _selectedCol = new JComboBox();
        _wellTypes   = new JComboBox( cs.toArray() );

        setLayout( new BorderLayout() );
        setBackground( CtConstants.NictaYellow );

        JPanel upper = new JPanel();
        BoxLayout bl = new BoxLayout( upper, BoxLayout.Y_AXIS );//BoxLayout.PAGE_AXIS );
        upper.setLayout( bl );
        upper.setOpaque( false );

//        JLabel title = new JLabel( CtApplication.h3( " Microwells" ) );
//        title.setMaximumSize( title.getPreferredSize() );

        JPanel type = new JPanel( new FlowLayout() );
        type.setOpaque( false );
        type.add( new JLabel( "Set type" ) );
        type.add( _wellTypes );

        JPanel row = new JPanel( new FlowLayout() );
        row.setOpaque( false );
//        row.add( new JLabel( "Row" ) );
        row.add( _selectedCol );
        row.add( _selectedRow );

        _show = new JButton( "Show" );
//        _show.setMaximumSize( _show.getPreferredSize() );
        row.add( _show );
        
//        title.setAlignmentX( Component.CENTER_ALIGNMENT );
//        type .setAlignmentX( Component.CENTER_ALIGNMENT );
//        row  .setAlignmentX( Component.CENTER_ALIGNMENT );
//        _show .setAlignmentX( Component.CENTER_ALIGNMENT );

//        upper.add( title );
        upper.add( type );
        upper.add( row );
//        upper.add( _show );

        add( upper, BorderLayout.NORTH );

        _wellTypes.setSelectedItem( mf.getWellTypeDescription( mc.getMicrowellsModel().getType() ) );

        _show.addActionListener( this );
        _wellTypes.addActionListener( this );

        int rows = CtMicrowellsFactory.WELLS_GRID_SIZE;//_mm.rows();
        int cols = CtMicrowellsFactory.WELLS_GRID_SIZE;//_mm.cols();

        for( int r = 0; r < rows; ++ r ) {
            _selectedRow.addItem( String.valueOf( r + 1 ) );
        }

        char letter = 'A';

        for( int c = 0; c < cols; ++c ) {
            _selectedCol.addItem( String.valueOf( letter ) );
            ++letter;
        }

//        // revalidate to fix the layout when undock any toolbar
//        this.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                getRootPane().revalidate();
//            }
//        });

        // Now create a default model:
//        changeWellType( (String)_wellTypes.getSelectedItem() );//_wellTypes.getActionCommand() );
    }

    protected void changeWellType( String wellType ) {

        CtMicrowellsController mc = CtMicrowellsController.get();
        CtMicrowellsFactory mf = mc.getMicrowellsFactory();
        CtMicrowellsTypes mt = mf.getWellType( wellType );

        mc.setMicrowellType( mt );
    }

    @Override public void actionPerformed( ActionEvent ae ) {

        if( ( ae.getSource() ).equals( _wellTypes ) ) {
            changeWellType( (String)_wellTypes.getSelectedItem() );
        }
        else if( ( ae.getSource() ).equals( _show ) ) {

            CtMicrowellsController mc = CtMicrowellsController.get();
            CtMicrowellsModel mm = mc.getMicrowellsModel();

            String sCol = (String)_selectedCol.getSelectedItem();
            String sRow = (String)_selectedRow.getSelectedItem();

            Rectangle2D boundingBox = mm.find( sCol+sRow ).getBoundingBox();

            Collection< CtZoomCanvasPanel > czcp = CtViewpointZoomCanvasPanelFactory.find( _dwg, _windowTypes );

            for( CtZoomCanvasPanel zcp : czcp ) {
                CtZoomCanvas zc = zcp.getZoomCanvas();
                zc.zoomNaturalWindowAround( boundingBox, 0.1 );
            }
        }
    }
}
