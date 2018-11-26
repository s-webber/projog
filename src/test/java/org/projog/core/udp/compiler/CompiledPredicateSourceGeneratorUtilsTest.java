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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

public class CompiledPredicateSourceGeneratorUtilsTest {
   @Test
   public void encodeEmpty() {
      assertEncoded("", "\"\"");
   }

   @Test
   public void encodeBlank() {
      assertEncoded("   ", "\"   \"");
   }

   @Test
   public void encodeLetters() {
      assertEncoded("qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM", "\"qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM\"");
   }

   @Test
   public void encodeDigits() {
      assertEncoded("01234567890", "\"01234567890\"");
   }

   @Test
   public void encodeNonAlphanumeric() {
      assertEncoded("!\"$%^&*()-_+-=[]{}:;@''#~?/.>,<\\|`'", "\"!\\\"$%^&*()-_+-=[]{}:;@''#~?/.>,<\\\\|`'\"");
   }

   @Test
   public void encodeEscapeSequences() {
      assertEncoded(" \t \b \n \r \f \' \" \\ ", "\" \\t \\b \\n \\r \\f ' \\\" \\\\ \"");
   }

   @Test
   public void encodeUnicode() {
      assertEncoded("abc\u00a5def", "\"abc\\u00a5def\"");
      assertEncoded("abc\uabcddef", "\"abc\\uabcddef\"");
      assertEncoded("abc\uefabdef", "\"abc\\uefabdef\"");
      assertEncoded("abc\u0000def", "\"abc\\u0000def\"");
      assertEncoded("abc\u0001def", "\"abc\\u0001def\"");
      assertEncoded("abc\u0012def", "\"abc\\u0012def\"");
      assertEncoded("abc\u0123def", "\"abc\\u0123def\"");
      assertEncoded("abc\u1234def", "\"abc\\u1234def\"");
      assertEncoded("abc\u9999def", "\"abc\\u9999def\"");
   }

   @Test
   public void testAnonymousVariable() {
      assertAnonymousVariable(new Variable("_"));
   }

   @Test
   public void testNotAnonymousVariable() {
      assertNotAnonymousVariable(new Variable("__"));
      assertNotAnonymousVariable(new Variable("_X"));
      assertNotAnonymousVariable(new Variable("X_"));
      assertNotAnonymousVariable(new Variable("X"));
      assertNotAnonymousVariable(new Variable("XYZ"));

      assertNotAnonymousVariable(new Atom("_"));
      assertNotAnonymousVariable(new Atom("test"));

      assertNotAnonymousVariable(new IntegerNumber(1));
      assertNotAnonymousVariable(new DecimalFraction(1));

      assertNotAnonymousVariable(Structure.createStructure("_", new Term[] {new Variable("_")}));
      assertNotAnonymousVariable(Structure.createStructure("a", new Term[] {new Variable("_")}));
      assertNotAnonymousVariable(Structure.createStructure("a", new Term[] {new Atom("b")}));

      assertNotAnonymousVariable(new List(new Variable("_"), new Variable("_")));
      assertNotAnonymousVariable(new List(new Variable("_"), new Atom("b")));
      assertNotAnonymousVariable(new List(new Atom("a"), new Variable("_")));
      assertNotAnonymousVariable(new List(new Atom("a"), new Atom("b")));
   }

   private void assertEncoded(String input, String expected) {
      assertEquals(expected, CompiledPredicateSourceGeneratorUtils.encodeName(input));
      assertEquals(expected, CompiledPredicateSourceGeneratorUtils.encodeName(new Atom(input)));
   }

   private void assertAnonymousVariable(Term t) {
      assertTrue(CompiledPredicateSourceGeneratorUtils.isAnonymousVariable(t));
      assertFalse(CompiledPredicateSourceGeneratorUtils.isNotAnonymousVariable(t));
   }

   private void assertNotAnonymousVariable(Term t) {
      assertFalse(CompiledPredicateSourceGeneratorUtils.isAnonymousVariable(t));
      assertTrue(CompiledPredicateSourceGeneratorUtils.isNotAnonymousVariable(t));
   }
}
