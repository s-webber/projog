package org.projog.core.function.db;

import static org.projog.core.KnowledgeBaseServiceLocator.getServiceLocator;
import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;

import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY recordz(a,q,X)
 %ANSWER X=0
 
 % Note: recordz/2 is equivalent to calling recordz/3 with the third argument as an anonymous variable.
 %TRUE recordz(a,w)
 
 %QUERY recorded(X,Y,Z)
 %ANSWER
 % X=a
 % Y=q
 % Z=0
 %ANSWER
 %ANSWER
 % X=a
 % Y=w
 % Z=1
 %ANSWER
 
 % Note: recorded/2 is equivalent to calling recorded/3 with the third argument as an anonymous variable.
 %QUERY recorded(a,Y)
 %ANSWER Y=q
 %ANSWER Y=w
 */
/**
 * <code>recordz(X,Y,Z)</code> - associates a term with a key.
 * <p>
 * <code>recordz(X,Y,Z)</code> associates <code>Y</code> with <code>X</code>. The unique reference for this association
 * will be unified with <code>Z</code>. <code>Y</code> is added to the end of the list of terms already associated with
 * <code>X</code>.
 */
public final class Recordz extends AbstractSingletonPredicate {
   private RecordedDatabase database;

   @Override
   public void init() {
      database = getServiceLocator(getKnowledgeBase()).getInstance(RecordedDatabase.class);
   }

   @Override
   public boolean evaluate(Term key, Term value) {
      return evaluate(key, value, ANONYMOUS_VARIABLE);
   }

   @Override
   public boolean evaluate(Term key, Term value, Term reference) {
      if (!reference.getType().isVariable()) {
         return false;
      }
      PredicateKey k = PredicateKey.createForTerm(key);
      Term result = database.add(k, value);
      return reference.unify(result);
   }
}