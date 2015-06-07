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
package org.projog.example;

import java.io.File;

import org.projog.api.Projog;
import org.projog.api.QueryResult;
import org.projog.api.QueryStatement;
import org.projog.core.term.Atom;

public class ProjogExample {
   public static void main(String[] args) {
      Projog p = new Projog();
      p.consultFile(new File("test.pl"));
      QueryStatement s1 = p.query("test(X,Y).");
      QueryResult r1 = s1.getResult();
      while (r1.next()) {
         System.out.println("X = " + r1.getTerm("X") + " Y = " + r1.getTerm("Y"));
      }
      QueryResult r2 = s1.getResult();
      r2.setTerm("X", new Atom("d"));
      while (r2.next()) {
         System.out.println("Y = " + r2.getTerm("Y"));
      }

      QueryStatement s2 = p.query("testRule(X).");
      QueryResult r3 = s2.getResult();
      while (r3.next()) {
         System.out.println("X = " + r3.getTerm("X"));
      }

      QueryStatement s3 = p.query("test(X, Y), Y<3.");
      QueryResult r4 = s3.getResult();
      while (r4.next()) {
         System.out.println("X = " + r4.getTerm("X") + " Y = " + r4.getTerm("Y"));
      }
   }
}
