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
package org.projog.core.predicate.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.projog.TestUtils.array;
import static org.projog.TermFactory.atom;
import static org.projog.TestUtils.createClauseModel;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;
import static org.projog.TermFactory.variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.udp.ClauseAction;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.Clauses;
import org.projog.core.predicate.udp.Indexes;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class IndexesTest {
   @Test
   public void testAllPermutationsOfThreeArgs() {
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Atom d = atom("d");
      Clauses clauses = createClauses("p(a,b,c).", "p(a,c,b).", "p(b,c,a).", "p(a,d,c).");
      ClauseAction first = clauses.getClauseActions()[0];
      ClauseAction second = clauses.getClauseActions()[1];
      ClauseAction third = clauses.getClauseActions()[2];
      ClauseAction fourth = clauses.getClauseActions()[3];

      Indexes indexes = new Indexes(clauses);
      assertEquals(0, indexes.countReferences());

      // 1 arg indexes
      assertMatches(indexes, array(a, v(), v()), first, second, fourth);
      assertMatches(indexes, array(v(), c, v()), second, third);
      assertMatches(indexes, array(v(), v(), a),third);
      assertNoMatches(indexes, array(v(), v(), d));

      // 2 arg indexes
      assertMatches(indexes, array(a, b, v()), first);
      assertMatches(indexes, array(a, v(), c), first, fourth);
      assertMatches(indexes, array(v(), c, a),third);
      assertNoMatches(indexes, array(b, a, v()));

      // 3 arg indexes
      assertMatches(indexes, array(a, b, c), first);
      assertMatches(indexes, array(a, c, b), second);
      assertMatches(indexes, array(b, c, a),third);
      assertMatches(indexes, array(a, d, c),fourth);
      assertNoMatches(indexes, array(c, a, b));

      assertEquals(7, indexes.countReferences());
   }

   @Test
   public void testMaxThreeArgsPerIndex() {
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Atom d = atom("d");
      Atom e = atom("e");
      Clauses clauses = createClauses("p(a,b,c,d,e).", "p(a,b,c,d,f).", "p(a,b,c,f,g).", "p(a,b,f,g,h).");
      ClauseAction first = clauses.getClauseActions()[0];
      ClauseAction second = clauses.getClauseActions()[1];
      ClauseAction third = clauses.getClauseActions()[2];

      Indexes indexes = new Indexes(clauses);

      // a maximum of the first 3 immutable args will be used in the index
      assertMatches(indexes, array(a, b, c, d, e), first, second, third);
      assertMatches(indexes, array(v(), b, c, d, e), first, second);
      assertMatches(indexes, array(v(), v(), c, d, e), first);
   }

   @Test
   public void testMaxNineArgsIndexable() {
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Atom d = atom("d");
      Atom e = atom("e");
      Atom f = atom("f");
      Atom g = atom("g");
      Atom h = atom("h");
      Atom i = atom("i");
      Atom j = atom("j");
      Clauses clauses = createClauses("p(a,b,c,d,e,f,g,h,i,j).", "p(q,w,e,r,t,y,u,i,o,p).", "p(z,x,k,v,b,n,m,a,s,d).");
      ClauseAction first = clauses.getClauseActions()[0];
      ClauseAction second = clauses.getClauseActions()[1];
      ClauseAction third = clauses.getClauseActions()[2];

      Indexes indexes = new Indexes(clauses);

      // a maximum of the first 9 immutable args will be considered for use in the index
      assertMatches(indexes, array(a, v(), v(), v(), v(), v(), v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), b, v(), v(), v(), v(), v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), c, v(), v(), v(), v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), d, v(), v(), v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), v(), e, v(), v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), f, v(), v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), g, v(), v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), h, v(), v()), first);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), i, v()), first);
      // expect all three clauses to be returned as final argument will not be used in the index
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), j), first, second, third);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), integerNumber(9)), first, second, third);
   }

   @Test
   public void testMuttableArgsNotIncludeInMaxLimits() {
      Atom a = atom("a");
      Atom b = atom("b");
      Atom c = atom("c");
      Atom d = atom("d");
      Atom q = atom("q");
      Atom k = atom("k");
      Atom j = atom("j");
      Atom x = atom("x");
      Atom z = atom("z");
      // 3rd and 8th args not considered indexable as sometimes mutable
      Clauses clauses = createClauses("p(z,x,X,v,b,n,m,h,s,d,e,q).", "p(a,b,c,d,e,f,g,p(Y),i,j,k,l).", "p(a,b,c,z,e,f,g,h,i,j,x,y).");
      ClauseAction first = clauses.getClauseActions()[0];
      ClauseAction second = clauses.getClauseActions()[1];
      ClauseAction third = clauses.getClauseActions()[2];

      Indexes indexes = new Indexes(clauses);
      // 4th argument ("d") will be included in composite index.
      // This is dispite there being a 3 arg max limit in the number of args per index.
      // This is because the third argument is not considered for indexing as one of the clauses has
      // a mutable term (a variable named "X") in that position.
      assertMatches(indexes, array(a, b, c, d, v(), v(), v(), v(), v(), v(), v(), v()), second);
      assertMatches(indexes, array(a, b, q, d, v(), v(), v(), v(), v(), v(), v(), v()), second);
      assertMatches(indexes, array(a, b, c, z, v(), v(), v(), v(), v(), v(), v(), v()), third);
      assertMatches(indexes, array(a, b, q, z, v(), v(), v(), v(), v(), v(), v(), v()), third);
      // 3rd and 8th args not indexed as a clause has a mutable term in that position.
      assertMatches(indexes, array(v(), v(), c, v(), v(), v(), v(), v(), v(), v(), v()), first, second, third);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), z, v(), v(), v(), v()), first, second, third);
      // 10th and 11th arg considered for indexing as within max of 9 mutable terms considered for indexing.
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), j, v(), v()), second, third);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), v(), k, v()), second);
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), v(), x, v()), third);
      // 12th arg not used in index as only max of 9 mutable terms considered for indexing.
      assertMatches(indexes, array(v(), v(), v(), v(), v(), v(), v(), v(), v(), v(), v(), z), first, second, third);
   }

   @Test
   public void testAllPermutationsOfLargeData() {
      int numClauses = 1000;
      int numArgs = 9;
      List<ClauseModel> models = new ArrayList<>(numClauses);
      for (int i = 0; i < numClauses; i++) {
         Term[] args = new Term[numArgs];
         for (int i2=0; i2<args.length; i2++) {
            args[i2] = integerNumber(i+i2);
         }
         models.add(ClauseModel.createClauseModel(structure("p", args)));
      }
      Clauses clauses = Clauses.createFromModels(TestUtils.createKnowledgeBase(), models);

      Indexes indexes = new Indexes(clauses);

      Collections.shuffle(models);
      Iterator<ClauseModel> itr = models.iterator();
      for (int i = 0; i < numArgs; i++) {
         ClauseModel expected = itr.next();
         Term[] args = array(v(), v(), v(), v(), v(), v(), v(), v(), v());
         args[i] = expected.getConsequent().getArgument(i);
         ClauseAction[] matches = indexes.index(args);
         assertEquals(1, matches.length);
         assertSame(expected, matches[0].getModel());
      }

      for (int i1 = 0; i1 < numArgs; i1++) {
         for (int i2 = i1 + 1; i2 < numArgs; i2++) {
            ClauseModel expected = itr.next();
            Term[] args = array(v(), v(), v(), v(), v(), v(), v(), v(), v());
            args[i1] = expected.getConsequent().getArgument(i1);
            args[i2] = expected.getConsequent().getArgument(i2);
            ClauseAction[] matches = indexes.index(args);
            assertEquals(1, matches.length);
            assertSame(expected, matches[0].getModel());
         }
      }

      for (int i1 = 0; i1 < numArgs; i1++) {
         for (int i2 = i1 + 1; i2 < numArgs; i2++) {
            for (int i3 = i2 + 1; i3 < numArgs; i3++) {

               ClauseModel expected = itr.next();
               Term[] args = array(v(), v(), v(), v(), v(), v(), v(), v(), v());
               args[i1] = expected.getConsequent().getArgument(i1);
               args[i2] = expected.getConsequent().getArgument(i2);
               args[i3] = expected.getConsequent().getArgument(i3);
               ClauseAction[] matches = indexes.index(args);
               assertEquals(1, matches.length);
               assertSame(expected, matches[0].getModel());
            }
         }
      }

      assertEquals(129, indexes.countReferences());
      // TODO set numClauses to a larger number (e.g. 100,000) to verify that some indexes have been garbage collected
      // TODO assertTrue(indexes.countClearedReferences() > 0);
   }

   private void assertMatches(Indexes indexes, Term[] input, ClauseAction... expected) {
      assertTrue(expected.length > 0);
      ClauseAction[] actual = indexes.index(input);
      assertSame(actual, indexes.index(input)); // assert same object gets returned for multiple calls
      assertEquals(expected.length, actual.length);
      for (int i = 0; i < actual.length; i++) {
         assertSame(expected[i], actual[i]);
      }
   }

   private void assertNoMatches(Indexes indexes, Term[] input) {
      ClauseAction[] actual = indexes.index(input);
      assertSame(actual, indexes.index(input)); // assert same object gets returned for multiple calls
      assertEquals(0, actual.length);
   }

   private Clauses createClauses(String... clauses) { // TODO move to TestUtils
      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      List<ClauseModel> models = new ArrayList<>();
      for (String clause : clauses) {
         models.add(createClauseModel(clause));
      }
      return Clauses.createFromModels(kb, models);
   }

   private Variable v() {
      return variable();
   }
}
