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
package org.projog.core.function.list;

import static org.projog.core.term.TermUtils.backtrack;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

// Moved methods to separate class so can be used by both MapList and SubList. If useful then move to TermUtils.
class PartialApplicationUtils {
   static boolean isAtomOrStructure(Term arg) {
      TermType type = arg.getType();
      return type == TermType.STRUCTURE || type == TermType.ATOM;
   }

   static boolean isList(Term arg) {
      TermType type = arg.getType();
      return type == TermType.EMPTY_LIST || type == TermType.LIST;
   }

   static PredicateFactory getPredicateFactory(KnowledgeBase kb, Term partiallyAppliedFunction) {
      return getPredicateFactory(kb, partiallyAppliedFunction, 1);
   }

   static PredicateFactory getPredicateFactory(KnowledgeBase kb, Term partiallyAppliedFunction, int numberOfExtraArguments) {
      int numArgs = partiallyAppliedFunction.getNumberOfArguments() + numberOfExtraArguments;
      PredicateKey key = new PredicateKey(partiallyAppliedFunction.getName(), numArgs);
      return kb.getPredicateFactory(key);
   }

   static Term[] createArguments(Term partiallyAppliedFunction, Term... extraArguments) {
      int originalNumArgs = partiallyAppliedFunction.getNumberOfArguments();
      Term[] result = new Term[originalNumArgs + extraArguments.length];

      for (int i = 0; i < originalNumArgs; i++) {
         result[i] = partiallyAppliedFunction.getArgument(i).getTerm();
      }

      for (int i = 0; i < extraArguments.length; i++) {
         result[originalNumArgs + i] = extraArguments[i].getTerm();
      }

      return result;
   }

   static boolean apply(PredicateFactory pf, Term[] args) {
      Predicate p = pf.getPredicate(args);
      if (p.evaluate()) {
         return true;
      } else {
         backtrack(args);
         return false;
      }
   }
}
