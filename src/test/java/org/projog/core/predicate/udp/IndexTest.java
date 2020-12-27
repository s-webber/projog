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
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.structure;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.udp.ClauseAction;
import org.projog.core.predicate.udp.ClauseActionFactory;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.Index;
import org.projog.core.predicate.udp.KeyFactories;
import org.projog.core.predicate.udp.KeyFactories.KeyFactory;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class IndexTest {
   private static final Atom A = atom("a");
   private static final Atom B = atom("b");
   private static final Atom C = atom("c");
   private static final Atom D = atom("d");
   private static final Atom E = atom("e");
   private static final KnowledgeBase KB = TestUtils.createKnowledgeBase();

   @Test
   public void testSingleArg() {
      // Create terms of 3 args, indexed by the 2nd argument.
      int[] positions = new int[] {1};
      Map<Object, ClauseAction[]> clauses = new HashMap<>();
      ClauseAction[] e1 = new ClauseAction[] {clause(A, B, C), clause(C, B, A)};
      clauses.put(B, e1);
      ClauseAction[] e2 = new ClauseAction[] {clause(A, C, B)};
      clauses.put(C, e2);

      // Create index to be tested.
      Index i = new Index(positions, clauses);

      // Assert getting matches where 2nd arg = B.
      assertSame(e1, i.getMatches(new Term[] {A, B, C}));
      assertSame(e1, i.getMatches(new Term[] {C, B, A}));
      assertSame(e1, i.getMatches(new Term[] {D, B, E}));

      // Assert getting matches where 2nd arg = C.
      assertSame(e2, i.getMatches(new Term[] {A, C, B}));
      assertSame(e2, i.getMatches(new Term[] {D, C, E}));

      // Assert when no match the same zero length arrays are always returned.
      ClauseAction[] noMatches = i.getMatches(new Term[] {C, A, B});
      assertEquals(0, noMatches.length);
      assertSame(noMatches, i.getMatches(new Term[] {B, D, C}));
   }

   @Test
   public void testTwoArgs() {
      // Create terms of 3 args, indexed by the 1st and 3rd arguments.
      int[] positions = new int[] {0, 2};
      KeyFactory kf = KeyFactories.getKeyFactory(positions.length);
      Map<Object, ClauseAction[]> clauses = new HashMap<>();
      ClauseAction[] e1 = new ClauseAction[] {clause(A, B, C), clause(A, D, C)};
      clauses.put(kf.createKey(positions, new Term[] {A, B, C}), e1);
      ClauseAction[] e2 = new ClauseAction[] {clause(A, B, D)};
      clauses.put(kf.createKey(positions, new Term[] {A, B, D}), e2);

      // Create index to be tested.
      Index i = new Index(positions, clauses);

      // Assert getting matches where 1st arg = A and 3rd arg = C.
      assertSame(e1, i.getMatches(new Term[] {A, B, C}));
      assertSame(e1, i.getMatches(new Term[] {A, D, C}));
      assertSame(e1, i.getMatches(new Term[] {A, E, C}));

      // Assert getting matches where 1st arg = A and 3rd arg = C.
      assertSame(e2, i.getMatches(new Term[] {A, B, D}));
      assertSame(e2, i.getMatches(new Term[] {A, C, D}));

      // Assert when no match the same zero length arrays are always returned.
      ClauseAction[] noMatches = i.getMatches(new Term[] {A, C, B});
      assertEquals(0, noMatches.length);
      assertSame(noMatches, i.getMatches(new Term[] {D, A, E}));
   }

   @Test
   public void testThreeArgs() {
      // Create terms of 3 args, indexed by all its arguments.
      int[] positions = new int[] {0, 1, 2};
      KeyFactory kf = KeyFactories.getKeyFactory(positions.length);
      Map<Object, ClauseAction[]> clauses = new HashMap<>();
      ClauseAction[] e1 = new ClauseAction[] {clause(A, B, C)};
      clauses.put(kf.createKey(positions, new Term[] {A, B, C}), e1);
      ClauseAction[] e2 = new ClauseAction[] {clause(A, B, D)};
      clauses.put(kf.createKey(positions, new Term[] {A, B, D}), e2);

      // Create index to be tested.
      Index i = new Index(positions, clauses);

      // Assert getting matches where 1st arg = A, 2nd arg = B and 3rd arg = C.
      assertSame(e1, i.getMatches(new Term[] {A, B, C}));

      // Assert getting matches where 1st arg = A, 2nd arg = B and 3rd arg = D.
      assertSame(e2, i.getMatches(new Term[] {A, B, D}));

      // Assert when no match the same zero length arrays are always returned.
      ClauseAction[] noMatches = i.getMatches(new Term[] {A, C, B});
      assertEquals(0, noMatches.length);
      assertSame(noMatches, i.getMatches(new Term[] {A, C, E}));
   }

   private ClauseAction clause(Term t1, Term t2, Term t3) {
      return ClauseActionFactory.createClauseAction(KB, ClauseModel.createClauseModel(structure("test", t1, t2, t3)));
   }
}
