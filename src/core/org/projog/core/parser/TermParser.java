/*
 * Copyright 2013 S Webber
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.projog.core.Operands;
import org.projog.core.term.AnonymousVariable;
import org.projog.core.term.Atom;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/**
 * Parses Prolog syntax representing individual terms.
 * <p>
 * The {@code TermParser} does not parse Prolog syntax containing operators. For example it can parse {@code +(X, Y)}
 * but not {@code X + Y}. For Prolog syntax including operators the {@link SentenceParser} should be used instead.
 * </p>
 * <p>
 * <b>Note:</b> not thread safe.
 * </p>
 * 
 * @see SentenceParser
 */
class TermParser {
   private static final char ARGUMENT_SEPARATOR = ',';
   private static final char PREDICATE_OPENING_BRACKET = '(';
   private static final char PREDICATE_CLOSING_BRACKET = ')';
   private static final char LIST_OPENING_BRACKET = '[';
   private static final char LIST_CLOSING_BRACKET = ']';
   private static final char LIST_TAIL = '|';
   /** If an atom's name is enclosed in quotes ({@code '}) then it may contain any character. */
   private static final char QUOTE = '\'';

   /** The dot character indicates the end of a Prolog statement (i.e a fact, rule or query). */
   static final Atom PERIOD = new Atom(".");
   /** A collection of {@code Variable}s this parser currently knows about (key = the variable id). */
   private final HashMap<String, Variable> variables = new HashMap<String, Variable>();
   /** Provides access to the underlying stream used as the source for the Prolog syntax being parsed. */
   protected final CharacterParser parser;
   protected final Operands operands;

   TermParser(CharacterParser parser, Operands operands) {
      this.parser = parser;
      this.operands = operands;
   }

   /**
    * Creates a {@link Term} from Prolog syntax read from this object's {@link CharacterParser}.
    * 
    * @return a {@link Term} created from Prolog syntax read from this object's {@link CharacterParser} or {@code null}
    * if the end of the underlying stream being parsed has been reached
    * @throws ParserException if an error parsing the Prolog syntax occurs
    * @see SentenceParser#parseSentence()
    */
   public Term parseTerm() {
      return parse(false);
   }

   /**
    * Returns collection of {@link Variable} instances created by this {@code TermParser}.
    * <p>
    * Returns all {@link Variable}s created by this {@code TermParser} either since it was created or since the last
    * execution of {@link #clearSharedVariables()}.
    * 
    * @return collection of {@link Variable} instances created by this {@code TermParser}
    * @see #clearSharedVariables()
    */
   @SuppressWarnings("unchecked")
   public Map<String, Variable> getParsedTermVariables() {
      return (Map<String, Variable>) variables.clone();
   }

   /**
    * Clears this parser's record of parsed {@link Variable}s.
    * 
    * @see #getParsedTermVariables()
    */
   protected void clearSharedVariables() {
      variables.clear();
   }

   /**
    * Parses and returns the next argument of a list or structure.
    * <p>
    * Arguments are separated by {@code ,} (commas).
    */
   protected Term getCommaSeparatedArgument() {
      return parse(true);
   }

   /**
    * Parses and returns the term contained in brackets.
    * <p>
    * Sometimes terms are contained in brackets to make it easier for writers and readers of Prolog syntax to avoid
    * ambiguity. e.g. the brackets in {@code X is 1-(2*3)} makes it clear that the result of {@code 2*3} will subtracted
    * from {@code 1} rather than the result of {@code 1-2} being multiplied by {@code 3}.
    */
   protected Term getTermInBrackets() {
      return parse(true);
   }

   /**
    * Throws a new {@link ParserException} with the specified message.
    */
   protected void throwParserException(String message) {
      throw new ParserException(message, parser);
   }

   /**
    * Creates a {@link Term} from Prolog syntax read from this object's {@link CharacterParser}.
    * <p>
    * The behaviour when there is no term to return, because the end of the underlying stream been reached, depends on
    * the value of the {@code throwExceptionRatherThanReturnNull} parameter - if {@code true} a {@code ParserException}
    * will be thrown else {@code null} will be returned.
    */
   private Term parse(boolean throwExceptionRatherThanReturnNull) {
      int c = getNextExcludingCommentsAndWhitespace();
      if (c == -1) {
         if (throwExceptionRatherThanReturnNull) {
            throwParserException("Unexpected end of stream");
         } else {
            return null;
         }
      }
      Term t;
      if (c == LIST_OPENING_BRACKET) {
         t = parseList();
      } else if (isDigit(c)) {
         t = getNumber((char) c);
      } else if (c == QUOTE) {
         String name = getQuotedString();
         t = getAtomOrStructure(name);
      } else if (isLowerCase(c)) {
         String name = getString((char) c);
         t = getAtomOrStructure(name);
      } else if (isUpperCase(c)) {
         String variableId = getString((char) c);
         t = getVariable(variableId);
      } else if (c == '_') {
         t = AnonymousVariable.ANONYMOUS_VARIABLE;
         // call to skipString added for declarations like: _Test
         // where _ is postfixed with some text
         skipString();
      } else if (c == PREDICATE_OPENING_BRACKET) {
         t = getTermInBrackets();
         if (getNextExcludingCommentsAndWhitespace() != PREDICATE_CLOSING_BRACKET) {
            throwParserException("No matching ) for (");
         }
      } else if (c == '.' && parser.peek() == PREDICATE_OPENING_BRACKET) {
         t = getAtomOrStructure(ListFactory.LIST_PREDICATE_NAME);
      } else {
         t = getOperandAtom((char) c);
      }
      return t;
   }

