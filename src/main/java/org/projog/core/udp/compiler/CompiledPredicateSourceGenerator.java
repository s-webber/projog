/*
 * Copyright 2018 S. Webber
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
import static org.projog.core.udp.compiler.CompiledPredicateConstants.ARITHMETIC_OPERATORS_VARIABLE_NAME;
import static org.projog.core.udp.compiler.CompiledPredicateConstants.COMPILED_PREDICATES_PACKAGE;
import static org.projog.core.udp.compiler.CompiledPredicateConstants.INIT_RULE_METHOD_NAME_PREFIX;
import static org.projog.core.udp.compiler.CompiledPredicateConstants.RETRY_RULE_METHOD_NAME_PREFIX;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.encodeName;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getKeyGeneration;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.isNotAnonymousVariable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.function.bool.Fail;
import org.projog.core.function.bool.True;
import org.projog.core.function.compare.NumericEquality;
import org.projog.core.function.compare.NumericGreaterThan;
import org.projog.core.function.compare.NumericGreaterThanOrEqual;
import org.projog.core.function.compare.NumericInequality;
import org.projog.core.function.compare.NumericLessThan;
import org.projog.core.function.compare.NumericLessThanOrEqual;
import org.projog.core.function.compound.Not;
import org.projog.core.function.compound.Once;
import org.projog.core.function.flow.Cut;
import org.projog.core.function.io.Write;
import org.projog.core.function.math.Is;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.TailRecursivePredicateMetaData;
import org.projog.core.udp.compiler.model.AntecedentElementMetaData;
import org.projog.core.udp.compiler.model.ClauseElement;
import org.projog.core.udp.compiler.model.ClauseVariableMetaData;
import org.projog.core.udp.compiler.model.ConsequentMetaData;
import org.projog.core.udp.compiler.model.PredicateMetaData;

/**
 * Translates Prolog predicates into Java source code.
 * <p>
 * Classes generated at runtime by this class will implement {@link CompiledPredicate}.
 */
final class CompiledPredicateSourceGenerator {
   private static final String CONJUNCTION_CTR = "j";
   private static final String CLAUSE_CTR = "c";
   private static final String SPYPOINT = "s";
   private static final String DEBUG_ENABLED_FLAG = "d";
   private static final String COULD_REEVALUATION_SUCCEED_FLAG = "r";

   private static final Map<Class<? extends AbstractSingletonPredicate>, String> NUMERIC_COMPARISON_PEDICATES = new HashMap<>();
   static {
      NUMERIC_COMPARISON_PEDICATES.put(NumericGreaterThan.class, "<=");
      NUMERIC_COMPARISON_PEDICATES.put(NumericGreaterThanOrEqual.class, "<");
      NUMERIC_COMPARISON_PEDICATES.put(NumericLessThan.class, ">=");
      NUMERIC_COMPARISON_PEDICATES.put(NumericLessThanOrEqual.class, ">");
      NUMERIC_COMPARISON_PEDICATES.put(NumericInequality.class, "==");
      NUMERIC_COMPARISON_PEDICATES.put(NumericEquality.class, "!=");
   }

   static String generateJavaSource(String compiledPredicateClassName, KnowledgeBase kb, List<ClauseModel> implications) {
      return new CompiledPredicateSourceGenerator(compiledPredicateClassName, kb, implications).write();
   }

   private final KnowledgeBase kb;
   private final CompiledPredicateState state;
   private PrintWriter pw;

   private CompiledPredicateSourceGenerator(String compiledPredicateClassName, KnowledgeBase kb, List<ClauseModel> implications) {
      this.kb = kb;
      this.state = new CompiledPredicateState(compiledPredicateClassName, kb, implications);
   }

