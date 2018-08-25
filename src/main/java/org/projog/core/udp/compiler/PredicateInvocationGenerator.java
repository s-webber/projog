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

/**
 * Generates Java source code for a particular predicate call of a user defined predicate.
 * <p>
 * A modular solution to provide the functionality to transform user defined predicates, specified using standard Prolog
 * syntax, into Java code at runtime.
 */
interface PredicateInvocationGenerator {
   /**
    * Generates the Java source code to call the current clause of the specified {@link CompiledPredicateWriter}.
    */
   void generate(CompiledPredicateWriter w);
}
