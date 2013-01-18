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

package au.com.nicta.ct.db;

import au.com.nicta.ct.db.hibernate.CtExperiments;
import au.com.nicta.ct.db.hibernate.CtSolutions;
import au.com.nicta.ct.db.hibernate.CtUsers;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author davidjr
 */
public class CtQueries {

    public static CtSolutions solution( CtExperiments e ) {
        String hql = " from CtSolutions s where s.ctExperiments = " + e.getPkExperiment();
        return (CtSolutions)CtSession.getObject( hql );
    }

    public static Collection< Object > solutions( CtExperiments e ) {
        String hql = " from CtSolutions s where s.ctExperiments = " + e.getPkExperiment();
        return CtSession.getObjects( hql );
    }

    public static Collection< Object > experiments( CtUsers u ) {

//        String hql = "select e from CtExperiments e ";
//        String hql = "select e from CtUsers u "
//                + " inner join CtUsersGroups ug "
//                + "inner join CtGroups "
//                + "inner join CtGroupsExperiments "
//                + "inner join CtExperiments e where u.pkUser = "
//                + 1;//u.getPkUser();

        String hql = "select e from CtUsers u "
                + " inner join u.ctUsersGroupses ug "
                + " inner join ug.ctGroups g "
                + " inner join g.ctGroupsExperimentses ge "
                + " inner join ge.ctExperiments e "
                + " where u.pkUser = '" + 1 + "'";
//                + "inner join CtGroupsExperiments "
//                + "inner join CtExperiments e where u.pkUser = "
//                + 1;//u.getPkUser();

        List< Object > l = CtSession.getObjects( hql );

        return l;
    }

}
