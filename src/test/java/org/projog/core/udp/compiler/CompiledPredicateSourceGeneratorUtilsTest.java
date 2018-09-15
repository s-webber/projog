/*
 * Copyright 2018 S. Webber
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.projog.core.term.Atom;

public class CompiledPredicateSourceGeneratorUtilsTest {
   @Test
   public void encodeName() {
      assertEncoded("", "\"\"");
      assertEncoded("   ", "\"   \"");
      assertEncoded("qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM", "\"qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM\"");
      assertEncoded("!\"$%^&*()-_+-=[]{}:;@''#~?/.>,<\\|`'", "\"!\\\"$%^&*()-_+-=[]{}:;@''#~?/.>,<\\\\|`'\"");
   }

   private void assertEncoded(String input, String expected) {
      assertEquals(expected, CompiledPredicateSourceGeneratorUtils.encodeName(input));
      assertEquals(expected, CompiledPredicateSourceGeneratorUtils.encodeName(new Atom(input)));
   }

   @Test
   public void getClassNameMinusPackage() {
      assertEquals("CompiledPredicateSourceGeneratorUtilsTest", CompiledPredicateSourceGeneratorUtils.getClassNameMinusPackage(this));
      assertEquals("String", CompiledPredicateSourceGeneratorUtils.getClassNameMinusPackage(""));
   }
}
