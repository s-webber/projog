package org.projog.core.function.db;

import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/** Represents a record stored in a {@code RecordedDatabase}. */
class Record {
   private final Term key;
   private final IntegerNumber reference;
   private final Term value;

   Record(Term key, IntegerNumber reference, Term value) {
      this.key = key;
      this.reference = reference;
      this.value = value;
   }

   Term getKey() {
      return key;
   }

   IntegerNumber getReference() {
      return reference;
   }

   Term getValue() {
      return value;
   }
}
