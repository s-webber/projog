% Facts.
father(homer,bart).
father(homer,lisa).
father(homer,maggie).

mother(marge,bart).
mother(marge,lisa).
mother(marge,maggie).

father(ned,rod).
father(ned,todd).

male(homer).
male(bart).
male(ned).
male(rod).
male(todd).

% Rules.
parents(F,M,C) :- father(F,C), mother(M,C).

siblings(A,B) :- parents(F,M,A), parents(F,M,B).
brother(A,B) :- siblings(A,B), male(B).

% Simple questions - true if the fact exists in the knowledge base, else false.

%TRUE father(homer,bart)
%TRUE father(homer,lisa)
%TRUE father(homer,maggie)
%FAIL father(homer,rod)
%FAIL father(homer,ralph)

% Question using a variable.

%?- father(homer,C)
% C=bart
% C=lisa
% C=maggie

%?- father(ned,C)
% C=rod
% C=todd

% Question using two variables - evaluation of the query uses backtracking to find multiple solutions.

%?- father(F,C)
% F=homer
% C=bart
% F=homer
% C=lisa
% F=homer
% C=maggie
% F=ned
% C=rod
% F=ned
% C=todd

%?- parents(homer,marge,C)
% C=bart
% C=lisa
% C=maggie

%?- brother(lisa,X)
% X=bart
%NO
%?- brother(bart,X)
% X=bart
%NO
