package org.projog.core.function.db;

import static org.projog.core.term.AnonymousVariable.ANONYMOUS_VARIABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.projog.core.PredicateKey;
import org.projog.core.term.Atom;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

/**
 * Provides a mechanism to associate a term with a key.
 * <p>
 * Multiple terms can be associated with the same key.
 */
public class RecordedDatabase {
   private final AtomicLong referenceCtr = new AtomicLong();
   private final SortedMap<Long, Link> references = new TreeMap<>();
   private final List<PredicateKey> keys = new ArrayList<>();
   private final SortedMap<PredicateKey, Chain> chains = new TreeMap<>();

   /**
    * Associates a value with a key.
    * 
    * @param key the key to associate the value with
    * @param value the value to store
    * @return reference for the newly added value
    */
   IntegerNumber add(PredicateKey key, Term value) {
      Chain chain = getOrCreateChain(key);
      Link link = createLink(chain, value);
      return link.reference;
   }

   Iterator<Record> getAll() {
      return new DatabaseIterator();
   }

   Iterator<Record> getChain(PredicateKey key) {
      Chain chain = chains.get(key);
      if (chain == null) {
         return Collections.emptyIterator();
      } else {
         return new ChainIterator(chain);
      }
   }

   private Chain getOrCreateChain(PredicateKey key) {
      Chain chain = chains.get(key);
      if (chain == null) {
         chain = createChain(key);
      }
      return chain;
   }

   private Chain createChain(PredicateKey key) {
      Chain chain;
      synchronized (chains) {
         chain = chains.get(key);
         if (chain == null) {
            chain = new Chain(createTermFromKey(key));
            chains.put(key, chain);
            keys.add(key);
         }
      }
      return chain;
   }

   private Link createLink(Chain chain, Term value) {
      IntegerNumber reference = createReference();
      Link link = new Link(chain, reference, value);
      addLinkToChain(link);
      references.put(reference.getLong(), link);
      return link;
   }

   private Term createTermFromKey(PredicateKey key) {
      String name = key.getName();
      int numArgs = key.getNumArgs();
      if (numArgs == 0) {
         return new Atom(name);
      } else {
         Term[] args = new Term[numArgs];
         Arrays.fill(args, ANONYMOUS_VARIABLE);
         return Structure.createStructure(name, args);
      }
   }

   private IntegerNumber createReference() {
      return new IntegerNumber(referenceCtr.getAndIncrement());
   }

   private void addLinkToChain(Link link) {
      Chain c = link.chain;
      synchronized (c) {
         if (c.last == null) {
            c.first = c.last = link;
         } else {
            c.last.next = link;
            c.last = link;
         }
      }
   }

   private class DatabaseIterator implements Iterator<Record> {
      private int keyIdx;
      private ChainIterator chainIterator;

      @Override
      public boolean hasNext() {
         while (chainIterator == null || !chainIterator.hasNext()) {
            if (hasIteratedOverAllChains()) {
               return false;
            } else {
               updateChainIterator();
            }
         }
         return true;
      }

      private boolean hasIteratedOverAllChains() {
         return keyIdx >= keys.size();
      }

      private void updateChainIterator() {
         PredicateKey key = keys.get(keyIdx++);
         Chain chain = chains.get(key);
         chainIterator = new ChainIterator(chain);
      }

      @Override
      public Record next() {
         if (hasNext()) {
            return chainIterator.next();
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class ChainIterator implements Iterator<Record> {
      private Link current;

      ChainIterator(Chain chain) {
         this.current = chain.first;
      }

      @Override
      public boolean hasNext() {
         skipDeleted();
         return current != null;
      }

      @Override
      public Record next() {
         if (hasNext()) {
            Record next = createRecord(current);
            current = current.next;
            return next;
         } else {
            throw new NoSuchElementException();
         }
      }

      private Record createRecord(Link link) {
         return new Record(link.chain.key, link.reference, link.value);
      }

      private void skipDeleted() {
         while (current != null && current.deleted) {
            current = current.next;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class Chain {
      final Term key;
      Link first;
      Link last;

      Chain(Term key) {
         this.key = key;
      }
   }

   private static class Link {
      final Chain chain;
      final IntegerNumber reference;
      final Term value;
      Link next;
      boolean deleted;

      Link(Chain chain, IntegerNumber reference, Term value) {
         this.chain = chain;
         this.reference = reference;
         this.value = value;
      }
   }
}