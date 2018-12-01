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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.projog.core.udp.compiler.model.AntecedentElementMetaData;
import org.projog.core.udp.compiler.model.ClauseMetaData;
import org.projog.core.udp.compiler.model.ConsequentMetaData;

/** Contains state used in the generation of Java source code to represent a clause. */
final class ClauseState {
   private final List<AntecedentElementState> antecedentElementStates;
   private ClauseMetaData md;

   ClauseState(ClauseMetaData md) {
      this.md = md;

      List<AntecedentElementMetaData> elements = md.getElements();
      antecedentElementStates = new ArrayList<>(elements.size());
      for (AntecedentElementMetaData element : elements) {
         AntecedentElementState state = new AntecedentElementState(this, element);
         antecedentElementStates.add(state);
      }
   }

   AntecedentElementState getLastAntecedentElement() {
      return antecedentElementStates.get(antecedentElementStates.size() - 1);
   }

   /** Returns antecedent elements that are after the first retryable element */
   Collection<AntecedentElementState> getAntecedentElementStatesAfterFirstRetryableElement() {
      return antecedentElementStates.subList(md.getIndexOfFirstRetryableElement() + 1, antecedentElementStates.size());
   }

   /** Returns antecedent elements starting from the first retryable element */
   Collection<AntecedentElementState> getAntecedentElementStatesToRetry() {
      return antecedentElementStates.subList(md.getIndexOfFirstRetryableElement(), antecedentElementStates.size());
   }

   /** Returns antecedent elements before the first retryable element */
   Collection<AntecedentElementState> getAntecedentElementStatesToInit() {
      return antecedentElementStates.subList(0, md.getIndexOfFirstRetryableElement());
   }

   AntecedentElementState getFirstRetryableAntecedentElement() {
      return antecedentElementStates.get(md.getIndexOfFirstRetryableElement());
   }

   Collection<AntecedentElementState> getAntecedentElementStates() {
      return antecedentElementStates;
   }

   AntecedentElementState getAntecedentElementState(int i) {
      return antecedentElementStates.get(i);
   }

   int getNumberOfElements() {
      return antecedentElementStates.size();
   }

   boolean hasCutWhichStopsReevaluation() {
      return md.hasCutWhichStopsReevaluation();
   }

   boolean hasRetryableElements() {
      return md.hasRetryableElements();
   }

   int getClauseIdx() {
      return md.getClauseIdx();
   }

   ConsequentMetaData getConsequentElement() {
      return md.getConsequentElement();
   }

   boolean containsCut() {
      return md.containsCut();
   }
}
