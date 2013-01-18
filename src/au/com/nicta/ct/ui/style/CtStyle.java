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

package au.com.nicta.ct.ui.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 *
 * @author davidjr
 */
public class CtStyle {
    public static BasicStroke THICK_STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);  // saves on the number of objects
    public static Color BRIGHT_GREEN_TRANSLUCENT = new Color(0,255,0,100);
    public static Color BRIGHT_GREEN = Color.GREEN;
    public Paint fillPaint = null;
    public Paint strokePaint = BRIGHT_GREEN_TRANSLUCENT;
    public Stroke stroke = THICK_STROKE;

    public static String h1( String s ) {
        return "<html><h1>&nbsp;&nbsp;&nbsp;"+s+"</h1></html>";
    }

    public static String h2( String s ) {
        return "<html><h2>"+s+"</h2></html>";
    }

    public static String h3( String s ) {
        return "<html><h3>"+s+"</h3></html>";
    }

}
