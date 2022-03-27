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

import static org.projog.core.term.TermUtils.castToNumeric;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.projog.clp.Add;
import org.projog.clp.Expression;
import org.projog.clp.FixedValue;
import org.projog.clp.Multiply;
import org.projog.clp.Subtract;
import org.projog.core.ProjogException;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/** Constructs {@code Expression} objects from {@code Term} objects. */
final class ExpressionFactory {
   private static final Map<String, BiFunction<Expression, Expression, Expression>> EXPRESSION_MAP = new HashMap<>();
   static {
      // TODO make map configurable using projog-bootstrap.pl, rather than hardcoding here
      EXPRESSION_MAP.put("+", Add::new);
      EXPRESSION_MAP.put("-", Subtract::new);
      EXPRESSION_MAP.put("*", Multiply::new);
   }

   private ExpressionFactory() {
   }

   static Expression toExpression(Term t, Set<ClpVariable> vars) {
      if (t.getType() == TermType.CLP_VARIABLE) {
         ClpVariable e = (ClpVariable) t.getTerm();
         vars.add(e);
         return e;
      } else if (t.getType() == TermType.INTEGER) {
         return new FixedValue(castToNumeric(t).getLong());
      } else if (t.getType().isVariable()) {
         ClpVariable c = new ClpVariable();
         t.unify(c);
         vars.add(c);
         return c;
      } else if (t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 2) {
         BiFunction<Expression, Expression, Expression> constructor = EXPRESSION_MAP.get(t.getName());
         if (constructor == null) {
            throw new ProjogException("Invalid term for a CLP expression: " + t);
         }
         return constructor.apply(toExpression(t.getArgument(0), vars), toExpression(t.getArgument(1), vars));
      } else {
         throw new ProjogException("Invalid term for a CLP expression: " + t);
      }
   }
}
