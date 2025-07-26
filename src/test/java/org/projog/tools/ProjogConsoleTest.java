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
package org.projog.tools;

import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;
import static org.projog.TestUtils.writeToTempFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Test;

public class ProjogConsoleTest {
   private static final String ERROR_MESSAGE = "Invalid. Enter ; to continue or q to quit. ";
   private static final String PROMPT = "?- ";
   private static final String YES = "yes (0 ms)";
   private static final String NO = "no (0 ms)";
   private static final String EXPECTED_HEADER = concatenate("INFO Reading prolog source in: projog-bootstrap.pl from classpath", "Projog Console", "projog.org", "");
   private static final String EXPECTED_FOOTER = lineSeparator() + PROMPT + lineSeparator() + YES + lineSeparator();
   private static final String QUIT_COMMAND = concatenate("quit.");

   @Test
   public void testTrue() throws IOException {
      String input = createInput("true.");
      String expected = createExpectedOutput(PROMPT, YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testFail() throws IOException {
      String input = createInput("fail.");
      String expected = createExpectedOutput(PROMPT, NO);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testSingleVariable() throws IOException {
      String input = createInput("X = y.");
      String expected = createExpectedOutput(PROMPT, "X = y", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultipleVariables() throws IOException {
      String input = createInput("W=X, X=1+1, Y is W, Z is -W.");
      String expected = createExpectedOutput(PROMPT, "W = 1 + 1", "X = 1 + 1", "Y = 2", "Z = -2", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLineQuery() throws IOException {
      String input = createInput("W=X,", "X=1+1,", "Y is W,", "Z is -W.");
      String expected = createExpectedOutput(PROMPT, "W = 1 + 1", "X = 1 + 1", "Y = 2", "Z = -2", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLineWithComment() throws IOException {
      String input = createInput("W=X, % single line comment", "X=1+1,  % single line comment with .", "Y is W, /* multi-line comment", "still in multi-line comment",
                  "end of multi-line comment*/", "Z is -W. % single line comment after end of sentence");
      String expected = createExpectedOutput(PROMPT, "W = 1 + 1", "X = 1 + 1", "Y = 2", "Z = -2", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLineList1() throws IOException {
      String input = createInput("X = [", "a,", "b", ",c", "]", ".");
      String expected = createExpectedOutput(PROMPT, "X = [a,b,c]", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLineList2() throws IOException {
      String input = createInput("X = [a,", "b", ",c", "]", ".");
      String expected = createExpectedOutput(PROMPT, "X = [a,b,c]", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLineListWithoutClosingBracket() throws IOException {
      String input = createInput("X = [a,", "b", ",c", ".");
      String expected = createExpectedOutput(PROMPT, "Error parsing query:", "No matching ] for [", "X = [a,", "    ^");
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLinePredicate1() throws IOException {
      String input = createInput("X = p(", "a,", "b", ",c", ")", ".");
      String expected = createExpectedOutput(PROMPT, "X = p(a, b, c)", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLinePredicate2() throws IOException {
      String input = createInput("X = p(a,", "b", ",c", ")", ".");
      String expected = createExpectedOutput(PROMPT, "X = p(a, b, c)", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMultiLinePredicateWithoutClosingBracket() throws IOException {
      String input = createInput("X = p(a,", "b", ",c", ".");
      String expected = createExpectedOutput(PROMPT, "Error parsing query:", "No matching ) for (", "X = p(a,", "    ^");
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testMoreInputAfterSentence() throws IOException {
      String input = createInput("true. true");
      String expected = createExpectedOutput(PROMPT, "org.projog.core.ProjogException caught parsing: true. true", "More input found after . in true. true");
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testNoSuitableOperands() throws IOException {
      String input = createInput("X is 1 2 3.");
      String expected = createExpectedOutput(PROMPT, "Error parsing query:", "No suitable operands", "X is 1 2 3.", "         ^");
      String actual = getOutput(input);
      compare(expected, actual);
   }

   @Test
   public void testCannotFindArithmeticOperator() throws IOException {
      String input = createInput("X is x.");
      String expected = createExpectedOutput(PROMPT, "Cannot find arithmetic operator: x/0");
      String actual = getOutput(input);
      compare(expected, actual);
   }

   /** Test inputting {@code ;} to continue evaluation and {@code q} to quit, plus validation of invalid input. */
   @Test
   public void testRepeat() throws IOException {
      String input = createInput("repeat.", ";", ";", "z", "", "qwerty", "q");
      String expected = createExpectedOutput(PROMPT, YES, YES, YES + ERROR_MESSAGE + ERROR_MESSAGE + ERROR_MESSAGE);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   /** Tests {@code trace} functionality using query against terms input using {@code consult}>. */
   @Test
   public void testConsultAndTrace() throws IOException {
      File tempFile = createFileToConsult("test(a).", "test(b).", "test(c).");
      String path = tempFile.getPath();
      String input = createInput("consult('" + path.replace("\\", "\\\\") + "').", "trace.", "test(X).", ";", ";");
      String expected = createExpectedOutput(PROMPT + "INFO Reading prolog source in: " + path + " from file system", "", YES, "", PROMPT, YES, "",
                  PROMPT + "[THREAD-ID] CALL test(X)", "[THREAD-ID] EXIT test(a)", "", "X = a", "", YES + "[THREAD-ID] REDO test(a)", "[THREAD-ID] EXIT test(b)", "", "X = b", "",
                  YES + "[THREAD-ID] REDO test(b)", "[THREAD-ID] EXIT test(c)", "", "X = c", "", YES);
      String actual = getOutput(input);
      compare(expected, actual);
   }

   private File createFileToConsult(String... lines) throws IOException {
      return writeToTempFile(this.getClass(), concatenate(lines));
   }

   private String createInput(String... lines) {
      return concatenate(lines) + QUIT_COMMAND;
   }

   private String createExpectedOutput(String... lines) {
      return EXPECTED_HEADER + concatenate(lines) + EXPECTED_FOOTER;
   }

   private String getOutput(String input) throws IOException {
      try (ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes()); ByteArrayOutputStream os = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(os)) {
         final ProjogConsole c = new ProjogConsole(is, ps);
         c.run(new ArrayList<String>());
         return os.toString();
      }
   }

   private void compare(String expected, String actual) {
      String tidiedExpected = makeSuitableForComparison(expected);
      String tidiedActual = makeSuitableForComparison(actual);
      assertEquals(tidiedExpected, tidiedActual);
   }

   /**
    * Output from the console application is unpredictable - some information returned (that is incidental to the core
    * functionality) will vary between multiple executions of the same query against the same knowledge base. In order
    * to check the actual input meets our expectations we first need to "tidy it" to remove inconsistencies (i.e. thread
    * IDs and timings).
    */
   private String makeSuitableForComparison(String in) {
      return replaceTimings(replaceThreadId(in));
   }

   /**
    * Return a version of the input with the thread IDs removed.
    * <p>
    * Output sometimes includes thread IDs contained in square brackets. e.g.: <code>[31966667]</code>
    */
   private String replaceThreadId(String in) {
      return in.replaceAll("\\[\\d*\\]", "[THREAD-ID]");
   }

   /**
    * Return a version of the input with the timings removed.
    * <p>
    * Output sometimes contains info on how long a query took to execute. e.g.: <code>(15 ms)</code>
    */
   private String replaceTimings(String in) {
      return in.replaceAll("\\(\\d* ms\\)", "(n ms)");
   }

   private static String concatenate(String... lines) {
      final StringBuilder result = new StringBuilder();
      for (final String line : lines) {
         result.append(line);
         result.append(lineSeparator());
      }
      return result.toString();
   }
}
