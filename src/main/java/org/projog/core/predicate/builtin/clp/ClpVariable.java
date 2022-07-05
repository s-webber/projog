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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.ConstraintResult;
import org.projog.clp.ConstraintStore;
import org.projog.clp.Expression;
import org.projog.clp.ExpressionResult;
import org.projog.clp.LeafExpression;
import org.projog.clp.ReadConstraintStore;
import org.projog.clp.VariableState;
import org.projog.core.ProjogException;
import org.projog.core.math.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/** A {@code Term} that could represent a number of possible numeric values. */
final class ClpVariable implements Numeric, LeafExpression {
   private static final int TRUE = 1;
   private static final int FALSE = 0;

   private ClpVariable child;
   private final VariableState state;
   private final List<Constraint> rules;

   public ClpVariable() {
      this.state = new VariableState();
      this.rules = new ArrayList<>();
   }

   private ClpVariable(ClpVariable parent) {
      this.state = parent.state.copy();
      this.rules = new ArrayList<>(parent.rules);
   }

   private ClpVariable(VariableState state, Collection<Constraint> rules) {
      this.state = state;
      this.rules = new ArrayList<>(rules);
   }

   List<Constraint> getConstraints() {
      if (child != null) {
         throw new IllegalStateException();
      }
      return new ArrayList<>(rules);
   }

   void addConstraint(Constraint c) {
      if (child != null) {
         throw new IllegalStateException();
      }
      rules.add(c);
   }

   VariableState getState() {
      return getTerm().state;
   }

   public ClpVariable copy() {
      if (child != null) {
         throw new IllegalStateException();
      }
      ClpVariable copy = new ClpVariable(this);
      this.child = copy;
      return copy;
   }

   @Override
   public String getName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Term[] getArgs() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getNumberOfArguments() {
      return 0;
   }

   @Override
   public Term getArgument(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public TermType getType() {
      return getState().isSingleValue() ? TermType.INTEGER : TermType.CLP_VARIABLE;
   }

   @Override
   public boolean isImmutable() {
      return child == null && !state.isCorrupt() && state.isSingleValue();
   }

   @Override
   public ClpVariable copy(Map<Variable, Variable> sharedVariables) {
      ClpVariable t = getTerm();
      if (t.isImmutable()) {
         return t;
      } else {
         // TODO is there a better alternative to throwing an exception?
         throw new ProjogException(TermType.CLP_VARIABLE + " does not support copy, so is not suitable for use in this scenario");
      }
   }

   @Override
   public ClpVariable getTerm() {
      ClpVariable c = this;
      while (c.child != null) {
         c = c.child;
      }
      return c;
   }

   @Override
   public boolean unify(Term t) {
      return unifyClpVariable(getTerm(), t);
   }

   private static boolean unifyClpVariable(ClpVariable a, Term b) {
      if (a == b) {
         return true;
      } else if (b.getType() == TermType.CLP_VARIABLE) {
         ClpVariable other = (ClpVariable) b.getTerm();

         if (a.child != null || other.child != null) {
            throw new IllegalStateException();
         }

         VariableState s = VariableState.and(a.state, other.state);
         if (s == null) {
            return false;
         }

         if (s == a.state) {
            other.child = a;
         } else if (s == other.state) {
            a.child = other;
         } else {
            Set<Constraint> newRules = new LinkedHashSet<>();
            newRules.addAll(a.rules);
            newRules.addAll(other.rules);
            ClpVariable newChild = new ClpVariable(s, newRules);
            a.child = newChild;
            other.child = newChild;
         }

         return true;
      } else if (b.getType() == TermType.INTEGER) {
         return a.unifyLong(b);
      } else if (b.getType() == TermType.VARIABLE) {
         return b.unify(a);
      } else {
         return false;
      }
   }

   private boolean unifyLong(Term t) {
      long value = castToNumeric(t).getLong();
      ClpVariable copy = copy();
      CoreConstraintStore environment = new CoreConstraintStore();
      if (copy.setMin(environment, value) == ExpressionResult.INVALID || copy.setMax(environment, value) == ExpressionResult.INVALID) {
         return false;
      } else {
         return environment.resolve();
      }
   }

   @Override
   public void backtrack() {
      this.child = null;
   }

   @Override
   public long getMin(ReadConstraintStore s) {
      return getState().getMin();
   }

   @Override
   public long getMax(ReadConstraintStore s) {
      return getState().getMax();
   }

   @Override
   public ExpressionResult setNot(ConstraintStore s, long not) {
      return s.setNot(this, not);
   }

   @Override
   public ExpressionResult setMin(ConstraintStore s, long min) {
      return s.setMin(this, min);
   }

   @Override
   public ExpressionResult setMax(ConstraintStore s, long max) {
      return s.setMax(this, max);
   }

   @Override
   public ConstraintResult enforce(ConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);
      if (min > TRUE || max < FALSE) {
         throw new IllegalStateException("Expected 0 or 1");
      } else if (s.setValue(this, TRUE) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.MATCHED;
      }
   }

   @Override
   public ConstraintResult prevent(ConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);
      if (min > TRUE || max < FALSE) {
         throw new IllegalStateException("Expected 0 or 1");
      } else if (s.setValue(this, FALSE) == ExpressionResult.INVALID) {
         return ConstraintResult.FAILED;
      } else {
         return ConstraintResult.MATCHED;
      }
   }

   @Override
   public ConstraintResult reify(ReadConstraintStore s) {
      long min = getMin(s);
      long max = getMax(s);

      if (min != max) {
         return ConstraintResult.UNRESOLVED;
      } else if (min == TRUE) {
         return ConstraintResult.MATCHED;
      } else if (min == FALSE) {
         return ConstraintResult.FAILED;
      } else {
         throw new IllegalStateException("Expected 0 or 1 but got " + min);
      }
   }

   @Override
   public void walk(Consumer<Expression> r) {
      r.accept(this);
   }

   @Override
   public LeafExpression replace(Function<LeafExpression, LeafExpression> function) {
      LeafExpression r = function.apply(this);
      if (r != null) {
         return r;
      }
      return this;
   }

   @Override
   public Numeric calculate(Term[] args) {
      return this;
   }

   @Override
   public long getLong() {
      VariableState s = getState();
      if (s.isSingleValue()) {
         return s.getMax();
      } else {
         throw new ProjogException("Cannot use " + TermType.CLP_VARIABLE + " as a number as has more than one possible value: " + s);
      }
   }

   @Override
   public double getDouble() {
      return getLong();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (isImmutable() && o instanceof Numeric) {
         Numeric n = (Numeric) o;
         return n.isImmutable() && n.getType() == TermType.INTEGER && state.getMax() == n.getLong();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return isImmutable() ? Long.hashCode(state.getMax()) : super.hashCode();
   }

   @Override
   public String toString() {
      return getState().toString();
   }
}
