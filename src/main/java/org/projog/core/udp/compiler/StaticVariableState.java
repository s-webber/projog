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
package org.projog.core.udp.compiler;

import org.projog.core.PredicateKey;

final class StaticVariableState<T> {
   private final PredicateKey predicateKey;
   private T variableType;
   private String variableName;

   StaticVariableState(PredicateKey predicateKey, T variableType, String variableName) {
      this.predicateKey = predicateKey;
      this.variableType = variableType;
      this.variableName = variableName;
   }

   PredicateKey getPredicateKey() {
      return predicateKey;
   }

   String getVariableTypeClassName() {
      return variableType.getClass().getName();
   }

   String getVariableName() {
      return variableName;
   }
}
