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
import static org.projog.TestUtils.parseTerm;

import org.junit.Test;
import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.EmptyList;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class TermCreationWriterTest {
   @Test
   public void emptyList() {
      assertStatement(EmptyList.EMPTY_LIST, "EmptyList.EMPTY_LIST");
   }

   @Test
   public void atom() {
      assertStatement(new Atom("a"), "new Atom(\"a\")");
      assertStatement(new Atom("hello \"world\"!"), "new Atom(\"hello \\\"world\\\"!\")");
   }

   @Test
   public void integer() {
      assertStatement(new IntegerNumber(42), "new IntegerNumber(42L)");
      assertStatement(new IntegerNumber(-7), "new IntegerNumber(-7L)");
      assertStatement(new IntegerNumber(0), "new IntegerNumber(0L)");
      assertStatement(new IntegerNumber(Long.MAX_VALUE), "new IntegerNumber(9223372036854775807L)");
      assertStatement(new IntegerNumber(Long.MIN_VALUE), "new IntegerNumber(-9223372036854775808L)");
   }

   @Test
   public void decimal() {
      assertStatement(new DecimalFraction(42), "new DecimalFraction(42.0)");
      assertStatement(new DecimalFraction(42.5), "new DecimalFraction(42.5)");
      assertStatement(new DecimalFraction(-7), "new DecimalFraction(-7.0)");
      assertStatement(new DecimalFraction(-7.253846), "new DecimalFraction(-7.253846)");
      assertStatement(new DecimalFraction(0), "new DecimalFraction(0.0)");
      assertStatement(new DecimalFraction(Double.MAX_VALUE), "new DecimalFraction(1.7976931348623157E308)");
      assertStatement(new DecimalFraction(Double.MIN_VALUE), "new DecimalFraction(4.9E-324)");
   }

   @Test
   public void structure() {
      assertStatement(Structure.createStructure("a", new Term[] {new Atom("b")}), "Structure.createStructure(\"a\",new Term[]{new Atom(\"b\")})");

      assertStatement(Structure.createStructure("hello \"world\"!", new Term[] {new Atom("b")}), "Structure.createStructure(\"hello \\\"world\\\"!\",new Term[]{new Atom(\"b\")})");

      assertStatement(parseTerm("a(b, 1, 1.5, [])"),
                  "Structure.createStructure(\"a\",new Term[]{new Atom(\"b\"),new IntegerNumber(1L),new DecimalFraction(1.5),EmptyList.EMPTY_LIST})");

      assertStatement(parseTerm("a(b(c))"), "Structure.createStructure(\"a\",new Term[]{Structure.createStructure(\"b\",new Term[]{new Atom(\"c\")})})");
   }

   @Test
   public void list() {
      assertStatement(parseTerm("[a]"), "new List(new Atom(\"a\"),EmptyList.EMPTY_LIST)");
      assertStatement(parseTerm("[a,b]"), "new List(new Atom(\"a\"),new List(new Atom(\"b\"),EmptyList.EMPTY_LIST))");
      assertStatement(parseTerm("[a|b]"), "new List(new Atom(\"a\"),new Atom(\"b\"))");
      assertStatement(parseTerm("[a,b,c]"), "new List(new Atom(\"a\"),new List(new Atom(\"b\"),new List(new Atom(\"c\"),EmptyList.EMPTY_LIST)))");
      assertStatement(parseTerm("[a,b|c]"), "new List(new Atom(\"a\"),new List(new Atom(\"b\"),new Atom(\"c\")))");
      assertStatement(parseTerm("[a(1),[b,c,d],e]"),
                  "new List(Structure.createStructure(\"a\",new Term[]{new IntegerNumber(1L)}),new List(new List(new Atom(\"b\"),new List(new Atom(\"c\"),new List(new Atom(\"d\"),EmptyList.EMPTY_LIST))),new List(new Atom(\"e\"),EmptyList.EMPTY_LIST)))");
   }

   private void assertStatement(Term input, String expected) {
      TermCreationWriter w = new TermCreationWriter();
      String actual = w.outputCreateTermStatement(input, null, true);
      assertEquals(expected, actual);
   }
}
