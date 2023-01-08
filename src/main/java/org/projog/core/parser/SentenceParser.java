/*
 * Copyright 2013 S. Webber
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

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static org.projog.core.parser.Delimiters.isArgumentSeperator;
import static org.projog.core.parser.Delimiters.isListCloseBracket;
import static org.projog.core.parser.Delimiters.isListOpenBracket;
import static org.projog.core.parser.Delimiters.isListTail;
import static org.projog.core.parser.Delimiters.isPredicateCloseBracket;
import static org.projog.core.parser.Delimiters.isPredicateOpenBracket;
import static org.projog.core.parser.Delimiters.isSentenceTerminator;
import static org.projog.core.term.Variable.ANONYMOUS_VARIABLE_ID;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/**
 * Parses Prolog syntax representing rules including operators.
 * <p>
 * <b>Note:</b> not thread safe.
 * </p>
 *
 * @see Operands
 */
public class SentenceParser {
   private static final String MINUS_SIGN = "-";
   private final TokenParser parser;
   private final Operands operands;
   /**
    * A collection of {@code Variable}s this parser currently knows about (key = the variable name).
    * <p>
    * The reason this information needs to be stored is so that each instance of the same variable name, in a single
    * sentence, refers to the same {@code Variable} instance.
    * <p>
    * e.g. During the parsing of the sentence <code>Y is 2, X is Y * 2.</code> two {@code Variable} instances need to be
    * created - one for the variable name <code>X</code> and one that is shared between both references to the variable
    * name <code>Y</code>.
    */
   private final HashMap<String, Variable> variables = new HashMap<>();
   /**
    * Tokens created, during the parsing of the current sentence, that were represented using infix notation.
    * <p>
    * Example of infix notation: <code>X = 1</code> where the predicate name <code>=</code> is positioned between its
    * two arguments <code>X</code> and <code>1</code>.
    * <p>
    * The reason these need to be kept a record of is, when the sentence has been fully read, the individual tokens can
    * be reordered to conform to operator precedence.
    * <p>
    * e.g. <code>1+2/3</code> will get ordered like: <code>+(1, /(2, 3))</code> while <code>1/2+3</code> will be ordered
    * like: <code>+(/(1, 2), 3)</code>.
    */
   private final Set<Token> parsedInfixTokens = new HashSet<>();
   /**
    * Tokens created, during the parsing of the current sentence, that were enclosed in brackets.
    * <p>
    * The reason this information needs to be stored is so that the parser knows to <i>not</i> reorder these tokens as
    * part of the reordering of infix tokens that occurs once the sentence is fully read. i.e. Using brackets to
    * explicitly define the ordering of tokens overrules the default operator precedence of infix tokens.
    * <p>
    * e.g. Although <code>1/2+3</code> will be ordered like: <code>+(/(1, 2), 3)</code>, <code>1/(2+3)</code> (i.e.
    * where <code>2+3</code> is enclosed in brackets) will be ordered like: <code>/(1, +(2, 3))</code>.
    */
   private final Set<Token> bracketedTokens = new HashSet<>();

   /**
    * Returns a new {@code SentenceParser} will parse the specified {@code String} using the specified {@code Operands}.
    *
    * @param prologSyntax the prolog syntax to be parsed
    * @param operands details of the operands to use during parsing
    * @return a new {@code SentenceParser}
    */
   public static SentenceParser getInstance(String prologSyntax, Operands operands) {
      Reader reader = new StringReader(prologSyntax);
      return getInstance(reader, operands);
   }

   /**
    * Returns a new {@code SentenceParser} that will parse Prolog syntax read from the specified {@code Reader} using
    * the specified {@code Operands}.
    *
    * @param reader the source of the prolog syntax to be parsed
    * @param operands details of the operands to use during parsing
    * @return a new {@code SentenceParser}
    */
   public static SentenceParser getInstance(Reader reader, Operands operands) {
      BufferedReader br = new BufferedReader(reader);
      return new SentenceParser(br, operands);
   }

