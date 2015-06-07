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
package org.projog.core.function.db;

import static org.projog.core.KnowledgeBaseServiceLocator.getServiceLocator;

import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
 % Add three records to the recorded database.
 %TRUE recordz(k,a,_), recordz(k,b,_), recordz(k,c,_)
 
 % Confirm the records have been added.
 %QUERY recorded(k,X)
 %ANSWER X=a
 %ANSWER X=b
 %ANSWER X=c
 
 % Erase (i.e. remove) a record. 
 %QUERY recorded(k,b,X), erase(X)
 %ANSWER X=1
 %NO
 
 % Confirm the record has been removed.
 %QUERY recorded(k,X)
 %ANSWER X=a
 %ANSWER X=c
 */
/**
 * <code>erase(X)</code> - removes a record from the recorded database.
 * <p>
 * Removes from the recorded database the term associated with the reference specified by <code>X</code>. The goal
 * succeeds even if there is no term associated with the specified reference.
 */
public final class Erase extends AbstractSingletonPredicate {
   private RecordedDatabase database;

   @Override
   protected void init() {
      database = getServiceLocator(getKnowledgeBase()).getInstance(RecordedDatabase.class);
   }

   @Override
   public boolean evaluate(Term arg) {
      Numeric reference = TermUtils.castToNumeric(arg);
      database.erase(reference.getLong());
      return true;
   }
}
