package org.projog.core.function.list;

/* TEST
 %TRUE nth1(1, [a,b,c], a)
 %TRUE nth1(2, [a,b,c], b)
 %TRUE nth1(3, [a,b,c], c)
 
 %FALSE nth1(0, [a,b,c], a)
 %FALSE nth1(2, [a,b,c], a)
 %FALSE nth1(4, [a,b,c], a)
 
 %QUERY nth1(1, [a,b,c], X)
 %ANSWER X=a
 %QUERY nth1(2, [a,b,c], X)
 %ANSWER X=b
 %QUERY nth1(3, [a,b,c], X)
 %ANSWER X=c
 
 %FALSE nth1(-1, [a,b,c], X)
 %FALSE nth1(0, [a,b,c], X)
 %FALSE nth1(4, [a,b,c], X)

 %QUERY nth1(X, [h,e,l,l,o], e)
 %ANSWER X=2
 %QUERY nth1(X, [h,e,l,l,o], l)
 %ANSWER X=3
 %ANSWER X=4
 %FALSE nth1(X, [h,e,l,l,o], z)

 %QUERY nth1(X, [h,e,l,l,o], Y)
 %ANSWER 
 % X=1
 % Y=h
 %ANSWER
 %ANSWER 
 % X=2
 % Y=e
 %ANSWER
 %ANSWER 
 % X=3
 % Y=l
 %ANSWER
 %ANSWER 
 % X=4
 % Y=l
 %ANSWER
 %ANSWER 
 % X=5
 % Y=o
 %ANSWER
 
 % Note: "nth" is a synonym for "nth1".
 %TRUE nth(2, [a,b,c], b)
 */
/**
 * <code>nth1(X,Y,Z)</code> - examines an element of a list.
 * <p>
 * Indexing starts at 1.
 * </p>
 */
public final class Nth1 extends AbstractNth {
   public Nth1() {
      super(1);
   }
}
