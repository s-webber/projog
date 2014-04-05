% Determining if a term is a list.

:- list([]).
list([X|Xs]) :- list(Xs).

% %TRUE% list([a,b,c])
% %FALSE% list(abc)

% Determining is a term is contained in a list.

:- member(X,[X|Xs]).
member(X,[Y|Ys]) :- member(X,Ys).

% %QUERY% member(X, [a,b,c])
% %ANSWER% X=a
% %ANSWER% X=b
% %ANSWER% X=c
% %NO%

% %TRUE_NO% member(a, [a,b,c])
% %TRUE_NO% member(b, [a,b,c])
% %TRUE_NO% member(c, [a,b,c])
% %FALSE% member(d, [a,b,c])
% %QUERY% member(a,[a,a,a])
% %ANSWER/%
% %ANSWER/%
% %ANSWER/%
% %NO%

% Finding prefixes of a list.

:- prefix([],Ys).
prefix([X|Xs],[X|Ys]) :- prefix(Xs,Ys).

% %QUERY% prefix(X, [a,b,c,d,e,f])
% %ANSWER% X=[]
% %ANSWER% X=[a]
% %ANSWER% X=[a,b]
% %ANSWER% X=[a,b,c]
% %ANSWER% X=[a,b,c,d]
% %ANSWER% X=[a,b,c,d,e]
% %ANSWER% X=[a,b,c,d,e,f]
% %NO%

% %TRUE% prefix([a],[a,b,c])
% %TRUE% prefix([a,b],[a,b,c])
% %TRUE% prefix([a,b,c],[a,b,c])
% %FALSE% prefix([x,a,b,c],[a,b,c])
% %FALSE% prefix([a,c],[a,b,c])
% %FALSE% prefix([b,c],[a,b,c])
% %QUERY% prefix([X,b],[a,b,c])
% %ANSWER% X=a
% %NO%

% Finding suffixes of a list.

:- suffix(Xs,Xs).
suffix(Xs,[Y|Ys]) :- suffix(Xs,Ys).

% %QUERY% suffix(X, [a,b,c,d,e,f])
% %ANSWER% X=[a,b,c,d,e,f]
% %ANSWER% X=[b,c,d,e,f]
% %ANSWER% X=[c,d,e,f]
% %ANSWER% X=[d,e,f]
% %ANSWER% X=[e,f]
% %ANSWER% X=[f]
% %ANSWER% X=[]
% %NO%

% %TRUE_NO% suffix([c],[a,b,c])
% %TRUE_NO% suffix([b,c],[a,b,c])
% %TRUE_NO% suffix([a,b,c],[a,b,c])
% %FALSE% suffix([x,a,b,c],[a,b,c])
% %FALSE% suffix([a,c],[a,b,c])
% %FALSE% suffix([a,b],[a,b,c])
% %QUERY% suffix([X,c],[a,b,c])
% %ANSWER% X=b
% %NO%

% Determining sublists of lists.

sublist(Xs,Ys) :- prefix(Xs,Ys).
sublist(Xs,[Y|Ys]) :- sublist(Xs,Ys).

% %TRUE_NO% sublist([a,b,c],[a,b,c,d,e,f])
% %TRUE_NO% sublist([b,c,d],[a,b,c,d,e,f])
% %TRUE_NO% sublist([c,d,e],[a,b,c,d,e,f])
% %FALSE% sublist([b,c,e],[a,b,c,d,e,f])
% %QUERY% sublist([b,X,d],[a,b,c,Y,e,f])
% %ANSWER% 
% X=c
% Y=d
% %ANSWER%
% %NO%

% Appending two lists.

:- append([],Ys,Ys).
append([X|Xs],Ys,[X|Zs]) :- append(Xs,Ys,Zs).

