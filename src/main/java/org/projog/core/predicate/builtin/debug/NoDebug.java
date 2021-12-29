/*
 * Copyright 2013 S. Webber
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
package org.projog.core.predicate.builtin.debug;

import java.util.Map;

import org.projog.core.event.SpyPoints;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;

/* TEST
%LINK prolog-debugging
*/
/**
 * <code>nodebug</code> - removes all current spy points.
 */
public final class NoDebug extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate() {
      SpyPoints spyPoints = getSpyPoints();
      Map<PredicateKey, SpyPoints.SpyPoint> map = spyPoints.getSpyPoints();
      for (Map.Entry<PredicateKey, SpyPoints.SpyPoint> e : map.entrySet()) {
         spyPoints.setSpyPoint(e.getKey(), false);
      }
      return true;
   }
}
