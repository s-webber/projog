/*
 * Copyright 2013-2014 S. Webber
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

import static org.projog.core.term.TermUtils.createAnonymousVariable;
import static org.projog.core.term.TermUtils.getAtomName;
import static org.projog.core.term.TermUtils.toInt;

import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/* TEST
 %QUERY functor(f(a,b,c(Z)),F,N)
 %ANSWER
 % Z=UNINSTANTIATED VARIABLE
 % F=f
 % N=3
 %ANSWER

 %QUERY functor(a+b,F,N)
 %ANSWER
 % F=+
 % N=2
 %ANSWER

 %QUERY functor([a,b,c],F,N)
 %ANSWER
 % F=.
 % N=2
 %ANSWER

 %QUERY functor(atom,F,N)
 %ANSWER
 % F=atom
 % N=0
 %ANSWER

 %FALSE functor([a,b,c],'.',3)
 %FALSE functor([a,b,c],a,Z)

 %QUERY functor( X, sentence, 2)
 %ANSWER X = sentence(_, _)

 copy(Old, New) :- functor(Old, F, N), functor(New, F, N).

 %QUERY copy(sentence(a,b), X)
 %ANSWER X = sentence(_, _)
 */
/**
 * <code>functor(T,F,N)</code>
 * <p>
 * Predicate <code>functor(T,F,N)</code> means "<code>T</code> is a structure with name (functor) <code>F</code> and
 * <code>N</code> number of arguments".
 * </p>
 */
public final class Functor extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term t, Term f, Term n) {
      switch (t.getType()) {
         case ATOM:
            return f.unify(t) && n.unify(new IntegerNumber(0));
         case STRUCTURE:
         case LIST:
         case EMPTY_LIST:
            return f.unify(new Atom(t.getName())) && n.unify(new IntegerNumber(t.getNumberOfArguments()));
         case NAMED_VARIABLE:
            int numArgs = toInt(n);
            Term[] a = new Term[numArgs];
            for (int i = 0; i < numArgs; i++) {
               a[i] = createAnonymousVariable();
            }
            String functorName = getAtomName(f);
            return t.unify(Structure.createStructure(functorName, a));
         default:
            throw new ProjogException("Invalid type for first argument of Functor command: " + t.getType());
      }
   }
}
