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

import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.encodeName;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getNewListSyntax;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getNewVariableSyntax;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getUnifyStatement;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.isNoMoreThanTwoElementList;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.projog.core.KnowledgeBase;
import org.projog.core.term.EmptyList;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;

/**
 * Constructs Java source code of new {@link CompiledPredicate} classes.
 */
final class CompiledPredicateWriter extends JavaSourceWriter {
   // TODO consider ways to improve this class through refactoring
   // In common with some other classes in org.projog.core.udp.compiler,
   // this class is large and its intentions not always immediately obvious.
   // CompiledPredicateSourceGeneratorTest (which checks actual content of generated source files)
   // and the system tests (which check actual behaviour) should give confidence when refactoring.

   static final String EMPTY_LIST_SYNTAX = "EmptyList.EMPTY_LIST";
   static final String MAIN_LOOP_LABEL = "mainloop";

   private static final AtomicInteger ctr = new AtomicInteger();

   private final String className;
   private final KnowledgeBase kb;
   private final PredicateMetaData factMetaData;
   private final CompiledPredicateVariables classVariables = new CompiledPredicateVariables();
   private ClauseMetaData currentClause;
   private boolean inStaticRecursiveMethodBlock;
   private boolean needsKnowledgeBaseStaticVariable;
   private boolean needsCalculatablesStaticVariable;

   CompiledPredicateWriter(KnowledgeBase kb, List<ClauseModel> copyImplications) {
      this.kb = kb;
      this.factMetaData = new PredicateMetaData(kb, copyImplications);
      this.className = generateClassName();
   }

   ClauseMetaData currentClause() {
      return currentClause;
   }

   void setCurrentClause(ClauseMetaData currentClause) {
      this.currentClause = currentClause;
   }

   boolean isNeedsKnowledgeBaseStaticVariable() {
      return needsKnowledgeBaseStaticVariable;
   }

   void setNeedsKnowledgeBaseStaticVariable(boolean needsKnowledgeBaseStaticVariable) {
      this.needsKnowledgeBaseStaticVariable = needsKnowledgeBaseStaticVariable;
   }

   public boolean isNeedsCalculatablesStaticVariable() {
      return needsCalculatablesStaticVariable;
   }

   void setNeedsCalculatablesStaticVariable(boolean needsCalculatablesStaticVariable) {
      this.needsCalculatablesStaticVariable = needsCalculatablesStaticVariable;
   }

   String className() {
      return className;
   }

   KnowledgeBase knowledgeBase() {
      return kb;
   }

   PredicateMetaData factMetaData() {
      return factMetaData;
   }

   CompiledPredicateVariables classVariables() {
      return classVariables;
   }

   private static String generateClassName() {
      int nextId = ctr.incrementAndGet();
      return "CompiledPredicate" + nextId;
   }

   String outputCreateTermStatement(Term t, boolean reuseImmutableTerms) {
      if (t == EmptyList.EMPTY_LIST) {
         return EMPTY_LIST_SYNTAX;
      } else if (reuseImmutableTerms && t.isImmutable()) {
         String immutableTermVariableName = classVariables.getTermVariableName(t);
         return immutableTermVariableName;
      } else if (t.getType() == TermType.NAMED_VARIABLE) {
         declareVariableIfNotAlready(t, reuseImmutableTerms);
         return getVariableId(t);
      } else if (t.getType() == TermType.STRUCTURE) {
         StringBuilder sb = new StringBuilder("Structure.createStructure(");
         sb.append(encodeName(t));
         sb.append(", new Term[]{");
         boolean first = true;
         for (Term arg : t.getArgs()) {
            if (first) {
               first = false;
            } else {
               sb.append(", ");
            }
            sb.append(outputCreateTermStatement(arg, reuseImmutableTerms));
            if (arg.isImmutable() == false) {
               sb.append(".getTerm()");
            }
         }
         sb.append("})");
         return sb.toString();
      } else if (t.getType() == TermType.LIST) {
         Term head = t.getArgument(0);
         String headSyntax = outputCreateTermStatement(head, reuseImmutableTerms);
         if (head.isImmutable() == false) {
            headSyntax += ".getTerm()";
         }
         Term tail = t.getArgument(1);
         String tailSyntax = outputCreateTermStatement(tail, reuseImmutableTerms);
         if (tail.isImmutable() == false) {
            tailSyntax += ".getTerm()";
         }
         return getNewListSyntax(headSyntax, tailSyntax);
      } else if (t.getType() == TermType.ATOM) {
         return "new Atom(" + encodeName(t) + ")";
      } else if (t.getType() == TermType.INTEGER) {
         return "new IntegerNumber(" + t.getName() + "L)";
      } else if (t.getType() == TermType.FRACTION) {
         return "new DecimalFraction(" + t.getName() + ")";
      } else {
         throw new RuntimeException("unknown " + t.getType() + " " + t.getClass() + " " + t);
      }
   }

