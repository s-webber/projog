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
package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getCalculatables;
import static org.projog.core.KnowledgeBaseUtils.getFileHandles;

import java.io.PrintStream;

import org.projog.core.Calculatables;
import org.projog.core.FileHandles;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %QUERY write(start), tab(1), write(finish)
 %OUTPUT start finish
 %ANSWER/

 %QUERY write(start), tab(2), write(finish)
 %OUTPUT start  finish
 %ANSWER/

 %QUERY write(start), tab(3), write(finish)
 %OUTPUT start   finish
 %ANSWER/

 %QUERY write(start), tab(32), write(finish)
 %OUTPUT start                                finish
 %ANSWER/

 %QUERY write(start), tab(3+4), write(finish)
 %OUTPUT start       finish
 %ANSWER/

 %QUERY write(start), tab(0), write(finish)
 %OUTPUT startfinish
 %ANSWER/

 %QUERY write(start), tab(-1), write(finish)
 %OUTPUT startfinish
 %ANSWER/

 %QUERY write(start), tab(3.5), write(finish)
 %OUTPUT start   finish
 %ANSWER/
 */
/**
 * <code>tab(X)</code> - writes <code>X</code> number of spaces to the output stream.
 */
public final class Tab extends AbstractSingletonPredicate {
   private FileHandles fileHandles;
   private Calculatables calculatables;

   @Override
   protected void init() {
      fileHandles = getFileHandles(getKnowledgeBase());
      calculatables = getCalculatables(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term arg) {
      long numberOfSpaces = calculatables.getNumeric(arg).getLong();
      PrintStream os = fileHandles.getCurrentOutputStream();
      for (int i = 0; i < numberOfSpaces; i++) {
         os.print(' ');
      }
      return true;
   }
}
