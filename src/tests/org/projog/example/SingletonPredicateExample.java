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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class SingletonPredicateExample extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String currentDate = sdf.format(new Date());
      return arg.unify(new Atom(currentDate));
   }
}