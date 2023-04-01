testNumericComparison(A,B,C) :- C=a, A>B.
testNumericComparison(A,B,C) :- C=b, A>=B.
testNumericComparison(A,B,C) :- C=c, A<B.
testNumericComparison(A,B,C) :- C=d, A=<B.
testNumericComparison(A,B,C) :- C=e, A=:=B.
testNumericComparison(A,B,C) :- C=f, A=\=B.
testNumericComparison(A,B,C) :- C=g, A-B>B+3.
testNumericComparison(A,B,C) :- C=h, A<7, 5=<B.

%?- testNumericComparison(5,5,X)
% X=b
% X=d
% X=e
% X=h

%?- testNumericComparison(6.5,6.5,X)
% X=b
% X=d
% X=e
% X=h

%?- testNumericComparison(5,5.0,X)
% X=b
% X=d
% X=e
% X=h

%?- testNumericComparison(42,41,X)
% X=a
% X=b
% X=f
%NO

%?- testNumericComparison(41.1,41,X)
% X=a
% X=b
% X=f
%NO

%?- testNumericComparison(7,8,X)
% X=c
% X=d
% X=f
%NO

%?- testNumericComparison(7,7.1,X)
% X=c
% X=d
% X=f
%NO

%?- testNumericComparison(17,7,X)
% X=a
% X=b
% X=f
%NO

%?- testNumericComparison(18,7,X)
% X=a
% X=b
% X=f
% X=g
%NO

% Note: the reason "g" matches is because "B+3" causes an overflow which results in a negative value.
%?- testNumericComparison(9223372036854775806, 9223372036854775807, X)
% X=c
% X=d
% X=f
% X=g
%NO

% Note: the reason "g" matches is because "B+3" causes an overflow which results in a negative value.
%?- testNumericComparison(9223372036854775807, 9223372036854775806, X)
% X=a
% X=b
% X=f
% X=g
%NO

% Note: the reason "g" matches is because "B+3" causes an overflow which results in a negative value.
%?- testNumericComparison(9223372036854775807, 9223372036854775807, X)
% X=b
% X=d
% X=e
% X=g
%NO

% Note: the reason "g" matches is because "B+3" causes an overflow which results in a negative value.
%?- testNumericComparison(9223372036854775806, 9223372036854775806, X)
% X=b
% X=d
% X=e
% X=g
%NO

%?- testNumericComparison(-9223372036854775808, -9223372036854775807, X)
% X=c
% X=d
% X=f
% X=g
%NO

%?- testNumericComparison(-9223372036854775807, -9223372036854775808, X)
% X=a
% X=b
% X=f
% X=g
%NO

%?- testNumericComparison(-9223372036854775808, -9223372036854775808, X)
% X=b
% X=d
% X=e
% X=g
%NO

%?- testNumericComparison(-9223372036854775807, -9223372036854775807, X)
% X=b
% X=d
% X=e
% X=g
%NO

%TRUE_NO testNumericComparison(6,6,d)

%FAIL testNumericComparison(17,7,g)
%TRUE_NO testNumericComparison(18,7,g)

%TRUE testNumericComparison(6,5,h)
%TRUE testNumericComparison(6,6,h)
%FAIL testNumericComparison(7,6,h)
%FAIL testNumericComparison(6,4,h)

testTailRecursiveAppend([], Ys, Ys).
testTailRecursiveAppend([X | Xs], Ys, [X | Zs]) :- testTailRecursiveAppend(Xs, Ys, Zs).

%?- testTailRecursiveAppend(A,B,[a,b,c])
% A=[]
% B=[a,b,c]
% A=[a]
% B=[b,c]
% A=[a,b]
% B=[c]
% A=[a,b,c]
% B=[]
%NO

%?- testTailRecursiveAppend([a,b,c,d,e],[f,g,h],X)
% X=[a,b,c,d,e,f,g,h]

%?- testTailRecursiveAppend([],[a,b,c,d,e,f,g,h],X)
% X=[a,b,c,d,e,f,g,h]

%?- testTailRecursiveAppend([a,b,c,d,e,f,g,h],[],X)
% X=[a,b,c,d,e,f,g,h]

%TRUE testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,h])
%FAIL testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,x])
%FAIL testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,h,i])
%FAIL testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g])

