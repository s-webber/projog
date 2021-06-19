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
package org.projog.core.predicate.builtin.list;

import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.apply;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.createArguments;
import static org.projog.core.predicate.builtin.list.PartialApplicationUtils.isAtomOrStructure;
import static org.projog.core.term.ListUtils.toJavaUtilList;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.PreprocessablePredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* TEST
 %TRUE maplist(atom, [])
 %TRUE maplist(atom, [a])
 %FALSE maplist(atom, [X])
 %FALSE maplist(atom, [1])
 %TRUE maplist(integer, [1])

 %TRUE maplist(atom, [a,a,a])
 %TRUE maplist(atom, [a,b,c])
 %FALSE maplist(atom, [1,b,c])
 %FALSE maplist(atom, [a,2,c])
 %FALSE maplist(atom, [a,b,3])
 %FALSE maplist(atom, [a,2,3])
 %FALSE maplist(atom, [1,b,3])
 %FALSE maplist(atom, [1,2,c])
 %FALSE maplist(atom, [1,2,3])

 %FALSE maplist(>(0), [3,4,2,1])
 %FALSE maplist(<(5), [3,4,2,1])
 %TRUE maplist(<(0), [3,4,2,1])
 %TRUE maplist(>(5), [3,4,2,1])
 %FALSE maplist(>(5), [3,4,5,2,1])
 %TRUE maplist(>=(5), [3,4,5,2,1])
 %FALSE maplist(>=(5), [3,4,5,2,1,6])
 %FALSE maplist(>=(5), [6,3,4,5,2,1])
 %FALSE maplist(>=(5), [3,4,5,6,2,1])

 %FALSE maplist(=(p(W)), [p(1),p(2),p(3)])

 % First argument must be an atom or structure. Second argument must be a list.
 %FALSE maplist(X, [])
 %FALSE maplist(atom, X)

 % maplist/3 applies the goal to pairs of elements from two lists.
 %TRUE maplist(=, [1,2,3], [1,2,3])
 %FALSE maplist(=, [1,2,3], [4,5,6])
 %FALSE maplist(=, [1,2,3], [1,3,2])
 %QUERY maplist(=, [X,2,3], [1,Y,Z])
 %ANSWER
 % X=1
 % Y=2
 % Z=3
 %ANSWER
 %QUERY maplist(=, [1,2,3], X)
 %ANSWER X=[1,2,3]
 %QUERY maplist(=, X, [1,2,3])
 %ANSWER X=[1,2,3]

 % Note: "checklist" is a synonym for "maplist".
 %TRUE checklist(atom, [a,b,c])
 %FALSE checklist(atom, [a,2,c])
 %TRUE checklist(=, [1,2,3], [1,2,3])
 %FALSE checklist(=, [1,2,3], [4,5,6])

 p(a,X,Y) :- X=Y.
 p(b,1,1).
 p(b,2,2).
 p(b,3,4).
 p(c,1,1).
 p(c,2,2).
 p(c,3,4).
 p(c,1,1).
 p(c,2,2).
 p(c,3,4).
 %TRUE maplist(p(a), [1,2,3], [1,2,3])
 %FALSE maplist(p(a), [1,2,3], [1,2,4])
 %FALSE maplist(p(b), [1,2,3], [1,2,3])
 %TRUE_NO maplist(p(b), [1,2,3], [1,2,4])
 %QUERY maplist(p(c), [1,2,3], [1,2,4])
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %NO
 %FALSE maplist(p(z), [1,2,3], [1,2,3])
 %TRUE maplist(p(z), [], [])
 %QUERY maplist(p(c,1), [1,1])
 %ANSWER/
 %ANSWER/
 %ANSWER/
 %ANSWER/

 %QUERY maplist(append, [[a,b],[c,d]], [[1,2],[3,4]], X)
 %ANSWER X=[[a,b,1,2],[c,d,3,4]]

 %QUERY maplist(append, X, Y, [[a,b,c,d],[1,2,3,4]])
 %ANSWER
 % X = [[],[]]
 % Y = [[a,b,c,d],[1,2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[],[1]]
 % Y = [[a,b,c,d],[2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[],[1,2]]
 % Y = [[a,b,c,d],[3,4]]
 %ANSWER
 %ANSWER
 % X = [[],[1,2,3]]
 % Y = [[a,b,c,d],[4]]
 %ANSWER
 %ANSWER
 % X = [[],[1,2,3,4]]
 % Y = [[a,b,c,d],[]]
 %ANSWER
 %ANSWER
 % X = [[a],[]]
 % Y = [[b,c,d],[1,2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a],[1]]
 % Y = [[b,c,d],[2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a],[1,2]]
 % Y = [[b,c,d],[3,4]]
 %ANSWER
 %ANSWER
 % X = [[a],[1,2,3]]
 % Y = [[b,c,d],[4]]
 %ANSWER
 %ANSWER
 % X = [[a],[1,2,3,4]]
 % Y = [[b,c,d],[]]
 %ANSWER
 %ANSWER
 % X = [[a,b],[]]
 % Y = [[c,d],[1,2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b],[1]]
 % Y = [[c,d],[2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b],[1,2]]
 % Y = [[c,d],[3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b],[1,2,3]]
 % Y = [[c,d],[4]]
 %ANSWER
 %ANSWER
 % X = [[a,b],[1,2,3,4]]
 % Y = [[c,d],[]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c],[]]
 % Y = [[d],[1,2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c],[1]]
 % Y = [[d],[2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c],[1,2]]
 % Y = [[d],[3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c],[1,2,3]]
 % Y = [[d],[4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c],[1,2,3,4]]
 % Y = [[d],[]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c,d],[]]
 % Y = [[],[1,2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c,d],[1]]
 % Y = [[],[2,3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c,d],[1,2]]
 % Y = [[],[3,4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c,d],[1,2,3]]
 % Y = [[],[4]]
 %ANSWER
 %ANSWER
 % X = [[a,b,c,d],[1,2,3,4]]
 % Y = [[],[]]
 %ANSWER

