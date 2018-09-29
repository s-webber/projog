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

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.isWhitespace;
import static org.projog.core.parser.Delimiters.isDelimiter;
import static org.projog.core.parser.Delimiters.isListOpenBracket;
import static org.projog.core.parser.TokenType.ANONYMOUS_VARIABLE;
import static org.projog.core.parser.TokenType.ATOM;
import static org.projog.core.parser.TokenType.FLOAT;
import static org.projog.core.parser.TokenType.INTEGER;
import static org.projog.core.parser.TokenType.QUOTED_ATOM;
import static org.projog.core.parser.TokenType.SYMBOL;
import static org.projog.core.parser.TokenType.VARIABLE;

import java.io.BufferedReader;
import java.io.Reader;

import org.projog.core.Operands;

/**
 * Parses an input stream into discrete 'tokens' that are used to represent Prolog queries and rules.
 *
 * @see SentenceParser
 */
class TokenParser {
   private final CharacterParser parser;
   private final Operands operands;
   private Token lastParsedToken;
   private boolean rewound;

   TokenParser(Reader reader, Operands operands) {
      BufferedReader br = new BufferedReader(reader);
      this.parser = new CharacterParser(br);
      this.operands = operands;
   }

   /** @return {@code true} if there are more tokens to be parsed, else {@code false} */
   boolean hasNext() {
      if (rewound) {
         return true;
      } else {
         skipWhitespaceAndComments();
         return !isEndOfStream(parser.peek());
      }
   }

   /**
    * Parse and return the next {@code Token}.
    *
    * @return the token that was parsed as a result of this call
    * @throws ParserException if there are no more tokens to parse (i.e. parser has reached the end of the underlying
    * input stream)
    */
   Token next() {
      if (rewound) {
         rewound = false;
      } else {
         lastParsedToken = parseToken();
      }
      return lastParsedToken;
   }

   private Token parseToken() {
      skipWhitespaceAndComments();
      final int c = parser.getNext();
      if (isEndOfStream(c)) {
         throw newParserException("Unexpected end of stream");
      } else if (isUpperCase(c)) {
         return parseText(c, VARIABLE);
      } else if (isAnonymousVariable(c)) {
         return parseText(c, ANONYMOUS_VARIABLE);
      } else if (isLowerCase(c)) {
         return parseText(c, ATOM);
      } else if (isQuote(c)) {
         return parseQuotedText();
      } else if (isZero(c)) {
         return parseLeadingZero(c);
      } else if (isDigit(c)) {
         return parseNumber(c);
      } else {
         return parseSymbol(c);
      }
   }

   /**
    * Rewinds the parser (i.e. "pushes-back" the last parsed token).
    * <p>
    * The last parsed value will remain after the next call to {@link #next()}
    *
    * @param value the value to rewind
    * @throws IllegalArgumentException if already in a rewound state (i.e. have already called
    * {@link TokenParser#rewind(String)} since the last call to {@link #next()}), or {@code value} is not equal to
    * {@link #getValue()}
    */
   void rewind(Token value) {
      if (lastParsedToken != value) {
         throw new IllegalArgumentException();
      }
      rewound = true;
   }

   /** Does the next value to be parsed represent a term (rather than a delimiter) */
   boolean isFollowedByTerm() {
      skipWhitespaceAndComments();
      int nextChar = parser.peek();
      return isListOpenBracket(nextChar) || !isDelimiter(nextChar);
   }

   /** Returns a new {@link ParserException} with the specified message. */
   ParserException newParserException(String message) {
      throw new ParserException(message, parser);
   }

   private void skipWhitespaceAndComments() {
      while (true) {
         final int c = parser.getNext();
         if (isEndOfStream(c)) {
            return;
         } else if (isWhitespace(c)) {
            skipWhitespace();
         } else if (isSingleLineComment(c)) {
            parser.skipLine(); // skip comment
         } else if (isMultiLineCommentStart(c, parser.peek())) {
            skipMultiLineComment();
         } else {
            parser.rewind();
            return;
         }
      }
   }

