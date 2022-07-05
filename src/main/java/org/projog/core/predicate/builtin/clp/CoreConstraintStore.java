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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.VariableState;
import org.projog.clp.VariableStateResult;

/** An implementation of {@code ConstraintStore} for use in Projog. */
final class CoreConstraintStore implements ConstraintStore {
   private final List<Constraint> queue = new ArrayList<>();
   private final Set<Constraint> matched = new HashSet<>();

   CoreConstraintStore() {
   }

   CoreConstraintStore(Constraint c) {
      queue.add(c);
   }

   CoreConstraintStore(List<Constraint> c) {
      queue.addAll(c);
   }

   boolean resolve() {
      while (!queue.isEmpty()) {
         Constraint c = queue.remove(0);
         if (!matched.contains(c)) {
            ConstraintResult result = c.enforce(this);
            if (result == ConstraintResult.FAILED) {
               return false;
            }
            if (result == ConstraintResult.MATCHED) {
               matched.add(c);
            }
         }
      }
      return true;
   }

   @Override
   public long getMin(Expression id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long getMax(Expression id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ExpressionResult setValue(Expression id, long value) {
      return update(id, v -> v.setValue(value));
   }

   @Override
   public ExpressionResult setMin(Expression id, long min) {
      return update(id, v -> v.setMin(min));
   }

   @Override
   public ExpressionResult setMax(Expression id, long max) {
      return update(id, v -> v.setMax(max));
   }

   @Override
   public ExpressionResult setNot(Expression id, long not) {
      return update(id, v -> v.setNot(not));
   }

   // TODO can this be more efficient? copying ClpVariable every time and backtracking on failure
   private ExpressionResult update(Expression id, Function<VariableState, VariableStateResult> f) {
      ClpVariable original = ((ClpVariable) id).getTerm();
      ClpVariable copy = original.copy();
      VariableStateResult r = f.apply(copy.getState());
      if (r == VariableStateResult.UPDATED) {
         addConstraints(copy);
      } else if (r == VariableStateResult.FAILED) {
         original.backtrack();
      }
      return r == VariableStateResult.FAILED ? ExpressionResult.INVALID : ExpressionResult.VALID;
   }

   private void addConstraints(ClpVariable copy) {
      for (Constraint c : copy.getConstraints()) {
         if (!matched.contains(c) && !queue.contains(c)) {
            queue.add(c);
         }
      }
   }
}
