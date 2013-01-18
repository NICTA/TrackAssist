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

package au.com.nicta.ct.graphics.canvas.images;

import au.com.nicta.ct.graphics.canvas.CtCanvasLayer;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasLayerListener;
import au.com.nicta.ct.graphics.canvas.zoom.CtZoomCanvasPanel;
import au.com.nicta.ct.experiment.coordinates.viewpoint.ui.CtViewpointZoomCanvasPanelFactory;
import au.com.nicta.ct.orm.mvc.change.CtChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * DAVE: Lifetime changed to match 1x IRCL
 * @author nvo
 */
public class CtImageResultsSelectionTool extends JPanel implements CtChangeListener {//CtZoomCanvasLayerListener, CtChangeListener {

//    protected HashSet< CtImageResultsCanvasLayer > _hs = new HashSet< CtImageResultsCanvasLayer >();
    protected CtImageResultsCanvasLayer _ircl;
    protected JComboBox _list;
//    protected HashMap< String, JToggleButton > map;

//    CtImageResultsCanvasLayer _ircl;
    //    private CtZoomCanvasPanel _irp;

    public CtImageResultsSelectionTool( CtZoomCanvasPanel zcp ) {
//        _irp = irp;
        _ircl = CtImageResultPanelFactory.getImageResultsCanvasLayer( zcp.getZoomCanvas() );
        _ircl.addImageResultsSelectionChangedListener( this );
//        map = new HashMap<String, JToggleButton>();

        _list = new JComboBox();
//        _list.addItem("Original");

        refreshComboBox();

        _list.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSelectionChanged();
//                JComboBox cb = (JComboBox)e.getSource();
//                String selected = (String)cb.getSelectedItem();
//
//                if( selected.compareTo("Original") == 0 ) {
//                    _irp.setModified( null );//_irp.getOriginal());
//                    for ( String s : map.keySet() ) {
//                        JToggleButton bt = map.get( s );
//                        bt.setSelected(false);
//                    }
//                }
//                else {
//                    for ( String s : map.keySet() ) {
//                        JToggleButton bt = map.get( s );
//                        bt.setSelected(false);
//                    }
//                    JToggleButton bt = map.get(selected);
//                    bt.setSelected(false);
//                    bt.setSelected(true);
////                    _irp.setModified( _irp.getLayer( selected ) );
//                }
            }
        });

        JLabel label = new JLabel( "Layer:" );
        this.add( label );
        this.add( _list );
//        this.setBackground( Color.WHITE );
        setOpaque( false );

//        zcpf._canvasLayerListeners.add( this );
    }

//    @Override public void onCreateCanvasLayer( CtCanvasLayer cl ) {
//        if( cl instanceof CtImageResultsCanvasLayer ) {
//            CtImageResultsCanvasLayer ircl = (CtImageResultsCanvasLayer)cl;
//            ircl.addImageResultsChangedListener( this );
//            _hs.add( ircl );
//        }
//
//        refreshComboBox();
//    }
//
//    @Override public void onDeleteCanvasLayer( CtCanvasLayer cl ) {
//
//        if( cl instanceof CtImageResultsCanvasLayer ) {
//            CtImageResultsCanvasLayer ircl = (CtImageResultsCanvasLayer)cl;
//            ircl.removeImageResultsChangedListener( this );
//            _hs.remove( ircl );
//        }
//
//        refreshComboBox();
//    }

    public void onSelectionChanged() {
        String selected = (String)_list.getSelectedItem();

//        for( CtImageResultsCanvasLayer ircl : _hs ) {
            _ircl.selectImageResult( selected );
//        }
//        _ircl.selectImageResult( selected );

//                if( selected.compareTo("Original") == 0 ) {
//                    _irp.setModified( null );//_irp.getOriginal());
//                    for ( String s : map.keySet() ) {
//                        JToggleButton bt = map.get( s );
//                        bt.setSelected(false);
//                    }
//                }
//                else {
//                    for ( String s : map.keySet() ) {
//                        JToggleButton bt = map.get( s );
//                        bt.setSelected(false);
//                    }
//                    JToggleButton bt = map.get(selected);
//                    bt.setSelected(false);
//                    bt.setSelected(true);
////                    _irp.setModified( _irp.getLayer( selected ) );
//                }
    }

    @Override public void propertyChange( PropertyChangeEvent evt ) {
        refreshComboBox(); // called when eg someone has added a new type of image result
    }

    public void refreshComboBox() {
        _list.removeAllItems();

        HashSet< String > allKeys = new HashSet< String >();
        String selectedKey = null;

//        for( CtImageResultsCanvasLayer ircl : _hs ) {
            Collection< String > cs = _ircl.getImageResultKeys();

            for( String s : cs ) {
                allKeys.add( s );
            }

            selectedKey = _ircl.getSelectedKey(); // uh.. dunno how to handle different layers in different images..
//        }

        for( String s : allKeys ) {
            _list.addItem( s );
        }

        _list.setSelectedItem( selectedKey );//_ircl.getSelectedKey() );
    }

    public JComboBox getList() {
        return _list;
    }

//    public void setList( JComboBox _list ) {
//        this._list = _list;
//    }
//
//    public void add( String key, CtImageResultToggle irt ) {//JToggleButton bt ) {
//        map.put( key, irt.getButton() );//bt );
//        _list.addItem( key );
//    }
}
