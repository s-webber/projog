package org.projog.core.parser;

import static org.projog.core.parser.WordType.SYMBOL;

class Delimiters {
   private static final char ARGUMENT_SEPARATOR = ',';
   private static final char PREDICATE_OPENING_BRACKET = '(';
   private static final char PREDICATE_CLOSING_BRACKET = ')';
   private static final char LIST_OPENING_BRACKET = '[';
   private static final char LIST_CLOSING_BRACKET = ']';
   private static final char LIST_TAIL = '|';
   private static final char PERIOD = '.';

   static boolean isDelimiter(String s) {
      return s != null && s.length() == 1 && isDelimiter(s.charAt(0));
   }

   static boolean isDelimiter(int c) {
      switch (c) {
         case ARGUMENT_SEPARATOR:
         case PREDICATE_OPENING_BRACKET:
         case PREDICATE_CLOSING_BRACKET:
         case LIST_OPENING_BRACKET:
         case LIST_CLOSING_BRACKET:
         case LIST_TAIL:
         case PERIOD:
            return true;
         default:
            return false;
      }
   }

   static boolean isListOpenBracket(int c) {
      return c == LIST_OPENING_BRACKET;
   }

   static boolean isPredicateOpenBracket(Word word) {
      return isMatch(word, PREDICATE_OPENING_BRACKET);
   }

   static boolean isPredicateCloseBracket(Word word) {
      return isMatch(word, PREDICATE_CLOSING_BRACKET);
   }

   static boolean isListOpenBracket(Word word) {
      return isMatch(word, LIST_OPENING_BRACKET);
   }

   static boolean isListCloseBracket(Word word) {
      return isMatch(word, LIST_CLOSING_BRACKET);
   }

   static boolean isListTail(Word word) {
      return isMatch(word, LIST_TAIL);
   }

   static boolean isArgumentSeperator(Word word) {
      return isMatch(word, ARGUMENT_SEPARATOR);
   }

   static boolean isSentenceTerminator(Word word) {
      return isMatch(word, PERIOD);
   }

   private static boolean isMatch(Word word, char expected) {
      return word != null && word.type == SYMBOL && word.value != null && word.value.length() == 1 && word.value.charAt(0) == expected;
   }
}
