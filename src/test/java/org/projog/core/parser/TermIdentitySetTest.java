/*
 * Copyright 2020 S. Webber
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
package org.projog.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.core.term.Atom;

public class TermIdentitySetTest {
   @Test
   public void test() {
      // create two terms that are equal
      Atom term1 = new Atom("a");
      Atom term2 = new Atom("a");
      assertEquals(term1, term2);

      // create set, assert "contains" returns false for both terms
      TermIdentitySet set = new TermIdentitySet();
      assertFalse(set.contains(term1));
      assertFalse(set.contains(term2));

      // add term to set, assert that "contains" returns true for it but not for the other term
      set.add(term1);
      assertTrue(set.contains(term1));
      assertFalse(set.contains(term2));

      // add other term to set, assert "contains" returns true for both terms
      set.add(term2);
      assertTrue(set.contains(term1));
      assertTrue(set.contains(term2));

      // clear set, assert "contains" returns false for both terms
      set.clear();
      assertFalse(set.contains(term1));
      assertFalse(set.contains(term2));
   }
}
