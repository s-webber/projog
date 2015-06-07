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

/**
 * A property of {@link ProjogEvent} used to categorise events.
 */
public enum ProjogEventType {
   /** The event type generated when an attempt is first made to evaluate a goal. */
   CALL,
   /** The event type generated when an attempt is made to re-evaluate a goal. */
   REDO,
   /** The event type generated when an attempt to evaluate a goal succeeds. */
   EXIT,
   /** The event type generated when all attempts to evaluate a goal have failed. */
   FAIL,
   /** The event type generated to warn clients of an event. */
   WARN,
   /** The event type generated to inform clients of an event. */
   INFO
}
