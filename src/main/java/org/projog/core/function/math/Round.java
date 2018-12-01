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
package org.projog.core.function.math;

import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Numeric;

/* TEST
%QUERY X is integer(7.0)
%ANSWER X=7
%QUERY X is integer(7.1)
%ANSWER X=7
%QUERY X is integer(7.2)
%ANSWER X=7
%QUERY X is integer(7.3)
%ANSWER X=7
%QUERY X is integer(7.4)
%ANSWER X=7
%QUERY X is integer(7.49999)
%ANSWER X=7
%QUERY X is integer(7.5)
%ANSWER X=8
%QUERY X is integer(7.50001)
%ANSWER X=8
%QUERY X is integer(7.6)
%ANSWER X=8
%QUERY X is integer(7.7)
%ANSWER X=8
%QUERY X is integer(7.8)
%ANSWER X=8
%QUERY X is integer(7.9)
%ANSWER X=8
%QUERY X is integer(8.0)
%ANSWER X=8

%QUERY X is integer(-7.0)
%ANSWER X=-7
%QUERY X is integer(-7.1)
%ANSWER X=-7
%QUERY X is integer(-7.2)
%ANSWER X=-7
%QUERY X is integer(-7.3)
%ANSWER X=-7
%QUERY X is integer(-7.4)
%ANSWER X=-7
%QUERY X is integer(-7.49999)
%ANSWER X=-7
% Note: in some Prolog implementations the result of "integer(-7.5)" would be -8
%QUERY X is integer(-7.5)
%ANSWER X=-7
%QUERY X is integer(-7.50001)
%ANSWER X=-8
%QUERY X is integer(-7.6)
%ANSWER X=-8
%QUERY X is integer(-7.7)
%ANSWER X=-8
%QUERY X is integer(-7.8)
%ANSWER X=-8
%QUERY X is integer(-7.9)
%ANSWER X=-8
%QUERY X is integer(-8.0)
%ANSWER X=-8

%QUERY X is integer(1.25+6.25)
%ANSWER X=8

%QUERY X is integer(1.25+6.24)
%ANSWER X=7

%QUERY X is integer(0.0)
%ANSWER X=0

%QUERY X is integer(7)
%ANSWER X=7

%QUERY X is integer(-7)
%ANSWER X=-7
 */
/**
 * <code>integer(X)</code> - round X to the nearest integer value.
 */
public final class Round extends AbstractArithmeticOperator {
   @Override
   public Numeric calculate(Numeric n) {
      long rounded = Math.round(n.getDouble());
      return new IntegerNumber(rounded);
   }
}
