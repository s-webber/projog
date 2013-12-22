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

import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.LINE_BREAK;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates web pages containing example Prolog queries and responses.
 * <p>
 * The source for the web pages comes from the {@code .pl} system test files in
 * {@link BuildUtilsConstants#SCRIPTS_OUTPUT_DIR}.
 * 
 * @see SysTestParser
 */
class CodeExamplesWebPageCreator {
   private FileWriter fw;
   private BufferedWriter bw;

   List<CodeExampleWebPage> generate(String directoryName) throws Exception {
      // build/scripts gets populated by sys-test task of build script
      File scriptsDir = new File(SCRIPTS_OUTPUT_DIR, directoryName);
      File[] scriptFiles = scriptsDir.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".pl");
         }
      });
      List<CodeExampleWebPage> indexOfGeneratedPages = new ArrayList<CodeExampleWebPage>();
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
      fw = new FileWriter(htmlFile);
      bw = new BufferedWriter(fw);
      List<String> textFileContents = page.getDescription();
      for (String s : textFileContents) {
         println(s);
      }
      generateExample(page.getPrologSourceFile());
      bw.close();
      fw.close();
   }

   private void generateExample(File scriptFile) throws IOException {
      println("<h3>Examples</h3><div class=\"example-content\">" + LINE_BREAK);

      SysTestParser sysTestParser = new SysTestParser(scriptFile);
      boolean inCodeSection = false;
      boolean inQuerySection = false;
      boolean lastLineWasBlank = false;
      SysTestContent content;
      while ((content = sysTestParser.getNext()) != null) {
         if (content instanceof SysTestQuery) {
            if (!inQuerySection) {
               if (inCodeSection) {
                  inCodeSection = false;
                  tableBottom();
                  htmlBreak();
               }
               inQuerySection = true;
               tableTop();
            } else {
               htmlBreak();
               htmlBreak();
            }

            // print query
            SysTestQuery query = (SysTestQuery) content;
            String question = query.getQueryStr() + ".";
            programOutput(QUESTION_PREDICATE_NAME);
            userInput(question);
            htmlBreak();

            // iterate through answers, printing variable assignments and system output
            List<SysTestAnswer> answers = query.getAnswers();
            SysTestAnswer lastAnswer = answers.isEmpty() ? null : answers.get(answers.size() - 1);
            for (SysTestAnswer answer : answers) {
               programOutput(answer.getExpectedOutput());
               for (Map.Entry<String, String> assignment : answer.getAssignments()) {
                  String variable = assignment.getKey();
                  String term = assignment.getValue();
                  programOutput(variable + " = " + term);
                  htmlBreak();
               }
               htmlBreak();
               programOutput("yes");
               if (query.isContinuesUntilFails() || answer != lastAnswer) {
                  userInput(";");
                  htmlBreak();
               }
            }

            // if required, output any exception or failed evaluation
            if (query.getExpectedExceptionMessage() != null) {
               htmlBreak();
               programOutput(query.getExpectedExceptionMessage());
            } else if (query.isContinuesUntilFails()) {
               htmlBreak();
               programOutput("no");
            }
         } else if (content instanceof SysTestComment) {
            if (inCodeSection || inQuerySection) {
               tableBottom();
            }
            inCodeSection = false;
            inQuerySection = false;

            String comment = ((SysTestComment) content).comment;
            println("<span class=\"comment\">" + comment + "</span>");
         } else if (content instanceof SysTestCode) {
            String code = ((SysTestCode) content).code;
            // ignore leading blank lines at top of code section
            if (code.trim().length() > 0) {
               if (!inCodeSection) {
                  if (inQuerySection) {
                     tableBottom();
                     htmlBreak();
                     inQuerySection = false;
                  }
                  tableTop();
                  inCodeSection = true;
               } else if (lastLineWasBlank) {
                  htmlBreak();
               }

               print(encode(code));
               htmlBreak();
               lastLineWasBlank = false;
            } else if (inCodeSection) {
               lastLineWasBlank = true;
            }
         } else if (content instanceof SysTestLink) {
            SysTestLink link = (SysTestLink) content;
            String target = link.target;
            String title = TableOfContentsReader.getTitleForTarget(target);
            println("See <a href=\"" + target + HTML_FILE_EXTENSION + "\">" + title + "</a>");
         } else {
            throw new RuntimeException("don't know about SysTestContent: " + content);
         }
      }
      if (inCodeSection || inQuerySection) {
         tableBottom();
      }

      println("</div>");
   }

   private void userInput(String userInput) throws IOException {
      print("<span class=\"input\">");
      print(encode(userInput));
      print("</span>");
   }

   private void programOutput(String programOutput) throws IOException {
      print("<span class=\"output\">");
      print(encode(programOutput));
      print("</span>");
   }

   private String encode(String input) {
      return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("  ", "&nbsp;&nbsp;").replace(LINE_BREAK, "<br>" + LINE_BREAK);
   }

   private void tableTop() throws IOException {
      println("<div class=\"code\"><code>");
   }

   private void tableBottom() throws IOException {
      println("</code></div>");
   }

   private void println(String l) throws IOException {
      print(l);
      carriageReturn();
   }

   private void print(String l) throws IOException {
      bw.write(l);
   }

   private void carriageReturn() throws IOException {
      bw.newLine();
   }

   private void htmlBreak() throws IOException {
      print("<br>");
      carriageReturn();
   }
}