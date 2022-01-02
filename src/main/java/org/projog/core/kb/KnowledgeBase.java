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
package org.projog.core.kb;

import org.projog.core.event.ProjogListeners;
import org.projog.core.event.SpyPoints;
import org.projog.core.io.FileHandles;
import org.projog.core.math.ArithmeticOperators;
import org.projog.core.parser.Operands;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.Predicates;
import org.projog.core.predicate.builtin.kb.AddPredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.term.TermFormatter;

/**
 * The central object that connects the various components of an instance of the "core" inference engine.
 * <p>
 * <img src="doc-files/KnowledgeBase.png">
 * </p>
 */
public final class KnowledgeBase {
   /**
    * Represents the {@code pj_add_predicate/2} predicate hard-coded in every {@code KnowledgeBase}.
    * <p>
    * The {@code pj_add_predicate/2} predicate allows other implementations of {@link PredicateFactory} to be
    * "plugged-in" to a {@code KnowledgeBase} at runtime using Prolog syntax.
    *
    * @see AddPredicateFactory#evaluate(Term[])
    */
   private static final PredicateKey ADD_PREDICATE_KEY = new PredicateKey("pj_add_predicate", 2);

   private final ProjogProperties projogProperties;
   private final Predicates predicates;
   private final ArithmeticOperators arithmeticOperators;
   private final ProjogListeners projogListeners;
   private final Operands operands;
   private final TermFormatter termFormatter;
   private final SpyPoints spyPoints;
   private final FileHandles fileHandles;

   /**
    * @see KnowledgeBaseUtils#createKnowledgeBase()
    * @see KnowledgeBaseUtils#createKnowledgeBase(ProjogProperties)
    */
   KnowledgeBase(ProjogProperties projogProperties) {
      this.projogProperties = projogProperties;
      this.predicates = new Predicates(this);
      this.predicates.addPredicateFactory(ADD_PREDICATE_KEY, new AddPredicateFactory(this));
      this.arithmeticOperators = new ArithmeticOperators(this);
      this.projogListeners = new ProjogListeners();
      this.operands = new Operands();
      this.termFormatter = new TermFormatter(operands);
      this.spyPoints = new SpyPoints(this);
      this.fileHandles = new FileHandles();
   }

   public ProjogProperties getProjogProperties() {
      return projogProperties;
   }

   public Predicates getPredicates() {
      return predicates;
   }

   public ArithmeticOperators getArithmeticOperators() {
      return arithmeticOperators;
   }

   public ProjogListeners getProjogListeners() {
      return projogListeners;
   }

   public Operands getOperands() {
      return operands;
   }

   public TermFormatter getTermFormatter() {
      return termFormatter;
   }

   public SpyPoints getSpyPoints() {
      return spyPoints;
   }

   public FileHandles getFileHandles() {
      return fileHandles;
   }
}
