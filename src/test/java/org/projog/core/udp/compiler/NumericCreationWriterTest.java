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

import static org.junit.Assert.assertEquals;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseSentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.projog.core.ArithmeticOperator;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.udp.ClauseModel;

public class NumericCreationWriterTest {
   @Test
   public void integer() {
      assertStatement(new IntegerNumber(42), "INTEGER_42");
      assertStatement(new IntegerNumber(-7), "INTEGER_N7");
      assertStatement(new IntegerNumber(0), "INTEGER_0");
      assertStatement(new IntegerNumber(Long.MAX_VALUE), "INTEGER_9223372036854775807");
      assertStatement(new IntegerNumber(Long.MIN_VALUE), "INTEGER_N9223372036854775808");
   }

   @Test
   public void decimal() {
      assertStatement(new DecimalFraction(42), "FRACTION_42_0");
      assertStatement(new DecimalFraction(42.5), "FRACTION_42_5");
      assertStatement(new DecimalFraction(-7), "FRACTION_N7_0");
      assertStatement(new DecimalFraction(-7.253846), "FRACTION_N7_253846");
      assertStatement(new DecimalFraction(0), "FRACTION_0_0");
      assertStatement(new DecimalFraction(Double.MAX_VALUE), "FRACTION_1_7976931348623157E308");
      assertStatement(new DecimalFraction(Double.MIN_VALUE), "FRACTION_4_9EN324");
   }

   @Test
   public void arithmeticOperator() {
      NumericCreationWriter writer = new NumericCreationWriter();
      List<ClauseModel> dummyClause = Collections.singletonList(ClauseModel.createClauseModel(new Atom("a")));
      CompiledPredicateState state = new CompiledPredicateState("test", createKnowledgeBase(), dummyClause);

      String statement1 = writer.outputCreateNumericStatement(parseSentence("1+2+3*4."), state);
      assertEquals("A0.calculate(A0.calculate(INTEGER_1,INTEGER_2),A1.calculate(INTEGER_3,INTEGER_4))", statement1);

      String statement2 = writer.outputCreateNumericStatement(parseSentence("1-2+7."), state);
      assertEquals("A0.calculate(A2.calculate(INTEGER_1,INTEGER_2),INTEGER_7)", statement2);

      List<StaticVariableState<ArithmeticOperator>> operators = new ArrayList<>(state.getArithmeticOperators());
      assertEquals(3, operators.size());
      assertArithmeticOperator(operators.get(0), "A0", "org.projog.core.function.math.Add");
      assertArithmeticOperator(operators.get(1), "A1", "org.projog.core.function.math.Multiply");
      assertArithmeticOperator(operators.get(2), "A2", "org.projog.core.function.math.Subtract");
   }

   private void assertArithmeticOperator(StaticVariableState<ArithmeticOperator> actual, String expectedName, String expectedClass) {
      assertEquals(expectedName, actual.getVariableName());
      assertEquals(expectedClass, actual.getVariableTypeClassName());
   }

   private void assertStatement(Term input, String expected) {
      List<ClauseModel> dummyClause = Collections.singletonList(ClauseModel.createClauseModel(new Atom("a")));
      CompiledPredicateState state = new CompiledPredicateState("test", createKnowledgeBase(), dummyClause);
      assertStatement(input, expected, state);
   }

   private void assertStatement(Term input, String expected, CompiledPredicateState compiledPredicateState) {
      NumericCreationWriter w = new NumericCreationWriter();
      String actual = w.outputCreateNumericStatement(input, compiledPredicateState);
      assertEquals(expected, actual);
   }
}