testTailRecursiveRepeat(N). 
testTailRecursiveRepeat(N) :- N > 1, N1 is N-1, testTailRecursiveRepeat(N1).

%?- testTailRecursiveRepeat(3)
%YES
%YES
%YES
%NO

singleFactSingleArgument(42).

singleFactMultipleArguments(2,4,16).

multipleFactsSingleArgument(qwerty).
multipleFactsSingleArgument([a,b,c]).
multipleFactsSingleArgument(fghj).
multipleFactsSingleArgument([a,s,d,f]).
multipleFactsSingleArgument([x,y,z]).

multipleFactsMultipleArguments([q,w,e,r,t,y],[u,i,o,p]).
multipleFactsMultipleArguments([a,s,d,f],[g,h,j,k,l]).
multipleFactsMultipleArguments([z,x,c],[v,b,n,m]).

singleClauseAlwaysTrue(_).

testConjunction(X,Y,Z) :-
 multipleFactsMultipleArguments(X, Y),
 testTailRecursiveAppend(X, Y, A),
 singleFactMultipleArguments(B1, B2, B3),
 testTailRecursiveAppend([B1,B2,B3], A, C),
 multipleFactsSingleArgument(D),
 is_list(D),
 testTailRecursiveAppend(C, D, E),
 length(E,Length),
 singleClauseAlwaysTrue(Z),
 singleFactSingleArgument(F),
 G is (F*2)+Length,
 true,
 testTailRecursiveAppend(E, [G,Length], Z),
 H is F-39,
 testTailRecursiveRepeat(H).

% TODO test with trace enabled

