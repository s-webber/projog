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

import static org.projog.core.term.TermUtils.getAtomName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.projog.core.term.Atom;
import org.projog.core.term.Term;

/**
 * Collection of input and output streams.
 * <p>
 * Each {@link org.projog.core.KnowledgeBase} has a single unique {@code FileHandles} instance.
 * 
 * @see KnowledgeBaseUtils#getFileHandles(KnowledgeBase)
 */
public final class FileHandles {
   public static final Atom USER_OUTPUT_HANDLE = new Atom("user_output");
   public static final Atom USER_INPUT_HANDLE = new Atom("user_input");

   private final Object lock = new Object();
   private final Map<String, InputStream> inputHandles = new HashMap<>();
   private final Map<String, PrintStream> outputHandles = new HashMap<>();

   /** Current input used by get_char and read */
   private Term currentInputHandle;
   /** Current output used by put_char, nl, write and write_canonical */
   private Term currentOutputHandle;

   private InputStream in;
   private PrintStream out;

   FileHandles() {
      Atom userInputHandle = USER_INPUT_HANDLE;
      Atom userOutputHandle = USER_OUTPUT_HANDLE;
      inputHandles.put(userInputHandle.getName(), System.in);
      outputHandles.put(userOutputHandle.getName(), System.out);
      setInput(userInputHandle);
      setOutput(userOutputHandle);
   }

   /**
    * Return the {@code Term} representing the current input stream.
    * <p>
    * By default this will be an {@code Atom} with the name "{@code user_input}".
    */
   public Term getCurrentInputHandle() {
      return currentInputHandle;
   }

   /**
    * Return the {@code Term} representing the current output stream.
    * <p>
    * By default this will be an {@code Atom} with the name "{@code user_output}".
    */
   public Term getCurrentOutputHandle() {
      return currentOutputHandle;
   }

   /**
    * Return the current input stream.
    * <p>
    * By default this will be {@code System.in}.
    */
   public InputStream getCurrentInputStream() {
      return in;
   }

   /**
    * Return the current output stream.
    * <p>
    * By default this will be {@code System.out}.
    */
   public PrintStream getCurrentOutputStream() {
      return out;
   }

   /**
    * Sets the current input stream to the input stream represented by the specified {@code Term}.
    * 
    * @throws ProjogException if the specified {@link Term} does not represent an {@link Atom}
    */
   public void setInput(Term handle) {
      String handleName = getAtomName(handle);
      synchronized (lock) {
         if (inputHandles.containsKey(handleName)) {
            currentInputHandle = handle;
            in = inputHandles.get(handleName);
         } else {
            throw new ProjogException("cannot find file input handle with name:" + handleName);
         }
      }
   }

   /**
    * Sets the current output stream to the output stream represented by the specified {@code Term}.
    * 
    * @throws ProjogException if the specified {@link Term} does not represent an {@link Atom}
    */
   public void setOutput(Term handle) {
      String handleName = getAtomName(handle);
      synchronized (lock) {
         if (outputHandles.containsKey(handleName)) {
            currentOutputHandle = handle;
            out = outputHandles.get(handleName);
         } else {
            throw new ProjogException("cannot find file output handle with name:" + handleName);
         }
      }
   }

   /**
    * Creates an intput file stream to read from the file with the specified name
    * 
    * @param fileName the system-dependent filename
    * @return a reference to the newly created stream (as required by {@link #setInput(Term)} and {@link #close(Term)})
    * @throws ProjogException if this object's collection of input streams already includes the specified file
    * @throws IOException if the file cannot be opened for reading
    */
   public Atom openInput(String fileName) throws IOException {
      String handleName = fileName + "_input_handle";
      synchronized (lock) {
         if (inputHandles.containsKey(handleName)) {
            throw new ProjogException("Can not open input for: " + fileName + " as it is already open");
         } else {
            InputStream is = new FileInputStream(fileName);
            inputHandles.put(handleName, is);
         }
      }
      return new Atom(handleName);
   }

   /**
    * Creates an output file stream to write to the file with the specified name
    * 
    * @param fileName the system-dependent filename
    * @return a reference to the newly created stream (as required by {@link #setOutput(Term)} and {@link #close(Term)})
    * @throws ProjogException if this object's collection of output streams already includes the specified file
    * @throws IOException if the file cannot be opened
    */
   public Atom openOutput(String fileName) throws IOException {
      String handleName = fileName + "_output_handle";
      synchronized (lock) {
         if (outputHandles.containsKey(handleName)) {
            throw new ProjogException("Can not open output for: " + fileName + " as it is already open");
         } else {
            OutputStream os = new FileOutputStream(fileName);
            outputHandles.put(handleName, new PrintStream(os));
         }
      }
      return new Atom(handleName);
   }

   /**
    * Closes the stream represented by the specified {@code Term}.
    * 
    * @throws ProjogException if the specified {@link Term} does not represent an {@link Atom}
    * @throws IOException if an I/O error occurs
    */
   public void close(Term handle) throws IOException {
      String handleName = getAtomName(handle);
      synchronized (lock) {
         PrintStream ps = outputHandles.get(handleName);
         if (ps != null) {
            outputHandles.remove(handleName);
            ps.close();
            return;
         }
         InputStream is = inputHandles.get(handleName);
         if (is != null) {
            inputHandles.remove(handleName);
            is.close();
            return;
         }
      }
   }

   public boolean isHandle(String handle) {
      return inputHandles.containsKey(handle) || outputHandles.containsKey(handle);
   }
}
