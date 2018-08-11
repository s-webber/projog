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

import org.projog.test.ProjogTestGenerator;
import org.projog.test.ProjogTestGeneratorConfig;

/** Called from build.xml Ant script as part of project's build process. */
public final class ProjogTestGeneratorApplication {
   public static final void main(String[] args) throws Exception {
      ProjogTestGeneratorConfig config = new ProjogTestGeneratorConfig();
      config.setJavaRootDirectory(SOURCE_INPUT_DIR);
      config.setPackageName(FUNCTION_PACKAGE_NAME);
      config.setPrologTestsDirectory(new File(SCRIPTS_OUTPUT_DIR, "commands"));
      config.setRequireJavadoc(true);
      config.setRequireTest(true);

      ProjogTestGenerator.generate(config);
   }
}
