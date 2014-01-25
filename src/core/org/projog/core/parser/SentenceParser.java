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

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.projog.core.Operands;
import org.projog.core.term.DoubleNumber;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Parses Prolog syntax representing rules including operators.
 * <p>
 * <b>Note:</b> not thread safe.
 * </p>
 * 
 * @see Operands
 */
public final class SentenceParser extends TermParser {
   // TODO some of these methods are too big - refactor
   // A reason for the complexity of the required functionality is the need to order,
   // based on the priority of the operands they represent, terms that exist with-in larger compound terms.
   // (Rather than ordering simply on the order in which they are parsed.)
   // Is it possible to make these clearer?
   // (Improve code structure to make meaning more explicit? More/better comments?
   // Link to external documentation that describes the specifics of Prolog syntax and operand precedence?)

   private final Set<Term> parsedInfixTerms = new HashSet<>();
   private Term lastParsedTerm;

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
      CharacterParser p = new CharacterParser(br);
      return new SentenceParser(p, operands);
   }

   private SentenceParser(CharacterParser parser, Operands operands) {
      super(parser, operands);
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
      super.clearSharedVariables();
      parsedInfixTerms.clear();

      Term t = getTerm(Integer.MAX_VALUE, false);
      if (t == null) {
         // reached end of stream
         return null;
      }
      Term next = get();
      while (isPostfix(next)) {
         t = addPostfixOperand(t, next);
         next = get();
      }
      if (!isPeriod(next)) {
         throwParserException("Expected . but got: " + next + " after: " + t);
      }

      return t;
   }

   /**
    * Parses and returns the next argument of a list or structure.
    * <p>
    * As a comma would indicate a delimiter in a sequence of arguments, we only want to continue parsing up to the point
    * of any comma. i.e. Any parsed comma should not be considered as part of the argument currently being parsed.
    */
   @Override
   protected Term getCommaSeparatedArgument() {
      // The reason we call getArgument with a priority/precedence/level
      // of 999 is because the priority of a comma is 1000 - so we only want 
      // to continue parsing terms that have a lower priority level than that.
      // TODO find a safer and clearer way of doing this than using a hard-coded value.
      // (Explicitly find out the priority of a comma at runtime and then store 
      // the value (minus 1) in a suitably named variable?)
      // The reason this is slightly complicated is because of the overloaded use of a comma in Prolog -  
      // as well as acting as a delimiter in a sequence of arguments for a list or structure,
      // a comma is also a predicate in it's own right (as a conjunction).
      return getArgument(999);
   }

   @Override
   protected Term getTermInBrackets() {
      // As we are at the starting point for parsing a term contained in brackets
      // (and as it being in brackets means we can parse it in isolation without 
      // considering the priority of any surrounding terms outside the brackets)
      // we call getArgument with the highest possible priority.
      return getArgument(Integer.MAX_VALUE);
   }

   /**
    * Returns a term constructed from syntax read up until the next delimiter character.
    * 
    * @param level the maximum operand priority/precedence/level of terms to retrieve - stops parsing if it parses an
    * term which represents an operand with a higher priority than the specified level.
    * @return an argument of a list or structure
    */
   private Term getArgument(int level) {
      Term t = getTerm(level, true);
      // Push-back the delimiter character that caused parsing to terminate 
      // (and is not included as part of the returned term) so it is not lost.
      parser.rewind();
      lastParsedTerm = null;
      return t;
   }

   /**
    * Creates a {@link Term} from Prolog syntax read from this object's {@link CharacterParser}.
    * <p>
    * The behaviour when there is no term to return, because the end of the underlying stream been reached, depends on
    * the value of the {@code throwExceptionRatherThanReturnNull} parameter - if {@code true} a {@code ParserException}
    * will be thrown else {@code null} will be returned.
    */
   private Term getTerm(int maxLevel, boolean throwExceptionRatherThanReturnNull) {
      Term firstArg = getPossiblePrefixArgument(maxLevel);

      Term builtInPredicate = get();
      if (builtInPredicate == null) {
         if (throwExceptionRatherThanReturnNull) {
            throwParserException("Unexpected end after: " + firstArg);
         } else if (firstArg != null) {
            throwParserException("incomplete sentence: " + firstArg);
         } else {
            return null;
         }
      } else if (!isInfix(builtInPredicate)) {
         // could be '.' if end of sentence 
         // or ',', '|', ']' or ')' if parsing list or predicate
         // or could be an error
         lastParsedTerm = builtInPredicate;
         return firstArg;
      }

      int level = getInfixLevel(builtInPredicate);
      if (level > maxLevel) {
         // we will get here when parsing:
         // ?- X is -Y, Z = 2.
         // to avoid: is(X, -(,(Y, Z)))
         lastParsedTerm = builtInPredicate;
         return firstArg;
      }
      if (firstArg != null && firstArg.getType() == TermType.STRUCTURE && operands.prefix(firstArg.getName())) {
         // this check was added for rules like:
         // -X<1
         // ?- :- a.
         // ?- a :- a.
         int prefixLevel = getPrefixLevel(firstArg);
         if (prefixLevel >= level) {
            throwParserException("Level");
         }
      }

      Term secondArg = getPossiblePrefixArgument(level);
      if (secondArg == null) {
         throwParserException("Unexpected end of statement");
      }

      Term t = Structure.createStructure(builtInPredicate.getName(), new Term[] {firstArg, secondArg});
      return getTerm(t, level, maxLevel);
   }

   /**
    * Recursively called to combine individual terms into a composite term.
    * <p>
    * While the parsing of the individual terms is performed, priority of the operands they represent needs to be
    * considered to make sure the terms are ordered correctly (due to different operand precedence it is not always the
    * case that the terms will be ordered in the resulting composite term in the same order they were parsed from the
    * input stream).
    * 
    * @param currentTerm represents the current state of the process to parse a complete term
    * @param currentLevel the current priority/precedence/level of terms being parsed - if an operand represented by a
    * term retrieved by this method has a higher priority then reordering needs to take place to position the term in
    * the right position in relation to the other terms that exist within the {@code currentTerm} structure (in order to
    * maintain the correct priority)
    * @param maxLevel the maximum priority/precedence/level of operands to parse - if an operand represented by the next
    * term retrieved by this method has a higher priority then it is ignored for now ({@code currentTerm} is returned
    * "as-is"}.
    */
   private Term getTerm(final Term currentTerm, final int currentLevel, final int maxLevel) {
      Term builtInPredicate = get();
      if (builtInPredicate == null) {
         throwParserException("Unexpected end after: " + currentTerm);
      } else if (isPostfix(builtInPredicate)) {
         Term postfixTerm = addPostfixOperand(currentTerm, builtInPredicate);
         return getTerm(postfixTerm, currentLevel, maxLevel);
      } else if (!isInfix(builtInPredicate)) {
         // could be '.' if end of sentence 
         // or ',', '|', ']' or ')' if parsing list or predicate
         // or could be an error
         lastParsedTerm = builtInPredicate;
         return currentTerm;
      }

      int level = getInfixLevel(builtInPredicate);
      if (level > maxLevel) {
         lastParsedTerm = builtInPredicate;
         return currentTerm;
      }

      Term secondArg = getPossiblePrefixArgument(level);
      if (isPeriod(secondArg)) {
         throwParserException("Unexpected . after " + builtInPredicate);
      }

      if (level < currentLevel) {
         // compare previous.getArgs()[1] to level -
         // keep going until find right level to add this term to
         Term t = currentTerm;
         while (isParsedInfixTerms(t.getArgs()[1]) && getInfixLevel(t.getArgs()[1]) > level) {
            t = t.getArgs()[1];
         }
         Term predicate = Structure.createStructure(builtInPredicate.getName(), new Term[] {t.getArgs()[1], secondArg});
         parsedInfixTerms.add(predicate);
         t.getArgs()[1] = predicate;
         return getTerm(currentTerm, currentLevel, maxLevel);
      } else {
         if (level == currentLevel) {
            if (operands.xfx(builtInPredicate.getName())) {
               throwParserException("Operand " + builtInPredicate + " has same precedence level as preceding operand: " + currentTerm);
            }
         }
         Term predicate = Structure.createStructure(builtInPredicate.getName(), new Term[] {currentTerm, secondArg});
         parsedInfixTerms.add(predicate);
         return getTerm(predicate, level, maxLevel);
      }
   }

   /**
    * Add a term, representing a post-fix operand, in the appropriate point of a composite term.
    * <p>
    * The correct position of the post-fix operand within the composite term (and so what the post-fix operands actual
    * argument will be) is determined by operand priority.
    * 
    * @param original a composite term representing the current state of parsing the current sentence
    * @param postfixOperand a term which represents a post-fix operand
    */
   private Term addPostfixOperand(Term original, Term postfixOperand) {
      int level = getPostfixLevel(postfixOperand);
      if (original.getNumberOfArguments() == 2) {
         boolean higherLevelInfixOperand = operands.infix(original.getName()) && getInfixLevel(original) > level;
         if (higherLevelInfixOperand) {
            String name = original.getName();
            Term firstArg = original.getArgument(0);
            Term newSecondArg = addPostfixOperand(original.getArgument(1), postfixOperand);
            return Structure.createStructure(name, new Term[] {firstArg, newSecondArg});
         }
      } else if (original.getNumberOfArguments() == 1) {
         if (operands.prefix(original.getName())) {
            if (getPrefixLevel(original) > level) {
               String name = original.getName();
               Term newFirstArg = addPostfixOperand(original.getArgument(0), postfixOperand);
               return Structure.createStructure(name, new Term[] {newFirstArg});
            }
         } else if (operands.postfix(original.getName())) {
            int levelToCompareTo = getPostfixLevel(original);
            // "x" in "xf" means that the argument can <i>only</i> contain operators of a lower priority.
            if (levelToCompareTo > level || (isXF(postfixOperand) && levelToCompareTo == level)) {
               throwParserException("Invalid postfix: " + postfixOperand + " " + level + " and term: " + original + " " + levelToCompareTo);
            }
         }
      }
      return Structure.createStructure(postfixOperand.getName(), new Term[] {original});
   }

   /**
    * Has the specified term already been parsed, and included as an argument in an infix operand, as part of parsing
    * the current sentence?
    */
   private boolean isParsedInfixTerms(Term t) {
      return parsedInfixTerms.contains(t);
   }

   /**
    * Parses and returns a {@code Term}.
    * <p>
    * If the parsed {@code Term} represents a prefix operand, then the subsequent term is also parsed so it can be used
    * as an argument in the returned structure.
    * 
    * @param currentLevel the current priority level of terms being parsed (if the parsed term represents a prefix
    * operand, then the operand cannot have a higher priority than {@code currentLevel} (a {@code ParserException} will
    * be thrown if does).
    */
   private Term getPossiblePrefixArgument(int currentLevel) {
      Term t = get();
      if (isPrefix(t) && isFollowedByDelimiter() == false) {
         int prefixLevel = getPrefixLevel(t);
         if (prefixLevel > currentLevel) {
            throwParserException("Invalid prefix: " + t + " level: " + prefixLevel + " greater than current level: " + currentLevel);
         }
         // The difference between "fy" and "fx" associativity is that a "y" means that the argument
         // can contain operators of <i>the same</i> or lower level of priority
         // while a "x" means that the argument can <i>only</i> contain operators of a lower priority.
         if (isFX(t)) {
            // -1 to only parse terms of a lower priority than the current prefix operator.
            prefixLevel--;
         }
         Term argument = getTerm(prefixLevel, true);
         return createPrefixTerm(t.getName(), argument);
      } else {
         return t;
      }
   }

   /**
    * Returns {@code true} if the next non-whitespace character read is a delimiter.
    * 
    * @see TermParser#isDelimiter(char)
    */
   private boolean isFollowedByDelimiter() {
      int c;
      while ((c = parser.getNext()) != -1 && Character.isWhitespace(c)) {
         // keep going until we find start of next term after prefix argument
         // (or reach end of stream)
      }
      parser.rewind();
      return isDelimiter((char) c);
   }

   /**
    * Returns a new {@code Term} representing the specified prefix operand and argument.
    * <p>
    * In most cases the result returned will be a {@code Structure} consisting of the specified name and argument.
    * <p>
    * If the specified prefix operand name is a "{@code -}" and the specified argument is a {@code Numeric} then a new
    * {@code Numeric} will be returned whose numeric value will be the negation of the numeric value of the specified
    * argument.
    */
   private Term createPrefixTerm(String prefixOperandName, Term argument) {
      if ("-".equals(prefixOperandName)) {
         if (argument.getType() == TermType.INTEGER) {
            return new IntegerNumber(-((Numeric) argument).getInt());
         } else if (argument.getType() == TermType.DOUBLE) {
            return new DoubleNumber(-((Numeric) argument).getDouble());
         }
      }
      return Structure.createStructure(prefixOperandName, new Term[] {argument});
   }

   private boolean isFX(Term t) {
      return t != null && t.getType() == TermType.ATOM && operands.fx(t.getName());
   }

   private boolean isXF(Term t) {
      return t != null && t.getType() == TermType.ATOM && operands.xf(t.getName());
   }

   private boolean isPrefix(Term t) {
      return t != null && t.getType() == TermType.ATOM && operands.prefix(t.getName());
   }

   private boolean isInfix(Term t) {
      return t != null && t.getType() == TermType.ATOM && operands.infix(t.getName());
   }

   private boolean isPostfix(Term t) {
      return t != null && t.getType() == TermType.ATOM && operands.postfix(t.getName());
   }

   private int getPrefixLevel(Term t) {
      return operands.getPrefixPriority(t.getName());
   }

   private int getInfixLevel(Term t) {
      return operands.getInfixPriority(t.getName());
   }

   private int getPostfixLevel(Term t) {
      return operands.getPostfixPriority(t.getName());
   }

   private Term get() {
      if (lastParsedTerm != null) {
         Term t = lastParsedTerm;
         lastParsedTerm = null;
         return t;
      }
      return super.parseTerm();
   }

   private boolean isPeriod(Term t) {
      return t == TermParser.PERIOD;
   }
}