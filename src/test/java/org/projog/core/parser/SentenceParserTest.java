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
package org.projog.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.parseSentence;
import static org.projog.TestUtils.write;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class SentenceParserTest {
   @Test
   public void testIncompleteSentences() {
      assertNull(parseSentence(""));
      assertNull(parseSentence(" "));
      assertNull(parseSentence("\n "));
      error(".");
      error("a :- .");
      error(":- X = 'hello."); // no closing quote on atom
      endOfStreamError(":-");
      endOfStreamError("a :-");
      endOfStreamError(":- X is");
      endOfStreamError(":- X is 1"); // no .
      endOfStreamError(":- X is p(a, b, c)"); // no '.' character at end of sentence
      endOfStreamError(":- X is [a, b, c | d]"); // no '.' character at end of sentence
      endOfStreamError(":- X = %hello."); // no . before % comment
      endOfStreamError(":- X = /*hello."); // no closing */ on comment
   }

   @Test
   public void testIncompletePredicateSyntax() {
      error(":- X is p(."); // no )
      error(":- X is p()."); // no arguments
      error(":- X is p(a, b."); // no )
      error(":- X is p(a, b))."); // extra )
      error(":- X is p(a b)."); // no ,
      error(":- X is p(, a, b)."); // leading , before first arg
      endOfStreamError(":- X is p(a, b"); // no ) or .
   }

   @Test
   public void testInvalidListSyntax() {
      error(":- X is [."); // no ]
      error(":- X is [a b."); // no , or |
      error(":- X is [a, b."); // no ]
      error(":- X is [a, b |."); // no tail
      error(":- X is [a, b | ]."); // no tail
      error(":- X is [a, b | c, d]."); // 2 args after |
      endOfStreamError(":- X is [a, b"); // no ] or .
      endOfStreamError(":- X is [a, b | "); // no ] or . after |
      endOfStreamError(":- X is [a, b | c"); // no ] or . after tail
   }

   @Test
   public void testInvalidOperators() {
      error("a xyz b.");
      error("a $ b.");
      error("a b.");
      error("$ b.");
   }

   @Test
   public void testInvalidOperatorOrder() {
      error("1 :- 2 :- 3.");
      error(":- X = 1 + 2 + 3 + 4 = 5.");
      error("a ?- b.");
      error("?- a ?- b.");
      error("?- :- X.");
      error("?- ?- true.");
   }

   @Test
   public void testEquationPrecedence() {
      checkEquation("(((1+2)-3)*4)/5", "/(*(-(+(1, 2), 3), 4), 5)");

      checkEquation("1+2-3*4/5", "-(+(1, 2), /(*(3, 4), 5))");
      checkEquation("1+2-3/4*5", "-(+(1, 2), *(/(3, 4), 5))");
      checkEquation("1+2/3-4*5", "-(+(1, /(2, 3)), *(4, 5))");
      checkEquation("1+2/3*4-5", "-(+(1, *(/(2, 3), 4)), 5)");
      checkEquation("1/2+3*4-5", "-(+(/(1, 2), *(3, 4)), 5)");
      checkEquation("1/2*3+4-5", "-(+(*(/(1, 2), 3), 4), 5)");

      checkEquation("1+2+3+4+5+6+7+8+9+0", "+(+(+(+(+(+(+(+(+(1, 2), 3), 4), 5), 6), 7), 8), 9), 0)");
      checkEquation("1*2+3+4+5+6+7+8+9+0", "+(+(+(+(+(+(+(+(*(1, 2), 3), 4), 5), 6), 7), 8), 9), 0)");
      checkEquation("1+2+3+4+5*6+7+8+9+0", "+(+(+(+(+(+(+(+(1, 2), 3), 4), *(5, 6)), 7), 8), 9), 0)");
      checkEquation("1+2+3+4+5+6+7+8+9*0", "+(+(+(+(+(+(+(+(1, 2), 3), 4), 5), 6), 7), 8), *(9, 0))");
      checkEquation("1*2+3+4+5*6+7+8+9+0", "+(+(+(+(+(+(+(*(1, 2), 3), 4), *(5, 6)), 7), 8), 9), 0)");
      checkEquation("1*2+3+4+5+6+7+8+9*0", "+(+(+(+(+(+(+(*(1, 2), 3), 4), 5), 6), 7), 8), *(9, 0))");
      checkEquation("1+2+3+4+5*6+7+8+9*0", "+(+(+(+(+(+(+(1, 2), 3), 4), *(5, 6)), 7), 8), *(9, 0))");
      checkEquation("1*2+3+4+5*6+7+8+9*0", "+(+(+(+(+(+(*(1, 2), 3), 4), *(5, 6)), 7), 8), *(9, 0))");

      checkEquation("1*2*3*4*5*6*7*8*9*0", "*(*(*(*(*(*(*(*(*(1, 2), 3), 4), 5), 6), 7), 8), 9), 0)");
      checkEquation("1+2*3*4*5*6*7*8*9*0", "+(1, *(*(*(*(*(*(*(*(2, 3), 4), 5), 6), 7), 8), 9), 0))");
      checkEquation("1*2*3*4*5+6*7*8*9*0", "+(*(*(*(*(1, 2), 3), 4), 5), *(*(*(*(6, 7), 8), 9), 0))");
      checkEquation("1*2*3*4*5*6*7*8*9+0", "+(*(*(*(*(*(*(*(*(1, 2), 3), 4), 5), 6), 7), 8), 9), 0)");
      checkEquation("1+2*3*4*5+6*7*8*9*0", "+(+(1, *(*(*(2, 3), 4), 5)), *(*(*(*(6, 7), 8), 9), 0))");
      checkEquation("1+2*3*4*5*6*7*8*9+0", "+(+(1, *(*(*(*(*(*(*(2, 3), 4), 5), 6), 7), 8), 9)), 0)");
      checkEquation("1*2*3*4*5+6*7*8*9+0", "+(+(*(*(*(*(1, 2), 3), 4), 5), *(*(*(6, 7), 8), 9)), 0)");
      checkEquation("1+2*3*4*5+6*7*8*9+0", "+(+(+(1, *(*(*(2, 3), 4), 5)), *(*(*(6, 7), 8), 9)), 0)");
   }

   @Test
   public void testMultiTerm() {
      String[] sentences = {"p(A, B, C) :- A = 1 , B = 2 , C = 3", "p(X, Y, Z) :- X = 1 , Y = 2 , Z = 3", "p(Q, W, E) :- Q = 1 ; W = 2 ; E = 3"};
      String source = sentences[0] + ".\n" + sentences[1] + ". " + sentences[2] + ".";
      SentenceParser sp = getSentenceParser(source);
      for (String sentence : sentences) {
         Term t = sp.parseSentence();
         assertNotNull(t);
         assertEquals(sentence, write(t));
      }
   }

   @Test
   public void testVariables() {
      Term t = parseSentence("test(A, A, _A, _A, B, _, _).");
      Variable a1 = (Variable) t.getArgument(0);
      Variable a2 = (Variable) t.getArgument(1);
      Variable _a1 = (Variable) t.getArgument(2);
      Variable _a2 = (Variable) t.getArgument(3);
      Variable b = (Variable) t.getArgument(4);
      Variable _1 = (Variable) t.getArgument(5);
      Variable _2 = (Variable) t.getArgument(6);

      assertEquals("A", a1.getId());
      assertEquals("A", a2.getId());
      assertEquals("_A", _a1.getId());
      assertEquals("_A", _a2.getId());
      assertEquals("B", b.getId());
      assertEquals("_", _1.getId());
      assertEquals("_", _2.getId());

      // variables in same clause with same ID should reference the same object
      assertSame(a1, a2);
      assertSame(_a1, _a2);
      // variables in same clause with different IDs should never reference the same object
      assertNotSame(b, a1);
      assertNotSame(b, _a1);
      // different anonymous variables should never reference the same object (despite having the same variable ID)
      assertNotSame(_1, _2);
      // anonymous variables should never reference the same object as a named variable
      assertNotSame(_1, a1);
      assertNotSame(_1, _a1);
      assertNotSame(_1, b);
   }

   @Test
   public void testConjunction() {
      assertParse("a, b, c.", "a , b , c", ",(a, ,(b, c))");
   }

   @Test
   public void testBrackets1() {
      assertParse("a(b,(c)).", "a(b, c)", "a(b, c)");
   }

   @Test
   public void testBrackets2() {
      assertParse("?- fail, fail ; true.", "?- fail , fail ; true", "?-(;(,(fail, fail), true))");
      assertParse("?- fail, (fail;true).", "?- fail , (fail ; true)", "?-(,(fail, ;(fail, true)))");
   }

   @Test
   public void testBrackets3() {
      assertParse("?- X is 4*(2+3).", "?- X is 4 * (2 + 3)", "?-(is(X, *(4, +(2, 3))))");
   }

   @Test
   public void testBrackets4() {
      assertParse("?- Y = ( ## = @@ ).", "?- Y = (## = @@)", "?-(=(Y, =(##, @@)))");
   }

   @Test
   public void testBrackets5() {
      assertParse("?- X = a(b,(c)).", "?- X = a(b, c)", "?-(=(X, a(b, c)))");
   }

   @Test
   public void testBrackets6() {
      assertParse("X = ( A = 1 , B = 2 , C = 3).", "X = (A = 1 , B = 2 , C = 3)", "=(X, ,(=(A, 1), ,(=(B, 2), =(C, 3))))");
   }

   @Test
   public void testParsingBrackets7() {
      assertParse("X = (!).", "X = !", "=(X, !)");
   }

   @Test
   public void testParsingBrackets8() {
      assertParse("X = (a, !).", "X = (a , !)", "=(X, ,(a, !))");
   }

   @Test
   public void testParsingBrackets9() {
      assertParse("X = (a, !; b).", "X = (a , ! ; b)", "=(X, ;(,(a, !), b))");
   }

   @Test
   public void testParsingBrackets10() {
      assertParse("X = [a,'('|Y].", "X = [a,(|Y]", "=(X, .(a, .((, Y)))");
   }

   @Test
   public void testParsingBrackets11() {
      assertParse("a :- (b, c ; e), f.", "a :- (b , c ; e) , f", ":-(a, ,(;(,(b, c), e), f))");
   }

   @Test
   public void testParsingBrackets12() {
      assertParse("a :- z, (b, c ; e), f.", "a :- z , (b , c ; e) , f", ":-(a, ,(z, ,(;(,(b, c), e), f)))");
   }

   @Test
   public void testParsingBrackets13() {
      assertParse("X = (a :- ','(b, c), d).", "X = (a :- b , c , d)", "=(X, :-(a, ,(,(b, c), d)))");
   }

   @Test
   public void testParsingBrackets14() {
      assertParse("X = (a :- b, ','(c, d)).", "X = (a :- b , c , d)", "=(X, :-(a, ,(b, ,(c, d))))");
   }

   @Test
   public void testExtraTextAfterFullStop() {
      SentenceParser sp = getSentenceParser("?- consult(\'bench.pl\'). jkhkj");
      Term t = sp.parseSentence();
      assertEquals("?-(consult(bench.pl))", t.toString());
      try {
         sp.parseSentence();
         fail();
      } catch (ParserException pe) {
         // expected
      }
   }

   @Test
   public void testMixtureOfPrefixInfixAndPostfixOperands() {
      assertParse("a --> { 1 + -2 }.", "a --> { 1 + -2 }", "-->(a, {(}(+(1, -2))))");
   }

   /**
    * Test "xf" (postfix) associativity.
    * <p>
    * A "x" means that the argument can contain operators of <i>only</i> a lower level of priority than the operator
    * represented by "f".
    */
   @Test
   public void testParseOperandXF() {
      Operands o = new Operands();
      o.addOperand("~", "xf", 900);
      SentenceParser sp = SentenceParser.getInstance("a ~.", o);
      Term t = sp.parseSentence();
      assertEquals("~(a)", t.toString());
      try {
         sp = SentenceParser.getInstance("a ~ ~.", o);
         sp.parseSentence();
         fail();
      } catch (ParserException e) {
         // expected
      }
   }

   /**
    * Test "yf" (postfix) associativity.
    * <p>
    * A "y" means that the argument can contain operators of <i>the same</i> or lower level of priority than the
    * operator represented by "f".
    */
   @Test
   public void testParseOperandYF() {
      Operands o = new Operands();
      o.addOperand(":", "yf", 900);
      SentenceParser sp = SentenceParser.getInstance("a : :.", o);
      Term t = sp.parseSentence();
      assertEquals(":(:(a))", t.toString());
   }

   @Test
   public void testBuiltInPredicateNamesAsAtomArguments() {
      check("[=]", ".(=, [])");
      check("[=, = | =]", ".(=, .(=, =))");

      check("[:-]", ".(:-, [])");
      check("[:-, :- | :-]", ".(:-, .(:-, :-))");

      check("p(?-)", "p(?-)");
      check("p(:-)", "p(:-)");
      check("p(<)", "p(<)");

      check("p(1<1,is)", "p(<(1, 1), is)");
      check("p(;, ',', :-, ?-)", "p(;, ,, :-, ?-)");

      check("?- write(p(1, :-, 1))", "?-(write(p(1, :-, 1)))");
      check("?- write(p(1, ',', 1))", "?-(write(p(1, ,, 1)))");
      check("?- write(p(<,>,=))", "?-(write(p(<, >, =)))");

      // following fails as '\+' prefix operand has higher precedence than '/' infix operand
      // Note that need to specify '\+' as '\\\\+' (escape slash once for Java String literal and once for Projog parser)
      error("?- test('\\\\+'/1, 'abc').");
      // following works as explicitly specifying '/' as the functor of a structure
      check("?- test('/'('\\\\+', 1), 'abc')", "?-(test(/(\\+, 1), abc))");

      error("p(a :- b).");
      check("p(:-(a, b))", "p(:-(a, b))");
      check("p(':-'(a, b))", "p(:-(a, b))");
   }

   @Test
   public void testListAfterPrefixOperator() {
      assertParse("?- [a,b,c].", "?- [a,b,c]", "?-(.(a, .(b, .(c, []))))");
   }

   @Test
   public void testSentenceTerminatorAsAtomName() {
      assertParse("p(C) :- C=='.'.", "p(C) :- C == .", ":-(p(C), ==(C, .))");
   }

   @Test
   public void testAlphaNumericPredicateName() {
      String expectedOutput = "is(X, ~(1, 1))";
      check("X is '~'(1,1)", expectedOutput);
      check("X is ~(1,1)", expectedOutput);
   }

   @Test
   public void testInfixOperatorAsPredicateName() {
      String expectedOutput = "is(X, +(1, 1))";
      check("X is '+'(1,1)", expectedOutput);
      check("X is 1+1", expectedOutput);
      check("X is +(1,1)", expectedOutput);
      check("X = >(+(1,1),-2)", "=(X, >(+(1, 1), -2))");
   }

   @Test
   public void testComplexSentence() {
      assertParse(":-(=(A,B,C),;(->(==(A,B),=(C,true)),;(->(\\=(A,B),=(C,false)),;(','(=(C,true),=(A,B)),','(=(C,false),dif(A,B)))))).",
                  "=(A, B, C) :- A == B -> C = true ; A \\= B -> C = false ; C = true , A = B ; C = false , dif(A, B)",
                  ":-(=(A, B, C), ;(->(==(A, B), =(C, true)), ;(->(\\=(A, B), =(C, false)), ;(,(=(C, true), =(A, B)), ,(=(C, false), dif(A, B))))))");

      assertParse("=(A, B, C) :- A == B -> C = true ; A \\= B -> C = false ; C = true , A = B ; C = false , dif(A, B).",
                  "=(A, B, C) :- A == B -> C = true ; A \\= B -> C = false ; C = true , A = B ; C = false , dif(A, B)",
                  ":-(=(A, B, C), ;(->(==(A, B), =(C, true)), ;(->(\\=(A, B), =(C, false)), ;(,(=(C, true), =(A, B)), ,(=(C, false), dif(A, B))))))");
   }

   @Test
   public void testCommasInPredicateArguments() {
      check("p(','(1,2))", "p(,(1, 2))");
      check("p(','(1+2))", "p(,(+(1, 2)))");
      check("p(1,(2+3))", "p(1, +(2, 3))");
      check("p(1,','(2,3))", "p(1, ,(2, 3))");
      check("p(1,','(2+3))", "p(1, ,(+(2, 3)))");
      check("p(1,2','(3+4),5)", "p(1, 2, +(3, 4), 5)");
   }

   @Test
   public void testHasNext() {
      SentenceParser sp = getSentenceParser("A = a. B = b. C = c.");
      assertTrue(sp.hasNext());
      assertEquals("=(A, a)", sp.parseSentence().toString());
      assertTrue(sp.hasNext());
      assertEquals("=(B, b)", sp.parseSentence().toString());
      assertTrue(sp.hasNext());
      assertEquals("=(C, c)", sp.parseSentence().toString());
      assertFalse(sp.hasNext());
      assertNull(sp.parseSentence());
      assertFalse(sp.hasNext());
   }

   private void checkEquation(String input, String expected) {
      check(input, expected);

      // apply same extra tests just because is easy to do...
      check("X is " + input, "is(X, " + expected + ")");
      String conjunction = "X is " + input + ", Y is " + input + ", Z is " + input;
      String expectedConjunctionResult = ",(is(X, " + expected + "), ,(is(Y, " + expected + "), is(Z, " + expected + ")))";
      check(conjunction, expectedConjunctionResult);
      check("?- " + conjunction, "?-(" + expectedConjunctionResult + ")");
      check("test(X, Y, Z) :- " + conjunction, ":-(test(X, Y, Z), " + expectedConjunctionResult + ")");

      for (int n = 0; n < 10; n++) {
         input = input.replace("" + n, "p(" + n + ")");
         expected = expected.replace("" + n, "p(" + n + ")");
      }
   }

   private void error(String input) {
      try {
         Term term = parseSentence(input);
         fail("parsing: " + input + " produced: " + term + " when expected an exception");
      } catch (ParserException pe) {
         // expect ParserException, don't expect EndOfStreamException
         assertNotSame(EndOfStreamException.class, pe.getClass());
      } catch (Exception e) {
         fail("parsing: " + input + " produced: " + e + " when expected a ParserException");
      }
   }

   private void endOfStreamError(String input) {
      assertThrows(EndOfStreamException.class, () -> parseSentence(input));
   }

   /**
    * @param input syntax (not including trailing .) to attempt to produce term for
    * @param expectedOutput what toString method of Term should look like
    */
   private Term check(String input, String expectedOutput) {
      // assert get EndOfStreamException when input is missing a trailing "."
      assertThrows(EndOfStreamException.class, () -> parseSentence(input));
      try {
         Term t = parseSentence(input + ".");
         if (!expectedOutput.equals(t.toString())) {
            throw new Exception("got: " + t + " instead of: " + expectedOutput);
         }
         return t;
      } catch (Exception e) {
         throw new RuntimeException("Exception parsing: " + input + " " + e.getClass() + " " + e.getMessage(), e);
      }
   }

   private void assertParse(String input, String expectedFormatterOutput, String expectedToString) {
      Term t = parseSentence(input);
      assertEquals(expectedFormatterOutput, write(t));
      assertEquals(expectedToString, t.toString());
   }

   private SentenceParser getSentenceParser(String source) {
      return TestUtils.createSentenceParser(source);
   }
}
