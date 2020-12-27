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
package org.projog.core.predicate.builtin.io;

import static org.junit.Assert.assertEquals;
import static org.projog.TestUtils.createKnowledgeBase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projog.core.predicate.builtin.io.Write;
import org.projog.core.term.Atom;

public class WriteTest {
   private static final String TEXT = "hello, world!";

   private final ByteArrayOutputStream redirectedOut = new ByteArrayOutputStream();
   private final PrintStream originalOut = System.out;

   @Before
   public void setUpStreams() {
      System.setOut(new PrintStream(redirectedOut));
   }

   @Test
   public void testWriteString() {
      Write w = Write.write();
      w.setKnowledgeBase(createKnowledgeBase());
      w.evaluate(new Atom(TEXT));
      assertEquals(TEXT, redirectedOut.toString());
   }

   @Test
   public void testWritelnString() {
      Write w = Write.writeln();
      w.setKnowledgeBase(createKnowledgeBase());
      w.evaluate(new Atom(TEXT));
      assertEquals(TEXT + System.lineSeparator(), redirectedOut.toString());
   }

   @After
   public void restoreStreams() {
      System.setOut(originalOut);
   }
}
