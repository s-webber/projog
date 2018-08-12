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
package org.projog.test;

import static org.projog.build.BuildUtilsConstants.isPrologScript;
import static org.projog.build.BuildUtilsConstants.readAllBytes;
import static org.projog.build.BuildUtilsConstants.toUnixLineEndings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.projog.api.Projog;
import org.projog.api.QueryResult;
import org.projog.api.QueryStatement;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.ProjogException;
import org.projog.core.event.ProjogEvent;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Runs tests defined in Prolog files and compares actual output against expected results.
 * <p>
 * Projog can operate in two modes - interpreted and compiled. Although they should give the same result there are some
 * subtle differences about how the results may be presented. As the expected output specified by the tests always
 * refers to what the compiled version should do, there are some "workarounds" for confirming the output of running in
 * interpreted mode.
 *
 * @see ProjogTestParser
 * @see ProjogTestExtractor
 */
public final class ProjogTestRunner implements Observer {
   private static final boolean DEBUG = false;

   private final File redirectedOutputFile = new File("ProjogTestRunnerOutput_" + hashCode() + ".tmp");
   private final Map<Object, Integer> spypointSourceIds = new HashMap<>();
   private final ProjogSupplier projogFactory;
   private Projog projog;
   private TestResults testResults;

   /**
    * Run the Prolog tests contained in the given file.
    *
    * @param testResources If {@code testResources} is a directory then all test scripts in the directory, and its
    * sub-directories, will be run. If {@code testResources} is a file then all tests contained in the file will be run.
    * @return the results of running the tests
    */
   public static TestResults runTests(File testResources) {
      return runTests(testResources, new ProjogSupplier() {
         @Override
         public Projog get() {
            return new Projog();
         }
      });
   }

   /**
    * Run the Prolog tests contained in the given file.
    *
    * @param testResources If {@code testResources} is a directory then all test scripts in the directory, and its
    * sub-directories, will be run. If {@code testResources} is a file then all tests contained in the file will be run.
    * @param projogFactory Used to obtain the {@link Projog} instance to run the tests against.
    * @return the results of running the tests
    */
   public static TestResults runTests(File testResources, ProjogSupplier projogFactory) {
      List<File> scripts = getScriptsToRun(testResources);
      return new ProjogTestRunner(projogFactory).checkScripts(scripts);
   }

   private ProjogTestRunner(ProjogSupplier projogFactory) {
      this.projogFactory = projogFactory;
   }

   private static List<File> getScriptsToRun(File f) {
      if (!f.exists()) {
         throw new RuntimeException(f.getPath() + " not found");
      }

      List<File> scripts = new ArrayList<>();
      if (f.isDirectory()) {
         findAllScriptsInDirectory(f, scripts);
      } else {
         scripts.add(f);
      }

      // sort to ensure scripts files are always run in a predictable order
      Collections.sort(scripts);

      return scripts;
   }

   private static void findAllScriptsInDirectory(File dir, List<File> scripts) {
      File[] files = dir.listFiles();
      for (File f : files) {
         if (f.isDirectory()) {
            findAllScriptsInDirectory(f, scripts);
         } else if (isPrologScript(f)) {
            scripts.add(f);
         }
      }
   }

   private TestResults checkScripts(List<File> scripts) {
      long start = System.currentTimeMillis();
      testResults = new TestResults(scripts.size());
      for (File script : scripts) {
         // create new Projog each time so rules added from previous scripts
         // don't interfere with results of the script about to be checked
         projog = projogFactory.get();
         projog.addObserver(this);
         checkScript(script);
      }
      testResults.duration = System.currentTimeMillis() - start;
      return testResults;
   }

   private void checkScript(File f) {
      try {
         consultFile(f);
         List<ProjogTestQuery> queries = ProjogTestParser.getQueries(f);
         checkQueries(f, queries);
      } catch (Exception e) {
         debug(e);
         testResults.addError(f, "Error checking Prolog test script: " + f.getPath() + " " + e);
      }
   }