   String write() {
      PredicateMetaData classMetaData = state.getClassMetaData();
      StringWriter sw = new StringWriter();
      pw = new PrintWriter(sw);

      int numArgs = classMetaData.getPredicateKey().getNumArgs();
      writePackageAndImports();
      writeClassDeclaration();

      if (state.getClassMetaData().isTailRecursive()) {
         outputRecursiveMethods();
      } else {
         for (ClauseState md : state.getClauseStates()) {
            writeClause(md);
         }
         writeEvaluateMethod();

         writeCouldReevaluationSucceedMethod();
      }

      writeGetPredicateMethods(numArgs);

      writeIsRetryableMethod();

      writePublicConstructor(kb, classMetaData, numArgs);

      writePrivateConstructor(numArgs);

      writeSetKnowledgeBaseMethod();

      writeDeclareStaticVariables();

      for (int i = 0; i < state.getPredicateCtr(); i++) {
         pw.println("private Predicate p" + i + ";");
      }
      for (ClauseVariableState v : state.getMembersVariables()) {
         if (v.isMemberVariable()) {
            pw.print("private ");
            pw.print(v.getJavaType());
            pw.print(' ');
            pw.print(v.getJavaVariableName());
            pw.println(';');
         }
      }
      for (Entry<String, Term> e : state.getStaticMemberVariables().entrySet()) {
         String className = e.getValue().getClass().getSimpleName();
         pw.print("private static final ");
         pw.print(className);
         pw.print(' ');
         pw.print(e.getKey());
         pw.print('=');
         if (e.getValue() instanceof Structure) {
            pw.print("(Structure)");
         }
         pw.print(state.outputCreateTermStatement(e.getValue(), true));
         pw.println(';');
      }
      for (int i = 0; i < numArgs; i++) {
         pw.println("private " + (state.isTailRecursive() ? "" : "final") + " Term a" + i + ";");
      }

      if (!state.isTailRecursive()) {
         if (isConjuctionCounterRequired()) {
            pw.println("private int " + CONJUNCTION_CTR + ";");
         }
         pw.println("private int " + CLAUSE_CTR + ";");
      }
      if (isSpyPointsEnabled()) {
         pw.println("private static org.projog.core.SpyPoints.SpyPoint " + SPYPOINT + ";");
         pw.println("private final boolean " + DEBUG_ENABLED_FLAG + ";");
      }

      pw.println('}');

      pw.close();

      return sw.toString();
   }

   private boolean isConjuctionCounterRequired() {
      if (state.isTailRecursive()) {
         return false;
      }

      for (ClauseState v : state.getClauseStates()) {
         if (v.hasRetryableElements()) {
            return true;
         }
      }

      return false;
   }

   private void writePackageAndImports() {
      pw.println("package " + COMPILED_PREDICATES_PACKAGE + ";");
      pw.println("import org.projog.core.term.*;");
      pw.println("import org.projog.core.CutException;");
      pw.println("import org.projog.core.KnowledgeBase;");
      pw.println("import org.projog.core.KnowledgeBaseUtils;");
      pw.println("import org.projog.core.Predicate;");
      pw.println("import org.projog.core.PredicateKey;");
   }

   private void writeClassDeclaration() {
      PredicateKey key = state.getClassMetaData().getPredicateKey();
      pw.println("// " + encodeName(key.getName()) + "/" + key.getNumArgs());
      pw.print("public final class " + state.getClassNameExcludingPackage());
      if (state.getClassMetaData().isTailRecursive()) {
         pw.println(" extends org.projog.core.udp.compiler.CompiledTailRecursivePredicate{");
      } else {
         pw.println(" implements org.projog.core.udp.compiler.CompiledPredicate{");
      }
   }

   private void writePublicConstructor(KnowledgeBase kb, PredicateMetaData classMetaData, int numArgs) {
      pw.println("public " + state.getClassNameExcludingPackage() + "(KnowledgeBase kb){");

      writeAssignStaticVariables();

      if (isSpyPointsEnabled()) {
         pw.println(SPYPOINT + "=KnowledgeBaseUtils.getSpyPoints(kb).getSpyPoint(" + getKeyGeneration(classMetaData.getPredicateKey()) + ");");
         pw.println(DEBUG_ENABLED_FLAG + "=false;");
      }
      for (int i = 0; i < numArgs; i++) {
         pw.println("a" + i + "=null;");
      }
      if (state.isPotentiallySingleResultTailRecursive()) {
         pw.print(COULD_REEVALUATION_SUCCEED_FLAG + "=true;");
      }

      pw.println('}');
   }

