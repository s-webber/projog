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
package org.projog.core.parser;

/** @see TokenParser */
enum TokenType {
   ATOM,
   INTEGER,
   FLOAT,
   VARIABLE,
   NAMED_BRACKET,
   OPERAND_AND_ARGUMENTS,
   SYMBOL,
   UNNAMED_BRACKET,
   EMPTY_LIST,
   LIST;

   boolean isPossibleOperand() {
      return this == ATOM || this == SYMBOL;
   }

   boolean isNumber() {
      return this == INTEGER || this == FLOAT;
   }
}
