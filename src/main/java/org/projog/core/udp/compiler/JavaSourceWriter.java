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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructs Java source code.
 */
class JavaSourceWriter {
   private static final String SEMI_COLON = ";";

   private final List<String> lines = new ArrayList<>();

   private String packageStructure;

   private String className;

   void writePackage(String packageStructure) {
      this.packageStructure = packageStructure;
      writeStatement("package " + packageStructure);
   }

   void writeImport(String importStructure) {
      writeStatement("import " + importStructure);
   }

   void beginClass(String className, String extendsAndImplements) {
      this.className = className;
      startBlock("public final class " + className + " " + extendsAndImplements);
   }

   void beginMethod(String statement) {
      startBlock(statement);
   }

   void returnThis() {
      writeStatement("return this");
   }

   void returnTrue() {
      writeStatement("return true");
   }

   void returnFalse() {
      writeStatement("return false");
   }

   void ifFalseReturnFalse(String condition) {
      ifTrueReturnFalse("!" + condition);
   }

   void ifTrueReturnFalse(String condition) {
      beginIf(condition);
      returnFalse();
      endBlock();
   }

   void ifTrueReturnTrue(String condition) {
      beginIf(condition);
      returnTrue();
      endBlock();
   }

   void beginIf(String condition) {
      startBlock("if (" + condition + ")");
   }

   void elseIf(String condition) {
      startBlock("} else if (" + condition + ")");
   }

   void elseStatement() {
      addLine("} else {");
   }

   void endBlock() {
      addLine("}");
   }

   void declare(String className, String destination, String source) {
      writeStatement(className + " " + destination + " = " + source);
   }

   void assignTrue(String destination) {
      assign(destination, "true");
   }

   void assignFalse(String destination) {
      assign(destination, "false");
   }

   void assign(String destination, String source) {
      writeStatement(destination + " = " + source);
   }

   void assign(String destination, int source) {
      writeStatement(destination + " = " + source);
   }

   private void startBlock(String statement) {
      addLine(statement + " {");
   }

   void println(String message) {
      writeStatement("System.out.println(" + message + ")");
   }

   void writeStatement(String statement) {
      addLine(statement + SEMI_COLON);
   }

   void comment(Object comment) {
      addLine("// " + comment);
   }

   void addLine(String line) {
      lines.add(line);
   }

   String getClassName() {
      return packageStructure + "." + className;
   }

   void beginSwitch(String variable) {
      addLine("switch (" + variable + ") {");
   }

   void beginCase(int constant) {
      addLine("case " + constant + ":");
   }

   File getSourceFile(File sourceDirectory) {
      File parentDir = sourceDirectory;
      for (String packageName : packageStructure.split("\\.")) {
         parentDir = new File(parentDir, packageName);
      }
      if (!parentDir.exists() && !parentDir.mkdirs()) {
         throw new RuntimeException("Was not able to create directory: " + parentDir);
      }
      return new File(parentDir, className + ".java");
   }

   String getSource() {
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line).append(System.lineSeparator());
      }
      return sb.toString();
   }
}
