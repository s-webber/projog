/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.core.udp.compiler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

final class DefaultPredicateInvocationGenerator implements PredicateInvocationGenerator {
   // TODO consider ways to improve this class through refactoring
   // In common with some other classes in org.projog.core.udp.compiler,
   // this class is large and its intentions not always immediately obvious.
   // CompiledPredicateSourceGeneratorTest (which checks actual content of generated source files)
   // and the system tests (which check actual behaviour) should give confidence when refactoring.

   @Override
   public void generate(final CompiledPredicateWriter g) {
      final Term function = g.currentClause().getCurrentFunction();
      final PredicateFactory ef = g.currentClause().getCurrentPredicateFactory();
      final boolean isRetryable = g.currentClause().isCurrentFunctionMulipleResult();
      final boolean inRetryMethod = g.currentClause().isInRetryMethod();
      final boolean firstInMethod = g.currentClause().isFirstMutlipleResultFunctionInConjunction();
      final int numberOfArguments = function.getNumberOfArguments();

      if (isRetryable) {
         if (inRetryMethod == false) {
            throw new RuntimeException("Should never have a retryable Predicate factory without out being in a retry method");
         }

         Set<Variable> variablesInCurrentFunction = g.currentClause().getVariablesInCurrentFunction();

         // only has to be unique per clause as can be reused
         final String predicateVariableName = g.classVariables().getNewMemberPredicateName(g.currentClause(), getPredicateReturnType(ef, numberOfArguments));

         String functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
         g.beginIf(predicateVariableName + "==null");
         StringBuilder methodArgs = new StringBuilder();
         final ArrayList<String> terms = new ArrayList<>();
         for (int i = 0; i < numberOfArguments; i++) {
            if (i != 0) {
               methodArgs.append(", ");
            }
            Term arg = function.getArgument(i);
            String argValue = g.outputCreateTermStatement(arg, true);
            if (arg.isImmutable()) {
               methodArgs.append(argValue);
            } else {
               String argVariable = g.classVariables().getNewTermVariable(g.currentClause());
               g.assign(argVariable, argValue + ".getTerm()");
               terms.add(argVariable);
               methodArgs.append(argVariable);
            }
         }
         g.assign(predicateVariableName, functionVariableName + ".getPredicate(" + methodArgs + ")");
         g.elseStatement();
         g.outputIfTrueThenBreak(predicateVariableName + ".couldReevaluationSucceed()==false", new Runnable() {
            @Override
            public void run() {
               for (String t : terms) {
                  g.writeStatement(t + ".backtrack();");
               }
               g.assign(predicateVariableName, "null");
            }
         });
         Map<String, String> variablesToKeepTempVersionOf = g.assignTempVariablesBackToTerm();
         g.endBlock();

         g.beginIf("!" + predicateVariableName + ".evaluate()");
         if (firstInMethod == false) {
            g.currentClause().addVariablesToBackTrack(variablesInCurrentFunction);
            g.outputBacktrack();
         }
         g.currentClause().clearVariablesToBackTrack();
         g.assign(predicateVariableName, null);
         g.exitClauseEvaluation();
         g.endBlock();

         g.assignTermToTempVariable(variablesToKeepTempVersionOf);
      } else {
         Set<Variable> variables = g.currentClause().getVariablesInCurrentFunction();
         g.currentClause().addVariablesToBackTrack(variables);
         StringBuilder methodArgs = new StringBuilder();
         for (int i = 0; i < numberOfArguments; i++) {
            if (i != 0) {
               methodArgs.append(", ");
            }
            methodArgs.append(g.outputCreateTermStatement(function.getArgument(i), true) + ".getTerm()");
         }
         String functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
         final String eval;
         if (ef instanceof AbstractSingletonPredicate) {
            // note: no need to getPredicate as know it will "return this;"
            eval = "!" + functionVariableName + ".evaluate(" + methodArgs + ")";
         } else {
            eval = "!" + functionVariableName + ".getPredicate(" + methodArgs + ").evaluate(" + methodArgs + ")";
         }
         g.outputIfTrueThenBreak(eval);
      }
   }

   private String getPredicateReturnType(PredicateFactory ef, int numberOfArguments) {
      Class<? extends PredicateFactory> predicateFactoryClass = ef.getClass();
      Method m;
      try {
         // if an overloaded version of the getPredicate method exists, with the exact number of required arguments, then use that
         m = predicateFactoryClass.getDeclaredMethod("getPredicate", getMethodParameters(numberOfArguments));
      } catch (NoSuchMethodException e) {
         try {
            // default to using the overridden varargs version of the getPredicate method (as defined by PredicateFactory)
            m = predicateFactoryClass.getDeclaredMethod("getPredicate", Term[].class);
         } catch (NoSuchMethodException e2) {
            throw new RuntimeException("No getPredicate(Term[]) method declared for: " + predicateFactoryClass, e2);
         }
      }
      return m.getReturnType().getName();
   }

   @SuppressWarnings("rawtypes")
   private Class<?>[] getMethodParameters(int numberOfArguments) {
      Class<?>[] args = new Class[numberOfArguments];
      for (int i = 0; i < numberOfArguments; i++) {
         args[i] = Term.class;
      }
      return args;
   }
}
