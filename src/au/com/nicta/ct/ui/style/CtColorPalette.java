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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Alan
 */
public class CtColorPalette {

    public Map<String, Color> colors = new HashMap<String, Color>();

    public static CtColorPalette nicta = new CtColorPalette();

    public static final Color         NICTA_PURPLE = new Color(134, 109, 169);
    public static final String STRING_NICTA_PURPLE = "NictaPurple";

    public static final Color         NICTA_GREEN = new Color(126, 176,  42);
    public static final String STRING_NICTA_GREEN = "NictaGreen";

    public static final Color         NICTA_YELLOW = new Color(255, 255, 158 );
    public static final String STRING_NICTA_YELLOW = "NictaYellow";


    public static final Color         NICTA_LIGHT_PURPLE = new Color(156, 135, 184);
    public static final String STRING_NICTA_LIGHT_PURPLE = "NictaLightPurple";

    public static final Color         NICTA_LIGHT_GREEN  = new Color(174, 218, 101);
    public static final String STRING_NICTA_LIGHT_GREEN = "NictaLightGreen";

    public static final Color         NICTA_LIGHT_YELLOW = new Color(255, 255, 215 );
    public static final String STRING_NICTA_LIGHT_YELLOW = "NictaYellow";


    public static final Color         NICTA_DARK_PURPLE = new Color(102,  79, 134);
    public static final String STRING_NICTA_DARK_PURPLE = "NictaDarkPurple";

    public static final Color         NICTA_DARK_GREEN = new Color( 73, 103,  24);
    public static final String STRING_NICTA_DARK_GREEN = "NictaDarkGreen";




    static {
        nicta.addColor(        NICTA_PURPLE,
                        STRING_NICTA_PURPLE );

        nicta.addColor(        NICTA_GREEN,
                        STRING_NICTA_GREEN );

        nicta.addColor(        NICTA_YELLOW,
                        STRING_NICTA_YELLOW );

        nicta.addColor(        NICTA_LIGHT_PURPLE,
                        STRING_NICTA_LIGHT_PURPLE );

        nicta.addColor(        NICTA_LIGHT_GREEN,
                        STRING_NICTA_LIGHT_GREEN );

        nicta.addColor(        NICTA_DARK_PURPLE,
                        STRING_NICTA_DARK_PURPLE );

        nicta.addColor(        NICTA_DARK_GREEN,
                        STRING_NICTA_DARK_GREEN );
    }


    Color addColor(Color color, String name) {
        return colors.put(name, color);
    }

}
