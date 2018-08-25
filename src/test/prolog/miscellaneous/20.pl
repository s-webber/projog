test1 :- true, !.
%TRUE test1

test2 :- false, !.
%FALSE test2

test3 :- true, true, true.
%TRUE test3

test4(_) :- true.
test4(_) :- true, !.
test4(_) :- true.
%QUERY test4(a)
%ANSWER/
%ANSWER/
%NO