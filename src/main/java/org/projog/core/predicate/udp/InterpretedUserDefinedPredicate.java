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
package org.projog.core.predicate.udp;

import java.util.Iterator;

import org.projog.core.ProjogException;
import org.projog.core.event.SpyPoints;
import org.projog.core.predicate.CutException;
import org.projog.core.predicate.Predicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/**
 * Represents a user defined predicate.
 *
 * @see #evaluate()
 */
public final class InterpretedUserDefinedPredicate implements Predicate {
   private final Iterator<ClauseAction> clauseActions;
   private final SpyPoints.SpyPoint spyPoint;
   private final Term[] queryArgs;
   private final boolean debugEnabled;

   private ClauseAction currentClause;
   private Predicate currentPredicate;
   private boolean retryCurrentClauseAction;

   public InterpretedUserDefinedPredicate(Iterator<ClauseAction> clauseActions, SpyPoints.SpyPoint spyPoint, Term[] queryArgs) {
      this.clauseActions = clauseActions;
      this.spyPoint = spyPoint;
      this.queryArgs = queryArgs;
      this.debugEnabled = spyPoint.isEnabled();
   }

   /**
    * Evaluates a user defined predicate.
    * <p>
    * The process for evaluating a user defined predicate is as follows:
    * <ul>
    * <li>Iterates through every clause of the user defined predicate.</li>
    * <li>For each clause it attempts to unify the arguments in its head (consequent) with the arguments in the query (
    * {@code queryArgs}).</li>
    * <li>If the head of the clause can be unified with the query then an attempt is made to evaluate the body
    * (antecedent) of the clause.</li>
    * <li>If the body of the clause is successfully evaluated then {@code true} is returned.</li>
    * <li>If the body of the clause is not successfully evaluated then the arguments in the query are backtracked.</li>
    * <li>When there are no more clauses left to check then {@code false} is returned.</li>
    * </ul>
    * Once {@code evaluate()} has returned {@code true} subsequent invocations of {@code evaluate()} will attempt to
    * re-evaluate the antecedent of the previously successfully evaluated clause. If the body of the clause is
    * successfully re-evaluated then {@code true} is returned. If the body of the clause is not successfully
    * re-evaluated then the arguments in the query are backtracked and the method continues to iterate through the
    * clauses starting with the next clause in the sequence.
    */
   @Override
   public boolean evaluate() {
      try {
         if (retryCurrentClauseAction) {
            if (debugEnabled) {
               spyPoint.logRedo(this, queryArgs);
            }
            if (currentPredicate.evaluate()) {
               retryCurrentClauseAction = currentPredicate.couldReevaluationSucceed();
               if (debugEnabled) {
                  spyPoint.logExit(this, queryArgs, currentClause.getModel());
               }
               return true;
            }
            // attempt at retrying has failed so discard it
            retryCurrentClauseAction = false;
            TermUtils.backtrack(queryArgs);
         } else if (currentClause == null) {
            if (debugEnabled) {
               spyPoint.logCall(this, queryArgs);
            }
         } else {
            if (debugEnabled) {
               spyPoint.logRedo(this, queryArgs);
            }
            TermUtils.backtrack(queryArgs);
         }
         // cycle though all rules until none left
         while (clauseActions.hasNext()) {
            currentClause = clauseActions.next();
            currentPredicate = currentClause.getPredicate(queryArgs);
            if (currentPredicate != null && currentPredicate.evaluate()) {
               retryCurrentClauseAction = currentPredicate.couldReevaluationSucceed();
               if (debugEnabled) {
                  spyPoint.logExit(this, queryArgs, currentClause.getModel());
               }
               return true;
            } else {
               retryCurrentClauseAction = false;
               TermUtils.backtrack(queryArgs);
            }
         }
         if (debugEnabled) {
            spyPoint.logFail(this, queryArgs);
         }
         return false;
      } catch (CutException e) {
         if (debugEnabled) {
            spyPoint.logFail(this, queryArgs);
         }
         return false;
      } catch (ProjogException pe) {
         pe.addClause(currentClause.getModel());
         throw pe;
      } catch (Throwable t) {
         ProjogException pe = new ProjogException("Exception processing: " + spyPoint.getPredicateKey(), t);
         pe.addClause(currentClause.getModel());
         throw pe;
      }
   }

   @Override
   public boolean couldReevaluationSucceed() {
      if (currentClause != null && currentClause.isAlwaysCutOnBacktrack()) {
         return false;
      } else {
         return retryCurrentClauseAction || clauseActions.hasNext();
      }
   }
}
