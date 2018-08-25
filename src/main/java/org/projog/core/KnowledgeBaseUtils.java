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

import static org.projog.core.KnowledgeBaseServiceLocator.getServiceLocator;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.event.ProjogEventsObservable;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;
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
    * Constructs a new {@code KnowledgeBase} object using {@link ProjogSystemProperties}
    */
   public static KnowledgeBase createKnowledgeBase() {
      return createKnowledgeBase(new ProjogSystemProperties());
   }

   /**
    * Constructs a new {@code KnowledgeBase} object using the specified {@link ProjogProperties}
    */
   public static KnowledgeBase createKnowledgeBase(ProjogProperties projogProperties) {
      KnowledgeBase kb = new KnowledgeBase();
      getServiceLocator(kb).addInstance(ProjogProperties.class, projogProperties);
      return kb;
   }

   /**
    * Consults the {@link ProjogProperties#getBootstrapScript()} for the {@code KnowledgeBase}.
    * <p>
    * This is a way to configure a new {@code KnowledgeBase} (i.e. plugging in {@link Calculatable} and
    * {@link PredicateFactory} instances).
    * <p>
    * When using {@link ProjogSystemProperties} the resource parsed will be {@code projog-bootstrap.pl} (contained in
    * {@code projog-core.jar}).
    * 
    * @see ProjogSourceReader#parseResource(KnowledgeBase, String)
    */
   public static void bootstrap(KnowledgeBase kb) {
      String bootstrapScript = getProjogProperties(kb).getBootstrapScript();
      ProjogSourceReader.parseResource(kb, bootstrapScript);
   }

   /**
    * Returns list of all user defined predicates with the specified name.
    */
   public static List<PredicateKey> getPredicateKeysByName(KnowledgeBase kb, String predicateName) {
      List<PredicateKey> matchingKeys = new ArrayList<>();
      for (PredicateKey key : kb.getUserDefinedPredicates().keySet()) {
         if (predicateName.equals(key.getName())) {
            matchingKeys.add(key);
         }
      }
      return matchingKeys;
   }

   /**
    * Returns a {@link Predicate} instance for the specified {@link Term}.
    */
   public static Predicate getPredicate(KnowledgeBase kb, Term t) {
      return kb.getPredicateFactory(t).getPredicate(t.getArgs());
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
    * Returns {@code true} if the specified {@link Term} represent a {@code dynamic} function call, else {@code false}.
    * <p>
    * A {@link Term} is judged to represent a dynamic function call (i.e. a request to mark a user defined predicate as
    * "dynamic") if it is a structure with a functor of {@code dynamic} and a single argument.
    */
   public static boolean isDynamicFunctionCall(Term t) {
      return "dynamic".equals(t.getName()) && t.getNumberOfArguments() == 1;
   }

   /**
    * Returns {@code true} if the predicate represented by the specified {@link Term} never succeeds on re-evaluation.
    */
   public static boolean isSingleAnswer(KnowledgeBase kb, Term term) {
      if (term.getType().isVariable()) {
         return false;
      } else if (isConjunction(term)) {
         return isConjunctionWithSingleResult(kb, term);
      } else {
         PredicateFactory ef = kb.getPredicateFactory(term);
         return ef instanceof AbstractSingletonPredicate;
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
         l.add(0, t.getArgs()[1]);
         t = t.getArgs()[0];
      }
      l.add(0, t);
      return l.toArray(new Term[l.size()]);
   }

   private static boolean isConjunctionWithSingleResult(KnowledgeBase kb, Term antecedant) {
      Term[] functions = toArrayOfConjunctions(antecedant);
      return isAllSingleAnswerFunctions(kb, functions);
   }

   private static boolean isAllSingleAnswerFunctions(KnowledgeBase kb, Term[] functions) {
      for (Term t : functions) {
         if (!isSingleAnswer(kb, t)) {
            return false;
         }
      }
      return true;
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

   public static ProjogEventsObservable getProjogEventsObservable(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(ProjogEventsObservable.class);
   }

   public static ProjogProperties getProjogProperties(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(ProjogProperties.class);
   }

   public static Operands getOperands(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(Operands.class);
   }

   public static TermFormatter getTermFormatter(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(TermFormatter.class);
   }

   public static SpyPoints getSpyPoints(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(SpyPoints.class);
   }

   public static FileHandles getFileHandles(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(FileHandles.class);
   }

   public static Calculatables getCalculatables(KnowledgeBase kb) {
      return getServiceLocator(kb).getInstance(Calculatables.class);
   }
}