   private SentenceParser(Reader reader, Operands operands) {
      this.parser = new TokenParser(reader, operands);
      this.operands = operands;
   }

   /**
    * Creates a {@link Term} from Prolog syntax, terminated by a {@code .}, read from this object's
    * {@link CharacterParser}.
    *
    * @return a {@link Term} created from Prolog syntax read from this object's {@link CharacterParser} or {@code null}
    * if the end of the underlying stream being parsed has been reached
    * @throws ParserException if an error parsing the Prolog syntax occurs
    */
   public Term parseSentence() {
      final Term t = parseTerm();
      if (t == null) {
         return null;
      }

      Token trailingToken = popValue();
      if (!isSentenceTerminator(trailingToken)) {
         throw newParserException("Expected . after: " + t + " but got: " + trailingToken);
      }

      return t;
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
      if (parser.hasNext()) {
         resetState();
         Token t = getToken(Integer.MAX_VALUE);
         return toTerm(t);
      } else {
         return null;
      }
   }

   private void resetState() {
      parsedInfixTokens.clear();
      variables.clear();
      bracketedTokens.clear();
   }

   /**
    * Returns collection of {@link Variable} instances created by this {@code SentenceParser}.
    * <p>
    * Returns all {@link Variable}s created by this {@code SentenceParser} either since it was created or since the last
    * execution of {@link #parseTerm()}.
    *
    * @return collection of {@link Variable} instances created by this {@code SentenceParser}
    */
   @SuppressWarnings("unchecked")
   public Map<String, Variable> getParsedTermVariables() {
      return (Map<String, Variable>) variables.clone();
   }

   /**
    * Creates a {@link Token} from Prolog syntax read from this object's {@link CharacterParser}.
    */
   private Token getToken(int maxLevel) {
      final Token firstArg = getPossiblePrefixArgument(maxLevel);
      if (parser.hasNext()) {
         return getToken(firstArg, maxLevel, maxLevel, true);
      } else {
         return firstArg;
      }
   }

   /**
    * Recursively called to combine individual tokens into a composite token.
    * <p>
    * While the parsing of the individual tokens is performed, priority of the operands they represent needs to be
    * considered to make sure the tokens are ordered correctly (due to different operand precedence it is not always the
    * case that the tokens will be ordered in the resulting composite Token in the same order they were parsed from the
    * input stream).
    *
    * @param currentToken represents the current state of the process to parse a complete token
    * @param currentLevel the current priority/precedence/level of tokens being parsed - if an operand represented by a
    * Token retrieved by this method has a higher priority then reordering needs to take place to position the Token in
    * the right position in relation to the other tokens that exist within the {@code currentToken} structure (in order
    * to maintain the correct priority)
    * @param maxLevel the maximum priority/precedence/level of operands to parse - if an operand represented by the next
    * Token retrieved by this method has a higher priority then it is ignored for now ({@code currentToken} is returned
    * "as-is").
    * @param {@code true} if this method is being called by another method, {@code false} if it is being called
    * recursively by itself.
    */
   private Token getToken(final Token currentToken, final int currentLevel, final int maxLevel, final boolean isFirst) {
      final Token nextToken = popValue();
      final String next = nextToken.getName();
      if (operands.postfix(next) && operands.getPostfixPriority(next) <= currentLevel) {
         Token postfixToken = addPostfixOperand(next, currentToken);
         return getToken(postfixToken, currentLevel, maxLevel, false);
      } else if (!operands.infix(next)) {
         // could be '.' if end of sentence
         // or ',', '|', ']' or ')' if parsing list or predicate
         // or could be an error
         parser.rewind(nextToken);
         return currentToken;
      }

      final int level = operands.getInfixPriority(next);
      if (level > maxLevel) {
         parser.rewind(nextToken);
         return currentToken;
      }

      final Token secondArg = getPossiblePrefixArgument(level);

      if (isFirst) {
         final Token t = createStructure(next, new Token[] {currentToken, secondArg});
         return getToken(t, level, maxLevel, false);
      } else if (level < currentLevel) {
         // compare previous.getArgument(1) to level -
         // keep going until find right level to add this Token to
         Token t = currentToken;
         while (isParsedInfixToken(t.getArgument(1)) && getInfixLevel(t.getArgument(1)) > level) {
            if (bracketedTokens.contains(t.getArgument(1))) {
               break;
            }
            t = t.getArgument(1);
         }
         Token predicate = createStructure(next, new Token[] {t.getArgument(1), secondArg});
         parsedInfixTokens.add(predicate);
         t.setArgument(1, predicate);
         return getToken(currentToken, currentLevel, maxLevel, false);
      } else {
         if (level == currentLevel) {
            if (operands.xfx(next)) {
               throw newParserException("Operand " + next + " has same precedence level as preceding operand: " + currentToken);
            }
         }
         Token predicate = createStructure(next, new Token[] {currentToken, secondArg});
         parsedInfixTokens.add(predicate);
         return getToken(predicate, level, maxLevel, false);
      }
   }

