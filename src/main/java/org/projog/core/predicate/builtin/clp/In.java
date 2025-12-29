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

import org.projog.clp.FixedValue;
import org.projog.clp.VariableState;
import org.projog.clp.VariableStateResult;
import org.projog.clp.compare.NotEqualTo;
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

%?- X in 4\/6, label(X)
% X=4
% X=6
%NO

% TODO should the result be formatted as  X=5..6\/8\/12\/22..25
%?- X in 5..6\/8\/12\/22..25
% X={5, 6, 8, 12, 22, 23, 24, 25}

%?- X in 5..6\/8\/12\/22..25, label(X)
% X=5
% X=6
% X=8
% X=12
% X=22
% X=23
% X=24
% X=25
%NO

%?- X in -7..-5\/-3\/-1\/5..7, label(X)
% X=-7
% X=-6
% X=-5
% X=-3
% X=-1
% X=5
% X=6
% X=7
%NO

%?- X in 5..6\/7\/8\/9..11\/12, label(X)
% X=5
% X=6
% X=7
% X=8
% X=9
% X=10
% X=11
% X=12
%NO

%?- X in 7
% X=7

%?- a in 7..8
%ERROR Unexpected term of type: ATOM with value: a
%?- X in a
%ERROR Unexpected term of type: ATOM with value: a
%?- X in 8..7
%ERROR Minimum value > maximum value in: 8..7
%?- X in a..9
%ERROR Expected Numeric but got: ATOM with value: a
%?- X in 7..z
%ERROR Expected Numeric but got: ATOM with value: z
%?- X in 7\/7
%ERROR Maximum value >= next minimum value in: 7 7
%?- X in 7\/6
%ERROR Maximum value >= next minimum value in: 7 6
%?- X in 6..7\/7..8
%ERROR Maximum value >= next minimum value in: 7 7
%?- X in 4..6\/1..3
%ERROR Maximum value >= next minimum value in: 6 1
*/
/**
 * <code>X in 1..4</code> / <code>[X,Y,Z] ins 1..4</code> - restrict CLP variables to a range of values.
 */
public final class In extends AbstractSingleResultPredicate {
   @Override
   public boolean evaluate(Term t, Term range) {
      List<long[]> possibleValues = new ArrayList<>();

      // parse range represented by second argument
      while (range != null) {
         if (range.getType() == TermType.STRUCTURE && "\\/".equals(range.getName()) && range.getNumberOfArguments() == 2) {
            possibleValues.add(parse(range.getArgument(1)));
            range = range.getArgument(0);
         } else {
            possibleValues.add(parse(range));
            range = null;
         }
      }

      // validate values in range are in order and without any overlaps
      for (int i = 0; i < possibleValues.size(); i++) {
         long min = possibleValues.get(i)[0];
         long max = possibleValues.get(i)[1];
         if (min > max) {
            throw new ProjogException("Minimum value > maximum value in: " + min + ".." + max);
         }
         if (i != 0 && max >= possibleValues.get(i - 1)[0]) {
            throw new ProjogException("Maximum value >= next minimum value in: " + max + " " + possibleValues.get(i - 1)[0]);
         }
      }

      // apply range to all terms represented by first argument
      TermType type = t.getType();
      if (type == TermType.EMPTY_LIST) {
         return true;
      } else if (type == TermType.LIST) {
         return setAll(t, possibleValues);
      } else {
         return set(t, possibleValues);
      }
   }

   private long[] parse(Term t) {
      if (t.getType().isNumeric()) {
         return new long[] {castToNumeric(t).getLong(), castToNumeric(t).getLong()};
      } else if (t.getType() == TermType.STRUCTURE && "..".equals(t.getName()) && t.getNumberOfArguments() == 2) {
         return new long[] {castToNumeric(t.getArgument(0)).getLong(), castToNumeric(t.getArgument(1)).getLong()};
      } else {
         throw new ProjogException("Unexpected term of type: " + t.getType() + " with value: " + t);
      }
   }

   private boolean setAll(Term t, List<long[]> possibleValues) {
      while (t != EmptyList.EMPTY_LIST) {
         assertType(t, TermType.LIST);

         if (!set(t.firstArgument(), possibleValues)) {
            return false;
         }

         t = t.secondArgument();
      }

      return true;
   }

   private boolean set(Term t, List<long[]> possibleValues) {
      long min = possibleValues.get(possibleValues.size() - 1)[0];
      long max = possibleValues.get(0)[1];

      ClpVariable c;
      TermType type = t.getType();
      if (type == TermType.INTEGER) {
         return isWithinRange(castToNumeric(t).getLong(), possibleValues);
      } else if (type.isVariable()) {
         c = new ClpVariable();
         t.unify(c);
      } else if (type == TermType.CLP_VARIABLE) {
         c = (ClpVariable) t.getTerm();
      } else {
         throw new ProjogException("Unexpected term of type: " + type + " with value: " + t);
      }

      VariableState v = c.getState();
      if (v.setMin(min) == VariableStateResult.FAILED || v.setMax(max) == VariableStateResult.FAILED) {
         return false;
      }

      for (int i = possibleValues.size() - 1; i > 0; i--) {
         for (long i2 = possibleValues.get(i)[1] + 1; i2 < possibleValues.get(i - 1)[0]; i2++) {
            c.addConstraint(new NotEqualTo(c, new FixedValue(i2)));
         }
      }

      return new CoreConstraintStore(c.getConstraints()).resolve();
   }

   private boolean isWithinRange(long value, List<long[]> result) {
      for (long[] element : result) {
         long min = element[0];
         long max = element[1];
         if (value >= min && value <= max) {
            return true;
         }
      }
      return false;
   }
}