   /** @param c the first, already parsed, character of the token. */
   private Token parseText(int c, TokenType t) {
      StringBuilder sb = new StringBuilder();

      do {
         sb.append((char) c);
         c = parser.getNext();
      } while (isValidForAtom(c));
      parser.rewind();

      return createToken(sb, t);
   }

   /**
    * Reads a {@code String} consisting of all characters read from the parser up to the next {@code '}.
    * <p>
    * If an atom's name is enclosed in quotes (i.e. {@code '}) then it may contain any character.
    * </p>
    */
   private Token parseQuotedText() {
      StringBuilder sb = new StringBuilder();
      do {
         int c = parser.getNext();
         if (isQuote(c)) {
            c = parser.getNext();
            // If we reach a ' that is not immediately followed by another '
            // we assume we have reached the end of the string.
            // If we find a ' that is immediately followed by another ' (i.e. '')
            // we treat it as a single ' - this is so the ' character can be included in strings.
            // e.g. 'abc''def' will be treated as  a single string with the value abc'def
            if (!isQuote(c)) {
               // found closing '
               parser.rewind();
               return createToken(sb, QUOTED_ATOM);
            }
         } else if (isEscapeSequencePrefix(c)) {
            c = parseEscapeSequence();
         } else if (isEndOfStream(c)) {
            throw newParserException("No closing ' on quoted string");
         }
         sb.append((char) c);
      } while (true);
   }

   /**
    * Parses a character code and represents it as an integer.
    * <p>
    * e.g. the text {@code 0'a} results in a token with the value {@code 97} (the ascii value for {@code a}) being
    * returned.
    */
   private Token parseLeadingZero(int zero) {
      if (zero != '0') {
         // sanity check - should never get here, as have already checked that the next character is a single quote
         throw new IllegalStateException();
      }

      if (!isQuote(parser.peek())) {
         return parseNumber(zero);
      }

      parser.getNext(); // skip single quote

      int code;
      int next = parser.getNext();
      if (next == -1) {
         throw newParserException("unexpected end of file after '");
      }

      if (isEscapeSequencePrefix(next)) {
         code = parseEscapeSequence();
      } else {
         code = next; // e.g. 0'a
      }

      return createToken(Integer.toString(code), INTEGER);
   }

   private int parseEscapeSequence() {
      int next = parser.getNext();
      if (next == 'u') {
         // e.g. 0'\u00a5
         return parseUnicode();
      } else {
         // e.g. 0'\n
         return escape(next);
      }
   }

   private int parseUnicode() {
      StringBuilder hex = new StringBuilder(4);
      hex.append(parseHex());
      hex.append(parseHex());
      hex.append(parseHex());
      hex.append(parseHex());
      return Integer.parseInt(hex.toString(), 16);
   }

   private char parseHex() {
      int h = parser.getNext();
      if (isDigit(h)) {
         return (char) h;
      } else if (h >= 'a' && h <= 'f') {
         return (char) h;
      } else if (h >= 'A' && h <= 'F') {
         return (char) h;
      } else {
         throw newParserException("invalid unicode value");
      }
   }

   /**
    * Parses a number, starting with the specified character, read from the parser.
    * <p>
    * Deals with numbers of the form {@code 3.4028235E38}.
    */
   private Token parseNumber(final int startChar) {
      StringBuilder sb = new StringBuilder();

      boolean keepGoing = true;
      boolean readDecimalPoint = false;
      boolean readExponent = false;
      boolean wasLastCharExponent = false;
      int c = startChar;
      do {
         sb.append((char) c);
         c = parser.getNext();
         if (c == '.') {
            if (readDecimalPoint) {
               // can't have more than one decimal point per number
               keepGoing = false;
            } else if (isDigit(parser.peek())) {
               readDecimalPoint = true;
            } else {
               // must be a digit after . for it to be a decimal number
               keepGoing = false;
            }
         } else if (c == 'e' || c == 'E') {
            if (readExponent) {
               throw newParserException("unexpected: " + (char) c);
            }
            readExponent = true;
            wasLastCharExponent = true;
         } else if (!isDigit(c)) {
            keepGoing = false;
         } else {
            wasLastCharExponent = false;
         }
      } while (keepGoing);
      parser.rewind();
      if (wasLastCharExponent) {
         throw newParserException("expected digit after e");
      }

      return createToken(sb, readDecimalPoint ? FLOAT : INTEGER);
   }