   private void writePrivateConstructor(int numArgs) {
      pw.print("private " + state.getClassNameExcludingPackage() + "(");
      for (int i = 0; i < numArgs; i++) {
         if (i != 0) {
            pw.print(',');
         }
         pw.print("Term i");
         pw.print(i);
      }
      pw.println(") {");
      for (int i = 0; i < numArgs; i++) {
         pw.println("a" + i + "=i" + i + ".getTerm();");
      }
      if (isSpyPointsEnabled()) {
         pw.println(DEBUG_ENABLED_FLAG + "=" + SPYPOINT + ".isEnabled();");
      }
      if (state.isPotentiallySingleResultTailRecursive()) {
         TailRecursivePredicateMetaData tailRecursivePredicateMetaData = state.getClassMetaData().getTailRecursivePredicateMetaData();
         pw.print(COULD_REEVALUATION_SUCCEED_FLAG + "=");
         boolean first = true;
         for (int i = 0; i < state.getClassMetaData().getPredicateKey().getNumArgs(); i++) {
            if (tailRecursivePredicateMetaData.isSingleResultIfArgumentImmutable(i)) {
               if (first) {
                  first = false;
               } else {
                  pw.print("&&");
               }
               pw.print("!i" + i + ".isImmutable()");
            }
         }
         pw.println(';');
      }
      pw.println('}');
      if (state.isPotentiallySingleResultTailRecursive()) {
         pw.println("private final boolean " + COULD_REEVALUATION_SUCCEED_FLAG + ";");
      }
   }

   private void writeGetPredicateMethods(int numArgs) {
      pw.println("public " + state.getClassNameExcludingPackage() + " getPredicate(Term... args){");
      pw.print("return getPredicate(");
      for (int i = 0; i < numArgs; i++) {
         if (i != 0) {
            pw.print(',');
         }
         pw.print("args[");
         pw.print(i);
         pw.print("]");
      }
      pw.println(");");
      pw.println("}");

      pw.print("public " + state.getClassNameExcludingPackage() + " getPredicate(");
      for (int i = 0; i < numArgs; i++) {
         if (i != 0) {
            pw.print(',');
         }
         pw.print("Term i");
         pw.print(i);
      }
      pw.println(") {");
      pw.print("return new " + state.getClassNameExcludingPackage() + "(");
      for (int i = 0; i < numArgs; i++) {
         if (i != 0) {
            pw.print(',');
         }
         pw.print("i");
         pw.print(i);
      }
      pw.print(");");
      pw.println("}");
   }

   private void writeEvaluateMethod() {
      pw.println("public boolean evaluate(){");

      logEnter();

      pw.println("try{");
      pw.println("switch(" + CLAUSE_CTR + "){");

      int caseCtr = 0;
      for (ClauseState cs : state.getClauseStates()) {
         pw.println("case " + caseCtr + ":");
         if (cs.hasRetryableElements()) {
            String predicateName = null;
            for (AntecedentElementState s : cs.getAntecedentElementStates()) {
               if (s.getElement().isRetryable()) {
                  predicateName = s.getPredicateName();
                  break;
               }
            }
            if (predicateName == null) {
               throw new RuntimeException();
            }
            pw.println("if(" + predicateName + "==null){");
            pw.println("if(" + INIT_RULE_METHOD_NAME_PREFIX + cs.getClauseIdx() + "()&&" + RETRY_RULE_METHOD_NAME_PREFIX + cs.getClauseIdx() + "())");
            writeExitBlock(cs.getClauseIdx());
            pw.println("}else if(" + RETRY_RULE_METHOD_NAME_PREFIX + cs.getClauseIdx() + "()){");
            writeExitBlock(cs.getClauseIdx());
            pw.print("}"); // end of if/else

            if (cs.containsCut()) {
               pw.println("if(" + CLAUSE_CTR + "==" + state.getClauseStates().size() + ")break;");
            }

            pw.println(CLAUSE_CTR + "=" + (caseCtr + 1) + ";");
         } else {
            pw.println(CLAUSE_CTR + "=" + (caseCtr + 1) + ";");
            pw.println("if(" + INIT_RULE_METHOD_NAME_PREFIX + cs.getClauseIdx() + "())");
            writeExitBlock(cs.getClauseIdx());

            if (cs.containsCut()) {
               pw.println("if(" + CLAUSE_CTR + "==" + state.getClauseStates().size() + ")break;");
            }
         }

         caseCtr++;
      }

      pw.print('}'); // end of switch
      pw.println("}catch(CutException e){" + getSetClauseCtrDueToCutStatement() + "}");
      logFail();
      pw.println("return false;}");
   }

