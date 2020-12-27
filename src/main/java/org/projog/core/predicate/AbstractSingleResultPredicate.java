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
package org.projog.core.predicate;

import static org.projog.core.predicate.udp.PredicateUtils.toPredicate;

import org.projog.core.event.ProjogListeners;
import org.projog.core.event.SpyPoints;
import org.projog.core.io.FileHandles;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.kb.KnowledgeBaseConsumer;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.parser.Operands;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

/**
 * Superclass of "plug-in" predicates that are not re-evaluated as part of backtracking.
 * <p>
 * Provides a skeletal implementation of {@link PredicateFactory} and {@link Predicate}. No attempt to find multiple
 * solutions will be made as part of backtracking as {@link #isRetryable()} always returns {@code false}.
 */
public abstract class AbstractSingleResultPredicate implements PredicateFactory, KnowledgeBaseConsumer {
   private KnowledgeBase knowledgeBase;

   @Override
   public final Predicate getPredicate(Term[] args) {
      boolean result = evaluate(args);
      return toPredicate(result);
   }

   public final boolean evaluate(Term[] args) {
      switch (args.length) {
         case 0:
            return evaluate();
         case 1:
            return evaluate(args[0]);
         case 2:
            return evaluate(args[0], args[1]);
         case 3:
            return evaluate(args[0], args[1], args[2]);
         case 4:
            return evaluate(args[0], args[1], args[2], args[3]);
         default:
            throw createWrongNumberOfArgumentsException(args.length);
      }
   }

   protected boolean evaluate() {
      throw createWrongNumberOfArgumentsException(0);
   }

   protected boolean evaluate(Term arg) {
      throw createWrongNumberOfArgumentsException(1);
   }

   protected boolean evaluate(Term arg1, Term arg2) {
      throw createWrongNumberOfArgumentsException(2);
   }

   protected boolean evaluate(Term arg1, Term arg2, Term arg3) {
      throw createWrongNumberOfArgumentsException(3);
   }

   protected boolean evaluate(Term arg1, Term arg2, Term arg3, Term arg4) {
      throw createWrongNumberOfArgumentsException(4);
   }

   private IllegalArgumentException createWrongNumberOfArgumentsException(int numberOfArguments) {
      throw new IllegalArgumentException("The predicate factory: " + getClass().getName() + " does next accept the number of arguments: " + numberOfArguments);
   }

   @Override
   public final boolean isRetryable() {
      return false;
   }

   @Override
   public final void setKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
      init();
   }

   /**
    * This method is called by {@link #setKnowledgeBase(KnowledgeBase)}.
    * <p>
    * Can be overridden by subclasses to perform initialisation before any calls to {@link #evaluate(Term...)} are made.
    * As {@link #setKnowledgeBase(KnowledgeBase)} will have already been called before this method is invoked,
    * overridden versions will be able to access the {@code KnowledgeBase} using {@link #getKnowledgeBase()}.
    */
   protected void init() {
   }

   protected final KnowledgeBase getKnowledgeBase() {
      return knowledgeBase;
   }

   protected final Predicates getPredicates() {
      return knowledgeBase.getPredicates();
   }

   protected final ArithmeticOperators getArithmeticOperators() {
      return knowledgeBase.getArithmeticOperators();
   }

   protected final ProjogListeners getProjogListeners() {
      return knowledgeBase.getProjogListeners();
   }

   protected final Operands getOperands() {
      return knowledgeBase.getOperands();
   }

   protected final TermFormatter getTermFormatter() {
      return knowledgeBase.getTermFormatter();
   }

   protected final SpyPoints getSpyPoints() {
      return knowledgeBase.getSpyPoints();
   }

   protected final FileHandles getFileHandles() {
      return knowledgeBase.getFileHandles();
   }

   // TODO add more convenience methods like getOutputStream() and formatTerm(Term)
}
