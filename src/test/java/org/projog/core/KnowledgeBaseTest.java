/*
 * Copyright 2013 S. Webber
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.core.KnowledgeBaseUtils.getArithmeticOperators;
import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.function.bool.Fail;
import org.projog.core.function.bool.True;
import org.projog.core.function.compare.Equal;
import org.projog.core.function.kb.AddPredicateFactory;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.udp.UserDefinedPredicateFactory;
public class KnowledgeBaseTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   /** Check that {@link ProjogSystemProperties} is used by default. */
   @Test
   public void testDefaultProjogProperties() {
      KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
      assertSame(ProjogSystemProperties.class, getProjogProperties(kb).getClass());
   }

   /** Check that {@link ProjogProperties} is configurable. */
   @Test
   public void testConfiguredProjogProperties() {
      KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
      assertSame(TestUtils.COMPILATION_DISABLED_PROPERTIES, getProjogProperties(kb));
   }

   /** @see ArithmeticOperatorsTest */
   @Test
   public void testGetNumeric() {
      Structure p = structure("-", integerNumber(7), integerNumber(3));
      Numeric n = getArithmeticOperators(kb).getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(4, n.getLong());
   }

   @Test
   public void testCannotOverwritePluginPredicate() {
      Term input = atom("true");
      PredicateKey key = PredicateKey.createForTerm(input);
      try {
         kb.createOrReturnUserDefinedPredicate(key);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true/0", e.getMessage());
      }
      try {
         kb.addUserDefinedPredicate(new StaticUserDefinedPredicateFactory(kb, key));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true/0", e.getMessage());
      }
   }

   @Test
   public void testGetUserDefinedPredicates() {
      assertTrue(kb.getUserDefinedPredicates().isEmpty());

      PredicateKey key1 = PredicateKey.createForTerm(atom("test"));
      UserDefinedPredicateFactory udp1 = kb.createOrReturnUserDefinedPredicate(key1);
      assertSame(key1, udp1.getPredicateKey());
      assertEquals(1, kb.getUserDefinedPredicates().size());

      PredicateKey key2 = PredicateKey.createForTerm(atom("test"));
      assertSame(udp1, kb.createOrReturnUserDefinedPredicate(key2));
      assertEquals(1, kb.getUserDefinedPredicates().size());

      UserDefinedPredicateFactory udp2 = new StaticUserDefinedPredicateFactory(kb, key1);
      kb.addUserDefinedPredicate(udp2);
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

   @Test
   public void testGetUserDefinedPredicatesUnmodifiable() {
      Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = kb.getUserDefinedPredicates();
      try {
         userDefinedPredicates.put(null, null);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testGetAllDefinedPredicateKeys() {
      // create keys
      PredicateKey k1 = new PredicateKey("a", 9);
      PredicateKey k2 = new PredicateKey("x", 1);
      PredicateKey k3 = new PredicateKey("x", 2);
      PredicateKey k4 = new PredicateKey("x", 3);
      PredicateKey k5 = new PredicateKey("z", 2);
      PredicateKey k6 = new PredicateKey("z", 6);
      PredicateKey k7 = new PredicateKey("z", 7);

      // add keys to knowledge base
      // add some as "build-in" predicates and others as "user-defined" predicates
      KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
      kb.createOrReturnUserDefinedPredicate(k7);
      kb.addPredicateFactory(k4, "com.example.Abc");
      kb.createOrReturnUserDefinedPredicate(k5);
      kb.addPredicateFactory(k2, "com.example.Xyz");
      kb.addPredicateFactory(k3, "com.example.Abc");
      kb.createOrReturnUserDefinedPredicate(k1);
      kb.addPredicateFactory(k6, new True());

      // get all defined predicate keys from the knowledge base
      Set<PredicateKey> allKeys = kb.getAllDefinedPredicateKeys();
      assertEquals(8, allKeys.size());

      // assert all the keys we added are included, and in the correct order
      Iterator<PredicateKey> iterator = allKeys.iterator();
      assertEquals(k1, iterator.next());
      // although we didn't add "pj_add_predicate/2" it will be returned -
      // it is the one predicate that is hardcoded in Projog and so is always present
      assertEquals(TestUtils.ADD_PREDICATE_KEY, iterator.next());
      assertEquals(k2, iterator.next());
      assertEquals(k3, iterator.next());
      assertEquals(k4, iterator.next());
      assertEquals(k5, iterator.next());
      assertEquals(k6, iterator.next());
      assertEquals(k7, iterator.next());
      assertFalse(iterator.hasNext());
   }

   @Test
   public void testGetPredicateAndGetPredicateFactory_1() {
      Term input = atom("true");
      assertGetPredicateFactory(input, True.class);
   }

   @Test
   public void testGetPredicateAndGetPredicateFactory_2() {
      Term input = atom("does_not_exist");
      assertGetPredicateFactory(input, UnknownPredicate.class);
   }

   @Test
   public void testGetPredicateAndGetPredicateFactory_3() {
      Term input = structure("=", atom(), atom());
      assertGetPredicateFactory(input, Equal.class);
   }

   @Test
   public void testGetPredicateAndGetPredicateFactory_4() {
      Term input = structure("=", atom(), atom(), atom());
      assertGetPredicateFactory(input, UnknownPredicate.class);
   }

   @Test
   public void testGetAddPredicateFactory() {
      PredicateFactory ef = kb.getPredicateFactory(ADD_PREDICATE_KEY);
      assertSame(AddPredicateFactory.class, ef.getClass());
      assertTrue(ef instanceof AbstractSingletonPredicate);
   }

   @Test
   public void testAddPredicateFactoryWithInstance() {
      PredicateFactory pf = new True();
      PredicateKey key = new PredicateKey("testAddPredicateFactoryWithInstance", 1);

      kb.addPredicateFactory(key, pf);

      assertSame(pf, kb.getPredicateFactory(key));

      // assert exception thrown if try to re-add
      try {
         kb.addPredicateFactory(key, True.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, Fail.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, pf);
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, new Fail());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
   }

   @Test
   public void testAddPredicateFactoryWithClassName() {
      // create PredicateKey to add to KnowledgeBase
      PredicateKey key = new PredicateKey("testAddPredicateFactoryWithClassName", 1);

      // assert not already defined in knowledge base
      assertSame(UnknownPredicate.class, kb.getPredicateFactory(key).getClass());

      // add
      kb.addPredicateFactory(key, True.class.getName());

      // assert now defined in knowledge base
      assertSame(True.class, kb.getPredicateFactory(key).getClass());
      // assert, once defined, the same instance is returned each time
      assertSame(kb.getPredicateFactory(key), kb.getPredicateFactory(key));

      // assert exception thrown if try to re-add
      try {
         kb.addPredicateFactory(key, True.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, Fail.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, new True());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         kb.addPredicateFactory(key, new Fail());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
   }

   @Test
   public void testAddPredicateFactoryClassNotFound() {
      PredicateKey key = new PredicateKey("testAddPredicateFactoryError", 1);
      kb.addPredicateFactory(key, "an invalid class name");
      try {
         kb.getPredicateFactory(key);
         fail();
      } catch (RuntimeException e) {
         // expected as specified class name is invalid
         assertEquals("Could not create new PredicateFactory using: an invalid class name", e.getMessage());
         assertSame(ClassNotFoundException.class, e.getCause().getClass());
      }
   }

   /** Test attempting to add a predicate factory that does not have a public no arg constructor. */
   @Test
   public void testAddPredicateFactoryIllegalAccess() {
      final PredicateKey key = new PredicateKey("testAddPredicateFactoryError", 1);
      final String className = DummyPredicateFactoryNoPublicConstructor.class.getName();
      kb.addPredicateFactory(key, DummyPredicateFactoryNoPublicConstructor.class.getName());
      try {
         kb.getPredicateFactory(key);
         fail();
      } catch (RuntimeException e) {
         // expected as Integer has no public constructor (and is also not a PredicateFactory)
         assertEquals("Could not create new PredicateFactory using: " + className, e.getMessage());
         assertSame(IllegalAccessException.class, e.getCause().getClass());
      }
   }

   /** Test using a static method to add a predicate factory that does not have a public no arg constructor. */
   @Test
   public void testAddPredicateFactoryUsingStaticMethod() {
      final PredicateKey key = new PredicateKey("testAddPredicateFactory", 1);
      final String className = DummyPredicateFactoryNoPublicConstructor.class.getName();
      kb.addPredicateFactory(key, className + "/getInstance");
      assertSame(DummyPredicateFactoryNoPublicConstructor.class, kb.getPredicateFactory(key).getClass());
   }

   @Test
   public void testPreprocess_when_PreprocessablePredicateFactory() {
      Term term = structure("testOptimise", atom("test"));
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      kb.addPredicateFactory(PredicateKey.createForTerm(term), mockPreprocessablePredicateFactory);

      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      when(mockPreprocessablePredicateFactory.preprocess(term)).thenReturn(mockPredicateFactory);

      assertSame(mockPredicateFactory, kb.getPreprocessedPredicateFactory(term));

      verify(mockPreprocessablePredicateFactory).preprocess(term);
      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory);
   }

   @Test
   public void testPreprocess_when_not_PreprocessablePredicateFactory() {
      // note that mockPredicateFactory is not an instance of PreprocessablePredicateFactory
      Term term = structure("testOptimise", atom("test"));
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      kb.addPredicateFactory(PredicateKey.createForTerm(term), mockPredicateFactory);

      assertSame(mockPredicateFactory, kb.getPreprocessedPredicateFactory(term));

      verifyNoMoreInteractions(mockPredicateFactory);
   }

   private void assertGetPredicateFactory(Term input, Class<?> expected) {
      PredicateFactory ef1 = kb.getPredicateFactory(input);
      assertSame(expected, ef1.getClass());

      PredicateKey key = PredicateKey.createForTerm(input);
      PredicateFactory ef2 = kb.getPredicateFactory(key);
      assertSame(expected, ef2.getClass());
   }

   public static class DummyPredicateFactoryNoPublicConstructor implements PredicateFactory {
      public static DummyPredicateFactoryNoPublicConstructor getInstance() {
         return new DummyPredicateFactoryNoPublicConstructor();
      }

      private DummyPredicateFactoryNoPublicConstructor() {
         // private as want to test creation using getInstance static method
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
      }

      @Override
      public Predicate getPredicate(Term... args) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isRetryable() {
         throw new UnsupportedOperationException();
      }
   }
}
