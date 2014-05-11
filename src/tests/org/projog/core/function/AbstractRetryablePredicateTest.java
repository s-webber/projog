package org.projog.core.function;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.term.Term;

public class AbstractRetryablePredicateTest {
   @Test
   public void testSimpleImplementation() {
      AbstractRetryablePredicate pf = new AbstractRetryablePredicate() {
         @Override
         public Predicate getPredicate(Term... args) {
            return this;
         }

         @Override
         public boolean evaluate(Term... args) {
            return false;
         }
      };

      assertTrue(pf.isRetryable());
      assertTrue(pf.couldReEvaluationSucceed());
      assertSame(pf, pf.getPredicate());

      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      pf.setKnowledgeBase(kb);
      assertSame(kb, pf.getKnowledgeBase());
   }
}