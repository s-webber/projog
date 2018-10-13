% ProjogSourceReader test

testTrueFail(X) :- X = 'rule1', true.
testTrueFail(X) :- X = 'rule2', fail.
testTrueFail(X) :- fail, X = 'rule3'.
testTrueFail(X) :- true, X = 'rule4'.

%TRUE notrace
%QUERY testTrueFail(X)
%ANSWER X=rule1
%ANSWER X=rule4

%TRUE trace
%QUERY testTrueFail(X)
%OUTPUT
% [1] CALL testTrueFail(X)
% [1] EXIT testTrueFail(rule1)
%
%OUTPUT
%ANSWER X=rule1
%OUTPUT
% [1] REDO testTrueFail(rule1)
% [1] EXIT testTrueFail(rule4)
%
%OUTPUT
%ANSWER X=rule4

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

testCallCompiledEvaluatable(A, B) :- C is (B*2)+1, testCalculatables(A, B, C), A>1.5, A=<18.5.

%TRUE notrace
%QUERY testCallCompiledEvaluatable(X, 3)
%ANSWER X=10
%ANSWER X=18.5
%ANSWER X=10

%TRUE trace
%QUERY testCallCompiledEvaluatable(X, 3)
%OUTPUT
% [1] CALL testCallCompiledEvaluatable(X, 3)
% [2] CALL testCalculatables(X, 3, 7)
% [2] EXIT testCalculatables(1000, 3, 7)
% [2] REDO testCalculatables(1000, 3, 7)
% [2] EXIT testCalculatables(1.5, 3, 7)
% [2] REDO testCalculatables(1.5, 3, 7)
% [2] EXIT testCalculatables(10, 3, 7)
% [1] EXIT testCallCompiledEvaluatable(10, 3)
%
%OUTPUT
%ANSWER X=10
%OUTPUT
% [1] REDO testCallCompiledEvaluatable(10, 3)
% [2] REDO testCalculatables(10, 3, 7)
% [2] EXIT testCalculatables(-4, 3, 7)
% [2] REDO testCalculatables(-4, 3, 7)
% [2] EXIT testCalculatables(21, 3, 7)
% [2] REDO testCalculatables(21, 3, 7)
% [2] EXIT testCalculatables(0, 3, 7)
% [2] REDO testCalculatables(0, 3, 7)
% [2] EXIT testCalculatables(19, 3, 7)
% [2] REDO testCalculatables(19, 3, 7)
% [2] EXIT testCalculatables(18.5, 3, 7)
% [1] EXIT testCallCompiledEvaluatable(18.5, 3)
%
%OUTPUT
%ANSWER X=18.5
%OUTPUT
% [1] REDO testCallCompiledEvaluatable(18.5, 3)
% [2] REDO testCalculatables(18.5, 3, 7)
% [2] EXIT testCalculatables(10, 3, 7)
% [1] EXIT testCallCompiledEvaluatable(10, 3)
%
%OUTPUT
%ANSWER X=10

testSimpleTailRecursive([]).
testSimpleTailRecursive([X|Xs]) :- testSimpleTailRecursive(Xs).

%TRUE notrace
%TRUE testSimpleTailRecursive([a,b,c])
%FALSE testSimpleTailRecursive(abc)

%TRUE trace
%QUERY testSimpleTailRecursive([a,b,c])
%OUTPUT
% [1] CALL testSimpleTailRecursive([a,b,c])
% [1] EXIT testSimpleTailRecursive([])
%
%OUTPUT
%ANSWER/
%QUERY testSimpleTailRecursive(abc)
%OUTPUT
% [1] CALL testSimpleTailRecursive(abc)
% [1] FAIL testSimpleTailRecursive(abc)
%
%OUTPUT
%NO

testSimpleNonTailRecursive(N). 
testSimpleNonTailRecursive(N) :- N > 1, N1 is N-1, testSimpleNonTailRecursive(N1).

%TRUE notrace
%QUERY testSimpleNonTailRecursive(3)
%ANSWER/
%ANSWER/
%ANSWER/
%NO

testNumericComparison(A,B,C) :- A>B, A<C.
testNumericComparison(A,B,C) :- A>=B, A=<C.
testNumericComparison(A,B,C) :- A=:=B, A=\=C.

%QUERY testNumericComparison(6,5,7)
%ANSWER/
%ANSWER/
%NO

%TRUE_NO testNumericComparison(6,6,6)

testNonOptimisedAbstractSingletonEvaluatable(X) :- atom(X), X@<b.

%TRUE testNonOptimisedAbstractSingletonEvaluatable(a)
%FALSE testNonOptimisedAbstractSingletonEvaluatable(b)
%FALSE testNonOptimisedAbstractSingletonEvaluatable(c)
%FALSE testNonOptimisedAbstractSingletonEvaluatable(a(a))
%FALSE testNonOptimisedAbstractSingletonEvaluatable(X)

testNonOptimisedAbstractRetryableEvaluatable(X) :- repeat(X), repeat(3).

