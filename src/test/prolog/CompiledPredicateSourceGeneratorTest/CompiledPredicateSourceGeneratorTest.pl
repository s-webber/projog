testNumericComparison(A,B,C) :- C=a, A>B.
testNumericComparison(A,B,C) :- C=b, A>=B.
testNumericComparison(A,B,C) :- C=c, A<B.
testNumericComparison(A,B,C) :- C=d, A=<B.
testNumericComparison(A,B,C) :- C=e, A=:=B.
testNumericComparison(A,B,C) :- C=f, A=\=B.
testNumericComparison(A,B,C) :- C=g, A-B>B+3.
testNumericComparison(A,B,C) :- C=h, A<7, 5=<B.

%QUERY testNumericComparison(5,5,X)
%ANSWER X=b
%ANSWER X=d
%ANSWER X=e
%ANSWER X=h

%QUERY testNumericComparison(6.5,6.5,X)
%ANSWER X=b
%ANSWER X=d
%ANSWER X=e
%ANSWER X=h

%QUERY testNumericComparison(5,5.0,X)
%ANSWER X=b
%ANSWER X=d
%ANSWER X=e
%ANSWER X=h

%QUERY testNumericComparison(42,41,X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=f
%NO

%QUERY testNumericComparison(41.1,41,X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=f
%NO

%QUERY testNumericComparison(7,8,X)
%ANSWER X=c
%ANSWER X=d
%ANSWER X=f
%NO

%QUERY testNumericComparison(7,7.1,X)
%ANSWER X=c
%ANSWER X=d
%ANSWER X=f
%NO

%QUERY testNumericComparison(17,7,X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=f
%NO

%QUERY testNumericComparison(18,7,X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=f
%ANSWER X=g
%NO

%TRUE_NO testNumericComparison(6,6,d)

%FALSE testNumericComparison(17,7,g)
%TRUE_NO testNumericComparison(18,7,g)

%TRUE testNumericComparison(6,5,h)
%TRUE testNumericComparison(6,6,h)
%FALSE testNumericComparison(7,6,h)
%FALSE testNumericComparison(6,4,h)

testTailRecursiveAppend([], Ys, Ys).
testTailRecursiveAppend([X | Xs], Ys, [X | Zs]) :- testTailRecursiveAppend(Xs, Ys, Zs).

%QUERY testTailRecursiveAppend(A,B,[a,b,c])
%ANSWER
% A=[]
% B=[a,b,c]
%ANSWER
%ANSWER
% A=[a]
% B=[b,c]
%ANSWER
%ANSWER
% A=[a,b]
% B=[c]
%ANSWER
%ANSWER
% A=[a,b,c]
% B=[]
%ANSWER
%NO

%QUERY testTailRecursiveAppend([a,b,c,d,e],[f,g,h],X)
%ANSWER X=[a,b,c,d,e,f,g,h]

%QUERY testTailRecursiveAppend([],[a,b,c,d,e,f,g,h],X)
%ANSWER X=[a,b,c,d,e,f,g,h]

%QUERY testTailRecursiveAppend([a,b,c,d,e,f,g,h],[],X)
%ANSWER X=[a,b,c,d,e,f,g,h]

%TRUE testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,h])
%FALSE testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,x])
%FALSE testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,h,i])
%FALSE testTailRecursiveAppend([a,b,c,d,e],[f,g,h],[a,b,c,d,e,f,g,])

testTailRecursiveRepeat(N). 
testTailRecursiveRepeat(N) :- N > 1, N1 is N-1, testTailRecursiveRepeat(N1).

%QUERY testTailRecursiveRepeat(3)
%ANSWER/
%ANSWER/
%ANSWER/
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

%QUERY testConjunction(X,Y,Z)
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,b,c,100,16]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,a,s,d,f,101,17]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
%ANSWER
%ANSWER
% X = [q,w,e,r,t,y]
% Y = [u,i,o,p]
% Z = [2,4,16,q,w,e,r,t,y,u,i,o,p,x,y,z,100,16]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,b,c,99,15]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,a,s,d,f,100,16]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
%ANSWER
%ANSWER
% X = [a,s,d,f]
% Y = [g,h,j,k,l]
% Z = [2,4,16,a,s,d,f,g,h,j,k,l,x,y,z,99,15]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,b,c,97,13]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,a,s,d,f,98,14]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
%ANSWER
%ANSWER
% X = [z,x,c]
% Y = [v,b,n,m]
% Z = [2,4,16,z,x,c,v,b,n,m,x,y,z,97,13]
%ANSWER
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

%QUERY testOnce(A,B,C,D,E,F,true)
%ANSWER
% A = [q,w,e,r,t,y]
% B = [u,i,o,p]
% C = []
% D = 0
% E = 42
% F = [q,w,e,r,t,y,u,i,o,p]
%ANSWER

%QUERY testOnce(A,B,C,D,E,F,E<43)
%ANSWER
% A = [q,w,e,r,t,y]
% B = [u,i,o,p]
% C = []
% D = 0
% E = 42
% F = [q,w,e,r,t,y,u,i,o,p]
%ANSWER

%QUERY testOnce([a,s,A,f],B,[a,b,c],D,42,F,true)
%ANSWER
% A = d
% B = [g,h,j,k,l]
% D = 3
% F = [a,s,d,f,g,h,j,k,l]
%ANSWER

