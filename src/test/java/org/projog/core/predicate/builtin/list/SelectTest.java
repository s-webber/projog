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
public class SelectTest {
   private static final String SELECT_PROLOG =
               //
               "select_(X, [Head|Tail], Rest) :- select3_(Tail, Head, X, Rest)." +
               "select3_(Tail, Head, Head, Tail)." +
               "select3_([Head2|Tail], Head, X, [Head|Rest]) :- select3_(Tail, Head2, X, Rest).";

   private static final ListPredicateAssert PREDICATE_ASSERT = new ListPredicateAssert("select", 3, SELECT_PROLOG);

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "a [a,b,c] X",
               "b [a,b,c] X",
               "c [a,b,c] X",
               "z [a,b,c] X",
               "a [a,b,a,c,a] X",
               "c [a,b,c,c,c] X",
               "Y [q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m] X",
               "Y [_,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m] X",
               "Y [q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,_] X",
               "Y [q,_,e,_,t,_,_,_,o,p,a,s,d,f,g,_,j,_,l,z,_,c,_,b,n,_] X",
               "c [a,b,c,c] [a,b,c]",
               "z [a,b,c] [a,b,c]",
               "Q [a,b,c,d,e] [a,X,Y,Z]",
               "Q [a,b,c,d,e] [b,X,Y,Z]",
               "Q [a,b,c,d,e] [X,b,Y,Z]",
               "Q [a,b,c,d,e] [X,b,Y,d]",
               "Q [a,b,c,d,e] [W,X,Y,Z]",
               "Q [a,c,b,c,c,d,e] [T,W,X,X,Y,Z]",
               "a [x|X] [x,y,z]",
               "X Y Z",})
   // TODO "X X X"
   // TODO more partial list examples
   public void test(String arg1, String arg2, String arg3) {
      PREDICATE_ASSERT.assertArgs(arg1, arg2, arg3);
   }

   @Test(timeout = 5000)
   @DataProvider(splitBy = " ", value = {
               "length(X,10000),numbervars(X),last(X,Last),select(Last,X,Result).",
               "length(X,10000),numbervars(X),last(X,Last),select(Last,X,Result).",})
   public void testLongLists(String query) {
      PREDICATE_ASSERT.assertQuery(query);
   }
}
