package org.projog.core.function.list;

/* TEST
 %TRUE nth0(0, [a,b,c], a)
 %TRUE nth0(1, [a,b,c], b)
 %TRUE nth0(2, [a,b,c], c)

 %FALSE nth0(-1, [a,b,c], a)
 %FALSE nth0(1, [a,b,c], a)
 %FALSE nth0(5, [a,b,c], a)

 %QUERY nth0(0, [a,b,c], X)
 %ANSWER X=a
 %QUERY nth0(1, [a,b,c], X)
 %ANSWER X=b
 %QUERY nth0(2, [a,b,c], X)
 %ANSWER X=c

 %FALSE nth0(-1, [a,b,c], X)
 %FALSE nth0(3, [a,b,c], X)

 %QUERY nth0(X, [h,e,l,l,o], e)
 %ANSWER X=1
 %NO
 %QUERY nth0(X, [h,e,l,l,o], l)
 %ANSWER X=2
 %ANSWER X=3
 %NO
 %FALSE nth0(X, [h,e,l,l,o], z)

 %QUERY nth0(X, [h,e,l,l,o], Y)
 %ANSWER 
 % X=0
 % Y=h
 %ANSWER
 %ANSWER 
 % X=1
 % Y=e
 %ANSWER
 %ANSWER 
 % X=2
 % Y=l
 %ANSWER
 %ANSWER 
 % X=3
 % Y=l
 %ANSWER
 %ANSWER 
 % X=4
 % Y=o
 %ANSWER
 */
/**
 * <code>nth0(X,Y,Z)</code> - examines an element of a list.
 * <p>
 * Indexing starts at 0.
 * </p>
 */
public final class Nth0 extends AbstractNth {
   public Nth0() {
      super(0);
   }
}
