split(L,L1,L2) :- append(L1,L2,L), L1 = [_|_], L2 = [_|_].

%QUERY split([a,b,c,d,e],A,B)
%ANSWER
% A=[a]
% B=[b,c,d,e]
%ANSWER
%ANSWER
% A=[a,b]
% B=[c,d,e]
%ANSWER
%ANSWER
% A=[a,b,c]
% B=[d,e]
%ANSWER
%ANSWER
% A=[a,b,c,d]
% B=[e]
%ANSWER
%NO
