package org.projog.core.function.kb;

import org.projog.core.KnowledgeBase;
import org.projog.core.term.Term;

/* TEST
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
 */
/**
 * <code>clause(X,Y)</code> - matches terms to existing clauses.
 * <p>
 * <code>clause(X,Y)</code> causes <code>X</code> and <code>Y</code> to be matched to the head and body of an existing
 * clause. If no clauses are found for the predicate represented by <code>X</code> then the goal fails. If there are
 * more than one that matches, the clauses will be matched one at a time as the goal is re-satisfied. <code>X</code>
 * must be suitably instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class InspectClause extends AbstractUserDefinedPredicateInspectionFunction {
   public InspectClause() {
   }

   protected InspectClause(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public InspectClause getPredicate(Term arg1, Term arg2) {
      return new InspectClause(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      return internalEvaluate(arg1, arg2);
   }

   @Override
   protected boolean doRemoveMatches() {
      return false;
   }
}