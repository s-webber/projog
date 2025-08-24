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
import org.projog.core.term.Atom;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;

/**
 * Used by system tests in src/test/prolog/udp/predicate-meta-data
 */
public final class PredicateMetaData extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term input, Term variable) {
      List<Term> attributes = new ArrayList<>();

      PredicateFactory pf = getPredicates().getPredicateFactory(input);
      attributes.addAll(toTerms("factory", pf));
      if (pf instanceof StaticUserDefinedPredicateFactory) {
         PredicateFactory apf = ((StaticUserDefinedPredicateFactory) pf).getActualPredicateFactory();
         attributes.addAll(toTerms("actual", apf));
      }

      PredicateFactory preprocessed = pf.preprocess(input);
      attributes.addAll(toTerms("processed", preprocessed));

      return new MetaDataPredicate(variable, attributes);
   }

   private List<Term> toTerms(String type, PredicateFactory pf) {
      List<Term> attributes = new ArrayList<>();
      attributes.add(StructureFactory.createStructure(":", new Term[] {new Atom(type + "_class"), new Atom(pf.getClass().getName())}));
      attributes.add(StructureFactory.createStructure(":", new Term[] {new Atom(type + "_isRetryable"), new Atom("" + pf.isRetryable())}));
      attributes.add(StructureFactory.createStructure(":", new Term[] {new Atom(type + "_isAlwaysCutOnBacktrack"), new Atom("" + pf.isAlwaysCutOnBacktrack())}));
      return attributes;
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
