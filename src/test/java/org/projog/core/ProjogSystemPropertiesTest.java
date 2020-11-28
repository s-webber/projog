/*
 * Copyright 2020 S. Webber
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
package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjogSystemPropertiesTest {
   @Before
   @After
   public void clearSystemProperties() {
      System.clearProperty("projog.compile");
      System.clearProperty("projog.compiledContentOutputDirectory");
   }

   @Test
   public void testDefault() {
      ProjogSystemProperties p = new ProjogSystemProperties();
      assertEquals("projog-bootstrap.pl", p.getBootstrapScript());
      assertFalse(p.isRuntimeCompilationEnabled());
      assertNull(p.getCompiledContentOutputDirectory());
   }

   @Test
   public void testCompileSystemProperty_true() {
      System.setProperty("projog.compile", "true");

      ProjogSystemProperties p = new ProjogSystemProperties();
      assertTrue(p.isRuntimeCompilationEnabled());
   }

   @Test
   public void testCompileSystemProperty_false() {
      System.setProperty("projog.compile", "false");

      ProjogSystemProperties p = new ProjogSystemProperties();
      assertFalse(p.isRuntimeCompilationEnabled());
   }

   @Test
   public void testOutputDirectorySystemProperty() {
      System.setProperty("projog.compiledContentOutputDirectory", "testSystemProperties");

      ProjogSystemProperties p = new ProjogSystemProperties();
      assertEquals("testSystemProperties", p.getCompiledContentOutputDirectory().getName());
   }
}
