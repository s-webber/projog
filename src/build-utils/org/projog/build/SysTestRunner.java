/*
 * Copyright 2013 S Webber
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
package org.projog.build;

import static org.projog.build.BuildUtilsConstants.PROLOG_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.toByteArray;

import java.io.File;
import java.util.ArrayList;
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
import org.projog.core.ProjogException;
import org.projog.core.ProjogSystemProperties;
import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventType;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Runs system tests comparing actual output to expected results.
 * <p>
 * The system tests can be run in two modes - interpreted and compiled. Although they should give the same result there
 * are some subtle differences about how the results may be presented. As the expected output specified by the tests
 * always refers to what the compiled version should do, there are some "workarounds" for confirming the output of
 * running in interpreted mode.
 * 
 * @see SysTestParser
 */
public class SysTestRunner implements Observer {
   private static final boolean DEBUG = false;
   private static final File REDIRECTED_OUTPUT_FILE = new File("SysTestRunnerOutput.tmp");
   private static final ProjogSystemProperties SYSTEM_PROPERTIES = new ProjogSystemProperties();

   private final StringBuilder errorMessages = new StringBuilder();
   private final Map<Object, Integer> spypointSourceIds = new HashMap<Object, Integer>();
   private Projog projog;
   private int numQueries;
   private int errorCtr;

   private SysTestRunner() {
   }

   private static List<File> getScriptsToRun(String file) {
      File f = new File(file);
      if (!f.exists()) {
         throw new RuntimeException(f.getPath() + " not found");
      }

      List<File> scripts = new ArrayList<File>();
      if (f.isDirectory()) {
         findAllScriptsInDirectory(f, scripts);
      } else {
         scripts.add(f);
      }
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

   private static boolean isPrologScript(File f) {
      return f.getName().endsWith(PROLOG_FILE_EXTENSION);
   }

   private void checkScripts(List<File> scripts) {
      for (File script : scripts) {
         // create new Projog each time so rules added from previous scripts
         // don't interfere with results of the script about to be checked
         projog = new Projog(SYSTEM_PROPERTIES);
         checkScript(script);
      }
   }

   private void checkScript(File f) {
      try {
         consultFile(f);
         List<SysTestQuery> queries = SysTestParser.getQueries(f);
         int currentErrorCtr = errorCtr;
         checkQueries(f, queries);
         if (currentErrorCtr == errorCtr) {
            checkAutomaticallyGeneratedSysTests(queries);
         }
      } catch (Exception e) {
         String msg = "Error checking systest script: " + f.getPath() + " " + e;
         System.out.println(msg);
         e.printStackTrace();
         errorMessages.append(f.getName() + " " + msg + "\n");
         errorCtr++;
      }
   }

   private void consultFile(File script) {
      debug("consulting: " + script);
      projog.consultFile(script);
   }

   private void checkQueries(File f, List<SysTestQuery> queries) {
      projog.addObserver(this);
      for (SysTestQuery query : queries) {
         try {
            checkQuery(query);
         } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n***********************\n\n" + "Exception caught executing: " + query.getQueryStr() + " from: " + f.getPath() + "\nclass: " + e.getClass() + "\nmessage: " + e.getMessage());
            errorMessages.append(f.getName() + " " + query.getQueryStr() + " " + e.getClass() + " " + e.getMessage() + "\n");
            errorCtr++;
         }
      }
   }

   private void checkQuery(SysTestQuery query) {
      debug("QUERY: " + query.getQueryStr());
      numQueries++;

      Iterator<SysTestAnswer> itr = null;
      Term redirectedOutputFileHandle = null;
      boolean parsedQuery = false;
      try {
         QueryStatement stmt = projog.query(query.getQueryStr() + ".");
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
            SysTestAnswer correctAnswer = itr.next();
            checkOutput(correctAnswer);
            checkAnswer(result, correctAnswer);

            isExhausted = result.isExhausted();

            closeOutput(redirectedOutputFileHandle);
            redirectedOutputFileHandle = redirectOutput();
            spypointSourceIds.clear();
         }
         if (isExhausted == query.isContinuesUntilFails()) {
            if (isInterpretedMode() && query.isContinuesUntilFails() == false) {
               // The interpreted mode is not as good as the compiled mode as determining that a
               // query definitely fail when an next attempt to evaluate it is made.
               // Therefore, only when running in interpreted mode, do not fail just because
               // the query was executed, and failed, once more then the tests suggested it should. 
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
            throw new RuntimeException("Expected: " + expected + " but got: " + actual);
         }
      } finally {
         if (parsedQuery) {
            closeOutput(redirectedOutputFileHandle);
         }
      }
      if (parsedQuery && itr.hasNext()) {
         throw new RuntimeException("Less answers than expected");
      }
   }

