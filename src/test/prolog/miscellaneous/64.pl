x([a,b]).
x([a,b,c]).
x([a,b,c,d]).
x([a]).

test(List1,List2) :-
   x(List1),
   length(List1, Length1),
   Length1 > 2,
   x(List2),
   length(List2, Length2),
   Length2 > 1.

%QUERY test(List1, List2)
%ANSWER
% List1=[a,b,c]
% List2=[a,b]
%ANSWER
%ANSWER
% List1=[a,b,c]
% List2=[a,b,c]
%ANSWER
%ANSWER
% List1=[a,b,c]
% List2=[a,b,c,d]
%ANSWER
%ANSWER
% List1=[a,b,c,d]
% List2=[a,b]
%ANSWER
%ANSWER
% List1=[a,b,c,d]
% List2=[a,b,c]
%ANSWER
%ANSWER
% List1=[a,b,c,d]
% List2=[a,b,c,d]
%ANSWER
%NO
