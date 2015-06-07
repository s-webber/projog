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
import static org.projog.build.BuildUtilsConstants.readText;
import static org.projog.build.BuildUtilsConstants.toUnixLineEndings;
import static org.projog.build.BuildUtilsTestConstants.TEST_RESOURCES_DIR;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class SysTestRunnerTest {
   @Test
   public void test() {
      List<File> testScripts = SysTestRunner.getScriptsToRun(TEST_RESOURCES_DIR.getPath());
      assertEquals(4, testScripts.size());
      SysTestRunner.Result r = SysTestRunner.checkScripts(testScripts);
      assertEquals(23, r.getQueryCount());
      assertEquals(13, r.getErrorCount());
      assertIgnoringCarriageReturns(getExpectedErrorMessages(), r.getErrorMessages());
   }

   private void assertIgnoringCarriageReturns(String expected, String actual) {
      assertEquals(toUnixLineEndings(expected), toUnixLineEndings(actual));
   }

   private String getExpectedErrorMessages() {
      return readText(new File(TEST_RESOURCES_DIR, "SysTestRunnerTest_ExpectedErrors.txt"));
   }
}
