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
package org.projog.core;

import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

/**
 * Represents an arithmetic expression.
 * <p>
 * <img src="doc-files/Calculatable.png">
 * 
 * @see Calculatables
 */
public interface Calculatable {
   /**
    * Returns the result of the calculation using the specified arguments.
    * 
    * @param args the arguments to use in the calculation
    * @return the result of the calculation using the specified arguments
    */
   Numeric calculate(Term... args);

   /**
    * Provides a reference to a {@code KnowledgeBase}.
    * <p>
    * Meaning this object will always have access to a {@code KnowledgeBase} by the time its {@code calculate} method
    * is invoked.
    */
   void setKnowledgeBase(KnowledgeBase kb);
}
