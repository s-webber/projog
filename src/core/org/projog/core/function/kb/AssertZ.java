package org.projog.core.function.kb;

import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/* TEST
 %QUERY X=p(1), assertz(X), assertz(p(2)), assertz(p(3))
 %ANSWER X=p(1)
 %QUERY p(X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3

 %QUERY retract(p(X))
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 
  % Note: "assert" is a synonym for "assertz".
  %FALSE z(X)
  %TRUE assert(z(a)), assert(z(b)), assert(z(c))
  %QUERY z(X)
  %ANSWER X=a
  %ANSWER X=b
  %ANSWER X=c
 */
/**
 * <code>assertz(X)</code> - adds a clause to the end of the knowledge base.
 * <p>
 * <code>assertz(X)</code> adds the clause <code>X</code> to the end of the knowledge base. <code>X</code> must be
 * suitably instantiated that the predicate of the clause can be determined.
 * </p>
 * <p>
 * This is <i>not</i> undone as part of backtracking.
 * </p>
 */
public final class AssertZ extends AbstractAssertFunction {
   @Override
   protected void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel) {
      userDefinedPredicate.addLast(clauseModel);
   }
}