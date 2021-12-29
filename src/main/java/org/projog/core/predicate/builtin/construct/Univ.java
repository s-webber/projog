/*
 * Copyright 2013 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.core.predicate.builtin.construct;

import java.util.ArrayList;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- p(a,b,c) =.. X
% X=[p,a,b,c]

%TRUE p(a,b,c) =.. [p,a,b,c]

%FAIL p(a,b,c) =.. [p,x,y,z]

%FAIL p(a,b,c) =.. []

%?- [a,b,c,d] =.. X
% X=[.,a,[b,c,d]]

%?- [a,b,c,d] =.. [X|Y]
% X=.
% Y=[a,[b,c,d]]

%?- X =.. [a,b,c,d]
% X=a(b, c, d)

%?- X =.. [a,[b,c],d]
% X=a([b,c], d)

%?- a+b =.. X
% X=[+,a,b]

%?- a+b =.. [+, X, Y]
% X=a
% Y=b

%TRUE a =.. [a]

%FAIL a =.. [b]

%FAIL p =.. [p,x,y,z]

%FAIL p(a,b,c) =.. [p]

%?- X =.. [a]
% X=a

%?- a+b =.. '+ X Y'
%ERROR Expected second argument to be a variable or a list but got a ATOM with value: + X Y

%?- X =.. Y
%ERROR Both arguments are variables: X and: Y
*/
/**
 * <code>X=..L</code> - "univ".
 * <p>
 * The <code>X=..L</code> predicate (pronounced "univ") provides a way to obtain the arguments of a structure as a list
 * or construct a structure or atom from a list of arguments.
 * </p>
 */
public final class Univ extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      TermType argType1 = arg1.getType();
      TermType argType2 = arg2.getType();
      boolean isFirstArgumentVariable = argType1.isVariable();
      boolean isFirstArgumentPredicate = argType1.isStructure();
      boolean isFirstArgumentAtom = argType1 == TermType.ATOM;
      boolean isSecondArgumentVariable = argType2.isVariable();
      boolean isSecondArgumentList = isList(argType2);

      if (!isFirstArgumentPredicate && !isFirstArgumentVariable && !isFirstArgumentAtom) {
         throw new ProjogException("Expected first argument to be a variable or a predicate but got a " + argType1 + " with value: " + arg1);
      } else if (!isSecondArgumentList && !isSecondArgumentVariable) {
         throw new ProjogException("Expected second argument to be a variable or a list but got a " + argType2 + " with value: " + arg2);
      } else if (isFirstArgumentVariable && isSecondArgumentVariable) {
         throw new ProjogException("Both arguments are variables: " + arg1 + " and: " + arg2);
      } else if (isFirstArgumentPredicate || isFirstArgumentAtom) {
         Term predicateAsList = toList(arg1);
         return predicateAsList.unify(arg2);
      } else {
         Term listAsPredicate = toPredicate(arg2);
         return arg1.unify(listAsPredicate);
      }
   }

   private boolean isList(TermType tt) {
      return tt == TermType.LIST || tt == TermType.EMPTY_LIST;
   }

   private Term toPredicate(Term t) {
      if (t.getArgument(0).getType() != TermType.ATOM) {
         throw new ProjogException("First argument is not an atom in list: " + t);
      }

      String predicateName = t.getArgument(0).getName();

      ArrayList<Term> predicateArgs = new ArrayList<>();
      Term arg = t.getArgument(1);
      while (arg.getType() == TermType.LIST) {
         predicateArgs.add(arg.getArgument(0));
         arg = arg.getArgument(1);
      }
      if (arg.getType() != TermType.EMPTY_LIST) {
         predicateArgs.add(arg);
      }

      if (predicateArgs.size() == 0) {
         return new Atom(predicateName);
      } else {
         return Structure.createStructure(predicateName, predicateArgs.toArray(new Term[predicateArgs.size()]));
      }
   }

   private Term toList(Term t) {
      String predicateName = t.getName();
      int numArgs = t.getNumberOfArguments();
      Term[] listArgs = new Term[numArgs + 1];
      listArgs[0] = new Atom(predicateName);
      for (int i = 0; i < numArgs; i++) {
         listArgs[i + 1] = t.getArgument(i);
      }
      return ListFactory.createList(listArgs);
   }
}
