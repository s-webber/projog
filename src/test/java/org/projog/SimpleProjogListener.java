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
package org.projog;

import java.util.ArrayList;
import java.util.List;

import org.projog.core.event.ProjogListener;
import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;

/** Used by tests to monitor events. */
public class SimpleProjogListener implements ProjogListener {
   private final List<String> events = new ArrayList<>();

   @Override
   public void onInfo(String message) {
      throw new UnsupportedOperationException(message);
   }

   @Override
   public void onWarn(String message) {
      throw new UnsupportedOperationException(message);
   }

   @Override
   public void onRedo(SpyPointEvent event) {
      add("REDO", event);
   }

   @Override
   public void onFail(SpyPointEvent event) {
      add("FAIL", event);
   }

   @Override
   public void onExit(SpyPointExitEvent event) {
      add("EXIT", event);
   }

   @Override
   public void onCall(SpyPointEvent event) {
      add("CALL", event);
   }

   private void add(String level, SpyPointEvent event) {
      events.add(level + event);
   }

   public boolean isEmpty() {
      return events.isEmpty();
   }

   public String get(int index) {
      return events.get(index);
   }

   public int size() {
      return events.size();
   }

   public String result() {
      StringBuilder result = new StringBuilder();
      for (String event : events) {
         result.append(event);
      }
      return result.toString();
   }
}
