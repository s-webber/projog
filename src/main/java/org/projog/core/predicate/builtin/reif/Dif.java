/*
 * Copyright 2023 S. Webber
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
package org.projog.core.predicate.builtin.reif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;
import org.projog.core.term.VariableAttribute;

/* TEST
%TRUE dif(a,b)
%FAIL dif(a,a)
%FAIL dif(X,X)
%FAIL X=Z, Y=Z, dif(X,Y)
%FAIL dif(X,Y), X=Z, Y=Z
%FAIL dif(p(X),p(X))

%FAIL dif(X,Y), X=1, Y=1
%FAIL dif(X,Y), Y=1, X=1
%?- dif(X,Y), X=1, Y=2
% X=1
% Y=2
%?- dif(X,Y), Y=1, X=2
% X=2
% Y=1

%FAIL dif(X,Y), dif(X,Z), X=1, Y=1, Z=2
%FAIL dif(X,Y), dif(X,Z), X=1, Y=2, Z=1
%?- dif(X,Y), dif(X,Z), X=1, Y=2, Z=2
% X=1
% Y=2
% Z=2

%FAIL dif(Y,X), dif(Z,X), X=1, Y=1, Z=2
%FAIL dif(Y,X), dif(Z,X), X=1, Y=2, Z=1
%?- dif(Y,X), dif(Z,X), X=1, Y=2, Z=2
% X=1
% Y=2
% Z=2

%FAIL dif(Y,X), dif(X,Z), X=1, Y=1, Z=2
%FAIL dif(Y,X), dif(X,Z), X=1, Y=2, Z=1
%?- dif(Y,X), dif(X,Z), X=1, Y=2, Z=2
% X=1
% Y=2
% Z=2

%FAIL dif(p(A,B),p(X,Y)), A=1, X=1, B=2, Y=2
%FAIL dif(p(A,B),p(X,Y)), Y=1, A=2, B=1, X=2
%FAIL A=1, X=1, B=2, Y=2, dif(p(A,B),p(X,Y))
%FAIL Y=1, A=2, B=1, X=2, dif(p(A,B),p(X,Y))
%FAIL A=1, Y=2, dif(p(A,B),p(X,Y)), X=1, B=2
%?- dif(p(A,B),p(X,Y)), A=1, B=1, X=1, Y=2
% A=1
% B=1
% X=1
% Y=2
%?- A=1, B=1, X=2, Y=1, dif(p(A,B),p(X,Y))
% A=1
% B=1
% X=2
% Y=1
%?- A=1, B=2, dif(p(A,B),p(X,Y)), X=1, Y=1
% A=1
% B=2
% X=1
% Y=1

%FAIL dif(a(z(Z,p(X))), a(z(Y,p(S)))), Z=1, Y=1, X=2, S=2

%?- dif(a(z(Z,p(X))), a(z(Y,p(S)))), Z=1, Y=1, X=2, S=1
% S = 1
% X = 2
% Y = 1
% Z = 1

%?- dif(a(z(Z,p(X))), a(z(Y,p(S)))), Z=1, Y=2, X=2, S=2
% S = 2
% X = 2
% Y = 2
% Z = 1

%FAIL dif(a(Q,W,E,R,T,Y), a(A,B,C,D,E,F)), Q=1, W=2, E=3, R=4, T=3, Y=6, A=1, B=2, C=3, D=4, E=3, F=6
%?- dif(a(Q,W,E,R,T,Y), a(A,B,C,D,E,F)), Q=1, W=1, E=1, R=1, T=1, Y=1, A=1, B=1, C=1, D=1, E=1, F=2
% Q = 1
% W = 1
% E = 1
% R = 1
% T = 1
% Y = 1
% A = 1
% B = 1
% C = 1
% D = 1
% F = 2

%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, X=2
%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, X=3
%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, Y=2
%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, Y=3
%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, Z=2
%FAIL dif(X,2), dif(Y, 3), X=Z, Z=Y, Z=3
%FAIL dif(X,2), dif(Y, 3), Z=X, Z=Y, Z=2
%FAIL dif(X,2), dif(Y, 3), Z=X, Z=Y, Z=3
%FAIL dif(X,2), dif(Y, 3), X=Z, Y=Z, Z=2
%FAIL dif(X,2), dif(Y, 3), X=Z, Y=Z, Z=3

%?- dif(X,2), dif(Y, 3), X=Z, Z=Y, X=4
% X=4
% Y=4
% Z=4
%?- dif(X,2), dif(Y, 3), X=Z, Z=Y, Y=1
% X=1
% Y=1
% Z=1
%?- dif(X,2), dif(Y, 3), X=Z, Z=Y, Z=a
% X=a
% Y=a
% Z=a

%FAIL dif(X, a(b,c)), X=a(B,C), B=b, C=c
 */
