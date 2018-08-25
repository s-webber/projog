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

import java.lang.reflect.Constructor;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseServiceLocator;
import org.projog.core.ProjogException;
import org.projog.core.udp.ClauseModel;

/**
 * Constructs and compiles source code for new {@link CompiledPredicate} classes.
 */
public final class CompiledPredicateClassGenerator {
   /**
    * Translates the specified {@code implications} into Java source code before compiling it and returning an instance
    * of the newly created class.
    */
   public static CompiledPredicate generateCompiledPredicate(KnowledgeBase kb, List<ClauseModel> implications) {
      CompiledPredicateWriter writer = new CompiledPredicateWriter(kb, implications);
      new CompiledPredicateSourceGenerator(writer).generateSource();
      return compileSource(kb, writer.getClassName(), writer.getSource());
   }

   /** Compiles the specified {@code sourceContent} Java code and returns a new instance of the new class. */
   private static CompiledPredicate compileSource(KnowledgeBase kb, String className, String sourceContent) {
      try {
         JavaSourceCompiler compiler = KnowledgeBaseServiceLocator.getServiceLocator(kb).getInstance(JavaSourceCompiler.class);
         Class<?> c = compiler.compileClass(className, sourceContent);
         Constructor<?> constructor = c.getConstructor(KnowledgeBase.class);
         return (CompiledPredicate) constructor.newInstance(kb);
      } catch (Throwable e) {
         throw new ProjogException("Caught " + e.getClass().getName() + " while attempting to compile class: " + className + " with message: " + e.getMessage(), e);
      }
   }
}
