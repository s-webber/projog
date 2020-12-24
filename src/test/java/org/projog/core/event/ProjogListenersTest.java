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
package org.projog.core.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.projog.core.SpyPoints.SpyPointEvent;
import org.projog.core.SpyPoints.SpyPointExitEvent;

public class ProjogListenersTest {
   @Test
   public void testProjogEventsObservable() {
      ProjogListeners testObject = new ProjogListeners();

      DummyListener o1 = new DummyListener();
      DummyListener o2 = new DummyListener();
      DummyListener o3 = new DummyListener();

      testObject.notifyInfo("info1");

      testObject.addListener(o1);
      testObject.addListener(o1);
      testObject.addListener(o2);
      testObject.addListener(o3);

      testObject.notifyWarn("warn");

      testObject.deleteListener(o2);

      testObject.notifyInfo("info2");

      assertEquals(2, o1.size());
      assertEquals(1, o2.size());
      assertEquals(2, o3.size());
      assertEquals("warninfo2", o1.result());
      assertEquals("warn", o2.result());
      assertEquals("warninfo2", o3.result());
   }

   private static class DummyListener implements ProjogListener {
      private final List<String> events = new ArrayList<>();

      @Override
      public void onInfo(String message) {
         add(message);
      }

      @Override
      public void onWarn(String message) {
         add(message);
      }

      @Override
      public void onRedo(SpyPointEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void onFail(SpyPointEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void onExit(SpyPointExitEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void onCall(SpyPointEvent event) {
         throw new UnsupportedOperationException();
      }

      private void add(String message) {
         events.add(message);
      }

      public int size() {
         return events.size();
      }

      public String result() {
         StringBuilder result = new StringBuilder();
         for (String event : events) {
            result.append(event);
         }
         return result.toString();
      }
   }
}
