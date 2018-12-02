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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.projog.core.ArithmeticOperator;
import org.projog.core.ArithmeticOperators;
import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;
import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.compiler.model.ClauseMetaData;
import org.projog.core.udp.compiler.model.ClauseVariableMetaData;
import org.projog.core.udp.compiler.model.PredicateMetaData;

/** Contains state used in the generation of Java source code to represent a user-defined Prolog predicate. */
final class CompiledPredicateState {
   private final KnowledgeBase kb;
   private final ArithmeticOperators ao;
   private final String className;
   private final Map<String, Term> staticMemberVariables = new LinkedHashMap<>();
   private int predicateCtr;
   private final Map<PredicateKey, StaticVariableState<PredicateFactory>> predicateFactories = new LinkedHashMap<>();
   private final Map<PredicateKey, StaticVariableState<ArithmeticOperator>> arithmeticOperators = new LinkedHashMap<>();
   private final Map<ClauseVariableMetaData, String> javaVariableNamesByMetaData = new LinkedHashMap<>();
   private final Map<String, String> javaVariableNamesByPrologVariableName = new LinkedHashMap<>();
   private final List<ClauseState> clauseStates;
   private final PredicateMetaData classMetaData;
   private final List<ClauseVariableState> membersVariables = new ArrayList<>();
   private final TermCreationWriter writer;
   private final NumericCreationWriter calculationWriter;

   CompiledPredicateState(String compiledPredicateClassName, KnowledgeBase kb, List<ClauseModel> implications) {
      this.kb = kb;
      this.ao = KnowledgeBaseUtils.getArithmeticOperators(kb);
      this.writer = new TermCreationWriter();
      this.calculationWriter = new NumericCreationWriter();
      this.className = compiledPredicateClassName;
      classMetaData = new PredicateMetaData(PredicateKey.createForTerm(implications.iterator().next().getConsequent().getTerm()), kb, implications);
      clauseStates = new ArrayList<>(implications.size());
      for (ClauseMetaData md : classMetaData.getClauses()) {
         ClauseState s = new ClauseState(md);
         clauseStates.add(s);
      }
   }

   int getPredicateCtr() {
      return predicateCtr;
   }

   List<ClauseState> getClauseStates() {
      return clauseStates;
   }

   PredicateMetaData getClassMetaData() {
      return classMetaData;
   }

   List<ClauseVariableState> getMembersVariables() {
      return membersVariables;
   }

   TermCreationWriter getWriter() {
      return writer;
   }

   NumericCreationWriter getCalculationWriter() {
      return calculationWriter;
   }

   String outputCreateTermStatement(Term t, boolean outputImmutable) {
      return writer.outputCreateTermStatement(t, this, outputImmutable);
   }

   String outputCreateNumericStatement(Term t) {
      return calculationWriter.outputCreateNumericStatement(t, this);
   }

   ClauseState getFirstClause() {
      return clauseStates.get(0);
   }

   ClauseState getSecondClause() {
      return clauseStates.get(1);
   }

   boolean isTailRecursive() {
      return classMetaData.isTailRecursive();
   }

   boolean isPotentiallySingleResultTailRecursive() {
      return classMetaData.isTailRecursive() && classMetaData.getTailRecursivePredicateMetaData().isPotentialSingleResult();
   }

   String getTermVariableName(Term t) {
      String originalVariableName = t.getType().toString();
      if (t.getType().isNumeric()) {
         originalVariableName += "_" + t.getName().replace('.', '_').replace('-', 'N');
      }
      String variableName = originalVariableName;
      int ctr = 0;
      while (staticMemberVariables.containsKey(variableName) && staticMemberVariables.get(variableName).strictEquality(t) == false) {
         variableName = originalVariableName + "_" + (ctr++);
      }
      staticMemberVariables.put(variableName, t);
      return variableName;
   }

   Collection<StaticVariableState<PredicateFactory>> getPredicateFactories() {
      return predicateFactories.values();
   }

   Collection<StaticVariableState<ArithmeticOperator>> getArithmeticOperators() {
      return arithmeticOperators.values();
   }

   Map<String, Term> getStaticMemberVariables() {
      return staticMemberVariables;
   }

   StaticVariableState<PredicateFactory> getPredicateFactoryMetaData(Term term) {
      PredicateKey key = PredicateKey.createForTerm(term);
      StaticVariableState<PredicateFactory> pfmd = predicateFactories.get(key);
      if (pfmd == null) {
         String name = "F" + predicateFactories.size();
         pfmd = new StaticVariableState<>(key, kb.getPredicateFactory(key), name);
         predicateFactories.put(key, pfmd);
      }
      return pfmd;
   }

   StaticVariableState<ArithmeticOperator> getArithmeticOperator(Term term) {
      PredicateKey key = PredicateKey.createForTerm(term);
      StaticVariableState<ArithmeticOperator> s = arithmeticOperators.get(key);
      if (s == null) {
         String name = "A" + arithmeticOperators.size();
         s = new StaticVariableState<>(key, ao.getArithmeticOperator(key), name);
         arithmeticOperators.put(key, s);
      }
      return s;
   }

   String getClassNameExcludingPackage() {
      return className;
   }

   String getExistingJavaVariableName(ClauseVariableMetaData cmdv) {
      String javaVariableName = javaVariableNamesByMetaData.get(cmdv);
      if (javaVariableName == null) {
         throw new RuntimeException(cmdv.getPrologVariableName() + " not in " + javaVariableNamesByMetaData);
      }
      return javaVariableName;
   }

   ClauseVariableState createClauseVariableState(ClauseVariableMetaData cmdv) {
      if (javaVariableNamesByMetaData.containsKey(cmdv)) {
         throw new RuntimeException(cmdv.getPrologVariableName() + " already in " + javaVariableNamesByMetaData);
      }
      String javaVariableName = "v" + javaVariableNamesByMetaData.size();
      javaVariableNamesByMetaData.put(cmdv, javaVariableName);
      javaVariableNamesByPrologVariableName.put(cmdv.getPrologVariableName(), javaVariableName);

      ClauseVariableState s = new ClauseVariableState(javaVariableName, cmdv);
      membersVariables.add(s);
      return s;
   }

   String getNextPredicateJavaVariableName() {
      return "p" + predicateCtr++;
   }

   String getJavaVariableName(Variable t) {
      String prologVariableName = t.getId();
      String javaVariableName = javaVariableNamesByPrologVariableName.get(prologVariableName);
      if (javaVariableName == null) {
         throw new NullPointerException(prologVariableName + " not in " + javaVariableNamesByPrologVariableName);
      }
      return javaVariableName;
   }

   boolean isRetryable() {
      return classMetaData.isRetryable();
   }
}
