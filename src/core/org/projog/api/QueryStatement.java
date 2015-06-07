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
package org.projog.api;

import static org.projog.core.KnowledgeBaseUtils.getOperands;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.ProjogException;
import org.projog.core.parser.ParserException;
import org.projog.core.parser.SentenceParser;
import org.projog.core.term.Term;
import org.projog.core.term.Variable;

/**
 * Represents a query.
 */
public final class QueryStatement {
   private final PredicateFactory predicateFactory;
   private final Term parsedInput;
   private final Map<String, Variable> variables;
   private final int numVariables;

   /**
    * Creates a new {@code QueryStatement} representing a query specified by {@code prologQuery}.
    * 
    * @param kb the {@link org.projog.core.KnowledgeBase} to query against
    * @param prologQuery prolog syntax representing a query (do not prefix with a {@code ?-})
    * @throws ProjogException if an error occurs parsing {@code prologQuery}
    * @see Projog#query(String)
    */
   QueryStatement(KnowledgeBase kb, String prologQuery) {
      try {
         SentenceParser sp = SentenceParser.getInstance(prologQuery, getOperands(kb));

         this.parsedInput = sp.parseSentence();
         this.predicateFactory = kb.getPredicateFactory(parsedInput);
         this.variables = sp.getParsedTermVariables();
         this.numVariables = variables.size();

         if (sp.parseSentence() != null) {
            throw new ProjogException("More input found after .");
         }
      } catch (ParserException pe) {
         throw pe;
      } catch (Exception e) {
         throw new ProjogException(e.getClass().getName() + " caught parsing: " + prologQuery, e);
      }
   }

   /**
    * Returns a new {@link QueryResult} for the query represented by this object.
    * <p>
    * Note that the query is not evaluated as part of a call to {@code getResult()}. It is on the first call of
    * {@link QueryResult#next()} that the first attempt to evaluate the query will be made.
    * <p>
    * {@code getResult()} can be called multiple times on the same {@code QueryStatement} instance.
    * 
    * @return a new {@link QueryResult} for the query represented by this object.
    */
   public QueryResult getResult() {
      if (numVariables == 0) {
         return new QueryResult(predicateFactory, parsedInput, variables);
      }

      Map<String, Variable> copyVariables = new HashMap<>(numVariables);
      Map<Variable, Variable> sharedVariables = new HashMap<>(numVariables);
      for (Map.Entry<String, Variable> e : variables.entrySet()) {
         String id = e.getKey();
         Variable v = new Variable(id);
         copyVariables.put(id, v);
         sharedVariables.put(e.getValue(), v);
      }
      Term copyParsedInput = parsedInput.copy(sharedVariables);
      return new QueryResult(predicateFactory, copyParsedInput, copyVariables);
   }
}
