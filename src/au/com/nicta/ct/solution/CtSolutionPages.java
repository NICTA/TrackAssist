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

package au.com.nicta.ct.solution;

import au.com.nicta.ct.orm.patterns.CtAbstractPair;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author davidjr
 */
public class CtSolutionPages {

    public static final String SOLUTION_PAGE_PROPERTY_KEY = "display-solution";
    public static final String TRACKING_PAGE_PROPERTY_KEY = "tracking-page";
    public static final String LINEAGE_PAGE_PROPERTY_KEY = "lineage-page";
    public static final String EXPORT_PAGE_PROPERTY_KEY = "export-page";

    public static Collection< CtAbstractPair< String, String > > getTrackingPageOptions() {

        ArrayList< CtAbstractPair< String, String > > al = new ArrayList< CtAbstractPair< String, String > >();

        al.add( new CtAbstractPair< String, String >( "Lineage", LINEAGE_PAGE_PROPERTY_KEY ) );
        al.add( new CtAbstractPair< String, String >( "Export", EXPORT_PAGE_PROPERTY_KEY ) );

        return al;
    }

    public static Collection< CtAbstractPair< String, String > > getSplitPageOptions() {

        ArrayList< CtAbstractPair< String, String > > al = new ArrayList< CtAbstractPair< String, String > >();

        al.add( new CtAbstractPair< String, String >( "Export", EXPORT_PAGE_PROPERTY_KEY ) );

        return al;
    }

    public static Collection< CtAbstractPair< String, String > > getLineagePageOptions() {

        ArrayList< CtAbstractPair< String, String > > al = new ArrayList< CtAbstractPair< String, String > >();

        al.add( new CtAbstractPair< String, String >( "Tracking", TRACKING_PAGE_PROPERTY_KEY ) );
        al.add( new CtAbstractPair< String, String >( "Export", EXPORT_PAGE_PROPERTY_KEY ) );

        return al;
    }

    public static Collection< CtAbstractPair< String, String > > getExportPageOptions() {

        ArrayList< CtAbstractPair< String, String > > al = new ArrayList< CtAbstractPair< String, String > >();

        al.add( new CtAbstractPair< String, String >( "Solution", SOLUTION_PAGE_PROPERTY_KEY ) );
//        al.add( new CtAbstractPair< String, String >( "Lineage", LINEAGE_PAGE_PROPERTY_KEY ) );

        return al;
    }
}
