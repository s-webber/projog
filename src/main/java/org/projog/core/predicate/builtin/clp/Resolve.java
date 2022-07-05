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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projog.clp.BruteForceSearch;
import org.projog.clp.ClpConstraintStore;
import org.projog.clp.Constraint;
import org.projog.clp.Variable;
import org.projog.core.ProjogException;
import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%?- X in 7..9, label([X])
% X=7
% X=8
% X=9
%NO

%?- X in 7..9, Y#=X*2, label([X])
% X=7
% Y=14
% X=8
% Y=16
% X=9
% Y=18
%NO

%?- Z#=X+Y, X in 7..9, Y in 4..5, label([X,Y])
% X=7
% Y=4
% Z=11
% X=8
% Y=4
% Z=12
% X=9
% Y=4
% Z=13
% X=7
% Y=5
% Z=12
% X=8
% Y=5
% Z=13
% X=9
% Y=5
% Z=14
%NO

%?- Z#=X+Y, X in 7..9, Y in 4..5, label([X,Y]), Z=12
% X = 8
% Y = 4
% Z = 12
% X = 7
% Y = 5
% Z = 12
%NO

%?- Vars=[X,Y,Z], all_different(Vars), Vars ins 1..2
% Vars=[1..2,1..2,1..2]
% X=1..2
% Y=1..2
% Z=1..2
%FAIL Vars=[X,Y,Z], all_different(Vars), Vars ins 1..2, label(Vars)

%?- X#=1, label([X])
% X=1
%?- X=1, label([X])
% X=1
%TRUE label([1])

%?- label(x)
%ERROR Expected LIST but got: ATOM with value: x
%?- label([x])
%ERROR Unexpected term of type: ATOM with value: x

%?- X#=Y, label([X,Y])
%ERROR java.lang.IllegalStateException: Variables not sufficiently bound. Too many possibilities.
*/
/**
 * <code>label([X])</code> - assigns concrete values to the given CLP variables.
 */
public final class Resolve extends AbstractPredicateFactory {
   @Override
   public Predicate getPredicate(Term arg) {
      ClpConstraintStore.Builder builder = new ClpConstraintStore.Builder();

      // find all variables in input argument, and all variables connected to them via constraints
      Set<ClpVariable> variablesList = getAllVariables(arg);
      if (variablesList.isEmpty()) {
         return PredicateUtils.TRUE; // if no variables found then return now, as nothing to resolve
      }

      // map each ClpVariable to a projog-clp Variable
      // doing this as BruteForceSearch uses projog-clp's Variable rather than projog's ClpVariable
      Map<ClpVariable, Variable> variablesSet = new HashMap<>();
      for (ClpVariable v : variablesList) {
         variablesSet.computeIfAbsent(v, notUsed -> builder.createVariable());
      }

      // for all constraints replace each ClpVariable with its corresponding Variable
      for (Constraint c : getConstraints(variablesList)) {
         Constraint replacement = c.replace(e -> {
            if (e instanceof ClpVariable) {
               Variable v = variablesSet.get(((ClpVariable) e).getTerm());
               if (v == null) {
                  throw new IllegalStateException("Have no record of " + e + " in " + variablesSet);
               }
               return v;
            } else {
               return null;
            }
         });
         builder.addConstraint(replacement);
      }

      BruteForceSearch bruteForceSearch = createBruteForceSearch(builder, variablesSet);
      return new ClpResolvePredicate(bruteForceSearch, variablesSet);
   }

   /** find all variables in input argument, and all variables connected to them via constraints */
   private Set<ClpVariable> getAllVariables(Term arg) {
      Set<ClpVariable> variables = getVariablesFromInputArgument(arg);

      List<Constraint> queue = new ArrayList<>(getConstraints(variables));
      Set<Constraint> processed = new HashSet<>();
      while (!queue.isEmpty()) {
         Constraint constraint = queue.remove(0);
         processed.add(constraint);
         constraint.walk(e -> {
            if (e instanceof ClpVariable) {
               ClpVariable v = ((ClpVariable) e).getTerm();
               if (variables.add(v)) {
                  for (Constraint c : v.getConstraints()) {
                     if (!processed.contains(c)) { // to avoid infinite loop, only add each constraint once
                        queue.add(c);
                     }
                  }
               }
            }
         });
      }

      return variables;
   }

   /** find all variables in the input argument - input argument could be a single variable or a list of variables */
   private Set<ClpVariable> getVariablesFromInputArgument(Term arg) {
      Set<ClpVariable> variables = new LinkedHashSet<>();

      if (arg.getType() == TermType.CLP_VARIABLE) {
         variables.add((ClpVariable) arg.getTerm());
         return variables;
      }

      while (arg != EmptyList.EMPTY_LIST) {
         assertType(arg, TermType.LIST);

         Term head = arg.getArgument(0);
         if (head.getType() == TermType.CLP_VARIABLE) {
            variables.add((ClpVariable) head.getTerm());
         } else if (head.getType() != TermType.INTEGER) {
            throw new ProjogException("Unexpected term of type: " + head.getType() + " with value: " + head);
         }

         arg = arg.getArgument(1);
      }

      return variables;
   }

   /** return all constraints of all of the given variables */
   private Set<Constraint> getConstraints(Set<ClpVariable> variables) {
      Set<Constraint> constraints = new LinkedHashSet<>();

      for (ClpVariable v : variables) {
         constraints.addAll(v.getConstraints());
      }

      return constraints;
   }

   private BruteForceSearch createBruteForceSearch(ClpConstraintStore.Builder builder, Map<ClpVariable, Variable> variables) {
      ClpConstraintStore environment = builder.build();

      // for each variable set its minimum and maximum values
      for (Map.Entry<ClpVariable, Variable> entry : variables.entrySet()) {
         entry.getValue().setMax(environment, entry.getKey().getMax(environment));
         entry.getValue().setMin(environment, entry.getKey().getMin(environment));
      }

      return new BruteForceSearch(environment);
   }

   private static final class ClpResolvePredicate implements Predicate {
      private final BruteForceSearch bruteForceSearch;
      private final Map<ClpVariable, Variable> variables;

      private ClpResolvePredicate(BruteForceSearch bruteForceSearch, Map<ClpVariable, Variable> variables) {
         this.bruteForceSearch = bruteForceSearch;
         this.variables = variables;
      }

      @Override
      public boolean evaluate() {
         ClpConstraintStore result = next();

         if (result != null) {
            // need to backtrack *all* variables before assigning to any, else constraints may fail
            for (ClpVariable v : variables.keySet()) {
               v.backtrack();
            }

            for (Map.Entry<ClpVariable, Variable> entry : variables.entrySet()) {
               long i = result.getValue(entry.getValue());
               // TODO don't do unify as forces resolve - add setvalue method instead
               if (!entry.getKey().unify(IntegerNumberCache.valueOf(i))) {
                  throw new RuntimeException(entry.getKey() + " " + entry.getKey().getType() + " " + i);
               }
            }

            return true;
         } else {
            return false;
         }
      }

      private ClpConstraintStore next() {
         try {
            return bruteForceSearch.next();
         } catch (RuntimeException e) {
            throw new ProjogException(e.toString(), e);
         }
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return true;
      }
   }
}
