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

package au.com.nicta.ct.solution.graphics.canvas.tools.annotations;

import au.com.nicta.ct.db.CtManualFlush;
import au.com.nicta.ct.orm.patterns.CtObjectDirectory;
import au.com.nicta.ct.db.CtSession;
import au.com.nicta.ct.db.hibernate.CtAnnotations;
import au.com.nicta.ct.db.hibernate.CtAnnotationsTypes;
import au.com.nicta.ct.db.hibernate.CtImages;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.solution.CtSolutionController;
import au.com.nicta.ct.solution.CtSolutionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JComboBox;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author davidjr
 */
public class CtAnnotationsController implements CtSolutionListener {//CtViewpointListener, MouseListener, MouseMotionListener {

    public static final int MODE_SELECT = 0;
    public static final int MODE_CREATE = 1;
    public static final int MODE_DELETE = 2;
    
//    protected CtZoomCanvas _zc;
//    protected CtCanvasLayer _cl;
    protected CtAnnotationsModel _am;
//    protected CtAnnotationsCanvasLayer _ap;

//    protected boolean _dragging = false;
//    protected int _xMousePressed = 0;
//    protected int _yMousePressed = 0;
//    protected int _dxScreen = 0;
//    protected int _dyScreen = 0;
//    protected CtAnnotations _selected;
    protected int _mode = MODE_SELECT;

    List< CtAnnotationsTypes > _types;
    int _currentTypeIdx = 0;

    public static CtAnnotationsController get() {
        CtAnnotationsController ac = (CtAnnotationsController)CtObjectDirectory.get( CtAnnotationsController.name() );

        if( ac == null ) {
            ac = new CtAnnotationsController();
        }

        return ac;
    }

    public static String name() {
        return "annotations-controller";
    }

    public CtAnnotationsController() {// CtZoomCanvas zc) {
        this( new CtAnnotationsModel() );
    }

    public CtAnnotationsController( CtAnnotationsModel am ) {// CtZoomCanvas zc) {

        _am = am;

        CtObjectDirectory.put( name(), this );

        CtSolutionController sc = CtSolutionController.get();
        sc.addSolutionListener( this );

        _types = loadTypes();
    }

    @Override public void onSolutionChanged( CtSolutions s ) {
        _am.setSolution( s );
    }

    public CtAnnotationsTypes getCurrentType() {
        return _types.get( _currentTypeIdx );
    }
    public int getCurrentTypeIdx() {
        return _currentTypeIdx;
    }

    public void setCurrentTypeIdx( int i ) {
        _currentTypeIdx = i;
    }

    final List<CtAnnotationsTypes> loadTypes() {
        Session s = CtSession.Current();
        CtManualFlush mf = new CtManualFlush(s);

        s.beginTransaction();

        Query q = s.createQuery(
                  " SELECT at"
                + " FROM CtAnnotationsTypes at" );

        List<CtAnnotationsTypes> l = (List<CtAnnotationsTypes>) q.list();

        s.getTransaction().commit();
        mf.restore();

        return l;
    }

    public CtAnnotationsModel getAnnotationsModel() {
        return _am;
    }

    public void setMode( int mode ) {
        _mode = mode;
    }

    public int getMode() {
        return _mode;
    }

    public void create( CtImages i, int x, int y, String input, CtAnnotationsTypes at ) {
        _am.create( i, x, y, input, at );
        _am.fireModelChanged();
    }

    public void remove( CtAnnotations a ) {
        if( a == null ) {
            return;
        }

        _am.remove( a );
        _am.fireModelChanged();
    }

    public void save( CtAnnotations a ) {
        if( a == null ) {
            return;
        }

        _am.save( a );
        _am.fireModelChanged();
    }

    public void removeRangeType( int startFrameIdx, int endFrameIdx, CtAnnotationsTypes at ) {
        _am.removeRangeType( startFrameIdx, endFrameIdx, at );
        _am.fireModelChanged();
    }

    public JComboBox createTypesCombo() {
        // insert all annotation types into combo
        final JComboBox cb = new JComboBox();

        for( CtAnnotationsTypes at : _types ) {
            cb.addItem(at.getValue());
        }

        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setCurrentTypeIdx(cb.getSelectedIndex());
            }
        });

        return cb;
    }
}
