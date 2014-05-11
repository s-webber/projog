package org.projog.build;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single system test query.
 * <p>
 * Contains the Prolog syntax of the query plus the expected results of trying to evaluate it.
 */
class SysTestQuery implements SysTestContent {
   private final List<SysTestAnswer> answers = new ArrayList<>();
   private final String queryStr;
   private boolean continuesUntilFails;
   private String expectedExceptionMessage;
   private String expectedOutput = "";

   SysTestQuery(String queryStr) {
      this.queryStr = queryStr;
   }

   String getQueryStr() {
      return queryStr;
   }

   boolean isContinuesUntilFails() {
      return continuesUntilFails;
   }

   void setContinuesUntilFails(boolean continuesUntilFails) {
      this.continuesUntilFails = continuesUntilFails;
   }

   String getExpectedExceptionMessage() {
      return expectedExceptionMessage;
   }

   void setExpectedExceptionMessage(String expectedExceptionMessage) {
      this.expectedExceptionMessage = expectedExceptionMessage;
   }

   String getExpectedOutput() {
      return expectedOutput;
   }

   void setExpectedOutput(String expectedOutput) {
      this.expectedOutput = expectedOutput;
   }

   List<SysTestAnswer> getAnswers() {
      return answers;
   }
}