   private Token createStructure(String name, Token[] tokens) {
      return new Token(name, TokenType.STRUCTURE, tokens);
   }

   /**
    * Parses and returns a {@code Token}.
    * <p>
    * If the parsed {@code Token} represents a prefix operand, then the subsequent Token is also parsed so it can be
    * used as an argument in the returned structure.
    *
    * @param currentLevel the current priority level of tokens being parsed (if the parsed Token represents a prefix
    * operand, then the operand cannot have a higher priority than {@code currentLevel} (a {@code ParserException} will
    * be thrown if does).
    */
   private Token getPossiblePrefixArgument(int currentLevel) {
      final Token token = popValue();
      final String value = token.getName();
      if (operands.prefix(value) && parser.isFollowedByTerm()) {
         if (value.equals(MINUS_SIGN) && isFollowedByNumber()) {
            return getNegativeNumber();
         }

         int prefixLevel = operands.getPrefixPriority(value);
         if (prefixLevel > currentLevel) {
            throw newParserException("Invalid prefix: " + value + " level: " + prefixLevel + " greater than current level: " + currentLevel);
         }

         // The difference between "fy" and "fx" associativity is that a "y" means that the argument
         // can contain operators of <i>the same</i> or lower level of priority
         // while a "x" means that the argument can <i>only</i> contain operators of a lower priority.
         if (operands.fx(value)) {
            // -1 to only parse tokens of a lower priority than the current prefix operator.
            prefixLevel--;
         }

         Token argument = getToken(prefixLevel);
         return createPrefixToken(value, argument);
      } else {
         parser.rewind(token);
         return getDiscreteToken();
      }
   }

   private Token getNegativeNumber() {
      final Token token = popValue();
      final String value = "-" + token.getName();
      return new Token(value, token.getType());
   }

   /**
    * Returns a new {@code Token} representing the specified prefix operand and argument.
    */
   private Token createPrefixToken(String prefixOperandName, Token argument) {
      return createStructure(prefixOperandName, new Token[] {argument});
   }