   private void writeExitBlock(int clauseIdx) {
      pw.println("{");
      logExit(clauseIdx + 1);
      pw.println("return true;}");
   }

   private void writeCouldReevaluationSucceedMethod() {
      pw.println("public boolean couldReevaluationSucceed(){");
      if (state.isRetryable()) {
         ClauseState last = state.getClauseStates().get(state.getClauseStates().size() - 1);
         if (last.hasRetryableElements()) {
            pw.print("if (" + CLAUSE_CTR + "==" + (state.getClauseStates().size() - 1) + ")return");
            boolean first = true;
            for (AntecedentElementState s : last.getAntecedentElementStates()) {
               if (s.getElement().isRetryable()) {
                  if (first) {
                     pw.print("(" + s.getPredicateName() + "==null||" + s.getPredicateName() + ".couldReevaluationSucceed())");
                     first = false;
                  } else {
                     pw.print("||(" + s.getPredicateName() + "!=null&&" + s.getPredicateName() + ".couldReevaluationSucceed())");
                  }
               }
            }
            pw.println(";");
         }
         pw.println("return " + CLAUSE_CTR + "!=" + state.getClauseStates().size() + ";");
      } else {
         pw.println("return false;");
      }
      pw.println('}');
   }

   private void writeIsRetryableMethod() {
      pw.println("public boolean isRetryable(){return " + state.isRetryable() + ";}");
   }

   private void writeSetKnowledgeBaseMethod() {
      pw.println("public void setKnowledgeBase(KnowledgeBase k){}");
   }

   private void writeAssignStaticVariables() {
      writeAssignStaticVariables(state.getPredicateFactories(), "kb.getPredicateFactory");

      pw.println(ARITHMETIC_OPERATORS_VARIABLE_NAME + "=KnowledgeBaseUtils.getArithmeticOperators(kb);");
      writeAssignStaticVariables(state.getArithmeticOperators(), ARITHMETIC_OPERATORS_VARIABLE_NAME + ".getArithmeticOperator");
   }

   private <T> void writeAssignStaticVariables(Collection<StaticVariableState<T>> variables, String factoryMethodName) {
      for (StaticVariableState<?> v : variables) {
         PredicateKey key = v.getPredicateKey();
         pw.println(v.getVariableName() + "=(" + v.getVariableTypeClassName() + ")" + factoryMethodName + "(" + getKeyGeneration(key) + ");");
      }
   }

   private void writeDeclareStaticVariables() {
      writeDeclareStaticVariables(state.getPredicateFactories());

      // TODO some predicates will not require ArithmeticOperators so in those cases we could avoid declaring it
      pw.println("private static org.projog.core.ArithmeticOperators " + ARITHMETIC_OPERATORS_VARIABLE_NAME + ";");
      writeDeclareStaticVariables(state.getArithmeticOperators());
   }

   private <T> void writeDeclareStaticVariables(Collection<StaticVariableState<T>> variables) {
      for (StaticVariableState<T> v : variables) {
         pw.println("private static " + v.getVariableTypeClassName() + " " + v.getVariableName() + ";");
      }
   }

   private void writeClause(ClauseState clauseState) {
      writeInitClauseMethod(clauseState);

      if (clauseState.hasRetryableElements()) {
         writeClauseRetryMethod(clauseState);
      }
   }

