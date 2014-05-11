package org.projog.build;

/**
 * Represents Prolog syntax defining clauses contained in a system test file.
 * <p>
 * The clauses will be added to the knowledge base before any of the queries contained in the system file are evaluated.
 * 
 * @see SysTestParser
 */
class SysTestCode implements SysTestContent {
   final String code;

   SysTestCode(String code) {
      this.code = code;
   }
}