   /**
    * Add a token, representing a post-fix operand, in the appropriate point of a composite token.
    * <p>
    * The correct position of the post-fix operand within the composite Token (and so what the post-fix operands actual
    * argument will be) is determined by operand priority.
    *
    * @param original a composite Token representing the current state of parsing the current sentence
    * @param postfixOperand a Token which represents a post-fix operand
    */
   private Token addPostfixOperand(String postfixOperand, Token original) {
      int level = operands.getPostfixPriority(postfixOperand);
      if (original.getNumberOfArguments() == 2) {
         boolean higherLevelInfixOperand = operands.infix(original.getName()) && getInfixLevel(original) > level;
         if (higherLevelInfixOperand) {
            String name = original.getName();
            Token firstArg = original.getArgument(0);
            Token newSecondArg = addPostfixOperand(postfixOperand, original.getArgument(1));
            return createStructure(name, new Token[] {firstArg, newSecondArg});
         }
      } else if (original.getNumberOfArguments() == 1) {
         if (operands.prefix(original.getName())) {
            if (getPrefixLevel(original) > level) {
               String name = original.getName();
               Token newFirstArg = addPostfixOperand(postfixOperand, original.getArgument(0));
               return createStructure(name, new Token[] {newFirstArg});
            }
         } else if (operands.postfix(original.getName())) {
            int levelToCompareTo = getPostfixLevel(original);
            // "x" in "xf" means that the argument can <i>only</i> contain operators of a lower priority.
            if (levelToCompareTo > level || (operands.xf(postfixOperand) && levelToCompareTo == level)) {
               throw newParserException("Invalid postfix: " + postfixOperand + " " + level + " and term: " + original + " " + levelToCompareTo);
            }
         }
      }
      return createStructure(postfixOperand, new Token[] {original});
   }

   private Token getDiscreteToken() {
      final Token token = popValue();
      if (isListOpenBracket(token)) {
         return parseList();
      } else if (isPredicateOpenBracket(token)) {
         return getTokenInBrackets();
      } else if (token.getType() == TokenType.SYMBOL || token.getType() == TokenType.ATOM) {
         return getAtomOrStructure(token.getName());
      } else {
         return token;
      }
   }

   private Term toTerm(Token token) {
      switch (token.getType()) {
         case ATOM:
            return new Atom(token.getName());
         case INTEGER:
            return IntegerNumberCache.valueOf(parseLong(token.getName()));
         case FLOAT:
            return new DecimalFraction(parseDouble(token.getName()));
         case VARIABLE:
            return getVariable(token.getName());
         case STRUCTURE:
            Term[] args = new Term[token.getNumberOfArguments()];
            for (int i = 0; i < args.length; i++) {
               args[i] = toTerm(token.getArgument(i));
            }
            return Structure.createStructure(token.getName(), args);
         case EMPTY_LIST:
            return EmptyList.EMPTY_LIST;
         default:
            throw new RuntimeException("Unexpected token type: " + token.getType() + " with value: " + token);
      }
   }

   /**
    * Returns either an {@code Atom} or {@code Structure} with the specified name.
    * <p>
    * If the next character read from the parser is a {@code (} then a newly created {@code Structure} is returned else
    * a newly created {@code Atom} is returned.
    */
   private Token getAtomOrStructure(String name) {
      Token token = parser.hasNext() ? peekValue() : null;
      if (isPredicateOpenBracket(token)) {
         popValue(); //skip opening bracket
         if (isPredicateCloseBracket(peekValue())) {
            throw newParserException("No arguments specified for structure: " + name);
         }

         ArrayList<Token> args = new ArrayList<>();

         Token t = getCommaSeparatedArgument();
         args.add(t);

         do {
            token = popValue();
            if (isPredicateCloseBracket(token)) {
               return createStructure(name, toArray(args));
            } else if (isArgumentSeperator(token)) {
               args.add(getCommaSeparatedArgument());
            } else {
               throw newParserException("While parsing arguments of " + name + " expected ) or , but got: " + token);
            }
         } while (true);
      } else {
         return new Token(name, TokenType.ATOM);
      }
   }

   /**
    * Returns a variable with the specified id.
    * <p>
    * If this object already has an instance of {@code Variable} with the specified id then it will be returned else a
    * new {@code Variable} will be created. The only exception to this behaviour is when the id equals
    * {@link Variable#ANONYMOUS_VARIABLE_ID} - in which case a new {@code Variable} will be always be returned.
    */
   private Variable getVariable(String id) {
      if (ANONYMOUS_VARIABLE_ID.equals(id)) {
         return new Variable();
      } else {
         return getNamedVariable(id);
      }
   }

