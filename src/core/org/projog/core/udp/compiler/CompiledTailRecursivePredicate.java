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
package org.projog.core.udp.compiler;

import org.projog.core.udp.TailRecursivePredicate;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/**
 * A super-class of all compiled "tail recursion optimised" user defined predicates.
 * <p>
 * For a user defined predicate to be implemented using {@code CompiledTailRecursivePredicate} it must be judged as
 * eligible for <i>tail recursion optimisation</i> using the criteria used by {@link TailRecursivePredicateMetaData}.
 * </p>
 * 
 * @see TailRecursivePredicateMetaData
 */
public abstract class CompiledTailRecursivePredicate extends TailRecursivePredicate implements CompiledPredicate {
   public abstract boolean[] isSingleResultIfArgumentImmutable();
}
