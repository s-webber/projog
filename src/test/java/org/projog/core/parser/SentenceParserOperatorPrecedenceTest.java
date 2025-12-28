/*
 * Copyright 2023 S. Webber
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
package org.projog.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class SentenceParserOperatorPrecedenceTest {
   private static final Operands OPERANDS = new Operands();
   static {
      OPERANDS.addOperand("xfx100", "xfx", 100);
      OPERANDS.addOperand("xfx200", "xfx", 200);

      OPERANDS.addOperand("xfy100", "xfy", 100);
      OPERANDS.addOperand("xfy200", "xfy", 200);

      OPERANDS.addOperand("yfx100", "yfx", 100);
      OPERANDS.addOperand("yfx200", "yfx", 200);

      OPERANDS.addOperand("fx100", "fx", 100);
      OPERANDS.addOperand("fx200", "fx", 200);

      OPERANDS.addOperand("fy100", "fy", 100);
      OPERANDS.addOperand("fy200", "fy", 200);

      OPERANDS.addOperand("xf100", "xf", 100);
      OPERANDS.addOperand("xf200", "xf", 200);

      OPERANDS.addOperand("yf100", "yf", 100);
      OPERANDS.addOperand("yf200", "yf", 200);
   }

   @Test
   @DataProvider({
               "1 xfx100 2 xfx100 3.",
               "1 xfx100 2 xfy100 3.",
               "1 yfx100 2 xfx100 3.",
               "1 yfx100 2 xfy100 3.",
               "fx100 1 xfx100 2.",
               "fx100 1 xfy100 2.",
               "1 xfx100 2 xf100.",
               "1 yfx100 2 xf100.",
               "fx100 fx100 1.",
               "fx100 fy100 1.",
               "1 xf100 xf100.",
               "1 yf100 xf100.",
               "fx100 1 xf100.",})
   public void testOperatorPriorityClash(String prologSyntax) {
      try {
         SentenceParser.getInstance(prologSyntax, OPERANDS).parseSentence();
         fail();
      } catch (ParserException e) {
         assertTrue(e.getMessage().startsWith("Operator priority clash"));
      }
   }

   @Test
   @DataProvider(splitBy = " OUTPUTS ", value = {
               "1 xfx200 2 xfx100 3. OUTPUTS xfx200(1, xfx100(2, 3))",
               "1 xfx100 2 xfx200 3. OUTPUTS xfx200(xfx100(1, 2), 3)",
               "1 xfy100 2 xfy100 3. OUTPUTS xfy100(1, xfy100(2, 3))",
               "1 xfy100 2 xfy200 3. OUTPUTS xfy200(xfy100(1, 2), 3)",
               "1 yfx100 2 yfx100 3. OUTPUTS yfx100(yfx100(1, 2), 3)",
               "1 yfx200 2 yfx100 3. OUTPUTS yfx200(1, yfx100(2, 3))",
               "1 xfy100 2 yfx100 3. OUTPUTS yfx100(xfy100(1, 2), 3)",
               "1 xfy200 2 yfx100 3. OUTPUTS xfy200(1, yfx100(2, 3))",
               "fy100 fy100 1. OUTPUTS fy100(fy100(1))",
               "fy100 fx100 1. OUTPUTS fy100(fx100(1))",
               "fx200 fy100 1. OUTPUTS fx200(fy100(1))",
               "1 yf100 yf100. OUTPUTS yf100(yf100(1))",
               "1 xf100 yf100. OUTPUTS yf100(xf100(1))",
               "1 yf100 xf200. OUTPUTS xf200(yf100(1))",
               "fx100 1 yf100. OUTPUTS yf100(fx100(1))",
               "fy100 1 xf100. OUTPUTS fy100(xf100(1))",
               "fy100 1 yf100. OUTPUTS yf100(fy100(1))",
               "fx100 1 yfx100 2. OUTPUTS yfx100(fx100(1), 2)",
               "fy100 1 xfy100 2. OUTPUTS fy100(xfy100(1, 2))",
               "1 xfy100 2 yf100. OUTPUTS yf100(xfy100(1, 2))",
               "fx100 1 xf200. OUTPUTS xf200(fx100(1))",
               "fx200 1 xf100. OUTPUTS fx200(xf100(1))",
               "fx100 1 xfx200 2. OUTPUTS xfx200(fx100(1), 2)",
               "fx200 1 xfx100 2. OUTPUTS fx200(xfx100(1, 2))",
               "1 xfx200 2 xf100. OUTPUTS xfx200(1, xf100(2))",
               "1 xfx100 2 xf200. OUTPUTS xf200(xfx100(1, 2))",
               "1 xfx100 2 yfx100 3. OUTPUTS yfx100(xfx100(1, 2), 3)",
               "fy100 1 xfx100 2. OUTPUTS fy100(xfx100(1, 2))",
               "1 xfx100 2 yf100. OUTPUTS yf100(xfx100(1, 2))",
               "1 xfy100 2 xfx100 3. OUTPUTS xfy100(1, xfx100(2, 3))",
               // testcases from testOperatorPriorityClash with brackets added to make valid:
               "(1 xfx100 2) xfx100 3. OUTPUTS xfx100(xfx100(1, 2), 3)",
               "(1 xfx100 2) xfy100 3. OUTPUTS xfy100(xfx100(1, 2), 3)",
               "(1 yfx100 2) xfx100 3. OUTPUTS xfx100(yfx100(1, 2), 3)",
               "(1 yfx100 2) xfy100 3. OUTPUTS xfy100(yfx100(1, 2), 3)",
               "1 xfx100 (2 xfx100 3). OUTPUTS xfx100(1, xfx100(2, 3))",
               "1 xfx100 (2 xfy100 3). OUTPUTS xfx100(1, xfy100(2, 3))",
               "1 xfx100 (2 yfx100 3). OUTPUTS xfx100(1, yfx100(2, 3))",
               "1 yfx100 (2 xfx100 3). OUTPUTS yfx100(1, xfx100(2, 3))",
               "1 yfx100 (2 xfy100 3). OUTPUTS yfx100(1, xfy100(2, 3))",
               "(fx100 1) xfx100 2. OUTPUTS xfx100(fx100(1), 2)",
               "(fx100 1) xfy100 2. OUTPUTS xfy100(fx100(1), 2)",
               "1 xfx100 (2 xf100). OUTPUTS xfx100(1, xf100(2))",
               "1 yfx100 (2 xf100). OUTPUTS yfx100(1, xf100(2))",
               "fx100 (1 xfx100 2). OUTPUTS fx100(xfx100(1, 2))",
               "fx100 (1 xfy100 2). OUTPUTS fx100(xfy100(1, 2))",
               "(1 xfx100 2) xf100. OUTPUTS xf100(xfx100(1, 2))",
               "(1 yfx100 2) xf100. OUTPUTS xf100(yfx100(1, 2))",
               "fx100 (fx100 1). OUTPUTS fx100(fx100(1))",
               "fx100 (fy100 1). OUTPUTS fx100(fy100(1))",
               "(1 xf100) xf100. OUTPUTS xf100(xf100(1))",
               "(1 yf100) xf100. OUTPUTS xf100(yf100(1))",
               "(fx100 1) xf100. OUTPUTS xf100(fx100(1))",
               "fx100 (1 xf100). OUTPUTS fx100(xf100(1))",
               "(fy100 1) xfx100 2. OUTPUTS xfx100(fy100(1), 2)",
               "1 xfx100 (2 yf100). OUTPUTS xfx100(1, yf100(2))"})
   public void testParsedSuccessfully(String input, String expected) {
      Term t = SentenceParser.getInstance(input, OPERANDS).parseSentence();
      assertEquals(expected, t.toString());
      assertEquals(input, new TermFormatter(OPERANDS).formatTerm(t) + ".");
   }
}
