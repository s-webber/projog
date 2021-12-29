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
package org.projog.core.predicate.builtin.kb;

import static org.projog.core.kb.KnowledgeBaseUtils.getPredicateKeysByName;
import static org.projog.core.term.TermUtils.getAtomName;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Term;

/* TEST
test(X) :- X < 3.
test(X) :- X > 9.
test(X) :- X = 5.
%?- listing(test)
%OUTPUT
%test(X) :- X < 3
%test(X) :- X > 9
%test(X) :- X = 5
%
%OUTPUT
%YES

overloaded_predicate_name(X) :- X = this_rule_has_one_argument.
overloaded_predicate_name(X, Y) :- X = this_rule_has_two_arguments, X = Y.
%?- listing(overloaded_predicate_name)
%OUTPUT
%overloaded_predicate_name(X) :- X = this_rule_has_one_argument
%overloaded_predicate_name(X, Y) :- X = this_rule_has_two_arguments , X = Y
%
%OUTPUT
%YES

%TRUE listing(predicate_name_that_doesnt_exist_in_knowledge_base)

%?- listing(X)
%ERROR Expected an atom but got: VARIABLE with value: X
*/
/**
 * <code>listing(X)</code> - outputs current clauses.
 * <p>
 * <code>listing(X)</code> allows you to inspect the clauses you currently have loaded. Causes all clauses with
 * <code>X</code> as the predicate name to be written to the current output stream.
 * </p>
 */
public final class Listing extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term arg) {
      String predicateName = getAtomName(arg);
      List<PredicateKey> keys = getPredicateKeysByName(getKnowledgeBase(), predicateName);
      for (PredicateKey key : keys) {
         listClauses(key);
      }
      return true;
   }

   private void listClauses(PredicateKey key) {
      Iterator<ClauseModel> implications = getClauses(key);
      while (implications.hasNext()) {
         listClause(implications.next());
      }
   }

   private Iterator<ClauseModel> getClauses(PredicateKey key) {
      Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = getPredicates().getUserDefinedPredicates();
      UserDefinedPredicateFactory userDefinedPredicate = userDefinedPredicates.get(key);
      return userDefinedPredicate.getImplications();
   }

   private void listClause(ClauseModel clauseModel) {
      String s = getTermFormatter().formatTerm(clauseModel.getOriginal());
      getFileHandles().getCurrentOutputStream().println(s);
   }
}
