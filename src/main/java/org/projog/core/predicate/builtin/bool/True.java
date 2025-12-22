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
package org.projog.core.predicate.builtin.bool;

import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.udp.PredicateUtils;
import org.projog.core.term.Term;

/* TEST
%TRUE true
*/
/**
 * <code>true</code> - always succeeds.
 * <p>
 * The goal <code>true</code> always succeeds.
 * </p>
 */
public final class True implements PredicateFactory {
   @Override
   public Predicate getPredicate(Term term) {
      return PredicateUtils.TRUE;
   }

   @Override
   public boolean isRetryable() {
      return false;
   }
}
