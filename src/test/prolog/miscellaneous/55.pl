split(L,L1,L2) :- append(L1,L2,L), L1 = [_|_], L2 = [_|_].

%?- split([a,b,c,d,e],A,B)
% A=[a]
% B=[b,c,d,e]
% A=[a,b]
% B=[c,d,e]
% A=[a,b,c]
% B=[d,e]
% A=[a,b,c,d]
% B=[e]
%NO