   /** Returns an Atom representing a non-alpha-numeric operand name (e.g. {@code :-}). */
   private Atom getOperandAtom(char startChar) {
      StringBuffer sb = new StringBuffer();
      sb.append(startChar);
      int c;
      while ((c = parser.getNext()) != -1 && !isWhiteSpace(c) && !isValidCharacterForUnquotedAtomName(c)) {
         sb.append((char) c);
      }
      parser.rewind();
      String command = sb.toString();
      if (".".equals(command)) {
         return PERIOD;
      } else if (isValidParseableElement(command)) {
         return new Atom(command);
      } else {
         // track back until find valid operand
         int idx = sb.length();
         while (--idx > 0) {
            command = sb.substring(0, idx);
            if (isValidParseableElement(command)) {
               for (int i = idx; i < sb.length(); i++) {
                  parser.rewind();
               }
               // considered caching operands but decided not to
               return new Atom(command);
            }
         }
         throwParserException("invalid command: " + sb);
         throw new RuntimeException(); // won't get here as above line throws exception first
      }
   }

   private boolean isValidParseableElement(String commandName) {
      // The ! symbol (representing a "Cut") is a special case in that
      // it is non-alphabetic single character that is neither a delimiter nor an operand -
      // but <i>is</i> a valid term in it's own right.
      // TODO is there a way to avoid the following hardcoding of the "!"?
      // (Are the other cases, other than "!", where this could be a problem in future?)
      return isDelimiter(commandName) || "!".equals(commandName) || operands.isDefined(commandName);
   }

   /**
    * Returns a variable with the specified id.
    * <p>
    * If this object already has an instance of {@code Variable} with the specified id then it will be returned else a
    * new {@code Variable} will be created.
    */
   private Variable getVariable(String id) {
      Variable v = variables.get(id);
      if (v == null) {
         v = new Variable(id);
         variables.put(id, v);
      }
      return v;
   }

   /**
    * Returns either an {@code Atom} or {@code Structure} with the specified name.
    * <p>
    * If the next character read from the parser is a {@code (} then a newly created {@code Structure} is returned else
    * a newly created {@code Atom} is returned.
    */
   private Term getAtomOrStructure(String name) {
      int c = parser.getNext();
      if (c == '(') {
         c = getNextExcludingCommentsAndWhitespace();
         if (c == ')') {
            return Structure.createStructure(name, TermUtils.EMPTY_ARRAY);
         }
         ArrayList<Term> args = new ArrayList<Term>();

         parser.rewind();
         Term t = getCommaSeparatedArgument();
         args.add(t);

         do {
            c = getNextExcludingCommentsAndWhitespace();
            if (c == PREDICATE_CLOSING_BRACKET) {
               return Structure.createStructure(name, toArray(args));
            } else if (c == ARGUMENT_SEPARATOR) {
               args.add(getCommaSeparatedArgument());
            } else if (c == -1) {
               throwParserException("No closing ) for " + name);
            } else {
               throwParserException("While parsing arguments of " + name + " expected ) or , but got: " + (char) c);
            }
         } while (true);
      } else {
         parser.rewind();
         return new Atom(name);
      }
   }

   /** Returns a {@code String}, starting with the specified character, read from the parser. */
   private String getString(char startChar) {
      StringBuilder sb = new StringBuilder();
      sb.append(startChar);
      do {
         int c = parser.getNext();
         if (isValidCharacterForUnquotedAtomName(c)) {
            sb.append((char) c);
         } else {
            parser.rewind();
            return sb.toString();
         }
      } while (true);
   }

   /** Reads, and ignores, characters from the parser until it reaches one that is not suitable for the name of an atom. */
   private void skipString() {
      while (isValidCharacterForUnquotedAtomName(parser.getNext())) {
      }
      parser.rewind();
   }

