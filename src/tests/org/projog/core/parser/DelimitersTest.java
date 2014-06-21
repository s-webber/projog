package org.projog.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DelimitersTest {
   @Test
   public void testArgumentSeperator() {
      assertTrue(Delimiters.isArgumentSeperator(","));
      assertFalse(Delimiters.isArgumentSeperator(";"));
      assertFalse(Delimiters.isArgumentSeperator(" "));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testListOpenBracket() {
      assertTrue(Delimiters.isListOpenBracket("["));
      assertFalse(Delimiters.isListOpenBracket("]"));
      assertFalse(Delimiters.isListOpenBracket("("));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testListCloseBracket() {
      assertTrue(Delimiters.isListCloseBracket("]"));
      assertFalse(Delimiters.isListCloseBracket("["));
      assertFalse(Delimiters.isListCloseBracket(")"));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testPredicateOpenBracket() {
      assertTrue(Delimiters.isPredicateOpenBracket("("));
      assertFalse(Delimiters.isPredicateOpenBracket(")"));
      assertFalse(Delimiters.isPredicateOpenBracket("["));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testPredicateCloseBracket() {
      assertTrue(Delimiters.isPredicateCloseBracket(")"));
      assertFalse(Delimiters.isPredicateCloseBracket("("));
      assertFalse(Delimiters.isPredicateCloseBracket("]"));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testListTail() {
      assertTrue(Delimiters.isListTail("|"));
      assertFalse(Delimiters.isListTail("["));
      assertFalse(Delimiters.isListTail("]"));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testSentenceTerminator() {
      assertTrue(Delimiters.isSentenceTerminator("."));
      assertFalse(Delimiters.isSentenceTerminator("..="));
      assertFalse(Delimiters.isSentenceTerminator(","));
      assertFalse(Delimiters.isArgumentSeperator(null));
   }

   @Test
   public void testDelimiter() {
      assertDelimiter(true, '[', ']', '(', ')', '|', ',', '.');
      assertDelimiter(false, '!', '?', '{', '}', ':', ';', '-', 'a', 'A', '1');
      assertFalse(Delimiters.isDelimiter("..="));
      assertFalse(Delimiters.isDelimiter(null));
   }

   private void assertDelimiter(boolean expectedResult, char... chars) {
      for (char c : chars) {
         assertEquals(expectedResult, Delimiters.isDelimiter(c));
         assertEquals(expectedResult, Delimiters.isDelimiter(Character.toString(c)));
      }
   }
}
