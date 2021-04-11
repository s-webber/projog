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
public class ReverseTest {
   private static final String REVERSE_PROLOG =
               //
               "reverse_(Xs, Ys) :- reverse_(Xs, [], Ys, Ys)."
               + "reverse_([], Ys, Ys, [])."
               + "reverse_([X|Xs], Rs, Ys, [_|Bound]) :- reverse_(Xs, [X|Rs], Ys, Bound).";

   private static final ListPredicateAssert PREDICATE_ASSERT = new ListPredicateAssert("reverse", 2, REVERSE_PROLOG);

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "[a,b,c] [c,b,a]",
               "[a(X),b(Y),c(Z)] [c(z),b(y),a(x)]",
               "[a(X),b(Y),c(Z)] [c(Z),b(Y),a(X)]",
               "[a,b,c] [z,c,b,a]",
               "[a,b,c] [a,b,c]",
               "[a,b,c] [a,b,c]",
               "[a,b,c] X",
               "[a,b,c] [X|Y]",
               "[a|X] Y",
               "[a,b,c|X] Y",
               "[a,b,c|X] [Y|Z]",
               "[a,b,c|X] [a|Z]",
               "[a,b,c|X] [a,b|Z]",
               "[a,b,c|X] [a,b,c|Z]",
               "[a,b,c|X] [z,x,c,b,a|Z]",
               "[a,b,c|X] [z,x,c,b,a,q|Z]",
               "[a,b,c|X] [z,x,c,q,b,a|Z]",
               "[a,b,c|X] [z,x,c,b|Z]",
               "[a,b,c|X] [z,x,c|Z]",
               "[a,b,c|X] [a|Z]",
               "[a,b,c|X] [b,a|Z]",
               "[a,b,c|X] [c,b,a|Z]",
               "[a,b,c|X] [d,c,b,a]",
               "[a,b,c|X] [e,d,b,a]",
               "[a,b,c|X] [e,d,c,b,a]",
               "[a,a,a|X] [a,a,a|Z]",
               "[a,a,a|X] [b,a,a,a|Z]",
               "[a,a,a|X] [a,a,a,b|Z]",
               "[a,a,a|X] [a|Z]",
               "X Y",
               "X X",})
   public void test(String arg1, String arg2) {
      PREDICATE_ASSERT.assertArgs(arg1, arg2);
      if (!arg1.equals(arg2)) {
         PREDICATE_ASSERT.assertArgs(arg2, arg1);
      }
   }

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "length(X,1000),reverse(X,Y),numbervars(X),reverse(X,Z),reverse(Q,Z).",
               "length(X,1000),length(Y,1000),length(Z,2000),numbervars(X),reverse(X,Y),reverse(Z,X).",
   })
   public void testLongLists(String query) {
      PREDICATE_ASSERT.assertQuery(query);
   }
}