nine_arg_predicate(V1,V2,V3,V4,V5,V6,V7,V8,V9) :-
  write(V1), write(' '),
  write(V2), write(' '),
  write(V3), write(' '),
  write(V4), write(' '),
  write(V5), write(' '),
  write(V6), write(' '),
  write(V7), write(' '),
  write(V8), write(' '),
  write(V9), nl.

%QUERY maplist(nine_arg_predicate(1,2,3,4,5,6,7,8),[q,w])
%OUTPUT
%1 2 3 4 5 6 7 8 q
%1 2 3 4 5 6 7 8 w
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2,3,4,5,6,7),[q,w],[e,r])
%OUTPUT
%1 2 3 4 5 6 7 q e
%1 2 3 4 5 6 7 w r
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2,3,4,5,6),[q,w],[e,r],[t,y])
%OUTPUT
%1 2 3 4 5 6 q e t
%1 2 3 4 5 6 w r y
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2,3,4,5),[q,w],[e,r],[t,y],[u,i])
%OUTPUT
%1 2 3 4 5 q e t u
%1 2 3 4 5 w r y i
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2,3,4),[q,w],[e,r],[t,y],[u,i],[o,p])
%OUTPUT
%1 2 3 4 q e t u o
%1 2 3 4 w r y i p
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2,3),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s])
%OUTPUT
%1 2 3 q e t u o a
%1 2 3 w r y i p s
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1,2),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f])
%OUTPUT
%1 2 q e t u o a d
%1 2 w r y i p s f
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate(1),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f],[g,h])
%OUTPUT
%1 q e t u o a d g
%1 w r y i p s f h
%
%OUTPUT
%ANSWER/

%QUERY maplist(nine_arg_predicate,[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f],[g,h],[j,k])
%OUTPUT
%q e t u o a d g j
%w r y i p s f h k
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3,4,5,6,7,8),[q,w])
%OUTPUT
%1 2 3 4 5 6 7 8 q
%1 2 3 4 5 6 7 8 w
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3,4,5,6,7),[q,w],[e,r])
%OUTPUT
%1 2 3 4 5 6 7 q e
%1 2 3 4 5 6 7 w r
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3,4,5,6),[q,w],[e,r],[t,y])
%OUTPUT
%1 2 3 4 5 6 q e t
%1 2 3 4 5 6 w r y
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3,4,5),[q,w],[e,r],[t,y],[u,i])
%OUTPUT
%1 2 3 4 5 q e t u
%1 2 3 4 5 w r y i
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3,4),[q,w],[e,r],[t,y],[u,i],[o,p])
%OUTPUT
%1 2 3 4 q e t u o
%1 2 3 4 w r y i p
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2,3),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s])
%OUTPUT
%1 2 3 q e t u o a
%1 2 3 w r y i p s
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1,2),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f])
%OUTPUT
%1 2 q e t u o a d
%1 2 w r y i p s f
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate(1),[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f],[g,h])
%OUTPUT
%1 q e t u o a d g
%1 w r y i p s f h
%
%OUTPUT
%ANSWER/

%QUERY checklist(nine_arg_predicate,[q,w],[e,r],[t,y],[u,i],[o,p],[a,s],[d,f],[g,h],[j,k])
%OUTPUT
%q e t u o a d g j
%w r y i p s f h k
%
%OUTPUT
%ANSWER/
 */
/**
 * <code>maplist(X,Y)</code> / <code>maplist(X,Y,Z)</code> - determines if a goal succeeds against elements of a list.
 * <p>
 * <code>maplist(X,Y)</code> succeeds if the goal <code>X</code> can be successfully applied to each elements of the
 * list <code>Y</code>.
 * </p>
 */
