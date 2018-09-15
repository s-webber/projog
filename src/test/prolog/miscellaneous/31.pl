a :- true, !, true, repeat(3).
a :- true.

%QUERY a
%ANSWER/
%ANSWER/
%ANSWER/
%NO

b :- true, repeat(3), !, repeat(3).
b :- true.

%QUERY b
%ANSWER/
%ANSWER/
%ANSWER/
%NO

c1 :- true, true, !.
c1 :- true.

c2 :- true, !, true.
c2 :- true.

c3 :- !, true, true.
c3 :- true.

c4 :- !, true, !, true.
c4 :- true.

c5 :- true, !, true, !.
c5 :- true.

c6 :- !, true, true, !.
c6 :- true.

c7 :- !, true, !, true, !.
c7 :- true.

c8 :- true, !.
c8 :- true.

c9 :- !, true.
c9 :- true.

c10 :- !.
c10 :- true.

%TRUE_NO c1
%TRUE_NO c2
%TRUE_NO c3
%TRUE_NO c4
%TRUE_NO c5
%TRUE_NO c6
%TRUE_NO c7
%TRUE_NO c8
%TRUE_NO c9
%TRUE_NO c10

d :- true, 1>2, !.
d :- true.

%TRUE d

e :- true, true, !, 1>2.
e :- true.

%FALSE e

f :- true, fail, !, 1>2.
f :- true.

%TRUE f