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
import au.com.nicta.ct.db.hibernate.CtUsers;
import java.util.Collection;
import java.util.List;

/**
 * Validates login data, handles salting + encryption of passwords etc.
 * Don't forget to take a random amount of time to avoid people guessing valid usernames.
 * For now, no encryption of passwords... 
 * @author davidjr
 */
public class CtCredentials {

    public static CtUsers user( String username, String password ) {

        Object o = CtSession.getObject( "from CtUsers where name = '"+username+"'" );

        if( o == null ) {
//System.out.println( "bad name "+username );
            return null;
        }

        CtUsers u = (CtUsers)o;

        if( matches( password, u ) ) {
//System.out.println( "match" );
            return u;
        }

        return null;
    }

    private static boolean matches( String password, CtUsers u ) {
        return password.equals( u.getPassword() );
    }

}