%QUERY testNonOptimisedAbstractRetryableEvaluatable(2)
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/
%ANSWER/

multiple_rules_with_multiple_immutable_arguments(q,w,e).
multiple_rules_with_multiple_immutable_arguments(a,s,d).
multiple_rules_with_multiple_immutable_arguments(z,x,z).

single_rule_with_multiple_immutable_arguments(a,s,d).

multiple_rules_with_single_immutable_argument(s).
multiple_rules_with_single_immutable_argument(d).
multiple_rules_with_single_immutable_argument(a).

single_rule_with_single_immutable_argument(z).

testImmutableFacts(W, X, Y, Z) :- 
	multiple_rules_with_single_immutable_argument(X),
	multiple_rules_with_multiple_immutable_arguments(W,_,W),	
	atom(W), 
	multiple_rules_with_single_immutable_argument(Y),
	single_rule_with_single_immutable_argument(W),
	multiple_rules_with_single_immutable_argument(Z),
	single_rule_with_multiple_immutable_arguments(X,Y,Z),
	atom(X), atom(Y), atom(Z),
	multiple_rules_with_multiple_immutable_arguments(X,Y,Z).
	
%QUERY testImmutableFacts(W, X, Y, Z)
%ANSWER 
% W=z
% X=a
% Y=s
% Z=d
%ANSWER
%NO

testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(A,B,C), A=X, B=Y, C=Z, RuleNo=1.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(A,B,C), X=A, Y=B, Z=C, RuleNo=2.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(_,Y,e),
	X=q,
	multiple_rules_with_multiple_immutable_arguments(X,Y,Z),
	RuleNo=3.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(A,Y,A),
	X=A, A=Z,
	RuleNo=4.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(q,w,z), RuleNo=5.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(q,y,e), RuleNo=6.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(y,w,z), RuleNo=7.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(q,e,w), RuleNo=8.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(q,w,e), 
	multiple_rules_with_multiple_immutable_arguments(a,s,d), 
	multiple_rules_with_multiple_immutable_arguments(z,x,z), 
	RuleNo=9.
testMultipleRulesWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	multiple_rules_with_multiple_immutable_arguments(X,w,e), 
	multiple_rules_with_multiple_immutable_arguments(a,Y,d), 
	multiple_rules_with_multiple_immutable_arguments(z,x,Z), 
	RuleNo=10.

%QUERY testMultipleRulesWithMultipleImmutableArguments(X,Y,Z,RuleNo)
%ANSWER 
% X=q
% Y=w
% Z=e
% RuleNo=1
%ANSWER
%ANSWER 
% X=a
% Y=s
% Z=d
% RuleNo=1
%ANSWER
%ANSWER 
% X=z
% Y=x
% Z=z
% RuleNo=1
%ANSWER
%ANSWER 
% X=q
% Y=w
% Z=e
% RuleNo=2
%ANSWER
%ANSWER 
% X=a
% Y=s
% Z=d
% RuleNo=2
%ANSWER
%ANSWER 
% X=z
% Y=x
% Z=z
% RuleNo=2
%ANSWER
%ANSWER 
% X=q
% Y=w
% Z=e
% RuleNo=3
%ANSWER
%ANSWER 
% X=z
% Y=x
% Z=z
% RuleNo=4
%ANSWER
%ANSWER 
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% RuleNo=9
%ANSWER
%ANSWER 
% X=q
% Y=s
% Z=z
% RuleNo=10
%ANSWER
%NO

testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(A,B,C), A=X, B=Y, C=Z, RuleNo=1.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(A,B,C), X=C, Y=A, Z=B, RuleNo=2.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(_,Y,d),
	X=a,
	single_rule_with_multiple_immutable_arguments(X,Y,Z),
	RuleNo=3.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(a,s,d), 
	RuleNo=4.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(b,s,d), RuleNo=5.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(a,d,d), RuleNo=6.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(a,s,t), RuleNo=7.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(d,s,a), RuleNo=8.
testSingleRuleWithMultipleImmutableArguments(X, Y, Z, RuleNo) :- 
	single_rule_with_multiple_immutable_arguments(a,s,X), 
	single_rule_with_multiple_immutable_arguments(Z,s,d), 
	single_rule_with_multiple_immutable_arguments(a,Y,d), 
	RuleNo=9.

%QUERY testSingleRuleWithMultipleImmutableArguments(X,Y,Z,RuleNo)
%ANSWER 
% X=a
% Y=s
% Z=d
% RuleNo=1
%ANSWER
%ANSWER 
% X=d
% Y=a
% Z=s
% RuleNo=2
%ANSWER
%ANSWER 
% X=a
% Y=s
% Z=d
% RuleNo=3
%ANSWER
%ANSWER 
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% RuleNo=4
%ANSWER
%ANSWER 
% X=d
% Y=s
% Z=a
% RuleNo=9
%ANSWER

testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(X), RuleNo=1.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(z), RuleNo=2.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(q), RuleNo=3.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(z(a)), RuleNo=4.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(Y), X=Y, RuleNo=5.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(Y), Y=X, RuleNo=6.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	X=Y, single_rule_with_single_immutable_argument(Y), RuleNo=7.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	Y=X, single_rule_with_single_immutable_argument(Y), RuleNo=8.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	single_rule_with_single_immutable_argument(X), single_rule_with_single_immutable_argument(X), RuleNo=9.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	X=z, single_rule_with_single_immutable_argument(X), RuleNo=10.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	X=q, single_rule_with_single_immutable_argument(X), RuleNo=11.
testSingleRuleWithSingleImmutableArgument(X, RuleNo) :- 
	X=z(a), single_rule_with_single_immutable_argument(X), RuleNo=12.

%QUERY testSingleRuleWithSingleImmutableArgument(X,RuleNo)
%ANSWER 
% X=z
% RuleNo=1
%ANSWER
%ANSWER 
% X=UNINSTANTIATED VARIABLE
% RuleNo=2
%ANSWER
%ANSWER 
% X=z
% RuleNo=5
%ANSWER
%ANSWER 
% X=z
% RuleNo=6
%ANSWER
%ANSWER 
% X=z
% RuleNo=7
%ANSWER
%ANSWER 
% X=z
% RuleNo=8
%ANSWER
%ANSWER 
% X=z
% RuleNo=9
%ANSWER
%ANSWER 
% X=z
% RuleNo=10
%ANSWER
%NO

testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(X), RuleNo=1.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(s), 
	multiple_rules_with_single_immutable_argument(d), 
	multiple_rules_with_single_immutable_argument(a),
	RuleNo=2.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(q), RuleNo=3.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(s(a)), RuleNo=4.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(Y), X=Y, RuleNo=5.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(Y), Y=X, RuleNo=6.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=Y, multiple_rules_with_single_immutable_argument(Y), RuleNo=7.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	Y=X, multiple_rules_with_single_immutable_argument(Y), RuleNo=8.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	multiple_rules_with_single_immutable_argument(X), multiple_rules_with_single_immutable_argument(X), RuleNo=9.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=d, multiple_rules_with_single_immutable_argument(X), RuleNo=10.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=s, multiple_rules_with_single_immutable_argument(X), RuleNo=11.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=a, multiple_rules_with_single_immutable_argument(X), RuleNo=12.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=q, multiple_rules_with_single_immutable_argument(X), RuleNo=13.
testMultipleRulesWithSingleImmutableArgument(X, RuleNo) :- 
	X=s(a), multiple_rules_with_single_immutable_argument(X), RuleNo=14.

%QUERY testMultipleRulesWithSingleImmutableArgument(X,RuleNo)
%ANSWER 
% X=s
% RuleNo=1
%ANSWER
%ANSWER 
% X=d
% RuleNo=1
%ANSWER
%ANSWER 
% X=a
% RuleNo=1
%ANSWER
%ANSWER 
% X=UNINSTANTIATED VARIABLE
% RuleNo=2
%ANSWER
%ANSWER 
% X=s
% RuleNo=5
%ANSWER
%ANSWER 
% X=d
% RuleNo=5
%ANSWER
%ANSWER 
% X=a
% RuleNo=5
%ANSWER
%ANSWER 
% X=s
% RuleNo=6
%ANSWER
%ANSWER 
% X=d
% RuleNo=6
%ANSWER
%ANSWER 
% X=a
% RuleNo=6
%ANSWER
%ANSWER 
% X=s
% RuleNo=7
%ANSWER
%ANSWER 
% X=d
% RuleNo=7
%ANSWER
%ANSWER 
% X=a
% RuleNo=7
%ANSWER
%ANSWER 
% X=s
% RuleNo=8
%ANSWER
%ANSWER 
% X=d
% RuleNo=8
%ANSWER
%ANSWER 
% X=a
% RuleNo=8
%ANSWER
%ANSWER 
% X=s
% RuleNo=9
%ANSWER
%ANSWER 
% X=d
% RuleNo=9
%ANSWER
%ANSWER 
% X=a
% RuleNo=9
%ANSWER
%ANSWER 
% X=d
% RuleNo=10
%ANSWER
%ANSWER 
% X=s
% RuleNo=11
%ANSWER
%ANSWER 
% X=a
% RuleNo=12
%ANSWER
%NO

testCutSingleRuleMultipleResults :- repeat(5), !, repeat(3).

%QUERY testCutSingleRuleMultipleResults
%ANSWER/
%ANSWER/
%ANSWER/
%NO

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
%NO

%FALSE testCutManyRules(2, 7, RuleNo)

%QUERY testCutManyRules(4, 6, RuleNo)
%ANSWER RuleNo=3
%ANSWER RuleNo=4
%NO

%QUERY testCutManyRules(4, 7, RuleNo)
%ANSWER RuleNo=3
%NO

%QUERY testCutManyRules(3, 6, RuleNo)
%ANSWER RuleNo=3
%ANSWER RuleNo=5
%ANSWER RuleNo=5

% TODO calling recursive functions