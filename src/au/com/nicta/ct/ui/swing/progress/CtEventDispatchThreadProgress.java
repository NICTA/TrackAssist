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

package au.com.nicta.ct.ui.swing.progress;

import au.com.nicta.ct.ui.swing.components.CtPageFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 *
 * @author davidjr
 */
public abstract class CtEventDispatchThreadProgress implements Runnable {

    public static final int DEFAULT_DELAY_MSEC = 500;

    JDialog _d;
    JProgressBar _pb;
    String _title;
    String _id;
    int _delayMsec;

    int _next = 0;
    boolean _first = false;

    public CtEventDispatchThreadProgress( String title, String id, int delayMsec ) {
        _title = title;
        _id = id;
        _delayMsec = delayMsec;
    }

    public CtEventDispatchThreadProgress( String title, String id ) {
        this( title, id, DEFAULT_DELAY_MSEC );
    }

    public String getID() {
        return _id;
    }

    public void enqueue() {
        CtEventDispatchThreadQueue.start( this );
    }

    public void start() {
        _next = 0;
        _pb = new JProgressBar(_next, getLength() );
        _pb.setValue(_next);

        _first = true;

        // display in modal dialog
        Object[] messageList = {new JLabel(_title), _pb};
        // create a buttonless dialog
        JOptionPane op = new JOptionPane(
                messageList,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] {},
                null);

        _d = op.createDialog(CtPageFrame.find(), "Progress");
        _d.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // schedule the processing task
        runAgain();

        if( _delayMsec == 0 ) { // then show immediately
            show();
        }
        else {
            // put delay in showing the dialog otherwise it's annoying that it pops
            // up all the time.
            Timer timer = new Timer(_delayMsec, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    show();
                }
            });
            timer.setRepeats(false); // one shot
            timer.start();
        }
    }

    public abstract int getLength();
    public abstract void doStep( int step );

    void show() {
        if( !isComplete() ) {
            CtPageFrame.showWaitCursor();
            _d.setVisible(true);
        }
    }


    public boolean isComplete() {
        int length = getLength();

        if( _next < length ) {
            return false;
        }

        return true;
    }

    protected void onStepComplete() {
        ++_next;

        if( _pb != null ) {
            _pb.setValue( _next );
        }

        runAgain();
    }

    protected void runAgain() {
        if( isComplete() ) {
            CtPageFrame.showDefaultCursor();
            _d.setVisible(false);
            return;
        }

        SwingUtilities.invokeLater( this );
    }

    @Override public void run() {
        if( isComplete() ) {
            return;
        }

        if( _first ) {
            _first = false;
            _pb.setValue( _next );
            runAgain();
            return;
        }

        doStep( _next );
        onStepComplete();
    }
}
