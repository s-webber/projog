% "Hello world" example.

%QUERY write('hello, world'), nl
%OUTPUT 
% hello, world
%
%OUTPUT
%ANSWER/

% Dynamically populate knowledge base with facts.

%TRUE asserta(message(hi, world))
%TRUE assertz(message(hello, everyone))
%TRUE asserta(message(hello, world))

% Query using new facts added to the knowledge base.

%TRUE message(hello, everyone)
%FALSE message(hi, everyone)

%QUERY message(X, Y)
%ANSWER
% X=hello
% Y=world
%ANSWER
%ANSWER
% X=hi
% Y=world
%ANSWER
%ANSWER
% X=hello
% Y=everyone
%ANSWER

%QUERY message(hello, Y)
%ANSWER Y=world
%ANSWER Y=everyone

% Simple example of both unification and arithmetic.

%QUERY W=X, X=1+1, Y is W, Z is -W
%ANSWER
% W=1 + 1
% X=1 + 1
% Y=2
% Z=-2
%ANSWER

% Populate the knowledge base with clauses read from a file containing Prolog syntax.

%TRUE consult('towers-of-hanoi-example.pl')

% View the definition of hanoi that has just been parsed from the consulted file.

%QUERY listing(hanoi)
%OUTPUT
% hanoi(N) :- move(N, left, centre, right)
%
%OUTPUT
%ANSWER/

% Use the clauses loaded from towers-of-hanoi-example.pl in a query.

%QUERY hanoi(2)
%OUTPUT
% [move,a,disc,from,the,left,pole,to,the,right,pole]
% [move,a,disc,from,the,left,pole,to,the,centre,pole]
% [move,a,disc,from,the,right,pole,to,the,centre,pole]
%
%OUTPUT
%ANSWER/
%NO
