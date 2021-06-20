/*
 * Copyright 2021 S. Webber
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
package org.projog;

import org.projog.core.term.Atom;
import org.projog.core.term.DecimalFraction;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.List;
import org.projog.core.term.ListFactory;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/** Creates instances of {@code Term} for use in unit-tests. */
public class TermFactory {
   /**
    * Private constructor as all methods are static.
    */
   private TermFactory() {
      // do nothing
   }

   public static Atom atom() {
      return atom("test");
   }

   public static Atom atom(String name) {
      return new Atom(name);
   }

   public static Structure structure() {
      return structure("test", new Term[] {atom()});
   }

   public static Structure structure(String name, Term... args) {
      return (Structure) Structure.createStructure(name, args);
   }

   public static List list(Term... args) {
      return (List) ListFactory.createList(args);
   }

   public static IntegerNumber integerNumber() {
      return integerNumber(1);
   }

   public static IntegerNumber integerNumber(long i) {
      return new IntegerNumber(i);
   }

   public static DecimalFraction decimalFraction() {
      return decimalFraction(1.0);
   }

   public static DecimalFraction decimalFraction(double d) {
      return new DecimalFraction(d);
   }

   public static Variable variable() {
      return variable("X");
   }

   public static Variable variable(String name) {
      return new Variable(name);
   }
}
