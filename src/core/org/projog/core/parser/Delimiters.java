package org.projog.core.parser;

class Delimiters {
   private static final char ARGUMENT_SEPARATOR = ',';
   private static final char PREDICATE_OPENING_BRACKET = '(';
   private static final char PREDICATE_CLOSING_BRACKET = ')';
   private static final char LIST_OPENING_BRACKET = '[';
   private static final char LIST_CLOSING_BRACKET = ']';
   private static final char LIST_TAIL = '|';
   private static final char PERIOD = '.';

   static boolean isDelimiter(String s) {
      return s.length() == 1 && isDelimiter(s.charAt(0));
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

   static boolean isPredicateOpenBracket(String value) {
      return isMatch(value, PREDICATE_OPENING_BRACKET);
   }

   static boolean isPredicateCloseBracket(String value) {
      return isMatch(value, PREDICATE_CLOSING_BRACKET);
   }

   static boolean isListOpenBracket(String value) {
      return isMatch(value, LIST_OPENING_BRACKET);
   }

   static boolean isListOpenBracket(int c) {
      return c == LIST_OPENING_BRACKET;
   }

   static boolean isListCloseBracket(String value) {
      return isMatch(value, LIST_CLOSING_BRACKET);
   }

   static boolean isListTail(String value) {
      return isMatch(value, LIST_TAIL);
   }

   static boolean isArgumentSeperator(String value) {
      return isMatch(value, ARGUMENT_SEPARATOR);
   }

   static boolean isSentenceTerminator(String value) {
      return isMatch(value, PERIOD);
   }

   private static boolean isMatch(String input, char expected) {
      return input.length() == 1 && input.charAt(0) == expected;
   }
}
