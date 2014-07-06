package org.projog.core.function.io;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* TEST
 write_to_file(X) :-
    open('get_char_test.tmp', write, Z),
    set_output(Z),
    write(X),
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

 %TRUE write_to_file('abc')
 %QUERY read_from_file
 %OUTPUT 
 % a
 % b
 % c
 % end_of_file
 %
 %OUTPUT
 %ANSWER/
 %NO

 %LINK prolog-io
 */
/**
 * <code>get_char(X)</code> - reads the next character from the input stream.
 * <p>
 * The goal succeeds if <code>X</code> can be unified with next character read from the current input stream. Succeeds
 * only once and the operation of moving to the next character is not undone on backtracking.
 * </p>
 * <p>
 * If there are no more characters to read from the current input stream (i.e. if the end of the stream has been reached) 
 * then an attempt is made to unify <code>X</code> with an atom with the value <code>end_of_file</code>.
 * </p>
 */
public final class GetChar extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term argument) {
      try {
         int c = getKnowledgeBase().getFileHandles().getCurrentInputStream().read();
         Atom next;
         if (c == -1) {
            next = new Atom("end_of_file");
         } else {
            next = new Atom(Character.toString((char) c));
         }
         return argument.unify(next);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}