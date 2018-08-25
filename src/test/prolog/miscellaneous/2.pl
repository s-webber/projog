% simple example of query of an implication giving many results
%QUERY father(C,F)
%ANSWER
% F=tom
% C=harry
%ANSWER
%ANSWER
% F=fred
% C=john
%ANSWER
%ANSWER
% F=fred
% C=mary
%ANSWER
%QUERY parent(C,M,F)
%ANSWER
% M=ann
% F=fred
% C=john
%ANSWER
%ANSWER
% M=ann
% F=fred
% C=mary
%ANSWER
%ANSWER
% M=jane
% F=tom
% C=harry
%ANSWER
%NO
female(mary).
mother(john,ann).
mother(mary,ann).
mother(harry,jane).
father(harry,tom).
father(john,fred).
father(mary,fred).
parent(C,M,F) :- mother(C,M),father(C,F).
