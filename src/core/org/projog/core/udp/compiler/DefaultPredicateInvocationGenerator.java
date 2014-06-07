package org.projog.core.udp.compiler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

final class DefaultPredicateInvocationGenerator implements PredicateInvocationGenerator {
   // TODO consider ways to improve this class through refactoring
   // In common with some other classes in org.projog.core.udp.compiler,
   // this class is large and it's intentions not always immediately obvious.
   // CompiledPredicateSourceGeneratorTest (which checks actual content of generated source files)
   // and the system tests (which check actual behaviour) should give confidence when refactoring. 

   @Override
   public void generate(CompiledPredicateWriter g) {
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
         String PredicateVariableName = g.classVariables().getNewMemberPredicateName(g.currentClause(), getPredicateReturnType(ef, numberOfArguments));

         String functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
         g.beginIf(PredicateVariableName + "==null");
         StringBuilder methodArgs = new StringBuilder();
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
               methodArgs.append(argVariable);
            }
         }
         g.assign(PredicateVariableName, functionVariableName + ".getPredicate(" + methodArgs + ")");
         g.elseStatement();
         g.outputIfTrueThenBreak(PredicateVariableName + ".isRetryable()==false");
         Map<String, String> variablesToKeepTempVersionOf = g.assignTempVariablesBackToTerm();
         g.endBlock();

         g.beginIf("!" + PredicateVariableName + ".evaluate(" + methodArgs + ")");
         if (firstInMethod == false) {
            g.currentClause().addVariablesToBackTrack(variablesInCurrentFunction);
            g.outputBacktrack();
         }
         g.currentClause().clearVariablesToBackTrack();
         g.assign(PredicateVariableName, null);
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
            methodArgs.append(g.outputCreateTermStatement(function.getArgument(i), true));
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
      try {
         Method m = ef.getClass().getDeclaredMethod("getPredicate", getMethodParameters(numberOfArguments));
         return m.getReturnType().getName();
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e);
      }
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