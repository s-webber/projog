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
package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getFileHandles;

import org.projog.core.FileHandles;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/* TEST
 write_to_file(X) :-
    open('get_code.tmp', write, Z),
    set_output(Z),
    writef(X),
    close(Z),
    set_output('user_output').

 read_from_file :-
    open('get_code.tmp', read, Z),
    set_input(Z),
    print_contents,
    close(Z).

 print_contents :-
    repeat,
    get_code(C),
    write(C),
    nl,
    C =:= -1,
    !.

 %TRUE write_to_file('ab\tc\r\nxyz')
 %QUERY read_from_file
 %OUTPUT
 % 97
 % 98
 % 9
 % 99
 % 13
 % 10
 % 120
 % 121
 % 122
 % -1
 %
 %OUTPUT
 %ANSWER/
 %NO

 force_error :-
    open('get_code.tmp', read, Z),
    set_input(Z),
    close(Z),
    print_contents.

 %QUERY force_error
 %ERROR Could not read next character from input stream

 %LINK prolog-io
 */
/**
 * <code>get_code(X)</code> - reads the next character from the input stream.
 * <p>
 * The goal succeeds if <code>X</code> can be unified with next character read from the current input stream. Succeeds
 * only once and the operation of moving to the next character is not undone on backtracking.
 * </p>
 * <p>
 * If there are no more characters to read from the current input stream (i.e. if the end of the stream has been
 * reached) then an attempt is made to unify <code>X</code> with the value <code>-1</code>.
 * </p>
 */
public final class GetCode extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term argument) {
      try {
         int c = fileHandles.getCurrentInputStream().read();
         IntegerNumber next = new IntegerNumber(c);
         return argument.unify(next);
      } catch (Exception e) {
         throw new ProjogException("Could not read next character from input stream", e);
      }
   }
}
