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
package org.projog.core.udp;

import static org.projog.core.KnowledgeBaseUtils.IMPLICATION_PREDICATE_NAME;

import org.projog.core.term.Atom;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/**
 * Represents a clause.
 * <p>
 * A clause consists of a head and a body. Where a clause is not explicitly specified it defaults to having a body of
 * {@code true}.
 * <p>
 * Called {@code ClauseModel} to differentiate it from {@link org.projog.core.udp.interpreter.ClauseAction}.
 */
public final class ClauseModel {
   private static final Term TRUE = new Atom("true");

   private final Term original;
   private final Term consequent;
   private final Term antecedent;

   public static ClauseModel createClauseModel(Term original) {
      final Term consequent;
      final Term antecedent;

      if (DefiniteClauseGrammerConvertor.isDCG(original)) {
         original = DefiniteClauseGrammerConvertor.convert(original);
      }

      if (original.getName().equals(IMPLICATION_PREDICATE_NAME)) {
         Term[] implicationArgs = original.getArgs();
         consequent = implicationArgs[0];
         if (implicationArgs.length == 2) {
            antecedent = implicationArgs[1];
         } else if (implicationArgs.length == 1) {
            antecedent = TRUE;
         } else {
            throw new RuntimeException();
         }
      } else {
         consequent = original;
         antecedent = TRUE;
      }

      return new ClauseModel(original, consequent, antecedent);
   }

   private ClauseModel(Term original, Term consequent, Term antecedent) {
      this.original = original;
      this.consequent = consequent;
      this.antecedent = antecedent;
   }

   /**
    * Replaced by {{@link #getAntecedent()} but marking as deprecated, rather than removing, as is used in
    * projog-examples.
    *
    * @deprecated
    * @see #getAntecedent
    */
   @Deprecated
   public Term getAntecedant() {
      return getAntecedent();
   }

   /** Returns the body of the clause. i.e. the bit after the {@code :-} */
   public Term getAntecedent() {
      return antecedent;
   }

   /** Returns the head of the clause. i.e. the bit before the {@code :-} */
   public Term getConsequent() {
      return consequent;
   }

   public Term getOriginal() {
      return original;
   }

   public ClauseModel copy() {
      Term[] newTerms = TermUtils.copy(original, consequent, antecedent);
      return new ClauseModel(newTerms[0], newTerms[1], newTerms[2]);
   }

   @Override
   public String toString() {
      return "[" + super.toString() + " " + consequent + " " + antecedent + "]";
   }
}
