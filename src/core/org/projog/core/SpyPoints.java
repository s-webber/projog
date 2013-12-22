/*
 * Copyright 2013 S Webber
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
package org.projog.core;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.term.Term;

/**
 * Collection of spy points.
 * <p>
 * Spy points are useful in the debugging of Prolog programs. When a spy point is set on a predicate a
 * {@link ProjogEventType} is generated every time the predicate is executed, fails or succeeds.
 * </p>
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code SpyPoints} instance.
 * </p>
 * 
 * @see org.projog.core.KnowledgeBase#getSpyPoints()
 */
public final class SpyPoints {
   private final Object lock = new Object();
   private final Map<PredicateKey, SpyPoint> spyPoints = new TreeMap<PredicateKey, SpyPoint>();
   private final KnowledgeBase kb;
   private boolean traceEnabled;

   SpyPoints(KnowledgeBase kb) {
      this.kb = kb;
   }

   public void setTraceEnabled(boolean traceEnabled) {
      synchronized (lock) {
         this.traceEnabled = traceEnabled;
         for (SpyPoints.SpyPoint sp : spyPoints.values()) {
            if (traceEnabled) {
               sp.enabled = true;
            } else {
               sp.enabled = sp.set;
            }
         }
      }
   }

   public void setSpyPoint(PredicateKey key, boolean set) {
      synchronized (lock) {
         SpyPoint sp = getSpyPoint(key);
         sp.set = set;
         sp.enabled = traceEnabled || sp.set;
      }
   }

   public SpyPoint getSpyPoint(PredicateKey key) {
      SpyPoint spyPoint = spyPoints.get(key);
      if (spyPoint == null) {
         spyPoint = createNewSpyPoint(key);
      }
      return spyPoint;
   }

   private SpyPoint createNewSpyPoint(PredicateKey key) {
      synchronized (lock) {
         SpyPoint spyPoint = spyPoints.get(key);
         if (spyPoint == null) {
            spyPoint = new SpyPoint(key);
            spyPoint.enabled = traceEnabled;
            spyPoints.put(key, spyPoint);
         }
         return spyPoint;
      }
   }

   public Map<PredicateKey, SpyPoint> getSpyPoints() {
      return Collections.unmodifiableMap(spyPoints);
   }

   public class SpyPoint {
      private final PredicateKey key;
      private boolean enabled;
      private boolean set;

      private SpyPoint(PredicateKey key) {
         this.key = key;
      }

      public boolean isSet() {
         return set;
      }

      public boolean isEnabled() {
         return enabled;
      }

      /** Generates an event of type {@link ProjogEventType#CALL} */
      public void logCall(Object source, Term[] args) {
         log(ProjogEventType.CALL, source, args);
      }

      /** Generates an event of type {@link ProjogEventType#REDO} */
      public void logRedo(Object source, Term[] args) {
         log(ProjogEventType.REDO, source, args);
      }

      /** Generates an event of type {@link ProjogEventType#EXIT} */
      public void logExit(Object source, Term[] args) {
         log(ProjogEventType.EXIT, source, args);
      }

      /** Generates an event of type {@link ProjogEventType#FAIL} */
      public void logFail(Object source, Term[] args) {
         log(ProjogEventType.FAIL, source, args);
      }

      private void log(ProjogEventType type, Object source, Term[] args) {
         if (isEnabled() == false) {
            return;
         }

         StringBuilder sb = new StringBuilder();
         sb.append(key.getName());
         if (args != null) {
            sb.append("( ");
            for (int i = 0; i < args.length; i++) {
               if (i != 0) {
                  sb.append(", ");
               }
               sb.append(kb.toString(args[i]));
            }
            sb.append(" )");
         }
         ProjogEvent event = new ProjogEvent(type, sb.toString(), source);
         kb.getProjogEventsObservable().notifyObservers(event);
      }
   }
}