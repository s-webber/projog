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
package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.Test;

public class CoreUtilsTest {
   @Test
   public void testInstantiateUsingNoArgConstructor() throws Exception {
      String s = CoreUtils.instantiate("java.lang.String");
      assertEquals("", s);
   }

   @Test
   public void testInstantiateUsingStaticMethod() throws Exception {
      Calendar c = CoreUtils.instantiate("java.util.Calendar/getInstance");
      assertNotNull(c);
   }

   @Test(expected = ClassNotFoundException.class)
   public void testInstantiateClassNotFound() throws Exception {
      CoreUtils.instantiate("org.projog.DoesntExist");
   }

   @Test(expected = NoSuchMethodException.class)
   public void testInstantiateNoSuchMethod() throws Exception {
      CoreUtils.instantiate("java.lang.String/getInstance");
   }

   @Test(expected = IllegalAccessException.class)
   public void testInstantiateIllegalAccess() throws Exception {
      CoreUtils.instantiate("java.util.Calendar");
   }
}