   /**
    * Redirect output to a file, rather than <code>System.out</code>, so it can be checked against the expectations.
    * 
    * @return a reference to the newly opened file so it can be closed and read from after the tests have run
    * @see #closeOutput(Term)
    */
   private Term redirectOutput() {
      REDIRECTED_OUTPUT_FILE.delete();
      QueryStatement openStmt = projog.query("open('" + REDIRECTED_OUTPUT_FILE.getName() + "',write,Z).");
      QueryResult openResult = openStmt.getResult();
      openResult.next();
      Term redirectedOutputFileHandle = openResult.getTerm("Z");
      QueryStatement setOutputStmt = projog.query("set_output(Z).");
      QueryResult setOutputResult = setOutputStmt.getResult();
      setOutputResult.setTerm("Z", redirectedOutputFileHandle);
      setOutputResult.next();
      return redirectedOutputFileHandle;
   }

   private void checkOutput(SysTestAnswer answer) {
      checkOutput(answer.getExpectedOutput());
   }

   private void checkOutput(String expected) {
      byte[] redirectedOutputFileContents = toByteArray(REDIRECTED_OUTPUT_FILE);
      String actual = new String(redirectedOutputFileContents);
      if (!expected.equals(actual)) {
         if (isInterpretedMode() && isEqualsIgnoringVariableIds(expected, actual)) {
            // when in interpreted mode, don't fail if only differences are variable names
            return;
         }
         throw new RuntimeException("expected: >\n" + expected + "\n< but got: >\n" + actual + "\n<");
      }
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
    * and <code>A</code>) that are different:
    * 
    * <pre>[2] CALL testCalculatables( X, 3, 7 )</pre>
    * 
    * <pre>[2] CALL testCalculatables( A, 3, 7 )</pre>
    * </p>
    * 
    * @param expected the expected output specified by a system test
    * @param actual the actual output generated by running the query of a system test
    * @return {@code true} if equal apart from variable names, else {@code false}
    */
   private boolean isEqualsIgnoringVariableIds(String expected, String actual) {
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
      REDIRECTED_OUTPUT_FILE.delete();
   }

   private void checkAnswer(QueryResult result, SysTestAnswer correctAnswer) {
      Set<String> variableIds = result.getVariableIds();
      if (variableIds.size() != correctAnswer.getAssignmentsCount()) {
         throw new RuntimeException("Different number of variables than expected. Actual: " + variableIds + " Expected: " + correctAnswer);
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
            throw new RuntimeException(variableId + " (" + variable + ") was not expected to be assigned to anything but was to: " + actualTerm + " " + correctAnswer);
         } else if (!actualTerm.equals(expectedTerm)) {
            throw new RuntimeException(variableId + " (" + variable + ") assigned to: " + actualTerm + " not: " + expectedTerm + " " + correctAnswer);
         }
      }
   }

   private void checkAutomaticallyGeneratedSysTests(List<SysTestQuery> queries) throws Exception {
      /*
      AutomaticallyGeneratedSysTests agst = AutomaticallyGeneratedSysTests.generateFromExistingQueries(projog, queries);
      consultFile(agst.script);
      int currentErrorCtr = errorCtr;
      checkQueries(agst.script, agst.queries);
      if (currentErrorCtr==errorCtr) {
      	agst.script.delete(); // delete if get here with no errors
      }
      */
   }

   /**
    * Writes to the output stream {@code ProjogEvent}s generated by running the tests.
    * <p>
    * Notified of events as is registered as an observer of {@link #projog}
    */
   @Override
   public void update(Observable o, Object arg) {
      ProjogEvent event = (ProjogEvent) arg;
      if (event.getType() == ProjogEventType.WARN || event.getType() == ProjogEventType.INFO) {
         // currently system tests do not include expectation about WARN or INFO events -
         // so ignore any generated at runtime
         return;
      }

      String message = generateLogMessageForEvent(event);
      writeLogMessage(message);
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
      return SYSTEM_PROPERTIES.isRuntimeCompilationEnabled() == false;
   }

   private static void debug(String s) {
      if (DEBUG) {
         System.out.println(s);
      }
   }

   public static final void main(String[] args) throws Exception {
      long start = System.currentTimeMillis();

      String arg;
      if (args.length == 0) {
         arg = "build/scripts";
         System.out.println();
         System.out.println("As no arguments provided, defaulting to running all system tests in " + arg);
         System.out.println();
      } else if (args.length == 1) {
         arg = args[0];
      } else {
         throw new RuntimeException("More than one argument supplied");
      }

      List<File> scripts = getScriptsToRun(arg);

      SysTestRunner st = new SysTestRunner();
      st.checkScripts(scripts);

      System.out.println("\nCompleted: " + st.numQueries + " queries from: " + scripts.size() + " files in: " + (System.currentTimeMillis() - start) + "ms");
      if (st.errorCtr != 0) {
         System.out.println("\n\n\n ***** Failed: " + st.errorCtr + " tests!!! *****\n\n\n");
         System.out.println(st.errorMessages);
         System.exit(-1);
      }
   }
}