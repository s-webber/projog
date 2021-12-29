% test of single argument immutable facts

p(a).
p(b).
p('2').
p(1).
p(0).
p(-1).
p(7.5).
p(7.5).
p(7.5).
p(7.5).
p(7.5).
p(7.0).
p([]).
p(a).
p(a).
p(1).
p(z(a,b)).
p(y(a,b)).
p(x(a,b)).
p(z(a,b)).
p(x(a,c)).
p(x(a,d)).
p(x(a,b,c)).
p(z(a,b)).
p(a).
p([a]).
p([a,b,c]).
p([a,b]).
p([b,a]).
p([a,b,c]).

%FAIL p(c)
%FAIL p(1.0)
%FAIL p(2)
%FAIL p(7)
%FAIL p(7.6)
%FAIL p(z(a,c))
%FAIL p(q(a,b))
%FAIL p([b])
%FAIL p([a,c,b])
%FAIL p([a,b,c,d])

%TRUE p(b)
%TRUE p('2')
%TRUE p(0)
%TRUE p(-1)
%TRUE p(7.0)
%TRUE p([])
%TRUE p(y(a,b))
%TRUE p(x(a,b))
%TRUE p(x(a,c))
%TRUE p(x(a,b,c))
%TRUE p([a])
%TRUE p([a,b])
%TRUE p([b,a])

%?- p(a)
%YES
%YES
%YES
%YES

%?- p(1)
%YES
%YES

%?- p(7.5)
%YES
%YES
%YES
%YES
%YES

%?- p(z(a,b))
%YES
%YES
%YES

%?- p([a,b,c])
%YES
%YES

%?- p(x(a,X))
% X=b
% X=c
% X=d

%?- p(X)
% X=a
% X=b
% X=2
% X=1
% X=0
% X=-1
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.0
% X=[]
% X=a
% X=a
% X=1
% X=z(a, b)
% X=y(a, b)
% X=x(a, b)
% X=z(a, b)
% X=x(a, c)
% X=x(a, d)
% X=x(a, b, c)
% X=z(a, b)
% X=a
% X=[a]
% X=[a,b,c]
% X=[a,b]
% X=[b,a]
% X=[a,b,c]

%?- p(X), p(X)
% X=a
% X=a
% X=a
% X=a
% X=b
% X=2
% X=1
% X=1
% X=0
% X=-1
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.5
% X=7.0
% X=[]
% X=a
% X=a
% X=a
% X=a
% X=a
% X=a
% X=a
% X=a
% X=1
% X=1
% X=z(a, b)
% X=z(a, b)
% X=z(a, b)
% X=y(a, b)
% X=x(a, b)
% X=z(a, b)
% X=z(a, b)
% X=z(a, b)
% X=x(a, c)
% X=x(a, d)
% X=x(a, b, c)
% X=z(a, b)
% X=z(a, b)
% X=z(a, b)
% X=a
% X=a
% X=a
% X=a
% X=[a]
% X=[a,b,c]
% X=[a,b,c]
% X=[a,b]
% X=[b,a]
% X=[a,b,c]
% X=[a,b,c]

%TRUE trace

% test trace when fails
%?- p(z)
%OUTPUT
%[1] CALL p(z)
%[1] FAIL p(z)
%
%OUTPUT
%NO

% test trace when succeeds once
%?- p(b)
%OUTPUT
%[1] CALL p(b)
%[1] EXIT p(b)
%
%OUTPUT
%YES

% test trace when succeeds multiple times
%?- p(z(a,b))
%OUTPUT
%[1] CALL p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%YES
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%YES
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%YES

% test trace when argument contains a variable and so will not use index
%?- p(x(a,X))
%OUTPUT
%[1] CALL p(x(a, X))
%[1] EXIT p(x(a, b))
%
%OUTPUT
% X=b
%OUTPUT
%[1] REDO p(x(a, b))
%[1] EXIT p(x(a, c))
%
%OUTPUT
% X=c
%OUTPUT
%[1] REDO p(x(a, c))
%[1] EXIT p(x(a, d))
%
%OUTPUT
% X=d

% test trace when argument contains a variable and so will not use index and query does not succeed
%?- p(x(q,X))
%OUTPUT
%[1] CALL p(x(q, X))
%[1] FAIL p(x(q, X))
%
%OUTPUT
%NO

% another test of trace when argument contains a variable and so will not use index
%?- p(z(a,X))
%OUTPUT
%[1] CALL p(z(a, X))
%[1] EXIT p(z(a, b))
%
%OUTPUT
% X=b
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
% X=b
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
% X=b