%QUERY testOnce(A,B,C,3,E,F,true)
%ANSWER
% A = [q,w,e,r,t,y]
% B = [u,i,o,p]
% C = [E0,E1,E2]
% E = 42
% F = [q,w,e,r,t,y,u,i,o,p]
%ANSWER

%FALSE testOnce(A,B,C,D,41,F,true)
%FALSE testOnce(A,B,C,D,E,F,fail)
%FALSE testOnce(A,B,C,D,E,F,E>43)

testWrite(X) :- writeln('hel'), write('lo, '), write(X), write('!').

%QUERY testWrite('world')
%OUTPUT
%hel
%lo, world!
%OUTPUT
%ANSWER/

testNot(X) :- X=1, \+ true.
testNot(X) :- X=2, \+ fail.
testNot(X) :- atom(X), \+ X.
testNot(X) :- X=4, \+ repeat(X).
testNot(X) :- X=5, \+ number(X).
testNot(X) :- X=6, \+ atom(X).
testNot(X) :- X=7, \+ [A,B,C,D,E]=[1,2,3,4,5].
testNot(X) :- C=3, D=5, \+ [A,B,C,9,D,E]=[1,2,3,4,5,6], A=42, B=7, E=51, X is A+B+C+D+E.

%QUERY testNot(X)
%ANSWER X=2
%ANSWER X=6
%ANSWER X=108

%TRUE_NO testNot(fail)
%FALSE testNot(true)

% TODO expand to b test unify consequent arguments

testAnonymous(p(X,Y),_,Y) :- singleFactSingleArgument(X).

%QUERY testAnonymous(X,32,12)
%ANSWER X=p(42, 12)

% TODO review cut examples (including where cut makes multi-clause predicate non-retryable)
testCutSingleRuleMultipleResults :- repeat(5), !, repeat(3).

%QUERY testCutSingleRuleMultipleResults
%ANSWER/
%ANSWER/
%ANSWER/

testCutSingleRuleSingleResult :- 1<2, !, 2>1.

%TRUE testCutSingleRuleSingleResult

testCutManyRules(X,Y,RuleNo) :- RuleNo=1, X>5, !, Y<7, repeat(3).
testCutManyRules(X,Y,RuleNo) :- RuleNo=2, X<3, !, Y<7, repeat(7), !, repeat(3), !.
testCutManyRules(X,Y,RuleNo) :- RuleNo=3.
testCutManyRules(X,Y,RuleNo) :- RuleNo=4, X>3, !, Y<7, repeat(3), !.
testCutManyRules(X,Y,RuleNo) :- RuleNo=5, repeat(2).

%QUERY testCutManyRules(6, 6, RuleNo)
%ANSWER RuleNo=1
%ANSWER RuleNo=1
%ANSWER RuleNo=1
%NO

%FALSE testCutManyRules(6, 7, RuleNo)

%QUERY testCutManyRules(2, 6, RuleNo)
%ANSWER RuleNo=2

%FALSE testCutManyRules(2, 7, RuleNo)

%QUERY testCutManyRules(4, 6, RuleNo)
%ANSWER RuleNo=3
%ANSWER RuleNo=4

%QUERY testCutManyRules(4, 7, RuleNo)
%ANSWER RuleNo=3
%NO

%QUERY testCutManyRules(3, 6, RuleNo)
%ANSWER RuleNo=3
%ANSWER RuleNo=5
%ANSWER RuleNo=5

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
%QUERY testCalculatables(X, 3, 7)
%ANSWER X=1000
%ANSWER X=1.5
%ANSWER X=10
%ANSWER X=-4
%ANSWER X=21
%ANSWER X=0
%ANSWER X=19
%ANSWER X=18.5
%ANSWER X=10

%TRUE trace
%QUERY testCalculatables(X, 3, 7)
%OUTPUT
% [1] CALL testCalculatables(X, 3, 7)
% [1] EXIT testCalculatables(1000, 3, 7)
%
%OUTPUT
%ANSWER X=1000
%OUTPUT
% [1] REDO testCalculatables(1000, 3, 7)
% [1] EXIT testCalculatables(1.5, 3, 7)
%
%OUTPUT
%ANSWER X=1.5
%OUTPUT
% [1] REDO testCalculatables(1.5, 3, 7)
% [1] EXIT testCalculatables(10, 3, 7)
%
%OUTPUT
%ANSWER X=10
%OUTPUT
% [1] REDO testCalculatables(10, 3, 7)
% [1] EXIT testCalculatables(-4, 3, 7)
%
%OUTPUT
%ANSWER X=-4
%OUTPUT
% [1] REDO testCalculatables(-4, 3, 7)
% [1] EXIT testCalculatables(21, 3, 7)
%
%OUTPUT
%ANSWER X=21
%OUTPUT
% [1] REDO testCalculatables(21, 3, 7)
% [1] EXIT testCalculatables(0, 3, 7)
%
%OUTPUT
%ANSWER X=0
%OUTPUT
% [1] REDO testCalculatables(0, 3, 7)
% [1] EXIT testCalculatables(19, 3, 7)
%
%OUTPUT
%ANSWER X=19
%OUTPUT
% [1] REDO testCalculatables(19, 3, 7)
% [1] EXIT testCalculatables(18.5, 3, 7)
%
%OUTPUT
%ANSWER X=18.5
%OUTPUT
% [1] REDO testCalculatables(18.5, 3, 7)
% [1] EXIT testCalculatables(10, 3, 7)
%
%OUTPUT
%ANSWER X=10
