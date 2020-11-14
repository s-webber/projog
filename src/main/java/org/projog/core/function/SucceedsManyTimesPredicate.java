/*
 * Copyright 2020 S. Webber
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
package org.projog.core.function;

public class SucceedsManyTimesPredicate implements SucceedsFixedAmountPredicate {
   private final int limit;
   private int ctr;

   /** @param limit the number of times to successfully evaluate */
   public SucceedsManyTimesPredicate(int limit) {
      this.limit = limit;
   }

   @Override
   public boolean evaluate() {
      if (ctr < limit) {
         ctr++;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean couldReevaluationSucceed() {
      return ctr < limit;
   }

   @Override
   public SucceedsManyTimesPredicate getFree() {
      return new SucceedsManyTimesPredicate(limit);
   }

   @Override
   public SucceedsManyTimesPredicate increment() {
      return new SucceedsManyTimesPredicate(limit + 1);
   }

   @Override
   public int getCount() {
      return limit;
   }
}
