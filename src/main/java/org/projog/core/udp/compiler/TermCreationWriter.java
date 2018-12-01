/*
 * Copyright 2018 S. Webber
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
package org.projog.core.udp.compiler;

import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.encodeName;
import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.isAnonymousVariable;

import org.projog.core.term.EmptyList;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/** Generates Java source code to construct a {@code Term}. */
final class TermCreationWriter {
   String outputCreateTermStatement(Term t, CompiledPredicateState state, boolean outputImmutable) {
      // TODO even when "outputImmutable==true" still use variable name, rather than creating a new term,
      // if a suitable candidate has already been created and assigned to a static variable.

      if (t == EmptyList.EMPTY_LIST) {
         return "EmptyList.EMPTY_LIST";
      } else if (!outputImmutable && t.isImmutable()) {
         return state.getTermVariableName(t);
      } else if (isAnonymousVariable(t)) {
         return "new Variable(\"" + Variable.ANONYMOUS_VARIABLE_ID + "\")";
      } else if (t.getType() == TermType.NAMED_VARIABLE) {
         return state.getJavaVariableName((Variable) t);
      } else if (t.getType() == TermType.STRUCTURE) {
         StringBuilder sb = new StringBuilder("Structure.createStructure(");
         sb.append(encodeName(t));
         sb.append(",new Term[]{");
         boolean first = true;
         for (Term arg : t.getArgs()) {
            if (first) {
               first = false;
            } else {
               sb.append(",");
            }
            sb.append(outputCreateTermStatement(arg, state, outputImmutable));
            if (arg.isImmutable() == false) {
               sb.append(".getTerm()");
            }
         }
         sb.append("})");
         return sb.toString();
      } else if (t.getType() == TermType.LIST) {
         String headSyntax = outputListElement(t.getArgument(0), state, outputImmutable);
         String tailSyntax = outputListElement(t.getArgument(1), state, outputImmutable);
         return "new List(" + headSyntax + "," + tailSyntax + ")";
      } else if (t.getType() == TermType.ATOM) {
         return "new Atom(" + encodeName(t) + ")";
      } else if (t.getType() == TermType.INTEGER) {
         return "new IntegerNumber(" + t.getName() + "L)";
      } else if (t.getType() == TermType.FRACTION) {
         return "new DecimalFraction(" + t.getName() + ")";
      } else {
         throw new RuntimeException("unknown " + t.getType() + " " + t.getClass() + " " + t);
      }
   }

   private String outputListElement(Term element, CompiledPredicateState state, boolean outputImmutable) {
      String syntax = outputCreateTermStatement(element, state, outputImmutable);
      if (element.isImmutable() == false) {
         syntax += ".getTerm()";
      }
      return syntax;
   }
}
