/*
 * Copyright 2025 S. Webber
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
package org.projog.core.predicate.udp;

import java.util.Iterator;
import java.util.List;

final class ImplicationsIterator implements Iterator<ClauseModel> {
   private final Iterator<ClauseModel> iterator;

   ImplicationsIterator(List<ClauseModel> implications) {
      iterator = implications.iterator();
   }

   @Override
   public boolean hasNext() {
      return iterator.hasNext();
   }

   /**
    * Returns a <i>new copy</i> to avoid the original being altered.
    */
   @Override
   public ClauseModel next() {
      ClauseModel clauseModel = iterator.next();
      return clauseModel.copy();
   }
}
