package org.projog.core.function.list;

import static org.projog.core.term.ListFactory.createList;
import static org.projog.core.term.ListUtils.toJavaUtilList;
import static org.projog.core.term.TermUtils.backtrack;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %TRUE maplist(atom, [])
 %TRUE include(atom, [], [])
 %TRUE maplist(atom, [a])
 %TRUE include(atom, [a], [a])
 %FALSE maplist(atom, [X])
 %QUERY include(atom, [X], [])
 %ANSWER X=UNINSTANTIATED VARIABLE
 %FALSE maplist(atom, [1])
 %TRUE include(atom, [1], [])
 %TRUE maplist(integer, [1])
 %TRUE include(integer, [1], [1])

 %TRUE maplist(atom, [a,a,a])
 %QUERY include(atom, [a,a,a], X)
 %ANSWER X=[a,a,a]
 
 %TRUE maplist(atom, [a,b,c])
 %QUERY include(atom, [a,b,c],X)
 %ANSWER X=[a,b,c]

 %FALSE maplist(atom, [1,b,c])
 %QUERY include(atom, [1,b,c], X)
 %ANSWER X=[b,c]
 
 %FALSE maplist(atom, [a,2,c])
 %QUERY include(atom, [a,2,c], X)
 %ANSWER X=[a,c]
 
 %FALSE maplist(atom, [a,b,3])
 %QUERY include(atom, [a,b,3], X)
 %ANSWER X=[a,b]
 
 %FALSE maplist(atom, [a,2,3])
 %QUERY include(atom, [a,2,3], X)
 %ANSWER X=[a]

 %FALSE maplist(atom, [1,b,3])
 %QUERY include(atom, [1,b,3], X)
 %ANSWER X=[b]

 %FALSE maplist(atom, [1,2,c])
 %QUERY include(atom, [1,2,c], X)
 %ANSWER X=[c]
 
 %FALSE maplist(atom, [1,2,3])
 %QUERY include(atom, [1,2,3], X)
 %ANSWER X=[]
 
 %FALSE maplist(>(0), [3,4,2,1])
 %FALSE maplist(<(5), [3,4,2,1])
 %TRUE maplist(<(0), [3,4,2,1])
 %TRUE maplist(>(5), [3,4,2,1])
 %FALSE maplist(>(5), [3,4,5,2,1])
 %TRUE maplist(>=(5), [3,4,5,2,1])
 %FALSE maplist(>=(5), [3,4,5,2,1,6])
 %FALSE maplist(>=(5), [6,3,4,5,2,1])
 %FALSE maplist(>=(5), [3,4,5,6,2,1])
 
 %TRUE include(<(0), [5,6,1,8,7,4,2,9,3], [5,6,1,8,7,4,2,9,3])
 %QUERY include(<(0), [5,6,1,8,7,4,2,9,3], X)
 %ANSWER X=[5,6,1,8,7,4,2,9,3]
 %QUERY include(>(5), [5,6,1,8,7,4,2,9,3], X)
 %ANSWER X=[1,4,2,3]
 %QUERY include(>(7), [5,6,1,8,7,4,2,9,3], X)
 %ANSWER X=[5,6,1,4,2,3]
 %TRUE include(>(7), [5,6,1,8,7,4,2,9,3], [5,6,1,4,2,3])
 %QUERY include(=(7), [5,6,1,8,7,4,2,9,3], X)
 %ANSWER X=[7]
 %QUERY include(=(0), [5,6,1,8,7,4,2,9,3], X)
 %ANSWER X=[]
 
 %FALSE maplist(=(p(W)), [p(1),p(2),p(3)])
 %QUERY include(=(p(W)), [p(1),p(2),p(3)], Z)
 %ANSWER
 % W=1
 % Z=[p(1)]
 %ANSWER
 %QUERY include(=(p(1,A,3)), [p(W,a,4), p(X,b,3), p(X,Y,3), p(Z,c,3)], B)
 %ANSWER
 % A=b
 % B=[p(1, b, 3),p(1, b, 3)]
 % W=UNINSTANTIATED VARIABLE
 % X=1
 % Y=b
 % Z=UNINSTANTIATED VARIABLE
 %ANSWER
 
 % First argument must be an atom or structure. Second argument must be a list.
 %FALSE maplist(X, [])
 %FALSE include(X, [], Z)
 %FALSE maplist(atom, X)
 %FALSE include(atom, X, Z)
 
 % Note: "checklist" is a synonym for "maplist".
 %TRUE checklist(atom, [a,b,c])
 %FALSE checklist(atom, [a,2,c])
 
 % Note: "sublist" is a synonym for "include".
 %QUERY sublist(atom, [a,b,c], X)
 %ANSWER X=[a,b,c]
 %QUERY sublist(atom, [a,2,c], X)
 %ANSWER X=[a,c]
 */
