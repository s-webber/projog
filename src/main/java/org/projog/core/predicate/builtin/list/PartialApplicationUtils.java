/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.term.TermUtils.backtrack;

import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.Predicates;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

// Moved methods to separate class so can be used by both MapList and SubList. TODO move to TermUtils
public class PartialApplicationUtils {
   private static final String KEY_VALUE_PAIR_FUNCTOR = "-";

   public static boolean isAtomOrStructure(Term arg) {
      TermType type = arg.getType();
      return type == TermType.STRUCTURE || type == TermType.ATOM;
   }

   public static boolean isList(Term arg) {
      TermType type = arg.getType();
      return type == TermType.EMPTY_LIST || type == TermType.LIST;
   }

   public static PredicateFactory getCurriedPredicateFactory(Predicates predicates, Term partiallyAppliedFunction) {
      return getPartiallyAppliedPredicateFactory(predicates, partiallyAppliedFunction, 1);
   }

   public static PredicateFactory getPreprocessedCurriedPredicateFactory(Predicates predicates, Term partiallyAppliedFunction) {
      return getPreprocessedPartiallyAppliedPredicateFactory(predicates, partiallyAppliedFunction, 1);
   }

   public static PredicateFactory getPreprocessedPartiallyAppliedPredicateFactory(Predicates predicates, Term partiallyAppliedFunction, int extraArgs) {
      Term[] args = new Term[partiallyAppliedFunction.getNumberOfArguments() + extraArgs];
      for (int i = 0; i < partiallyAppliedFunction.getNumberOfArguments(); i++) {
         args[i] = partiallyAppliedFunction.getArgument(i);
      }
      for (int i = partiallyAppliedFunction.getNumberOfArguments(); i < args.length; i++) {
         args[i] = new Variable();
      }
      // TODO check not numeric before calling .getName()
      Term t = Structure.createStructure(partiallyAppliedFunction.getName(), args);
      return predicates.getPreprocessedPredicateFactory(t);
   }

   public static PredicateFactory getPartiallyAppliedPredicateFactory(Predicates predicates, Term partiallyAppliedFunction, int numberOfExtraArguments) {
      int numArgs = partiallyAppliedFunction.getNumberOfArguments() + numberOfExtraArguments;
      // TODO check not numeric before calling .getName()
      PredicateKey key = new PredicateKey(partiallyAppliedFunction.getName(), numArgs);
      return predicates.getPredicateFactory(key);
   }

   // TODO have overloaded version that avoids varargs
   public static Term[] createArguments(Term partiallyAppliedFunction, Term... extraArguments) {
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

   public static boolean apply(PredicateFactory pf, Term[] args) {
      Predicate p = pf.getPredicate(args);
      if (p.evaluate()) {
         return true;
      } else {
         backtrack(args);
         return false;
      }
   }

   public static Predicate getPredicate(PredicateFactory pf, Term action, Term... args) {
      if (action.getNumberOfArguments() == 0) {
         return pf.getPredicate(args);
      } else {
         return pf.getPredicate(createArguments(action, args));
      }
   }

   static boolean isKeyValuePair(Term t) {
      return t.getType() == TermType.STRUCTURE && KEY_VALUE_PAIR_FUNCTOR.equals(t.getName()) && t.getNumberOfArguments() == 2;
   }
}
