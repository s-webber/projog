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

import org.projog.core.FileHandles;
import org.projog.core.function.AbstractSingletonPredicate;

/* TEST
 %QUERY write('a'), write('b'), nl, write('c')
 %OUTPUT
 % ab
 % c
 %OUTPUT
 %ANSWER/
 */
/**
 * <code>nl</code> - outputs a new line character.
 * <p>
 * Causes a line break to be output to the current stream.
 * </p>
 * <p>
 * This goal succeeds only once.
 * </p>
 */
public final class NewLine extends AbstractSingletonPredicate {
   private FileHandles fileHandles;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
   }

   @Override
   public boolean evaluate() {
      fileHandles.getCurrentOutputStream().println();
      return true;
   }
}
