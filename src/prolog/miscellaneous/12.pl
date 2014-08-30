x1(W,Z) :- W is 1, Z is 2.

%FALSE x1(X,X)
%QUERY x1(X,Y)
%ANSWER
% X=1
% Y=2
%ANSWER

x2(W,Z) :- W is 1, Z == 1.

%QUERY x2(X,X)
%ANSWER
% X=1
%ANSWER
%FALSE x2(X,Y)

x3(X, Y) :- X = Y, x1(X,Y).
%FALSE x3(X,X)
%FALSE x3(X,Y)

x4(X, Y) :- X = Y, x2(X,Y).
%QUERY x4(X,Y)
%ANSWER
% X=1
% Y=1
%ANSWER
%QUERY x4(X,X)
%ANSWER
% X=1
%ANSWER

%QUERY X=Y, x2(X,Y)
%ANSWER 
% X=1
% Y=1
%ANSWER

%QUERY Y=X, x2(X,Y)
%ANSWER 
% X=1
% Y=1
%ANSWER

q(1).
q(2).
q(3).

x5(X, Y) :- X = Y, q(X).

%QUERY x5(X,Y)
%ANSWER 
% X=1
% Y=1
%ANSWER
%ANSWER 
% X=2
% Y=2
%ANSWER
%ANSWER 
% X=3
% Y=3
%ANSWER

%QUERY x5(X,X)
%ANSWER 
% X=1
%ANSWER
%ANSWER 
% X=2
%ANSWER
%ANSWER 
% X=3
%ANSWER

x6(X, Y) :- X = Y, q(Y).

%QUERY x6(X,Y)
%ANSWER 
% X=1
% Y=1
%ANSWER
%ANSWER 
% X=2
% Y=2
%ANSWER
%ANSWER 
% X=3
% Y=3
%ANSWER

%QUERY x6(X,X)
%ANSWER 
% X=1
%ANSWER
%ANSWER 
% X=2
%ANSWER
%ANSWER 
% X=3
%ANSWER