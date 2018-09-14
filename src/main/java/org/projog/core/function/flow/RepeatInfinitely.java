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
package org.projog.core.function.flow;

import org.projog.core.Predicate;
import org.projog.core.function.AbstractPredicate;
import org.projog.core.function.AbstractPredicateFactory;

/* TEST
 write_to_file(X) :-
    open('io_test.tmp', write, Z),
    set_output(Z),
    write(X),
    close(Z),
    set_output('user_output').

 read_from_file :-
    open('io_test.tmp', read, Z),
    set_input(Z),
    print_first_sentence,
    close(Z).

 print_first_sentence :-
    repeat,
    get_char(C),
    write(C),
    C=='.',
    !.

 %TRUE write_to_file('The first sentence. The second sentence.')

 %QUERY read_from_file
 %OUTPUT The first sentence.
 %ANSWER/
 %NO
 */
/**
 * <code>repeat</code> - always succeeds.
 * <p>
 * <code>repeat</code> <i>always</i> succeeds even when an attempt is made to re-satisfy it.
 * </p>
 */
public final class RepeatInfinitely extends AbstractPredicateFactory {
   private static final RepeatInfinitelyPredicate SINGLETON = new RepeatInfinitelyPredicate();

   @Override
   public Predicate getPredicate() {
      return SINGLETON;
   }

   private static class RepeatInfinitelyPredicate extends AbstractPredicate {
      @Override
      public boolean evaluate() {
         return true;
      }
   }
}
