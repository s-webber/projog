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

import static org.projog.core.term.TermType.ATOM;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
% Examples of when all three terms are atoms:
%TRUE atom_concat(abc, def, abcdef)
%TRUE atom_concat(a, bcdef, abcdef)
%TRUE atom_concat(abcde, f, abcdef)
%TRUE atom_concat(abcdef, '', abcdef)
%TRUE atom_concat('', abcdef, abcdef)
%TRUE atom_concat('', '', '')
%FAIL atom_concat(ab, def, abcdef)
%FAIL atom_concat(abc, ef, abcdef)

% Examples of when first term is a variable:
%?- atom_concat(abc, X, abcdef)
% X=def
%?- atom_concat(abcde, X, abcdef)
% X=f
%?- atom_concat(a, X, abcdef)
% X=bcdef
%?- atom_concat('', X, abcdef)
% X=abcdef
%?- atom_concat(abcdef, X, abcdef)
% X=

% Examples of when second term is a variable:
%?- atom_concat(X, def, abcdef)
% X=abc
%?- atom_concat(X, f, abcdef)
% X=abcde
%?- atom_concat(X, bcdef, abcdef)
% X=a
%?- atom_concat(X, abcdef, abcdef)
% X=
%?- atom_concat(X, '', abcdef)
% X=abcdef

% Examples of when third term is a variable:
%?- atom_concat(abc, def, X)
% X=abcdef
%?- atom_concat(a, bcdef, X)
% X=abcdef
%?- atom_concat(abcde, f, X)
% X=abcdef
%?- atom_concat(abcdef, '', X)
% X=abcdef
%?- atom_concat('', abcdef, X)
% X=abcdef
%?- atom_concat('', '', X)
% X=

% Examples of when first and second terms are variables:
%?- atom_concat(X, Y, abcdef)
% X=
% Y=abcdef
% X=a
% Y=bcdef
% X=ab
% Y=cdef
% X=abc
% Y=def
% X=abcd
% Y=ef
% X=abcde
% Y=f
% X=abcdef
% Y=
%?- atom_concat(X, Y, a)
% X=
% Y=a
% X=a
% Y=
%?- atom_concat(X, Y, '')
% X=
% Y=

% Examples when combination of term types cause failure:
%?- atom_concat(X, Y, Z)
%ERROR Expected an atom but got: VARIABLE with value: Z
%?- atom_concat('', Y, Z)
%ERROR Expected an atom but got: VARIABLE with value: Z
%?- atom_concat(X, '', Z)
%ERROR Expected an atom but got: VARIABLE with value: Z
%FAIL atom_concat(a, b, c)
%FAIL atom_concat(a, '', '')
%FAIL atom_concat('', b, '')
%FAIL atom_concat('', '', c)
*/
/**
 * <code>atom_concat(X, Y, Z)</code> - concatenates atom names.
 * <p>
 * <code>atom_concat(X, Y, Z)</code> succeeds if the name of atom <code>Z</code> matches the concatenation of the names
 * of atoms <code>X</code> and <code>Y</code>.
 * </p>
 */
public final class AtomConcat extends AbstractPredicateFactory {
   @Override
   protected Predicate getPredicate(Term prefix, Term suffix, Term combined) {
      if (prefix.getType().isVariable() && suffix.getType().isVariable()) {
         return new Retryable(prefix, suffix, getAtomName(combined));
      } else {
         boolean result = evaluate(prefix, suffix, combined);
         return PredicateUtils.toPredicate(result);
      }
   }

   private boolean evaluate(Term arg1, Term arg2, Term arg3) { // TODO rename arguments
      assertAtomOrVariable(arg1);
      assertAtomOrVariable(arg2);
      assertAtomOrVariable(arg3);

      final boolean isArg1Atom = isAtom(arg1);
      final boolean isArg2Atom = isAtom(arg2);
      if (isArg1Atom && isArg2Atom) {
         final Atom concat = new Atom(arg1.getName() + arg2.getName());
         return arg3.unify(concat);
      } else {
         final String atomName = getAtomName(arg3);
         if (isArg1Atom) {
            String prefix = arg1.getName();
            return (atomName.startsWith(prefix) && arg2.unify(new Atom(atomName.substring(prefix.length()))));
         } else if (isArg2Atom) {
            String suffix = arg2.getName();
            return (atomName.endsWith(suffix) && arg1.unify(new Atom(atomName.substring(0, (atomName.length() - suffix.length())))));
         } else {
            throw new ProjogException("If third argument is not an atom then both first and second arguments must be: " + arg1 + " " + arg2 + " " + arg3);
         }
      }
   }

   private void assertAtomOrVariable(Term t) {
      final TermType type = t.getType();
      if (type != TermType.ATOM && !type.isVariable()) {
         throw new ProjogException("Expected an atom or variable but got: " + type + " with value: " + t);
      }
   }

   private boolean isAtom(Term t) {
      return t.getType() == ATOM;
   }

   private static class Retryable implements Predicate {
      final Term arg1;
      final Term arg2;
      final String combined;
      int ctr;

      Retryable(Term arg1, Term arg2, String combined) {
         this.arg1 = arg1;
         this.arg2 = arg2;
         this.combined = combined;
      }

      @Override
      public boolean evaluate() {
         while (couldReevaluationSucceed()) {
            arg1.backtrack();
            arg2.backtrack();

            Atom prefix = new Atom(combined.substring(0, ctr));
            Atom suffix = new Atom(combined.substring(ctr));
            ctr++;

            return arg1.unify(prefix) && arg2.unify(suffix);
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return ctr <= combined.length();
      }
   }
}
