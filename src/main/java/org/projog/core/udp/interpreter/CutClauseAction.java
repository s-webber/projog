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
package org.projog.core.udp.interpreter;

import java.util.Map;

import org.projog.core.CutException;
import org.projog.core.KnowledgeBase;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/**
 * A clause whose body consists of a single cut ({@code !}) predicate.
 * <p>
 * e.g. {@code p(a,b,c) :- !.}
 * 
 * @see org.projog.core.function.flow.Cut
 */
public final class CutClauseAction extends AbstractMultiAnswerClauseAction {
   CutClauseAction(KnowledgeBase kb, Term[] consequentArgs) {
      super(kb, consequentArgs);
   }

   private CutClauseAction(CutClauseAction action) {
      super(action);
   }

   @Override
   protected boolean evaluateAntecedant(Map<Variable, Variable> sharedVariables) {
      return true;
   }

   @Override
   protected boolean reEvaluateAntecedant() {
      throw CutException.CUT_EXCEPTION;
   }

   @Override
   public CutClauseAction getFree() {
      return new CutClauseAction(this);
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return true;
   }
}
