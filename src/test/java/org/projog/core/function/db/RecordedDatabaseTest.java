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
package org.projog.core.function.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.projog.core.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

public class RecordedDatabaseTest {
   @Test
   public void testAdd() {
      RecordedDatabase d = new RecordedDatabase();
      PredicateKey key = new PredicateKey("a", 1);
      Atom value = new Atom("test");

      assertEquals(0L, d.add(key, value, true).getLong());
      assertEquals(1L, d.add(key, value, true).getLong());
      assertEquals(2L, d.add(key, value, false).getLong());
      assertEquals(3L, d.add(key, value, false).getLong());
      assertEquals(4L, d.add(key, value, true).getLong());
   }

   @Test
   public void testGetAll_Empty() {
      RecordedDatabase d = new RecordedDatabase();

      Iterator<Record> itr = d.getAll();

      assertFalse(itr.hasNext());
      assertNoMoreElements(itr);
   }

   @Test
   public void testGetAll_SingleElement() {
      RecordedDatabase d = new RecordedDatabase();

      PredicateKey key = new PredicateKey("a", 1);
      Atom value = new Atom("test");
      Term reference = d.add(key, value, true);

      Iterator<Record> itr = d.getAll();

      assertNext(itr, key, reference, value);

      assertNoMoreElements(itr);
   }

   @Test
   public void testGetAll_MultipleElementsAddLast() {
      RecordedDatabase d = new RecordedDatabase();

      PredicateKey key = new PredicateKey("a", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key, value1, true);
      IntegerNumber reference2 = d.add(key, value2, true);
      IntegerNumber reference3 = d.add(key, value3, true);

      Iterator<Record> itr = d.getAll();

      assertNext(itr, key, reference1, value1);
      assertNext(itr, key, reference2, value2);
      assertNext(itr, key, reference3, value3);

      assertNoMoreElements(itr);
   }

   @Test
   public void testGetAll_MultipleElementsAddFirst() {
      RecordedDatabase d = new RecordedDatabase();

      PredicateKey key = new PredicateKey("a", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key, value1, false);
      IntegerNumber reference2 = d.add(key, value2, false);
      IntegerNumber reference3 = d.add(key, value3, false);

      Iterator<Record> itr = d.getAll();

      assertNext(itr, key, reference3, value3);
      assertNext(itr, key, reference2, value2);
      assertNext(itr, key, reference1, value1);

      assertNoMoreElements(itr);
   }

   @Test
   public void testGetAll_MultipleElementsAddFirstAndLast() {
      RecordedDatabase d = new RecordedDatabase();

      PredicateKey key = new PredicateKey("a", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key, value1, true);
      IntegerNumber reference2 = d.add(key, value2, false);
      IntegerNumber reference3 = d.add(key, value3, true);

      Iterator<Record> itr = d.getAll();

      assertNext(itr, key, reference2, value2);
      assertNext(itr, key, reference1, value1);
      assertNext(itr, key, reference3, value3);

      assertNoMoreElements(itr);
   }

   @Test
   public void testErase() {
      RecordedDatabase d = new RecordedDatabase();

      PredicateKey key = new PredicateKey("a", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key, value1, true);
      IntegerNumber reference2 = d.add(key, value2, true);
      IntegerNumber reference3 = d.add(key, value3, true);

      assertTrue(d.erase(reference2.getLong()));
      Iterator<Record> itr = d.getAll();
      assertNext(itr, key, reference1, value1);
      assertNext(itr, key, reference3, value3);
      assertNoMoreElements(itr);

      assertFalse(d.erase(reference2.getLong()));
      itr = d.getAll();
      assertNext(itr, key, reference1, value1);
      assertNext(itr, key, reference3, value3);
      assertNoMoreElements(itr);

      assertTrue(d.erase(reference1.getLong()));
      itr = d.getAll();
      assertNext(itr, key, reference3, value3);
      assertNoMoreElements(itr);

      assertTrue(d.erase(reference3.getLong()));
      itr = d.getAll();
      assertNoMoreElements(itr);

      assertFalse(d.erase(reference3.getLong()));
   }

   @Test
   public void testGetAll_MultipleKeys() {
      RecordedDatabase d = new RecordedDatabase();
      PredicateKey key1 = new PredicateKey("a", 1);
      PredicateKey key2 = new PredicateKey("b", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key1, value1, true);
      IntegerNumber reference2 = d.add(key2, value2, true);
      IntegerNumber reference3 = d.add(key1, value3, true);

      Iterator<Record> itr = d.getAll();

      assertNext(itr, key1, reference1, value1);
      assertNext(itr, key1, reference3, value3);
      assertNext(itr, key2, reference2, value2);

      assertNoMoreElements(itr);
   }

   @Test
   public void testGetChain_Empty() {
      RecordedDatabase d = new RecordedDatabase();
      PredicateKey key = new PredicateKey("a", 1);

      Iterator<Record> itr = d.getChain(key);

      assertFalse(itr.hasNext());
      assertNoMoreElements(itr);
   }

   @Test
   public void testGetChain_SingleElement() {
      RecordedDatabase d = new RecordedDatabase();
      PredicateKey key = new PredicateKey("a", 1);
      Atom value = new Atom("test");
      IntegerNumber reference = d.add(key, value, true);

      Iterator<Record> itr = d.getChain(key);

      assertNext(itr, key, reference, value);

      assertNoMoreElements(itr);
   }

   @Test
   public void testGetChain_MultipleKeys() {
      RecordedDatabase d = new RecordedDatabase();
      PredicateKey key1 = new PredicateKey("a", 1);
      PredicateKey key2 = new PredicateKey("b", 1);
      Atom value1 = new Atom("test1");
      Atom value2 = new Atom("test2");
      Atom value3 = new Atom("test3");
      IntegerNumber reference1 = d.add(key1, value1, true);
      IntegerNumber reference2 = d.add(key2, value2, true);
      IntegerNumber reference3 = d.add(key1, value3, true);

      Iterator<Record> itr1 = d.getChain(key1);
      Iterator<Record> itr2 = d.getChain(key2);

      assertNext(itr1, key1, reference1, value1);
      assertNext(itr1, key1, reference3, value3);
      assertNoMoreElements(itr1);

      assertNext(itr2, key2, reference2, value2);
      assertNoMoreElements(itr2);
   }

   private void assertNext(Iterator<Record> itr, PredicateKey key, Term reference, Term value) {
      assertTrue(itr.hasNext());
      Record r = itr.next();
      assertKey(key, r.getKey());
      assertEquals(reference, r.getReference());
      assertEquals(value, r.getValue());
   }

   private void assertKey(PredicateKey key, Term term) {
      int numberOfArguments = key.getNumArgs();
      assertEquals(numberOfArguments, term.getNumberOfArguments());
      HashSet<Term> uniqueTerms = new HashSet<>();
      for (int i = 0; i < numberOfArguments; i++) {
         Term a = term.getArgument(i);
         assertTrue(uniqueTerms.add(a));
         assertSame(TermType.NAMED_VARIABLE, a.getType());
         assertEquals("_", a.toString());
      }
   }

   private void assertNoMoreElements(Iterator<Record> itr) {
      assertFalse(itr.hasNext());
      try {
         itr.next();
         fail();
      } catch (NoSuchElementException e) {
         // expected
      }
   }
}
