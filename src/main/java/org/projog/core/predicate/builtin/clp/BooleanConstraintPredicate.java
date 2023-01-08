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

import static org.projog.core.predicate.udp.PredicateUtils.toPredicate;
import static org.projog.core.term.TermUtils.castToNumeric;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.projog.clp.Constraint;
import org.projog.clp.FixedValue;
import org.projog.clp.VariableState;
import org.projog.clp.VariableStateResult;
import org.projog.clp.bool.And;
import org.projog.clp.bool.Equivalent;
import org.projog.clp.bool.Implication;
import org.projog.clp.bool.Not;
import org.projog.clp.bool.Or;
import org.projog.clp.bool.Xor;
import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;

/* TEST
%?- X #/\ Y
% X=1
% Y=1

%?- X #\/ Y, label([X,Y])
% X = 0
% Y = 1
% X = 1
% Y = 0
% X = 1
% Y = 1
%NO

%?- X #\ Y, label([X,Y])
% X = 0
% Y = 1
% X = 1
% Y = 0
%NO

%?- #\ X
% X = 0

%?- X #<==> Y, label([X,Y])
% X = 0
% Y = 0
% X = 1
% Y = 1
%NO

%?- X #==> Y, label([X,Y])
% X = 0
% Y = 0
% X = 0
% Y = 1
% X = 1
% Y = 1
%NO

%?- X #<== Y, label([X,Y])
% X = 0
% Y = 0
% X = 1
% Y = 0
% X = 1
% Y = 1
%NO

%?- X in 4..5, Y in 5..6, Z#<==> X#<Y, label([X,Y,Z])
% X = 4
% Y = 5
% Z = 1
% X = 4
% Y = 6
% Z = 1
% X = 5
% Y = 5
% Z = 0
% X = 5
% Y = 6
% Z = 1
%NO

%?- X in 2..3, X#<==>Y
%NO

%?- X #<==> append(_,_,_)
%ERROR Cannot create CLP constraint from term: append(_, _, _)
*/
/**
 * CLP predicates for comparing boolean values.
 * <p>
 * Integer values are used to represent boolean values. 1 repesents true and 0 represents false.
 * <ul>
 * <li><code>#/\</code> and</li>
 * <li><code>#\/</code> or</li>
 * <li><code>#\</code> exclusive or</li>
 * <li><code>#&lt;==&gt;</code> equivalent</li>
 * <li><code>#==&gt;</code> implication</li>
 * </ul>
 */
public final class BooleanConstraintPredicate implements PredicateFactory, ConstraintFactory, KnowledgeBaseConsumer {
   public static BooleanConstraintPredicate equivalent() {
      return new BooleanConstraintPredicate(args -> new Equivalent(args[0], args[1]));
   }

   public static BooleanConstraintPredicate leftImpliesRight() {
      return new BooleanConstraintPredicate(args -> new Implication(args[0], args[1]));
   }

   public static BooleanConstraintPredicate rightImpliesLeft() {
      return new BooleanConstraintPredicate(args -> new Implication(args[1], args[0]));
   }

   public static BooleanConstraintPredicate and() {
      return new BooleanConstraintPredicate(args -> new And(args[0], args[1]));
   }

   public static BooleanConstraintPredicate or() {
      return new BooleanConstraintPredicate(args -> new Or(args[0], args[1]));
   }

   public static BooleanConstraintPredicate xor() {
      return new BooleanConstraintPredicate(args -> new Xor(args[0], args[1]));
   }

   public static BooleanConstraintPredicate not() {
      return new BooleanConstraintPredicate(args -> new Not(args[0]));
   }

   private final Function<Constraint[], Constraint> constraintGenerator;
   private Predicates predicates;

   private BooleanConstraintPredicate(Function<Constraint[], Constraint> constraintGenerator) {
      this.constraintGenerator = constraintGenerator;
   }

   @Override
   public Predicate getPredicate(Term[] args) {
      Set<ClpVariable> vars = new HashSet<>();
      Constraint[] constraints = new Constraint[args.length];
      for (int i = 0; i < args.length; i++) {
         constraints[i] = toConstraint(args[i], vars);
      }
      Constraint rule = constraintGenerator.apply(constraints);
      for (ClpVariable c : vars) {
         if (c.getState().isCorrupt()) {
            return PredicateUtils.FALSE;
         }

         c.addConstraint(rule);
      }
      return toPredicate(new CoreConstraintStore(rule).resolve());
   }

   private Constraint toConstraint(Term t, Set<ClpVariable> vars) {
      switch (t.getType()) {
         case VARIABLE:
            ClpVariable c = new ClpVariable();
            restrictValues(c);
            t.unify(c);
            vars.add(c);
            return c;
         case CLP_VARIABLE:
            ClpVariable e = (ClpVariable) t.getTerm();
            restrictValues(e);
            vars.add(e);
            return e;
         case INTEGER:
            return new FixedValue(castToNumeric(t).getLong());
         case ATOM:
         case STRUCTURE:
            PredicateKey key = PredicateKey.createForTerm(t);
            PredicateFactory factory = predicates.getPredicateFactory(key);
            if (factory instanceof ConstraintFactory) {
               return ((ConstraintFactory) factory).createConstraint(t.getArgs(), vars);
            } else {
               throw new ProjogException("Cannot create CLP constraint from term: " + t);
            }
         default:
            throw new ProjogException("Cannot get CLP expression for term: " + t + " of type: " + t.getType());
      }
   }

   private void restrictValues(ClpVariable c) {
      VariableState s = c.getState();
      if (s.setMin(0) != VariableStateResult.FAILED) {
         s.setMax(1);
      }
   }

   @Override
   public boolean isRetryable() {
      return false;
   }

   @Override
   public Constraint createConstraint(Term[] args, Set<ClpVariable> vars) {
      Constraint[] constraints = new Constraint[args.length];
      for (int i = 0; i < args.length; i++) {
         constraints[i] = toConstraint(args[i], vars);
      }
      return constraintGenerator.apply(constraints);
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase knowledgeBase) { // TODO pass KnowledgeBase into constructor rather than implementing KnowledgeBaseConsumer
      this.predicates = knowledgeBase.getPredicates();
   }
}
