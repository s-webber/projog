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
package org.projog.core.function.kb;

import static org.projog.core.KnowledgeBaseUtils.getProjogEventsObservable;
import static org.projog.core.term.TermUtils.getAtomName;

import java.util.HashSet;
import java.util.Set;

import org.projog.core.ProjogSourceReader;
import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>ensure_loaded(X)</code> - reads clauses and goals from a file.
 * <p>
 * <code>ensure_loaded(X)</code> reads clauses and goals from a file. <code>X</code> must be instantiated to the name of
 * a text file containing Prolog clauses and goals which will be added to the knowledge base. Will do nothing when
 * <code>X</code> represents a file that has already been loaded using <code>ensure_loaded(X)</code>.
 * </p>
 */
public final class EnsureLoaded extends AbstractSingletonPredicate {
   private final Object lock = new Object();

   private final Set<String> loadedResources = new HashSet<>();

   @Override
   public boolean evaluate(Term arg) {
      String resourceName = getResourceName(arg);
      synchronized (lock) {
         if (loadedResources.contains(resourceName)) {
            ProjogEvent event = new ProjogEvent(ProjogEventType.INFO, "Already loaded: " + resourceName, this);
            getProjogEventsObservable(getKnowledgeBase()).notifyObservers(event);
         } else {
            ProjogSourceReader.parseResource(getKnowledgeBase(), resourceName);
            loadedResources.add(resourceName);
         }
      }
      return true;
   }

   private String getResourceName(Term arg) {
      String resourceName = getAtomName(arg);
      if (resourceName.indexOf('.') == -1) {
         return resourceName + ".pl";
      } else {
         return resourceName;
      }
   }
}