   final boolean declareVariableIfNotAlready(Term variable, boolean assign) {
      String variableId = getVariableId(variable);
      if (classVariables.addDeclaredVariable(variableId)) {
         if (classVariables.isMemberVariable(variableId)) {
            if (assign) {
               if (classVariables.addAssignedVariable(variableId)) {
                  beginIf(variableId + "==null");
                  assign(variableId, getNewVariableSyntax(variable));
                  endBlock();
               }
            }
         } else {
            if (assign) {
               if (classVariables.addAssignedVariable(variableId)) {
                  assign("final Term " + variableId, getNewVariableSyntax(variable));
               }
            } else {
               writeStatement("final Term " + variableId);
            }
         }
         return true;
      } else {
         if (assign && classVariables.addAssignedVariable(variableId)) {
            assign(variableId, getNewVariableSyntax(variable));
         }
         return false;
      }
   }

   final String getVariableId(Term variable) {
      return classVariables.getVariableId(currentClause, (Variable) variable);
   }

   final void outputIfFailThenBreak(String eval) {
      outputIfTrueThenBreak("!" + eval);
   }

   final void outputIfFailThenBreak(String eval, Runnable onBreakCallback) {
      outputIfTrueThenBreak("!" + eval, onBreakCallback);
   }

   final void outputIfTrueThenBreak(String eval) {
      outputIfTrueThenBreak(eval, null);
   }

   final void outputIfTrueThenBreak(String eval, Runnable onBreakCallback) {
      beginIf(eval);
      if (onBreakCallback != null) {
         onBreakCallback.run();
      }
      outputBacktrackAndExitClauseEvaluation();
      endBlock();
   }

   final void outputBacktrackAndExitClauseEvaluation() {
      outputBacktrack();
      exitClauseEvaluation();
   }

   @SuppressWarnings("unchecked")
   final void outputBacktrack() {
      outputBacktrack(Collections.EMPTY_SET);
   }

   final void outputBacktrack(Set<Variable> variablesToIgnore) {
      if (currentClause.isInRetryMethod() == false) {
         return;
      }

      for (Variable v : currentClause.getVariablesToBackTrack()) {
         String variableId = getVariableId(v);
         if (classVariables.isAssignedVariable(variableId) && variablesToIgnore.contains(v) == false) {
            outputBacktrack(variableId);
         } else {
            // not backtracking as not yet declared: "variableId"
         }
      }
   }

   final void exitClauseEvaluation() {
      if (currentClause.isLastCutAfterLastBacktrackPoint()) {
         if (factMetaData().isSingleResultPredicate() == false) {
            assignTrue("isCut");
         }
         exitCodeBlock();
      } else if (currentClause.isInRetryMethod() && currentClause.isFirstMutlipleResultFunctionInConjunction() == false) {
         if (currentClause.isConjunctionMulipleResult(currentClause.getConjunctionIndex())) {
            // only set conjunctionCtr if required
            assign("conjunctionCtr", currentClause.getLastBacktrackPoint());
         }
         writeStatement("break " + MAIN_LOOP_LABEL);
      } else {
         exitCodeBlock();
      }
   }

   final void exitCodeBlock() {
      if (inStaticRecursiveMethodBlock) {
         writeStatement("break");
      } else {
         returnFalse();
      }
   }

