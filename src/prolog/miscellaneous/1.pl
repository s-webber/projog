% simple recursion example
%TRUE_NO recursion( a(a(a(b))) )
%TRUE_NO recursion( a(b) )
%TRUE_NO recursion(b)
%FALSE recursion(a)
recursion(b).
recursion(a(X)) :- recursion(X).