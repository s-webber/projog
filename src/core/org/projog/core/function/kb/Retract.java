package org.projog.core.function.kb;

import org.projog.core.KnowledgeBase;
import org.projog.core.term.Term;

/* TEST
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
 */
/**
 * <code>retract(X)</code> - remove clauses from the knowledge base.
 * <p>
 * The first clause that <code>X</code> matches is removed from the knowledge base. When an attempt is made to
 * re-satisfy the goal, the next clause that <code>X</code> matches is removed. <code>X</code> must be suitably
 * instantiated that the predicate of the clause can be determined.
 * </p>
 */
public final class Retract extends AbstractUserDefinedPredicateInspectionFunction {
   public Retract() {
   }

   protected Retract(KnowledgeBase kb) {
      setKnowledgeBase(kb);
   }

   @Override
   public Retract getPredicate(Term... args) {
      return getPredicate(args[0]);
   }

   public Retract getPredicate(Term arg) {
      return new Retract(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   public boolean evaluate(Term arg) {
      return internalEvaluate(arg, null);
   }

   @Override
   protected boolean doRemoveMatches() {
      return true;
   }
}