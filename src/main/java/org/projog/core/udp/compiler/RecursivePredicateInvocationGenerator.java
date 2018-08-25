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
package org.projog.core.udp.compiler;

import static org.projog.core.udp.compiler.CompiledPredicateVariables.ARGUMENT_PREFIX;
import static org.projog.core.udp.compiler.CompiledPredicateVariables.PLACEHOLDER_PREFIX;

import org.projog.core.term.Term;

final class RecursivePredicateInvocationGenerator implements PredicateInvocationGenerator {
   @Override
   public void generate(CompiledPredicateWriter g) {
      Term function = g.currentClause().getCurrentFunction();

      if (g.factMetaData().isTailRecursive() && g.currentClause().getConjunctionIndex() == g.currentClause().getConjunctionCount() - 1) {
         for (int i = 0; i < g.factMetaData().getNumberArguments(); i++) {
            Term tailRecursiveArgument = function.getArgument(i);
            String tailRecursiveArgumentSyntax = g.outputCreateTermStatement(tailRecursiveArgument, true);
            if (g.factMetaData().isTailRecursiveArgument(i)) {
               g.assign(ARGUMENT_PREFIX + i, tailRecursiveArgumentSyntax + "==null?" + PLACEHOLDER_PREFIX + i + ":" + tailRecursiveArgumentSyntax + ".getTerm()");
            } else {
               g.assign(ARGUMENT_PREFIX + i, tailRecursiveArgumentSyntax + ".getTerm()");
            }
         }
      } else {
         g.callUserDefinedPredicate(g.className(), true);
      }
   }
}
