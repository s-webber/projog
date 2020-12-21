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

%FALSE p(c)
%FALSE p(1.0)
%FALSE p(2)
%FALSE p(7)
%FALSE p(7.6)
%FALSE p(z(a,c))
%FALSE p(q(a,b))
%FALSE p([b])
%FALSE p([a,c,b])
%FALSE p([a,b,c,d])

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

%QUERY p(a)
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY p(1)
%ANSWER/
%ANSWER/

%QUERY p(7.5)
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY p(z(a,b))
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY p([a,b,c])
%ANSWER/
%ANSWER/

%QUERY p(x(a,X))
%ANSWER X=b
%ANSWER X=c
%ANSWER X=d

%QUERY p(X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=2
%ANSWER X=1
%ANSWER X=0
%ANSWER X=-1
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.0
%ANSWER X=[]
%ANSWER X=a
%ANSWER X=a
%ANSWER X=1
%ANSWER X=z(a, b)
%ANSWER X=y(a, b)
%ANSWER X=x(a, b)
%ANSWER X=z(a, b)
%ANSWER X=x(a, c)
%ANSWER X=x(a, d)
%ANSWER X=x(a, b, c)
%ANSWER X=z(a, b)
%ANSWER X=a
%ANSWER X=[a]
%ANSWER X=[a,b,c]
%ANSWER X=[a,b]
%ANSWER X=[b,a]
%ANSWER X=[a,b,c]

%QUERY p(X), p(X)
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=b
%ANSWER X=2
%ANSWER X=1
%ANSWER X=1
%ANSWER X=0
%ANSWER X=-1
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.5
%ANSWER X=7.0
%ANSWER X=[]
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=1
%ANSWER X=1
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=y(a, b)
%ANSWER X=x(a, b)
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=x(a, c)
%ANSWER X=x(a, d)
%ANSWER X=x(a, b, c)
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=z(a, b)
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=a
%ANSWER X=[a]
%ANSWER X=[a,b,c]
%ANSWER X=[a,b,c]
%ANSWER X=[a,b]
%ANSWER X=[b,a]
%ANSWER X=[a,b,c]
%ANSWER X=[a,b,c]

%TRUE trace

% test trace when fails
%QUERY p(z)
%OUTPUT
%[1] CALL p(z)
%[1] FAIL p(z)
%
%OUTPUT
%NO

% test trace when succeeds once
%QUERY p(b)
%OUTPUT
%[1] CALL p(b)
%[1] EXIT p(b)
%
%OUTPUT
%ANSWER/

% test trace when succeeds multiple times
%QUERY p(z(a,b))
%OUTPUT
%[1] CALL p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER/
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER/
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER/

% test trace when argument contains a variable and so will not use index
%QUERY p(x(a,X))
%OUTPUT
%[1] CALL p(x(a, X))
%[1] EXIT p(x(a, b))
%
%OUTPUT
%ANSWER X=b
%OUTPUT
%[1] REDO p(x(a, b))
%[1] EXIT p(x(a, c))
%
%OUTPUT
%ANSWER X=c
%OUTPUT
%[1] REDO p(x(a, c))
%[1] EXIT p(x(a, d))
%
%OUTPUT
%ANSWER X=d

% test trace when argument contains a variable and so will not use index and query does not succeed
%QUERY p(x(q,X))
%OUTPUT
%[1] CALL p(x(q, X))
%[1] FAIL p(x(q, X))
%
%OUTPUT
%NO

% another test of trace when argument contains a variable and so will not use index
%QUERY p(z(a,X))
%OUTPUT
%[1] CALL p(z(a, X))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER X=b
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER X=b
%OUTPUT
%[1] REDO p(z(a, b))
%[1] EXIT p(z(a, b))
%
%OUTPUT
%ANSWER X=b
