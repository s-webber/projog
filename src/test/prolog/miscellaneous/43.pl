x(L,L).
x([X|Xs],Ys) :- x(Xs,Ys).

%QUERY x([a,b,c], X)
%ANSWER X=[a,b,c]
%ANSWER X=[b,c]
%ANSWER X=[c]
%ANSWER X=[]
%NO

%FALSE x([a,b,c], [a,c])

%QUERY x(a, X)
%ANSWER X=a
%NO