   private void writeInitClauseMethod(ClauseState clauseState) {
      int idx = clauseState.getClauseIdx();
      pw.println("private boolean " + INIT_RULE_METHOD_NAME_PREFIX + idx + "(){");

      ConsequentMetaData consequentElement = clauseState.getConsequentElement();
      if (idx > 0) {
         for (int i = 0; i < consequentElement.getTerm().getNumberOfArguments(); i++) {
            pw.print('a');
            pw.print(i);
            pw.println(".backtrack();");
         }
      }
      assignVariables(consequentElement, clauseState);

      StringBuilder unifyConsequentArgsLogic = new StringBuilder();
      for (int i = 0; i < consequentElement.getTerm().getNumberOfArguments(); i++) {
         Term argument = consequentElement.getTerm().getArgument(i);
         if (isNotAnonymousVariable(argument)) {
            if (unifyConsequentArgsLogic.length() != 0) {
               unifyConsequentArgsLogic.append("&&");
            }
            unifyConsequentArgsLogic.append(state.outputCreateTermStatement(argument, false)).append(".unify(").append('a').append(i).append(')');
         }
      }

      if (unifyConsequentArgsLogic.length() != 0) {
         pw.print("if(");
         pw.print(unifyConsequentArgsLogic);
         pw.println("){");
      }

      for (AntecedentElementState elementState : clauseState.getAntecedentElementStatesToInit()) {
         assignVariables(elementState);
         if (!clauseState.hasRetryableElements() && elementState.getElement().isCut()) {
            pw.println(getSetClauseCtrDueToCutStatement());
         }
         writeEvaluateSingleResultPredicate(elementState);
      }
      if (clauseState.hasRetryableElements() && isConjuctionCounterRequired()) {
         pw.println(CONJUNCTION_CTR + "=0;");
      }
      pw.println("return true;");
      if (unifyConsequentArgsLogic.length() != 0) {
         pw.println("}else return false;");
      }

      pw.println('}'); // end of init method
   }

   private void writeClauseRetryMethod(ClauseState clauseState) {
      pw.println("private boolean " + RETRY_RULE_METHOD_NAME_PREFIX + clauseState.getClauseIdx() + "(){");
      pw.println("while(true){");
      pw.println("switch(" + CONJUNCTION_CTR + "){");

      int caseIdx = -1;
      for (AntecedentElementState elementState : clauseState.getAntecedentElementStatesToRetry()) {
         AntecedentElementMetaData element = elementState.getElement();

         if (element.isRetryable()) {
            caseIdx++;
            if (caseIdx != 0) {
               pw.println(CONJUNCTION_CTR + "=" + caseIdx + ";");
            }
            pw.println("case " + caseIdx + ":");
         }

         if (element.isRetryable()) {
            String predicateName = state.getNextPredicateJavaVariableName();
            elementState.setPredicateName(predicateName);
            pw.println("if(" + predicateName + "==null){");
            assignVariables(elementState);
            pw.println(predicateName + "=" + getPredicateStatement(element.getTerm()) + ";");
            pw.println("if(!" + predicateName + ".evaluate()){");
            handledFailedRetryableElement(caseIdx, elementState);
            pw.println("}}else{");
            pw.println("b" + clauseState.getClauseIdx() + "_" + elementState.getElementIdx() + "();");
            pw.println("if(!" + predicateName + ".couldReevaluationSucceed()||!" + predicateName + ".evaluate()){");
            handledFailedRetryableElement(caseIdx, elementState);
            pw.println("}}");
         } else {
            assignVariables(elementState);
            writeEvaluateSingleResultPredicate(elementState);
         }
      }

      pw.println('}'); // end of switch statement
      if (doesCutPreventReevaluation(clauseState)) {
         pw.println(getSetClauseCtrDueToCutStatement());
      }
      pw.println("return true;");
      pw.println('}'); // end of while statement
      pw.println('}'); // end of method

      for (AntecedentElementState elementState : clauseState.getAntecedentElementStatesToRetry()) {
         if (elementState.getElement().isRetryable()) {
            writeBacktrackMethodCalls(elementState);
         }
      }
   }

