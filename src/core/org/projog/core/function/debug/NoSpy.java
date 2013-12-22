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
package org.projog.core.function.debug;

/* SYSTEM TEST
 % %LINK% prolog-debugging
 */
/**
 * <code>nospy X</code> - removes a spy point for a predicate.
 * <p>
 * By adding removing a spy point for the predicate name instantiated to <code>X</code> the programmer will no longer be
 * informed how it is used in the resolution of a goal.
 * </p>
 */
public final class NoSpy extends AbstractAlterSpyPointFunction {
   public NoSpy() {
      super(false);
   }
}