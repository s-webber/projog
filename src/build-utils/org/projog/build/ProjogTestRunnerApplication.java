/*
 * Copyright 2018 S. Webber
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

import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;

import java.io.File;

import org.projog.test.ProjogTestRunner;
import org.projog.test.ProjogTestRunner.TestResults;

/** Called from build.xml Ant script as part of project's build process. */
public final class ProjogTestRunnerApplication {
   public static final void main(String[] args) throws Exception {
      File testResourcesDir;
      if (args.length == 0) {
         testResourcesDir = SCRIPTS_OUTPUT_DIR;
         System.out.println("As no arguments provided, defaulting to running all system tests in " + testResourcesDir);
      } else if (args.length == 1) {
         testResourcesDir = new File(args[0]);
      } else {
         throw new RuntimeException("More than one argument supplied");
      }

      TestResults result = ProjogTestRunner.runTests(testResourcesDir);
      System.out.println(result.getSummary());
      logMemory();
      if (result.hasFailures()) {
         System.exit(-1);
      }
   }

   private static void logMemory() {
      Runtime r = Runtime.getRuntime();
      logMemory(r);
      r.gc();
      logMemory(r);
   }

   private static void logMemory(Runtime r) {
      long totalMemory = r.totalMemory();
      long freeMemory = r.freeMemory();
      System.out.println("Max memory: " + r.maxMemory() + " Total memory: " + totalMemory + " Free memory: " + freeMemory + " Used memory: " + (totalMemory - freeMemory));
   }
}
