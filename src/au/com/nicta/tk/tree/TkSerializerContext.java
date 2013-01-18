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

package au.com.nicta.tk.tree;

import java.util.HashMap;

/**
 *
 * @author Alan
 */
public class TkSerializerContext {

    /**
     * Sort classes such that the most derived classes some before their super classes
     */
//    class CompareClass implements Comparator<Class> {
//
//        public int compare(Class o1, Class o2) {
////            System.out.println("o1.getClass(): " + (o1.getName()) );
////            System.out.println("o2.getClass(): " + (o2.getName()) );
//            if( o1.isAssignableFrom(o2) ) {
//                if( o2.isAssignableFrom(o1) ) { // then same class
//                    return 0;
//                }
//                else { // o1 is super of o2, make o1 > o2
//                    return 1;
//                }
//            }
//            else
//            if( o2.isAssignableFrom(o1) ) { // o2 is super of o1, make o1 < o2
//                return -1;
//            }
//
//            // no relationship, use some other test to break the tie
//            return o1.getName().compareTo( o2.getName() );
//        }
//
//    }

    // TODO: make this sorted according to most derived.
    // Bit should probably do this via a serialiser that forwards the call onto
    // the target object.
    public HashMap<Class, TkSerializer> serialisers = new HashMap<Class, TkSerializer>();

    public String defaultMethodName;


    public static boolean invoke(Object o, String methodName, TkSerializerContext sc) {
        try {
            o.getClass().getMethod( methodName, TkSerializerContext.class ).invoke(o, sc);
        }
        catch(Exception e) {
            return false;
        }

        return true;
    }

    public void ctSerialize(Object o) {

        // Try to find an exact matching class's serializer
        TkSerializer s = serialisers.get( o.getClass() );
        if( s != null ) {
            System.out.println("Found exact class");
            s.ctSerialize(this, o);
            return;
        }

        if(    defaultMethodName != null
            && !defaultMethodName.isEmpty() ) {
            if( !invoke(o, defaultMethodName, this) ) {
                System.out.println("CtSerialiserObject: No method found: " + defaultMethodName );
            }
        }

//        // no exact type, find the next super class
//        for( Map.Entry<Class, TkSerializer> e : serialisers.entrySet() ) {
//            if( e.getKey().isAssignableFrom( o.getClass() ) ) {
//                System.out.println("Found super class");
//                e.getValue().ctSerialize(this, o);
//                return;
//            }
//        }
        
//        System.out.println("No serialiser found for: " + o.getClass().toString() );
    }

}