/**
 * <code>dif(X,Y)</code> - enforces restriction that the two given terms are never equal.
 * <p>
 * If <code>X</code> and <code>Y</code> cannot unify then the goal succeeds. If <code>X</code> and <code>Y</code> are
 * equal then the goal fails. If <code>X</code> and <code>Y</code> could unify then a restriction is added to prevent
 * <code>X</code> and <code>Y</code> from unifying in the future.
 */
public final class Dif extends AbstractSingleResultPredicate {
   private static final DifVariableAttribute ATTRIBUTE_KEY = new DifVariableAttribute();

   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      if (TermUtils.termsEqual(arg1, arg2)) {
         return false;
      }

      boolean isVar1 = arg1.getType().isVariable();
      boolean isVar2 = arg2.getType().isVariable();
      if (isVar1 || isVar2) {
         Term attributeElement = new org.projog.core.term.List(arg1, arg2);
         if (isVar1) {
            appendAttribute(((Variable) arg1.getTerm()), attributeElement);
         }
         if (isVar2) {
            appendAttribute(((Variable) arg2.getTerm()), attributeElement);
         }
         return true;
      }

      MyMap sharedVariables = new MyMap();
      Term copy1 = arg1.copy(sharedVariables);
      Term copy2 = arg2.copy(sharedVariables);
      if (!copy1.unify(copy2)) {
         copy1.backtrack();
         copy2.backtrack();
         return true;
      }

      List<Variable> args1 = new ArrayList<>();
      List<Term> args2 = new ArrayList<>();
      for (Variable v : sharedVariables.variables) {
         Term t = v.getTerm();
         if (v != t) {
            args1.add(v);
            args2.add(t);
         }
      }

      copy1.backtrack();
      copy2.backtrack();

      Term attributeElement = new org.projog.core.term.List(createTerm(args1), createTerm(args2));

      for (Variable v : sharedVariables.variables) {
         appendAttribute(v, attributeElement);
      }

      return true;
   }

   private Term createTerm(List<? extends Term> args) {
      switch (args.size()) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
            return args.get(0);
         case 2:
            return new org.projog.core.term.List(args.get(0), args.get(1));
         default:
            return Structure.createStructure("f", args.toArray(new Term[args.size()]));
      }
   }

   private static void appendAttribute(Variable v, Term attributeElement) {
      Term existingValue = v.getAttributeOrDefault(ATTRIBUTE_KEY, EmptyList.EMPTY_LIST);
      Term newValue = new org.projog.core.term.List(attributeElement, existingValue);
      v.putAttribute(ATTRIBUTE_KEY, newValue);
   }

   private static final class DifVariableAttribute implements VariableAttribute {
      @Override
      public String getName() {
         return "dif";
      }

      @Override
      public boolean postUnify(Variable variable, final Term input) {
         Term attributeValue = input;
         do {
            Term head = attributeValue.getArgument(0);
            if (TermUtils.termsEqual(head.getArgument(0), head.getArgument(1))) {
               return false;
            }
            attributeValue = attributeValue.getArgument(1);
         } while (attributeValue != EmptyList.EMPTY_LIST);

         MyMap sharedVariables = new MyMap();
         variable.copy(sharedVariables);
         for (Variable v : sharedVariables.variables) {
            if (v != variable) {
               attributeValue = input;
               do {
                  Term head = attributeValue.getArgument(0);
                  appendAttribute((Variable) v.getTerm(), head);
                  attributeValue = attributeValue.getArgument(1);
               } while (attributeValue != EmptyList.EMPTY_LIST);
            }
         }

         return true;
      }

      @Override
      public Term join(Term a, Term b) {
         Term result = b;

         do {
            Term head = a.getArgument(0);
            result = new org.projog.core.term.List(head, result);
            a = a.getArgument(1);
         } while (a != EmptyList.EMPTY_LIST);

         return result;
      }
   }

   private static final class MyMap implements Map<Variable, Variable> {
      Set<Variable> variables = new HashSet<>();

      @Override
      public int size() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isEmpty() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsKey(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsValue(Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Variable get(Object key) {
         Variable v = (Variable) key;
         variables.add(v);
         return v;
      }

      @Override
      public Variable put(Variable key, Variable value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Variable remove(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putAll(Map<? extends Variable, ? extends Variable> m) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<Variable> keySet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<Variable> values() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<Map.Entry<Variable, Variable>> entrySet() {
         throw new UnsupportedOperationException();
      }
   }
}