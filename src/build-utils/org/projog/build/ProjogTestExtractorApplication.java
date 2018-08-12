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

import static org.projog.build.BuildUtilsConstants.FUNCTION_PACKAGE_NAME;
import static org.projog.build.BuildUtilsConstants.SCRIPTS_OUTPUT_DIR;
import static org.projog.build.BuildUtilsConstants.SOURCE_INPUT_DIR;

import java.io.File;
import java.io.FileFilter;

import org.projog.test.ProjogTestExtractor;
import org.projog.test.ProjogTestExtractorConfig;

/** Called from build.xml Ant script as part of project's build process. */
public final class ProjogTestExtractorApplication {
   public static final void main(String[] args) throws Exception {
      ProjogTestExtractorConfig config = new ProjogTestExtractorConfig();
      config.setJavaRootDirectory(SOURCE_INPUT_DIR);
      config.setPrologTestsDirectory(new File(SCRIPTS_OUTPUT_DIR, "commands"));
      config.setRequireJavadoc(true);
      config.setRequireTest(true);
      config.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.getPath().replace(File.separatorChar, '.').contains(FUNCTION_PACKAGE_NAME);
         }
      });

      ProjogTestExtractor.extractTests(config);
   }
}
