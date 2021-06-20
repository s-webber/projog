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
package org.projog.core.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.projog.TermFactory.atom;

import java.io.StringReader;
import java.util.Arrays;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.api.Projog;
import org.projog.api.QueryPlan;
import org.projog.api.QueryResult;
import org.projog.core.ProjogException;
import org.projog.core.predicate.udp.PredicateUtils;

/**
 * Tests of attempting to replace or update an already defined predicate.
 * <p>
 * See: https://github.com/s-webber/projog/issues/195
 */
public class PredicatesTest {
   private final PredicateKey KEY = new PredicateKey("test", 2);

   @Test
   public void testCannotReplacePredicateFactoryWithAnotherPredicateFactory() {
      Projog projog = new Projog();

      // given that a build-in predicate is associated with the key
      projog.addPredicateFactory(KEY, mock(PredicateFactory.class));

      // attempting to associate another built-in predicate with the key should cause an exception
      try {
         projog.addPredicateFactory(KEY, mock(PredicateFactory.class));
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: test/2", e.getMessage());
      }
   }

   @Test
   public void testCannotReplacePredicateFactoryWithNonDynamicUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a build-in predicate is associated with the key
      projog.addPredicateFactory(KEY, mock(PredicateFactory.class));

      // attempting to add user defined clauses for the key should cause an exception
      try {
         projog.consultReader(new StringReader("test(a, b)."));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined built-in predicate: test/2", e.getCause().getMessage());
      }
   }

   @Test
   public void testCannotReplacePredicateFactoryWithDynamicUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a build-in predicate is associated with the key
      projog.addPredicateFactory(KEY, mock(PredicateFactory.class));

      // attempting to add dynamic user defined clauses for the key should cause an exception
      try {
         projog.consultReader(new StringReader("?- dynamic(test/2). test(a, b)."));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined built-in predicate: test/2", e.getCause().getMessage());
      }
   }

   @Test
   public void testCannotReplacePredicateFactoryWithAssertedPredicate() {
      Projog projog = new Projog();

      // given that a build-in predicate is associated with the key
      projog.addPredicateFactory(KEY, mock(PredicateFactory.class));

      // attempting to assert clauses for the key should cause an exception
      try {
         projog.executeOnce("assert(test(a, b)).");
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined built-in predicate: test/2", e.getMessage());
      }
   }

   @Test
   public void testCannotReplaceNonDynamicUserDefinedPredicateWithPredicateFactory() {
      Projog projog = new Projog();

      // given that a non-dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("test(a, b)."));

      // attempting to associate a built-in predicate with the key should cause an exception
      try {
         projog.addPredicateFactory(KEY, mock(PredicateFactory.class));
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: test/2", e.getMessage());
      }
   }

   @Test
   public void testCannotUpdateNonDynamicUserDefinedPredicateWithNonDynamicUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a non-dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("test(a, b)."));

      // attempting to add more user defined clauses for the key should cause an exception
      try {
         projog.consultReader(new StringReader("test(c, d)."));
         fail();
      } catch (ProjogException e) {
         assertEquals(
                     "Cannot append to already defined user defined predicate as it is not dynamic. You can set the predicate to dynamic by adding the following line to start of the file that the predicate is defined in:\n"
                                 + "?- dynamic(test/2).",
                                 e.getCause().getMessage());
      }
   }

   @Test
   public void testCannotUpdateNonDynamicUserDefinedPredicateWithDynamicUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a non-dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("test(a, b)."));

      // attempting to add more user defined clauses for the key should cause an exception
      try {
         projog.consultReader(new StringReader("?- dynamic(test/2). test(c, d)."));
         fail();
      } catch (ProjogException e) {
         assertEquals("Predicate has already been defined and is not dynamic: test/2", e.getCause().getMessage());
      }
   }

   @Test
   public void testCannotUpdateNonDynamicUserDefinedPredicateWithAssertedPredicate() {
      Projog projog = new Projog();

      // given that a non-dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("test(a, b)."));

      // attempting to add more user defined clauses for the key should cause an exception
      try {
         projog.executeOnce("assert(test(a, b)).");
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot append to already defined user defined predicate as it is not dynamic: test/2 clause: test(a, b)", e.getMessage());
      }
   }

   @Test
   public void testCannotReplaceDynamicUserDefinedPredicateWithPredicateFactory() {
      Projog projog = new Projog();

      // given that a dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("?- dynamic(test/2). test(a, b)."));

      // attempting to associate a built-in predicate with the key should cause an exception
      try {
         projog.addPredicateFactory(KEY, mock(PredicateFactory.class));
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: test/2", e.getMessage());
      }
   }

   @Test
   public void testCanUpdateDynamicUserDefinedPredicateWithUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("?- dynamic(test/2). test(a, b)."));

      // querying it should find the defined clause
      QueryResult r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertFalse(r.next());

      // attempting to add more user defined clauses for the key should succeed, as was declared dynamic when first consulted
      projog.consultReader(new StringReader("test(c, d). test(e, f)."));

      // querying it should find the original defined clause and the subsequently defined clauses
      r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("c", r.getAtomName("X"));
      assertEquals("d", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("e", r.getAtomName("X"));
      assertEquals("f", r.getAtomName("Y"));
      assertFalse(r.next());
   }

   @Test
   public void testCanUpdateDynamicUserDefinedPredicateWithDynamicUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a dynamic user defined predicate is associated with the key
      projog.consultReader(new StringReader("?- dynamic(test/2). test(a, b)."));

      // querying it should find the defined clause
      QueryResult r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertFalse(r.next());

      // attempting to add more user defined clauses for the key should succeed, as was declared dynamic when first consulted
      projog.consultReader(new StringReader("?- dynamic(test/2). test(c, d). test(e, f)."));

      // querying it should find the original defined clause and the subsequently defined clauses
      r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("c", r.getAtomName("X"));
      assertEquals("d", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("e", r.getAtomName("X"));
      assertEquals("f", r.getAtomName("Y"));
      assertFalse(r.next());
   }

   @Test
   public void testCanUpdateAssertedPredicateWithUserDefinedPredicate() {
      Projog projog = new Projog();

      // given that a clause has been asserted for the key
      projog.executeOnce("assert(test(a, b)).");

      // querying it should find the defined clause
      QueryResult r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertFalse(r.next());

      // attempting to add more user defined clauses for the key should succeed, as was first created via an assert so is dynamic
      projog.consultReader(new StringReader("test(c, d). test(e, f)."));

      // querying it should find the original defined clause and the subsequently defined clauses
      r = projog.executeQuery("test(X, Y).");
      assertTrue(r.next());
      assertEquals("a", r.getAtomName("X"));
      assertEquals("b", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("c", r.getAtomName("X"));
      assertEquals("d", r.getAtomName("Y"));
      assertTrue(r.next());
      assertEquals("e", r.getAtomName("X"));
      assertEquals("f", r.getAtomName("Y"));
      assertFalse(r.next());
   }

   @Test
   public void testCannotReplaceAssertedPredicateWithPredicateFactory() {
      Projog projog = new Projog();

      // given that a clause has been asserted for the key
      projog.executeOnce("assert(test(a, b)).");

      // attempting to associate a built-in predicate with the key should cause an exception
      try {
         projog.addPredicateFactory(KEY, mock(PredicateFactory.class));
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: test/2", e.getMessage());
      }
   }

   /**
    * Test scenario described in https://github.com/s-webber/projog/issues/195
    * <p>
    * <pre>
    * 1. Consult resource containing facts that have been defined as dynamic, and a rule that uses those facts.
    * 2. Consult another resource that contains an additional fact for the predicate defined in step 1.
    * 3. Query the rule defined in step to confirm it uses that facts defined in both steps 1 and 2.
    * </pre>
    */
   @Test
   public void testAppendToAlreadyDefinedClauseUsedByRule() {
      Projog projog = new Projog();

      // given that a dynamic user defined predicate is associated with the key
      StringBuilder input1 = new StringBuilder();
      input1.append("?- dynamic(test/2)."); // define as dynamic so can be updated by later consultations
      input1.append("test(a,1).");
      input1.append("test(b,2).");
      input1.append("test(c,3).");
      input1.append("test(d,4).");
      input1.append("test(e,5).");
      input1.append("test(f,6).");
      input1.append("test(g,7).");
      input1.append("test(h,8).");
      input1.append("test(i,9).");
      input1.append("testRule(X) :- test(X, Y), Y mod 2 =:= 0.");
      projog.consultReader(new StringReader(input1.toString()));

      // querying it should find the defined clause
      QueryPlan plan = projog.createPlan("testRule(X).");
      QueryResult r = plan.executeQuery();
      assertTrue(r.next());
      assertEquals("b", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("d", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("f", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("h", r.getAtomName("X"));
      assertFalse(r.next());

      assertEquals(Arrays.asList("b", "d", "f", "h"), plan.createStatement().findAllAsAtomName());

      // attempting to add more user defined clauses for the key should succeed, as was declared dynamic when first consulted
      StringBuilder input2 = new StringBuilder();
      input2.append("test(a,1).");

      projog.consultReader(new StringReader("test(j,10)."));

      // querying it should find the original defined clause and the subsequently defined clauses
      r = plan.executeQuery();
      assertTrue(r.next());
      assertEquals("b", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("d", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("f", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("h", r.getAtomName("X"));
      assertTrue(r.next());
      assertEquals("j", r.getAtomName("X"));
      assertFalse(r.next());

      assertEquals(Arrays.asList("b", "d", "f", "h", "j"), plan.createStatement().findAllAsAtomName());
   }

   @Test
   public void testGetPredicate() {
      Predicates p = TestUtils.createKnowledgeBase().getPredicates();
      assertSame(PredicateUtils.TRUE, p.getPredicate(atom("true")));
      assertSame(PredicateUtils.FALSE, p.getPredicate(atom("does_not_exist")));
   }
}
