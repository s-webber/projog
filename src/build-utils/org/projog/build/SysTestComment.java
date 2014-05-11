package org.projog.build;

/**
 * Represents a Prolog comment contained in a system test file.
 * 
 * @see SysTestParser
 */
class SysTestComment implements SysTestContent {
   final String comment;

   SysTestComment(String comment) {
      this.comment = comment;
   }
}