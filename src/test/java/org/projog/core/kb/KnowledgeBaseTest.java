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
package org.projog.core.kb;

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
import static org.projog.TermFactory.atom;
import static org.projog.TermFactory.integerNumber;
import static org.projog.TermFactory.structure;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.math.ArithmeticOperatorsTest;
import org.projog.core.math.Numeric;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.UnknownPredicate;
import org.projog.core.predicate.builtin.bool.Fail;
import org.projog.core.predicate.builtin.bool.True;
import org.projog.core.predicate.builtin.compare.Equal;
import org.projog.core.predicate.builtin.kb.AddPredicateFactory;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

public class KnowledgeBaseTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();
   private final Predicates predicates = kb.getPredicates();

   /** Check that {@link ProjogDefaultProperties} is used by default. */
   @Test
   public void testDefaultProjogProperties() {
      KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
      assertSame(ProjogDefaultProperties.class, kb.getProjogProperties().getClass());
   }

   /** Check that {@link ProjogProperties} is configurable. */
   @Test
   public void testConfiguredProjogProperties() {
      KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase(TestUtils.PROJOG_DEFAULT_PROPERTIES);
      assertSame(TestUtils.PROJOG_DEFAULT_PROPERTIES, kb.getProjogProperties());
   }

   /** @see ArithmeticOperatorsTest */
   @Test
   public void testGetNumeric() {
      Term p = structure("-", integerNumber(7), integerNumber(3));
      Numeric n = kb.getArithmeticOperators().getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(4, n.getLong());
   }

   @Test
   public void testCannotOverwritePluginPredicate() { // TODO these assertions are duplicated in PredicatesTest
      Term input = atom("true");
      PredicateKey key = PredicateKey.createForTerm(input);
      try {
         predicates.createOrReturnUserDefinedPredicate(key);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined built-in predicate: true/0", e.getMessage());
      }
      try {
         predicates.addUserDefinedPredicate(new StaticUserDefinedPredicateFactory(kb, key));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined built-in predicate: true/0", e.getMessage());
      }
      try {
         PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
         predicates.addPredicateFactory(key, mockPredicateFactory);
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: true/0", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, "com.example.DummyPredicateFactory");
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: true/0", e.getMessage());
      }
   }

   @Test
   public void testGetUserDefinedPredicates() {
      assertTrue(predicates.getUserDefinedPredicates().isEmpty());

      // Create user defined predicate test/0.
      PredicateKey key1 = PredicateKey.createForTerm(atom("test"));
      UserDefinedPredicateFactory udp1 = predicates.createOrReturnUserDefinedPredicate(key1);
      assertSame(key1, udp1.getPredicateKey());
      assertSame(key1, udp1.getPredicateKey());
      assertEquals(1, predicates.getUserDefinedPredicates().size());

      // Add a clause to the user defined predicate.
      ClauseModel clause1 = ClauseModel.createClauseModel(TestUtils.parseSentence("test :- write(clause1)."));
      udp1.addLast(clause1);

      // Retrieve user defined predicate test/0
      PredicateKey key2 = PredicateKey.createForTerm(atom("test"));
      assertSame(udp1, predicates.createOrReturnUserDefinedPredicate(key2));
      assertEquals(1, predicates.getUserDefinedPredicates().size());

      // Create new user defined predicate with same key as already defined version. Add a clause.
      UserDefinedPredicateFactory udp2 = new StaticUserDefinedPredicateFactory(kb, key1);
      ClauseModel clause2 = ClauseModel.createClauseModel(TestUtils.parseSentence("test :- write(clause2)."));
      udp2.addLast(clause2);

      // Add new user defined predicate test/0 and confirm previous version has been updated with extra clause.
      predicates.addUserDefinedPredicate(udp2);
      assertEquals(1, predicates.getUserDefinedPredicates().size());
      assertSame(udp1, predicates.createOrReturnUserDefinedPredicate(key1));
      assertEquals(clause1.getOriginal(), udp1.getClauseModel(0).getOriginal());
      assertEquals(clause2.getOriginal(), udp1.getClauseModel(1).getOriginal());
      assertEquals(1, predicates.getUserDefinedPredicates().size());

      PredicateKey key3 = PredicateKey.createForTerm(atom("test2"));
      UserDefinedPredicateFactory udp3 = predicates.createOrReturnUserDefinedPredicate(key3);
      assertSame(key3, udp3.getPredicateKey());
      assertEquals(2, predicates.getUserDefinedPredicates().size());

      assertNotSame(udp1, udp2);
      assertNotSame(udp1, udp3);
      assertNotSame(udp2, udp3);
   }

   @Test
   public void testGetUserDefinedPredicatesUnmodifiable() {
      Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = predicates.getUserDefinedPredicates();
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
      Predicates predicates = kb.getPredicates();
      predicates.createOrReturnUserDefinedPredicate(k7);
      predicates.addPredicateFactory(k4, "com.example.Abc");
      predicates.createOrReturnUserDefinedPredicate(k5);
      predicates.addPredicateFactory(k2, "com.example.Xyz");
      predicates.addPredicateFactory(k3, "com.example.Abc");
      predicates.createOrReturnUserDefinedPredicate(k1);
      predicates.addPredicateFactory(k6, new True());

      // get all defined predicate keys from the knowledge base
      Set<PredicateKey> allKeys = predicates.getAllDefinedPredicateKeys();
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
      PredicateFactory ef = predicates.getPredicateFactory(ADD_PREDICATE_KEY);
      assertSame(AddPredicateFactory.class, ef.getClass());
      assertTrue(ef instanceof AbstractSingleResultPredicate);
   }

   @Test
   public void testAddPredicateFactoryWithInstance() {
      PredicateFactory pf = new True();
      PredicateKey key = new PredicateKey("testAddPredicateFactoryWithInstance", 1);

      predicates.addPredicateFactory(key, pf);

      assertSame(pf, predicates.getPredicateFactory(key));

      // assert exception thrown if try to re-add
      try {
         predicates.addPredicateFactory(key, True.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, Fail.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, pf);
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithInstance/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, new Fail());
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
      assertSame(UnknownPredicate.class, predicates.getPredicateFactory(key).getClass());

      // add
      predicates.addPredicateFactory(key, True.class.getName());

      // assert now defined in knowledge base
      assertSame(True.class, predicates.getPredicateFactory(key).getClass());
      // assert, once defined, the same instance is returned each time
      assertSame(predicates.getPredicateFactory(key), predicates.getPredicateFactory(key));

      // assert exception thrown if try to re-add
      try {
         predicates.addPredicateFactory(key, True.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, Fail.class.getName());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, new True());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
      try {
         predicates.addPredicateFactory(key, new Fail());
         fail();
      } catch (ProjogException e) {
         assertEquals("Already defined: testAddPredicateFactoryWithClassName/1", e.getMessage());
      }
   }

   @Test
   public void testAddPredicateFactoryClassNotFound() {
      PredicateKey key = new PredicateKey("testAddPredicateFactoryError", 1);
      predicates.addPredicateFactory(key, "an invalid class name");
      try {
         predicates.getPredicateFactory(key);
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
      predicates.addPredicateFactory(key, DummyPredicateFactoryNoPublicConstructor.class.getName());
      try {
         predicates.getPredicateFactory(key);
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
      predicates.addPredicateFactory(key, className + "/getInstance");
      assertSame(DummyPredicateFactoryNoPublicConstructor.class, predicates.getPredicateFactory(key).getClass());
   }

   @Test
   public void testPreprocess_when_PreprocessablePredicateFactory() {
      Term term = structure("testOptimise", atom("test"));
      PreprocessablePredicateFactory mockPreprocessablePredicateFactory = mock(PreprocessablePredicateFactory.class);
      predicates.addPredicateFactory(PredicateKey.createForTerm(term), mockPreprocessablePredicateFactory);

      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      when(mockPreprocessablePredicateFactory.preprocess(term)).thenReturn(mockPredicateFactory);

      assertSame(mockPredicateFactory, predicates.getPreprocessedPredicateFactory(term));

      verify(mockPreprocessablePredicateFactory).preprocess(term);
      verifyNoMoreInteractions(mockPreprocessablePredicateFactory, mockPredicateFactory);
   }

   @Test
   public void testPreprocess_when_not_PreprocessablePredicateFactory() {
      // note that mockPredicateFactory is not an instance of PreprocessablePredicateFactory
      Term term = structure("testOptimise", atom("test"));
      PredicateFactory mockPredicateFactory = mock(PredicateFactory.class);
      predicates.addPredicateFactory(PredicateKey.createForTerm(term), mockPredicateFactory);

      assertSame(mockPredicateFactory, predicates.getPreprocessedPredicateFactory(term));

      verifyNoMoreInteractions(mockPredicateFactory);
   }

   private void assertGetPredicateFactory(Term input, Class<?> expected) {
      PredicateFactory ef1 = predicates.getPredicateFactory(input);
      assertSame(expected, ef1.getClass());

      PredicateKey key = PredicateKey.createForTerm(input);
      PredicateFactory ef2 = predicates.getPredicateFactory(key);
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
      public Predicate getPredicate(Term term) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isRetryable() {
         throw new UnsupportedOperationException();
      }
   }
}
