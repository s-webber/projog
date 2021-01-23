/*
 * Copyright 2020 S. Webber
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
package org.projog.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.projog.core.ProjogException;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class QueryResultTest {
   @Test
   public void testNoSolutions() {
      QueryResult r = new Projog().executeQuery("fail.");
      assertFalse(r.isExhausted());
      assertFalse(r.next());
      assertTrue(r.isExhausted());
   }

   @Test
   public void testOneSolution() {
      QueryResult r = new Projog().executeQuery("true.");
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertTrue(r.isExhausted());
      assertFalse(r.next());
   }

   @Test
   public void testMultipleSolutions() {
      QueryResult r = new Projog().executeQuery("repeat(3).");
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertTrue(r.isExhausted());
      assertFalse(r.next());
   }

   @Test
   public void testMultipleSolutionsWithVariable() {
      Projog p = new Projog();
      p.consultReader(new StringReader("test(a, 1).test(b, 2).test(c, 3)."));
      QueryResult r = p.executeQuery("test(X, Y).");
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals(1, r.getLong("Y"));
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertEquals("b", r.getAtomName("X"));
      assertEquals(2, r.getLong("Y"));
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertEquals("c", r.getAtomName("X"));
      assertEquals(3, r.getLong("Y"));
      assertTrue(r.isExhausted());
      assertFalse(r.next());
   }

   @Test
   public void testCutOnFirstAttempt() {
      QueryResult r = new Projog().executeQuery("repeat, !, fail.");
      assertFalse(r.isExhausted());
      assertFalse(r.next());
      assertTrue(r.isExhausted());
   }

   @Test
   public void testCutOnRetry() {
      QueryResult r = new Projog().executeQuery("repeat, !.");
      assertFalse(r.isExhausted());
      assertTrue(r.next());
      assertFalse(r.isExhausted());
      assertFalse(r.next());
      assertTrue(r.isExhausted());
   }

   @Test
   public void testProjogExceptionOnExecuteQuery() {
      Projog p = new Projog();
      StringReader sr = new StringReader("a(A) :- b(A). b(Z) :- c(Z, 5). c(X,Y) :- Z is X + Y, Z < 9.");
      p.consultReader(sr);
      QueryStatement s = p.createStatement("a(X).");
      try {
         // as a/1 only has a single clause then will try to evaluate as part of PredicateFactory.getPredicate(), which is why exception occurs now rather than on a later call to .next()
         s.executeQuery();
         fail();
      } catch (ProjogException e) {
         assertSame(ProjogException.class, e.getClass()); // check it is not a sub-class
         assertEquals("Cannot get Numeric for term: X of type: VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testProjogExceptionOnNext() {
      Projog p = new Projog();
      p.consultReader(new StringReader("a(A) :- b(A). a(A) :- A = test. b(Z) :- c(Z, 5). c(X,Y) :- Z is X + Y, Z < 9."));
      QueryStatement s = p.createStatement("a(X).");
      QueryResult r = s.executeQuery();
      try {
         // as a/1 only has multiple clauses then not try to evaluate as part of PredicateFactory.getPredicate(), which is why exception only occurs on call to .next()
         r.next();
         fail();
      } catch (ProjogException e) {
         assertSame(ProjogException.class, e.getClass()); // check it is not a sub-class
         assertEquals("Cannot get Numeric for term: X of type: VARIABLE", e.getMessage());
      }
   }

   @Test
   public void testGetTerm() {
      QueryResult r = new Projog().executeQuery("X = test(a, 1).");
      assertTrue(r.next());
      Term expected = Structure.createStructure("test", new Term[] {new Atom("a"), new IntegerNumber(1)});
      assertEquals(expected, r.getTerm("X"));
   }

   @Test
   public void testGetTermUnknownVariable() {
      QueryResult r = new Projog().executeQuery("X = test(a, 1).");
      assertTrue(r.next());
      try {
         r.getTerm("Y");
         fail();
      } catch (ProjogException e) {
         assertEquals("Unknown variable ID: Y. Query contains the variables: [X]", e.getMessage());
      }
   }

   @Test
   public void testGetTermBeforeNext() {
      QueryResult r = new Projog().executeQuery("X = test(a, 1).");
      try {
         r.getTerm("X");
         fail();
      } catch (ProjogException e) {
         assertEquals("Query not yet evaluated. Call QueryResult.next() before attempting to get value of variables.", e.getMessage());
      }
   }

   @Test
   public void testGetTermAfterFailure() {
      Projog p = new Projog();
      p.consultReader(new StringReader("a(1)."));
      QueryResult r = p.executeQuery("a(X).");

      // assert first evaluation succeeds
      assertTrue(r.next());
      assertEquals(1, r.getLong("X"));

      // assert second evaluation fails and subsequent call to getTerm throws an exception
      assertFalse(r.next());
      try {
         r.getTerm("X");
         fail();
      } catch (ProjogException e) {
         assertEquals("No more solutions. Last call to QueryResult.next() returned false.", e.getMessage());
      }
   }

   @Test
   public void testGetAtomName() {
      QueryResult r = new Projog().executeQuery("X = a.");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
   }

   @Test
   public void testGetDouble() {
      QueryResult r = new Projog().executeQuery("X = 1.5.");
      assertTrue(r.next());
      assertEquals(1.5, r.getDouble("X"), 0);
   }

   @Test
   public void testGetLong() {
      QueryResult r = new Projog().executeQuery("X = 1.");
      assertTrue(r.next());
      assertEquals(1L, r.getLong("X"));
   }

   @Test
   public void testGetVariableIds() {
      QueryResult r = new Projog().executeQuery("X = 1, Y=a, Z=[].");
      Set<String> expected = new HashSet<>();
      expected.add("X");
      expected.add("Y");
      expected.add("Z");
      assertEquals(expected, r.getVariableIds());
   }
}
