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
package org.projog.core.udp.compiler;

import org.projog.core.PredicateKey;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/**
 * Contains static methods which aid the construction of {@link CompiledPredicate} source code.
 */
final class CompiledPredicateSourceGeneratorUtils {
   /**
    * Private constructor as all methods are static.
    */
   private CompiledPredicateSourceGeneratorUtils() {
      // do nothing
   }

   static String getClassNameMinusPackage(Object o) {
      return o.getClass().getSimpleName();
   }

   static String encodeName(Term t) {
      return encodeName(t.getName());
   }

   static String encodeName(String in) {
      StringBuilder out = new StringBuilder(in.length() + 2);
      out.append('\"');
      for (int i = 0; i < in.length(); i++) {
         char c = in.charAt(i);
         if (c == '\\') {
            out.append("\\\\");
         } else if (c == '\"') {
            out.append("\\\"");
         } else if (c >= ' ' && c <= '~') {
            out.append(c);
         } else {
            switch (c) {
               case '\t': // tab
                  out.append("\\t");
                  break;
               case '\b': // backspace
                  out.append("\\b");
                  break;
               case '\n': // newline
                  out.append("\\n");
                  break;
               case '\r': // carriage return
                  out.append("\\r");
                  break;
               case '\f': // formfeed
                  out.append("\\f");
                  break;
               case '\"': // double quote
                  out.append("\\\"");
                  break;
               case '\\': // backslash
                  out.append("\\\\");
                  break;
               default:
                  out.append("\\u");
                  String hex = Integer.toString(c, 16);
                  for (int p = hex.length(); p < 4; p++) {
                     out.append('0');
                  }
                  out.append(hex);
            }
         }
      }
      out.append('\"');
      return out.toString();
   }

   static String getKeyGeneration(PredicateKey key) {
      return "new PredicateKey(" + encodeName(key.getName()) + ", " + key.getNumArgs() + ")";
   }

   static boolean isNoMoreThanTwoElementList(Term t) {
      if (t.getType() != TermType.LIST) {
         return false;
      }
      Term tail = t.getArgument(1);
      return tail.getType() != TermType.LIST;
   }

   static String getUnifyStatement(String variable1, String variable2) {
      return variable1 + ".unify(" + variable2 + ")";
   }

   static String getNewVariableSyntax(Term variable) {
      return "new Variable(\"" + ((Variable) variable).getId() + "\")";
   }

   static String getNewListSyntax(String head, String tail) {
      return "new List(" + head + ", " + tail + ")";
   }
}
