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
import static org.junit.Assert.assertSame;
import static org.projog.build.BuildUtilsConstants.DOCS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsTestConstants.TEST_RESOURCES_DIR;

import java.io.File;

import org.junit.Test;

public class CodeExampleWebPageTest {
   @Test
   public void test() {
      File prologSourceFile = new File(TEST_RESOURCES_DIR, "org.projog.abc.Qwerty.pl");
      CodeExampleWebPage p = CodeExampleWebPage.create(prologSourceFile);
      String expectedHtmlFileName = "Qwerty.html";
      assertEquals(expectedHtmlFileName, p.getHtmlFileName());
      assertEquals("How to Qwerty", p.getTitle());
      assertEquals("<p>\na description\nof Qwerty...\n</p>\n", p.getDescription());
      assertEquals(p.getHtmlFile(), new File(DOCS_OUTPUT_DIR, expectedHtmlFileName));
      assertSame(prologSourceFile, p.getPrologSourceFile());
   }
}
