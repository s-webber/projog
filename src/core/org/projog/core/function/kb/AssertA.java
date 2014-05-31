package org.projog.core.function.kb;

import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/* TEST
 %QUERY X=p(1), asserta(X), asserta(p(2)), asserta(p(3))
 %ANSWER X=p(1)
 %QUERY p(X)
 %ANSWER X=3
 %ANSWER X=2
 %ANSWER X=1

 %QUERY retract(p(X))
 %ANSWER X=3
 %ANSWER X=2
 %ANSWER X=1
 */
/**
 * <code>asserta(X)</code> - adds a clause to the front of the knowledge base.
 * <p>
 * <code>asserta(X)</code> adds the clause <code>X</code> to the front of the knowledge base. <code>X</code> must be
 * suitably instantiated that the predicate of the clause can be determined.
 * </p>
 * <p>
 * This is <i>not</i> undone as part of backtracking.
 * </p>
 */
public final class AssertA extends AbstractAssertFunction {
   @Override
   protected void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel) {
      userDefinedPredicate.addFirst(clauseModel);
   }
}