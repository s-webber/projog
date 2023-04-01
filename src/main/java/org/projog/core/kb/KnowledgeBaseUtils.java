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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.projog.core.math.ArithmeticOperator;
import org.projog.core.parser.ProjogSourceReader;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Helper methods for performing common tasks on {@link KnowledgeBase} instances.
 */
public final class KnowledgeBaseUtils {
   /**
    * The functor of structures representing conjunctions ({@code ,}).
    */
   public static final String CONJUNCTION_PREDICATE_NAME = ",";
   /**
    * The functor of structures representing implications ({@code :-}).
    */
   public static final String IMPLICATION_PREDICATE_NAME = ":-";
   /**
    * The functor of structures representing questions (i.e. queries) ({@code ?-}).
    */
   public static final String QUESTION_PREDICATE_NAME = "?-";

   /**
    * Private constructor as all methods are static.
    */
   private KnowledgeBaseUtils() {
      // do nothing
   }

   /**
    * Constructs a new {@code KnowledgeBase} object using {@link ProjogDefaultProperties}
    */
   public static KnowledgeBase createKnowledgeBase() {
      return createKnowledgeBase(new ProjogDefaultProperties());
   }

   /**
    * Constructs a new {@code KnowledgeBase} object using the specified {@link ProjogProperties}
    */
   public static KnowledgeBase createKnowledgeBase(ProjogProperties projogProperties) {
      return new KnowledgeBase(projogProperties);
   }

   /**
    * Consults the {@link ProjogProperties#getBootstrapScript()} for the {@code KnowledgeBase}.
    * <p>
    * This is a way to configure a new {@code KnowledgeBase} (i.e. plugging in {@link ArithmeticOperator} and
    * {@link PredicateFactory} instances).
    * <p>
    * When using {@link ProjogDefaultProperties} the resource parsed will be {@code projog-bootstrap.pl} (contained in
    * {@code projog-core.jar}).
    *
    * @see ProjogSourceReader#parseResource(KnowledgeBase, String)
    */
   public static void bootstrap(KnowledgeBase kb) {
      String bootstrapScript = kb.getProjogProperties().getBootstrapScript();
      ProjogSourceReader.parseResource(kb, bootstrapScript);
   }

   /**
    * Returns list of all user defined predicates with the specified name.
    */
   public static List<PredicateKey> getPredicateKeysByName(KnowledgeBase kb, String predicateName) {
      List<PredicateKey> matchingKeys = new ArrayList<>();
      for (PredicateKey key : kb.getPredicates().getUserDefinedPredicates().keySet()) {
         if (predicateName.equals(key.getName())) {
            matchingKeys.add(key);
         }
      }
      return matchingKeys;
   }

   /**
    * Returns {@code true} if the specified {@link Term} represents a question or directive, else {@code false}.
    * <p>
    * A {@link Term} is judged to represent a question if it is a structure a single argument and with a functor
    * {@link #QUESTION_PREDICATE_NAME} or {@link #IMPLICATION_PREDICATE_NAME}.
    */
   public static boolean isQuestionOrDirectiveFunctionCall(Term t) {
      return t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 1 && (QUESTION_PREDICATE_NAME.equals(t.getName()) || IMPLICATION_PREDICATE_NAME.equals(t.getName()));
   }

   /**
    * Returns {@code true} if the predicate represented by the specified {@link Term} never succeeds on re-evaluation.
    */
   public static boolean isSingleAnswer(KnowledgeBase kb, Term term) {
      if (term.getType().isVariable()) {
         return false;
      } else {
         PredicateFactory ef = kb.getPredicates().getPreprocessedPredicateFactory(term);
         return !ef.isRetryable();
      }
   }

   /**
    * Returns an array of all {@link Term}s that make up the conjunction represented by the specified {@link Term}.
    * <p>
    * If the specified {@link Term} does not represent a conjunction then it will be used as the only element in the
    * returned array.
    */
   public static Term[] toArrayOfConjunctions(Term t) {
      List<Term> l = new ArrayList<>();
      while (isConjunction(t)) {
         l.add(t.getArgs()[0]);
         t = t.getArgs()[1];
      }
      l.add(t);
      return l.toArray(new Term[l.size()]);
   }

   /**
    * Returns {@code true} if the specified {@link Term} represent a conjunction, else {@code false}.
    * <p>
    * A {@link Term} is judged to represent a conjunction if is a structure with a functor of
    * {@link #CONJUNCTION_PREDICATE_NAME} and exactly two arguments.
    */
   public static boolean isConjunction(Term t) {
      // is relying on assumption that conjunctions are only, and always, represented by a comma
      return t.getType() == TermType.STRUCTURE && CONJUNCTION_PREDICATE_NAME.equals(t.getName()) && t.getArgs().length == 2;
   }

   /**
    * Returns a new object created using reflection.
    * <p>
    * The {@code input} parameter can be in one of two formats:
    * <ol>
    * <li>The class name - e.g. {@code java.lang.String} - this will cause an attempt to create a new instance of the
    * specified class using its no argument constructor.</li>
    * <li>The class name <i>and</i> a method name (separated by a {@code /}) - e.g.
    * {@code java.util.Calendar/getInstance} - this will cause an attempt to create a new instance of the class by
    * invoking the specified method (as a no argument static method) of the specified class.</li>
    * </ol>
    */
   public static <T> T instantiate(KnowledgeBase knowledgeBase, String input) throws ReflectiveOperationException {
      T result = instantiate(input);

      if (result instanceof KnowledgeBaseConsumer) {
         ((KnowledgeBaseConsumer) result).setKnowledgeBase(knowledgeBase);
      }

      return result;
   }

   // TODO share with KnowledgeBaseServiceLocator.newInstance pass KnowledgeBase to constructor
   @SuppressWarnings("unchecked")
   private static <T> T instantiate(String input) throws ReflectiveOperationException {
      T result;
      int slashPos = input.indexOf('/');
      if (slashPos != -1) {
         String className = input.substring(0, slashPos);
         String methodName = input.substring(slashPos + 1);
         Method m = Class.forName(className).getMethod(methodName);
         result = (T) m.invoke(null);
      } else {
         result = (T) Class.forName(input).newInstance();
      }
      return result;
   }
}
