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
package org.projog.core.function.io;

import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-io
 */
/**
 * <code>get_char(X)</code> - reads a character.
 * <p>
 * <code>get_char(X)</code> - reads the next character from the input stream.
 * </p>
 * <p>
 * The goal succeeds if <code>X</code> can be matched with next character read from the current input stream. Succeeds
 * only once and the operation of moving to the next character is not undone on backtracking.
 * </p>
 */
public final class GetChar extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term argument) {
      try {
         char c = (char) getKnowledgeBase().getFileHandles().getCurrentInputStream().read();
         Atom a = new Atom(Character.toString(c));
         return argument.unify(a);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}