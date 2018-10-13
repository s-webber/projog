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

/**
 * Represents an event that has occurred during the evaluation of Prolog goals.
 *
 * @see ProjogEventsObservable#notifyObservers(ProjogEvent)
 */
public class ProjogEvent {
   private final ProjogEventType type;
   private final Object message;
   private final Object source;

   /**
    * @param message a description of the event
    * @param source the object that generated the event
    */
   public ProjogEvent(ProjogEventType type, Object message, Object source) {
      this.type = type;
      this.message = message;
      this.source = source;
   }

   public ProjogEventType getType() {
      return type;
   }

   /**
    * Returns details of the event.
    */
   public Object getDetails() { // TODO rename
      return message;
   }

   /**
    * Returns the description of the event.
    *
    * @deprecated use {{@link #getDetails()} instead
    */
   @Deprecated
   public String getMessage() { // TODO rename
      return message.toString();
   }

   /**
    * Returns the object that generated this event.
    */
   public Object getSource() {
      return source;
   }
}
