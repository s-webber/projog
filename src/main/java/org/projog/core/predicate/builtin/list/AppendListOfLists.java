/*
 * Copyright 2020 S. Webber
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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.term.TermUtils.assertType;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.parser.SentenceParser;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.term.Atom;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumberCache;
import org.projog.core.term.StructureFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
%TRUE append([], [])
%TRUE_NO append([[]], [])
%TRUE_NO append([[a]], [a])
%TRUE_NO append([[a,b,c],[d,e,f,g,h]], [a,b,c,d,e,f,g,h])
%FAIL append([[a,b,c],[d,e,f,g,h]], [a,b,c,d,e,f,g,x])

%?- append([[a,b,c],[[d,e,f],x,y,z],[1,2,3],[]],X)
% X=[a,b,c,[d,e,f],x,y,z,1,2,3]
%NO

%?- append([[a,b,c],[[d,e,f],x,y,z],[1,2,3],[]],[a,b,c,[d,e,f],x|X])
% X=[y,z,1,2,3]
%NO

%?- append([X,[Y],Z], [a,b,c,d,e])
% X = []
% Y = a
% Z = [b,c,d,e]
% X = [a]
% Y = b
% Z = [c,d,e]
% X = [a,b]
% Y = c
% Z = [d,e]
% X = [a,b,c]
% Y = d
% Z = [e]
% X = [a,b,c,d]
% Y = e
% Z = []
%NO

%?- append(a, X)
%ERROR Expected LIST but got: ATOM with value: a
%?- append(Y, X)
%ERROR Expected LIST but got: VARIABLE with value: Y

%FAIL append([a], X)
%FAIL append([1], X)

%?- append([[a,b|X],[c,d]], Y)
% X = []
% Y = [a,b,c,d]
% X = [X]
% Y = [a,b,X,c,d]
% X = [X,X]
% Y = [a,b,X,X,c,d]
% X = [X,X,X]
% Y = [a,b,X,X,X,c,d]
% X = [X,X,X,X]
% Y = [a,b,X,X,X,X,c,d]
%QUIT

%?- append([[a,b],[c,d|X]], Y)
% X = []
% Y = [a,b,c,d]
% X = [X]
% Y = [a,b,c,d,X]
% X = [X,X]
% Y = [a,b,c,d,X,X]
% X = [X,X,X]
% Y = [a,b,c,d,X,X,X]
% X = [X,X,X,X]
% Y = [a,b,c,d,X,X,X,X]
%QUIT

%?- append([[a,b|X],[e,x|Y]], [a,b,c,d,e,x,y,z])
% X = [c,d]
% Y = [y,z]
%NO

%?- append([[a,b|X],[e,x|Y]], [a,b,c,d,e,x,y|Z])
% X = [c,d]
% Y = [y]
% Z = []
% X = [c,d]
% Y = [y,X]
% Z = [X]
% X = [c,d]
% Y = [y,X,X]
% Z = [X,X]
% X = [c,d]
% Y = [y,X,X,X]
% Z = [X,X,X]
%QUIT

%?- append([[a,b|X],[e,x|Y]], [a,b,c,d,e,x,y|Z]), Z=[1,2,3], !
% X = [c,d]
% Y = [y,1,2,3]
% Z = [1,2,3]
%NO

%?- append([X],Y)
% X = []
% Y = []
% X = [X]
% Y = [X]
% X = [X,X]
% Y = [X,X]
% X = [X,X,X]
% Y = [X,X,X]
%QUIT
*/
/**
 * <code>append(ListOfLists, List)</code> - concatenates a list of lists.
 * <p>
 * The <code>append(ListOfLists, List)</code> goal succeeds if the concatenation of lists contained in
 * <code>ListOfLists</code> matches the list <code>List</code>.
 * </p>
 */
public final class AppendListOfLists implements PredicateFactory {
   private final StaticUserDefinedPredicateFactory pf;

   public AppendListOfLists(KnowledgeBase kb) {
      PredicateKey key = PredicateKey.createFromNameAndArity(StructureFactory.createStructure("/", new Term[] {new Atom("pj_append"), IntegerNumberCache.valueOf(2)}));
      this.pf = new StaticUserDefinedPredicateFactory(kb, key);
      kb.getPredicates().addUserDefinedPredicate(pf);
      SentenceParser sp = SentenceParser
                  .getInstance("pj_append([], []). "
                               + "pj_append([CurrentList|RemainingLists], Words) :- append(CurrentList, RemainingWords, Words), pj_append(RemainingLists, RemainingWords).",
                              kb.getOperands());
      pf.addLast(ClauseModel.createClauseModel(sp.parseSentence()));
      pf.addLast(ClauseModel.createClauseModel(sp.parseSentence()));
      pf.compile();
   }

   @Override
   public Predicate getPredicate(Term input) {
      Term listOfLists = input.firstArgument();
      Term termToUnifyWith = input.secondArgument();
      if (listOfLists.getType() == TermType.EMPTY_LIST) {
         return PredicateUtils.toPredicate(termToUnifyWith.unify(EmptyList.EMPTY_LIST));
      }
      assertType(listOfLists, TermType.LIST);
      return pf.getPredicate(input);
   }

   @Override
   public boolean isRetryable() {
      return true;
   }
}
