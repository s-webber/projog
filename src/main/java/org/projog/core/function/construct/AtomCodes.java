/*
 * Copyright 2018 S. Webber
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
 %QUERY atom_codes(X,[a,p,p,l,e])
 %ANSWER X = apple

 %QUERY atom_codes(X,[97,112,112,108,101])
 %ANSWER X = apple

 %FALSE atom_codes(apple,[a,p,p,l,e])

 %TRUE atom_codes(apple,[97,112,112,108,101])

 %QUERY atom_codes(apple,X)
 %ANSWER X = [97,112,112,108,101]

 %TRUE atom_codes('APPLE',[65,80,80,76,69])

 %FALSE atom_codes(apple,[a,112,p,108,101])

 %FALSE atom_codes(apple,[97,112,108,108,101])

 %FALSE atom_codes(apple,[97,112,112,108,101,102])

 %FALSE atom_codes(apple,[a,112,p,108,102])

 %FALSE atom_codes('APPLE',[97,112,112,108,101])

 %FALSE atom_codes('apple',[65,80,80,76,69])

 %QUERY atom_codes(apple,[X,Y,Y,Z,101])
 %ANSWER
 % X = 97
 % Y = 112
 % Z = 108
 %ANSWER

 %FALSE atom_codes(apple,[X,Y,Z,Z,101])

 %QUERY atom_codes(X,'apple')
 %ERROR As the first argument: X is a variable the second argument needs to be a list but was: apple of type: ATOM
 */
/**
 * <code>atom_codes(A,L)</code> - compares an atom to a list of characters.
 * <p>
 * <code>atom_codes(A,L)</code> compares the name of an atom <code>A</code> with the list of characters <code>L</code>.
 * </p>
 */
public final class AtomCodes extends AbstractTermSplitFunction {
   public AtomCodes() {
      super(false, true);
   }
}