public final class MapList implements PredicateFactory, PreprocessablePredicateFactory, KnowledgeBaseConsumer {
   private Predicates predicates;

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      this.predicates = kb.getPredicates();
   }

   @Override
   public PredicateFactory preprocess(Term input) {
      Term action = input.getArgument(0);
      if (PartialApplicationUtils.isAtomOrStructure(action)) {
         PredicateFactory pf = PartialApplicationUtils.getPreprocessedPartiallyAppliedPredicateFactory(predicates, action, input.getNumberOfArguments() - 1);
         return new PreprocessedMapList(pf);
      } else {
         return this;
      }
   }

   private static class PreprocessedMapList implements PredicateFactory {
      private final PredicateFactory pf;

      PreprocessedMapList(PredicateFactory pf) {
         this.pf = pf;
      }

      @Override
      public Predicate getPredicate(Term[] args) {
         return getMapListPredicate(pf, args);
      }

      @Override
      public boolean isRetryable() {
         return pf.isRetryable();
      }
   }

   @Override
   public boolean isRetryable() {
      return true;
   }

   @Override
   public Predicate getPredicate(Term[] input) {
      Term partiallyAppliedFunction = input[0];
      if (!isAtomOrStructure(partiallyAppliedFunction)) {
         return PredicateUtils.FALSE;
      }

      final PredicateFactory pf = PartialApplicationUtils.getPartiallyAppliedPredicateFactory(predicates, partiallyAppliedFunction, input.length - 1);
      return getMapListPredicate(pf, input);
   }

   private static Predicate getMapListPredicate(PredicateFactory pf, Term[] input) {
      Term partiallyAppliedFunction = input[0];

      @SuppressWarnings("unchecked")
      List<Term>[] lists = new List[input.length - 1];
      int length = -1;
      for (int i = 0; i < lists.length; i++) {
         Term t = input[i + 1];
         if (!t.getType().isVariable()) {
            List<Term> list = toJavaUtilList(t);
            if (list == null) {
               return PredicateUtils.FALSE;
            } else if (length == -1) {
               length = list.size();
            } else if (length != list.size()) {
               return PredicateUtils.FALSE;
            }
            lists[i] = list;
         }
      }
      if (length == -1) {
         return PredicateUtils.FALSE;
      }
      for (int i = 0; i < lists.length; i++) {
         lists[i] = new ArrayList<>(length);
         for (int t = 0; t < length; t++) {
            lists[i].add(new Variable());
         }
         input[i + 1].unify(ListFactory.createList(lists[i]));
      }
      if (length == 0) {
         return PredicateUtils.TRUE;
      }
      if (pf.isRetryable()) {
         return new Retryable(pf, partiallyAppliedFunction, lists);
      }
      for (int i = 0; i < lists[0].size(); i++) {
         Term[] args = new Term[lists.length];
         for (int a = 0; a < args.length; a++) {
            args[a] = lists[a].get(i);
         }
         if (!apply(pf, createArguments(partiallyAppliedFunction, args))) {
            return PredicateUtils.FALSE;
         }
      }
      return PredicateUtils.TRUE;
   }

   private static class Retryable implements Predicate {
      private final PredicateFactory pf;
      private final Term action;
      private final List<Term>[] lists;
      private final List<Predicate> predicates;
      private final List<Term[]> backtrack;
      private int idx;

      private Retryable(PredicateFactory pf, Term action, List<Term>[] lists) {
         this.pf = pf;
         this.action = action;
         this.lists = lists;
         this.predicates = new ArrayList<>(lists.length);
         this.backtrack = new ArrayList<>(lists.length);
      }

      @Override
      public boolean evaluate() {
         while (idx > -1) {
            final boolean success;
            if (predicates.size() == idx) {
               Term[] args = new Term[lists.length];
               for (int a = 0; a < args.length; a++) {
                  args[a] = lists[a].get(idx).getTerm();
               }
               Predicate p = PartialApplicationUtils.getPredicate(pf, action, args);
               success = p.evaluate();

               predicates.add(p);
               backtrack.add(args);
            } else {
               Predicate p = predicates.get(idx);
               success = p.couldReevaluationSucceed() && p.evaluate();
            }

            if (success) {
               if (idx < lists[0].size() - 1) {
                  idx++;
               } else {
                  return true;
               }
            } else {
               predicates.remove(idx);
               TermUtils.backtrack(backtrack.remove(idx));
               idx--;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         if (predicates.isEmpty()) { // if empty then has not been evaluated yet
            return true;
         }

         for (Predicate p : predicates) {
            if (p.couldReevaluationSucceed()) {
               return true;
            }
         }
         return false;
      }
   }
}
