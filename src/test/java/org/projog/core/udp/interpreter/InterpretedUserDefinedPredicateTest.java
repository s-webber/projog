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
package org.projog.core.udp.interpreter;

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
import static org.projog.TestUtils.array;
import static org.projog.TestUtils.atom;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projog.TestUtils;
import org.projog.core.CutException;
import org.projog.core.Predicate;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.SpyPoints;
import org.projog.core.SpyPoints.SpyPoint;
import org.projog.core.event.ProjogEvent;
import org.projog.core.event.ProjogEventsObservable;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

public class InterpretedUserDefinedPredicateTest {
   private SpyPoints spyPoints;
   private SpyPoint spyPoint;
   private ClauseAction mockAction1;
   private ClauseAction mockAction2;
   private ClauseAction mockAction3;
   private Term[] queryArgs = array(atom("a"), atom("b"), atom("c"));
   private SimpleObserver observer;

   @Before
   public void before() {
      this.mockAction1 = mock(ClauseAction.class);
      this.mockAction2 = mock(ClauseAction.class);
      this.mockAction3 = mock(ClauseAction.class);

      this.observer = new SimpleObserver();
      ProjogEventsObservable observable = new ProjogEventsObservable();
      observable.addObserver(observer);
      this.spyPoints = new SpyPoints(observable, TestUtils.createTermFormatter());
      this.spyPoint = spyPoints.getSpyPoint(new PredicateKey("test", 3));
   }

   @After
   public void after() {
      verifyNoMoreInteractions(mockAction1, mockAction2, mockAction3);
   }

   @Test
   public void testAllSucceedOnce_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertAllSucceedOnce();

      assertEquals("", observer.result());
   }

   @Test
   public void testAllSucceedOnce_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertAllSucceedOnce();

      assertEquals("CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)", observer.result());
      verify(mockAction1).getModel();
      verify(mockAction2).getModel();
      verify(mockAction3).getModel();
   }

   private void assertAllSucceedOnce() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction3.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertFalse(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockAction3).getPredicate(queryArgs);
   }

   @Test
   public void testAllFail_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertAllFail();

      assertEquals("", observer.result());
   }

   @Test
   public void testAllFail_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertAllFail();

      assertEquals("CALLtest(a, b, c)FAILtest(a, b, c)", observer.result());
   }

   private void assertAllFail() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.FAIL);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.FAIL);
      when(mockAction3.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.FAIL);

      assertTrue(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockAction3).getPredicate(queryArgs);
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilFails_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuleRepeatableContinueUntilFails();

      assertEquals("", observer.result());
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilFails_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuleRepeatableContinueUntilFails();

      assertEquals(
                  "CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)",
                  observer.result());
      verify(mockAction1).getModel();
      verify(mockAction2, times(5)).getModel();
      verify(mockAction3).getModel();
   }

   private void assertSecondRuleRepeatableContinueUntilFails() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenReturn(true, true, true, true, true, false);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true, true, true, true, true);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(mockPredicate);
      when(mockAction3.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);

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

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockAction3).getPredicate(queryArgs);
      verify(mockPredicate, times(6)).evaluate();
      verify(mockPredicate, times(5)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilReevaluationCannotSucceed_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed();

      assertEquals("", observer.result());
   }

   @Test
   public void testSecondRuleRepeatableContinueUntilReevaluationCannotSucceed_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed();

      assertEquals(
                  "CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)FAILtest(a, b, c)",
                  observer.result());
      verify(mockAction1).getModel();
      verify(mockAction2, times(5)).getModel();
      verify(mockAction3).getModel();
   }

   private void assertSecondRuleRepeatableContinueUntilReevaluationCannotSucceed() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenReturn(true, true, true, true, true);
      when(mockPredicate.couldReevaluationSucceed()).thenReturn(true, true, true, true, false);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(mockPredicate);
      when(mockAction3.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);

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

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockAction3).getPredicate(queryArgs);
      verify(mockPredicate, times(5)).evaluate();
      verify(mockPredicate, times(5)).couldReevaluationSucceed();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuleCutException() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenThrow(CutException.CUT_EXCEPTION);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(mockPredicate);

      assertTrue(testObject.couldReevaluationSucceed());
      assertTrue(testObject.evaluate());
      assertTrue(testObject.couldReevaluationSucceed());
      assertFalse(testObject.evaluate());

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockPredicate).evaluate();
      verifyNoMoreInteractions(mockPredicate);
   }

   @Test
   public void testSecondRuntimeException_spypoint_disabled() {
      spyPoints.setTraceEnabled(false);

      assertSecondRuntimeException();

      assertEquals("", observer.result());
   }

   @Test
   public void testSecondRuntimeException_spypoint_enabled() {
      spyPoints.setTraceEnabled(true);

      assertSecondRuntimeException();

      assertEquals("CALLtest(a, b, c)EXITtest(a, b, c)REDOtest(a, b, c)", observer.result());
      verify(mockAction1).getModel();
      verify(mockAction2).getModel();
   }

   private void assertSecondRuntimeException() {
      InterpretedUserDefinedPredicate testObject = new InterpretedUserDefinedPredicate(Arrays.asList(mockAction1, mockAction2, mockAction3).iterator(), spyPoint, queryArgs);

      RuntimeException exception = new RuntimeException();
      Predicate mockPredicate = mock(Predicate.class);
      when(mockPredicate.evaluate()).thenThrow(exception);

      when(mockAction1.getPredicate(queryArgs)).thenReturn(AbstractSingletonPredicate.TRUE);
      when(mockAction2.getPredicate(queryArgs)).thenReturn(mockPredicate);

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

      verify(mockAction1).getPredicate(queryArgs);
      verify(mockAction2).getPredicate(queryArgs);
      verify(mockAction2).getModel();
      verify(mockPredicate).evaluate();
      verifyNoMoreInteractions(mockPredicate);
   }

   private static class SimpleObserver implements Observer {
      final StringBuilder result = new StringBuilder();

      @Override
      public void update(Observable o, Object arg) {
         ProjogEvent e = (ProjogEvent) arg;
         result.append(e.getType());
         result.append(e.getDetails());
      }

      String result() {
         return result.toString();
      }
   }
}
