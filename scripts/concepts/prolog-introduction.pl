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

%TRUE_NO father(homer,bart)
%FALSE father(homer,rod)
%FALSE father(homer,ralph)

% Question using a variable.

%QUERY father(homer,C)
%ANSWER C=bart
%ANSWER C=lisa
%ANSWER C=maggie
%NO

% Question using two variables - evaluation of the query uses backtracking to find multiple solutions.

%QUERY father(F,C)
%ANSWER 
% F=homer
% C=bart
%ANSWER
%ANSWER 
% F=homer
% C=lisa
%ANSWER 
%ANSWER 
% F=homer
% C=maggie
%ANSWER 
%ANSWER 
% F=ned
% C=rod
%ANSWER 
%ANSWER 
% F=ned
% C=todd
%ANSWER 

%QUERY parents(homer,marge,C)
%ANSWER C=bart
%ANSWER C=lisa
%ANSWER C=maggie
%NO

%QUERY brother(lisa,X)
%ANSWER X=bart
%NO
%QUERY brother(bart,X)
%ANSWER X=bart
%NO
