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
package org.projog.core.function.construct;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;

import org.projog.core.Calculatables;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Extended by {@code Predicate}s that compares a term to a list of individual characters or digits.
 */
abstract class AbstractTermSplitFunction extends AbstractSingletonPredicate {
   private final boolean firstArgNumeric;
   private final boolean convertToCharCodes;

   private Calculatables calculatables;

   AbstractTermSplitFunction(boolean firstArgNumeric, boolean convertToCharCodes) {
      this.firstArgNumeric = firstArgNumeric;
      this.convertToCharCodes = convertToCharCodes;
   }

   @Override
   public final void init() {
      calculatables = getCalculatables(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg1, Term arg2) {
      if (arg1.getType().isVariable()) {
         return evaluateWithVariableFirstArgument(arg1, arg2);
      } else {
         return evaluateWithConcreteFirstArgument(arg1, arg2);
      }
   }

   /**
    * Converts {@code arg2} from a list to an atom and attempts to unify it with {@code arg1}.
    * <p>
    * Example of a prolog query that would cause this method to be used: <pre>
    * ?- number_chars(X, [1,4,2]).
    * X = 142
    * </pre>
    *
    * @param arg1 a {@code Variable}
    * @param arg2 a {@code List}
    * @return {@code true} if was able to unify
    */
   private boolean evaluateWithVariableFirstArgument(Term arg1, Term arg2) {
      if (isNotList(arg2)) {
         throw new ProjogException("As the first argument: " + arg1 + " is a variable the second argument needs to be a list but was: " + arg2 + " of type: " + arg2.getType());
      }
      StringBuffer sb = new StringBuffer();
      appendListElementsToString(sb, arg2);
      Term t = toTerm(sb.toString());
      return arg1.unify(t);
   }

   /**
    * Converts {@code arg1} to a list and attempts to unify it with {@code arg2}.
    * <p>
    * Example of a prolog query that would cause this method to be used: <pre>
    * ?- atom_chars(apple, X).
    * X = [a,p,p,l,e]
    * </pre>
    *
    * @param arg1 a {@code Atom} or {@code Numeric}
    * @param arg2 in order to unify, this argument must represent a {@code Atom} or {@code List}
    * @return {@code true} if was able to unify
    */
   private boolean evaluateWithConcreteFirstArgument(Term arg1, Term arg2) {
      isValidConcreteFirstArgument(arg1);
      char[] chars = arg1.getName().toCharArray();
      int numChars = chars.length;
      Term[] listElements = new Term[numChars];
      for (int i = 0; i < numChars; i++) {
         listElements[i] = charToTerm(chars[i]);
      }
      Term l = ListFactory.createList(listElements);
      return arg2.unify(l);
   }

   private void isValidConcreteFirstArgument(Term arg) {
      TermType t = arg.getType();
      if (t.isNumeric()) {
         return;
      }

      if (t == TermType.ATOM && !firstArgNumeric) {
         return;
      }

      throw new ProjogException("Unexpected type for first argument: " + t);
   }

   private boolean isNotList(Term t) {
      TermType tt = t.getType();
      return tt != TermType.LIST && tt != TermType.EMPTY_LIST;
   }

   private void appendListElementsToString(StringBuffer sb, Term t) {
      TermType type = t.getType();
      if (type == TermType.LIST) {
         appendListElementsToString(sb, t.getArgument(0));
         appendListElementsToString(sb, t.getArgument(1));
      } else if (type == TermType.ATOM) {
         String name = t.getName();
         sb.append(stringToChar(name));
      } else if (type.isNumeric()) {
         long n = calculatables.getNumeric(t).getLong();
         sb.append(numericToChar(n));
      } else if (type != TermType.EMPTY_LIST) {
         throw new ProjogException("Unexpected type in list: " + type);
      }
   }

   private char stringToChar(String name) {
      if (name.length() == 1) {
         return name.charAt(0);
      } else {
         throw new ProjogException("Expected atom in list to have exactly one character: " + name);
      }
   }

   protected Term charToTerm(char c) {
      if (convertToCharCodes) {
         return new IntegerNumber(c);
      } else {
         return new Atom(Character.toString(c));
      }
   }

   private char numericToChar(long n) {
      return (char) n;
   }

   private Term toTerm(String s) {
      if (firstArgNumeric) {
         return toNumeric(s);
      } else {
         return new Atom(s);
      }
   }

   private Numeric toNumeric(String s) {
      if (s.indexOf('.') == -1) {
         return toInteger(s);
      } else {
         return toDecimal(s);
      }
   }

   private IntegerNumber toInteger(String s) {
      try {
         return new IntegerNumber(Integer.parseInt(s));
      } catch (NumberFormatException e) {
         throw new ProjogException("Could not convert characters to an integer: '" + s + "'");
      }
   }

   private Numeric toDecimal(String s) {
      try {
         return new DecimalFraction(Double.parseDouble(s));
      } catch (NumberFormatException e) {
         throw new ProjogException("Could not convert characters to a decimal: '" + s + "'");
      }
   }
}
