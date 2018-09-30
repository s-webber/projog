/*
 * Copyright 2013-2014 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.core.function.construct;

/* TEST
 %QUERY atom_chars(X,[a,p,p,l,e])
 %ANSWER X = apple

 %QUERY atom_chars(X,[97,112,112,108,101])
 %ANSWER X = apple

 %QUERY atom_chars(apple,X)
 %ANSWER X = [a,p,p,l,e]

 %TRUE atom_chars(apple,[a,p,p,l,e])

 %FALSE atom_chars(apple,[97,112,112,108,101])

 %TRUE atom_chars('APPLE',['A','P','P','L','E'])

 %FALSE atom_chars(apple,[a,112,p,108,101])

 %FALSE atom_chars(apple,[a,p,l,l,e])

 %FALSE atom_chars(apple,[a,p,p,l,e,s])

 %FALSE atom_chars(apple,[a,112,p,108,102])

 %FALSE atom_chars('APPLE',[a,p,p,l,e])

 %FALSE atom_chars('apple',['A','P','P','L','E'])

 %QUERY atom_chars(apple,[X,Y,Y,Z,e])
 %ANSWER
 % X = a
 % Y = p
 % Z = l
 %ANSWER

 %FALSE atom_chars(apple,[X,Y,Z,Z,e])

 %QUERY atom_chars(X,'apple')
 %ERROR As the first argument: X is a variable the second argument needs to be a list but was: apple of type: ATOM
 */
/**
 * <code>atom_chars(A,L)</code> - compares an atom to a list of characters.
 * <p>
 * <code>atom_chars(A,L)</code> compares the name of an atom <code>A</code> with the list of characters <code>L</code>.
 * </p>
 */
public final class AtomChars extends AbstractTermSplitFunction {
   public AtomChars() {
      super(false, false);
   }
}
