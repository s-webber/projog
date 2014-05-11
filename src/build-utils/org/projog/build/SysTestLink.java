package org.projog.build;

/**
 * Represents a link contained in a system test file.
 * 
 * @see SysTestParser
 */
class SysTestLink implements SysTestContent {
   final String target;

   SysTestLink(String target) {
      this.target = target;
   }
}