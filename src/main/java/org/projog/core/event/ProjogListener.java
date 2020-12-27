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

import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;

public interface ProjogListener {
   /** The event generated when an attempt is first made to evaluate a goal. */
   void onCall(SpyPointEvent event);

   /** The event generated when an attempt is made to re-evaluate a goal. */
   void onRedo(SpyPointEvent event);

   /** The event generated when an attempt to evaluate a goal succeeds. */
   void onExit(SpyPointExitEvent event);

   /** The event generated when all attempts to evaluate a goal have failed. */
   void onFail(SpyPointEvent event);

   /** The event generated to warn clients of an event. */
   void onWarn(String message);

   /** The event generated to inform clients of an event. */
   void onInfo(String message);
}