   private int escape(int escape) {
      // https://docs.oracle.com/javase/tutorial/java/data/characters.html
      switch (escape) {
         case 't': // tab
            return '\t';
         case 'b': // backspace
            return '\b';
         case 'n': // newline
            return '\n';
         case 'r': // carriage return
            return '\r';
         case 'f': // formfeed
            return '\f';
         case '\'': // single quote
            return '\'';
         case '\"': // double quote
            return '\"';
         case '\\': // backslash
            return '\\';
         default:
            throw newParserException("invalid character escape sequence");
      }
   }

   private Token parseSymbol(int c) {
      StringBuilder sb = new StringBuilder();
      do {
         sb.append((char) c);
         c = parser.getNext();
      } while (!isAlphabetic(c) && !isDigit(c) && !isWhitespace(c) && !isEndOfStream(c));
      parser.rewind();

      if (isValidParseableElement(sb.toString())) {
         return createToken(sb, SYMBOL);
      }

      int length = sb.length();
      int idx = length;
      while (--idx > 0) {
         final String substring = sb.substring(0, idx);
         if (isValidParseableElement(substring)) {
            parser.rewind(length - idx);
            return createToken(substring, SYMBOL);
         }
      }

      for (int i = 1; i < length; i++) {
         final String substring = sb.substring(i);
         if (isValidParseableElement(substring) || isDelimiter(sb.charAt(i))) {
            parser.rewind(length - i);
            return createToken(sb.substring(0, i), SYMBOL);
         }
      }

      return createToken(sb, SYMBOL);
   }

   private void skipWhitespace() {
      while (isWhitespace(parser.peek())) {
         parser.getNext();
      }
   }

   private void skipMultiLineComment() {
      parser.getNext(); // skip * after /
      int previous = parser.getNext();
      while (true) {
         int current = parser.getNext();
         if (isEndOfStream(current)) {
            throw newParserException("Missing */ to close multi-line comment");
         } else if (isMultiLineCommentEnd(previous, current)) {
            return;
         } else {
            previous = current;
         }
      }
   }

   private boolean isValidParseableElement(String commandName) {
      return isDelimiter(commandName) || operands.isDefined(commandName);
   }

   private static boolean isEndOfStream(int c) {
      return c == -1;
   }

   private static boolean isSingleLineComment(int c) {
      return c == '%';
   }

   private static boolean isMultiLineCommentStart(int c1, int c2) {
      return c1 == '/' && c2 == '*';
   }

   private static boolean isMultiLineCommentEnd(int c1, int c2) {
      return c1 == '*' && c2 == '/';
   }

   private static boolean isValidForAtom(int c) {
      return isAlphabetic(c) || isDigit(c) || isAnonymousVariable(c);
   }

   private static boolean isAnonymousVariable(int c) {
      return c == '_';
   }

   private static boolean isQuote(int c) {
      return c == '\'';
   }

   private static boolean isZero(int c) {
      return c == '0';
   }

   private static boolean isEscapeSequencePrefix(int c) {
      return c == '\\';
   }

   private static Token createToken(StringBuilder value, TokenType type) {
      return createToken(value.toString(), type);
   }

   private static Token createToken(String value, TokenType type) {
      return new Token(value, type);
   }
}