%?- testConjunction(X,Y,Z)
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
% X=[q,w,e,r,t,y]
% Y=[u,i,o,p]
% Z=[2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
% X=[a,s,d,f]
% Y=[g,h,j,k,l]
% Z=[2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
% X=[z,x,c]
% Y=[v,b,n,m]
% Z=[2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
%NO

testOnce(A,B,C,D,E,F,G) :- 
 once(true),
 once(multipleFactsMultipleArguments(A,B)),
 once(singleFactSingleArgument(X)),
 once(repeat(3)),
 once(length(C,D)),
 once(E=X),
 once(testTailRecursiveAppend(A,B,F)),
 once(G).

%?- testOnce(A,B,C,D,E,F,true)
% A=[q,w,e,r,t,y]
% B=[u,i,o,p]
% C=[]
% D=0
% E=42
% F=[q,w,e,r,t,y,u,i,o,p]

%?- testOnce(A,B,C,D,E,F,E<43)
% A=[q,w,e,r,t,y]
% B=[u,i,o,p]
% C=[]
% D=0
% E=42
% F=[q,w,e,r,t,y,u,i,o,p]

%?- testOnce([a,s,A,f],B,[a,b,c],D,42,F,true)
% A=d
% B=[g,h,j,k,l]
% D=3
% F=[a,s,d,f,g,h,j,k,l]

%?- testOnce(A,B,C,3,E,F,true)
% A=[q,w,e,r,t,y]
% B=[u,i,o,p]
% C=[E0,E1,E2]
% E=42
% F=[q,w,e,r,t,y,u,i,o,p]

%FAIL testOnce(A,B,C,D,41,F,true)
%FAIL testOnce(A,B,C,D,E,F,fail)
%FAIL testOnce(A,B,C,D,E,F,E>43)

testWrite(X) :- writeln('hello, "world"!'), write('hello, "'), write(X), write('"!').

%?- testWrite('everyone')
%OUTPUT
%hello, "world"!
%hello, "everyone"!
%OUTPUT
%YES

testNot(X) :- X=1, \+ true.
testNot(X) :- X=2, \+ fail.
testNot(X) :- atom(X), \+ X.
testNot(X) :- X=4, \+ repeat(X).
testNot(X) :- X=5, \+ number(X).
testNot(X) :- X=6, \+ atom(X).
testNot(X) :- X=7, \+ [A,B,C,D,E]=[1,2,3,4,5].
testNot(X) :- C=3, D=5, \+ [A,B,C,9,D,E]=[1,2,3,4,5,6], A=42, B=7, E=51, X is A+B+C+D+E.

%?- testNot(X)
% X=2
% X=6
% X=108

%TRUE_NO testNot(fail)
%FAIL testNot(true)

% TODO expand to b test unify consequent arguments

testAnonymous(p(X,Y),_,Y) :- singleFactSingleArgument(X).

%?- testAnonymous(X,32,12)
% X=p(42, 12)

% TODO review cut examples (including where cut makes multi-clause predicate non-retryable)
testCutSingleRuleMultipleResults :- repeat(5), !, repeat(3).

%?- testCutSingleRuleMultipleResults
%YES
%YES
%YES
%NO

testCutSingleRuleSingleResult :- 1<2, !, 2>1.

%TRUE testCutSingleRuleSingleResult

testCutManyRules(X,Y,RuleNo) :- RuleNo=1, X>5, !, Y<7, repeat(3).
testCutManyRules(X,Y,RuleNo) :- RuleNo=2, X<3, !, Y<7, repeat(7), !, repeat(3), !.
testCutManyRules(X,Y,RuleNo) :- RuleNo=3.
testCutManyRules(X,Y,RuleNo) :- RuleNo=4, X>3, !, Y<7, repeat(3), !.
testCutManyRules(X,Y,RuleNo) :- RuleNo=5, repeat(2).

%?- testCutManyRules(6, 6, RuleNo)
% RuleNo=1
% RuleNo=1
% RuleNo=1
%NO

%FAIL testCutManyRules(6, 7, RuleNo)

%?- testCutManyRules(2, 6, RuleNo)
% RuleNo=2

%FAIL testCutManyRules(2, 7, RuleNo)

%?- testCutManyRules(4, 6, RuleNo)
% RuleNo=3
% RuleNo=4

%?- testCutManyRules(4, 7, RuleNo)
% RuleNo=3
%NO

%?- testCutManyRules(3, 6, RuleNo)
% RuleNo=3
% RuleNo=5
% RuleNo=5

testCalculatables(A,B,C) :- A is 1000, Z is 500*2, A is Z.
testCalculatables(A,B,C) :- Y is 1.5, A is Y.
testCalculatables(A,B,C) :- A is B+C.
testCalculatables(A,B,C) :- A is B-C.
testCalculatables(A,B,C) :- A is B*C.
testCalculatables(A,B,C) :- A is B//C.
testCalculatables(A,B,C) :- A is (B//C)+B*C-2.
testCalculatables(A,B,C) :- A=Z, Z is (B//C)+B*C-(1.25+0.25+1).
testCalculatables(A,B,C) :- Z is B+C, A is Z.

%TRUE notrace
%?- testCalculatables(X, 3, 7)
% X=1000
% X=1.5
% X=10
% X=-4
% X=21
% X=0
% X=19
% X=18.5
% X=10

%TRUE trace
%?- testCalculatables(X, 3, 7)
%OUTPUT
%[1] CALL testCalculatables(X, 3, 7)
%[1] EXIT testCalculatables(1000, 3, 7)
%
%OUTPUT
% X=1000
%OUTPUT
%[1] REDO testCalculatables(1000, 3, 7)
%[1] EXIT testCalculatables(1.5, 3, 7)
%
%OUTPUT
% X=1.5
%OUTPUT
%[1] REDO testCalculatables(1.5, 3, 7)
%[1] EXIT testCalculatables(10, 3, 7)
%
%OUTPUT
% X=10
%OUTPUT
%[1] REDO testCalculatables(10, 3, 7)
%[1] EXIT testCalculatables(-4, 3, 7)
%
%OUTPUT
% X=-4
%OUTPUT
%[1] REDO testCalculatables(-4, 3, 7)
%[1] EXIT testCalculatables(21, 3, 7)
%
%OUTPUT
% X=21
%OUTPUT
%[1] REDO testCalculatables(21, 3, 7)
%[1] EXIT testCalculatables(0, 3, 7)
%
%OUTPUT
% X=0
%OUTPUT
%[1] REDO testCalculatables(0, 3, 7)
%[1] EXIT testCalculatables(19, 3, 7)
%
%OUTPUT
% X=19
%OUTPUT
%[1] REDO testCalculatables(19, 3, 7)
%[1] EXIT testCalculatables(18.5, 3, 7)
%
%OUTPUT
% X=18.5
%OUTPUT
%[1] REDO testCalculatables(18.5, 3, 7)
%[1] EXIT testCalculatables(10, 3, 7)
%
%OUTPUT
% X=10

