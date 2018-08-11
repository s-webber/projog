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
package org.projog.build;

import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.LINE_BREAK;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.htmlEncode;
import static org.projog.build.BuildUtilsConstants.isPrologScript;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.projog.test.ProjogTestAnswer;
import org.projog.test.ProjogTestCode;
import org.projog.test.ProjogTestComment;
import org.projog.test.ProjogTestContent;
import org.projog.test.ProjogTestLink;
import org.projog.test.ProjogTestParser;
import org.projog.test.ProjogTestQuery;

/**
 * Generates web pages containing example Prolog queries and responses.
 * <p>
 * The source for the web pages comes from the {@code .pl} system test files in
 * {@link BuildUtilsConstants#SCRIPTS_OUTPUT_DIR}.
 *
 * @see ProjogTestParser
 */
final class CodeExamplesWebPageCreator {
   List<CodeExampleWebPage> generate(String directoryName) throws Exception {
      // build/scripts gets populated by sys-test task of build script
      File scriptsDir = new File(SCRIPTS_OUTPUT_DIR, directoryName);
      File[] scriptFiles = scriptsDir.listFiles(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return isPrologScript(f);
         }
      });
      List<CodeExampleWebPage> indexOfGeneratedPages = new ArrayList<>();
      for (File scriptFile : scriptFiles) {
         CodeExampleWebPage page = CodeExampleWebPage.create(scriptFile);
         indexOfGeneratedPages.add(page);
         System.out.println("Producing html documentation for: " + scriptFile);
         generateSection(page);
      }
      return indexOfGeneratedPages;
   }

   private void generateSection(CodeExampleWebPage page) throws IOException {
      File htmlFile = page.getHtmlFile();
      try (PrintWriter pw = new PrintWriter(htmlFile)) {
         pw.print(page.getDescription());
         generateExample(pw, page.getPrologSourceFile());
      }
   }

   private void generateExample(PrintWriter pw, File scriptFile) throws IOException {
      try (ProjogTestParser parser = new ProjogTestParser(scriptFile)) {
         generateExample(pw, parser);
      }
   }

   private void generateExample(PrintWriter pw, ProjogTestParser parser) throws IOException {
      pw.println("<h3>Examples</h3><div class=\"example-content\">" + LINE_BREAK);

      boolean inCodeSection = false;
      boolean inQuerySection = false;
      boolean lastLineWasBlank = false;
      ProjogTestContent content;
      while ((content = parser.getNext()) != null) {
         if (content instanceof ProjogTestQuery) {
            if (!inQuerySection) {
               if (inCodeSection) {
                  inCodeSection = false;
                  tableBottom(pw);
                  htmlBreak(pw);
               }
               inQuerySection = true;
               tableTop(pw);
            } else {
               htmlBreak(pw);
               htmlBreak(pw);
            }

            printQuery(pw, (ProjogTestQuery) content);
         } else if (content instanceof ProjogTestComment) {
            if (inCodeSection || inQuerySection) {
               tableBottom(pw);
            }
            inCodeSection = false;
            inQuerySection = false;

            printComment(pw, (ProjogTestComment) content);
         } else if (content instanceof ProjogTestCode) {
            String code = ((ProjogTestCode) content).getPrologCode();
            // ignore leading blank lines at top of code section
            if (code.trim().length() > 0) {
               if (!inCodeSection) {
                  if (inQuerySection) {
                     tableBottom(pw);
                     htmlBreak(pw);
                     inQuerySection = false;
                  }
                  tableTop(pw);
                  inCodeSection = true;
               } else if (lastLineWasBlank) {
                  htmlBreak(pw);
               }

               pw.print(htmlEncode(code));
               htmlBreak(pw);
               lastLineWasBlank = false;
            } else if (inCodeSection) {
               lastLineWasBlank = true;
            }
         } else if (content instanceof ProjogTestLink) {
            printLink(pw, (ProjogTestLink) content);
         } else {
            throw new RuntimeException("don't know about ProjogTestContent: " + content);
         }
      }
      if (inCodeSection || inQuerySection) {
         tableBottom(pw);
      }

      pw.println("</div>");
   }

   private void printQuery(PrintWriter pw, ProjogTestQuery query) throws IOException {
      String question = query.getPrologQuery() + ".";
      programOutput(pw, QUESTION_PREDICATE_NAME + " ");
      userInput(pw, question);
      htmlBreak(pw);

      // iterate through answers, printing variable assignments and system output
      List<ProjogTestAnswer> answers = query.getAnswers();
      ProjogTestAnswer lastAnswer = answers.isEmpty() ? null : answers.get(answers.size() - 1);
      for (ProjogTestAnswer answer : answers) {
         programOutput(pw, answer.getExpectedOutput());
         printVariables(pw, answer);
         htmlBreak(pw);
         programOutput(pw, "yes");
         if (query.isContinuesUntilFails() || answer != lastAnswer) {
            userInput(pw, ";");
            htmlBreak(pw);
         }
      }

      // if required, output any exception or failed evaluation
      if (query.getExpectedExceptionMessage() != null) {
         htmlBreak(pw);
         programOutput(pw, query.getExpectedExceptionMessage());
      } else if (query.isContinuesUntilFails()) {
         programOutput(pw, query.getExpectedOutput());
         htmlBreak(pw);
         programOutput(pw, "no");
      }
   }

   private void printVariables(PrintWriter pw, ProjogTestAnswer answer) throws IOException {
      for (Map.Entry<String, String> assignment : answer.getAssignments()) {
         String variable = assignment.getKey();
         String term = assignment.getValue();
         programOutput(pw, variable + " = " + term);
         htmlBreak(pw);
      }
   }

   private void printComment(PrintWriter pw, ProjogTestComment comment) throws IOException {
      String text = comment.getComment();
      pw.println("<p><span class=\"comment\">" + text + "</span></p>");
   }

   private void printLink(PrintWriter pw, ProjogTestLink link) throws IOException {
      String target = link.getTarget();
      String title = TableOfContentsReader.getTitleForTarget(target);
      pw.println("See <a href=\"" + target + HTML_FILE_EXTENSION + "\">" + title + "</a>");
   }

   private void userInput(PrintWriter pw, String userInput) throws IOException {
      pw.print("<span class=\"input\">");
      pw.print(htmlEncode(userInput));
      pw.print("</span>");
   }

   private void programOutput(PrintWriter pw, String programOutput) throws IOException {
      pw.print("<span class=\"output\">");
      pw.print(htmlEncode(programOutput));
      pw.print("</span>");
   }

   private void tableTop(PrintWriter pw) throws IOException {
      pw.println("<div class=\"code\"><code>");
   }

   private void tableBottom(PrintWriter pw) throws IOException {
      pw.println("</code></div>");
   }

   private void htmlBreak(PrintWriter pw) throws IOException {
      pw.println("<br>");
   }
}
