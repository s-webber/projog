package org.projog.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses system test files to produce {@link SysTestContent} objects.
 * <p>
 * System test files contain both standard Prolog syntax plus extra detail contained in comments which specify queries
 * and their expected results. The system tests have two purposes:
 * <ol>
 * <li>To confirm that the Projog software works as required by comparing the expected results contained in the system
 * tests against the actual results generated when evaluating the queries.</li>
 * <li>To produce the examples contained in the web based Projog manual.</li>
 * </ol>
 * </p>
 * <p>
 * Examples of how system tests can be specified using comments (i.e. lines prefixed with a <code>%</code>) are:
 * <ol>
 * <li>Test that that the query <code>?- test().</code> succeeds once and no attempt will be made to find an alternative
 * solution:
 * 
 * <pre>%TRUE test1()</pre>
 * </li>
 * <li>Test that that the query <code>?- test().</code> succeeds once and will fail when an attempt is made to find an
 * alternative solution:
 * 
 * <pre>%TRUE_NO test1()</pre>
 * </li>
 * <li>Test that that the query <code>?- test().</code> will fail on the first attempt to evaluate it:
 * 
 * <pre>%FALSE test1()</pre>
 * </li>
 * <li>Test that that the query <code>?- test().</code> will succeed three times and there will be no attempt to
 * evaluate it for a fourth time:
 * 
 * <pre>
 * %QUERY test()
 * %ANSWER/
 * %ANSWER/
 * %ANSWER/
 * </pre>
 * </li>
 * <li>Test that that the query <code>?- test().</code> will succeed three times and will fail when an attempt is made
 * to evaluate it for a fourth time:
 * 
 * <pre>
 * %QUERY test()
 * %ANSWER/
 * %ANSWER/
 * %ANSWER/
 * %NO
 * </pre>
 * </li>
 * <li>Test that that the query <code>?- test(X).</code> will succeed three times and there will be no attempt to
 * evaluate it for a fourth time, specifying expectations about variable unification:
 * 
 * <pre>
 * %QUERY test(X)
 * %ANSWER X=a
 * %ANSWER X=b
 * %ANSWER X=c
 * </pre>
 * The test contains the following expectations about variable unification:
 * <ul>
 * <li>After the first attempt the variable <code>X</code> will be instantiated to <code>a</code>.</li>
 * <li>After the second attempt the variable <code>X</code> will be instantiated to <code>b</code>.</li>
 * <li>After the third attempt the variable <code>X</code> will be instantiated to <code>c</code>.</li>
 * </ul>
 * </li>
 * <li>Test that that the query <code>?- test(X,Y).</code> will succeed three times and will fail when an attempt is
 * made to evaluate it for a fourth time, specifying expectations about variable unification:
 * 
 * <pre>
 * %QUERY test(X,Y)
 * %ANSWER
 * X=a
 * Y=1
 * %ANSWER
 * %ANSWER
 * X=b
 * Y=2
 * %ANSWER
 * %ANSWER
 * X=c
 * Y=3
 * %ANSWER
 * %NO
 * </pre>
 * The test contains the following expectations about variable unification:
 * <ul>
 * <li>After the first attempt the variable <code>X</code> will be instantiated to <code>a</code> and the variable
 * <code>Y</code> will be instantiated to <code>1</code>.</li>
 * <li>After the second attempt the variable <code>X</code> will be instantiated to <code>b</code> and the variable
 * <code>Y</code> will be instantiated to <code>2</code>.</li>
 * <li>After the third attempt the variable <code>X</code> will be instantiated to <code>c</code> and the variable
 * <code>Y</code> will be instantiated to <code>3</code>.</li>
 * </ul>
 * </li>
 * <li>Test that that the query <code>?- test().</code> will succeed three times and there will be no attempt to
 * evaluate it for a fourth time, specifying expectations about what should be written to standard output:
 * 
 * <pre>
 * %QUERY repeat(3), write('hello world'), nl
 * %OUTPUT 
 * % hello world
 * %
 * %OUTPUT
 * %ANSWER/ 
 * %OUTPUT 
 * % hello world
 * %
 * %OUTPUT
 * %ANSWER/ 
 * %OUTPUT 
 * % hello world
 * %
 * %OUTPUT
 * %ANSWER/
 * </pre>
 * The test contains expectations that every evaluation will cause the text <code>hello world</code> and a new-line
 * character to be written to the standard output stream.</li>
 * <li>Test that while evaluating the query <code>?- repeat(X).</code> an exception will be thrown with a particular
 * message:
 * 
 * <pre>
 * %QUERY repeat(X)
 * %ERROR Expected Numeric but got: NAMED_VARIABLE with value: X
 * </pre>
 * </li>
 * <li>The following would be ignored when running the system tests but would be used when constructing the web based
 * documentation to include a link to <code>test.html</code>:
 * 
 * <pre>%LINK test</pre>
 * </li>
 * </ol>
 * </p>
 * <img src="doc-files/SysTestParser.png">
 */
