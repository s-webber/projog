package org.projog.core.parser;

/** @see WordParser#next() */
class Word {
   final String value;
   final WordType type;

   Word(String value, WordType type) {
      this.value = value;
      this.type = type;
   }

   @Override
   public String toString() {
      return value;
   }
}
