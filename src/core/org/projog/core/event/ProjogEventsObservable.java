/*
 * Copyright 2013-2014 S. Webber
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

import java.util.Observable;
import java.util.Observer;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;

/**
 * Controls the registering and notification of observers of a {@link org.projog.core.KnowledgeBase}.
 * <p>
 * Each {@code ProjogEventsObservable} has its own internal {@code java.util.Observable} that it delegates to.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code ProjogEventsObservable} instance.
 * 
 * @see KnowledgeBaseUtils#getProjogEventsObservable(KnowledgeBase)
 */
public class ProjogEventsObservable {
   private final Observable observable = new Observable() {
      @Override
      public void notifyObservers(Object arg) {
         super.setChanged();
         super.notifyObservers(arg);
      }
   };

   /**
    * Adds an observer to the set of observers for this objects internal {@code Observable}.
    * 
    * @param observer an observer to be added
    */
   public void addObserver(Observer observer) {
      observable.addObserver(observer);
   }

   /**
    * Deletes an observer from the set of observers of this objects internal {@code Observable}.
    * 
    * @param observer an observer to be deleted
    */
   public void deleteObserver(Observer observer) {
      observable.deleteObserver(observer);
   }

   /**
    * Notify all observers.
    * <p>
    * Each observer has its <code>update</code> method called with two arguments: this objects internal
    * {@code Observable} object and the <code>event</code> argument.
    */
   public void notifyObservers(ProjogEvent event) {
      observable.notifyObservers(event);
   }
}
