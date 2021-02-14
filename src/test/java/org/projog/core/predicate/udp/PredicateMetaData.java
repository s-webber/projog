/*
 * Copyright 2021 S. Webber
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
package org.projog.core.predicate.udp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.term.Atom;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/**
 * Used by system tests in src/test/prolog/udp/predicate-meta-data
 */
public final class PredicateMetaData extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term input, Term variable) {
      List<Term> attributes = new ArrayList<>();

      PredicateFactory pf = getPredicates().getPredicateFactory(input);
      attributes.add(toTerm("factory", pf));
      if (pf instanceof StaticUserDefinedPredicateFactory) {
         PredicateFactory apf = ((StaticUserDefinedPredicateFactory) pf).getActualPredicateFactory();
         attributes.add(toTerm("actual", apf));
      }
      if (pf instanceof PreprocessablePredicateFactory) {
         PredicateFactory preprocessed = ((PreprocessablePredicateFactory) pf).preprocess(input);
         attributes.add(toTerm("processed", preprocessed));
      }

      return new MetaDataPredicate(variable, attributes);
   }

   private Term toTerm(String type, PredicateFactory pf) {
      return Structure.createStructure(":", new Term[] {new Atom(type), new Atom(pf.getClass().getName())});
   }

   private static class MetaDataPredicate implements Predicate {
      final Term variable;
      final Iterator<Term> iterator;

      MetaDataPredicate(Term variable, List<Term> attributes) {
         this.variable = variable;
         this.iterator = attributes.iterator();
      }

      @Override
      public boolean evaluate() {
         while (iterator.hasNext()) {
            variable.backtrack();
            Term next = iterator.next();
            if (variable.unify(next)) {
               return true;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return iterator.hasNext();
      }
   }
}
