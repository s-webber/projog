package org.projog.core.parser;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Reads characters from a {@code BufferedReader}.
 * <p>
 * Provides details of current line and column number being parsed.
 * </p>
 * 
 * @see SentenceParser#getInstance(java.io.Reader, org.projog.core.Operands)
 */
final class CharacterParser {
   private final BufferedReader br;
   private String currentLine;
   /**
    * The line number of the current line being parsed.
    * <p>
    * Required in order to provide useful information if a {@link ParserException} is thrown.
    */
   private int lineNumber;
   /**
    * The position, within the current line, of the character being parsed.
    * <p>
    * Required in order to provide useful information if a {@link ParserException} is thrown.
    */
   private int columnNumber;

   CharacterParser(BufferedReader br) {
      this.br = br;
   }

   /**
    * Reads a single character.
    * <p>
    * Every call to {@code getNext()} causes the parser to move forward one character - meaning that by making repeated
    * calls to {@code getNext()} all characters in the the underlying stream represented by this object will be
    * returned.
    * 
    * @return The character read, as an integer in the range 0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the end of the
    * stream has been reached
    * @exception ParserException if an I/O error occurs
    * @see #peek()
    */
   int getNext() {
      try {
         if (currentLine == null) {
            currentLine = br.readLine();
            if (currentLine == null) {
               return -1;
            }
            lineNumber++;
         }

         if (columnNumber == currentLine.length()) {
            columnNumber++;
            return '\n';
         }
         while (columnNumber >= currentLine.length()) {
            String nextLine = br.readLine();
            if (nextLine == null) {
               return -1;
            }
            currentLine = nextLine;
            lineNumber++;
            columnNumber = 0;
         }
         return currentLine.charAt(columnNumber++);
      } catch (IOException e) {
         throw new ParserException("Unexpected exception getting next character", this, e);
      }
   }

   /**
    * Reads a single character but does not consume it.
    * <p>
    * Calls to {@code getNext()} do not cause the parser to move forward one character - meaning that adjacent calls to
    * {@code peek()} will return the same value.
    * 
    * @return The character read, as an integer in the range 0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the end of the
    * stream has been reached
    * @exception ParserException if an I/O error occurs
    * @see #getNext()
    */
   int peek() {
      int i = getNext();
      rewind();
      return i;
   }

   /**
    * Moves the parser back one character.
    * <p>
    * Calls to this method will leave this object in the same state it was before the previous {@link #getNext()} call
    * on it. i.e. {@code p.getNext();p.rewind();} is the same as {@code peek();}
    */
   void rewind() {
      columnNumber--;
   }

   /**
    * Skips the remainder of the line currently being parsed.
    */
   void skipLine() {
      columnNumber = currentLine.length();
   }

   /**
    * Returns the entire contents of the line currently being parsed.
    */
   String getLine() {
      return currentLine;
   }

   /**
    * Returns the line number of the line currently being parsed.
    */
   int getLineNumber() {
      return lineNumber;
   }

   /**
    * Returns the index, in the line currently being parsed, of the character that will be returned by the next call to
    * {@link #getNext()}.
    */
   int getColumnNumber() {
      return columnNumber;
   }
}