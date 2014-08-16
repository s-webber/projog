x(X,Y) :- Y is X + 0.5.
x(X,Y) :- Y is X - 0.5.
x(X,Y) :- Y is X * 0.5.
x(X,Y) :- Y is X / 0.5.
x(X,Y) :- Y is 0.25 + X.
x(X,Y) :- Y is 25 + X.
x(X,Y) :- Y is X + X.
x(X,Y) :- Y is 0.5 + 0.5.
x(X,Y) :- Y is 0.5 + 2.
x(X,Y) :- Y is 0.5 + 0.5 + X.
x(X,Y) :- Y is 0.5 + 7 + X.

%QUERY x(2,Y)
%ANSWER Y=2.5
%ANSWER Y=1.5
%ANSWER Y=1.0
%ANSWER Y=4.0
%ANSWER Y=2.25
%ANSWER Y=27
%ANSWER Y=4
%ANSWER Y=1.0
%ANSWER Y=2.5
%ANSWER Y=3.0
%ANSWER Y=9.5

%QUERY x(2.0,Y)
%ANSWER Y=2.5
%ANSWER Y=1.5
%ANSWER Y=1.0
%ANSWER Y=4.0
%ANSWER Y=2.25
%ANSWER Y=27.0
%ANSWER Y=4.0
%ANSWER Y=1.0
%ANSWER Y=2.5
%ANSWER Y=3.0
%ANSWER Y=9.5