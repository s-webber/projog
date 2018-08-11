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

import org.projog.api.Projog;

/**
 * Collection of configuration properties.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single {@code ProjogProperties} instance.
 *
 * @see KnowledgeBaseUtils#getProjogProperties(KnowledgeBase)
 */
public interface ProjogProperties {
   /**
    * The file to consult when a new {@link Projog} instance is created.
    * <p>
    * Used to initialise the knowledge base.
    *
    * @see #getBootstrapScript()
    */
   String DEFAULT_BOOTSTRAP_SCRIPT = "projog-bootstrap.pl";

   /**
    * Returns {@code true} if the use of spy points is enabled.
    * <p>
    * If spy points are enabled then it will be possible to get information about the sequence of goals being evaluated
    * by Projog as they are evaluated. This facility aids the debugging of Prolog code but can also have a slight impact
    * on performance.
    *
    * @return {@code true} if the use of spy points is enabled
    * @see SpyPoints
    */
   boolean isSpyPointsEnabled();

   /**
    * Returns {@code true} if user defined predicates should be compiled at runtime.
    * <p>
    * Projog is able to convert user defined predicates specified using Prolog syntax into native Java code. Converting
    * Prolog syntax into Java code offers optimised performance. The generation of the Java code and its compilation
    * happens at runtime as new clauses are consulted. If runtime compilation is disabled then Projog operates in
    * "interpreted" mode - this will impact performance but avoid the need of compiling Java code at runtime.
    *
    * @return {@code true} if user defined predicates should be compiled at runtime
    */
   boolean isRuntimeCompilationEnabled();

   /**
    * Returns the name of the resource loaded by {@link KnowledgeBaseUtils#bootstrap(KnowledgeBase)}.
    *
    * @return the name of the resource loaded by {@link KnowledgeBaseUtils#bootstrap(KnowledgeBase)}
    * @see KnowledgeBaseUtils#bootstrap(KnowledgeBase)
    */
   String getBootstrapScript();
}
