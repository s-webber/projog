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

import java.util.ArrayList;
import java.util.List;

import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * An exception that provides information on an error within the Projog environment.
 * <p>
 * Maintains a collection of all {@link org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate} instances that
 * form the exception's stack trace.
 */
public class ProjogException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   private final List<InterpretedUserDefinedPredicate> interpretedUserDefinedPredicates = new ArrayList<>();

   public ProjogException(String message) {
      super(message, null);
   }

   public ProjogException(String message, Throwable throwable) {
      super(message, throwable);
   }

   public void addUserDefinedPredicate(InterpretedUserDefinedPredicate userDefinedPredicate) {
      interpretedUserDefinedPredicates.add(userDefinedPredicate);
   }

   public List<InterpretedUserDefinedPredicate> getInterpretedUserDefinedPredicates() {
      return interpretedUserDefinedPredicates;
   }
}
