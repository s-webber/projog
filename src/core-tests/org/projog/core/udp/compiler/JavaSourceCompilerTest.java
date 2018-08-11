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
package org.projog.core.udp.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;
import org.projog.core.ProjogException;

public class JavaSourceCompilerTest {
   /** Confirm classes compiled with the same compiler can access each other. */
   @Test
   public void testMultipleClassesSameCompiler() throws Exception {
      JavaSourceCompiler compiler = new JavaSourceCompiler();

      String class1Name = "com.example.Class1";
      Class<?> class1 = compile(compiler, class1Name, //
                  "package com.example;", //
                  "public class Class1 {", //
                  "  public static int PUBLIC_STATIC = 3;", //
                  "  static int PACKAGE_STATIC = 7;", //
                  "  public static String publicStaticMethod() {return \"publicStaticMethod\";}", //
                  "  static String packageStaticMethod() {return \"packageStaticMethod\";}", //
                  "  public String publicMethod() {return \"publicMethod\";}", //
                  "  String packageMethod() {return \"packageMethod\";}", //
                  "}");
      assertEquals(class1Name, class1.getName());

      String class2Name = "com.example.Class2";
      Class<?> class2 = compile(compiler, class2Name, //
                  "package com.example;", //
                  "public class Class2 {", //
                  "  Class1 class1 = new Class1();", //
                  "  public String publicMethod() {", //
                  "    Class1.PUBLIC_STATIC++;", //
                  "    Class1.PACKAGE_STATIC++;", //
                  "    return Class1.PUBLIC_STATIC + \" \" + Class1.PACKAGE_STATIC + \" \" +", //
                  "           Class1.publicStaticMethod() + \" \" + Class1.packageStaticMethod() + \" \" +", //
                  "           class1.publicMethod() + \" \" + class1.packageMethod();", //
                  "  }", //
                  "  public static Class<?> getClass1() {return new Class1().getClass();};", //
                  "}");
      assertEquals(class2Name, class2.getName());

      String class3Name = "com.example.Class3";
      Class<?> class3 = compile(compiler, class3Name, //
                  "package com.example;", //
                  "public class Class3 {", //
                  "  public static String staticPublicMethod() {", //
                  "    Class1.PUBLIC_STATIC++;", //
                  "    Class1.PACKAGE_STATIC++;", //
                  "    return Class1.PUBLIC_STATIC + \" \" + Class1.PACKAGE_STATIC + \" \" + new Class2().publicMethod();", //
                  "  }", //
                  "  public static Class<?> getClass1() {return new Class1().getClass();};", //
                  "}");
      assertEquals(class3Name, class3.getName());

      Object[] noArgs = new Object[0];
      Object objectA = class2.newInstance();
      Object objectB = class3.newInstance();
      assertSame(class1, class2.getMethod("getClass1").invoke(objectA, noArgs));
      assertSame(class1, class3.getMethod("getClass1").invoke(objectB, noArgs));

      Method methodA = class2.getMethod("publicMethod");
      Method methodB = class3.getMethod("staticPublicMethod");
      assertEquals("4 8 publicStaticMethod packageStaticMethod publicMethod packageMethod", methodA.invoke(objectA, noArgs));
      assertEquals("5 9 6 10 publicStaticMethod packageStaticMethod publicMethod packageMethod", methodB.invoke(objectB, noArgs));
      assertEquals("7 11 8 12 publicStaticMethod packageStaticMethod publicMethod packageMethod", methodB.invoke(objectB, noArgs));
      assertEquals("9 13 publicStaticMethod packageStaticMethod publicMethod packageMethod", methodA.invoke(objectA, noArgs));
      assertEquals("10 14 publicStaticMethod packageStaticMethod publicMethod packageMethod", methodA.invoke(objectA, noArgs));
   }

   /** Confirm classes compiled with the different compilers are distinct. */
   @Test
   public void testMultipleCompilers() throws Exception {
      String name = "com.example.MyClass";
      String methodName = "myMethod";
      String source = "package com.example;public class MyClass{private static int X;public static int " + methodName + "(){return X++;}}";

      JavaSourceCompiler compiler1 = new JavaSourceCompiler();
      JavaSourceCompiler compiler2 = new JavaSourceCompiler();

      Class<?> class1 = compile(compiler1, name, source);
      Method method1 = class1.getMethod(methodName);

      Class<?> class2 = compile(compiler2, name, source);
      Method method2 = class2.getMethod(methodName);

      assertEquals(class1.getName(), class2.getName());
      assertNotSame(class1, class2);
      assertEquals(0, method1.invoke(null, new Object[0]));
      assertEquals(1, method1.invoke(null, new Object[0]));
      assertEquals(0, method2.invoke(null, new Object[0]));
      assertEquals(1, method2.invoke(null, new Object[0]));
      assertEquals(2, method2.invoke(null, new Object[0]));
      assertEquals(3, method2.invoke(null, new Object[0]));
      assertEquals(2, method1.invoke(null, new Object[0]));
   }

   @Test
   public void testCompilationFailure() {
      String name = "com.example.MyClass";
      String source = "package com.example;public class MyClass{int x(){i++;}}";
      try {
         new JavaSourceCompiler().compileClass(name, source);
         fail();
      } catch (ProjogException e) {
         assertEquals("Cannot compile: " + name + " source: " + source, e.getMessage());
         assertEquals(ProjogException.class, e.getCause().getClass());
         assertTrue(e.getCause().getMessage().contains("Error on line 1 in /com/example/MyClass.java:1: error: cannot find symbol"));
      }
   }

   private Class<?> compile(JavaSourceCompiler compiler, String name, String... lines) {
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
         sb.append(line).append(System.lineSeparator());
      }
      return compiler.compileClass(name, sb.toString());
   }
}