   private void consultFile(File script) {
      println("Checking script: " + script.getPath());
      projog.consultFile(script);
   }

   private void checkQueries(File f, List<ProjogTestQuery> queries) {
      for (ProjogTestQuery query : queries) {
         try {
            checkQuery(query);
         } catch (Exception e) {
            debug(e);
            testResults.addError(f, query.getPrologQuery() + " " + e.getClass() + " " + e.getMessage());
         }
      }
   }

   private void checkQuery(ProjogTestQuery query) {
      debug("QUERY: " + query.getPrologQuery());
      testResults.queryCount++;

      Iterator<ProjogTestAnswer> itr = null;
      Term redirectedOutputFileHandle = null;
      boolean parsedQuery = false;
      try {
         QueryStatement stmt = projog.query(query.getPrologQuery() + ".");
         QueryResult result = stmt.getResult();
         parsedQuery = true;
         itr = query.getAnswers().iterator();

         boolean isExhausted = result.isExhausted();
         redirectedOutputFileHandle = redirectOutput();
         spypointSourceIds.clear();
         while (result.next()) {
            if (isExhausted) {
               throw new RuntimeException("isExhausted() was true when there were still more answers available");
            }
            debug("ANSWERS:");
            if (!itr.hasNext()) {
               throw new RuntimeException("More answers than expected");
            }
            ProjogTestAnswer correctAnswer = itr.next();
            checkOutput(correctAnswer);
            checkAnswer(result, correctAnswer);

            isExhausted = result.isExhausted();

            closeOutput(redirectedOutputFileHandle);
            redirectedOutputFileHandle = redirectOutput();
            spypointSourceIds.clear();
         }
         if (isExhausted == query.isContinuesUntilFails()) {
            if (isInterpretedMode() && query.isContinuesUntilFails() == false) {
               // The "interpreted" mode is not as good as the "compiled" mode at determining that a
               // query will definitely fail when the next attempt to evaluate it is made.
               // Therefore, only when running in "interpreted" mode, do not fail just because
               // the query was executed, and failed, one more time than the tests suggested it should.
            } else {
               throw new RuntimeException("isExhausted was: " + isExhausted + " but query.isContinuesUntilFails was: " + query.isContinuesUntilFails());
            }
         }
         if (query.isContinuesUntilFails()) {
            checkOutput(query.getExpectedOutput());
         }
      } catch (ProjogException pe) {
         String actual = pe.getMessage();
         String expected = query.getExpectedExceptionMessage();
         if (actual.equals(expected) == false) {
            throw new RuntimeException("Expected: >" + expected + "< but got: >" + actual + "<");
         }
      } finally {
         if (parsedQuery) {
            closeOutput(redirectedOutputFileHandle);
         }
      }
      if (parsedQuery && itr.hasNext()) {
         throw new RuntimeException("Less answers than expected for: " + query.getPrologQuery());
      }
   }

   /**
    * Redirect output to a file, rather than <code>System.out</code>, so it can be checked against the expectations.
    *
    * @return a reference to the newly opened file so it can be closed and read from after the tests have run
    * @see #closeOutput(Term)
    */
   private Term redirectOutput() {
      redirectedOutputFile.delete();
      QueryStatement openStmt = projog.query("open('" + redirectedOutputFile.getName() + "',write,Z).");
      QueryResult openResult = openStmt.getResult();
      openResult.next();
      Term redirectedOutputFileHandle = openResult.getTerm("Z");
      QueryStatement setOutputStmt = projog.query("set_output(Z).");
      QueryResult setOutputResult = setOutputStmt.getResult();
      setOutputResult.setTerm("Z", redirectedOutputFileHandle);
      setOutputResult.next();
      return redirectedOutputFileHandle;
   }