   void callUserDefinedPredicate(String compiledPredicateName, boolean isRetryable) {
      Term function = currentClause.getCurrentFunction();
      Set<Variable> variablesInCurrentFunction = currentClause.getVariablesInCurrentFunction();
      boolean firstInMethod = currentClause.isFirstMutlipleResultFunctionInConjunction();

      StringBuilder constructorArgs = new StringBuilder();
      for (int i = 0; i < function.getNumberOfArguments(); i++) {
         if (i != 0) {
            constructorArgs.append(", ");
         }
         Term a = function.getArgument(i);
         constructorArgs.append(outputCreateTermStatement(a, true));
      }
      currentClause.addVariablesToBackTrack(variablesInCurrentFunction);
      if (isRetryable) {
         String compiledPredicateVariableName = classVariables.getNewCompiledPredicateVariableName(currentClause(), compiledPredicateName);

         beginIf(compiledPredicateVariableName + "==null");

         for (Variable v : variablesInCurrentFunction) {
            String variableId = getVariableId(v);
            if (classVariables.addDeclaredVariable(variableId)) {
               classVariables.addAssignedVariable(variableId);
               assign(variableId, getNewVariableSyntax(v));
            }
         }
         String declaration = "new " + compiledPredicateName + "(" + constructorArgs + ")";
         assign(compiledPredicateVariableName, declaration);
         elseStatement();
         Map<String, String> variablesToKeepTempVersionOf = assignTempVariablesBackToTerm();
         endBlock();
         beginIf("!" + compiledPredicateVariableName + ".evaluate()");
         if (firstInMethod == false) {
            assign(compiledPredicateVariableName, null);
            outputBacktrack();
            currentClause.clearVariablesToBackTrack();
         }
         exitClauseEvaluation();
         endBlock();
         assignTermToTempVariable(variablesToKeepTempVersionOf);
      } else {
         outputStaticEvaluateCall(compiledPredicateName, constructorArgs);
      }
   }

   private void outputStaticEvaluateCall(String compiledPredicateName, StringBuilder constructorArgs) {
      outputIfFailThenBreak(compiledPredicateName + ".staticEvaluate(" + constructorArgs + ")");
   }

   void outputEqualsEvaluation() {
      Term equalsFunction = currentClause.getCurrentFunction();
      Term t1 = equalsFunction.getArgument(0);
      Term t2 = equalsFunction.getArgument(1);

      outputEqualsEvaluation(t1, t2, DUMMY);
   }

   private static final Runnable DUMMY = new Runnable() {
      @Override
      public void run() {
      }
   };

   void outputEqualsEvaluation(Term t1, Term t2, Runnable onBreakCallback) {
      if (t2.getType() == TermType.NAMED_VARIABLE) {
         Term tmp = t1;
         t1 = t2;
         t2 = tmp;
      }

      // compare "t1" to "t2"
      if (isNoMoreThanTwoElementList(t1) && isNoMoreThanTwoElementList(t2)) {
         outputEqualsEvaluation(t1.getArgument(0), t2.getArgument(0), onBreakCallback);
         outputEqualsEvaluation(t1.getArgument(1), t2.getArgument(1), onBreakCallback);
      } else if (t1.getType() == TermType.NAMED_VARIABLE && isListOfTwoVariables(t2)) {
         Set<Variable> variables = TermUtils.getAllVariablesInTerm(t1);
         variables.addAll(TermUtils.getAllVariablesInTerm(t2));
         Set<Variable> newlyDeclaredVariables = new HashSet<>();
         for (Variable v : variables) {
            if (declareVariableIfNotAlready(v, false)) {
               newlyDeclaredVariables.add(v);
            }
         }

         String arg1 = outputCreateTermStatement(t1, true);
         beginIf(arg1 + ".getType()==TermType.LIST");
         outputAssignOfUnifyListElement(t2, arg1, 0, onBreakCallback);
         outputAssignOfUnifyListElement(t2, arg1, 1, onBreakCallback);

         elseIf(arg1 + ".getType()==TermType.NAMED_VARIABLE");
         String arg2 = outputCreateTermStatement(t2, true);
         beginIf("!" + getUnifyStatement(arg1, arg2));
         onBreakCallback.run();
         outputBacktrackAndExitClauseEvaluation();
         endBlock();
         elseStatement();
         onBreakCallback.run();
         outputBacktrackAndExitClauseEvaluation();
         endBlock();
      } else if (t1.getType() == TermType.NAMED_VARIABLE) {
         boolean firstUse = declareVariableIfNotAlready(t1, false);
         String variableId = getVariableId(t1);
         String arg2 = outputCreateTermStatement(t2, true);
         if (firstUse) {
            classVariables.addAssignedVariable(variableId);
            assign(variableId, arg2);
         } else {
            outputIfFailThenBreak(getUnifyStatement(variableId, arg2), onBreakCallback);
         }
      } else {
         String arg1 = outputCreateTermStatement(t1, true);
         String arg2 = outputCreateTermStatement(t2, true);
         outputIfFailThenBreak(getUnifyStatement(arg1, arg2), onBreakCallback);
      }
   }

