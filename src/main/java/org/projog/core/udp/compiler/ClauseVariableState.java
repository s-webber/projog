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

import org.projog.core.udp.compiler.model.ClauseVariableMetaData;

/** Associates a Java variable name to a Prolog variable for the purpose of translating Prolog predicates to Java. */
final class ClauseVariableState {
   // TODO should some of the logic contained in this class be moved to ClauseVariableMetaData?
   private final String javaVariableName;
   private final ClauseVariableMetaData cmdv;

   ClauseVariableState(String javaVariableName, ClauseVariableMetaData cmdv) {
      this.javaVariableName = javaVariableName;
      this.cmdv = cmdv;
   }

   boolean isMemberVariable() {
      return cmdv.isMemberVariable();
   }

   String getJavaVariableName() {
      return javaVariableName;
   }

   boolean isVariableTerm() {
      return cmdv.previous() == null;
   }

   String getJavaType() {
      return isVariableTerm() ? "Variable" : "Term";
   }
}
