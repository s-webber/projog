and(A,B) :- A, B.
or(A,B) :- A; B.
true(X) :- X.

%FAIL and(fail, fail)
%FAIL and(true, fail)
%FAIL and(fail, true)
%TRUE and(true, true)

%FAIL or(fail, fail)
%TRUE_NO or(true, fail)
%TRUE or(fail, true)
%?- or(true, true)
%YES
%YES

%?- X=true, X
% X=true

%FAIL X=fail, X

%FAIL true(fail)
%TRUE true(true)
%?- true(repeat(3))
%YES
%YES
%YES
