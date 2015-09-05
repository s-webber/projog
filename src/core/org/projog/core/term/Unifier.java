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
package org.projog.core.term;

/**
 * Unifies the arguments in the head (consequent) of a clause with a query.
 */
public final class Unifier {
   /**
    * Private constructor as {@link #preMatch(Term[], Term[])} is static.
    */
   private Unifier() {
      // do nothing
   }

   /**
    * Unifies the arguments in the head (consequent) of a clause with a query.
    * <p>
    * When Prolog attempts to answer a query it searches its knowledge base for all rules with the same functor and
    * arity. For each rule founds it attempts to unify the arguments in the query with the arguments in the head
    * (consequent) of the rule. Only if the query and rule's head can be unified can it attempt to evaluate the body
    * (antecedant) of the rule to determine if the rule is true.
    * 
    * @param inputArgs the arguments contained in the query
    * @param consequentArgs the arguments contained in the head (consequent) of the clause
    * @return {@code true} if the attempt to unify the arguments was successful
    * @see Term#unify(Term)
    */
   public static boolean preMatch(Term[] inputArgs, Term[] consequentArgs) {
      for (int i = 0; i < inputArgs.length; i++) {
         if (!inputArgs[i].unify(consequentArgs[i])) {
            return false;
         }
      }
      for (int i = 0; i < inputArgs.length; i++) {
         consequentArgs[i] = consequentArgs[i].getTerm();
      }
      return true;
   }
}