   private void outputAssignOfUnifyListElement(Term list, String listId, int elementId, Runnable onBreakCallback) {
      String variableId = getVariableId(list.getArgument(elementId));
      String element = listId + ".getArgument(" + elementId + ").getTerm()";
      if (isAssigned(variableId)) {
         beginIf("!" + getUnifyStatement(variableId, element));
         onBreakCallback.run();
         outputBacktrackAndExitClauseEvaluation();
         endBlock();
      } else {
         assign(variableId, element);
      }
   }

   private boolean isListOfTwoVariables(Term t) {
      return t.getType() == TermType.LIST && t.getArgument(0).getType().isVariable() && t.getArgument(1).getType().isVariable();
   }

   final Map<String, String> assignTempVariablesBackToTerm() {
      // use LinkedHashMap so order predictable (makes unit tests easier)
      Map<String, String> variablesToKeepTempVersionOf = new LinkedHashMap<>();
      for (String variableId : getVariablesToKeepTempVersionOf()) {
         String tmpVariableName = classVariables.getNewTempVariableName();
         assign(variableId, tmpVariableName);
         variablesToKeepTempVersionOf.put(tmpVariableName, variableId);
      }
      return variablesToKeepTempVersionOf;
   }

   final void assignTermToTempVariable(Map<String, String> variablesToKeepTempVersionOf) {
      for (Map.Entry<String, String> e : variablesToKeepTempVersionOf.entrySet()) {
         String tmpVariableName = e.getKey();
         String variableId = e.getValue();
         assign(tmpVariableName, variableId);
         assign(variableId, variableId + ".getTerm()");
      }
   }

   /**
    * Return IDs of variables that need to be kept track of.
    * <p>
    * Returns variables already defined (including in current clause) that are reused in future clauses of this same
    * rule.
    */
   @SuppressWarnings("unchecked")
   private final Set<String> getVariablesToKeepTempVersionOf() {
      if (currentClause.getConjunctionIndex() == currentClause.getConjunctionCount()) {
         return Collections.EMPTY_SET;
      }

      // LinkedHashSet to make order predictable (makes unit tests easier)
      Set<String> alreadyDeclaredVariables = new LinkedHashSet<>();
      Set<Variable> variables1 = TermUtils.getAllVariablesInTerm(currentClause.getConsequent());
      for (Variable v : variables1) {
         String variableId = getVariableId(v);
         alreadyDeclaredVariables.add(variableId);
      }

      for (int i = 0; i <= currentClause.getConjunctionIndex(); i++) {
         Set<Variable> variables = currentClause.getVariablesInConjunction(i);
         for (Variable v : variables) {
            String variableId = getVariableId(v);
            alreadyDeclaredVariables.add(variableId);
         }
      }

      int start = currentClause.getConjunctionIndex();
      if (start < 0) {
         start = 0;
      }
      Set<String> usedLaterVariables = new HashSet<>();
      for (int i = start; i < currentClause.getConjunctionCount(); i++) {
         Set<Variable> variables = currentClause.getVariablesInConjunction(i);
         for (Variable v : variables) {
            String variableId = getVariableId(v);
            usedLaterVariables.add(variableId);
         }
      }

      // LinkedHashSet to make order predictable (makes unit tests easier)
      Set<String> result = new LinkedHashSet<>();
      for (String variableId : alreadyDeclaredVariables) {
         if (usedLaterVariables.contains(variableId) && classVariables.isMemberVariable(variableId)) {
            result.add(variableId);
         }
      }
      return result;
   }

   final Map<Term, String> getTermsThatRequireBacktrack(Term function) {
      Set<Variable> x = getTermArgumentsThatAreCurrentlyUnassignedAndNotReusedWithinTheTerm(function);
      // use LinkedHashMap so order is predictable - purely so unit tests are easier
      Map<Term, String> tempVars = new LinkedHashMap<>();

      for (int i = 0; i < function.getNumberOfArguments(); i++) {
         Term arg = function.getArgument(i);
         if (x.contains(arg)) {
            // ignore
         } else {
            String id = outputCreateTermStatement(arg, true);
            if (arg.isImmutable() == false) {
               id = classVariables.getNewTempVariableName();
            }
            tempVars.put(arg, id);
         }
      }
      return tempVars;
   }

   /**
    * Identifies cases where a variable is reused multiple times in a clause before it is assigned.
    * <p>
    * e.g. <code>p(X,X,X)</code>
    */
   final Set<Variable> getTermArgumentsThatAreCurrentlyUnassignedAndNotReusedWithinTheTerm(Term function) {
      // LinkedHashSet so predictable order (makes unit tests easier)
      final Set<Variable> result = new LinkedHashSet<>();
      final Set<Variable> duplicates = new HashSet<>();
      for (int i = 0; i < function.getNumberOfArguments(); i++) {
         Term t = function.getArgument(i);
         if (t.getType() == TermType.NAMED_VARIABLE) {
            Variable v = (Variable) t;
            if (classVariables.isAssignedVariable(getVariableId(v)) == false && duplicates.contains(v) == false) {
               boolean newEntry = result.add(v);
               if (newEntry == false) {
                  duplicates.add(v);
                  result.remove(v);
               }
            }
         }
      }
      return result;
   }

