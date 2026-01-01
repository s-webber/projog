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

import static org.projog.core.kb.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.projog.api.Projog;
import org.projog.api.QueryResult;
import org.projog.api.QueryStatement;
import org.projog.core.ProjogException;
import org.projog.core.event.LoggingProjogListener;
import org.projog.core.parser.EndOfStreamException;
import org.projog.core.parser.ParserException;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Term;

/**
 * Command line interface to Prolog.
 * <p>
 * Provides a mechanism for users to interact with Projog via a read-evaluate-print loop (REPL).
 * </p>
 * <img src="doc-files/ProjogConsole.png" alt="Class diagram">
 */
public class ProjogConsole {
   /** Command user can enter to exit the console application. */
   private static final PredicateKey QUIT_COMMAND = new PredicateKey("quit", 0);
   private static final String CONTINUE_EVALUATING = ";";
   private static final String STOP_EVALUATING = "q";

   private final Scanner in;
   private final PrintStream out;
   private final Projog projog;
   private boolean quit;

   ProjogConsole(InputStream in, PrintStream out) {
      this.in = new Scanner(in);
      this.out = out;
      this.projog = new Projog(new LoggingProjogListener(out));
      this.projog.addPredicateFactory(QUIT_COMMAND, new AbstractSingleResultPredicate() {
         @Override
         protected boolean evaluate() {
            quit = true;
            return true;
         }
      });
   }

   void run(List<String> startupScriptFilenames) throws IOException {
      out.println("Projog Console");
      out.println("projog.org");

      consultScripts(startupScriptFilenames);

      while (!quit) {
         printPrompt();
         parseAndExecute();
      }
   }

   private void printPrompt() {
      out.println();
      out.print(QUESTION_PREDICATE_NAME + " ");
   }

   private void consultScripts(List<String> scriptFilenames) {
      for (String startupScriptName : scriptFilenames) {
         consultScript(startupScriptName);
      }
   }

   private void consultScript(String startupScriptName) {
      try {
         File startupScriptFile = new File(startupScriptName);
         projog.consultFile(startupScriptFile);
      } catch (Throwable e) {
         out.println();
         processThrowable(e);
      }
   }

   private void parseAndExecute() {
      try {
         String inputSyntax = "";
         QueryStatement s = null;
         while (s == null) {
            inputSyntax += in.nextLine();
            if (inputSyntax.trim().length() != 0) {
               try {
                  s = projog.createPlan(inputSyntax).createStatement();
               } catch (EndOfStreamException pe) {
                  inputSyntax += System.lineSeparator();
               }
            }
         }

         QueryResult r = s.executeQuery();
         Set<String> variableIds = r.getVariableIds();
         while (evaluateOnce(r, variableIds) && shouldContinue()) {
            // keep evaluating the query
         }
         out.println();
      } catch (ParserException pe) {
         out.println();
         out.println("Error parsing query:");
         pe.getDescription(out);
      } catch (Throwable e) {
         out.println();
         processThrowable(e);
         projog.printProjogStackTrace(e);
      }
   }

   private boolean shouldContinue() {
      while (true) {
         String input = in.nextLine();
         if (CONTINUE_EVALUATING.equals(input)) {
            return true;
         } else if (STOP_EVALUATING.equals(input)) {
            return false;
         } else {
            out.print("Invalid. Enter ; to continue or q to quit. ");
         }
      }
   }

   private void processThrowable(Throwable e) {
      if (e instanceof ParserException) {
         ParserException pe = (ParserException) e;
         out.println("ParserException at line: " + pe.getLineNumber());
         pe.getDescription(out);
      } else if (e instanceof ProjogException) {
         out.println(e.getMessage());
         Throwable cause = e.getCause();
         if (cause != null) {
            processThrowable(cause);
         }
      } else {
         StackTraceElement ste = e.getStackTrace()[0];
         out.println("Caught: " + e.getClass().getName() + " from class: " + ste.getClassName() + " method: " + ste.getMethodName() + " line: " + ste.getLineNumber());
         String message = e.getMessage();
         if (message != null) {
            out.println("Description: " + message);
         }
      }
   }

   /** Returns {@code true} if {@code QueryResult} can be re-tried */
   private boolean evaluateOnce(QueryResult r, Set<String> variableIds) {
      long start = System.currentTimeMillis();
      boolean success = r.next();
      if (success) {
         printVariableAssignments(r, variableIds);
      }
      printOutcome(success, System.currentTimeMillis() - start);
      return success && !r.isExhausted();
   }

   private void printVariableAssignments(QueryResult r, Set<String> variableIds) {
      if (!variableIds.isEmpty()) {
         out.println();
         for (String variableId : variableIds) {
            Term answer = r.getTerm(variableId);
            String s = projog.formatTerm(answer);
            out.println(variableId + " = " + s);
         }
      }
   }

   private void printOutcome(boolean success, long timing) {
      out.println();
      out.print(success ? "yes" : "no");
      out.print(" (");
      out.print(timing);
      out.print(" ms)");
   }

   public static void main(String[] args) throws IOException {
      ArrayList<String> startupScriptFilenames = new ArrayList<>();
      for (String arg : args) {
         if (arg.startsWith("-")) {
            System.out.println();
            System.out.println("don't know about argument: " + arg);
            System.exit(-1);
         }
         startupScriptFilenames.add(arg);
      }

      ProjogConsole console = new ProjogConsole(System.in, System.out);
      console.run(startupScriptFilenames);
   }
}
