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

import org.projog.core.term.Term;

/**
 * Outputs java code that matches the functionality of classes in {@code org.projog.core.function.compare}
 */
abstract class NumericComparisonPredicateInvocationGenerator implements PredicateInvocationGenerator {
   protected void ouputNumericComparison(CompiledPredicateWriter g, String logic) {
      Term function = g.currentClause().getCurrentFunction();
      String args = g.outputCreateTermStatement(function.getArgument(0), true) + ", " + g.outputCreateTermStatement(function.getArgument(1), true);
      g.setNeedsCalculatablesStaticVariable(true);
      String eval = "NUMERIC_TERM_COMPARATOR.compare(" + args + ", c)" + logic;
      // NOTE: no need to backtrack args in numeric term comparator evaluation (as no assignments made)
      // (so no need to to update currentClause.variablesToBackTrack)
      g.outputIfTrueThenBreak(eval);
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericEquality}
    */
   static class NumericEqualPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, "!=0");
      }
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericInequality}
    */
   static class NumericNotEqualPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, "==0");
      }
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericGreaterThan}
    */
   static class NumericGreaterThanPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, "!=1");
      }
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericGreaterThanOrEqual}
    */
   static class NumericGreaterThanOrEqualPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, "<0");
      }
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericLessThan}
    */
   static class NumericLessThanPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, "!=-1");
      }
   }

   /**
    * Outputs java code that matches functionality of {@link org.projog.core.function.compare.NumericLessThanOrEqual}
    */
   static class NumericLessThanOrEqualPredicateInvocationGenerator extends NumericComparisonPredicateInvocationGenerator {
      @Override
      public void generate(CompiledPredicateWriter g) {
         ouputNumericComparison(g, ">0");
      }
   }
}
