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

c :- true, true, !.
c :- true.

%TRUE_NO c

d :- true, 1>2, !.
d :- true.

%TRUE d

e :- true, true, !, 1>2.
e :- true.

%FALSE e

f :- true, fail, !, 1>2.
f :- true.

%TRUE f