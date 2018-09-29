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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.core.KnowledgeBaseUtils.getOperands;
import static org.projog.core.parser.TokenType.ANONYMOUS_VARIABLE;
import static org.projog.core.parser.TokenType.ATOM;
import static org.projog.core.parser.TokenType.FLOAT;
import static org.projog.core.parser.TokenType.INTEGER;
import static org.projog.core.parser.TokenType.QUOTED_ATOM;
import static org.projog.core.parser.TokenType.VARIABLE;

import java.io.StringReader;

import org.junit.Test;
import org.projog.core.Operands;

public class TokenParserTest {
   private final Operands operands = getOperands(createKnowledgeBase());

   @Test
   public void testAtom() {
      assertTokenType("a", ATOM);
      assertTokenType("ab", ATOM);
      assertTokenType("aB", ATOM);
      assertTokenType("a1", ATOM);
      assertTokenType("a_", ATOM);
      assertTokenType("a_2bY", ATOM);
   }

   @Test
   public void testQuotedAtom() {
      assertTokenType("'abcdefg'", "abcdefg", QUOTED_ATOM);
      assertTokenType("''", "", QUOTED_ATOM);
      assertTokenType("''''", "'", QUOTED_ATOM);
      assertTokenType("''''''''''", "''''", QUOTED_ATOM);
      assertTokenType("'q 1 \" 0.5 | '' @#~'", "q 1 \" 0.5 | ' @#~", QUOTED_ATOM);
   }

   @Test
   public void testVariable() {
      assertTokenType("X", VARIABLE);
      assertTokenType("XY", VARIABLE);
      assertTokenType("Xy", VARIABLE);
      assertTokenType("X1", VARIABLE);
      assertTokenType("X_", VARIABLE);
      assertTokenType("X_7hU", VARIABLE);
   }

   @Test
   public void testAnonymousVariable() {
      assertTokenType("_", ANONYMOUS_VARIABLE);
      assertTokenType("__", ANONYMOUS_VARIABLE);
      assertTokenType("_X", ANONYMOUS_VARIABLE);
      assertTokenType("_x", ANONYMOUS_VARIABLE);
      assertTokenType("_2", ANONYMOUS_VARIABLE);
      assertTokenType("_X_2a", ANONYMOUS_VARIABLE);
   }

   @Test
   public void testInteger() {
      assertTokenType("0", INTEGER);
      assertTokenType("1", INTEGER);
      assertTokenType("6465456456", INTEGER);
   }

   @Test
   public void testFloat() {
      assertTokenType("0.0", FLOAT);
      assertTokenType("0.1", FLOAT);
      assertTokenType("768.567567", FLOAT);
      assertTokenType("3.4028235E38", FLOAT);
      assertTokenType("3.4028235e38", FLOAT);
   }

   @Test
   public void testUnescapedCharCode() {
      for (char c = '!'; c <= '~'; c++) {
         // ignore escape character - that is tested in testEscapedCharCode instead
         if (c != '\\') {
            assertCharCode("0'" + c, c);
         }
      }
   }

   @Test
   public void testEscapedCharCode() {
      assertCharCode("0'\\t", '\t');
      assertCharCode("0'\\b", '\b');
      assertCharCode("0'\\n", '\n');
      assertCharCode("0'\\r", '\r');
      assertCharCode("0'\\f", '\f');
      assertCharCode("0'\\'", '\'');
      assertCharCode("0'\\\"", '\"');
      assertCharCode("0'\\\\", '\\');
   }

   @Test
   public void testInvalidEscapedCharCode() {
      assertParserException("0'\\a", "invalid character escape sequence Line: 0'\\a");
      assertParserException("0'\\A", "invalid character escape sequence Line: 0'\\A");
      assertParserException("0'\\1", "invalid character escape sequence Line: 0'\\1");
      assertParserException("0'\\ ", "invalid character escape sequence Line: 0'\\ ");
      assertParserException("0'\\.", "invalid character escape sequence Line: 0'\\.");
   }

   @Test
   public void testUnicodeCharCode() {
      assertCharCode("0'\\u0020", ' ');
      assertCharCode("0'\\u0061", 'a');
      assertCharCode("0'\\u0059", 'Y');
      assertCharCode("0'\\u00A5", 165);
      assertCharCode("0'\\u017F", 383);
      assertCharCode("0'\\u1E6A", '\u1E6A');
      assertCharCode("0'\\u1EF3", '\u1EF3');
      assertCharCode("0'\\u00a5", 165);
      assertCharCode("0'\\u1ef3", '\u1EF3');
      assertCharCode("0'\\uabcd", '\uabcd');
      assertCharCode("0'\\u1eF3", '\u1EF3');
   }

   @Test
   public void testInvalidUnicodeCharCode() {
      // not letters or numbers
      assertParserException("0'\\u12-4", "invalid unicode value Line: 0'\\u12-4");
      assertParserException("0'\\u12/4", "invalid unicode value Line: 0'\\u12/4");
      assertParserException("0'\\u12:4", "invalid unicode value Line: 0'\\u12:4");
      assertParserException("0'\\u12@4", "invalid unicode value Line: 0'\\u12@4");

      // not hex letter
      assertParserException("0'\\u12G4", "invalid unicode value Line: 0'\\u12G4");
      assertParserException("0'\\u12g4", "invalid unicode value Line: 0'\\u12g4");
      assertParserException("0'\\u12Z4", "invalid unicode value Line: 0'\\u12Z4");
      assertParserException("0'\\u12z4", "invalid unicode value Line: 0'\\u12z4");

      // too short
      assertParserException("0'\\u12", "invalid unicode value Line: 0'\\u12");
      assertParserException("0'\\u12\n4", "invalid unicode value Line: 0'\\u12");
      assertParserException("0'\\u12.", "invalid unicode value Line: 0'\\u12.");
      assertParserException("0'\\u.", "invalid unicode value Line: 0'\\u.");
      assertParserException("0'\\u", "invalid unicode value Line: 0'\\u");
   }

