package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.integerNumber;
import static org.projog.TestUtils.structure;
import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;

import java.util.Map;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
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
      KnowledgeBase kb = new KnowledgeBase();
      assertSame(ProjogSystemProperties.class, getProjogProperties(kb).getClass());
   }

   /** Check that {@link ProjogProperties} is configurable. */
   @Test
   public void testConfiguredProjogProperties() {
      KnowledgeBase kb = new KnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
      assertSame(TestUtils.COMPILATION_DISABLED_PROPERTIES, getProjogProperties(kb));
   }

   /** @see CalculatablesTest */
   @Test
   public void testGetNumeric() {
      Structure p = structure("-", integerNumber(7), integerNumber(3));
      Numeric n = kb.getNumeric(p);
      assertSame(IntegerNumber.class, n.getClass());
      assertEquals(4, n.getLong());
   }

   @Test
   public void testUserDefinedPredicatesUnmodifiable() {
      Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = kb.getUserDefinedPredicates();
      try {
         userDefinedPredicates.put(null, null);
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   @Test
   public void testCannotOverwritePluginPredicate() {
      Term input = atom("true");
      PredicateKey key = PredicateKey.createForTerm(input);
      try {
         kb.createOrReturnUserDefinedPredicate(key);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true", e.getMessage());
      }
      try {
         kb.setUserDefinedPredicate(new StaticUserDefinedPredicateFactory(key));
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot replace already defined plugin predicate: true", e.getMessage());
      }
   }

   @Test
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
   public void testAddPredicateFactory() {
      // create PredicateKey to add to KnowledgeBase
      PredicateKey key = new PredicateKey("testAddPredicateFactory", 1);

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
         assertEquals("Already defined: testAddPredicateFactory/1", e.getMessage());
      }
   }

   @Test
   public void testAddPredicateFactory_NoPublicConstructor() {
      PredicateKey key = new PredicateKey("testAddPredicateFactoryError", 1);
      kb.addPredicateFactory(key, java.lang.Integer.class.getName());
      try {
         kb.getPredicateFactory(key);
         fail();
      } catch (RuntimeException e) {
         // expected as Integer has no public constructor (and is also not a PredicateFactory)
         assertEquals("Could not create new PredicateFactory", e.getMessage());
      }
   }

   @Test
   public void testAddPredicateFactory_InvalidClassName() {
      PredicateKey key = new PredicateKey("testAddPredicateFactoryError", 1);
      kb.addPredicateFactory(key, "an invalid class name");
      try {
         kb.getPredicateFactory(key);
         fail();
      } catch (RuntimeException e) {
         // expected as specified class name is invalid
         assertEquals("Could not create new PredicateFactory", e.getMessage());
      }
   }

   private void assertGetPredicateFactory(Term input, Class<?> expected) {
      PredicateFactory ef1 = kb.getPredicateFactory(input);
      assertSame(expected, ef1.getClass());

      PredicateKey key = PredicateKey.createForTerm(input);
      PredicateFactory ef2 = kb.getPredicateFactory(key);
      assertSame(expected, ef2.getClass());
   }
}