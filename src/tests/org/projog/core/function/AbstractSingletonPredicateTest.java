package org.projog.core.function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;

public class AbstractSingletonPredicateTest {
   @Test
   public void testSimpleImplementation() {
      PredicateFactory pf = new AbstractSingletonPredicate() {
      };

      Predicate p = pf.getPredicate((Term[]) null);
      assertFalse(p.isRetryable());
      assertFalse(p.couldReEvaluationSucceed());
      assertSame(pf, p);

      KnowledgeBase kb = TestUtils.createKnowledgeBase();
      pf.setKnowledgeBase(kb);
      assertSame(kb, ((AbstractSingletonPredicate) pf).getKnowledgeBase());
   }
}