   Map<String, String> outputBacktrackTermArguments(Map<Term, String> termsThatRequireBacktrack) {
      beginIf(classVariables.getCurrentInlinedCtrVariableName() + "!=0");
      Map<String, String> variablesToKeepTempVersionOf = assignTempVariablesBackToTerm();

      for (Map.Entry<Term, String> e : termsThatRequireBacktrack.entrySet()) {
         if (e.getKey().isImmutable() == false) {
            outputBacktrack(e.getValue());
         }
      }

      addLine("} else {");

      for (Map.Entry<Term, String> e : termsThatRequireBacktrack.entrySet()) {
         if (e.getKey().isImmutable() == false) {
            String createTermStatement = outputCreateTermStatement(e.getKey(), true);
            assign(e.getValue(), createTermStatement + ".getTerm()");
         }
      }

      endBlock();

      return variablesToKeepTempVersionOf;
   }

   final Runnable createOnBreakCallback(final String functionVariableName, final Term function, final String ctrVarName) {
      return new Runnable() {
         @Override
         public void run() {
            if (ctrVarName != null) {
               assign(ctrVarName, 0);
            }
            logInlinedPredicatePredicate("Fail", functionVariableName, function);
         }
      };
   }

   final void logMultipleRulesWithImmutableArgumentsPredicateCall(String functionVariableName, String ctrVarName, Term... arguments) {
      if (isSpyPointsEnabled() == false) {
         return;
      }

      beginIf(ctrVarName + "==0");
      log("Call", functionVariableName, arguments);
      addLine("} else {");
      log("Redo", functionVariableName, arguments);
      endBlock();
   }

   void logExitInlinedPredicatePredicate(String functionVariableName, Term function, String clauseIdx) {
      log("Exit", functionVariableName, clauseIdx, function.getArgs());
   }

   void logInlinedPredicatePredicate(String type, String functionVariableName, Term function) {
      log(type, functionVariableName, function.getArgs());
   }

   private void log(String type, String functionVariableName, Term... arguments) {
      log(type, functionVariableName, null, arguments);
   }

   private void log(String type, String functionVariableName, String clauseIdx, Term... arguments) {
      if (isSpyPointsEnabled() == false) {
         return;
      }

      StringBuilder sb = new StringBuilder();
      if (arguments.length == 0) {
         sb.append(", TermUtils.EMPTY_ARRAY");
      } else {
         sb.append(", new Term[]{");
         for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
               sb.append(", ");
            }
            Term arg = arguments[i];
            sb.append(getLogArgument(arg));
         }
         sb.append("}");
      }
      if (clauseIdx != null) {
         sb.append("," + clauseIdx);
      }
      String spyPointVariableName = functionVariableName + ".spyPoint";
      beginIf(spyPointVariableName + ".isEnabled()");
      writeStatement(functionVariableName + ".spyPoint.log" + type + "(" + functionVariableName + sb + ")");
      endBlock();
   }

   private String getLogArgument(Term arg) {
      if (arg.getType() == TermType.NAMED_VARIABLE) {
         String variableId = getVariableId(arg);
         if (classVariables.isMemberVariable(variableId) == false && classVariables.isAssignedVariable(variableId) == false) {
            return "new Variable(\"_\")";
         } else if (classVariables.isAssignedVariable(variableId) == false) {
            return variableId;
         } else {
            return variableId;
         }
      } else {
         return outputCreateTermStatement(arg, true);
      }
   }

   void outputBacktrack(String variableId) {
      String statement = variableId + ".backtrack()";
      if (classVariables.isMemberVariable(variableId)) {
         // if we are attempting to backtrack a member variable then, the way the compiler is currently written,
         // we cannot be sure it has even been assigned yet - so need to include a null check
         statement = "if (" + variableId + "!=null) " + statement;
      }
      writeStatement(statement);
   }

   boolean isSpyPointsEnabled() {
      return getProjogProperties(kb).isSpyPointsEnabled();
   }

   boolean isAssigned(String id) {
      return classVariables.isAssignedVariable(id);
   }
}