% %TRUE% append([a,b,c],[d,e,f],[a,b,c,d,e,f])
% %FALSE% append([a,b,c],[d,e,f],[a,b,c,d,e,g])
% %FALSE% append([a,b,c],[d,e,f],[a,b,c,d,e,f,g])
% %QUERY% append([a,X,c],[d,e,Y],[a,b,c,Z,e,f])
% %ANSWER%
% X=b
% Y=f
% Z=d
% %ANSWER%
% %NO%
% %QUERY% append([a,X,c],[Y,e(X,Y),f],[a,b,c,d,Z,f])
% %ANSWER%
% X=b
% Y=d
% Z=e(b, d)
% %ANSWER%
% %NO%
% %QUERY% append([a,b,c],[d,e,f],X)
% %ANSWER% X=[a,b,c,d,e,f]
% %QUERY% append([a,b,c],X,[a,b,c,d,e,f])
% %ANSWER% X=[d,e,f]
% %QUERY% append(X,[d,e,f],[a,b,c,d,e,f])
% %ANSWER% X=[a,b,c]
% %NO%

% Reversing the order of terms in a list.
reverse(Xs,Ys) :- reverse(Xs,[],Ys).
reverse([X|Xs],Acc,Ys) :- reverse(Xs,[X|Acc],Ys).
reverse([],Ys,Ys).

% %TRUE% reverse([],[])
% %TRUE_NO% reverse([a],[a])
% %FALSE% reverse([a],[b])
% %TRUE_NO% reverse([a,b],[b,a])
% %FALSE% reverse([a,b],[a,b])
% %FALSE% reverse([a,b],[a,a])
% %FALSE% reverse([a,b],[b,b])
% %FALSE% reverse([a,b],[a])
% %FALSE% reverse([a,b],[b])
% %FALSE% reverse([a,b],[c,b,a])
% %TRUE_NO% reverse([a,b,c,d,e,f],[f,e,d,c,b,a])
% %FALSE% reverse([a,b,c,d,e,f],[f,e,d,c,a,b])
% %QUERY% reverse([a,b,c,X,e,Y],[f,Z,d,c,b,a])
% %ANSWER%
% X=d
% Y=f
% Z=e
% %ANSWER%
% %NO%
% %QUERY% reverse([a,b,c,d,e,f],X)
% %ANSWER% X=[f,e,d,c,b,a]
% %NO%
% %QUERY% reverse([a,b,c,[1,2,3]],X)
% %ANSWER% X=[[1,2,3],c,b,a]
% %NO%

% Determine if elements are next to each other in a list.
adjacent(X,Y,Zs) :- append(As,[X,Y|Ys],Zs).

% %TRUE_NO% adjacent(a,b,[a,b,c,d,e,f])
% %TRUE_NO% adjacent(c,d,[a,b,c,d,e,f])
% %TRUE_NO% adjacent(e,f,[a,b,c,d,e,f])
% %FALSE% adjacent(a,c,[a,b,c,d,e,f])
% %FALSE% adjacent(b,z,[a,b,c,d,e,f])
% %QUERY% adjacent(b,X,[a,b,c,d,e,f])
% %ANSWER% X=c
% %NO%
% %QUERY% adjacent(X,e,[a,b,c,d,e,f])
% %ANSWER% X=d
% %NO%
% %QUERY% adjacent(X,Y,[a,b,c,d,e,f])
% %ANSWER% 
% X=a
% Y=b
% %ANSWER%
% %ANSWER% 
% X=b
% Y=c
% %ANSWER%
% %ANSWER% 
% X=c
% Y=d
% %ANSWER%
% %ANSWER% 
% X=d
% Y=e
% %ANSWER%
% %ANSWER% 
% X=e
% Y=f
% %ANSWER%
% %NO%

% Find the last element of a list.
last(X,Xs) :- append(As,[X],Xs).

