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
package org.projog.tools;

import static java.lang.System.out;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.projog.api.Projog;
import org.projog.api.QueryResult;
import org.projog.api.QueryStatement;
import org.projog.core.ProjogException;
import org.projog.core.event.ProjogEvent;
import org.projog.core.parser.ParserException;
import org.projog.core.term.Term;

/**
 * Command line interface to Prolog.
 * <p>
 * Provides a mechanism for users to interact with Projog via a read-evaluate-print loop (REPL).
 * <p>
 * <img src="doc-files/ProjogConsole.png">
 */
public final class ProjogConsole implements Observer {
   // TODO need to unit test this - including testing the generation of error messages when an error occurs while evaluating a query.

   /** Command user can enter to exit the console application. */
   private static final String QUIT_COMMAND = "quit.";

   private final Projog projog;

   private ProjogConsole() {
      projog = new Projog(this);
   }

   private void run(List<String> startupScriptFilenames) throws IOException {
      out.println("Projog Console\nwww.projog.org");

      consultScripts(startupScriptFilenames);

      try (InputStreamReader isr = new InputStreamReader(System.in); BufferedReader in = new BufferedReader(isr)) {
         String inputSyntax;
         do {
            out.print("\n" + QUESTION_PREDICATE_NAME + " ");
            inputSyntax = in.readLine();
            if (isNotEmpty(inputSyntax)) {
               parseAndExecute(inputSyntax, in);
            }
         } while (!QUIT_COMMAND.equals(inputSyntax));
      }
   }

   private static boolean isNotEmpty(String input) {
      return input.trim().length() > 0;
   }

   /**
    * Observer method that informs user of events generated during the evaluation of goals.
    */
   @Override
   public void update(Observable o, Object arg) {
      ProjogEvent event = (ProjogEvent) arg;
      Object source = event.getSource();
      String id = source == null ? "?" : Integer.toString(source.hashCode());
      out.println("[" + id + "] " + event.getType() + " " + event.getMessage());
   }

   private void consultScripts(List<String> scriptFilenames) {
      for (String startupScriptName : scriptFilenames) {
         try {
            File startupScriptFile = new File(startupScriptName);
            projog.consultFile(startupScriptFile);
         } catch (Throwable e) {
            out.println();
            processThrowable(e);
         }
      }
   }

   private void parseAndExecute(String inputSyntax, BufferedReader in) {
      try {
         QueryStatement s = projog.query(inputSyntax);
         QueryResult r = s.getResult();
         Set<String> variableIds = r.getVariableIds();
         while (evaluateOnce(r, variableIds)) {
            waitForPromptToContinue(in);
         }
         out.println();
      } catch (ParserException pe) {
         out.println();
         out.println("Error parsing query:");
         pe.getDescription(System.out);
      } catch (Throwable e) {
         out.println();
         processThrowable(e);
         projog.printProjogStackTrace(e);
      }
   }

   private void waitForPromptToContinue(BufferedReader in) {
      try {
         in.readLine();
      } catch (Exception e) {
         // This shouldn't happen but make stacktrace visible if it does
         e.printStackTrace();
      }
   }

   private void processThrowable(Throwable e) {
      if (e instanceof ParserException) {
         ParserException pe = (ParserException) e;
         out.println("ParserException at line: " + pe.getLineNumber());
         pe.getDescription(System.out);
      } else if (e instanceof ProjogException) {
         out.println(e.getMessage());
         Throwable cause = e.getCause();
         if (cause != null) {
            processThrowable(cause);
         }
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("Caught: ");
         sb.append(e.getClass().getName());
         StackTraceElement ste = e.getStackTrace()[0];
         sb.append(" from class: ");
         sb.append(ste.getClassName());
         sb.append(" method: ");
         sb.append(ste.getMethodName());
         sb.append(" line: ");
         sb.append(ste.getLineNumber());
         out.println(sb);
         String message = e.getMessage();
         if (message != null) {
            out.println("Description: " + message);
         }
      }
   }

   /** Returns {@code true} if {@code QueryResult} can be retried */
   private boolean evaluateOnce(QueryResult r, Set<String> variableIds) {
      long start = System.currentTimeMillis();
      boolean success = r.next();
      if (success) {
         for (String variableId : variableIds) {
            Term answer = r.getTerm(variableId);
            String s = projog.toString(answer);
            out.println(variableId + " = " + s);
         }
         out.println();
         out.print("yes (" + (System.currentTimeMillis() - start) + " ms)");
         return r.isExhausted() == false;
      } else {
         out.println();
         out.print("no (" + (System.currentTimeMillis() - start) + " ms)");
         return false;
      }
   }

   public static void main(String[] args) throws IOException {
      ArrayList<String> startupScriptFilenames = new ArrayList<>();
      for (String arg : args) {
         if (arg.startsWith("-")) {
            out.println();
            out.println("don't know about argument: " + arg);
            System.exit(-1);
         }
         startupScriptFilenames.add(arg);
      }

      ProjogConsole console = new ProjogConsole();
      console.run(startupScriptFilenames);
   }
}