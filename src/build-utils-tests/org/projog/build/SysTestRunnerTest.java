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