   @Test
   public void testEmptyInput() {
      assertFalse(create("").hasNext());
      assertFalse(create("\t \r\n   ").hasNext());
      assertFalse(create("%abcde").hasNext()); // single line comment
      assertFalse(create("/* hgjh\nghj*/").hasNext()); // multi line comment
   }

   @Test
   public void testSequence() {
      assertParse("Abc12.5@>=-0_2_jgkj a-2hUY_ty\nu\n% kghjgkj\na/*b*/c 0'zyz 0' 0'\u00610'\u0062345", "Abc12", ".", "5", "@>=", "-", "0", "_2_jgkj", "a", "-", "2", "hUY_ty", "u",
                  "a", "c", "122", "yz", "32", "97", "98", "345");
   }

   @Test
   public void testSentence() {
      assertParse("X is ~( 'Y', 1 ,a).", "X", "is", "~", "(", "Y", ",", "1", ",", "a", ")", ".");
   }

   @Test
   public void testNonAlphanumericCharacterFollowedByPeriod() {
      assertParse("!.", "!", ".");
   }

   /** Test that "!" and ";" get parsed separately, rather than as single combined "!;" element. */
   @Test
   public void testCutFollowedByDisjunction() {
      assertParse("!;true", "!", ";", "true");
   }

   /** Test that "(", "!", ")" and "." get parsed separately, rather than as single combined "(!)." element. */
   @Test
   public void testCutInBrackets() {
      assertParse("(!).", "(", "!", ")", ".");
   }

   @Test
   public void testWhitespaceAndComments() {
      TokenParser p = create("/* comment */\t % comment\n % comment\r\n\n");
      assertFalse(p.hasNext());
   }

   @Test
   public void testMultiLineComments() {
      assertParse("/*\n\n*\n/\n*/a/*/b*c/d/*e*/f", "a", "f");
   }

   @Test
   public void testFollowedByTerm() {
      TokenParser tp = create("?- , [ abc )");
      tp.next();
      assertFalse(tp.isFollowedByTerm());
      tp.next();
      assertTrue(tp.isFollowedByTerm());
      tp.next();
      assertTrue(tp.isFollowedByTerm());
      tp.next();
      assertFalse(tp.isFollowedByTerm());
   }

   /** @see {@link TokenParser#rewind(String)} */
   @Test
   public void testRewindException() {
      TokenParser tp = create("a b c");
      assertEquals("a", tp.next().value);
      Token b = tp.next();
      assertEquals("b", b.value);
      tp.rewind(b);
      assertSame(b, tp.next());
      tp.rewind(b);

      // check that can only rewind one token
      assertRewindException(tp, "b");
      assertRewindException(tp, "a");

      assertEquals("b", tp.next().value);
      Token c = tp.next();
      assertEquals("c", c.value);

      // check that the value specified in call to rewind has to be the last value parsed
      assertRewindException(tp, "b");
      assertRewindException(tp, null);
      assertRewindException(tp, "z");

      tp.rewind(c);
      assertSame(c, tp.next());
      assertFalse(tp.hasNext());
      tp.rewind(c);
      assertTrue(tp.hasNext());

      // check that can only rewind one token
      assertRewindException(tp, "c");
   }

   private void assertRewindException(TokenParser tp, String value) {
      try {
         tp.rewind(new Token(value, TokenType.ATOM));
         fail();
      } catch (IllegalArgumentException e) {
         // expected
      }
   }

   private void assertCharCode(String input, int expectedOutput) {
      assertTokenType(input, Integer.toString(expectedOutput), INTEGER);
   }

   private void assertTokenType(String syntax, TokenType type) {
      assertTokenType(syntax, syntax, type);
   }

   private void assertTokenType(String syntax, String value, TokenType type) {
      TokenParser p = create(syntax);
      assertTrue(p.hasNext());
      Token token = p.next();
      assertEquals(value, token.value);
      assertSame(type, token.type);
      assertFalse(p.hasNext());
   }

   private void assertParse(String sentence, String... tokens) {
      TokenParser tp = create(sentence);
      for (String w : tokens) {
         Token next = tp.next();
         assertEquals(w, next.value);
         tp.rewind(next);
         assertSame(next, tp.next());
      }
      assertFalse(tp.hasNext());
      try {
         tp.next();
         fail();
      } catch (ParserException e) {
         assertEquals("Unexpected end of stream Line: " + e.getLine(), e.getMessage());
      }
   }

   private void assertParserException(String input, String expectedExceptionMessage) {
      try {
         TokenParser p = create(input);
         p.next();
         fail();
      } catch (ParserException e) {
         assertEquals(expectedExceptionMessage, e.getMessage());
      }
   }

   private TokenParser create(String syntax) {
      StringReader sr = new StringReader(syntax);
      return new TokenParser(sr, operands);
   }
}
