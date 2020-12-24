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
import static org.projog.TestUtils.atom;
import static org.projog.TestUtils.write;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ArithmeticOperator;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.ProjogProperties;
import org.projog.core.function.flow.RepeatSetAmount;
import org.projog.core.parser.ParserException;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/**
 * Tests {@link Projog}.
 * <p>
 * The majority of tests in this class work by calling {@link #createQueryResult()} which provides a convenient way of
 * creating a {@link QueryResult}. The {@link QueryResult} uses a new {@link Projog} instance that has been populated
 * with the contents of {@link #createTestScript()}
 */
public class ProjogTest {
   @Test
   public void testSetUserOutput() {
      Projog projog = new Projog();

      // given the user output has been reassigned to a new stream
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      projog.setUserOutput(new PrintStream(baos));

      // when we execute a query that writes to output
      projog.evaluateOnce("write(hello).");

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
   public void testSimpleQuery() {
      QueryResult r = createQueryResult();
      assertTrue(r.next());
      assertEquals("a", r.getTerm("X").toString());
      assertEquals("b", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertEquals("1", r.getTerm("X").toString());
      assertEquals("1", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertEquals("a", r.getTerm("X").toString());
      assertEquals("c", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertEquals("z", r.getTerm("X").toString());
      assertEquals("9", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertEquals("a", r.getTerm("X").toString());
      assertEquals("e", r.getTerm("Y").toString());
      assertFalse(r.next());
   }

   @Test
   public void testQueryMultiResultsAfterSetTerm() {
      QueryStatement s = createQueryStatement();
      Atom a = atom("a");
      s.setTerm("X", a);
      QueryResult r = s.executeQuery();
      assertSame(a, r.getTerm("X"));
      assertTrue(r.next());
      assertSame(a, r.getTerm("X"));
      assertEquals("b", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertSame(a, r.getTerm("X"));
      assertEquals("c", r.getTerm("Y").toString());
      assertTrue(r.next());
      assertSame(a, r.getTerm("X"));
      assertEquals("e", r.getTerm("Y").toString());
      assertFalse(r.next());
   }

   @Test
   public void testQuerySingleResultsAfterSetTerm() {
      QueryStatement s = createQueryStatement();
      Atom z = atom("z");
      s.setTerm("X", z);
      QueryResult r = s.executeQuery();
      assertSame(z, r.getTerm("X"));
      assertTrue(r.next());
      assertSame(z, r.getTerm("X"));
      assertEquals("9", r.getTerm("Y").toString());
      assertFalse(r.next());
   }

   @Test
   public void testQueryNoResultsAfterSetTerm() {
      QueryStatement s = createQueryStatement();
      Atom y = atom("y");
      s.setTerm("X", y);
      QueryResult r = s.executeQuery();
      assertSame(y, r.getTerm("X"));
      assertFalse(r.next());
   }

   @Test
   public void testSetTermForUnknownVariable() {
      QueryStatement s = createQueryStatement();
      Atom x = atom("x");
      try {
         s.setTerm("Z", x);
         fail();
      } catch (ProjogException e) {
         assertEquals("Do not know about variable named: Z in query: test(X, Y)", e.getMessage());
      }
   }

   @Test
   public void testGetTermForUnknownVariable() {
      QueryResult r = createQueryResult();
      r.getTerm("X");
      r.getTerm("Y");
      try {
         r.getTerm("Z");
         fail();
      } catch (ProjogException e) {
         assertEquals("Unknown variable ID: Z", e.getMessage());
      }
   }

   @Test
   public void testInvalidQuery() {
      try {
         Projog p = createProjog();
         p.createStatement("X");
         fail();
      } catch (ParserException e) {
         assertEquals("Unexpected end of stream Line: X", e.getMessage());
      }
   }

   @Test
   public void testMoreThanOneSentenceInQuery() {
      try {
         Projog p = createProjog();
         p.createStatement("X is 1. Y is 2.");
         fail();
      } catch (ProjogException e) {
         assertEquals("org.projog.core.ProjogException caught parsing: X is 1. Y is 2.", e.getMessage());
         assertEquals("More input found after . in X is 1. Y is 2.", e.getCause().getMessage());
      }
   }

   @Test
   public void testProjogExceptionOnGetResult() {
      Projog p = createProjog();
      StringReader sr = new StringReader("a(A) :- b(A). b(Z) :- c(Z, 5). c(X,Y) :- Z is X + Y, Z < 9.");
      p.consultReader(sr);
      QueryStatement s = p.createStatement("a(X).");
      try {
         // as a/1 only has a single clause then will try to evaluate as part of .getResult(), which is why exception occurs now rather than on a later call to .next()
         s.executeQuery();
         fail();
      } catch (ProjogException e) {
         assertSame(ProjogException.class, e.getClass()); // check it is not a sub-class
         assertEquals("Cannot get Numeric for term: X of type: VARIABLE", e.getMessage());
      }
   }

   /**
    * Tests a query that fails while it is being evaluated.
    * <p>
    * This method is a little different than the other tests in the class in that it does not use
    * {@link #createQueryResult()}.
    */
   @Test
   public void testProjogExceptionOnNext() {
      Projog p = createProjog();
      StringReader sr = new StringReader("a(A) :- b(A). a(A) :- A = test. b(Z) :- c(Z, 5). c(X,Y) :- Z is X + Y, Z < 9.");
      p.consultReader(sr);
      QueryStatement s = p.createStatement("a(X).");
      QueryResult r = s.executeQuery();
      try {
         // as a/1 only has multiple clauses then not try to evaluate as part of .getResult(), which is why exception only occurs on call to .next()
         r.next();
         fail();
      } catch (ProjogException e) {
         assertSame(ProjogException.class, e.getClass()); // check it is not a sub-class
         assertEquals("Cannot get Numeric for term: X of type: VARIABLE", e.getMessage());
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

   /** Tests a query that contains a cut (i.e. !). */
   @Test
   public void testQueryContainingCut() {
      Projog p = createProjog();
      QueryStatement s = p.createStatement("repeat, !.");
      QueryResult r = s.executeQuery();
      assertTrue(r.next());
      assertFalse(r.next());
   }

   @Test
   public void testFormatTerm() {
      Projog p = createProjog();
      Term inputTerm = TestUtils.parseSentence("X is 1 + 1 ; 3 < 5.");
      assertEquals(write(inputTerm), p.formatTerm(inputTerm));
   }

   private void assertProjogStackTraceElement(ProjogStackTraceElement actual, String expectedKey, String expectedTerm) {
      assertEquals(expectedKey, actual.getPredicateKey().toString());
      assertEquals(expectedTerm, actual.getTerm().toString());
   }

   /** Provides a convenient way of creating a {@link QueryStatement} to test with. */
   private QueryStatement createQueryStatement() {
      Projog p = createProjog();
      return p.createStatement("test(X,Y).");
   }

   /** Provides a convenient way of creating a {@link QueryResult} to test with. */
   private QueryResult createQueryResult() {
      Projog p = createProjog();
      QueryStatement s = p.createStatement("test(X,Y).");
      return s.executeQuery();
   }

   /** Returns a new {@link Projog} instance that has been populated with rules from {@link #createTestScript()}. */
   private Projog createProjog() {
      Projog p = new Projog();
      p.consultFile(createTestScript());
      return p;
   }

   /**
    * Returns a file containing Prolog syntax that can be used to populate the knowledge base of a {@link Projog}
    * instance.
    */
   private File createTestScript() {
      try {
         File f = File.createTempFile(getClass().getName(), ".pl", new File("target"));
         f.deleteOnExit();
         try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("test(a,b).");
            pw.println("test(A,A) :- A is 1.");
            pw.println("test(a,c).");
            pw.println("test(z,9).");
            pw.println("test(a,e).");
         }
         return f;
      } catch (Exception e) {
         throw new RuntimeException("error creating test script" + e, e);
      }
   }
}
