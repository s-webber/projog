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
package org.projog.core.predicate;

/**
 * A skeletal implementation of {@link Predicate}
 *
 * @deprecated Only contains one method - {@link #couldReevaluationSucceed()}. Recommended that classes implement the
 * {@link Predicate} interface directly rather than extend this class.
 */
@Deprecated
public abstract class AbstractPredicate implements Predicate {
   /** Always returns {@code true}. */
   @Override
   public boolean couldReevaluationSucceed() {
      return true;
   }
}
