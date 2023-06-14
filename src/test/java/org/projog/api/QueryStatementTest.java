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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.parser.ParserException;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class QueryStatementTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testSetTerm() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      Term term = Structure.createStructure("test", new Term[] {new Atom("a")});
      s.setTerm("Y", term);
      assertSame(term, s.findFirstAsTerm());
   }

   @Test
   public void testSetAtomName() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setAtomName("Y", "a");
      assertEquals(new Atom("a"), s.findFirstAsTerm());
   }

   @Test
   public void testSetDouble() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setDouble("Y", 42.5);
      assertEquals(new DecimalFraction(42.5), s.findFirstAsTerm());
   }

   @Test
   public void testSetLong() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setLong("Y", 42);
      assertEquals(new IntegerNumber(42), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfTerms_varargs_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      Term term1 = Structure.createStructure("test", new Term[] {new Atom("a")});
      Term term2 = new Atom("a");
      Term term3 = new IntegerNumber(1);
      s.setListOfTerms("Y", term1, term2, term3);
      assertEquals(new List(term1, new List(term2, new List(term3, EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfTerms_list_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      Term term1 = Structure.createStructure("test", new Term[] {new Atom("a")});
      Term term2 = new Atom("a");
      Term term3 = new IntegerNumber(1);
      s.setListOfTerms("Y", Arrays.asList(term1, term2, term3));
      assertEquals(new List(term1, new List(term2, new List(term3, EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfAtomNames_varargs_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setListOfAtomNames("Y", "a", "b", "c");
      assertEquals(new List(new Atom("a"), new List(new Atom("b"), new List(new Atom("c"), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfAtomNames_list_version() {
      QueryStatement s = new Projog().createStatement("X = Y.");
      s.setListOfAtomNames("Y", Arrays.asList("a", "b", "c"));
      assertEquals(new List(new Atom("a"), new List(new Atom("b"), new List(new Atom("c"), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfDoubles_varargs_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setListOfDoubles("Y", 42.5, 180.2, -7.0);
      assertEquals(new List(new DecimalFraction(42.5), new List(new DecimalFraction(180.2), new List(new DecimalFraction(-7.0), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfDoubles_list_version() {
      QueryStatement s = new Projog().createStatement("X = Y.");
      s.setListOfDoubles("Y", Arrays.asList(42.5, 180.2, -7.0));
      assertEquals(new List(new DecimalFraction(42.5), new List(new DecimalFraction(180.2), new List(new DecimalFraction(-7.0), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfLongs_varargs_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setListOfLongs("Y", 42, 180, -7);
      assertEquals(new List(new IntegerNumber(42), new List(new IntegerNumber(180), new List(new IntegerNumber(-7), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testSetListOfLongs_list_version() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setListOfLongs("Y", Arrays.asList(42L, 180L, -7L));
      assertEquals(new List(new IntegerNumber(42), new List(new IntegerNumber(180), new List(new IntegerNumber(-7), EmptyList.EMPTY_LIST))), s.findFirstAsTerm());
   }

   @Test
   public void testNotReusable() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.executeQuery();
      try {
         s.executeQuery();
         fail();
      } catch (ProjogException e) {
         assertEquals("This QueryStatement has already been evaluated. If you want to reuse the same query then consider using a QueryPlan. See: Projog.createPlan(String)",
                     e.getMessage());
      }
   }

   @Test
   public void testUnknownVariable() {
      QueryStatement s = new Projog().createStatement("X = Y.");
      try {
         s.setTerm("Z", new Atom("a"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Do not know about variable named: Z in query: =(X, Y)", e.getMessage());
      }
   }

   @Test
   public void testAlreadySetVariable() {
      QueryStatement s = new QueryStatement(kb, "X = Y.");
      s.setTerm("X", new Atom("a"));
      try {
         s.setTerm("X", new Atom("b"));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot set: X to: b as has already been set to: a", e.getMessage());
      }
   }

   @Test
   public void testInvalidQuery() {
      try {
         new QueryStatement(kb, "X");
         fail();
      } catch (ParserException e) {
         assertEquals("No . to indicate end of sentence Line: X", e.getMessage());
      }
   }

   @Test
   public void testMoreThanOneSentenceInQuery() {
      try {
         new QueryStatement(kb, "X is 1. Y is 2.");
         fail();
      } catch (ProjogException e) {
         assertEquals("org.projog.core.ProjogException caught parsing: X is 1. Y is 2.", e.getMessage());
         assertEquals("More input found after . in X is 1. Y is 2.", e.getCause().getMessage());
      }
   }

   @Test
   public void testExecuteOnce() {
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      when(mockPredicateFactory.getPredicate(new Term[0])).thenReturn(PredicateUtils.TRUE);
      kb.getPredicates().addPredicateFactory(new PredicateKey("mock", 0), mockPredicateFactory);

      QueryStatement s = new QueryStatement(kb, "repeat, mock.");
      s.executeOnce();

      verify(mockPredicateFactory).getPredicate(new Term[0]);
      verifyNoMoreInteractions(mockPredicateFactory);
   }

   @Test
   public void testExecuteOnceNoSolution() {
      QueryStatement s = new QueryStatement(kb, "true, true, fail.");
      try {
         s.executeOnce();
         fail();
      } catch (ProjogException projogException) {
         assertEquals("Failed to find a solution for: ,(true, ,(true, fail))", projogException.getMessage());
      }
   }
}