   private void writeBacktrackMethodCalls(final AntecedentElementState elementToBacktrackTo) {
      pw.println("private void b" + elementToBacktrackTo.getClauseState().getClauseIdx() + "_" + elementToBacktrackTo.getElementIdx() + "(){");

      for (int i = elementToBacktrackTo.getElementIdx() + 1; i < elementToBacktrackTo.getClauseState().getNumberOfElements(); i++) {
         AntecedentElementState elementToBacktrack = elementToBacktrackTo.getClauseState().getAntecedentElementState(i);
         for (ClauseVariableMetaData cvmd : elementToBacktrack.getVariables()) {
            if (!cvmd.isMemberVariable()) {
               throw new IllegalStateException();
            }
            String javaVariableName = state.getExistingJavaVariableName(cvmd);
            pw.println("if(" + javaVariableName + "!=null)" + javaVariableName + ".backtrack();");
         }
         if (elementToBacktrack.getPredicateName() != null) {
            pw.println(elementToBacktrack.getPredicateName() + "=null;");
         }
         if (elementToBacktrack.getElement().isRetryable() || elementToBacktrack.getElement().isCut()) {
            break;
         }
      }

      pw.println("}");
   }

   private boolean doesCutPreventReevaluation(ClauseState clauseState) {
      AntecedentElementState lastAntecedentElement = clauseState.getLastAntecedentElement();
      if (lastAntecedentElement.getElement().isRetryable()) {
         return false;
      } else if (lastAntecedentElement.getElement().isCut()) {
         return true;
      } else {
         return lastAntecedentElement.wouldBacktrackingInvokeCut();
      }
   }

   private void handledFailedRetryableElement(int caseIdx, AntecedentElementState elementState) {
      if (!elementState.getElement().isRetryable()) {
         throw new IllegalStateException();
      } else if (elementState.wouldBacktrackingInvokeCut()) {
         pw.println(getSetClauseCtrDueToCutStatement());
         pw.println("return false;");
      } else if (caseIdx == 0) {
         pw.println("return false;");
      } else {
         pw.println(CONJUNCTION_CTR + "=" + (caseIdx - 1) + ";");
         pw.println("continue;");
      }
   }

   private void assignVariables(AntecedentElementState elementState) {
      elementState.setVariables(assignVariables(elementState.getElement(), elementState.getClauseState()));
   }

   private ArrayList<ClauseVariableMetaData> assignVariables(ClauseElement element, ClauseState clauseState) {
      ArrayList<ClauseVariableMetaData> variablesToBacktrack = new ArrayList<>();

      for (ClauseVariableMetaData cmdv : element.getVariables()) {
         ClauseVariableState javaVariable = state.createClauseVariableState(cmdv);
         StringBuilder sb = new StringBuilder();
         if (!cmdv.isMemberVariable()) {
            sb.append(javaVariable.getJavaType()).append(' ');
         }
         sb.append(javaVariable.getJavaVariableName());
         if (javaVariable.isVariableTerm()) {
            sb.append("=new Variable(\"").append(cmdv.getPrologVariableName()).append("\");");
         } else {
            sb.append('=').append(state.getExistingJavaVariableName(cmdv.previous())).append(".getTerm();");
         }
         pw.println(sb);
         variablesToBacktrack.add(cmdv);
      }

      return variablesToBacktrack;
   }

