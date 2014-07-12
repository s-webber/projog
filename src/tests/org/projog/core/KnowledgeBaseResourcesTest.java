package org.projog.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.createKnowledgeBase;

import java.io.Serializable;
import java.util.Map;

import org.junit.Test;

public class KnowledgeBaseResourcesTest {
   /** Tests one-to-one relationship between KnowledgeBase and KnowledgeBaseResources instances */
   @Test
   public void testCreation() {
      KnowledgeBase knowledgeBase1 = createKnowledgeBase();
      KnowledgeBaseResources knowledgeBaseResources1 = KnowledgeBaseResources.getKnowledgeBaseResources(knowledgeBase1);
      assertNotNull(knowledgeBaseResources1);
      assertSame(knowledgeBaseResources1, KnowledgeBaseResources.getKnowledgeBaseResources(knowledgeBase1));

      KnowledgeBase knowledgeBase2 = createKnowledgeBase();
      KnowledgeBaseResources knowledgeBaseResources2 = KnowledgeBaseResources.getKnowledgeBaseResources(knowledgeBase2);
      assertNotNull(knowledgeBaseResources2);
      assertNotSame(knowledgeBaseResources1, knowledgeBaseResources2);
   }

   @Test
   public void testGetResource_OneArgument() {
      KnowledgeBaseResources l = createKnowledgeBaseResources();
      Object o = l.getResource(Object.class);
      assertSame(o, l.getResource(Object.class));

      StringBuilder sb = l.getResource(StringBuilder.class);
      assertSame(sb, l.getResource(StringBuilder.class));
      assertNotSame(sb, o);
      assertNotSame(sb, l.getResource(StringBuffer.class));
   }

   @Test
   public void testGetResource_TwoArguments() {
      KnowledgeBaseResources l = createKnowledgeBaseResources();

      StringBuilder o = l.getResource(Object.class, StringBuilder.class);
      assertSame(o, l.getResource(Object.class, StringBuilder.class));
      assertSame(o, l.getResource(Object.class, StringBuffer.class));
      assertSame(o, l.getResource(Object.class));

      StringBuilder c = l.getResource(CharSequence.class, StringBuilder.class);
      assertSame(c, l.getResource(CharSequence.class, StringBuilder.class));
      assertSame(c, l.getResource(CharSequence.class, StringBuffer.class));
      assertSame(c, l.getResource(CharSequence.class));

      assertNotSame(o, c);
      assertNotSame(o, l.getResource(StringBuilder.class));
      assertNotSame(c, l.getResource(StringBuilder.class));
   }

   @Test
   public void testGetResource_Interface() {
      try {
         createKnowledgeBaseResources().getResource(Serializable.class);
         fail();
      } catch (RuntimeException e) {
         assertEquals("Could not create new instance of resource: interface java.io.Serializable", e.getMessage());
      }
   }

   @Test
   public void testGetResource_NoValidConstructor() {
      try {
         createKnowledgeBaseResources().getResource(Integer.class);
         fail();
      } catch (RuntimeException e) {
         assertEquals("Could not create new instance of resource: class java.lang.Integer", e.getMessage());
      }
   }

   @Test
   public void testGetResource_InstanceDoesNotExtendReference() {
      try {
         createKnowledgeBaseResources().getResource(StringBuffer.class, StringBuilder.class);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("class java.lang.StringBuilder is not of type: class java.lang.StringBuffer", e.getMessage());
      }
   }

   public void testGetResource_InstanceDoesNotImplementReference() {
      try {
         createKnowledgeBaseResources().getResource(Map.class, StringBuilder.class);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("class java.lang.StringBuilder is not of type: interface java.util.Map", e.getMessage());
      }
   }

   @Test
   public void testAddResource() {
      KnowledgeBaseResources l = createKnowledgeBaseResources();
      String s = "hello";
      l.addResource(String.class, s);
      assertSame(s, l.getResource(String.class));
   }

   @Test
   public void testAddResource_IllegalStateException() {
      KnowledgeBaseResources l = createKnowledgeBaseResources();
      l.addResource(String.class, "hello");
      try {
         l.addResource(String.class, "hello");
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Already have a resource with key: class java.lang.String", e.getMessage());
      }
   }

   @Test
   public void testAddResource_IllegalArgumentException() {
      try {
         createKnowledgeBaseResources().addResource(StringBuilder.class, "hello");
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("hello is not of type: class java.lang.StringBuilder", e.getMessage());
      }
   }

   /** Test that the KnowledgeBase gets passed as an argument to the constructor of new resources */
   @Test
   public void testClassWithSingleKnowledgeBaseArgumentConstrutor() {
      KnowledgeBase kb = createKnowledgeBase();
      KnowledgeBaseResources l = KnowledgeBaseResources.getKnowledgeBaseResources(kb);
      Resource r = l.getResource(Resource.class);
      assertSame(r, l.getResource(Resource.class));
      assertSame(kb, r.kb);
   }

   private KnowledgeBaseResources createKnowledgeBaseResources() {
      KnowledgeBase kb = createKnowledgeBase();
      return KnowledgeBaseResources.getKnowledgeBaseResources(kb);
   }

   public static class Resource {
      private final KnowledgeBase kb;

      public Resource(KnowledgeBase kb) {
         this.kb = kb;
      }
   }
}
