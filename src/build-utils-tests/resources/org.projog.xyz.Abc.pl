%QUERY write(hello)
%OUTPUT hello
%ANSWER/

%QUERY write(hello)
%OUTPUT hi
%ANSWER/

%QUERY write(hello), nl, write(world), nl
%OUTPUT
%hello
%world
%
%OUTPUT
%ANSWER/

%QUERY write(hello), nl, write(world), nl
%OUTPUT
%hello
%earth
%
%OUTPUT
%ANSWER/

z(1).
z(2).
z(3).
z(q).

%QUERY z(Z), write(here), write(Z), Y is Z*2
%OUTPUT here1
%ANSWER
% Z=1
% Y=2
%ANSWER
%OUTPUT here2
%ANSWER
% Z=2
% Y=4
%ANSWER
%OUTPUT here3
%ANSWER
% Z=3
% Y=6
%ANSWER
%OUTPUT hereq
%ERROR Cannot find calculatable: q

%QUERY z(Z), write(here), write(Z), Y is Z*2
%OUTPUT here1
%ANSWER
% Z=1
% Y=2
%ANSWER
%OUTPUT here2
%ANSWER
% Z=2
% Y=4
%ANSWER
%OUTPUT here3
%ANSWER
% Z=3
% Y=6
%ANSWER
%ERROR Cannot find calculatable: q

%QUERY z(Z), write(here), write(Z), Y is Z*2
%OUTPUT here1
%ANSWER
% Z=1
% Y=2
%ANSWER
%OUTPUT here2
%ANSWER
% Z=2
% Y=4
%ANSWER
%OUTPUT here3
%ANSWER
% Z=3
% Y=6
%ANSWER
%OUTPUT hereq
