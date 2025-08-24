/*
 * Copyright 2013 S. Webber
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
package org.projog.core.kb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.junit.Test;

public class CoreUtilsTest {
   @Test
   public void testInstantiateUsingNoArgConstructor() throws Exception {
      String s = KnowledgeBaseUtils.instantiate(null, "java.lang.String");
      assertEquals("", s);
   }

   @Test
   public void testInstantiateUsingStaticMethod() throws Exception {
      Calendar c = KnowledgeBaseUtils.instantiate(null, "java.util.Calendar/getInstance");
      assertNotNull(c);
   }

   @Test(expected = ClassNotFoundException.class)
   public void testInstantiateClassNotFound() throws Exception {
      KnowledgeBaseUtils.instantiate(null, "org.projog.DoesntExist");
   }

   @Test(expected = NoSuchMethodException.class)
   public void testInstantiateNoSuchMethod() throws Exception {
      KnowledgeBaseUtils.instantiate(null, "java.lang.String/getInstance");
   }

   @Test(expected = IllegalAccessException.class)
   public void testInstantiateIllegalAccess() throws Exception {
      KnowledgeBaseUtils.instantiate(null, "java.util.Calendar");
   }

   @Test
   public void testKnowledgeBaseConsumer_noArgConstructor() throws Exception {
      KnowledgeBase knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
      KnowledgeBaseConsumerNoArgConstructorExample o = KnowledgeBaseUtils.instantiate(knowledgeBase,
                  "org.projog.core.kb.CoreUtilsTest$KnowledgeBaseConsumerNoArgConstructorExample");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, KnowledgeBaseConsumerNoArgConstructorExample.INSTANCE_CTR);
   }

   @Test
   public void testKnowledgeBaseConsumer_singleArgConstructor() throws Exception {
      KnowledgeBase knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
      SingleArgConstructorExample o = KnowledgeBaseUtils.instantiate(knowledgeBase, "org.projog.core.kb.CoreUtilsTest$SingleArgConstructorExample");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, SingleArgConstructorExample.INSTANCE_CTR);
   }

   @Test
   public void testKnowledgeBaseConsumer_noArgStaticMethod() throws Exception {
      KnowledgeBase knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
      KnowledgeBaseConsumerStaticMethodExample o = KnowledgeBaseUtils.instantiate(knowledgeBase,
                  "org.projog.core.kb.CoreUtilsTest$KnowledgeBaseConsumerStaticMethodExample/create");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, KnowledgeBaseConsumerStaticMethodExample.INSTANCE_CTR);
   }

   @Test
   public void testKnowledgeBaseConsumer_singleArgStaticMethod() throws Exception {
      KnowledgeBase knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
      SingleArgStaticMethodExample o = KnowledgeBaseUtils.instantiate(knowledgeBase, "org.projog.core.kb.CoreUtilsTest$SingleArgStaticMethodExample/create");
      assertNotNull(o);
      assertSame(knowledgeBase, o.kb);
      assertEquals(1, SingleArgStaticMethodExample.INSTANCE_CTR);
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

   public static class SingleArgConstructorExample {
      static int INSTANCE_CTR;

      final KnowledgeBase kb;

      public SingleArgConstructorExample(KnowledgeBase kb) {
         this.kb = kb;
         INSTANCE_CTR++;
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

   public static class SingleArgStaticMethodExample {
      static int INSTANCE_CTR;

      final KnowledgeBase kb;

      public static SingleArgStaticMethodExample create(KnowledgeBase kb) {
         INSTANCE_CTR++;
         return new SingleArgStaticMethodExample(kb);
      }

      private SingleArgStaticMethodExample(KnowledgeBase kb) {
         this.kb = kb;
      }
   }
}
