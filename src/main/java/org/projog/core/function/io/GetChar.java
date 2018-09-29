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
package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getFileHandles;

import org.projog.core.FileHandles;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 write_to_file(X) :-
    open('get_char_test.tmp', write, Z),
    set_output(Z),
    writef(X),
    close(Z),
    set_output('user_output').

 read_from_file :-
    open('get_char_test.tmp', read, Z),
    set_input(Z),
    print_contents,
    close(Z).

 print_contents :-
    repeat,
    get_char(C),
    write(C),
    nl,
    C=='end_of_file',
    !.

 %TRUE write_to_file('abc\nxyz')
 %QUERY read_from_file
 %OUTPUT
 % a
 % b
 % c
 %
 %
 % x
 % y
 % z
 % end_of_file
 %
 %OUTPUT
 %ANSWER/
 %NO

 force_error :-
    open('get_char_test.tmp', read, Z),
    set_input(Z),
    close(Z),
    print_contents.

 %QUERY force_error
 %ERROR Could not read next character from input stream

 %LINK prolog-io
 */
/**
 * <code>get_char(X)</code> - reads the next character from the input stream.
 * <p>
 * The goal succeeds if <code>X</code> can be unified with next character read from the current input stream. Succeeds
 * only once and the operation of moving to the next character is not undone on backtracking.
 * </p>
 * <p>
 * If there are no more characters to read from the current input stream (i.e. if the end of the stream has been
 * reached) then an attempt is made to unify <code>X</code> with an atom with the value <code>end_of_file</code>.
 * </p>
 */
public final class GetChar extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term argument) {
      try {
         int c = fileHandles.getCurrentInputStream().read();
         Atom next = toAtom(c);
         return argument.unify(next);
      } catch (Exception e) {
         throw new ProjogException("Could not read next character from input stream", e);
      }
   }

   private Atom toAtom(int c) {
      return new Atom(toString(c));
   }

   private String toString(int c) {
      if (c == -1) {
         return "end_of_file";
      } else {
         return Character.toString((char) c);
      }
   }
}
