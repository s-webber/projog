test_repeat(N).
test_repeat(N) :- N > 1, N1 is N-1, test_repeat(N1).

p(1).
p(10).
p(100).

q1('q').
q1('w').
q1('e').
q1('r').
q1('t').
q1('y').
q1('a').

q2('a').
q2('s').
q2('d').
q2('f').
q2('g').
q2('h').
q2('i').

lastquery(X,Y,Z) :- F = 1, A = B, test_repeat(1), C = D, test_repeat(F), C = X, test_repeat(F), A = Y, test_repeat(F), E = A, F = B, p(D), Z is D + F.

%QUERY lastquery(X,Y,Z)
%ANSWER
% X=1
% Y=1
% Z=2
%ANSWER
%ANSWER
% X=10
% Y=1
% Z=11
%ANSWER
%ANSWER
% X=100
% Y=1
% Z=101
%ANSWER
%NO

test1(X) :- test2(X).

test2(999) :- fail.
test2(X) :- test3(X).
test2(X) :- X=999, X<999.
test2(X) :- X=999, !, X>999.
test2(999).

test3(X) :- test4(X), test4(X).

test4(X) :- Y = X, test5(Y).
test5(X) :- X = Y, test6(Y).
test6(X) :- test7(Y), X = Y.
test7(X) :- test9(Y), Y = X.

test9(X) :- test10(Z), A = B, B = X, Y = X, Y = Z.
test10(X) :- A = B, B = X, Y = X, Y = Z, test11(Z).
test11(X) :- test12(Z), A = B, B = X, Y = Z, Y = X.
test12(X) :- A = B, B = X, Y = Z, Y = X, test13(Z).
test13(X) :- test14(Z), A = B, Y = X, B = X, Y = Z.
test14(X) :- A = B, Y = X, B = X, Y = Z, test15(Z).
test15(X) :- test16(Z), A = B, Y = X, Y = Z, B = X.
test16(X) :- A = B, Y = X, Y = Z, B = X, test17(Z).
test17(X) :- test18(Z), A = B, Y = Z, B = X, Y = X.
test18(X) :- A = B, Y = Z, B = X, Y = X, test19(Z).
test19(X) :- test20(Z), A = B, Y = Z, Y = X, B = X.
test20(X) :- A = B, Y = Z, Y = X, B = X, test21(Z).
test21(X) :- test22(Z), B = X, A = B, Y = X, Y = Z.
test22(X) :- B = X, A = B, Y = X, Y = Z, test23(Z).
test23(X) :- test24(Z), B = X, A = B, Y = Z, Y = X.
test24(X) :- B = X, A = B, Y = Z, Y = X, test25(Z).
test25(X) :- test26(Z), B = X, Y = X, A = B, Y = Z.
test26(X) :- B = X, Y = X, A = B, Y = Z, test27(Z).
test27(X) :- test28(Z), B = X, Y = X, Y = Z, A = B.
test28(X) :- B = X, Y = X, Y = Z, A = B, test29(Z).
test29(X) :- test30(Z), B = X, Y = Z, A = B, Y = X.
test30(X) :- B = X, Y = Z, A = B, Y = X, test31(Z).
test31(X) :- test32(Z), B = X, Y = Z, Y = X, A = B.
test32(X) :- B = X, Y = Z, Y = X, A = B, test33(Z).
test33(X) :- test34(Z), Y = X, A = B, B = X, Y = Z.
test34(X) :- Y = X, A = B, B = X, Y = Z, test35(Z).
test35(X) :- test36(Z), Y = X, A = B, Y = Z, B = X.
test36(X) :- Y = X, A = B, Y = Z, B = X, test37(Z).
test37(X) :- test38(Z), Y = X, B = X, A = B, Y = Z.
test38(X) :- Y = X, B = X, A = B, Y = Z, test39(Z).
test39(X) :- test40(Z), Y = X, B = X, Y = Z, A = B.
test40(X) :- Y = X, B = X, Y = Z, A = B, test41(Z).
test41(X) :- test42(Z), Y = X, Y = Z, A = B, B = X.
test42(X) :- Y = X, Y = Z, A = B, B = X, test43(Z).
test43(X) :- test44(Z), Y = X, Y = Z, B = X, A = B.
test44(X) :- Y = X, Y = Z, B = X, A = B, test45(Z).
test45(X) :- test46(Z), Y = Z, A = B, B = X, Y = X.
test46(X) :- Y = Z, A = B, B = X, Y = X, test47(Z).
test47(X) :- test48(Z), Y = Z, A = B, Y = X, B = X.
test48(X) :- Y = Z, A = B, Y = X, B = X, test49(Z).
test49(X) :- test50(Z), Y = Z, B = X, A = B, Y = X.
test50(X) :- Y = Z, B = X, A = B, Y = X, test51(Z).
test51(X) :- test52(Z), Y = Z, B = X, Y = X, A = B.
test52(X) :- Y = Z, B = X, Y = X, A = B, test53(Z).
test53(X) :- test54(Z), Y = Z, Y = X, A = B, B = X.
test54(X) :- Y = Z, Y = X, A = B, B = X, test55(Z).
test55(X) :- test56(Z), Y = Z, Y = X, B = X, A = B.
test56(X) :- Y = Z, Y = X, B = X, A = B, test100(Z).

test100(X) :- [Y] = [Z], [Y] = [X], [B] = [X], [A] = [B], test101(Z).
test101(X) :- test102(Z), [Y] = [Z], [Y] = [X], [B] = [X], [A] = [B].
test102(X) :- [V1,Y] = [V2,Z], [Y,V4] = [X,V5], [B|V6] = [X|V7], [V8|A] = [V9,B], test103(Z).
test103(X) :- test104(Z), [V1,Y] = [V2,Z], [Y,V4] = [X,V5], [B|V6] = [X|V7], [V8|A] = [V9,B].
test104(X) :- p(p(a,b,c,[V1,Y,V2])) = p(p(a,b,c,[1,Z,2])), p(p(a,b,c,[1,2,Y])) = p(p(a,b,c,[1,2,X])), p(p(a,b,c,[B,2,3])) = p(p(a,b,c,[X,2,3])), p(A) = p(B), test105(Z).
test105(X) :- test999(Z), p(p(a,b,c,[V1,Y,V2])) = p(p(a,b,c,[1,Z,2])), p(p(a,b,c,[1,2,Y])) = p(p(a,b,c,[1,2,X])), p(p(a,b,c,[B,2,3])) = p(p(a,b,c,[X,2,3])), p(A) = p(B).

test999(X) :- lastquery(A, B, X).

%TRUE_NO test1(2)
%TRUE_NO test1(11)
%TRUE_NO test1(101)
%FALSE test1(3)

%QUERY test1(X)
%ANSWER X=2
%ANSWER X=11
%ANSWER X=101
%NO
