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
package org.projog.core.function.io;

import java.io.InputStreamReader;

import org.projog.core.parser.SentenceParser;
import org.projog.core.term.Term;

/* SYSTEM TEST
 % %LINK% prolog-io
 */
/**
 * <code>read(X)</code> - reads a term from the input stream.
 * <p>
 * <code>read(X)</code> reads the next term from the input stream and matches it with <code>X</code>.
 * </p>
 * <p>
 * Succeeds only once.
 * </p>
 */
public final class Read extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term argument) {
      InputStreamReader isr = new InputStreamReader(getKnowledgeBase().getFileHandles().getCurrentInputStream());
      SentenceParser sp = SentenceParser.getInstance(isr, getKnowledgeBase().getOperands());
      Term t = sp.parseTerm();
      return argument.unify(t);
   }
}