   private void writeEvaluateSingleResultPredicate(AntecedentElementState elementState) {
      AntecedentElementMetaData element = elementState.getElement();
      PredicateFactory pf = element.getPredicateFactory();

      if (pf instanceof True) {
         return;
      }
      if (pf instanceof Cut) {
         return;
      }
      if (pf instanceof Write) {
         pw.print(state.getPredicateFactoryMetaData(element.getTerm()).getVariableName());
         Term arg = element.getTerm().getArgument(0);
         if (arg.getType() == TermType.ATOM) {
            pw.print(".writeString(");
            pw.print(encodeName(arg.getName()));
         } else {
            pw.print(".evaluate(");
            pw.print(state.outputCreateTermStatement(arg, false));
         }
         pw.println(");");
         return;
      }

      boolean backtrackOnSuccess = false;
      String condition;
      if (pf instanceof Fail) {
         condition = "true";
      } else if (pf instanceof Not && !elementState.getFirstArgument().getType().isVariable()) {
         Term firstArgument = elementState.getFirstArgument();
         if (kb.getPredicateFactory(firstArgument) instanceof AbstractSingletonPredicate) {
            condition = getAbstractSingletonPredicateEvaluateMethodCall(firstArgument);
         } else {
            condition = getPredicateEvaluateMethodCall(firstArgument);
         }
         backtrackOnSuccess = true;
      } else if (pf instanceof Once && !elementState.getFirstArgument().getType().isVariable()) {
         Term firstArgument = elementState.getFirstArgument();
         if (kb.getPredicateFactory(firstArgument) instanceof AbstractSingletonPredicate) {
            condition = "!" + getAbstractSingletonPredicateEvaluateMethodCall(firstArgument);
         } else {
            condition = "!" + getPredicateEvaluateMethodCall(firstArgument);
         }
      } else if (pf instanceof Is) {
         // TODO If the first argument of the "is" predicate is the first use of Prolog variable then we could assign
         // the corresponding Java variable directly to the Numeric represented by the second argument of the "is" predicate.
         // This would avoid the intermediary step of assigning the Java variable to a new org.projog.term.Variable
         // that is then unified with the Numeric represented by the second argument.
         String left = state.outputCreateTermStatement(elementState.getFirstArgument(), false);
         String right = state.outputCreateNumericStatement(elementState.getSecondArgument());
         condition = "!" + left + ".unify(" + right + ")";
      } else if (NUMERIC_COMPARISON_PEDICATES.containsKey(pf.getClass())) {
         String left = state.outputCreateNumericStatement(elementState.getFirstArgument());
         String right = state.outputCreateNumericStatement(elementState.getSecondArgument());
         condition = "NumericTermComparator.NUMERIC_TERM_COMPARATOR.compare(" + left + "," + right + ")" + NUMERIC_COMPARISON_PEDICATES.get(pf.getClass()) + "0";
      } else if (pf instanceof AbstractSingletonPredicate) {
         condition = "!" + getAbstractSingletonPredicateEvaluateMethodCall(element.getTerm());
      } else {
         condition = "!" + getPredicateEvaluateMethodCall(element.getTerm());
      }

      pw.print("if(");
      pw.print(condition);
      pw.print(")");

      if (elementState.wouldBacktrackingInvokeCut()) {
         pw.println("{" + getSetClauseCtrDueToCutStatement() + "return false;}");
      } else if (elementState.hasPrecedingRetryableElements()) {
         pw.println("continue;");
      } else {
         pw.println("return false;");
      }

      if (backtrackOnSuccess) {
         for (ClauseVariableMetaData cvmd : elementState.getVariables()) {
            pw.println(state.getExistingJavaVariableName(cvmd) + ".backtrack();");
         }
      }
   }

   private String getAbstractSingletonPredicateEvaluateMethodCall(Term firstArg) {
      return state.getPredicateFactoryMetaData(firstArg).getVariableName() + ".evaluate(" + getArgumentsAsCsv(firstArg) + ")";
   }

   private String getPredicateEvaluateMethodCall(Term term) {
      return getPredicateStatement(term) + ".evaluate()";
   }

   private String getPredicateStatement(Term term) {
      String predicateFactoryVariableName = state.getPredicateFactoryMetaData(term).getVariableName();
      return predicateFactoryVariableName + ".getPredicate(" + getArgumentsAsCsv(term) + ")";
   }

   private StringBuilder getArgumentsAsCsv(Term term) {
      StringBuilder args = new StringBuilder();
      for (int i = 0; i < term.getNumberOfArguments(); i++) {
         if (i > 0) {
            args.append(',');
         }
         args.append(state.outputCreateTermStatement(term.getArgument(i), false));
      }
      return args;
   }

   private void logEnter() {
      if (isSpyPointsEnabled()) {
         pw.println("if(" + DEBUG_ENABLED_FLAG + "){");
         ClauseState first = state.getFirstClause();
         if (first.hasRetryableElements()) {
            pw.println("if(" + first.getFirstRetryableAntecedentElement().getPredicateName() + "==null){");
         } else {
            pw.println("if(" + CLAUSE_CTR + "==0){");
         }
         String logStatementMethodArguments = getLogStatementMethodArguments();
         pw.println(getLogStatementMethodCall("Call", logStatementMethodArguments));
         pw.println("}else{");
         pw.println(getLogStatementMethodCall("Redo", logStatementMethodArguments));
         pw.println("}}");
      }
   }

