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
package org.projog.core.event;

import java.io.PrintStream;

import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;

public class LoggingProjogListener implements ProjogListener {
   private final PrintStream out;

   public LoggingProjogListener(PrintStream out) {
      this.out = out;
   }

   @Override
   public void onCall(SpyPointEvent event) {
      log("CALL", event);
   }

   @Override
   public void onRedo(SpyPointEvent event) {
      log("REDO", event);
   }

   @Override
   public void onExit(SpyPointExitEvent event) {
      log("EXIT", event);
   }

   @Override
   public void onFail(SpyPointEvent event) {
      log("FAIL", event);
   }

   @Override
   public void onWarn(String message) {
      log("WARN " + message);
   }

   @Override
   public void onInfo(String message) {
      log("INFO " + message);
   }

   private void log(String level, SpyPointEvent event) {
      log("[" + event.getSourceId() + "] " + level + " " + event.getFormattedTerm());
   }

   private void log(String message) {
      out.println(message);
   }
}
