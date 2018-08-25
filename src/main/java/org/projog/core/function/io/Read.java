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
package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getFileHandles;
import static org.projog.core.KnowledgeBaseUtils.getOperands;

import java.io.InputStreamReader;

import org.projog.core.FileHandles;
import org.projog.core.Operands;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.parser.SentenceParser;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
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
public final class Read extends AbstractSingletonPredicate {
   private FileHandles fileHandles;
   private Operands operands;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
      operands = getOperands(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term argument) {
      InputStreamReader isr = new InputStreamReader(fileHandles.getCurrentInputStream());
      SentenceParser sp = SentenceParser.getInstance(isr, operands);
      Term t = sp.parseTerm();
      return argument.unify(t);
   }
}
