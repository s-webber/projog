% "Hello world" example.

%?- write('hello, world'), nl
%OUTPUT 
%hello, world
%
%OUTPUT
%YES

% Dynamically populate knowledge base with facts.

%TRUE asserta(message(hi, world))
%TRUE assertz(message(hello, everyone))
%TRUE asserta(message(hello, world))

% Query using new facts added to the knowledge base.

%TRUE message(hello, everyone)
%FAIL message(hi, everyone)

%?- message(X, Y)
% X=hello
% Y=world
% X=hi
% Y=world
% X=hello
% Y=everyone

%?- message(hello, Y)
% Y=world
% Y=everyone

% Simple example of both unification and arithmetic.

%?- W=X, X=1+1, Y is W, Z is -W
% W=1 + 1
% X=1 + 1
% Y=2
% Z=-2

% Populate the knowledge base with clauses read from a file containing Prolog syntax.

%TRUE consult('towers-of-hanoi-example.pl')

% View the definition of hanoi that has just been parsed from the consulted file.

%?- listing(hanoi)
%OUTPUT
%hanoi(N) :- move(N, left, centre, right)
%
%OUTPUT
%YES

% Use the clauses loaded from towers-of-hanoi-example.pl in a query.

%?- hanoi(2)
%OUTPUT
%[move,a,disc,from,the,left,pole,to,the,right,pole]
%[move,a,disc,from,the,left,pole,to,the,centre,pole]
%[move,a,disc,from,the,right,pole,to,the,centre,pole]
%
%OUTPUT
%YES

