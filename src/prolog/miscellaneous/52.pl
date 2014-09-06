%QUERY X = p(_), X = p(1)
%ANSWER X=p(1)

%FALSE X = p(_), X = p(1), X = p(2)

%QUERY X = p(_), X = p(1), Y=p(_), Y=p(2)
%ANSWER
% X=p(1)
% Y=p(2)
%ANSWER

%TRUE p(_,_,_)=p(1,2,3)

x(X,Y) :- X = p(_), X = p(1), Y=p(_), Y=p(2).

%QUERY x(X,Y)
%ANSWER
% X=p(1)
% Y=p(2)
%ANSWER
