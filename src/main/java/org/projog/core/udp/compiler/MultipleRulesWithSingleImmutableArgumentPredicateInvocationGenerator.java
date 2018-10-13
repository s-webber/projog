/*
 * Copyright 2013 S. Webber
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

import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getUnifyStatement;

import java.util.Map;

import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.udp.MultipleRulesWithSingleImmutableArgumentPredicate;

final class MultipleRulesWithSingleImmutableArgumentPredicateInvocationGenerator implements PredicateInvocationGenerator {
   // TODO consider ways to improve this class through refactoring
   // In common with some other classes in org.projog.core.udp.compiler,
   // this class is large and its intentions not always immediately obvious.
   // CompiledPredicateSourceGeneratorTest (which checks actual content of generated source files)
   // and the system tests (which check actual behaviour) should give confidence when refactoring.

   @Override
   public void generate(CompiledPredicateWriter g) {
      Term function = g.currentClause().getCurrentFunction();
      PredicateFactory ef = g.currentClause().getCurrentPredicateFactory();

      MultipleRulesWithSingleImmutableArgumentPredicate mrwsia = (MultipleRulesWithSingleImmutableArgumentPredicate) ef;
      String functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
      Term arg = function.getArgument(0);
      String ctrVarName = g.classVariables().getNewInlinedCtrVariableName();
      boolean firstUse = arg.getType() == TermType.NAMED_VARIABLE && g.classVariables().isAssignedVariable(g.getVariableId(arg)) == false;
      Runnable r = g.createOnBreakCallback(functionVariableName, function, ctrVarName);
      if (firstUse) {
         g.logMultipleRulesWithImmutableArgumentsPredicateCall(functionVariableName, ctrVarName, arg);
         String variableId = g.getVariableId(arg);
         g.outputIfTrueThenBreak(ctrVarName + ">" + (mrwsia.data.length - 1), r);
         g.classVariables().addAssignedVariable(variableId);
         g.assign(variableId, functionVariableName + ".data[" + ctrVarName + "++]");
         g.logExitInlinedPredicatePredicate(functionVariableName, function, ctrVarName);
      } else {
         Map<Term, String> tmpVars = g.getTermsThatRequireBacktrack(function);
         Map<String, String> variablesToKeepTempVersionOf = g.outputBacktrackTermArguments(tmpVars);
         String termId = tmpVars.get(arg);
         g.logMultipleRulesWithImmutableArgumentsPredicateCall(functionVariableName, ctrVarName, arg);
         g.addLine("do {");
         g.outputIfTrueThenBreak(ctrVarName + ">" + (mrwsia.data.length - 1), r);
         g.beginIf(getUnifyStatement(termId, functionVariableName + ".data[" + ctrVarName + "++]"));
         g.logExitInlinedPredicatePredicate(functionVariableName, function, ctrVarName);
         g.writeStatement("break");
         if (arg.isImmutable() == false) {
            g.elseStatement();
            g.outputBacktrack(termId);
         }
         g.endBlock();
         g.addLine("} while (true);");
         g.assignTermToTempVariable(variablesToKeepTempVersionOf);
      }

      g.currentClause().clearVariablesToBackTrack();
   }
}
