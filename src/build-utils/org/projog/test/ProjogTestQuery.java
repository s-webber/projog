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
package org.projog.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single system test query.
 * <p>
 * Contains the Prolog syntax of the query plus the expected results of trying to evaluate it.
 */
public final class ProjogTestQuery implements ProjogTestContent {
   private final List<ProjogTestAnswer> answers = new ArrayList<>();
   private final String prologQuery;
   private boolean continuesUntilFails;
   private String expectedExceptionMessage;
   private String expectedOutput = "";

   ProjogTestQuery(String prologQuery) {
      this.prologQuery = prologQuery;
   }

   public String getPrologQuery() {
      return prologQuery;
   }

   public boolean isContinuesUntilFails() {
      return continuesUntilFails;
   }

   void setContinuesUntilFails(boolean continuesUntilFails) {
      this.continuesUntilFails = continuesUntilFails;
   }

   public String getExpectedExceptionMessage() {
      return expectedExceptionMessage;
   }

   void setExpectedExceptionMessage(String expectedExceptionMessage) {
      this.expectedExceptionMessage = expectedExceptionMessage;
   }

   public String getExpectedOutput() {
      return expectedOutput;
   }

   void setExpectedOutput(String expectedOutput) {
      this.expectedOutput = expectedOutput;
   }

   public List<ProjogTestAnswer> getAnswers() {
      return answers;
   }
}
