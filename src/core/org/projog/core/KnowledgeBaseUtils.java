package org.projog.core;

import static org.projog.core.KnowledgeBaseResources.getKnowledgeBaseResources;

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
      return getKnowledgeBaseResources(kb).getResource(ProjogEventsObservable.class);
   }

   public static ProjogProperties getProjogProperties(KnowledgeBase kb) {
      return getKnowledgeBaseResources(kb).getResource(ProjogProperties.class);
   }

   public static Operands getOperands(KnowledgeBase kb) {
      return getKnowledgeBaseResources(kb).getResource(Operands.class);
   }

   public static TermFormatter getTermFormatter(KnowledgeBase kb) {
      return getKnowledgeBaseResources(kb).getResource(TermFormatter.class);
   }

   public static SpyPoints getSpyPoints(KnowledgeBase kb) {
      return getKnowledgeBaseResources(kb).getResource(SpyPoints.class);
   }
}