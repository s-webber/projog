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
package org.projog.core.event;

import java.util.HashSet;
import java.util.Set;

import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;
import org.projog.core.kb.KnowledgeBase;

/**
 * Controls the registering and notification of listeners of a {@link org.projog.core.kb.KnowledgeBase}.
 * <p>
 * Each {@link org.projog.core.kb.KnowledgeBase} has a single unique {@code ProjogListeners} instance.
 *
 * @see KnowledgeBase#getProjogListeners()
 */
public class ProjogListeners {
   private final Set<ProjogListener> listeners = new HashSet<>();

   /**
    * Adds a listener to the set of listeners.
    *
    * @param listener a listener to be added
    * @return <tt>true</tt> if this instance did not already reference the specified listener
    */
   public boolean addListener(ProjogListener listener) {
      return listeners.add(listener);
   }

   /**
    * Deletes an observer from the set of observers of this objects internal {@code Observable}.
    *
    * @param listener a listener to be deleted
    * @return <tt>true</tt> if this instance did reference the specified listener
    */
   public boolean deleteListener(ProjogListener listener) {
      return listeners.remove(listener);
   }

   /** Notify all listeners of a first attempt to evaluate a goal. */
   public void notifyCall(SpyPointEvent event) {
      for (ProjogListener listener : listeners) {
         listener.onCall(event);
      }
   }

   /** Notify all listeners of an attempt to re-evaluate a goal. */
   public void notifyRedo(SpyPointEvent event) {
      for (ProjogListener listener : listeners) {
         listener.onRedo(event);
      }
   }

   /** Notify all listeners when an attempt to evaluate a goal succeeds. */
   public void notifyExit(SpyPointExitEvent event) {
      for (ProjogListener listener : listeners) {
         listener.onExit(event);
      }
   }

   /** Notify all listeners when an attempt to evaluate a goal fails. */
   public void notifyFail(SpyPointEvent event) {
      for (ProjogListener listener : listeners) {
         listener.onFail(event);
      }
   }

   /** Notify all listeners of a warning. */
   public void notifyWarn(String message) {
      for (ProjogListener listener : listeners) {
         listener.onWarn(message);
      }
   }

   /** Notify all listeners of a general information event. */
   public void notifyInfo(String message) {
      for (ProjogListener listener : listeners) {
         listener.onInfo(message);
      }
   }
}
