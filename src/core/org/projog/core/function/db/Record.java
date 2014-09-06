package org.projog.core.function.db;

import static org.projog.core.term.TermUtils.createAnonymousVariable;

import java.util.Arrays;

import org.projog.core.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/** Represents a record stored in a {@code RecordedDatabase}. */
class Record {
   private final PredicateKey key;
   private final IntegerNumber reference;
   private final Term value;

   Record(PredicateKey key, IntegerNumber reference, Term value) {
      this.key = key;
      this.reference = reference;
      this.value = value;
   }

   Term getKey() {
      String name = key.getName();
      int numArgs = key.getNumArgs();
      if (numArgs == 0) {
         return new Atom(name);
      } else {
         Term[] args = new Term[numArgs];
         Arrays.fill(args, createAnonymousVariable());
         return Structure.createStructure(name, args);
      }
   }

   IntegerNumber getReference() {
      return reference;
   }

   Term getValue() {
      return value;
   }
}
