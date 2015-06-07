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
package org.projog.core.function.debug;

import static org.projog.core.KnowledgeBaseUtils.getSpyPoints;

import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractSingletonPredicate;

/* TEST
 %LINK prolog-debugging
 */
/**
 * <code>notrace</code> - disables exhaustive tracing.
 * <p>
 * By disabling exhaustive tracing the programmer will no longer be informed of every goal their program attempts to
 * resolve. Any tracing due to the presence of spy points <i>will</i> continue.
 * </p>
 */
public final class NoTrace extends AbstractSingletonPredicate {
   private SpyPoints spyPoints;

   @Override
   protected void init() {
      spyPoints = getSpyPoints(getKnowledgeBase());
   }

   @Override
   public boolean evaluate() {
      spyPoints.setTraceEnabled(false);
      return true;
   }
}
