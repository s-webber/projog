package org.projog.build;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents an answer to a query contained in a system test file.
 * 
 * @see SysTestParser
 */
class SysTestAnswer {
   /**
    * Text expected to be written to the standard output stream as part of determining this answer.
    */
   private String expectedOuput = "";

   /**
    * Values expected to be assigned to query variables as part of determining this answer.
    * <p>
    * Key = variable id. Value = String representation of assigned term.
    */
   private final Map<String, String> assignments = new TreeMap<String, String>();

   void setExpectedOutput(String expectedOuput) {
      this.expectedOuput = expectedOuput;
   }

   String getExpectedOutput() {
      return expectedOuput;
   }

   void addAssignment(String variableId, String expectedValue) {
      assignments.put(variableId, expectedValue);
   }

   String getAssignedValue(String variableId) {
      return assignments.get(variableId);
   }

   Set<Map.Entry<String, String>> getAssignments() {
      return assignments.entrySet();
   }

   int getAssignmentsCount() {
      return assignments.size();
   }

   @Override
   public String toString() {
      return "[" + super.toString() + " " + assignments.toString() + "]";
   }
}