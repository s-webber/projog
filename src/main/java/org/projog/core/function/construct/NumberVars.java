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
package org.projog.core.function.construct;

import java.util.LinkedHashMap;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* TEST
%QUERY numbervars(a,0,Y)
%ANSWER Y=0

%QUERY numbervars(X,0,Y)
%ANSWER
% X=$VAR(0)
% Y=1
%ANSWER

%QUERY numbervars(X,42,Y)
%ANSWER
% X=$VAR(42)
% Y=43
%ANSWER

%QUERY X=p(A,B,p(C,B,C,D,[E,A,F,F,G,D])), numbervars(X,0,Y)
%ANSWER
% A=$VAR(0)
% B=$VAR(1)
% C=$VAR(2)
% D=$VAR(3)
% E=$VAR(4)
% F=$VAR(5)
% G=$VAR(6)
% X=p($VAR(0), $VAR(1), p($VAR(2), $VAR(1), $VAR(2), $VAR(3), [$VAR(4),$VAR(0),$VAR(5),$VAR(5),$VAR(6),$VAR(3)]))
% Y=7
%ANSWER

%QUERY X=p(A,B,p(C,B,C,D,[E,A,F,F,G,D])), numbervars(X,42,Y)
%ANSWER
% A=$VAR(42)
% B=$VAR(43)
% C=$VAR(44)
% D=$VAR(45)
% E=$VAR(46)
% F=$VAR(47)
% G=$VAR(48)
% X=p($VAR(42), $VAR(43), p($VAR(44), $VAR(43), $VAR(44), $VAR(45), [$VAR(46),$VAR(42),$VAR(47),$VAR(47),$VAR(48),$VAR(45)]))
% Y=49
%ANSWER

% numbervars(X) operates in the same way as numbervars(X,0,_)

%QUERY X=p(A,B,p(C,B,C,D,[E,A,F,F,G,D])), numbervars(X)
%ANSWER
% A=$VAR(0)
% B=$VAR(1)
% C=$VAR(2)
% D=$VAR(3)
% E=$VAR(4)
% F=$VAR(5)
% G=$VAR(6)
% X=p($VAR(0), $VAR(1), p($VAR(2), $VAR(1), $VAR(2), $VAR(3), [$VAR(4),$VAR(0),$VAR(5),$VAR(5),$VAR(6),$VAR(3)]))
%ANSWER
 */
/**
 * <code>numbervars(Term,Start,End)</code> - unifies free variables of a term.
 * <p>
 * Unifies the free variables in <code>Term</code> with a structure of the form <code>$VAR(N)</code> where
 * <code>N</code> is the number of the variable. Numbering of the variables starts with the numeric value represented by
 * <code>Start</code>. <code>End</code> is unified with the number that would of been given to the next variable.
 * </p>
 */
public final class NumberVars extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term arg) {
      numberVars(arg, 0);
      return true;
   }

   @Override
   public boolean evaluate(Term arg1, Term arg2, Term arg3) {
      long start = TermUtils.castToNumeric(arg2).getLong();
      long end = numberVars(arg1, start);
      return arg3.unify(new IntegerNumber(end));
   }

   private long numberVars(Term term, long start) {
      LinkedHashMap<Variable, Variable> sharedVariables = new LinkedHashMap<>();
      term.copy(sharedVariables);

      long ctr = start;
      for (Variable v : sharedVariables.keySet()) {
         v.unify(Structure.createStructure("$VAR", new Term[] {new IntegerNumber(ctr)}));
         ctr++;
      }
      return ctr;
   }
}