   /** Returns a newly created {@code List} with elements read from the parser. */
   private Term parseList() {
      ArrayList<Term> args = new ArrayList<Term>();
      Term tail = EmptyList.EMPTY_LIST;

      while (true) {
         int c = getNextExcludingCommentsAndWhitespace();
         if (c == LIST_CLOSING_BRACKET) {
            break;
         } else if (c == -1) {
            throwParserException("No ] to mark end of list");
         }
         parser.rewind();
         Term arg = getCommaSeparatedArgument();
         args.add(arg);

         c = getNextExcludingCommentsAndWhitespace(); // | ] or ,
         if (c == LIST_CLOSING_BRACKET) {
            break;
         } else if (c == LIST_TAIL) { // tail
            tail = getCommaSeparatedArgument();
            c = getNextExcludingCommentsAndWhitespace();
            if (c != LIST_CLOSING_BRACKET) {
               throwParserException("No ] to mark end of list after tail");
            }
            break;
         } else if (c != ARGUMENT_SEPARATOR) {
            if (c == -1) {
               throwParserException("No ] to mark end of list");
            } else {
               throwParserException("While parsing list expected ] | or , but got: " + (char) c);
            }
         }
      }
      return ListFactory.create(toArray(args), tail);
   }

   /** Returns a {@code String} consisting of all characters read from the parser up to the next {@code '}. */
   private String getQuotedString() {
      StringBuilder sb = new StringBuilder();
      do {
         int c = parser.getNext();
         if (c == QUOTE) {
            c = parser.getNext();
            // If we reach a ' that is not immediately followed by another '
            // we assume we have reached the end of the string.
            // If we find a ' that is immediately followed by another ' (i.e. '') 
            // we treat it as a single ' - this is so the ' character can be included in strings. 
            // e.g. 'abc''def' will be treated as  a single string with the value abc'def
            if (c != QUOTE) {
               // found closing '
               parser.rewind();
               return sb.toString();
            }
         } else if (c == -1) {
            throwParserException("No closing ' on quoted string");
         }
         sb.append((char) c);
      } while (true);
   }

   /**
    * Returns a {@code Numeric} representing the number, starting with the specified character, read from the parser.
    * <p>
    * The exact implementation returned will be either a {@code DoubleNumber} or {@code IntegerNumber}. Deals with
    * numbers of the form {@code 3.4028235E38}.
    */
   private Numeric getNumber(char startChar) {
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
               throwParserException("unexpected: " + (char) c);
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
         throwParserException("expected digit after e");
      }
      if (readDecimalPoint || readExponent) {
         double value = Double.parseDouble(sb.toString());
         return new DoubleNumber(value);
      } else {
         int value = Integer.parseInt(sb.toString());
         return new IntegerNumber(value);
      }
   }

   /**
    * Returns the next character from this object's {@link CharacterParser} that is not whitespace or a comment.
    * 
    * @return The next character read or -1 if the end of the stream has been reached
    */
   private int getNextExcludingCommentsAndWhitespace() {
      do {
         int c = parser.getNext();
         if (c == '%') {
            // skip single line comment
            parser.skipLine();
         } else if (c == '/' && parser.peek() == '*') {
            // skip multi-line comment
            parser.getNext(); // read the * we just peeked at
            int previousChar;
            do {
               previousChar = c;
               c = parser.getNext();
               if (c == -1) {
                  throwParserException("No */ closing multi-line comment");
               }
            } while (previousChar != '*' || c != '/');
         } else if (!isWhiteSpace(c)) {
            return c;
         }
      } while (true);
   }

   /**
    * Although the first character of an unquoted atom's name must be lower case, following characters can be lower or
    * upper case, digits or an underscore ({@code _}).
    */
   private boolean isValidCharacterForUnquotedAtomName(int c) {
      return isUpperCase(c) || isLowerCase(c) || isDigit(c) || c == '_';
   }

   private boolean isLowerCase(int c) {
      return c >= 'a' && c <= 'z';
   }

   private boolean isUpperCase(int c) {
      return c >= 'A' && c <= 'Z';
   }

   private boolean isDigit(int c) {
      return c >= '0' && c <= '9';
   }

   private boolean isWhiteSpace(int c) {
      return c == ' ' || c == '\t' || c == '\r' || c == '\n';
   }

   private boolean isDelimiter(String s) {
      return s.length() == 1 && isDelimiter(s.charAt(0));
   }

   protected boolean isDelimiter(char c) {
      return c == ARGUMENT_SEPARATOR || c == PREDICATE_CLOSING_BRACKET || c == LIST_CLOSING_BRACKET || c == LIST_TAIL;
   }

   private Term[] toArray(ArrayList<Term> al) {
      return al.toArray(TermUtils.EMPTY_ARRAY);
   }
}