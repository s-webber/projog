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

import org.projog.core.term.Term;

/**
 * Exception thrown when the evaluation of a rule backtracks to a cut.
 * 
 * @see org.projog.core.function.flow.Cut
 * @see org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate
 */
public final class CutException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   /**
    * Singleton instance.
    * <p>
    * Reuse a single instance to avoid the stack trace generation overhead of creating a new exception each time. The
    * {@code CutException} is specifically used for control flow in
    * {@link org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate#evaluate(Term[])} and its stack trace is
    * not required.
    */
   public static final CutException CUT_EXCEPTION = new CutException();

   /**
    * Private constructor to force use of {@link #CUT_EXCEPTION}
    */
   private CutException() {
      // do nothing
   }
}