   private Variable getNamedVariable(String id) {
      Variable v = variables.get(id);
      if (v == null) {
         v = new Variable(id);
         variables.put(id, v);
      }
      return v;
   }

   /** Returns a newly created {@code Token} representing a Prolog list with elements read from the parser. */
   private Token parseList() {
      ArrayList<Token> args = new ArrayList<>();
      Token tail = new Token(null, TokenType.EMPTY_LIST);

      while (true) {
         Token token = popValue();
         if (isListCloseBracket(token)) {
            break;
         }
         parser.rewind(token);
         Token arg = getCommaSeparatedArgument();
         args.add(arg);

         token = popValue(); // | ] or ,
         if (isListCloseBracket(token)) {
            break;
         } else if (isListTail(token)) {
            tail = getCommaSeparatedArgument();
            token = popValue();
            if (!isListCloseBracket(token)) {
               throw newParserException("Expected ] to mark end of list after tail but got: " + token);
            }
            break;
         } else if (!isArgumentSeperator(token)) {
            throw newParserException("While parsing list expected ] | or , but got: " + token);
         }
      }

      Token list = tail;
      for (int i = args.size() - 1; i > -1; i--) {
         Token element = args.get(i);
         list = createStructure(ListFactory.LIST_PREDICATE_NAME, new Token[] {element, list});
      }
      return list;
   }

   /**
    * Parses and returns the next argument of a list or structure.
    * <p>
    * As a comma would indicate a delimiter in a sequence of arguments, we only want to continue parsing up to the point
    * of any comma. i.e. Any parsed comma should not be considered as part of the argument currently being parsed.
    */
   private Token getCommaSeparatedArgument() {
      // Call getArgument with a priority/precedence/level of one less than the priority of a comma -
      // as we only want to continue parsing tokens that have a lower priority level than that.
      // The reason this is slightly complicated is because of the overloaded use of a comma in Prolog -
      // as well as acting as a delimiter in a sequence of arguments for a list or structure,
      // a comma is also a predicate in its own right (as a conjunction).
      if (operands.infix(",")) {
         return getToken(operands.getInfixPriority(",") - 1);
      } else {
         return getToken(Integer.MAX_VALUE);
      }
   }

   private Token getTokenInBrackets() {
      // As we are at the starting point for parsing a Token contained in brackets
      // (and as it being in brackets means we can parse it in isolation without
      // considering the priority of any surrounding tokens outside the brackets)
      // we call getArgument with the highest possible priority.
      Token token = getToken(Integer.MAX_VALUE);
      Token next = popValue();
      if (!isPredicateCloseBracket(next)) {
         throw newParserException("Expected ) but got: " + next + " after " + token);
      }
      bracketedTokens.add(token);
      return token;
   }

   private Token popValue() {
      return parser.next();
   }

   private Token peekValue() {
      Token token = popValue();
      parser.rewind(token);
      return token;
   }

   private boolean isFollowedByNumber() {
      Token token = popValue();
      TokenType tt = token.getType();
      parser.rewind(token);
      return tt == TokenType.INTEGER || tt == TokenType.FLOAT;
   }

   /**
    * Has the specified Token already been parsed, and included as an argument in an infix operand, as part of parsing
    * the current sentence?
    */
   private boolean isParsedInfixToken(Token t) {
      return parsedInfixTokens.contains(t);
   }

   private int getPrefixLevel(Token t) {
      return operands.getPrefixPriority(t.getName());
   }

   private int getInfixLevel(Token t) {
      return operands.getInfixPriority(t.getName());
   }

   private int getPostfixLevel(Token t) {
      return operands.getPostfixPriority(t.getName());
   }

   private Token[] toArray(ArrayList<Token> al) {
      return al.toArray(new Token[al.size()]);
   }

   /** Returns a new {@link ParserException} with the specified message. */
   private ParserException newParserException(String message) {
      return parser.newParserException(message);
   }
}
