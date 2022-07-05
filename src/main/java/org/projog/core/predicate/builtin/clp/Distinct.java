/*
 * Copyright 2022 S. Webber
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
package org.projog.core.predicate.builtin.clp;

import static org.projog.core.term.TermUtils.assertType;
import static org.projog.core.term.TermUtils.castToNumeric;

import java.util.ArrayList;
import java.util.List;

import org.projog.clp.Constraint;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.compare.NotEqualTo;
import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%FAIL all_different([X,Y,Z]), X#=1, Y#=1
%FAIL all_different([X,Y,Z]), [X,Y,Z] ins 1..2, label([X,Y,Z])
%?- all_different([X,Y,Z]), [X,Y,Z] ins 1..3
% X=1..3
% Y=1..3
% Z=1..3
%?- all_different([X,Y,Z]), [X,Y,Z] ins 1..3, Y#=2
% X={1, 3}
% Y=2
% Z={1, 3}
%?- all_different([X,Y,Z]), [X,Y,Z] ins 1..3, Y#=1
% X=2..3
% Y=1
% Z=2..3
%?- all_different([X,Y,Z]), [X,Y,Z] ins 1..3, Y#=1, Z#=Y+2
% X=2
% Y=1
% Z=3

%TRUE all_different([])
%?- all_different([X]), X#=7
% X=7
%FAIL all_different([X,X]), X in 1..3, label([X])

%TRUE all_different([6,7,8])
%FAIL all_different([6,7,6])
%FAIL all_different([6,6,8])
%FAIL all_different([6,8,8])
%FAIL all_different([7,7,7])

%?- all_different(x)
%ERROR Expected LIST but got: ATOM with value: x
%?- all_different([x])
%ERROR Unexpected term of type: ATOM with value: x
*/
/**
 * <code>all_different([X,Y,Z])</code> - enforce that none of the given CLP variables share the same value.
 */
public final class Distinct extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate(Term arg) {
      List<Expression> expressions = getOrCreateVariables(arg);
      List<Constraint> constraints = createConstraints(expressions);
      return new CoreConstraintStore(constraints).resolve();
   }

   private List<Expression> getOrCreateVariables(Term arg) {
      List<Expression> expressions = new ArrayList<>();

      while (arg != EmptyList.EMPTY_LIST) {
         assertType(arg, TermType.LIST);

         Term head = arg.getArgument(0);
         if (head.getType() == TermType.CLP_VARIABLE) {
            expressions.add((ClpVariable) head.getTerm());
         } else if (head.getType() == TermType.VARIABLE) {
            ClpVariable v = new ClpVariable();
            head.unify(v);
            expressions.add(v);
         } else if (head.getType() == TermType.INTEGER) {
            expressions.add(new FixedValue(castToNumeric(head).getLong()));
         } else {
            throw new ProjogException("Unexpected term of type: " + head.getType() + " with value: " + head);
         }

         arg = arg.getArgument(1);
      }

      return expressions;
   }

   private List<Constraint> createConstraints(List<Expression> expressions) {
      List<Constraint> constraints = new ArrayList<>();

      for (int i1 = 0; i1 < expressions.size() - 1; i1++) {
         Expression e1 = expressions.get(i1);
         for (int i2 = i1 + 1; i2 < expressions.size(); i2++) {
            Expression e2 = expressions.get(i2);
            NotEqualTo constraint = new NotEqualTo(e1, e2);
            constraints.add(constraint);
            if (e1 instanceof ClpVariable) {
               ((ClpVariable) e1).addConstraint(constraint);
            }
            if (e2 instanceof ClpVariable) {
               ((ClpVariable) e2).addConstraint(constraint);
            }
         }
      }

      return constraints;
   }
}
