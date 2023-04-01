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
import java.util.function.Function;

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
   private static final int COMMA_PRIORITY = 1000;
   private static final Token EMPTY_LIST_TOKEN = new Token(null, TokenType.EMPTY_LIST);
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
         // need to check if '.' is followed by a '(' as don't want to terminate when encounter '.(1,2)'
         Token token = parseToken(false, t -> Delimiters.isSentenceTerminator(t) && !parser.isImmediatelyFollowedByBracket(true));
         return toTerm(token);
      } else {
         return null;
      }
   }

   private Token parseToken(boolean rewindOnTermination, Function<Token, Boolean> terminationCriteria) {
      List<Token> tokens = parseTokens(rewindOnTermination, terminationCriteria);
      return toSingleToken(tokens.toArray(new Token[tokens.size()]), 0, tokens.size(), Integer.MAX_VALUE, false);
   }

   private List<Token> parseTokens(boolean rewindOnTermination, Function<Token, Boolean> terminationCriteria) {
      List<Token> parsedTokens = new ArrayList<>();

      while (true) {
         Token next = parser.next();

         if (terminationCriteria.apply(next)) {
            if (rewindOnTermination) {
               parser.rewind(next);
            }
            return parsedTokens;
         } else if (Delimiters.isListOpenBracket(next)) {
            parsedTokens.add(parseList());
         } else if (Delimiters.isPredicateOpenBracket(next)) {
            Token bracketedTerm = parseToken(false, Delimiters::isPredicateCloseBracket);
            parsedTokens.add(new Token(null, TokenType.UNNAMED_BRACKET, new Token[] {bracketedTerm}));
         } else if (parser.isImmediatelyFollowedByBracket(false)) {
            parsedTokens.add(parsePredicate(next));
         } else {
            parsedTokens.add(next);
         }
      }
   }

   private Token parseList() {
      Token emptyListCheck = parser.next();
      if (Delimiters.isListCloseBracket(emptyListCheck)) {
         return EMPTY_LIST_TOKEN;
      }
      parser.rewind(emptyListCheck);

      List<Token> args = new ArrayList<>();

      while (true) {
         List<Token> elementTokens = parseTokens(true, t -> t.getType() == TokenType.SYMBOL && (",".equals(t.getName()) || "|".equals(t.getName()) || "]".equals(t.getName())));
         args.add(toSingleToken(elementTokens.toArray(new Token[elementTokens.size()]), 0, elementTokens.size(), COMMA_PRIORITY, true));

         Token delimiter = parser.next();

         if (Delimiters.isListCloseBracket(delimiter)) {
            args.add(EMPTY_LIST_TOKEN);
            return new Token(".", TokenType.LIST, args.toArray(new Token[args.size()]));
         }

         if (Delimiters.isListTail(delimiter)) {
            List<Token> tailTokens = parseTokens(false, Delimiters::isListCloseBracket);
            args.add(toSingleToken(tailTokens.toArray(new Token[tailTokens.size()]), 0, tailTokens.size(), COMMA_PRIORITY, true));
            return new Token(".", TokenType.LIST, args.toArray(new Token[args.size()]));
         }
      }
   }

   private Token parsePredicate(Token next) {
      List<Token> args = parseTokens(false, Delimiters::isPredicateCloseBracket);
      return new Token(next.getName(), TokenType.NAMED_BRACKET, args.toArray(new Token[args.size()]));
   }

   private Token toSingleToken(Token[] tokens, int startIdx, int endIdx, int previousPriority, boolean faiIfPriorityEqual) {
      if (endIdx <= startIdx) {
         throw newParserException("No arguments to parse");
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
         StringBuilder sb = new StringBuilder();
         for (int i = startIdx; i < endIdx; i++) {
            if (i != startIdx) {
               sb.append(' ');
            }
            sb.append(tokens[i].getName());
         }
         throw newParserException("No suitable operands found in: " + sb);
      }

      Token operandToken = tokens[maxPriorityIdx];
      if (maxPriority > previousPriority
          || (maxPriority == previousPriority && (faiIfPriorityEqual || (maxPriorityIdx > startIdx && maxPriorityIdx < endIdx - 1 && operands.xfx(operandToken.getName()))))) {
         throw newParserException("Operator priority clash. " + operandToken.getName() + " (" + maxPriority + ") conflicts with previous priority (" + previousPriority + ")");
      }

      if (operandToken.getType() == TokenType.NAMED_BRACKET) {
         // e.g.: a+(b+c)
         System.out.println(operandToken + " " + startIdx + " " + maxPriorityIdx);
         Token leftArg = toSingleToken(tokens, startIdx, maxPriorityIdx, maxPriority, !operands.yfx(operandToken.getName()));
         tokens[maxPriorityIdx] = toSingleToken(operandToken.getArguments(), 0, operandToken.getArguments().length, Integer.MAX_VALUE, false);
         Token rightArg = toSingleToken(tokens, maxPriorityIdx, endIdx, maxPriority, !operands.xfy(operandToken.getName()));
         return new Token(operandToken.getName(), TokenType.OPERAND_AND_ARGUMENTS, new Token[] {leftArg, rightArg});
      } else if (maxPriorityIdx == startIdx) {
         // prefix
         Token arg = toSingleToken(tokens, startIdx + 1, endIdx, maxPriority, operands.fx(operandToken.getName()));
         return new Token(operandToken.getName(), TokenType.OPERAND_AND_ARGUMENTS, new Token[] {arg});
      } else if (maxPriorityIdx == endIdx - 1) {
         // postfix
         Token arg = toSingleToken(tokens, startIdx, endIdx - 1, maxPriority, operands.xf(operandToken.getName()));
         return new Token(operandToken.getName(), TokenType.OPERAND_AND_ARGUMENTS, new Token[] {arg});
      } else {
         // infix
         Token leftArg = toSingleToken(tokens, startIdx, maxPriorityIdx, maxPriority, !operands.yfx(operandToken.getName()));
         Token rightArg = toSingleToken(tokens, maxPriorityIdx + 1, endIdx, maxPriority, !operands.xfy(operandToken.getName()));
         return new Token(operandToken.getName(), TokenType.OPERAND_AND_ARGUMENTS, new Token[] {leftArg, rightArg});
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
            return toIntegerNumber(token.getName());
         case FLOAT:
            return toDecimalFraction(token.getName());
         case EMPTY_LIST:
            return EmptyList.EMPTY_LIST;
         case VARIABLE:
            return getOrCreateVariable(token.getName());
         default:
            throw new IllegalArgumentException(token.getType() + " " + token);
      }
   }

   private Term toStructureFromNamedBracket(Token token) {
      // convert -(1) or -(1.0) to numbers rather than structures
      if ("-".equals(token.getName()) && token.getNumberOfArguments() == 1) {
         Token arg = token.getArgument(0);
         if (arg.getType() == TokenType.INTEGER) {
            return toIntegerNumber("-" + arg.getName());
         }
         if (arg.getType() == TokenType.FLOAT) {
            return toDecimalFraction("-" + arg.getName());
         }
      }

      Token[] input = token.getArguments();

      List<Token> tokens = new ArrayList<>();
      int start = 0;
      for (int i = 0; i < input.length; i++) {
         Token next = input[i];
         if (Delimiters.isArgumentSeperator(next)) {
            Token singleToken = toSingleToken(input, start, i, COMMA_PRIORITY, true);
            tokens.add(singleToken);
            start = i + 1;
         } else if (",".equals(next.getName()) && next.getType() == TokenType.NAMED_BRACKET) {
            if (start != i) {
               Token singleToken = toSingleToken(input, start, i, COMMA_PRIORITY, true);
               tokens.add(singleToken);
            }
            input[i] = toSingleToken(next.getArguments(), 0, next.getArguments().length, Integer.MAX_VALUE, false);
            start = i;
         }
      }
      tokens.add(toSingleToken(input, start, input.length, COMMA_PRIORITY, true));

      Term[] args = new Term[tokens.size()];
      for (int i = 0; i < args.length; i++) {
         args[i] = toTerm(tokens.get(i));
      }
      return Structure.createStructure(token.getName(), args);
   }

   private Term toStructureFromOperandAndArguments(Token token) {
      // convert -(1) or -(1.0) to numbers rather than structures
      if ("-".equals(token.getName()) && token.getNumberOfArguments() == 1) {
         Token arg = token.getArgument(0);
         if (arg.getType() == TokenType.INTEGER) {
            return toIntegerNumber("-" + arg.getName());
         }
         if (arg.getType() == TokenType.FLOAT) {
            return toDecimalFraction("-" + arg.getName());
         }
      }

      Term[] args = new Term[token.getNumberOfArguments()];
      for (int i = 0; i < args.length; i++) {
         args[i] = toTerm(token.getArgument(i));
      }
      return Structure.createStructure(token.getName(), args);
   }

   private Term toList(Token token) {
      Term[] elements = new Term[token.getNumberOfArguments() - 1];
      for (int i = 0; i < elements.length; i++) {
         elements[i] = toTerm(token.getArgument(i));
      }
      Term tail = toTerm(token.getArgument(elements.length));
      return ListFactory.createList(elements, tail);
   }

   private IntegerNumber toIntegerNumber(String value) {
      return IntegerNumberCache.valueOf(Long.parseLong(value));
   }

   private DecimalFraction toDecimalFraction(String value) {
      return new DecimalFraction(Double.parseDouble(value));
   }

   private Variable getOrCreateVariable(String id) {
      if (ANONYMOUS_VARIABLE_ID.equals(id)) {
         return new Variable();
      } else {
         return variables.computeIfAbsent(id, name -> new Variable(name));
      }
   }

   /** Returns a new {@link ParserException} with the specified message. */
   private ParserException newParserException(String message) {
      return parser.newParserException(message);
   }
}