class SysTestParser {
   private static final String COMMENT_CHARACTER = "%";
   private static final String TRUE_TAG = "%TRUE";
   private static final String TRUE_NO_TAG = "%TRUE_NO";
   private static final String NO_TAG = "%NO";
   private static final String FALSE_TAG = "%FALSE";
   private static final String QUERY_TAG = "%QUERY";
   private static final String ANSWER_TAG = "%ANSWER";
   private static final String ANSWER_NO_VARIABLES_TAG = "%ANSWER/";
   private static final String OUTPUT_TAG = "%OUTPUT";
   private static final String EXCEPTION_TAG = "%ERROR";
   private static final String LINK_TAG = "%LINK";

   /**
    * @throws RuntimeException if script has no tests and no links
    */
   static List<SysTestQuery> getQueries(File testScript) {
      boolean hasLinks = false;
      SysTestParser p = null;
      try {
         List<SysTestQuery> queries = new ArrayList<>();
         p = new SysTestParser(testScript);
         SysTestContent c;
         while ((c = p.getNext()) != null) {
            if (c instanceof SysTestQuery) {
               queries.add((SysTestQuery) c);
            } else if (c instanceof SysTestLink) {
               hasLinks = true;
            }
         }
         if (queries.isEmpty() && !hasLinks) {
            throw new RuntimeException("could not find any tests or links in: " + testScript);
         }
         return queries;
      } catch (IOException e) {
         throw new RuntimeException("Exception parsing test script: " + testScript, e);
      } finally {
         if (p != null) {
            p.close();
         }
      }
   }

   private final BufferedReader br;

   SysTestParser(File testScript) throws FileNotFoundException {
      FileReader fr = new FileReader(testScript);
      br = new BufferedReader(fr);
   }

   SysTestContent getNext() throws IOException {
      final String line = br.readLine();
      if (line == null) {
         // end of file
         return null;
      } else if (line.startsWith(LINK_TAG)) {
         return new SysTestLink(getText(line).trim());
      } else if (line.startsWith(TRUE_NO_TAG)) {
         SysTestQuery query = createSingleCorrectAnswerWithNoAssignmentsQuery(line);
         query.setContinuesUntilFails(true);
         return query;
      } else if (line.startsWith(TRUE_TAG)) {
         return createSingleCorrectAnswerWithNoAssignmentsQuery(line);
      } else if (line.startsWith(FALSE_TAG)) {
         String queryStr = getText(line);
         // no answers
         SysTestQuery query = new SysTestQuery(queryStr);
         query.setContinuesUntilFails(true);
         return query;
      } else if (line.startsWith(QUERY_TAG)) {
         String queryStr = getText(line);
         SysTestQuery query = new SysTestQuery(queryStr);
         query.getAnswers().addAll(getAnswers());
         mark();
         String nextLine = br.readLine();
         if (nextLine != null && nextLine.startsWith(OUTPUT_TAG)) {
            String expectedOutput = readLinesUntilNextTag(nextLine, OUTPUT_TAG);
            query.setExpectedOutput(expectedOutput);
            query.setContinuesUntilFails(true);

            mark();
            nextLine = br.readLine();
         }
         if (nextLine != null && nextLine.startsWith(NO_TAG)) {
            query.setContinuesUntilFails(true);
         } else if (nextLine != null && nextLine.startsWith(EXCEPTION_TAG)) {
            String expectedExceptionMessage = readLinesUntilNextTag(nextLine, EXCEPTION_TAG);
            query.setExpectedExceptionMessage(expectedExceptionMessage);
         } else {
            reset();
         }
         return query;
      } else if (isStandardComment(line)) {
         return new SysTestComment(line.substring(1).trim());
      } else if (isMarkupComment(line)) {
         throw new IllegalArgumentException("Unknown sys-test markup: " + line);
      } else {
         return new SysTestCode(line);
      }
   }