% %TRUE_NO% last(f,[a,b,c,d,e,f])
% %FALSE% last(a,[a,b,c,d,e,f])
% %FALSE% last(b,[a,b,c,d,e,f])
% %FALSE% last(c,[a,b,c,d,e,f])
% %FALSE% last(d,[a,b,c,d,e,f])
% %FALSE% last(e,[a,b,c,d,e,f])
% %TRUE_NO% last(f,[a,b,c,d,e,f])
% %FALSE% last(z,[a,b,c,d,e,f])
% %QUERY% last(X,[a,b,c,d,e,f])
% %ANSWER% X=f
% %NO%
% %QUERY% last(f,[a,b,c,d,e,X])
% %ANSWER% X=f
% %NO%

% Get a count of the number of terms contained in a list.
% (Note: Projog provides a built-in length(X,Y) predicate.)

list_length([],0).
list_length([X|Xs],A) :- list_length(Xs,B), A is B+1.

% %QUERY% list_length([],X)
% %ANSWER% X=0
% %NO%
% %QUERY% list_length([a],X)
% %ANSWER% X=1
% %NO%
% %QUERY% list_length([a,b],X)
% %ANSWER% X=2
% %NO%
% %QUERY% list_length([a,b,c,d,e,f],X)
% %ANSWER% X=6
% %NO%
% %TRUE_NO% list_length([a,b,c,d,e,f],6)
% %FALSE% list_length([a,b,c,d,e,f],5)

% Delete elements from a list.

delete([X|Xs],X,Ys) :- delete(Xs,X,Ys).
delete([X|Xs],Z,[X|Ys]) :- \+ X==Z, delete(Xs, Z, Ys).
delete([],X,[]).

% %TRUE_NO% delete([a,z,c],z,[a,c])
% %QUERY% delete([a,z,c],y,X)
% %ANSWER% X=[a,z,c]
% %NO%
% %TRUE_NO% delete([z,a,z,z,b,c,z,d,e,f,z],z,[a,b,c,d,e,f])
% %QUERY% delete([z,a,z,z,b,c,z,d,e,f,z],X,[a,b,c,d,e,f])
% %ANSWER% X=z
% %NO%

% Select elements from a list.

select(X,[X|Xs],Xs).
select(X,[Y|Ys],[Y|Zs]) :- select(X,Ys,Zs).

% %TRUE_NO% select(b,[a,b,c],[a,c])
% %FALSE% select(z,[a,b,c],[a,b,c])
% %QUERY% select(z,[z,a,z,z,b,c,z,d,e,f,z],X)
% %ANSWER% X=[a,z,z,b,c,z,d,e,f,z]
% %ANSWER% X=[z,a,z,b,c,z,d,e,f,z]
% %ANSWER% X=[z,a,z,b,c,z,d,e,f,z]
% %ANSWER% X=[z,a,z,z,b,c,d,e,f,z]
% %ANSWER% X=[z,a,z,z,b,c,z,d,e,f]
% %NO%

% Checks terms in list are ordered.

ordered([]).
ordered([X]).
ordered([X,Y|Ys]) :- X @=< Y, ordered([Y|Ys]).

% %TRUE_NO% ordered([a,b,c,d,e,f])
% %FALSE% ordered([a,b,c,e,d,f])

% Find permuatations of terms in a list.

permutation(Xs,[Z|Zs]) :- select(Z,Xs,Ys), permutation(Ys,Zs).
permutation([],[]).

% %QUERY% permutation([a,b],X)
% %ANSWER% X=[a,b]
% %ANSWER% X=[b,a]
% %NO%

% %QUERY% permutation([a,b,c],X)
% %ANSWER% X=[a,b,c]
% %ANSWER% X=[a,c,b]
% %ANSWER% X=[b,a,c]
% %ANSWER% X=[b,c,a]
% %ANSWER% X=[c,a,b]
% %ANSWER% X=[c,b,a]
% %NO%

% %TRUE_NO% permutation([a,b,c,d,e,f],[f,e,d,c,b,a])

% %QUERY% permutation([q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m],[a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,Z])
% %ANSWER% Z=z
% %NO%
