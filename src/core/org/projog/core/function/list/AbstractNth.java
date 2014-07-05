package org.projog.core.function.list;

import static org.projog.core.term.ListUtils.toJavaUtilList;
import static org.projog.core.term.TermUtils.toInt;

import java.util.Collections;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

abstract class AbstractNth implements PredicateFactory {
   private final Singleton singleton = new Singleton();
   private final int startingIdx;

   public AbstractNth(int startingIdx) {
      this.startingIdx = startingIdx;
   }

   @Override
   public Predicate getPredicate(Term... args) {
      return getPredicate(args[0], args[1], args[2]);
   }

   public Predicate getPredicate(Term index, Term list, Term element) {
      if (index.getType().isVariable()) { // TODO or anon var _
         return new Retryable(toJavaUtilList(list));
      } else {
         return singleton;
      }
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      singleton.setKnowledgeBase(kb);
   }

   private class Singleton extends AbstractSingletonPredicate {
      @Override
      protected boolean evaluate(Term index, Term list, Term element) {
         List<Term> l = toJavaUtilList(list);
         if (l == null) {
            return false;
         }

         int i = toInt(index); // TODO accept 1+1
         int idx = i - startingIdx;
         if (isValidIndex(l, idx)) {
            return element.unify(l.get(idx));
         } else {
            return false;
         }
      }

      private boolean isValidIndex(List<Term> l, int idx) {
         return idx > -1 && idx < l.size();
      }
   };

   private class Retryable implements Predicate {
      final List<Term> javaUtilList;
      int ctr;

      @SuppressWarnings("unchecked")
      Retryable(List<Term> javaUtilList) {
         this.javaUtilList = javaUtilList == null ? Collections.EMPTY_LIST : javaUtilList;
      }

      @Override
      public boolean evaluate(Term... args) {
         return evaluate(args[0], args[1], args[2]);
      }

      private boolean evaluate(Term index, Term list, Term element) {
         while (couldReEvaluationSucceed()) {
            backtrack(index, list, element);
            Term t = javaUtilList.get(ctr);
            IntegerNumber n = new IntegerNumber(ctr + startingIdx);
            ctr++;
            if (index.unify(n) && element.unify(t)) {
               return true;
            }
         }
         return false;
      }

      //TODO add to TermUtils (plus 1 and 2 args versions)
      private void backtrack(Term index, Term list, Term element) {
         index.backtrack();
         list.backtrack();
         element.backtrack();
      }

      @Override
      public boolean isRetryable() {
         return true;
      }

      @Override
      public boolean couldReEvaluationSucceed() {
         return ctr < javaUtilList.size();
      }
   };
}