   private SysTestQuery createSingleCorrectAnswerWithNoAssignmentsQuery(String line) {
      String queryStr = getText(line);
      SysTestQuery queryWithSingleCorrectAnswer = new SysTestQuery(queryStr);
      // single correct answer with no assignments
      queryWithSingleCorrectAnswer.getAnswers().add(new SysTestAnswer());
      return queryWithSingleCorrectAnswer;
   }

   private List<SysTestAnswer> getAnswers() throws IOException {
      List<SysTestAnswer> answers = new ArrayList<>();
      SysTestAnswer answer;
      while ((answer = getAnswer()) != null) {
         answers.add(answer);
      }
      return answers;
   }

   private SysTestAnswer getAnswer() throws IOException {
      mark();
      String line = br.readLine();
      if (line == null) {
         // end of file
         return null;
      }
      SysTestAnswer answer = new SysTestAnswer();

      if (line.startsWith(OUTPUT_TAG)) {
         String expectedOutput = readLinesUntilNextTag(line, OUTPUT_TAG);
         line = br.readLine();
         if (line == null) {
            reset();
            return null;
         }
         answer.setExpectedOutput(expectedOutput);
      }
      if (line.startsWith(ANSWER_NO_VARIABLES_TAG)) {
         // query succeeds but no variables to check
         return answer;
      } else if (line.trim().equals(ANSWER_TAG)) {
         // query succeeds with variables to check
         addAssignments(answer);
         return answer;
      } else if (line.startsWith(ANSWER_TAG)) {
         // query succeeds with single variable to check
         addAssignment(answer, line);
         return answer;
      } else {
         reset();
         return null;
      }
   }

   private void addAssignments(SysTestAnswer answer) throws IOException {
      String next;
      while (!(next = br.readLine()).startsWith(ANSWER_TAG)) {
         addAssignment(answer, next);
      }
   }

   private void addAssignment(SysTestAnswer answer, String line) {
      String assignmentStatement = getText(line);
      int equalsPos = assignmentStatement.indexOf('=');
      String variableId = assignmentStatement.substring(0, equalsPos).trim();
      String expectedValue = assignmentStatement.substring(equalsPos + 1).trim();
      answer.addAssignment(variableId, expectedValue);
   }

   private String readLinesUntilNextTag(String line, String tagName) throws IOException {
      String expectedOutput = getText(line).trim();
      if (expectedOutput.length() == 0) {
         boolean first = true;
         StringBuilder sb = new StringBuilder();
         while (!(line = br.readLine()).startsWith(tagName)) {
            line = line.trim().substring(1).trim();
            boolean addNewLine = (first && line.length() == 0) || !first;
            if (addNewLine) {
               sb.append(System.lineSeparator());
            }
            sb.append(line);
            first = false;
         }
         expectedOutput = sb.toString();
      }
      return expectedOutput;
   }

   /**
    * Returns {@code true} if {@code line} represents a "standard" comment.
    * <p>
    * In this context, a "standard" comment is a comment used to provide descriptive human readable messages - rather
    * than "mark-up" comments used to specify system tests. Standard comments are identified by having whitespace
    * directly after the {@code %} comment character.
    */
   private boolean isStandardComment(final String line) {
      return line.startsWith(COMMENT_CHARACTER) && line.length() > 1 && Character.isWhitespace(line.charAt(1));
   }

   /**
    * Returns {@code true} if {@code line} represents a "mark-up" comment.
    * <p>
    * In this context, a "mark-up" comment is a comment used to provide specify system tests. Mark-up comments are
    * identified by having no whitespace directly after the {@code %} comment character.
    */
   private boolean isMarkupComment(final String line) {
      return line.startsWith(COMMENT_CHARACTER) && !isStandardComment(line) && !line.trim().equals(COMMENT_CHARACTER);
   }

   /**
    * Get text minus any sys-test markup.
    * 
    * @param line e.g.: {@code %QUERY X is 1}
    * @return e.g.: {@code X is 1}
    */
   private static String getText(String line) {
      line = line.trim();
      int spacePos = line.indexOf(" ");
      if (spacePos == -1) {
         return "";
      } else {
         return line.substring(spacePos + 1).trim();
      }
   }

   private void mark() throws IOException {
      br.mark(1024);
   }

   private void reset() throws IOException {
      br.reset();
      br.mark(0);
   }

   void close() {
      try {
         if (br != null) {
            br.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}