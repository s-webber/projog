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

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;
import org.projog.core.term.TermUtils;

/**
 * Collection of spy points.
 * <p>
 * Spy points are useful in the debugging of Prolog programs. When a spy point is set on a predicate a
 * {@link SpyPointEvent} is generated every time the predicate is executed, fails or succeeds.
 * </p>
 * <p>
 * Each {@link org.projog.core.kb.KnowledgeBase} has a single unique {@code SpyPoints} instance.
 * </p>
 *
 * @see KnowledgeBase#getSpyPoints()
 */
public final class SpyPoints {
   private final Object lock = new Object();
   private final Map<PredicateKey, SpyPoint> spyPoints = new TreeMap<>(); // TODO make concurrent?
   private final KnowledgeBase kb;
   private final ProjogListeners projogListeners;
   private final TermFormatter termFormatter;
   private boolean traceEnabled;

   public SpyPoints(KnowledgeBase kb) {
      this.kb = kb;
      this.projogListeners = kb.getProjogListeners();
      this.termFormatter = kb.getTermFormatter();
   }

   public SpyPoints(ProjogListeners observable, TermFormatter termFormatter) { // TODO only used by tests - remove
      this.kb = null;
      this.projogListeners = observable;
      this.termFormatter = termFormatter;
   }

   public void setTraceEnabled(boolean traceEnabled) {
      this.traceEnabled = traceEnabled;
   }

   public void setSpyPoint(PredicateKey key, boolean set) {
      synchronized (lock) {
         SpyPoint sp = getSpyPoint(key);
         sp.set = set;
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
      private boolean set;

      private SpyPoint(PredicateKey key) {
         this.key = key;
      }

      public PredicateKey getPredicateKey() {
         return key;
      }

      public boolean isSet() {
         return set;
      }

      public boolean isEnabled() {
         return set || traceEnabled;
      }

      /** Notifies listeners of a first attempt to evaluate a goal. */
      public void logCall(Object source, Term[] args) {
         if (isEnabled() == false) {
            return;
         }

         projogListeners.notifyCall(new SpyPointEvent(key, args, source));
      }

      /** Notifies listeners of an attempt to re-evaluate a goal. */
      public void logRedo(Object source, Term[] args) {
         if (isEnabled() == false) {
            return;
         }

         projogListeners.notifyRedo(new SpyPointEvent(key, args, source));
      }

      /** Notifies listeners of that an attempt to evaluate a goal has succeeded. */
      @Deprecated
      public void logExit(Object source, Term[] args, int clauseNumber) {
         ClauseModel clauseModel;
         if (clauseNumber != -1) {
            Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = kb.getPredicates().getUserDefinedPredicates();
            UserDefinedPredicateFactory userDefinedPredicate = userDefinedPredicates.get(getPredicateKey());
            // clauseNumber starts at 1 / getClauseModel starts at 0
            clauseModel = userDefinedPredicate.getClauseModel(clauseNumber - 1);
         } else {
            clauseModel = null;
         }

         logExit(source, args, clauseModel);
      }

      /** Notifies listeners of that an attempt to evaluate a goal has succeeded. */
      public void logExit(Object source, Term[] args, ClauseModel clause) {
         if (isEnabled() == false) {
            return;
         }

         projogListeners.notifyExit(new SpyPointExitEvent(key, args, source, clause));
      }

      /** Notifies listeners of that an attempt to evaluate a goal has failed. */
      public void logFail(Object source, Term[] args) {
         if (isEnabled() == false) {
            return;
         }

         projogListeners.notifyFail(new SpyPointEvent(key, args, source));
      }
   }

   public class SpyPointEvent {
      private final PredicateKey key;
      private final Term[] args;
      private final Object source;

      private SpyPointEvent(PredicateKey key, Term[] args, Object source) {
         this.key = key;
         this.args = TermUtils.copy(args);
         this.source = source;
      }

      public PredicateKey getPredicateKey() {
         return key;
      }

      public String getFormattedTerm() {
         if (args.length == 0) {
            return key.getName();
         } else {
            Term term = Structure.createStructure(key.getName(), args);
            return termFormatter.formatTerm(term);
         }
      }

      public int getSourceId() {
         return source.hashCode();
      }

      @Override
      public String toString() {
         return getFormattedTerm();
      }
   }

   public class SpyPointExitEvent extends SpyPointEvent {
      private final ClauseModel clauseModel;

      private SpyPointExitEvent(PredicateKey key, Term[] args, Object source, ClauseModel clauseModel) {
         super(key, args, source);
         this.clauseModel = clauseModel;
      }

      public String getFormattedClause() {
         return termFormatter.formatTerm(clauseModel.getOriginal());
      }

      public ClauseModel getClauseModel() {
         return clauseModel;
      }
   }
}
