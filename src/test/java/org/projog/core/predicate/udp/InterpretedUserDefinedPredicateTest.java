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
package org.projog.core.predicate.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.projog.TermFactory.atom;
import static org.projog.TestUtils.array;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.ProjogException;
import org.projog.core.event.ProjogListener;
import org.projog.core.event.ProjogListeners;
import org.projog.core.event.SpyPoints;
import org.projog.core.event.SpyPoints.SpyPoint;
import org.projog.core.event.SpyPoints.SpyPointEvent;
import org.projog.core.event.SpyPoints.SpyPointExitEvent;
import org.projog.core.predicate.CutException;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.term.Structure;
import org.projog.core.term.Term;

public class InterpretedUserDefinedPredicateTest {
   private final Term[] queryArgs = array(atom("a"), atom("b"), atom("c"));
   private final Term term = Structure.createStructure("test", queryArgs);
   private SpyPoints spyPoints;
   private SpyPoint spyPoint;
   private ClauseAction mockAction1;
   private ClauseAction mockAction2;
   private ClauseAction mockAction3;
   private SimpleListener listener;

   @Before
   public void before() {
      this.mockAction1 = mock(ClauseAction.class);
      this.mockAction2 = mock(ClauseAction.class);
      this.mockAction3 = mock(ClauseAction.class);

      this.listener = new SimpleListener();
      ProjogListeners observable = new ProjogListeners();
      observable.addListener(listener);
      this.spyPoints = new SpyPoints(observable, TestUtils.createTermFormatter());
      this.spyPoint = spyPoints.getSpyPoint(PredicateKey.createForTerm(term));
   }

   @After
   public void after() {
      verifyNoMoreInteractions(mockAction1, mockAction2, mockAction3);
   }

   @Test
   public void testAllSucceedOnce_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertAllSucceedOnce();

      assertEquals("", listener.result());
   }

   @Test
   public void testAllSucceedOnce_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertAllSucceedOnce();

      assertEquals("CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)", listener.result());
      verify(mockAction1).getModel();
      verify(mockAction2).getModel();
      verify(mockAction3).getModel();
   }

   private void assertAllSucceedOnce() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction2.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction3.getPredicate(term)).thenReturn(PredicateUtils.TRUE);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertFalse(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(term);
      verify(mockAction1).isAlwaysCutOnBacktrack();
      verify(mockAction2).getPredicate(term);
      verify(mockAction2).isAlwaysCutOnBacktrack();
      verify(mockAction3).getPredicate(term);
      verify(mockAction3).isAlwaysCutOnBacktrack();
   }

   @Test
   public void testAllFail_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertAllFail();

      assertEquals("", listener.result());
   }

   @Test
   public void testAllFail_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertAllFail();

      assertEquals("CALLtest(a, b, c)FAILtest(a, b, c)", listener.result());
   }

   private void assertAllFail() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.FALSE);
      when(mockAction2.getPredicate(term)).thenReturn(PredicateUtils.FALSE);
      when(mockAction3.getPredicate(term)).thenReturn(PredicateUtils.FALSE);

      assertTrue(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(term);
      verify(mockAction2).getPredicate(term);
      verify(mockAction3).getPredicate(term);
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilFails_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuleRepeatableContinueUntilFails();

      assertEquals("", listener.result());
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilFails_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuleRepeatableContinueUntilFails();

      assertEquals(
                  "CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)",
                  listener.result());
      verify(mockAction1).getModel();
      verify(mockAction2, times(5)).getModel();
      verify(mockAction3).getModel();
   }

   private void assertSecondRuleRepeatableContinueUntilFails() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenReturn(true, true, true, true, true, false);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true, true, true, true, true);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction2.getPredicate(term)).thenReturn(mockPredicate);
      when(mockAction3.getPredicate(term)).thenReturn(PredicateUtils.TRUE);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertFalse(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(term);
      verify(mockAction1).isAlwaysCutOnBacktrack();
      verify(mockAction2).getPredicate(term);
      verify(mockAction2, times(5)).isAlwaysCutOnBacktrack();
      verify(mockAction3).getPredicate(term);
      verify(mockAction3).isAlwaysCutOnBacktrack();
      verify(mockPredicate, times(6)).evaluate();
      verify(mockPredicate, times(5)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilReevaluationCannotSucceed_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed();

      assertEquals("", listener.result());
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilReevaluationCannotSucceed_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed();

      assertEquals(
                  "CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)",
                  listener.result());
      verify(mockAction1).getModel();
      verify(mockAction2, times(5)).getModel();
      verify(mockAction3).getModel();
   }

   private void assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenReturn(true, true, true, true, true);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true, true, true, true, false);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction2.getPredicate(term)).thenReturn(mockPredicate);
      when(mockAction3.getPredicate(term)).thenReturn(PredicateUtils.TRUE);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertFalse(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(term);
      verify(mockAction1).isAlwaysCutOnBacktrack();
      verify(mockAction2).getPredicate(term);
      verify(mockAction2, times(5)).isAlwaysCutOnBacktrack();
      verify(mockAction3).getPredicate(term);
      verify(mockAction3).isAlwaysCutOnBacktrack();
      verify(mockPredicate, times(5)).evaluate();
      verify(mockPredicate, times(5)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuleCutException() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenThrow(CutException.CUT_EXCEPTION);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction2.getPredicate(term)).thenReturn(mockPredicate);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(term);
      verify(mockAction1).isAlwaysCutOnBacktrack();
      verify(mockAction2).getPredicate(term);
      verify(mockPredicate).evaluate();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuntimeException_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuntimeException();

      assertEquals("", listener.result());
   }

   @Test
   public void testSecondRuntimeException_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuntimeException();

      assertEquals("CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)", listener.result());
      verify(mockAction1).getModel();
      verify(mockAction2).getModel();
   }

   private void assertSecondRuntimeException() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, term);

      RuntimeException exception = new RuntimeException();
      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenThrow(exception);

      when(mockAction1.getPredicate(term)).thenReturn(PredicateUtils.TRUE);
      when(mockAction2.getPredicate(term)).thenReturn(mockPredicate);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      try {
         testObject.evaluate();
         fail();
      } catch (ProjogException e) {
         assertEquals("Exception processing: test/3", e.getMessage());
         assertSame(exception, e.getCause());
      }

      verify(mockAction1).getPredicate(term);
      verify(mockAction1).isAlwaysCutOnBacktrack();
      verify(mockAction2).getPredicate(term);
      verify(mockAction2).getModel();
      verify(mockPredicate).evaluate();
      verifyNoMoreInteractions(mockPredicate);
   }

   private static class SimpleListener implements ProjogListener {
      final StringBuilder result = new StringBuilder();

      @Override
      public void onInfo(String message) {
         throw new UnsupportedOperationException(message);
      }

      @Override
      public void onWarn(String message) {
         throw new UnsupportedOperationException(message);
      }

      @Override
      public void onCall(SpyPointEvent event) {
         update("CALL", event);
      }

      @Override
      public void onRedo(SpyPointEvent event) {
         update("REDO", event);
      }

      @Override
      public void onExit(SpyPointExitEvent event) {
         update("EXIT", event);
      }

      @Override
      public void onFail(SpyPointEvent event) {
         update("FAIL", event);
      }

      private void update(String level, SpyPointEvent event) {
         result.append(level);
         result.append(event);
      }

      String result() {
         return result.toString();
      }
   }
}
