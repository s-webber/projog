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
package org.projog.core.udp;

import static org.projog.core.KnowledgeBaseUtils.CONJUNCTION_PREDICATE_NAME;
import static org.projog.core.KnowledgeBaseUtils.IMPLICATION_PREDICATE_NAME;
import static org.projog.core.KnowledgeBaseUtils.toArrayOfConjunctions;

import java.util.ArrayList;

import org.projog.core.ProjogException;
import org.projog.core.term.List;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.Variable;

/**
 * Provides support for Definite Clause Grammars (DCG).
 * <p>
 * DCGs provide a convenient way to express grammar rules.
 */
final class DefiniteClauseGrammerConvertor {
   static boolean isDCG(Term dcgTerm) { // should this be moved to KnowledgeBaseUtils?
      return dcgTerm.getType() == TermType.STRUCTURE && dcgTerm.getNumberOfArguments() == 2 && dcgTerm.getName().equals("-->");
   }

   /**
    * @param dcgTerm predicate with name "-->" and two arguments
    */
   static Term convert(Term dcgTerm) {
      if (isDCG(dcgTerm) == false) {
         throw new ProjogException("Expected two argument predicate named \"-->\" but got: " + dcgTerm);
      }

      Term consequent = getConsequent(dcgTerm);
      Term antecedant = getAntecedant(dcgTerm);
      // slightly inefficient as will have already converted to an array in validate method
      Term antecedants[] = toArrayOfConjunctions(antecedant);

      if (hasSingleListWithSingleAtomElement(antecedants)) {
         return convertSingleListTermAntecedant(consequent, antecedants[0]);
      } else {
         return convertConjunctionOfAtomsAntecedant(consequent, antecedants);
      }
   }

   private static Term convertSingleListTermAntecedant(Term consequent, Term antecedant) {
      String consequentName = consequent.getName();
      Variable variable = new Variable("A");
      List list = ListFactory.createList(antecedant.getArgument(0), variable);
      Term[] args = new Term[consequent.getNumberOfArguments() + 2];
      for (int i = 0; i < consequent.getNumberOfArguments(); i++) {
         args[i] = consequent.getArgument(i);
      }
      args[args.length - 2] = list;
      args[args.length - 1] = variable;
      return Structure.createStructure(consequentName, args);
   }

   // TODO this method is too long - refactor
   private static Term convertConjunctionOfAtomsAntecedant(Term consequent, Term[] conjunctionOfAtoms) {
      ArrayList<Term> newSequence = new ArrayList<>();

      Variable lastArg = new Variable("A0");

      int varctr = 1;
      Term previous = lastArg;
      Term previousList = null;
      for (int i = conjunctionOfAtoms.length - 1; i > -1; i--) {
         Term term = conjunctionOfAtoms[i];
         if (term.getName().equals("{")) {
            Term newAntecedantArg = term.getArgument(0).getArgument(0);
            newSequence.add(0, newAntecedantArg);
         } else if (term.getType() == TermType.LIST) {
            if (previousList != null) {
               term = appendToEndOfList(term, previousList);
            }
            previousList = term;
         } else {
            if (previousList != null) {
               Variable next = new Variable("A" + (varctr++));
               Term newAntecedantArg = Structure.createStructure("=", new Term[] {next, appendToEndOfList(previousList, previous)});
               newSequence.add(0, newAntecedantArg);
               previousList = null;
               previous = next;
            }

            Variable next = new Variable("A" + (varctr++));
            Term newAntecedantArg = createNewPredicate(term, next, previous);
            previous = next;
            newSequence.add(0, newAntecedantArg);
         }
      }

      Term newAntecedant;
      if (newSequence.isEmpty()) {
         newAntecedant = null;
      } else {
         newAntecedant = newSequence.get(0);
         for (int i = 1; i < newSequence.size(); i++) {
            newAntecedant = Structure.createStructure(CONJUNCTION_PREDICATE_NAME, new Term[] {newAntecedant, newSequence.get(i)});
         }
      }

      if (previousList != null) {
         previous = appendToEndOfList(previousList, previous);
      }
      Term newConsequent = createNewPredicate(consequent, previous, lastArg);

      if (newAntecedant == null) {
         return newConsequent;
      } else {
         return Structure.createStructure(IMPLICATION_PREDICATE_NAME, new Term[] {newConsequent, newAntecedant});
      }
   }

   private static Term appendToEndOfList(Term list, Term newTail) {
      ArrayList<Term> terms = new ArrayList<>();
      while (list.getType() == TermType.LIST) {
         terms.add(list.getArgument(0));
         list = list.getArgument(1);
      }
      return ListFactory.createList(terms.toArray(new Term[terms.size()]), newTail);
   }

   private static Term createNewPredicate(Term original, Term previous, Term next) {
      Term[] args = new Term[original.getNumberOfArguments() + 2];
      for (int a = 0; a < original.getNumberOfArguments(); a++) {
         args[a] = original.getArgument(a);
      }
      args[original.getNumberOfArguments()] = previous;
      args[original.getNumberOfArguments() + 1] = next;
      return Structure.createStructure(original.getName(), args);
   }

   private static Term getConsequent(Term dcgTerm) {
      return dcgTerm.getArgument(0);
   }

   private static Term getAntecedant(Term dcgTerm) {
      return dcgTerm.getArgument(1);
   }

   private static boolean hasSingleListWithSingleAtomElement(Term[] terms) {
      return terms.length == 1
             && terms[0].getType() == TermType.LIST
             && terms[0].getArgument(0).getType() == TermType.ATOM
             && terms[0].getArgument(1).getType() == TermType.EMPTY_LIST;
   }
}
