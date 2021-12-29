% simple example of query of an implication giving many results
%?- father(C,F)
% F=tom
% C=harry
% F=fred
% C=john
% F=fred
% C=mary
%?- parent(C,M,F)
% M=ann
% F=fred
% C=john
% M=ann
% F=fred
% C=mary
% M=jane
% F=tom
% C=harry

female(mary).
mother(john,ann).
mother(mary,ann).
mother(harry,jane).
father(harry,tom).
father(john,fred).
father(mary,fred).
parent(C,M,F) :- mother(C,M),father(C,F).
