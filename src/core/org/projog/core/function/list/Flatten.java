package org.projog.core.function.list;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
 %QUERY flatten([a,[[b]],[c]], X)
 %ANSWER X=[a,b,c]
 
 %QUERY flatten([a,b,c], X)
 %ANSWER X=[a,b,c]

 %QUERY flatten([[[[a]]],[],[],[]], X)
 %ANSWER X=[a]

 %QUERY flatten([a], X)
 %ANSWER X=[a]
 
 %QUERY flatten(a, X)
 %ANSWER X=[a]

 %QUERY flatten([[[[]]],[],[],[]], X)
 %ANSWER X=[]

 %QUERY flatten([], X)
 %ANSWER X=[]
 
 %QUERY flatten([a|b], X)
 %ANSWER X=[a,b]

 %QUERY flatten([a|[]], X)
 %ANSWER X=[a]
 
 %QUERY flatten([[a|b],[c,d|e],[f|[]],g|h], X)
 %ANSWER X=[a,b,c,d,e,f,g,h]
 
 %QUERY flatten([p([[a]]),[[[p(p())]],[p([a,b,c])]]], X)
 %ANSWER X=[p([[a]]),p(p()),p([a,b,c])]
 
 %FALSE flatten([a,b,c], [c,b,a])
 %FALSE flatten([a,b,c], [a,[b],c])
 */
/**
 * <code>flatten(X,Y)</code> - flattens a nested list.
 * <p>
 * Flattens the nested list represented by <code>X</code> and attempts to unify it with <code>Y</code>.
 */
public final class Flatten extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   public boolean evaluate(final Term original, final Term expected) {
      final Term flattenedVersion;
      switch (original.getType()) {
         case LIST:
            flattenedVersion = ListFactory.create(flattenList(original));
            break;
         case EMPTY_LIST:
            flattenedVersion = original;
            break;
         default:
            flattenedVersion = ListFactory.createList(original, EmptyList.EMPTY_LIST);
      }
      return expected.unify(flattenedVersion);
   }

   private List<Term> flattenList(final Term input) {
      List<Term> result = new ArrayList<Term>();
      Term next = input;
      while (next.getType() == TermType.LIST) {
         Term head = next.getArgument(0);
         if (head.getType() == TermType.LIST) {
            result.addAll(flattenList(head));
         } else if (head.getType() != TermType.EMPTY_LIST) {
            result.add(head);
         }

         next = next.getArgument(1);
      }
      if (next.getType() != TermType.EMPTY_LIST) {
         result.add(next);
      }
      return result;
   }
}
