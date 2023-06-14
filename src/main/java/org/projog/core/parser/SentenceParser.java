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

import static org.projog.core.term.Variable.ANONYMOUS_VARIABLE_ID;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
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
   private static final int DEFAULT_TOKEN_ARRAY_LENGTH = 32;
   private static final int COMMA_PRIORITY = 1000;
   private static final Token EMPTY_LIST_TOKEN = new Token((String) null, TokenType.EMPTY_LIST, new Token[0]);
   private static final Terminator SENTENCE_TERMINATOR = new Terminator() {
      @Override
      public boolean terminate(Token t, TokenParser parser) {
         // need to check if '.' is followed by a '(' as don't want to terminate when encounter '.(1,2)'
         return Delimiters.isSentenceTerminator(t) && !parser.isImmediatelyFollowedByBracket(true);
      }

      @Override
      public boolean rewindOnTermination() {
         return false;
      }

      @Override
      public int maxPriority() {
         return Integer.MAX_VALUE;
      }

      @Override
      public boolean faiIfPriorityEqual() {
         return false;
      }

      @Override
      public String message() {
         return "No . to indicate end of sentence";
      }
   };
   private static final Terminator LIST_TERMINATOR = new Terminator() {
      @Override
      public boolean terminate(Token t, TokenParser parser) {
         return t.getType() == TokenType.SYMBOL && (",".equals(t.getName()) || "|".equals(t.getName()) || "]".equals(t.getName()));
      }

      @Override
      public boolean rewindOnTermination() {
         return true;
      }

      @Override
      public int maxPriority() {
         return COMMA_PRIORITY;
      }

      @Override
      public boolean faiIfPriorityEqual() {
         return true;
      }

      @Override
      public String message() {
         return "No matching ] for [";
      }
   };
   private static final Terminator BRACKET_TERMINATOR = new Terminator() {
      @Override
      public boolean terminate(Token t, TokenParser parser) {
         return Delimiters.isPredicateCloseBracket(t);
      }

      @Override
      public boolean rewindOnTermination() {
         return false;
      }

      @Override
      public int maxPriority() {
         return Integer.MAX_VALUE;
      }

      @Override
      public boolean faiIfPriorityEqual() {
         return false;
      }

      @Override
      public String message() {
         return "No matching ) for (";
      }
   };

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
    * Returns collection of {@link Variable} instances created by this {@code SentenceParser}.
    * <p>
    * Returns all {@link Variable}s created by this {@code SentenceParser} either since it was created or since the last
    * execution of {@link #parseSentence()}.
    *
    * @return collection of {@link Variable} instances created by this {@code SentenceParser}
    */
   @SuppressWarnings("unchecked")
   public Map<String, Variable> getParsedTermVariables() {
      return (Map<String, Variable>) variables.clone();
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
      if (parser.hasNext()) {
         variables.clear();
         Token first = parser.next();
         parser.rewind(first);
         Token token = parseToken(first, SENTENCE_TERMINATOR);
         return toTerm(token);
      } else {
         return null;
      }
   }

   private Token parseToken(Token previous, Terminator terminator) {
      if (!parser.hasNext()) {
         throw newParserException(previous, terminator.message());
      }
      Token first = parser.next();
      if (terminator.terminate(first, parser)) {
         throw newParserException(first, "Expected term before " + first);
      }
      first = parseToken(first);

      if (!parser.hasNext()) {
         throw newParserException(previous, terminator.message());
      }
      Token second = parser.next();
      if (terminator.terminate(second, parser)) {
         if (terminator.rewindOnTermination()) {
            parser.rewind(second);
         }
         return first;
      }
      second = parseToken(second);

      Token[] tokens = new Token[DEFAULT_TOKEN_ARRAY_LENGTH];
      tokens[0] = first;
      tokens[1] = second;
      int idx = 1;
      while (true) {
         if (!parser.hasNext()) {
            throw newParserException(previous, terminator.message());
         }

         Token next = parser.next();
         if (terminator.terminate(next, parser)) {
            if (terminator.rewindOnTermination()) {
               parser.rewind(next);
            }
            return toSingleToken(tokens, 0, idx + 1, terminator.maxPriority(), terminator.faiIfPriorityEqual());
         } else {
            if (++idx == tokens.length) {
               Token copy[] = new Token[tokens.length + DEFAULT_TOKEN_ARRAY_LENGTH];
               System.arraycopy(tokens, 0, copy, 0, tokens.length);
               tokens = copy;
            }
            tokens[idx] = parseToken(next);
         }
      }
   }

   private Token parseToken(Token next) {
      if (Delimiters.isListOpenBracket(next)) {
         return parseList(next);
      } else if (Delimiters.isPredicateOpenBracket(next)) {
         Token bracketedTerm = parseToken(next, BRACKET_TERMINATOR);
         return new Token(next, TokenType.UNNAMED_BRACKET, new Token[] {bracketedTerm});
      } else if (parser.isImmediatelyFollowedByBracket(false)) {
         return parsePredicate(next);
      } else {
         return next;
      }
   }

   private Token parseList(Token openBracket) {
      Token emptyListCheck = parser.next();
      if (Delimiters.isListCloseBracket(emptyListCheck)) {
         return EMPTY_LIST_TOKEN;
      }
      parser.rewind(emptyListCheck);

      List<Token> args = new ArrayList<>();

      while (true) {
         Token elementToken = parseToken(openBracket, LIST_TERMINATOR);
         args.add(elementToken);

         Token delimiter = parser.next();

         if (Delimiters.isListCloseBracket(delimiter)) {
            args.add(EMPTY_LIST_TOKEN);
            return new Token((String) null, TokenType.LIST, args.toArray(new Token[args.size()]));
         }

         if (Delimiters.isListTail(delimiter)) {
            Token tail = parseToken(openBracket, LIST_TERMINATOR);
            Token end = parser.next();
            if (!Delimiters.isListCloseBracket(end)) {
               throw newParserException(end, "Expected ]");
            }

            args.add(tail);
            return new Token((String) null, TokenType.LIST, args.toArray(new Token[args.size()]));
         }
      }
   }

   private Token parsePredicate(Token name) {
      List<Token> args = new ArrayList<>();

      while (true) {
         if (!parser.hasNext()) {
            throw newParserException(name, BRACKET_TERMINATOR.message());
         }

         Token next = parser.next();

         if (Delimiters.isPredicateCloseBracket(next)) {
            return new Token(name, TokenType.NAMED_BRACKET, args.toArray(new Token[args.size()]));
         }

         args.add(parseToken(next));
      }
   }

   private Token toSingleToken(Token[] tokens, int startIdx, int endIdx, int previousPriority, boolean faiIfPriorityEqual) {
      if (endIdx <= startIdx) {
         throw new RuntimeException("No arguments to parse");
      }
      if (startIdx == endIdx - 1) {
         return tokens[startIdx];
      }

      int maxPriority = -1;
      int maxPriorityIdx = startIdx;

      // find the token which represents an operand with the highest priority
      for (int i = startIdx; i < endIdx; i++) {
         Token next = tokens[i];

         if (next.getType().isPossibleOperand()) {
            int priority = -1;

            if ("-".equals(next.getName()) && maxPriorityIdx > startIdx && maxPriorityIdx == i - 1) {
               // e.g. 1 + -2.
            } else if (i == startIdx) {
               if (operands.prefix(next.getName())) {
                  priority = operands.getPrefixPriority(next.getName());
               }
            } else if (i == endIdx - 1) {
               if (operands.postfix(next.getName())) {
                  priority = operands.getPostfixPriority(next.getName());
               }
            } else if (operands.infix(next.getName())) {
               priority = operands.getInfixPriority(next.getName());
            }

            if (priority > maxPriority
                || (priority == maxPriority
                    && ((i > startIdx
                         && i < endIdx - 1
                         && ((operands.yfx(next.getName()) || (operands.xfx(next.getName())) && (maxPriorityIdx != startIdx || !operands.fx(tokens[startIdx].getName())))))
                        || (i == endIdx - 1 && operands.yf(next.getName()))))) {
               maxPriority = priority;
               maxPriorityIdx = i;
            }
         } else if (i > startIdx && next.getType() == TokenType.NAMED_BRACKET && operands.infix(next.getName())) {
            // e.g.: a+(b+c)
            int priority = operands.getInfixPriority(next.getName());
            if (priority > maxPriority || (priority == maxPriority && (operands.yfx(next.getName()) && (maxPriorityIdx != startIdx || !operands.fx(tokens[startIdx].getName()))))) {
               maxPriority = priority;
               maxPriorityIdx = i;
            }
         }
      }

      if (maxPriority == -1) {
         throw newParserException(tokens[endIdx - 1], "No suitable operands");
      }

      Token operandToken = tokens[maxPriorityIdx];
      if (maxPriority > previousPriority
          || (maxPriority == previousPriority && (faiIfPriorityEqual || (maxPriorityIdx > startIdx && maxPriorityIdx < endIdx - 1 && operands.xfx(operandToken.getName()))))) {
         throw newParserException(operandToken,
                     "Operator priority clash. " + operandToken.getName() + " (" + maxPriority + ") conflicts with previous priority (" + previousPriority + ")");
      }

      if (operandToken.getType() == TokenType.NAMED_BRACKET) {
         // e.g.: a+(b+c)
         Token leftArg = toSingleToken(tokens, startIdx, maxPriorityIdx, maxPriority, !operands.yfx(operandToken.getName()));
         tokens[maxPriorityIdx] = toSingleToken(operandToken.getArguments(), 0, operandToken.getArguments().length, Integer.MAX_VALUE, false);
         Token rightArg = toSingleToken(tokens, maxPriorityIdx, endIdx, maxPriority, !operands.xfy(operandToken.getName()));
         return new Token(operandToken, TokenType.OPERAND_AND_ARGUMENTS, new Token[] {leftArg, rightArg});
      } else if (maxPriorityIdx == startIdx) {
         // prefix
         Token arg = toSingleToken(tokens, startIdx + 1, endIdx, maxPriority, operands.fx(operandToken.getName()));
         return new Token(operandToken, TokenType.OPERAND_AND_ARGUMENTS, new Token[] {arg});
      } else if (maxPriorityIdx == endIdx - 1) {
         // postfix
         Token arg = toSingleToken(tokens, startIdx, endIdx - 1, maxPriority, operands.xf(operandToken.getName()));
         return new Token(operandToken, TokenType.OPERAND_AND_ARGUMENTS, new Token[] {arg});
      } else {
         // infix
         Token leftArg = toSingleToken(tokens, startIdx, maxPriorityIdx, maxPriority, !operands.yfx(operandToken.getName()));
         Token rightArg = toSingleToken(tokens, maxPriorityIdx + 1, endIdx, maxPriority, !operands.xfy(operandToken.getName()));
         return new Token(operandToken, TokenType.OPERAND_AND_ARGUMENTS, new Token[] {leftArg, rightArg});
      }
   }

   private Term toTerm(Token token) {
      switch (token.getType()) {
         case UNNAMED_BRACKET:
            return toTerm(token.getArgument(0));
         case ATOM:
         case SYMBOL:
            return new Atom(token.getName());
         case NAMED_BRACKET:
            return toStructureFromNamedBracket(token);
         case OPERAND_AND_ARGUMENTS:
            return toStructureFromOperandAndArguments(token);
         case LIST:
            return toList(token);
         case INTEGER:
            return toIntegerNumber(token, token.getName());
         case FLOAT:
            return toDecimalFraction(token, token.getName());
         case EMPTY_LIST:
            return EmptyList.EMPTY_LIST;
         case VARIABLE:
            return getOrCreateVariable(token.getName());
         default:
            throw new IllegalArgumentException(token.getType() + " " + token);
      }
   }

   private Term toStructureFromNamedBracket(Token token) {
      if (isNegativeNumber(token)) {
         return toNegativeNumber(token);
      }

      Token[] input = token.getArguments();

      List<Token> tokens = new ArrayList<>();
      int start = 0;
      boolean previousWasDelimiter = true;
      for (int i = 0; i < input.length; i++) {
         Token next = input[i];
         if (Delimiters.isArgumentSeperator(next)) {
            if (i <= start) {
               throw newParserException(next, "Expected argument but found " + next.getName());
            }
            Token singleToken = toSingleToken(input, start, i, COMMA_PRIORITY, true);
            tokens.add(singleToken);
            start = i + 1;
            previousWasDelimiter = true;
         } else if (!previousWasDelimiter && ",".equals(next.getName()) && next.getType() == TokenType.NAMED_BRACKET) {
            if (start != i) {
               Token singleToken = toSingleToken(input, start, i, COMMA_PRIORITY, true);
               tokens.add(singleToken);
            }
            input[i] = toSingleToken(next.getArguments(), 0, next.getNumberOfArguments(), Integer.MAX_VALUE, false);
            start = i;

            previousWasDelimiter = false;
         } else {
            previousWasDelimiter = false;
         }
      }
      if (input.length <= start) {
         throw newParserException(input.length == 0 ? token : input[input.length - 1], "No arguments to parse");
      }
      tokens.add(toSingleToken(input, start, input.length, COMMA_PRIORITY, true));

      Term[] args = new Term[tokens.size()];
      for (int i = 0; i < args.length; i++) {
         args[i] = toTerm(tokens.get(i));
      }
      return Structure.createStructure(token.getName(), args);
   }

   private Term toStructureFromOperandAndArguments(Token token) {
      if (isNegativeNumber(token)) {
         return toNegativeNumber(token);
      }

      Term[] args = new Term[token.getNumberOfArguments()];
      for (int i = 0; i < args.length; i++) {
         args[i] = toTerm(token.getArgument(i));
      }
      return Structure.createStructure(token.getName(), args);
   }

   private boolean isNegativeNumber(Token t) {
      return t.getNumberOfArguments() == 1 && "-".equals(t.getName()) && t.getArgument(0).getType().isNumber();
   }

   private Term toNegativeNumber(Token t) {
      // convert -(1) or -(1.0) to numbers rather than structures
      Token arg = t.getArgument(0);
      if (arg.getType() == TokenType.INTEGER) {
         return toIntegerNumber(t, "-" + arg.getName());
      } else {
         return toDecimalFraction(t, "-" + arg.getName());
      }
   }

   private Term toList(Token token) {
      Term[] elements = new Term[token.getNumberOfArguments() - 1];
      for (int i = 0; i < elements.length; i++) {
         elements[i] = toTerm(token.getArgument(i));
      }
      Term tail = toTerm(token.getArgument(elements.length));
      return ListFactory.createList(elements, tail);
   }

   private IntegerNumber toIntegerNumber(Token t, String value) {
      try {
         return IntegerNumberCache.valueOf(Long.parseLong(value));
      } catch (NumberFormatException e) {
         throw newParserException(t, "Invalid numeric value: " + value);
      }
   }

   private DecimalFraction toDecimalFraction(Token t, String value) {
      try {
         return new DecimalFraction(Double.parseDouble(value));
      } catch (NumberFormatException e) {
         throw newParserException(t, "Invalid numeric value: " + value);
      }
   }

   private Variable getOrCreateVariable(String id) {
      if (ANONYMOUS_VARIABLE_ID.equals(id)) {
         return new Variable();
      } else {
         return variables.computeIfAbsent(id, name -> new Variable(name));
      }
   }

   /** Returns a new {@link ParserException} with the specified message. */
   private static ParserException newParserException(Token token, String message) {
      throw new ParserException(message, token);
   }

   private static interface Terminator {
      boolean terminate(Token token, TokenParser parser);

      boolean rewindOnTermination();

      int maxPriority();

      boolean faiIfPriorityEqual();

      String message();
   }
}
