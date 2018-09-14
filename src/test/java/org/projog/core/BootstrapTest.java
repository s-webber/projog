/*
 * Copyright 2013-2014 S. Webber
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.ADD_CALCULATABLE_KEY;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.BOOTSTRAP_FILE;
import static org.projog.TestUtils.parseTermsFromFile;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.term.Term;

/**
 * Tests contents of {@code projog-bootstrap.pl}.
 * <p>
 * {@code projog-bootstrap.pl} is used to configure the build-in predicates and arithmetic functions.
 */
public class BootstrapTest {
   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   @Test
   public void testBuiltInPredicates() throws Exception {
      List<Term> terms = getQueriesByKey(ADD_PREDICATE_KEY);
      assertFalse(terms.isEmpty());
      for (Term t : terms) {
         assertBuiltInPredicate(t.getArgument(0));
      }
   }

   @Test
   public void testCalculatables() throws Exception {
      List<Term> terms = getQueriesByKey(ADD_CALCULATABLE_KEY);
      assertFalse(terms.isEmpty());
      for (Term t : terms) {
         assertCalculatable(t.getArgument(1));
      }
   }

   private List<Term> getQueriesByKey(PredicateKey key) {
      List<Term> result = new ArrayList<>();
      Term[] terms = parseTermsFromFile(BOOTSTRAP_FILE);
      for (Term next : terms) {
         if (QUESTION_PREDICATE_NAME.equals(next.getName())) {
            Term t = next.getArgument(0);
            if (key.equals(PredicateKey.createForTerm(t))) {
               result.add(t);
            }
         }
      }
      return result;
   }

   @SuppressWarnings("rawtypes")
   private void assertBuiltInPredicate(Term nameAndArity) throws Exception {
      PredicateKey key = PredicateKey.createFromNameAndArity(nameAndArity);
      PredicateFactory ef = kb.getPredicateFactory(key);
      assertFinal(ef);
      Class[] methodParameters = getMethodParameters(key);
      if (ef instanceof PredicateFactory) {
         // TODO assertClassImplementsOptimisedGetPredicateMethod(ef, methodParameters);
      }
      if (ef instanceof Predicate) {
         assertClassImplementsOptimisedEvaluateMethod(ef, methodParameters);
      }
   }

   private void assertCalculatable(Term className) throws Exception {
      Class<?> c = Class.forName(className.getName());
      Object o = c.newInstance();
      assertTrue(o instanceof Calculatable);
      assertFinal(o);
   }

   private void assertFinal(Object o) {
      Class<? extends Object> c = o.getClass();
      assertTrue("Not final: " + c, Modifier.isFinal(c.getModifiers()));
   }

   @SuppressWarnings("rawtypes")
   private Class<?>[] getMethodParameters(PredicateKey key) {
      int numberOfArguments = key.getNumArgs();
      Class<?>[] args = new Class[numberOfArguments];
      for (int i = 0; i < numberOfArguments; i++) {
         args[i] = Term.class;
      }
      return args;
   }

   @SuppressWarnings("rawtypes")
   private void assertClassImplementsOptimisedGetPredicateMethod(PredicateFactory ef, Class[] methodParameters) {
      try {
         // Test that the getPredicate method has a return type of the actual sub-class rather than Predicate
         Method m = ef.getClass().getDeclaredMethod("getPredicate", methodParameters);
         assertSame(ef.getClass() + "'s getPredicate(" + Arrays.toString(methodParameters) + ") method returns " + m.getReturnType(), ef.getClass(), m.getReturnType());
      } catch (NoSuchMethodException e) {
         fail("Testing getPredicate method of " + ef.getClass() + " with: " + methodParameters.length + " arguments caused: " + e.toString());
      }
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private void assertClassImplementsOptimisedEvaluateMethod(PredicateFactory ef, Class[] methodParameters) {
      Class c = ef.getClass();
      boolean success = false;
      while (success == false && c != null) {
         try {
            Method m = c.getDeclaredMethod("evaluate", methodParameters);
            assertSame(boolean.class, m.getReturnType());
            success = true;
         } catch (NoSuchMethodException e) {
            // if we can't find a matching method in the class then try its superclass
            c = c.getSuperclass();
         }
      }
      if (success == false) {
         fail(ef.getClass() + " does not implement an evaluate method with " + methodParameters.length + " parameters");
      }
   }
}
