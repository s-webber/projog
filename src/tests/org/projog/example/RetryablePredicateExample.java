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
package org.projog.example;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class RetryablePredicateExample extends AbstractRetryablePredicate {
   private Iterator<Map.Entry<Object, Object>> systemProperties;

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      if (systemProperties == null) {
         systemProperties = System.getProperties().entrySet().iterator();
      } else {
         arg1.backtrack();
         arg2.backtrack();
      }
      while (systemProperties.hasNext()) {
         Entry<Object, Object> entry = systemProperties.next();
         String key = (String) entry.getKey();
         String value = (String) entry.getValue();
         if (arg1.unify(new Atom(key)) && arg2.unify(new Atom(value))) {
            return true;
         } else {
            arg1.backtrack();
            arg2.backtrack();
         }
      }
      return false;
   }

   @Override
   public RetryablePredicateExample getPredicate(Term... args) {
      return getPredicate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #getPredicate(Term...)} that avoids the overhead of creating a new {@code Term}
    * array.
    * 
    * @see org.projog.core.PredicateFactory#getPredicate(Term...)
    */
   public RetryablePredicateExample getPredicate(Term arg1, Term arg2) {
      return new RetryablePredicateExample();
   }
}