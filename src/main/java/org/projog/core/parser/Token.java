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
package org.projog.core.parser;

import java.util.Arrays;

/** @see TokenParser#next() */
final class Token {
   private static final Token[] EMPTY_ARGS = new Token[0];

   private final String value;
   private final TokenType type;
   private final Token[] args;
   private final String line;
   private final int lineNumber;
   private final int columnNumber;

   Token(String value, TokenType type, String line, int lineNumber, int columnNumber) {
      this.value = value;
      this.type = type;
      this.args = EMPTY_ARGS;
      this.line = line;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
   }

   Token(String value, TokenType type, Token[] args) {
      this.value = value;
      this.type = type;
      this.args = args;
      this.line = "";
      this.lineNumber = 0;
      this.columnNumber = 0;
   }

   Token(Token parent, TokenType type, Token[] args) {
      this.value = parent.value;
      this.type = type;
      this.args = args;
      this.line = parent.line;
      this.lineNumber = parent.lineNumber;
      this.columnNumber = parent.columnNumber;
   }

   String getName() {
      return value;
   }

   TokenType getType() {
      return type;
   }

   Token[] getArguments() {
      return args;
   }

   Token getArgument(int i) {
      return args[i];
   }

   int getNumberOfArguments() {
      return args.length;
   }

   @Override
   public String toString() {
      return value + (args.length == 0 ? "" : (" " + Arrays.toString(args)));
   }

   public int getLineNumber() {
      return lineNumber;
   }

   public int getColumnNumber() {
      return columnNumber;
   }

   public String getLine() {
      return line;
   }
}
