/*
 * Copyright 2013 S Webber
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

import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Term;

/* SYSTEM TEST
 write_to_file(X) :-
    open('io_test.tmp', write, Z), 
    set_output(Z), 
    write(X), 
    close(Z), 
    set_output('user_output').

 read_from_file() :-
    open('io_test.tmp', read, Z), 
    set_input(Z), 
    print_first_sentence(), 
    close(Z).
   
 print_first_sentence() :- 
    repeat, 
    get_char(C), 
    write(C), 
    C=='.', 
    !.
	
 % %TRUE% write_to_file('The first sentence. The second sentence.')
 
 % %QUERY% read_from_file()
 % %OUTPUT% The first sentence.
 % %ANSWER/%
 % %NO%
 */
/**
 * <code>repeat</code>- always succeeds.
 * <p>
 * <code>repeat</code> <i>always</i> succeeds even when an attempt is made to re-satisfy it.
 * </p>
 */
public final class RepeatInfinitely extends AbstractRetryablePredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate();
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate() {
      return true;
   }

   @Override
   public RepeatInfinitely getPredicate(Term... args) {
      return getPredicate();
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public RepeatInfinitely getPredicate() {
      return this;
   }
}