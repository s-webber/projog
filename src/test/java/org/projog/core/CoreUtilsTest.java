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
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.junit.Test;

public class CoreUtilsTest {
   @Test
   public void testInstantiateUsingNoArgConstructor() throws Exception {
      String s = CoreUtils.instantiate(null, "java.lang.String");
      assertEquals("", s);
   }

   @Test
   public void testInstantiateUsingStaticMethod() throws Exception {
      Calendar c = CoreUtils.instantiate(null, "java.util.Calendar/getInstance");
      assertNotNull(c);
   }

   @Test(expected = ClassNotFoundException.class)
   public void testInstantiateClassNotFound() throws Exception {
      CoreUtils.instantiate(null, "org.projog.DoesntExist");
   }

   @Test(expected = NoSuchMethodException.class)
   public void testInstantiateNoSuchMethod() throws Exception {
      CoreUtils.instantiate(null, "java.lang.String/getInstance");
   }

   @Test(expected = IllegalAccessException.class)
   public void testInstantiateIllegalAccess() throws Exception {
      CoreUtils.instantiate(null, "java.util.Calendar");
   }

   @Test
   public void testKnowledgeBaseConsumer_noArgConstructor() throws Exception {
      KnowledgeBase knowledgeBase = new KnowledgeBase(new ProjogDefaultProperties());
      KnowledgeBaseConsumerNoArgConstructorExample o = CoreUtils.instantiate(knowledgeBase, "org.projog.core.CoreUtilsTest$KnowledgeBaseConsumerNoArgConstructorExample");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, KnowledgeBaseConsumerNoArgConstructorExample.INSTANCE_CTR);
   }

   @Test
   public void testKnowledgeBaseConsumer_staticMethod() throws Exception {
      KnowledgeBase knowledgeBase = new KnowledgeBase(new ProjogDefaultProperties());
      KnowledgeBaseConsumerStaticMethodExample o = CoreUtils.instantiate(knowledgeBase, "org.projog.core.CoreUtilsTest$KnowledgeBaseConsumerStaticMethodExample/create");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, KnowledgeBaseConsumerStaticMethodExample.INSTANCE_CTR);
   }

   public static class KnowledgeBaseConsumerNoArgConstructorExample implements KnowledgeBaseConsumer {
      static int INSTANCE_CTR;

      KnowledgeBase kb;

      public KnowledgeBaseConsumerNoArgConstructorExample() {
         INSTANCE_CTR++;
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
         if (this.kb != null || knowledgeBase == null) {
            throw new IllegalStateException();
         }
         this.kb = knowledgeBase;
      }
   }

   public static class KnowledgeBaseConsumerStaticMethodExample implements KnowledgeBaseConsumer {
      static int INSTANCE_CTR;

      KnowledgeBase kb;

      public static KnowledgeBaseConsumerStaticMethodExample create() {
         INSTANCE_CTR++;
         return new KnowledgeBaseConsumerStaticMethodExample();
      }

      private KnowledgeBaseConsumerStaticMethodExample() {
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
         if (this.kb != null || knowledgeBase == null) {
            throw new IllegalStateException();
         }
         this.kb = knowledgeBase;
      }
   }
}
