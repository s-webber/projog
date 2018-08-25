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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.Test;

public class ProjogEventsObservableTest {
   @Test
   public void testProjogEventsObservable() {
      ProjogEventsObservable testObject = new ProjogEventsObservable();

      DummyObserver o1 = new DummyObserver();
      DummyObserver o2 = new DummyObserver();
      DummyObserver o3 = new DummyObserver();

      ProjogEvent e1 = createEvent();
      ProjogEvent e2 = createEvent();
      ProjogEvent e3 = createEvent();

      testObject.notifyObservers(e1);

      testObject.addObserver(o1);
      testObject.addObserver(o1);
      testObject.addObserver(o2);
      testObject.addObserver(o3);

      testObject.notifyObservers(e2);

      testObject.deleteObserver(o2);

      testObject.notifyObservers(e3);

      assertEventsUpdated(o1, e2, e3);
      assertEventsUpdated(o2, e2);
      assertEventsUpdated(o3, e2, e3);
   }

   private void assertEventsUpdated(DummyObserver o, ProjogEvent... expectedEvents) {
      final List<Object> actualEvents = o.l;
      assertEquals(expectedEvents.length, actualEvents.size());
      for (int i = 0; i < expectedEvents.length; i++) {
         assertSame(expectedEvents[i], actualEvents.get(i));
      }
   }

   private ProjogEvent createEvent() {
      return new ProjogEvent(null, null, null);
   }

   private static class DummyObserver implements Observer {
      final List<Object> l = new ArrayList<>();

      @Override
      public void update(Observable o, Object arg) {
         l.add(arg);
      }
   }
}
