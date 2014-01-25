/*
 * Copyright 2013 S Webber
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
package org.projog.core.function.construct;

import java.util.ArrayList;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* SYSTEM TEST
 % %QUERY% p(a,b,c) =.. X
 % %ANSWER% X=[p,a,b,c]

 % %FALSE% p(a,b,c) =.. [p,x,y,z]

 % %FALSE% p(a,b,c) =.. []
 
 % %QUERY% [a,b,c,d] =.. X
 % %ANSWER% X=[.,a,[b,c,d]]

 % %QUERY% [a,b,c,d] =.. [X|Y]
 % %ANSWER% 
 % X=.
 % Y=[a,[b,c,d]]
 % %ANSWER%

 % %QUERY% X =.. [a,b,c,d]
 % %ANSWER% X=a(b, c, d)

 % %QUERY% X =.. [a,[b,c],d]
 % %ANSWER% X=a([b,c], d)

 % %QUERY% a+b =.. X
 % %ANSWER% X=[+,a,b]

 % %QUERY% a+b =.. [+, X, Y]
 % %ANSWER%
 % X=a
 % Y=b
 % %ANSWER%
 
 % %QUERY% a =.. [a]
 % %EXCEPTION% Expected first argument to be a variable or a predicate but got a ATOM with value: a

 % %QUERY% a+b =.. '+ X Y'
 % %EXCEPTION% Expected second argument to be a variable or a list but got a ATOM with value: + X Y

 % %QUERY% X =.. Y
 % %EXCEPTION% Both arguments are variables: X and: Y
 */
/**
 * <code>X=..L</code> - "univ".
 * <p>
 * The <code>X=..L</code> predicate (pronounced "univ") provides a way to obtain the arguments of a structure as a list
 * or construct a structure from a list of arguments.
 * </p>
 */
public final class Univ extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2) {
      TermType argType1 = arg1.getType();
      TermType argType2 = arg2.getType();
      boolean isFirstArgumentVariable = argType1.isVariable();
      boolean isFirstArgumentPredicate = argType1.isStructure();
      boolean isSecondArgumentVariable = argType2.isVariable();
      boolean isSecondArgumentList = isList(argType2);

      if (!isFirstArgumentPredicate && !isFirstArgumentVariable) {
         throw new ProjogException("Expected first argument to be a variable or a predicate but got a " + argType1 + " with value: " + arg1);
      } else if (!isSecondArgumentList && !isSecondArgumentVariable) {
         throw new ProjogException("Expected second argument to be a variable or a list but got a " + argType2 + " with value: " + arg2);
      } else if (isFirstArgumentVariable && isSecondArgumentVariable) {
         throw new ProjogException("Both arguments are variables: " + arg1 + " and: " + arg2);
      } else if (isFirstArgumentPredicate) {
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
      return Structure.createStructure(predicateName, predicateArgs.toArray(new Term[predicateArgs.size()]));
   }

   private Term toList(Term t) {
      String predicateName = t.getName();
      int numArgs = t.getNumberOfArguments();
      Term[] listArgs = new Term[numArgs + 1];
      listArgs[0] = new Atom(predicateName);
      for (int i = 0; i < numArgs; i++) {
         listArgs[i + 1] = t.getArgument(i);
      }
      return ListFactory.create(listArgs);
   }
}