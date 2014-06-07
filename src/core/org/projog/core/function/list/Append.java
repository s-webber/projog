package org.projog.core.function.list;

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %TRUE append([a,b,c], [d,e,f], [a,b,c,d,e,f])
 
 %QUERY append([a,b,c], [d,e,f], X)
 %ANSWER X=[a,b,c,d,e,f]
 */
/**
 * <code>append(X,Y,Z)</code> - concatenates two lists.
 * <p>
 * The <code>append(X,Y,Z)</code> goal succeeds if the concatenation of lists
 * <code>X</code> and <code>Y</code> matches the list <code>Z</code>.
 * </p>
 */
public final class Append extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(final Term prefix, final Term suffix, final Term concatenated) {
      assertList(prefix);
      assertList(suffix);

      java.util.List<Term> javaList = toJavaUtilList(prefix);
      javaList.addAll(toJavaUtilList(suffix));

      return concatenated.unify(createList(javaList));
   }

   private void assertList(Term t) {
      if (t.getType() != TermType.LIST && t.getType() != TermType.EMPTY_LIST) {
         throw new ProjogException("Expected list but got: " + t);
      }
   }
}
