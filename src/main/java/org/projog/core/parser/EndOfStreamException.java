/*
 * Copyright 2025 S. Webber
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

/**
 * Signals a failure to successfully parse Prolog syntax due to incomplete input.
 * <p>
 * This subclass of ParserException is used to indicate that the input is incomplete and parsing may succeed if more
 * input is provided, as as opposed to other instances of ParserException which indicate that the input is invalid.
 */
public class EndOfStreamException extends ParserException {
   private static final long serialVersionUID = 1L;

   EndOfStreamException(String message, Token token) {
      super(message, token);
   }

   EndOfStreamException(String message, CharacterParser parser) {
      super(message, parser);
   }
}