   private void logCall() {
      log("Call", getLogStatementMethodArguments());
   }

   private void logRedo() {
      log("Redo", getLogStatementMethodArguments());
   }

   private void logExit(int clauseIdx) {
      log("Exit", getLogStatementMethodArguments() + "," + clauseIdx);
   }

   private void logFail() {
      log("Fail", getLogStatementMethodArguments());
   }

   private void log(String level, String logStatementMethodArguments) {
      if (isSpyPointsEnabled()) {
         pw.println("if(" + DEBUG_ENABLED_FLAG + "){" + getLogStatementMethodCall(level, logStatementMethodArguments) + "}");
      }
   }

   private String getLogStatementMethodCall(String level, String logStatementMethodArguments) {
      return SPYPOINT + ".log" + level + "(" + logStatementMethodArguments + ");";
   }

   private String getLogStatementMethodArguments() {
      String source = "this,";
      int numArgs = state.getClassMetaData().getPredicateKey().getNumArgs();
      if (numArgs > 0) {
         source += "new Term[]{";
         for (int i = 0; i < numArgs; i++) {
            if (i > 0) {
               source += ',';
            }
            source += "a" + i;
         }
         source += '}';
      } else {
         source += "TermUtils.EMPTY_ARRAY";
      }
      return source;
   }

   private boolean isSpyPointsEnabled() {
      return getProjogProperties(kb).isSpyPointsEnabled();
   }

   private String getSetClauseCtrDueToCutStatement() {
      return CLAUSE_CTR + "=" + state.getClauseStates().size() + ";";
   }

   /**
    * Constructs implementations of {@link org.projog.core.udp.TailRecursivePredicate#matchFirstRule()} and
    * {@link org.projog.core.udp.TailRecursivePredicate#matchSecondRule()}.
    */
   private void outputRecursiveMethods() {
      writeInitClauseMethod(state.getFirstClause());
      writeInitClauseMethod(state.getSecondClause());

      pw.print("public boolean couldReevaluationSucceed(){return ");
      pw.print(state.isPotentiallySingleResultTailRecursive() ? COULD_REEVALUATION_SUCCEED_FLAG : "true");
      pw.println(";}");

      TailRecursivePredicateMetaData tailRecursivePredicateMetaData = state.getClassMetaData().getTailRecursivePredicateMetaData();

      pw.print("public final boolean[] isSingleResultIfArgumentImmutable(){return new boolean[]{");
      for (int i = 0; i < state.getClassMetaData().getPredicateKey().getNumArgs(); i++) {
         if (i != 0) {
            pw.print(',');
         }
         pw.print(tailRecursivePredicateMetaData.isSingleResultIfArgumentImmutable(i));
      }
      pw.println("};}");

      pw.println("protected final boolean matchFirstRule(){return " + INIT_RULE_METHOD_NAME_PREFIX + "0();}");

      pw.println("protected final boolean matchSecondRule(){if (" + INIT_RULE_METHOD_NAME_PREFIX + "1()){");
      AntecedentElementState firstRetryable = state.getSecondClause().getFirstRetryableAntecedentElement();
      for (int i = 0; i < state.getClassMetaData().getPredicateKey().getNumArgs(); i++) {
         pw.println("a" + i + "=" + state.outputCreateTermStatement(firstRetryable.getElement().getTerm().getArgument(i), false) + ".getTerm();");
      }
      pw.println("return true;}return false;}");

      pw.println("protected final void backtrack(){");
      for (int i = 0; i < state.getClassMetaData().getPredicateKey().getNumArgs(); i++) {
         pw.println("a" + i + ".backtrack();");
      }
      pw.println('}');

      pw.println("protected final void logCall(){");
      logCall();
      pw.println('}');

      pw.println("protected final void logRedo(){");
      logRedo();
      pw.println('}');

      pw.println("protected final void logExit(){");
      logExit(1);
      pw.println('}');

      pw.println("protected final void logFail(){");
      logFail();
      pw.println('}');
   }
}
