package org.projog.core.function.compound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

abstract class AbstractCollectionOf extends AbstractRetryablePredicate {
   private List<Variable> variablesNotInTemplate;
   private Iterator<Entry<Key, List<Term>>> itr;

   @Override
   public final boolean evaluate(Term... args) {
      return evaluate(args[0], args[1], args[2]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public final boolean evaluate(Term template, Term goal, Term bag) {
      if (itr == null) {
         init(template, goal);
      }

      if (itr.hasNext()) {
         template.backtrack();
         Entry<Key, List<Term>> e = itr.next();
         bag.backtrack();
         bag.unify(ListFactory.create(e.getValue()));
         for (int i = 0; i < variablesNotInTemplate.size(); i++) {
            Variable v = variablesNotInTemplate.get(i);
            v.backtrack();
            v.unify(e.getKey().terms.get(i));
         }
         return true;
      } else {
         return false;
      }
   }

   private void init(Term template, Term goal) {
      variablesNotInTemplate = getVariablesNotInTemplate(template, goal);

      Predicate predicate = KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), goal);
      Term[] goalArguments = goal.getArgs();

      Map<Key, List<Term>> m = new LinkedHashMap<>();
      if (predicate.evaluate(goalArguments)) {
         do {
            Key key = new Key(variablesNotInTemplate);
            List<Term> l = m.get(key);
            if (l == null) {
               l = new ArrayList<Term>();
               m.put(key, l);
            }
            add(l, template.getTerm());
         } while (hasFoundAnotherSolution(predicate, goalArguments));
      }

      goal.backtrack();

      itr = m.entrySet().iterator();
   }

   protected abstract void add(List<Term> l, Term t);

   private List<Variable> getVariablesNotInTemplate(Term template, Term goal) {
      Set<Variable> variablesInGoal = TermUtils.getAllVariablesInTerm(goal);
      Set<Variable> variablesInTemplate = TermUtils.getAllVariablesInTerm(template);
      variablesInGoal.removeAll(variablesInTemplate);
      return new ArrayList<Variable>(variablesInGoal);
   }

   private boolean hasFoundAnotherSolution(final Predicate predicate, final Term[] goalArguments) {
      return predicate.isRetryable() && predicate.couldReEvaluationSucceed() && predicate.evaluate(goalArguments);
   }

   @Override
   public final boolean couldReEvaluationSucceed() {
      return itr == null || itr.hasNext();
   }

   /** Represents a combination of possible values for the variables contained in the goal. */
   private static class Key {
      final List<Term> terms;

      Key(List<Variable> variables) {
         terms = new ArrayList<>(variables.size());
         for (Variable v : variables) {
            terms.add(v.getTerm());
         }
      }

      @Override
      public boolean equals(Object o) {
         Key k = (Key) o;
         for (int i = 0; i < terms.size(); i++) {
            if (!terms.get(i).strictEquality(k.terms.get(i))) {
               return false;
            }
         }
         return true;
      }

      @Override
      public int hashCode() {
         // TODO is it possible to improve on returning the same hashCode for all instances?
         return 0;
      }
   }
}