/**
 * <code>maplist(X,Y)</code> / <code>include(X,Y,Z)</code> - determines if a goal succeeds against elements of a list.
 * <p>
 * <code>maplist(X,Y)</code> succeeds if the goal <code>X</code> can be successfully applied to each elements of the
 * list <code>Y</code>.
 * </p>
 * <p>
 * <code>include(X,Y,Z)</code> succeeds if the list <code>Z</code> consists of the elements of the list <code>Y</code>
 * for which the goal <code>X</code> can be successfully applied.
 * </p>
 */
public final class MapList extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term partiallyAppliedFunction, Term args) {
      if (!isValidArguments(partiallyAppliedFunction, args)) {
         return false;
      }

      final PredicateFactory pf = getPredicateFactory(partiallyAppliedFunction);
      for (Term arg : toJavaUtilList(args)) {
         if (!evaluate(pf, createArguments(partiallyAppliedFunction, arg))) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean evaluate(Term partiallyAppliedFunction, Term args, Term filteredOutput) {
      if (!isValidArguments(partiallyAppliedFunction, args)) {
         return false;
      }

      final List<Term> matches = new ArrayList<>();
      final PredicateFactory pf = getPredicateFactory(partiallyAppliedFunction);
      for (Term arg : toJavaUtilList(args)) {
         if (evaluate(pf, createArguments(partiallyAppliedFunction, arg))) {
            matches.add(arg);
         }
      }
      return filteredOutput.unify(createList(matches));
   }

   private boolean isValidArguments(Term partiallyAppliedFunction, Term arg) {
      return isAtomOrStructure(partiallyAppliedFunction) && isList(arg);
   }

   private boolean isAtomOrStructure(Term arg) {
      TermType type = arg.getType();
      return type == TermType.STRUCTURE || type == TermType.ATOM;
   }

   private boolean isList(Term arg) {
      TermType type = arg.getType();
      return type == TermType.EMPTY_LIST || type == TermType.LIST;
   }

   private PredicateFactory getPredicateFactory(Term partiallyAppliedFunction) {
      int numArgs = partiallyAppliedFunction.getNumberOfArguments() + 1;
      PredicateKey key = new PredicateKey(partiallyAppliedFunction.getName(), numArgs);
      return getKnowledgeBase().getPredicateFactory(key);
   }

   private Term[] createArguments(Term partiallyAppliedFunction, Term finalArgument) {
      int numArgs = partiallyAppliedFunction.getNumberOfArguments();
      Term[] result = new Term[numArgs + 1];
      for (int i = 0; i < numArgs; i++) {
         result[i] = partiallyAppliedFunction.getArgument(i).getTerm();
      }
      result[numArgs] = finalArgument.getTerm();
      return result;
   }

   private boolean evaluate(PredicateFactory pf, Term[] args) {
      Predicate p = pf.getPredicate(args);
      if (p.evaluate(args)) {
         return true;
      } else {
         backtrack(args);
         return false;
      }
   }
}
