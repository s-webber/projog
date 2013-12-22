/*
 * Copyright 2013 S Webber
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
package org.projog.core;

import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;

import java.util.Map;

import junit.framework.TestCase;

import org.projog.TestUtils;
import org.projog.core.function.bool.True;
import org.projog.core.function.compare.Equal;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.udp.UserDefinedPredicateFactory;

public class KnowledgeBaseTest extends TestCase {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   /** Check that {@link ProjogSystemProperties} is used by default. */
   public void testDefaultProjogProperties() {
      KnowledgeBase kb = new KnowledgeBase();
      assertSame(ProjogSystemProperties.class, kb.getProjogProperties().getClass());
   }

   /** Check that {@link ProjogProperties} is configurable. */
   public void testConfiguredProjogProperties() {
      KnowledgeBase kb = new KnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
      assertSame(TestUtils.COMPILATION_DISABLED_PROPERTIES, kb.getProjogProperties());
   }

   /** @see SpyPointsTest */
   public void testGetSpyPoints() {
      SpyPoints sp = kb.getSpyPoints();
      assertSame(sp, kb.getSpyPoints());
   }

   /** @see FileHandlesTest */
   public void testGetFileHandles() {
      FileHandles fh = kb.getFileHandles();
      assertSame(fh, kb.getFileHandles());
   }

   /** @see OperandsTest */
   public void testGetOperands() {
      Operands o = kb.getOperands();
      assertSame(o, kb.getOperands());
   }

   /** @see CalculatableFactoryTest */
   public void testGetNumeric() {
      Structure p = structure("-", integerNumber(7), integerNumber(3));
      Numeric n = kb.getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(4, n.getInt());
   }

   public void testUserDefinedPredicatesUnmodifiable() {
      Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = kb.getUserDefinedPredicates();
      try {
         userDefinedPredicates.put(null, null);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public void testCannotOverwritePluginPredicate() {
      Term input = atom("true");
      PredicateKey key = PredicateKey.createForTerm(input);
      try {
         kb.createOrReturnUserDefinedPredicate(key);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true/-1", e.getMessage());
      }
      try {
         kb.setUserDefinedPredicate(new StaticUserDefinedPredicateFactory(key));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true/-1", e.getMessage());
      }
   }

   public void testUserDefinedPredicates() {
      assertTrue(kb.getUserDefinedPredicates().isEmpty());

      PredicateKey key1 = PredicateKey.createForTerm(atom("test"));
      UserDefinedPredicateFactory udp1 = kb.createOrReturnUserDefinedPredicate(key1);
      assertSame(key1, udp1.getPredicateKey());
      assertEquals(1, kb.getUserDefinedPredicates().size());

      PredicateKey key2 = PredicateKey.createForTerm(atom("test"));
      assertSame(udp1, kb.createOrReturnUserDefinedPredicate(key2));
      assertEquals(1, kb.getUserDefinedPredicates().size());

      UserDefinedPredicateFactory udp2 = new StaticUserDefinedPredicateFactory(key1);
      kb.setUserDefinedPredicate(udp2);
      assertEquals(1, kb.getUserDefinedPredicates().size());
      assertSame(udp2, kb.createOrReturnUserDefinedPredicate(key1));
      assertEquals(1, kb.getUserDefinedPredicates().size());

      PredicateKey key3 = PredicateKey.createForTerm(atom("test2"));
      UserDefinedPredicateFactory udp3 = kb.createOrReturnUserDefinedPredicate(key3);
      assertSame(key3, udp3.getPredicateKey());
      assertEquals(2, kb.getUserDefinedPredicates().size());

      assertNotSame(udp1, udp2);
      assertNotSame(udp1, udp3);
      assertNotSame(udp2, udp3);
   }

   public void testGetPredicateAndGetPredicateFactory_1() {
      Term input = atom("true");
      assertGetPredicateFactory(input, True.class);
   }

   public void testGetPredicateAndGetPredicateFactory_2() {
      Term input = atom("does_not_exist");
      assertGetPredicateFactory(input, UnknownPredicate.class);
   }

   public void testGetPredicateAndGetPredicateFactory_3() {
      Term input = structure("=", atom(), atom());
      assertGetPredicateFactory(input, Equal.class);
   }

   public void testGetPredicateAndGetPredicateFactory_4() {
      Term input = structure("=", atom(), atom(), atom());
      assertGetPredicateFactory(input, UnknownPredicate.class);
   }

   private void assertGetPredicateFactory(Term input, Class<?> expected) {
      PredicateFactory ef1 = kb.getPredicateFactory(input);
      assertSame(expected, ef1.getClass());

      PredicateKey key = PredicateKey.createForTerm(input);
      PredicateFactory ef2 = kb.getPredicateFactory(key);
      assertSame(expected, ef2.getClass());
   }

   public void testTermToString() {
      String inputSyntax = "X = 1 + 1 , p(1, 7.3, [_,[]|c])";
      Term inputTerm = TestUtils.parseSentence(inputSyntax + ".");
      assertEquals(inputSyntax, kb.toString(inputTerm));
   }
}