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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * global properties / constants
 * @author davidjr
 */
public class CtConstants {

    public static Color NictaPurple = new Color( 0x866DA9 );// 134, 109, 169 );
    public static Color NictaGreen  = new Color( 0x72AA12 );//114, 170, 18 );
    public static Color NictaYellow = new Color( 0xfff3cb ); // 255 243 203
    public static Color NictaHighlight = new Color( 255, 100, 100 );

    public static final int SCROLL_PANEL_HEIGHT = 500;
    public static final int TEXT_FIELD_WIDTH_CHARACTERS = 50;

    public static void setPreferredSize( Component c ) {
        c.setPreferredSize( new Dimension( c.getMaximumSize().width, SCROLL_PANEL_HEIGHT ) );
    }

}
