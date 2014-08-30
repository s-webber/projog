and(A,B) :- A, B.
or(A,B) :- A; B.
true(X) :- X.

%FALSE and(fail, fail)
%FALSE and(true, fail)
%FALSE and(fail, true)
%TRUE and(true, true)

%FALSE or(fail, fail)
%TRUE_NO or(true, fail)
%TRUE or(fail, true)
%QUERY or(true, true)
%ANSWER/
%ANSWER/

%QUERY X=true, X
%ANSWER X=true

%FALSE X=fail, X

%FALSE true(fail)
%TRUE true(true)
%QUERY true(repeat(3))
%ANSWER/
%ANSWER/
%ANSWER/