   private void checkOutput(ProjogTestAnswer answer) {
      checkOutput(answer.getExpectedOutput());
   }

   private void checkOutput(String expected) {
      byte[] redirectedOutputFileContents = readAllBytes(redirectedOutputFile);
      String actual = new String(redirectedOutputFileContents);
      if (!equalExcludingLineTerminators(expected, actual)) {
         if (isInterpretedMode() && isEqualsIgnoringVariableIds(expected, actual)) {
            // when in interpreted mode, don't fail if only differences are variable names
            return;
         }
         throw new RuntimeException("Expected: >\n" + expected + "\n< but got: >\n" + actual + "\n<");
      }
   }

   private static boolean equalExcludingLineTerminators(String expected, String actual) {
      return toUnixLineEndings(expected).equals(toUnixLineEndings(actual));
   }

   /**
    * Assert that the specified expected and actual output are equal ignoring variable names.
    * <p>
    * There are differences between the compiled and interpreted modes regarding how uninstantiated variables are
    * displayed in output from evaluating a query. As the expected output defined in the tests always refers to the
    * compiled version, when running in interpreted mode we need to ignore differences in variable names between the
    * expected and actual output - while not ignoring any other differences.
    * </p>
    * <p>
    * Example: this method will consider the following two lines to be equal as it is the variable name ( <code>X</code>
    * and <code>A</code>) that are different: <pre>
    * [2] CALL testCalculatables( X, 3, 7 )
    * </pre> <pre>
    * [2] CALL testCalculatables( A, 3, 7 )
    * </pre>
    * </p>
    *
    * @param expected the expected output specified by a system test
    * @param actual the actual output generated by running the query of a system test
    * @return {@code true} if equal apart from variable names, else {@code false}
    */
   private static boolean isEqualsIgnoringVariableIds(String expected, String actual) {
      String actualLines[] = actual.split("\n");
      String expectedLines[] = expected.split("\n");

      if (actualLines.length != expectedLines.length) {
         return false;
      }

      for (int i = 0; i < actualLines.length; i++) {
         String actualLine = actualLines[i];
         String expectedLine = expectedLines[i];
         if (actualLine.equals(expectedLine) == false) {
            if (actualLine.length() == expectedLine.length()) {
               for (int i2 = 0; i2 < actualLine.length(); i2++) {
                  char c1 = actualLine.charAt(i);
                  char c2 = expectedLine.charAt(i);
                  if (c1 != c2 && !Character.isUpperCase(c1) && !Character.isUpperCase(c2)) {
                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Close the file that was used to redirect output from the tests.
    *
    * @param redirectedOutputFileHandle reference to the file to close
    * @see #redirectOutput()
    */
   private void closeOutput(Term redirectedOutputFileHandle) {
      QueryStatement closeStmt = projog.query("close(Z).");
      QueryResult closeResult = closeStmt.getResult();
      closeResult.setTerm("Z", redirectedOutputFileHandle);
      closeResult.next();
      redirectedOutputFile.delete();
   }

   private void checkAnswer(QueryResult result, ProjogTestAnswer correctAnswer) {
      Set<String> variableIds = result.getVariableIds();
      if (variableIds.size() != correctAnswer.getAssignmentsCount()) {
         throw new RuntimeException("Different number of variables than expected. Actual: " + variableIds + " Expected: " + correctAnswer.getAssignments());
      }

      for (String variableId : variableIds) {
         Term variable = result.getTerm(variableId);
         String actualTerm;
         if (variable.getType() == TermType.NAMED_VARIABLE) {
            actualTerm = "UNINSTANTIATED VARIABLE";
         } else {
            actualTerm = projog.toString(variable);
         }

         String expectedTerm = correctAnswer.getAssignedValue(variableId);
         if (expectedTerm == null) {
            throw new RuntimeException(
                        variableId + " (" + variable + ") was not expected to be assigned to anything but was to: " + actualTerm + " " + correctAnswer.getAssignments());
         } else if (!actualTerm.equals(expectedTerm)) {
            throw new RuntimeException(variableId + " (" + variable + ") assigned to: " + actualTerm + " not: " + expectedTerm + " " + correctAnswer.getAssignments());
         }
      }
   }

   /**
    * Writes to the output stream {@code ProjogEvent}s generated by running the tests.
    * <p>
    * Notified of events as is registered as an observer of {@link #projog}
    */
   @Override
   public void update(Observable o, Object arg) {
      ProjogEvent event = (ProjogEvent) arg;
      // currently system tests do not include expectations about WARN or INFO events - so don't check them
      switch (event.getType()) {
         case WARN:
            // log to console
            println(event.getType() + " " + event.getMessage());
            break;
         case INFO:
            // ignore
            break;
         default:
            String message = generateLogMessageForEvent(event);
            writeLogMessage(message);
      }
   }

   /**
    * @return e.g. <code>[2] CALL testCalculatables( X, 3, 7 )</code>
    */
   private String generateLogMessageForEvent(ProjogEvent event) {
      Object source = event.getSource();
      String id;
      if (source == null) {
         id = "?";
      } else {
         Integer i = spypointSourceIds.get(source);
         if (i == null) {
            i = spypointSourceIds.size() + 1;
            spypointSourceIds.put(source, i);
         }
         id = i.toString();
      }
      return "[" + id + "] " + event.getType() + " " + event.getMessage();
   }

   private void writeLogMessage(String message) {
      QueryStatement openStmt = projog.query("write('" + message + "'), nl.");
      QueryResult openResult = openStmt.getResult();
      openResult.next();
   }

   private boolean isInterpretedMode() {
      return !KnowledgeBaseUtils.getProjogProperties(projog.getKnowledgeBase()).isRuntimeCompilationEnabled();
   }

   private static void debug(String s) {
      if (DEBUG) {
         println(s);
      }
   }

   private void debug(Exception e) {
      if (DEBUG) {
         e.printStackTrace(System.out);
      }
   }

   private static void println(String s) {
      System.out.println(s);
   }

   /**
    * Represents the results of running the Prolog tests.
    *
    * @see #assertSuccess()
    */
   public static class TestResults {
      private final StringBuilder errorMessages = new StringBuilder();
      private final int scriptsCount;
      private int queryCount;
      private int errorCount;
      private long duration;

      private TestResults(int scriptsCount) {
         this.scriptsCount = scriptsCount;
      }

      private void addError(File f, String errorDescription) {
         errorMessages.append(f.getName() + " " + errorDescription + "\n");
         errorCount++;
      }

      public int getScriptsCount() {
         return scriptsCount;
      }

      public int getQueryCount() {
         return queryCount;
      }

      public int getErrorCount() {
         return errorCount;
      }

      public boolean hasFailures() {
         return errorCount != 0;
      }

      public String getErrorMessages() {
         return errorMessages.toString();
      }

      /**
       * Throws an exception if any of the tests failed.
       *
       * @throws RuntimeException if any of the tests failes
       */
      public void assertSuccess() {
         if (hasFailures()) {
            throw new RuntimeException(errorCount + " test failures:\n" + errorMessages);
         }
      }

      public String getSummary() {
         StringBuilder sb = new StringBuilder();
         sb.append("Completed " + queryCount + " queries from " + scriptsCount + " files with " + errorCount + " failures in: " + duration + "ms");
         if (hasFailures()) {
            sb.append("\n\n\n ***** Failed: " + errorCount + " tests!!! *****\n\n\n");
            sb.append(errorMessages);
         }
         return sb.toString();
      }
   }

   /**
    * Creates the {@link Projog} instance that tests will be run against.
    * <p>
    * TODO if this project is upgraded from Java 7 then this interface can be replaced with: java.util.function.Supplier
    */
   public interface ProjogSupplier {
      Projog get();
   }
}
