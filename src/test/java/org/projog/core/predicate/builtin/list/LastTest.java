/*
 * Copyright 2021 S. Webber
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
package org.projog.core.predicate.builtin.list;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class LastTest {
   private static final String LAST_PROLOG =
               //
               "last_([X|Xs], Last) :- last_(Xs, X, Last)." +
               "last_([], Last, Last)." +
               "last_([X|Xs], _, Last) :- last_(Xs, X, Last).";

   private static final ListPredicateAssert PREDICATE_ASSERT = new ListPredicateAssert("last", 2, LAST_PROLOG);

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "[a] X",
               "[a] a",
               "[a] b",
               "[a,b,c] X",
               "[a,b,c] a",
               "[a,b,c] b",
               "[a,b,c] c",
               "[a,b,c] z",
               "[a,b,c|X] Y",
               "[a,b,c|X] a",
               "[a,b,c|X] b",
               "[a,b,c|X] c",
               "[a,b,c|X] z",
               "[Y,b,c|X] Y",
               "[a,Y,c|X] Y",
               "[a,b,Y|X] Y",
               "[Y,p(a,b,Y,d),Y|X] Y",
               "[] X",
               "[] []",
               "a X",
               "1 X",
               "X Y",
               "X X",
               "p(a) X",
               "p(a,b) X",
               "p(a,b,c) X",
               ".(a,b,c) a",
               ".(a) a",})
   public void test(String arg1, String arg2) {
      PREDICATE_ASSERT.assertArgs(arg1, arg2);
   }

   @Test(timeout = 5000)
   public void testLongList() {
      PREDICATE_ASSERT.assertQuery("length(X,1000),numbervars(X),last(X,Y).");
   }
}
