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

package au.com.nicta.ct.experiment.coordinates.viewpoint;

/**
 *
 * @author davidjr
 */
public interface CtViewpointListener {//extends CtPanZoomListener implements CtCoordinatesListener {
//
//    public void onViewpointChanged(); // any change in viewpoint
//    public void onOrdinatesChanged(); // offsets to coordinates or lock
//    public void onPanZoomChanged(); // changes in way viewpoint appears
//
    // These remain centralized models:
//    public void onTimeWindowChanged();
//    public void onTimeSelectionChanged();
//    public void onPlaybackChanged();
//
    public static final String EVT_VIEWPOINT_CHANGED = "evt-viewpoint-changed"; // called on ANY change to viewpoint, including all the below:

    public static final String EVT_ORDINATES_CHANGED = "evt-ordinates-changed";
    public static final String EVT_IMAGE_CHANGED = "evt-image-changed";
    public static final String EVT_PAN_CHANGED = "evt-pan-changed";
    public static final String EVT_ZOOM_CHANGED = "evt-zoom-changed";

}
