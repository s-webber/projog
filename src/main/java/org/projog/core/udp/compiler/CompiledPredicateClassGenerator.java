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
package org.projog.core.udp.compiler;

import static org.projog.core.udp.compiler.CompiledPredicateConstants.COMPILED_PREDICATES_PACKAGE;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.ProjogException;
import org.projog.core.udp.ClauseModel;

/**
 * Constructs and compiles source code for new {@link CompiledPredicate} classes.
 * <p>
 * Used at runtime to convert user defined predicates (defined using Prolog syntax) into Java classes.
 *
 * @see CompiledPredicateSourceGenerator
 * @see JavaSourceCompiler
 */
public final class CompiledPredicateClassGenerator {
   private final AtomicInteger classCtr = new AtomicInteger();
   private final JavaSourceCompiler compiler = new JavaSourceCompiler();

   /**
    * Translates the specified {@code implications} into Java source code before compiling it and returning an instance
    * of the newly created class.
    */
   public CompiledPredicate generateCompiledPredicate(KnowledgeBase kb, List<ClauseModel> implications) {
      String className = getCompiledPredicateClassName();
      String sourceCode = CompiledPredicateSourceGenerator.generateJavaSource(className, kb, implications);
      writeSourceToFileSystem(kb, className, sourceCode);
      return createInstance(kb, className, sourceCode);
   }

   private String getCompiledPredicateClassName() {
      return "P" + classCtr.getAndIncrement();
   }

   private void writeSourceToFileSystem(KnowledgeBase kb, String className, String sourceCode) {
      File compiledContentOutputDirectory = KnowledgeBaseUtils.getProjogProperties(kb).getCompiledContentOutputDirectory();
      if (compiledContentOutputDirectory != null) {
         try {
            Files.write(new File(compiledContentOutputDirectory, className + ".txt").toPath(), sourceCode.getBytes());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   /** Compiles the specified {@code sourceCode} Java code and returns a new instance of the new class. */
   private CompiledPredicate createInstance(KnowledgeBase kb, String className, String sourceCode) {
      try {
         Class<?> c = compiler.compileClass(COMPILED_PREDICATES_PACKAGE + "." + className, sourceCode);
         Constructor<?> constructor = c.getConstructor(KnowledgeBase.class);
         return (CompiledPredicate) constructor.newInstance(kb);
      } catch (Throwable e) {
         e.printStackTrace();
         throw new ProjogException("Caught " + e.getClass().getName() + " while attempting to compile class: " + className + " with message: " + e.getMessage(), e);
      }
   }
}
