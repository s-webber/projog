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

final class ClauseState {
   private final List<AntecedantElementState> antecedantElementStates;
   private ClauseMetaData md;

   ClauseState(ClauseMetaData md) {
      this.md = md;

      List<AntecedentElementMetaData> elements = md.getElements();
      antecedantElementStates = new ArrayList<>(elements.size());
      for (AntecedentElementMetaData element : elements) {
         AntecedantElementState state = new AntecedantElementState(this, element);
         antecedantElementStates.add(state);
      }
   }

   AntecedantElementState getLastAntecedantElement() {
      return antecedantElementStates.get(antecedantElementStates.size() - 1);
   }

   /** Returns antecedant elements that are after the first retryable element */
   Collection<AntecedantElementState> getAntecedantElementStatesAfterFirstRetryableElement() {
      return antecedantElementStates.subList(md.getIndexOfFirstRetryableElement() + 1, antecedantElementStates.size());
   }

   /** Returns antecedant elements starting from the first retryable element */
   Collection<AntecedantElementState> getAntecedantElementStatesToRetry() {
      return antecedantElementStates.subList(md.getIndexOfFirstRetryableElement(), antecedantElementStates.size());
   }

   /** Returns antecedant elements before the first retryable element */
   Collection<AntecedantElementState> getAntecedantElementStatesToInit() {
      return antecedantElementStates.subList(0, md.getIndexOfFirstRetryableElement());
   }

   AntecedantElementState getFirstRetryableAntecedantElement() {
      return antecedantElementStates.get(md.getIndexOfFirstRetryableElement());
   }

   Collection<AntecedantElementState> getAntecedantElementStates() {
      return antecedantElementStates;
   }

   AntecedantElementState getAntecedantElementState(int i) {
      return antecedantElementStates.get(i);
   }

   int getNumberOfElements() {
      return antecedantElementStates.size();
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
