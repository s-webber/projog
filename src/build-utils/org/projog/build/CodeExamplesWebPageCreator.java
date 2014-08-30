package org.projog.build;

import static org.projog.build.BuildUtilsConstants.HTML_FILE_EXTENSION;
import static org.projog.build.BuildUtilsConstants.LINE_BREAK;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.isPrologScript;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
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
      pw.println("<h3>Examples</h3><div class=\"example-content\">" + LINE_BREAK);

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
                  tableBottom(pw);
                  htmlBreak(pw);
               }
               inQuerySection = true;
               tableTop(pw);
            } else {
               htmlBreak(pw);
               htmlBreak(pw);
            }

            printQuery(pw, (SysTestQuery) content);
         } else if (content instanceof SysTestComment) {
            if (inCodeSection || inQuerySection) {
               tableBottom(pw);
            }
            inCodeSection = false;
            inQuerySection = false;

            printComment(pw, (SysTestComment) content);
         } else if (content instanceof SysTestCode) {
            String code = ((SysTestCode) content).code;
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

               pw.print(encode(code));
               htmlBreak(pw);
               lastLineWasBlank = false;
            } else if (inCodeSection) {
               lastLineWasBlank = true;
            }
         } else if (content instanceof SysTestLink) {
            printLink(pw, (SysTestLink) content);
         } else {
            throw new RuntimeException("don't know about SysTestContent: " + content);
         }
      }
      if (inCodeSection || inQuerySection) {
         tableBottom(pw);
      }

      pw.println("</div>");
   }

   private void printQuery(PrintWriter pw, SysTestQuery query) throws IOException {
      String question = query.getQueryStr() + ".";
      programOutput(pw, QUESTION_PREDICATE_NAME + " ");
      userInput(pw, question);
      htmlBreak(pw);

      // iterate through answers, printing variable assignments and system output
      List<SysTestAnswer> answers = query.getAnswers();
      SysTestAnswer lastAnswer = answers.isEmpty() ? null : answers.get(answers.size() - 1);
      for (SysTestAnswer answer : answers) {
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

   private void printVariables(PrintWriter pw, SysTestAnswer answer) throws IOException {
      for (Map.Entry<String, String> assignment : answer.getAssignments()) {
         String variable = assignment.getKey();
         String term = assignment.getValue();
         programOutput(pw, variable + " = " + term);
         htmlBreak(pw);
      }
   }

   private void printComment(PrintWriter pw, SysTestComment comment) throws IOException {
      String text = comment.comment;
      pw.println("<p><span class=\"comment\">" + text + "</span></p>");
   }

   private void printLink(PrintWriter pw, SysTestLink link) throws IOException {
      String target = link.target;
      String title = TableOfContentsReader.getTitleForTarget(target);
      pw.println("See <a href=\"" + target + HTML_FILE_EXTENSION + "\">" + title + "</a>");
   }

   private void userInput(PrintWriter pw, String userInput) throws IOException {
      pw.print("<span class=\"input\">");
      pw.print(encode(userInput));
      pw.print("</span>");
   }

   private void programOutput(PrintWriter pw, String programOutput) throws IOException {
      pw.print("<span class=\"output\">");
      pw.print(encode(programOutput));
      pw.print("</span>");
   }

   private String encode(String input) {
      return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("  ", "&nbsp;&nbsp;").replace(LINE_BREAK, "<br>" + LINE_BREAK);
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