package org.projog.core.function.kb;

import java.util.Iterator;
import java.util.Map;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractRetryablePredicate;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/* TEST
 % Examples of using "clause":
 test(a,b) :- true.
 test(1,2) :- true.
 test(A,B,C) :- true.
 test([_|Y],p(1)) :- true.
 %QUERY clause(test(X,Y), Z)
 %ANSWER
 % X=a
 % Y=b
 % Z=true
 %ANSWER
 %ANSWER
 % X=1
 % Y=2
 % Z=true
 %ANSWER
 %ANSWER
 % X=[_|Y]
 % Y=p(1)
 % Z=true
 %ANSWER

 %TRUE clause(test(a,b,c), true)
 %TRUE clause(test(1,2,3), true)
 %FALSE clause(tset(1,2,3), true)

 % Examples of using "retract":

 %TRUE assertz(p(a,b,c))
 %TRUE assertz(p(1,2,3))
 %TRUE assertz(p(a,c,e))
 
 %FALSE retract(p(x,y,z))
 %FALSE retract(p(a,b,e))
 
 %QUERY p(X,Y,Z)
 %ANSWER
 % X=a
 % Y=b
 % Z=c
 %ANSWER
 %ANSWER
 % X=1
 % Y=2
 % Z=3
 %ANSWER
 %ANSWER
 % X=a
 % Y=c
 % Z=e
 %ANSWER

 %QUERY retract(p(a,Y,Z))
 %ANSWER
 % Y=b
 % Z=c
 %ANSWER
 %ANSWER
 % Y=c
 % Z=e
 %ANSWER

 %QUERY p(X,Y,Z)
 %ANSWER
 % X=1
 % Y=2
 % Z=3
 %ANSWER
 
 %QUERY retract(p(X,Y,Z))
 %ANSWER
 % X=1
 % Y=2
 % Z=3
 %ANSWER
 
 %FALSE p(X,Y,Z)
 
 % Argument must be suitably instantiated that the predicate of the clause can be determined.
 %QUERY retract(X)
 %ERROR Expected an atom or a predicate but got a NAMED_VARIABLE with value: X
 */
/**
 * <code>clause(X,Y)</code> / <code>retract(X)</code> - matches terms to existing clauses.
 * <p>
 * <code>clause(X,Y)</code> causes <code>X</code> and <code>Y</code> to be matched to the head and body of an existing
 * clause. If no clauses are found for the predicate represented by <code>X</code> then the goal fails. If there are
 * more than one that matches, the clauses will be matched one at a time as the goal is re-satisfied. <code>X</code>
 * must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 * <p>
 * <code>retract(X)</code> - remove clauses from the knowledge base. The first clause that <code>X</code> matches is
 * removed from the knowledge base. When an attempt is made to re-satisfy the goal, the next clause that <code>X</code>
 * matches is removed. <code>X</code> must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class Inspect extends AbstractRetryablePredicate {
   public static Inspect inspectClause() {
      return new Inspect(false);
   }

   public static Inspect retract() {
      return new Inspect(true);
   }

   /**
    * {@code true} if matching rules should be removed (retracted) from the knowledge base as part of calls to
    * {@link #evaluate(Term, Term)} or {@code false} if the knowledge base should remain unaltered.
    */
   private final boolean doRemoveMatches;
   private Iterator<ClauseModel> implications;

   private Inspect(boolean doRemoveMatches) {
      this.doRemoveMatches = doRemoveMatches;
   }

   @Override
   public Inspect getPredicate(Term arg) {
      return createInspect();
   }

   @Override
   public Inspect getPredicate(Term arg1, Term arg2) {
      return createInspect();
   }

   private Inspect createInspect() {
      Inspect i = new Inspect(doRemoveMatches);
      i.setKnowledgeBase(getKnowledgeBase());
      return i;
   }

   @Override
   public boolean evaluate(Term arg) {
      return evaluate(arg, null);
   }

   /**
    * @param clauseHead cannot be {@code null}
    * @param clauseBody can be {@code null}
    * @return {@code true} if there is a rule in the knowledge base whose consequent can be unified with
    * {@code clauseHead} and, if {@code clauseBody} is not {@code null}, whose antecedent can be unified with
    * {@code clauseBody}.
    */
   @Override
   public boolean evaluate(Term clauseHead, Term clauseBody) {
      if (implications == null) {
         PredicateKey key = PredicateKey.createForTerm(clauseHead);
         Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = getKnowledgeBase().getUserDefinedPredicates();
         UserDefinedPredicateFactory userDefinedPredicate = userDefinedPredicates.get(key);
         if (userDefinedPredicate == null) {
            return false;
         }
         implications = userDefinedPredicate.getImplications();
      } else {
         clauseHead.backtrack();
         if (clauseBody != null) {
            clauseBody.backtrack();
         }
      }

      while (implications.hasNext()) {
         ClauseModel clauseModel = implications.next();
         if (unifiable(clauseHead, clauseBody, clauseModel)) {
            if (doRemoveMatches) {
               implications.remove();
            }
            return true;
         }
      }
      return false;
   }

   private boolean unifiable(Term clauseHead, Term clauseBody, ClauseModel clauseModel) {
      Term consequent = clauseModel.getConsequent();
      Term antecedant = clauseModel.getAntecedant();
      if (clauseHead.unify(consequent)) {
         if (clauseBody == null || clauseBody.unify(antecedant)) {
            return true;
         } else {
            clauseHead.backtrack();
         }
      }
      return false;
   }

   @Override
   public boolean couldReEvaluationSucceed() {
      return implications == null || implications.hasNext();
   }
}
