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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.build.BuildUtilsConstants.readText;
import static org.projog.build.BuildUtilsConstants.toUnixLineEndings;
import static org.projog.build.BuildUtilsTestConstants.TEST_RESOURCES_DIR;

import java.io.File;

import org.junit.Test;

public class ProjogTestRunnerTest {
   @Test
   public void test() {
      final String expectedErrorMessages = getExpectedErrorMessages();
      final int expectedScriptsCount = 4;
      final int expectedQueryCount = 23;
      final int expectedErrorCount = 13;

      ProjogTestRunner.Result r = ProjogTestRunner.test(TEST_RESOURCES_DIR);
      assertEquals(expectedScriptsCount, r.getScriptsCount());
      assertEquals(expectedQueryCount, r.getQueryCount());
      assertEquals(expectedErrorCount, r.getErrorCount());
      assertIgnoringCarriageReturns(expectedErrorMessages, r.getErrorMessages());
      assertTrue(r.hasFailures());
      assertTrue(r.getSummary().startsWith("Completed " + expectedQueryCount + " queries from " + expectedScriptsCount + " files with " + expectedErrorCount + " failures"));

      try {
         r.assertSuccess();
         fail();
      } catch (RuntimeException e) {
         assertIgnoringCarriageReturns(expectedErrorCount + " test failures: " + expectedErrorMessages, e.getMessage());
      }
   }

   private void assertIgnoringCarriageReturns(String expected, String actual) {
      assertEquals(toUnixLineEndings(expected), toUnixLineEndings(actual));
   }

   private String getExpectedErrorMessages() {
      return readText(new File(TEST_RESOURCES_DIR, "ProjogTestRunnerTest_ExpectedErrors.txt"));
   }
}
