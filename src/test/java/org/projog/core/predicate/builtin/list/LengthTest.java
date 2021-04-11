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
public class LengthTest {
   private static final String LENGTH_PROLOG =
               //
               "length_(L, N) :- length_(L, 0, N)." +
               "length_(L,_,_) :- nonvar(L), \\+ L==[], \\+ L=[_|_], _ is _+1." +
               "length_(_,_,N) :- nonvar(N), \\+ number(N), _ is N+1." +
               "length_([],N,N)." +
               "length_([_|L],N0,N) :- (var(N);number(N)), N1 is N0+1,length_(L, N1, N).";

   private static final ListPredicateAssert PREDICATE_ASSERT = new ListPredicateAssert("length", 2, LENGTH_PROLOG);

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "[] X",
               "[] -1",
               "[] 0",
               "[] 1",
               "[a] X",
               "[a] -1",
               "[a] 0",
               "[a] 1",
               "[a] 2",
               "[a] 3",
               "[a] 4",
               "[a,b] X",
               "[a,b] -1",
               "[a,b] 0",
               "[a,b] 1",
               "[a,b] 2",
               "[a,b] 3",
               "[a,b] 4",
               "[a,b,c] X",
               "[X,b,c] X",
               "[a,X,c] X",
               "[a,b,X] X",
               "[X,X,X] X",
               "[a,b,c] -1",
               "[a,b,c] 0",
               "[a,b,c] 1",
               "[a,b,c] 2",
               "[a,b,c] 3",
               "[a,b,c] 4",
               "[a|X] Y",
               "[Y|X] Y",
               "[a,b|X] Y",
               "[a,b,c|X] Y",
               "[Y,b,c|X] Y",
               "[a,Y,c|X] Y",
               "[a,b,Y|X] Y",
               "[Y,Y,Y|X] Y",
               "[X] Y",
               "[X] X",
               "[X|Y] X",
               "[X|X] X",
               "[a|X] X",
               "[a,b,c|X] X",
               "a X",
               "X a",
               "X Y",
               "X X",})
   public void test(String arg1, String arg2) {
      PREDICATE_ASSERT.assertArgs(arg1, arg2);
   }
}
