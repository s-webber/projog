/*
 * Copyright 2013 S Webber
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
package org.projog.core.function.kb;

import org.projog.core.udp.ClauseModel;
import org.projog.core.udp.UserDefinedPredicateFactory;

/* SYSTEM TEST
 % %QUERY% X=p(1), asserta(X), asserta(p(2)), asserta(p(3))
 % %ANSWER% X=p(1)
 % %QUERY% p(X)
 % %ANSWER% X=3
 % %ANSWER% X=2
 % %ANSWER% X=1

 % %QUERY% retract(p(X))
 % %ANSWER% X=3
 % %ANSWER% X=2
 % %ANSWER% X=1
 */
/**
 * <code>asserta(X)</code> - adds a clause to the front of the knowledge base.
 * <p>
 * <code>asserta(X)</code> adds the clause <code>X</code> to the front of the knowledge base. <code>X</code> must be
 * suitably instantiated that the predicate of the clause can be determined.
 * </p>
 * <p>
 * This is <i>not</i> undone as part of backtracking.
 * </p>
 */
public final class AssertA extends AbstractAssertFunction {
   @Override
   protected void add(UserDefinedPredicateFactory userDefinedPredicate, ClauseModel clauseModel) {
      userDefinedPredicate.addFirst(clauseModel);
   }
}