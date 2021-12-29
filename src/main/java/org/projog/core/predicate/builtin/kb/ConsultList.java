/*
 * Copyright 2018 S. Webber
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
package org.projog.core.predicate.builtin.kb;

import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.parser.ProjogSourceReader;
import org.projog.core.predicate.AbstractSingleResultPredicate;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/* TEST
write_to_file(Filename, Contents) :-
   open(Filename, write, Z),
   set_output(Z),
   writef(Contents),
   close(Z),
   set_output('user_output').

%TRUE write_to_file('consult_list_example_1.tmp', 'test1.')
%TRUE write_to_file('consult_list_example_2.tmp', 'test2.')
%TRUE write_to_file('consult_list_example_3.tmp', 'test3.')
%TRUE write_to_file('consult_list_example_4.tmp', 'test4.')
%TRUE write_to_file('consult_list_example_5.tmp', 'test5.')
%TRUE write_to_file('consult_list_example_6.tmp', 'test6.')

%FAIL test1
%TRUE ['consult_list_example_1.tmp']
%TRUE test1

%FAIL test2
%FAIL test3
%TRUE ['consult_list_example_2.tmp', 'consult_list_example_3.tmp']
%TRUE test2, test3

%FAIL test4
%FAIL test5
%FAIL test6
%TRUE ['consult_list_example_4.tmp', 'consult_list_example_5.tmp', 'consult_list_example_6.tmp']
%TRUE test4, test5, test6
*/
/**
 * Read clauses and goals from a list of files.
 */
public final class ConsultList extends AbstractSingleResultPredicate {
   @Override
   protected boolean evaluate(Term head, Term tail) {
      while (true) {
         consult(head);

         if (tail.getType() == TermType.LIST) {
            head = tail.getArgument(0);
            tail = tail.getArgument(1);
         } else {
            if (tail.getType() != TermType.EMPTY_LIST) {
               consult(tail);
            }
            return true;
         }
      }
   }

   private void consult(Term filename) {
      ProjogSourceReader.parseResource(getKnowledgeBase(), getAtomName(filename));
   }
}
