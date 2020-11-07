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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.TestUtils.atom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.Test;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;

public class FileHandlesTest {
   @Test
   public void testUserInputHandle() {
      assertEquals("user_input", FileHandles.USER_INPUT_HANDLE.getName());
   }

   @Test
   public void testUserOutputHandle() {
      assertEquals("user_output", FileHandles.USER_OUTPUT_HANDLE.getName());
   }

   @Test
   public void testDefaultInputStream() {
      FileHandles fh = new FileHandles();
      assertSame(System.in, fh.getCurrentInputStream());
   }

   @Test
   public void testDefaultOutputStream() {
      FileHandles fh = new FileHandles();
      assertSame(System.out, fh.getCurrentOutputStream());
   }

   @Test
   public void testDefaultInputHandle() {
      FileHandles fh = new FileHandles();
      Term expected = new Atom("user_input");
      Term actual = fh.getCurrentInputHandle();
      assertTrue(expected.strictEquality(actual));
   }

   @Test
   public void testDefaultOutputHandle() {
      FileHandles fh = new FileHandles();
      Term expected = new Atom("user_output");
      Term actual = fh.getCurrentOutputHandle();
      assertTrue(expected.strictEquality(actual));
   }

   @Test
   public void testSetUserInputWhenCurrent() {
      FileHandles fh = new FileHandles();

      // given the standard stream is also the current stream
      assertSame(FileHandles.USER_INPUT_HANDLE, fh.getCurrentInputHandle());

      // when we reassign the standard stream
      InputStream is = new ByteArrayInputStream(new byte[0]);
      fh.setUserInput(is);

      // then the current stream should be updated
      assertSame(is, fh.getCurrentInputStream());
   }

   @Test
   public void testSetUserInputWhenNotCurrent() throws IOException {
      FileHandles fh = new FileHandles();

      // set input to something other than the standard stream
      String filename = createFileName("testSetUserInputWhenNotCurrentInput");
      Files.createFile(new File(filename).toPath());
      Term handle = fh.openInput(filename);
      fh.setInput(handle);

      // reassign the standard stream
      InputStream is = new ByteArrayInputStream(new byte[0]);
      fh.setUserInput(is);

      // confirm that reassigning the standard stream has not altered the current input
      assertSame(handle, fh.getCurrentInputHandle());
      assertNotSame(is, fh.getCurrentInputStream());

      // switch back to the standard stream and confirm it has been reassigned
      fh.setInput(FileHandles.USER_INPUT_HANDLE);
      assertSame(is, fh.getCurrentInputStream());
   }

   @Test
   public void testSetUserOutputWhenCurrent() {
      FileHandles fh = new FileHandles();

      // given the standard stream is also the current stream
      assertSame(FileHandles.USER_OUTPUT_HANDLE, fh.getCurrentOutputHandle());

      // when we reassign the standard stream
      PrintStream ps = new PrintStream(new ByteArrayOutputStream());
      fh.setUserOutput(ps);

      // then the current stream should be updated
      assertSame(ps, fh.getCurrentOutputStream());
   }

   @Test
   public void testSetUserOutputWhenNotCurrent() throws IOException {
      FileHandles fh = new FileHandles();

      // set output to something other than the standard stream
      Term handle = fh.openOutput(createFileName("testSetUserOutputWhenNotCurrentOutput"));
      fh.setOutput(handle);

      // reassign the standard stream
      PrintStream ps = new PrintStream(new ByteArrayOutputStream());
      fh.setUserOutput(ps);

      // confirm that reassigning the standard stream has not altered the current output
      assertSame(handle, fh.getCurrentOutputHandle());
      assertNotSame(ps, fh.getCurrentOutputStream());

      // switch back to the standard stream and confirm it has been reassigned
      fh.setOutput(FileHandles.USER_OUTPUT_HANDLE);
      assertSame(ps, fh.getCurrentOutputStream());
   }

   @Test
   public void testSetInputFailure() {
      FileHandles fh = new FileHandles();
      Term t = atom("test");
      try {
         fh.setInput(t);
         fail("could set input for unopened file");
      } catch (ProjogException e) {
         assertEquals("cannot find file input handle with name: test", e.getMessage());
      }
   }

   @Test
   public void testSetOutputFailure() {
      FileHandles fh = new FileHandles();
      Term t = atom("test");
      try {
         fh.setInput(t);
         fail("could set output for unopened file");
      } catch (ProjogException e) {
         assertEquals("cannot find file input handle with name: test", e.getMessage());
      }
   }

   @Test
   public void testWriteAndRead() throws IOException {
      FileHandles fh = new FileHandles();
      String filename = createFileName("testWriteAndRead");
      String contentsToWrite = "test";
      write(fh, filename, contentsToWrite);
      String contentsRead = read(fh, filename);
      assertEquals(contentsToWrite, contentsRead);
   }

   @Test
   public void testIsHandle() throws IOException {
      FileHandles fh = new FileHandles();
      String filename = createFileName("testIsHandle");
      Term handle = openOutput(fh, filename);
      assertTrue(fh.isHandle(handle.getName()));
      fh.close(handle);
      assertFalse(fh.isHandle(handle.getName()));
   }

   private String createFileName(String name) {
      return "target/" + getClass().getName() + "_" + name + "_" + System.currentTimeMillis() + ".tmp";
   }

   private void write(FileHandles fh, String filename, String contents) throws IOException {
      Term handle = openOutput(fh, filename);
      fh.setOutput(handle);
      assertSame(handle, fh.getCurrentOutputHandle());
      PrintStream ps = fh.getCurrentOutputStream();
      ps.append(contents);
      fh.close(handle);
      assertFalse(ps.checkError());
      ps.append("extra stuff after close was called");
      assertTrue(ps.checkError());
   }

   private String read(FileHandles fh, String filename) throws IOException {
      Term handle = openInput(fh, filename);
      fh.setInput(handle);
      assertSame(handle, fh.getCurrentInputHandle());
      InputStream is = fh.getCurrentInputStream();
      String contents = "";
      int next;
      while ((next = is.read()) != -1) {
         contents += (char) next;
      }
      fh.close(handle);
      try {
         is.read();
         fail("could read from closed input stream");
      } catch (IOException e) {
         // expected now stream has been closed
      }
      return contents;
   }

   private Term openOutput(FileHandles fh, String filename) throws IOException {
      Term handle = fh.openOutput(filename);
      try {
         fh.openOutput(filename);
         fail("was able to reopen already opened file for output");
      } catch (ProjogException e) {
         assertEquals("Can not open output for: " + filename + " as it is already open", e.getMessage());
      }
      return handle;
   }

   private Term openInput(FileHandles fh, String filename) throws IOException {
      Term handle = fh.openInput(filename);
      try {
         fh.openInput(filename);
         fail("was able to reopen already opened file for input");
      } catch (ProjogException e) {
         assertEquals("Can not open input for: " + filename + " as it is already open", e.getMessage());
      }
      return handle;
   }
}
