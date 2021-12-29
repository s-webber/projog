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

import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/* TEST
%?- functor(f(a,b,c(Z)),F,N)
% Z=UNINSTANTIATED VARIABLE
% F=f
% N=3

%?- functor(a+b,F,N)
% F=+
% N=2

%?- functor([a,b,c],F,N)
% F=.
% N=2

%?- functor(atom,F,N)
% F=atom
% N=0

%?- functor(X,x,0)
% X=x

%?- functor(X,x,1)
% X=x(_)

%?- functor(X,x,2)
% X=x(_, _)

%?- functor(X,x,3)
% X=x(_, _, _)

%TRUE functor(x,x,0)
%FAIL functor(x,x,3)

%TRUE functor(x(1,2,3),x,3)
%FAIL functor(x(1,2,3),y,3)
%FAIL functor(x(1,2,3),x,0)
%FAIL functor(x(1,2,3),x,1)
%FAIL functor(x(1,2,3),x,2)
%FAIL functor(x(1,2,3),x,4)

%FAIL functor([a,b,c],'.',3)
%FAIL functor([a,b,c],a,Z)

copy(Old, New) :- functor(Old, F, N), functor(New, F, N).

%?- copy(sentence(a,b), X)
% X=sentence(_, _)
*/
/**
 * <code>functor(T,F,N)</code>
 * <p>
 * Predicate <code>functor(T,F,N)</code> means "<code>T</code> is a structure with name (functor) <code>F</code> and
 * <code>N</code> number of arguments".
 * </p>
 */
public final class Functor extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term term, Term functor, Term arity) {
      switch (term.getType()) {
         case ATOM:
            return functor.unify(term) && arity.unify(IntegerNumberCache.ZERO);
         case STRUCTURE:
         case LIST:
         case EMPTY_LIST:
            return functor.unify(new Atom(term.getName())) && arity.unify(IntegerNumberCache.valueOf(term.getNumberOfArguments()));
         case VARIABLE:
            Term createdTerm = createTerm(functor, arity);
            return term.unify(createdTerm);
         default:
            throw new ProjogException("Invalid type for first argument of Functor command: " + term.getType());
      }
   }

   /**
    * Creates a term using the given functor (name) and arity (number of arguments).
    *
    * @param functor an atom representing the name of the term to create
    * @param arity a numeric representing the number of arguments of the term to create
    * @return if arity is 0 then an atom will be returned, else a structure will be created.
    */
   private Term createTerm(Term functor, Term arity) {
      if (functor.getType() != TermType.ATOM) {
         throw new ProjogException("Expected atom but got: " + functor.getType() + " " + functor);
      }

      int numArgs = toInt(arity);
      if (numArgs == 0) {
         return functor;
      } else {
         Term[] args = new Term[numArgs];
         for (int i = 0; i < numArgs; i++) {
            args[i] = new Variable();
         }
         return Structure.createStructure(functor.getName(), args);
      }
   }
}
