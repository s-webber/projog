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

import org.projog.clp.VariableState;
import org.projog.clp.VariableStateResult;
import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.EmptyList;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- X in 7..9
% X=7..9
%?- X in 7..7
% X=7


%?- [X,Y,Z] ins -7..9
% X=-7..9
% Y=-7..9
% Z=-7..9

%?- X in 7..9, X=7
% X=7
%?- X in 7..9, X#=7
% X=7
%?- X=8, X in 7..9
% X=8
%?- X#=8, X in 7..9
% X=8
%FAIL X in 7..9, X=6
%FAIL X in 7..9, X#=6
%FAIL X=6, X in 7..9
%FAIL X#=6, X in 7..9
%FAIL X in 7..9, X=10
%FAIL X in 7..9, X#=10
%FAIL X=10, X in 7..9
%FAIL X#=10, X in 7..9

%?- X#>=Z+2, [X,Y,Z] ins 7..9
% X=9
% Y=7..9
% Z=7
%?- [X,Y,Z] ins 7..9, X#=<Z-2
% X=7
% Y=7..9
% Z=9
%FAIL X#>Z+2, [X,Y,Z] ins 7..9
%FAIL [X,Y,Z] ins 7..9, X#<Z-2

%TRUE 7 in 7..9
%TRUE 8 in 7..9
%TRUE 9 in 7..9
%FAIL 6 in 7..9
%FAIL 10 in 7..9

%TRUE [7,9,8] ins 7..9
%TRUE [8,8,8] ins 7..9
%FAIL [7,9,6] ins 7..9
%FAIL [7,10,9] ins 7..9
%FAIL [6,9,8] ins 7..9

%?- a in 7..8
%ERROR Unexpected term of type: ATOM with value: a
%?- X in 7
%ERROR Expected a predicate with two arguments and the name: '..' but got: 7
%?- X in a
%ERROR Expected a predicate with two arguments and the name: '..' but got: a
%?- X in 8..7
%ERROR Minimum value > maximum value in: ..(8, 7)
%?- X in a..9
%ERROR Expected Numeric but got: ATOM with value: a
%?- X in 7..z
%ERROR Expected Numeric but got: ATOM with value: z
*/
/**
 * <code>X in 1..4</code> / <code>[X,Y,Z] ins 1..4</code> - restrict CLP variables to a range of values.
 */
public final class In extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate(Term t, Term range) {
      assertRange(range);
      long min = castToNumeric(range.getArgument(0)).getLong();
      long max = castToNumeric(range.getArgument(1)).getLong();
      if (min > max) {
         throw new ProjogException("Minimum value > maximum value in: " + range);
      }

      TermType type = t.getType();
      if (type == TermType.EMPTY_LIST) {
         return true;
      } else if (type == TermType.LIST) {
         return setAll(t, min, max);
      } else {
         return set(t, min, max);
      }
   }

   private void assertRange(Term t) {
      if (t.getType() != TermType.STRUCTURE || !"..".equals(t.getName()) || t.getNumberOfArguments() != 2) {
         throw new ProjogException("Expected a predicate with two arguments and the name: '..' but got: " + t);
      }
   }

   private boolean setAll(Term t, long min, long max) {
      while (t != EmptyList.EMPTY_LIST) {
         assertType(t, TermType.LIST);

         if (!set(t.getArgument(0), min, max)) {
            return false;
         }

         t = t.getArgument(1);
      }

      return true;
   }

   private boolean set(Term t, long min, long max) {
      ClpVariable c;
      TermType type = t.getType();
      if (type == TermType.INTEGER) {
         long value = castToNumeric(t).getLong();
         return value >= min && value <= max;
      } else if (type.isVariable()) {
         c = new ClpVariable();
         t.unify(c);
      } else if (type == TermType.CLP_VARIABLE) {
         c = (ClpVariable) t.getTerm();
      } else {
         throw new ProjogException("Unexpected term of type: " + type + " with value: " + t);
      }

      VariableState s = c.getState();
      return s.setMin(min) != VariableStateResult.FAILED && s.setMax(max) != VariableStateResult.FAILED && new CoreConstraintStore(c.getConstraints()).resolve();
   }
}
