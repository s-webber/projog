package org.projog.core.function.io;

import static org.projog.core.term.TermUtils.castToNumeric;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.term.Term;

/* SYSTEM TEST
 '~'(X,Y) :- X>Y-4, X<Y+4.

 % %TRUE% op(1000,xfx,'~')

 % %TRUE% 4 ~ 7
 % %TRUE% 7 ~ 7
 % %TRUE% 10 ~ 7
 % %FALSE% 11 ~ 7
 % %FALSE% 3 ~ 7

 % Example of invalid arguments
 % %QUERY% op(X,xfx,'><')
 % %EXCEPTION% Expected Numeric but got: NAMED_VARIABLE with value: X

 % %QUERY% op(1000,Y,'><')
 % %EXCEPTION% Expected an atom but got: NAMED_VARIABLE with value: Y

 % %QUERY% op(1000,xfx,Z)
 % %EXCEPTION% Expected an atom but got: NAMED_VARIABLE with value: Z

 % %QUERY% op(1000,zfz,'><')
 % %EXCEPTION% Cannot add operand with associativity of: zfz as the only values allowed are: [xfx, xfy, yfx, fx, fy, xf, yf]
 
 % Create some prefix and postfix operators for the later examples below.

 % %TRUE% op(550, fy, 'fyExample')
 % %TRUE% op(650, fx, 'fxExample')
 % %TRUE% op(600, yf, 'yfExample')
 % %TRUE% op(500, xf, 'xfExample')

 % Example of nested prefix operators.

 % %QUERY% X = fxExample fyExample fyExample a, write_canonical(X), nl
 % %OUTPUT%
 % fxExample(fyExample(fyExample(a)))
 % 
 % %OUTPUT%
 % %ANSWER% X=fxExample fyExample fyExample a

 % Example of a postfix operator.

 % %QUERY% X = 123 yfExample, write_canonical(X), nl
 % %OUTPUT%
 % yfExample(123)
 % 
 % %OUTPUT%
 % %ANSWER% X=123 yfExample

 % Example of nested postfix operators.

 % %QUERY% X = a xfExample yfExample yfExample, write_canonical(X), nl
 % %OUTPUT%
 % yfExample(yfExample(xfExample(a)))
 % 
 % %OUTPUT%
 % %ANSWER% X=a xfExample yfExample yfExample

 % Example of combining post and prefix operators where the postfix operator has the higher precedence.

 % %QUERY% X = fyExample a yfExample, write_canonical(X), nl
 % %OUTPUT%
 % yfExample(fyExample(a))
 % 
 % %OUTPUT%
 % %ANSWER% X=fyExample a yfExample

 % Example of combining post and prefix operators where the prefix operator has the higher precedence.

 % %TRUE% op(700, fy, 'fyExampleB')

 % %QUERY% X = fyExampleB a yfExample, write_canonical(X), nl
 % %OUTPUT%
 % fyExampleB(yfExample(a))
 % 
 % %OUTPUT%
 % %ANSWER% X=fyExampleB a yfExample

 % Examples of how an "x" in an associativity (i.e. "fx" or "xf") means that the argument can contain operators of only a lower level of priority than the operator represented by "f".
 
 % %QUERY% X = a xfExample xfExample
 % %EXCEPTION% Invalid postfix: xfExample 500 and term: xfExample(a) 500 Line: ?- X = a xfExample xfExample.

 % %QUERY% X = fxExample fxExample a
 % %EXCEPTION% Invalid prefix: fxExample level: 650 greater than current level: 649 Line: ?- X = fxExample fxExample a.
 */
/**
 * <code>op(X,Y,Z)</code>
 * <p>
 * Allows functors (names of predicates) to be defined as "operators". The use of operators allows syntax to be easier
 * to write and read. <code>Z</code> is the atom that we want to be an operator, <code>X</code> is the precedence class
 * (an integer), and <code>Y</code> the associativity specifier. e.g. <code>op(1200,xfx,':-')</code>
 * </p>
 */
public final class Op extends org.projog.core.function.AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0], args[1], args[2]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg1, Term arg2, Term arg3) {
      int precedence = castToNumeric(arg1).getInt();
      String associativity = getAtomName(arg2);
      String name = getAtomName(arg3);
      getKnowledgeBase().getOperands().addOperand(name, associativity, precedence);
      return true;
   }
}