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
package org.projog.api;

import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.PROJOG_DEFAULT_PROPERTIES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.kb.ProjogProperties;
import org.projog.core.math.ArithmeticOperator;
import org.projog.core.math.Numeric;
import org.projog.core.predicate.PredicateFactory;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.builtin.flow.RepeatSetAmount;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

public class ProjogTest {
   @Test
   public void testSetUserOutput() {
      Projog projog = new Projog();

      // given the user output has been reassigned to a new stream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      projog.setUserOutput(new PrintStream(baos));

      // when we execute a query that writes to output
      projog.executeOnce("write(hello).");

      // then the new stream should be written to
      assertEquals("hello", new String(baos.toByteArray()));
   }

   @Test
   public void testSetUserInput() {
      Projog projog = new Projog();

      // given the user input has been reassigned to a new stream
      projog.setUserInput(new ByteArrayInputStream("hello".getBytes()));

      // when we execute a query that reads from input
      QueryResult result = projog.executeQuery("read(X).");
      result.next();

      // then the new stream should be read from
      assertEquals("hello", TermUtils.getAtomName(result.getTerm("X")));
   }

   @Test
   public void testAddPredicateFactory() {
      Projog projog = new Projog();

      // associate testAddPredicateFactory/1 with an instance of RepeatSetAmount
      PredicateKey key = new PredicateKey("testAddPredicateFactory", 1);
      PredicateFactory pf = new RepeatSetAmount();
      projog.addPredicateFactory(key, pf);

      // confirm that queries can use testAddPredicateFactory/1
      QueryResult result = projog.createStatement("testAddPredicateFactory(3).").executeQuery();
      assertTrue(result.next());
      assertTrue(result.next());
      assertTrue(result.next());
      assertFalse(result.next()); // expect false on 4th attempt as used 3 as argument
   }

   @Test
   public void testArithmeticOperator() {
      Projog projog = new Projog();

      // associate testArithmeticOperator/1 with an operator that adds 7 to its argument
      PredicateKey key = new PredicateKey("testArithmeticOperator", 1);
      ArithmeticOperator pf = new ArithmeticOperator() {
         @Override
         public Numeric calculate(Term[] args) {
            Numeric n = TermUtils.castToNumeric(args[0]);
            return new IntegerNumber(n.getLong() + 7);
         }
      };
      projog.addArithmeticOperator(key, pf);

      // confirm that queries can use testAddPredicateFactory/1
      QueryResult result = projog.createStatement("X is testArithmeticOperator(3).").executeQuery();
      assertTrue(result.next());
      assertEquals(10, TermUtils.castToNumeric(result.getTerm("X")).getLong()); // 3 + 7 = 10
   }

   @Test
   public void testCreatePlan() {
      Projog projog = new Projog();
      QueryPlan plan = projog.createPlan("X = 1.");
      QueryResult result = plan.executeQuery();
      assertTrue(result.next());
      assertEquals(1, result.getLong("X"));
   }

   @Test
   public void testCreateStatement() {
      Projog projog = new Projog();
      QueryStatement statement = projog.createStatement("X = 1.");
      QueryResult result = statement.executeQuery();
      assertTrue(result.next());
      assertEquals(1, result.getLong("X"));
   }

   @Test
   public void testExecuteQuery() {
      Projog projog = new Projog();
      QueryResult result = projog.executeQuery("X = 1.");
      assertTrue(result.next());
      assertEquals(1, result.getLong("X"));
   }

   @Test
   public void testExecuteOnceNoSolution() {
      Projog projog = new Projog();
      try {
         projog.executeOnce("true, true, fail.");
         fail();
      } catch (ProjogException projogException) {
         assertEquals("Failed to find a solution for: ,(,(true, true), fail)", projogException.getMessage());
      }
   }

   /** Attempts to open a file that doesn't exist to see how non-ProjogException exceptions are dealt with. */
   @Test
   public void testIOExceptionWhileEvaluatingQueries() {
      assertStackTraceOfIOExceptionWhileEvaluatingQueries(PROJOG_DEFAULT_PROPERTIES);
   }

   private void assertStackTraceOfIOExceptionWhileEvaluatingQueries(ProjogProperties projogProperties) {
      Projog p = new Projog(projogProperties);
      StringBuilder inputSource = new StringBuilder();
      inputSource.append("x(A) :- fail. x(A) :- y(A). x(A). ");
      inputSource.append("y(A) :- Q is 4 + 5, z(A, A, Q). ");
      inputSource.append("z(A, B, C) :- fail. z(A, B, C) :- 7<3. z(A, B, C) :- open(A,'read',Z). z(A, B, C). ");
      StringReader sr = new StringReader(inputSource.toString());
      p.consultReader(sr);
      QueryStatement s = p.createStatement("x('a_directory_that_doesnt_exist/another_directory_that_doesnt_exist/some_file.xyz').");
      QueryResult r = s.executeQuery();
      try {
         r.next();
         fail();
      } catch (ProjogException projogException) {
         assertEquals("Unable to open input for: a_directory_that_doesnt_exist/another_directory_that_doesnt_exist/some_file.xyz", projogException.getMessage());
         assertSame(FileNotFoundException.class, projogException.getCause().getClass());

         // retrieve and check stack trace elements
         ProjogStackTraceElement[] elements = p.getStackTrace(projogException);
         assertEquals(3, elements.length);
         assertProjogStackTraceElement(elements[0], "z/3", ":-(z(A, B, C), open(A, read, Z))");
         assertProjogStackTraceElement(elements[1], "y/1", ":-(y(A), ,(is(Q, +(4, 5)), z(A, A, Q)))");
         assertProjogStackTraceElement(elements[2], "x/1", ":-(x(A), y(A))");

         // Write stack trace to OutputStream so it can be compared against the expected result.
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         PrintStream ps = new PrintStream(bos);
         p.printProjogStackTrace(projogException, ps);
         ps.close();

         // Generate expected stack trace.
         StringBuilder expectedResult = new StringBuilder();
         expectedResult.append("z/3 clause: z(A, B, C) :- open(A, read, Z)");
         expectedResult.append(lineSeparator());
         expectedResult.append("y/1 clause: y(A) :- Q is 4 + 5 , z(A, A, Q)");
         expectedResult.append(lineSeparator());
         expectedResult.append("x/1 clause: x(A) :- y(A)");
         expectedResult.append(lineSeparator());

         // Confirm contents of stack trace
         assertEquals(expectedResult.toString(), bos.toString());
      }
   }

   @Test
   public void testFormatTerm() {
      Projog p = new Projog();
      Term inputTerm = TestUtils.parseSentence("X is 1 + 1 ; 3 < 5.");
      assertEquals("X is 1 + 1 ; 3 < 5", p.formatTerm(inputTerm));
   }

   private void assertProjogStackTraceElement(ProjogStackTraceElement actual, String expectedKey, String expectedTerm) {
      assertEquals(expectedKey, actual.getPredicateKey().toString());
      assertEquals(expectedTerm, actual.getTerm().toString());
   }
}
