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
package org.projog.core.function.debug;

import static org.projog.core.KnowledgeBaseUtils.getPredicateKeysByName;
import static org.projog.core.KnowledgeBaseUtils.getSpyPoints;

import java.util.List;

import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.SpyPoints;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-debugging
 */
/**
 * <code>spy X</code> / <code>nospy X</code> - add or remove a spy point for a predicate.
 * <p>
 * <code>spy X</code> - add a spy point for a predicate. By adding a spy point for the predicate name instantiated to
 * <code>X</code> the programmer will be informed how it is used in the resolution of a goal.
 * </p>
 * <p>
 * <code>nospy X</code> - removes a spy point for a predicate. By removing a spy point for the predicate name
 * instantiated to <code>X</code> the programmer will no longer be informed how it is used in the resolution of a goal.
 * </p>
 */
public final class AlterSpyPoint extends AbstractSingletonPredicate {
   public static AlterSpyPoint spy() {
      return new AlterSpyPoint(true);
   }

   public static AlterSpyPoint noSpy() {
      return new AlterSpyPoint(false);
   }

   private final boolean valueToSetSpyPointTo;
   private SpyPoints spyPoints;

   /**
    * The {@code valueToSetSpyPointTo} parameter specifies whether spy points matched by the {@link #evaluate(Term)}
    * method should be enabled or disabled.
    * 
    * @param valueToSetSpyPointTo {@code true} to enable spy points, {@code false} to disable spy points
    */
   private AlterSpyPoint(boolean valueToSetSpyPointTo) {
      this.valueToSetSpyPointTo = valueToSetSpyPointTo;
   }

   @Override
   protected void init() {
      spyPoints = getSpyPoints(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term t) {
      switch (t.getType()) {
         case ATOM:
            List<PredicateKey> keys = getPredicateKeysByName(getKnowledgeBase(), t.getName());
            setSpyPoints(keys);
            break;
         case STRUCTURE:
            PredicateKey key = PredicateKey.createFromNameAndArity(t);
            setSpyPoint(key);
            break;
         default:
            throw new ProjogException("Expected an atom or a structure but got a " + t.getType() + " with value: " + t);
      }
      return true;
   }

   private void setSpyPoints(List<PredicateKey> keys) {
      for (PredicateKey key : keys) {
         setSpyPoint(key);
      }
   }

   private void setSpyPoint(PredicateKey key) {
      spyPoints.setSpyPoint(key, valueToSetSpyPointTo);
   }
}
