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
package org.projog.core.predicate.udp;

import static org.projog.core.kb.KnowledgeBaseUtils.CONJUNCTION_PREDICATE_NAME;
import static org.projog.core.kb.KnowledgeBaseUtils.IMPLICATION_PREDICATE_NAME;
import static org.projog.core.kb.KnowledgeBaseUtils.toArrayOfConjunctions;

import java.util.ArrayList;

import org.projog.core.ProjogException;
import org.projog.core.term.List;
import org.projog.core.term.ListFactory;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/**
 * Provides support for Definite Clause Grammars (DCG).
 * <p>
 * DCGs provide a convenient way to express grammar rules.
 */
final class DefiniteClauseGrammerConvertor {
   private DefiniteClauseGrammerConvertor() {
   }

   static boolean isDCG(Term dcgTerm) { // should this be moved to KnowledgeBaseUtils?
      return dcgTerm.getType() == TermType.STRUCTURE && dcgTerm.getNumberOfArguments() == 2 && "-->".equals(dcgTerm.getName());
   }

   /**
    * @param dcgTerm predicate with name "-->" and two arguments
    */
   static Term convert(Term dcgTerm) {
      if (!isDCG(dcgTerm)) {
         throw new ProjogException("Expected two argument predicate named \"-->\" but got: " + dcgTerm);
      }

      Term consequent = getConsequent(dcgTerm);
      Term antecedent = getAntecedent(dcgTerm);
      // slightly inefficient as will have already converted to an array in validate method
      Term antecedents[] = toArrayOfConjunctions(antecedent);

      if (hasSingleListWithSingleAtomElement(antecedents)) {
         return convertSingleListTermAntecedent(consequent, antecedents[0]);
      } else {
         return convertConjunctionOfAtomsAntecedent(consequent, antecedents);
      }
   }

   private static Term convertSingleListTermAntecedent(Term consequent, Term antecedent) {
      String consequentName = consequent.getName();
      Variable variable = new Variable("A");
      List list = ListFactory.createList(antecedent.firstArgument(), variable);
      Term[] args = new Term[consequent.getNumberOfArguments() + 2];
      for (int i = 0; i < consequent.getNumberOfArguments(); i++) {
         args[i] = consequent.getArgument(i);
      }
      args[args.length - 2] = list;
      args[args.length - 1] = variable;
      return StructureFactory.createStructure(consequentName, args);
   }

   // TODO this method is too long - refactor
   private static Term convertConjunctionOfAtomsAntecedent(Term consequent, Term[] conjunctionOfAtoms) {
      ArrayList<Term> newSequence = new ArrayList<>();

      Variable lastArg = new Variable("A0");

      int varctr = 1;
      Term previous = lastArg;
      Term previousList = null;
      for (int i = conjunctionOfAtoms.length - 1; i > -1; i--) {
         Term term = conjunctionOfAtoms[i];
         if ("{".equals(term.getName())) {
            Term newAntecedentArg = term.firstArgument().firstArgument();
            newSequence.add(0, newAntecedentArg);
         } else if (term.getType() == TermType.LIST) {
            if (previousList != null) {
               term = appendToEndOfList(term, previousList);
            }
            previousList = term;
         } else {
            if (previousList != null) {
               Variable next = new Variable("A" + (varctr++));
               Term newAntecedentArg = StructureFactory.createStructure("=", new Term[] {next, appendToEndOfList(previousList, previous)});
               newSequence.add(0, newAntecedentArg);
               previousList = null;
               previous = next;
            }

            Variable next = new Variable("A" + (varctr++));
            Term newAntecedentArg = createNewPredicate(term, next, previous);
            previous = next;
            newSequence.add(0, newAntecedentArg);
         }
      }

      Term newAntecedent;
      if (newSequence.isEmpty()) {
         newAntecedent = null;
      } else if (newSequence.size() == 1) {
         newAntecedent = newSequence.get(0);
      } else {
         newAntecedent = newSequence.get(newSequence.size() - 1);
         for (int i = newSequence.size() - 2; i > -1; i--) {
            newAntecedent = StructureFactory.createStructure(CONJUNCTION_PREDICATE_NAME, new Term[] {newSequence.get(i), newAntecedent});
         }
      }

      if (previousList != null) {
         previous = appendToEndOfList(previousList, previous);
      }

      Term newConsequent = createNewPredicate(consequent, previous, lastArg);

      if (newAntecedent == null) {
         return newConsequent;
      } else {
         return StructureFactory.createStructure(IMPLICATION_PREDICATE_NAME, new Term[] {newConsequent, newAntecedent});
      }
   }

   private static Term appendToEndOfList(Term list, Term newTail) {
      ArrayList<Term> terms = new ArrayList<>();
      while (list.getType() == TermType.LIST) {
         terms.add(list.firstArgument());
         list = list.secondArgument();
      }
      return ListFactory.createList(terms.toArray(new Term[0]), newTail);
   }

   private static Term createNewPredicate(Term original, Term previous, Term next) {
      Term[] args = new Term[original.getNumberOfArguments() + 2];
      for (int a = 0; a < original.getNumberOfArguments(); a++) {
         args[a] = original.getArgument(a);
      }
      args[original.getNumberOfArguments()] = previous;
      args[original.getNumberOfArguments() + 1] = next;
      return StructureFactory.createStructure(original.getName(), args);
   }

   private static Term getConsequent(Term dcgTerm) {
      return dcgTerm.firstArgument();
   }

   private static Term getAntecedent(Term dcgTerm) {
      return dcgTerm.secondArgument();
   }

   private static boolean hasSingleListWithSingleAtomElement(Term[] terms) {
      return terms.length == 1
             && terms[0].getType() == TermType.LIST
             && terms[0].firstArgument().getType() == TermType.ATOM
             && terms[0].secondArgument().getType() == TermType.EMPTY_LIST;
   }
}
