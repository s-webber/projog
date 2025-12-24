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
package org.projog.core.predicate.builtin.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Set;

import org.projog.api.Projog;
import org.projog.api.QueryResult;
import org.projog.core.ProjogException;
import org.projog.core.parser.ParserException;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.UnknownPredicate;
import org.projog.core.predicate.builtin.construct.NumberVars;
import org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;

/**
 * Used to confirm that built-in predicate (written in Java) has same behaviour as equivalent version written in Prolog.
 */
final class ListPredicateAssert {
   private final String builtInPredicateName;
   private final String userDefinedPredicateName;
   private final int arity;
   private final Projog projog;

   ListPredicateAssert(String builtInPredicateName, int arity, String prologSource) {
      this.builtInPredicateName = builtInPredicateName;
      this.userDefinedPredicateName = builtInPredicateName + "_";
      this.arity = arity;
      projog = new Projog();
      projog.consultReader(new StringReader(prologSource));
      Predicates predicates = projog.getKnowledgeBase().getPredicates();
      PredicateFactory builtInPredicateFactory = predicates.getPredicateFactory(new PredicateKey(builtInPredicateName, arity));
      assertNotSame(builtInPredicateName, UnknownPredicate.class, builtInPredicateFactory.getClass());
      PredicateFactory userDefinedPredicateFactory = predicates.getPredicateFactory(new PredicateKey(userDefinedPredicateName, arity));
      assertSame(userDefinedPredicateName, StaticUserDefinedPredicateFactory.class, userDefinedPredicateFactory.getClass());
   }

   void assertQuery(String query) {
      String query2 = query.replace(builtInPredicateName, userDefinedPredicateName);
      assertNotEquals(query, query2);
      assertQueries(query, query2);
   }

   void assertArgs(String... arguments) {
      assertEquals(arity, arguments.length);
      assertQueries(constructQuery(builtInPredicateName, arguments), constructQuery(userDefinedPredicateName, arguments));
   }

   private String constructQuery(String predicateName, String... arguments) {
      return predicateName + "(" + String.join(",", arguments) + ").";
   }

   private void assertQueries(String query1, String query2) {
      QueryResult r1;
      try {
         r1 = projog.executeQuery(query1);
      } catch (ProjogException | OutOfMemoryError e1) {
         assertFalse(e1.getMessage(), e1 instanceof ParserException);
         try {
            projog.executeQuery(query2).next();
            throw new RuntimeException("No exception " + query2, e1);
         } catch (ProjogException | OutOfMemoryError e2) {
            assertSame(e1.getClass(), e2.getClass());
            return;
         }
      }
      QueryResult r2 = projog.executeQuery(query2);
      Set<String> variableIds = r1.getVariableIds();
      assertEquals(variableIds, r2.getVariableIds());

      int ctr = 0;
      while (true) {
         try {
            if (!r1.next()) {
               break;
            }
         } catch (ProjogException | OutOfMemoryError e1) {
            try {
               r2.next();
               throw new RuntimeException("No exception " + query2, e1);
            } catch (ProjogException | OutOfMemoryError e2) {
               assertSame(e1.getClass(), e2.getClass());
               return;
            }
         }

         assertTrue(r2.next());
         for (String variableId : variableIds) {
            Term t1;
            try {
               t1 = r1.getTerm(variableId);
            } catch (StackOverflowError | OutOfMemoryError e1) {
               try {
                  r2.getTerm(variableId);
                  throw new RuntimeException("No exception " + query2, e1);
               } catch (StackOverflowError | OutOfMemoryError e2) {
                  assertSame(e1.getClass(), e2.getClass());
                  return;
               }
            }
            Term t2 = r2.getTerm(variableId);
            assertEquals(query1, numberVariables(t1), numberVariables(t2));
         }

         if (ctr++ > 50) {
            // to avoid getting stuck evaluating infinite queries, exit after max number of iterations
            // TODO make limit configurable
            return;
         }
      }

      assertFalse(r2.next());
   }

   private Term numberVariables(Term t) {
      Term copy = t.copy();
      new NumberVars().getPredicate(StructureFactory.createStructure("numbervars", new Term[] {copy}));
      return copy.getTerm();
   }
}
