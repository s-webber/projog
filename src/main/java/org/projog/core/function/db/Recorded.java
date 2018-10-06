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
package org.projog.core.function.db;

import static org.projog.core.KnowledgeBaseServiceLocator.getServiceLocator;
import static org.projog.core.term.TermUtils.createAnonymousVariable;

import java.util.Iterator;

import org.projog.core.Predicate;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractPredicateFactory;
import org.projog.core.term.Term;

/* TEST
 %FALSE recorded(X,Y,Z)

 % Note: recorded/2 is equivalent to calling recorded/3 with the third argument as an anonymous variable.
 %FALSE recorded(X,Y)
 */
/**
 * <code>recorded(X,Y,Z)</code> - checks if a term is associated with a key.
 * <p>
 * <code>recorded(X,Y,Z)</code> succeeds if there exists an association between the key represented by <code>X</code>
 * and the term represented by <code>Y</code>, with the reference represented by <code>Z</code>.
 */
public final class Recorded extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term key, Term value) {
      return getPredicate(key, value, createAnonymousVariable());
   }

   @Override
   public Predicate getPredicate(Term key, Term value, Term reference) {
      RecordedDatabase database = getServiceLocator(getKnowledgeBase()).getInstance(RecordedDatabase.class);
      Iterator<Record> itr = getIterator(key, database);
      return new RecordedPredicate(key, value, reference, itr);
   }

   private Iterator<Record> getIterator(Term key, RecordedDatabase database) {
      if (key.getType().isVariable()) {
         return database.getAll();
      } else {
         PredicateKey k = PredicateKey.createForTerm(key);
         return database.getChain(k);
      }
   }

   private static final class RecordedPredicate implements Predicate {
      private final Term key;
      private final Term value;
      private final Term reference;
      private final Iterator<Record> itr;

      private RecordedPredicate(Term key, Term value, Term reference, Iterator<Record> itr) {
         this.key = key;
         this.value = value;
         this.reference = reference;
         this.itr = itr;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            Record next = itr.next();
            key.backtrack();
            value.backtrack();
            reference.backtrack();
            if (unify(next, key, value, reference)) {
               return true;
            }
         }
         return false;
      }

      private boolean unify(Record record, Term key, Term value, Term reference) {
         return key.unify(record.getKey()) && value.unify(record.getValue()) && reference.unify(record.getReference());
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return itr.hasNext();
      }
   }
}
