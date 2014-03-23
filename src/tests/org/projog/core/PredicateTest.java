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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.parseTermsFromFile;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

public class PredicateTest {
   private static final File BOOTSTRAP_FILE = new File("etc/projog-bootstrap.pl");

   private final KnowledgeBase kb = TestUtils.createKnowledgeBase();

   /**
    * Maintains record of class hierarchy of all PredicateFactory implementations
    * <p>
    * key = class name, value = immediate superclass of the class specified by the associated key
    * <p>
    * Used to automatically generate input to plantuml to generate uml class diagram.
    */
   private final Map<String, String> classStructure = new HashMap<>();

   @Test
   public void testBuiltInPredicates() throws Exception {
      Term[] terms = parseTermsFromFile(BOOTSTRAP_FILE);
      int builtInPredicateCtr = 0;
      for (Term next : terms) {
         if (QUESTION_PREDICATE_NAME.equals(next.getName())) {
            // 1st argument will be the actual query e.g. for:
            // ?- pj_add_Predicate(true, 'org.projog.core.function.bool.True').
            // the 1st argument will be pj_add_Predicate(true, 'org.projog.core.function.bool.True')
            Term t = next.getArgument(0);
            PredicateKey key = PredicateKey.createForTerm(t);
            if (key.equals(ADD_PREDICATE_KEY)) {
               // 1st argument will be name and possibly arity details of built-in Predicate (
               // e.g. "arg/3" or "true")
               testBuiltInPredicate(t.getArgument(0));
               builtInPredicateCtr++;
            }
         }
      }
      // check we did manage to find some build in Predicates
      assertFalse(builtInPredicateCtr == 0);

      // produce input required by plantuml to generate uml class diagram
      Set<String> abstractClasses = new HashSet<>();
      for (Map.Entry<String, String> e : classStructure.entrySet()) {
         String key = e.getKey();
         if (key.startsWith("Abstract")) {
            abstractClasses.add(key);
         }
         System.out.println(e.getValue() + " <|-- " + key);
      }
      for (String key : abstractClasses) {
         System.out.println("abstract " + key);
      }
   }

   @SuppressWarnings("rawtypes")
   private void testBuiltInPredicate(Term t) throws Exception {
      PredicateKey key = PredicateKey.createFromNameAndArity(t);
      PredicateFactory ef = kb.getPredicateFactory(key);
      Class[] methodParameters = getMethodParameters(key);
      if (!(ef instanceof AbstractSingletonPredicate)) {
         assertClassImplementsOptimisedGetPredicateMethod(ef, new Class[] {Term[].class});
         assertClassImplementsOptimisedGetPredicateMethod(ef, methodParameters);
      }
      assertClassImplementsOptimisedEvaluateMethod(ef, methodParameters);

      Class c = ef.getClass();
      do {
         Class s = c.getSuperclass();
         if (s == Object.class) {
            return;
         }
         classStructure.put(getName(c), getName(s));
         c = s;
      } while (true);
   }

   private String getName(Class<?> c) {
      String s = c.toString();
      return s.substring(s.lastIndexOf('.') + 1);
   }

   @SuppressWarnings("rawtypes")
   private Class[] getMethodParameters(PredicateKey key) {
      List<Class> args = new ArrayList<>();
      for (int i = 1; i <= key.getNumArgs(); i++) {
         args.add(Term.class);
      }
      return args.toArray(new Class[args.size()]);
   }

   @SuppressWarnings("rawtypes")
   private void assertClassImplementsOptimisedGetPredicateMethod(PredicateFactory ef, Class[] methodParameters) {
      try {
         // Test that the getPredicate method has a return type of the actual sub-class rather than Predicate
         Method m = ef.getClass().getDeclaredMethod("getPredicate", methodParameters);
         assertSame(ef.getClass() + "'s getPredicate(" + Arrays.toString(methodParameters) + ") method returns " + m.getReturnType(), ef.getClass(), m.getReturnType());
      } catch (NoSuchMethodException e) {
         fail(e.toString());
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
            // if we can't find a matching method in the class then try it's superclass
            c = c.getSuperclass();
         }
      }
      if (success == false) {
         fail(ef.getClass() + " does not implement an evaluate method with " + methodParameters.length + " parameters");
      }
   }
}