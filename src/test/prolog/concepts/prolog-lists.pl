% Determining if a term is a list.
% (Note: Projog provides a built-in is_list(X) predicate.)

list([]).
list([X|Xs]) :- list(Xs).

%TRUE list([a,b,c])
%FALSE list(abc)

% Determining is a term is contained in a list.
% (Note: Projog provides built-in member(X,Y) and memberchk(X,Y) predicates.)

list_member(X,[X|Xs]).
list_member(X,[Y|Ys]) :- list_member(X,Ys).

%QUERY list_member(X, [a,b,c])
%ANSWER X=a
%ANSWER X=b
%ANSWER X=c
%NO

%TRUE_NO list_member(a, [a,b,c])
%TRUE_NO list_member(b, [a,b,c])
%TRUE_NO list_member(c, [a,b,c])
%FALSE list_member(d, [a,b,c])
%QUERY list_member(a,[a,a,a])
%ANSWER/
%ANSWER/
%ANSWER/
%NO

% Finding prefixes of a list.

prefix([],Ys).
prefix([X|Xs],[X|Ys]) :- prefix(Xs,Ys).

%QUERY prefix(X, [a,b,c,d,e,f])
%ANSWER X=[]
%ANSWER X=[a]
%ANSWER X=[a,b]
%ANSWER X=[a,b,c]
%ANSWER X=[a,b,c,d]
%ANSWER X=[a,b,c,d,e]
%ANSWER X=[a,b,c,d,e,f]
%NO

%TRUE prefix([a],[a,b,c])
%TRUE prefix([a,b],[a,b,c])
%TRUE prefix([a,b,c],[a,b,c])
%FALSE prefix([x,a,b,c],[a,b,c])
%FALSE prefix([a,c],[a,b,c])
%FALSE prefix([b,c],[a,b,c])
%QUERY prefix([X,b],[a,b,c])
%ANSWER X=a
%NO

% Finding suffixes of a list.

suffix(Xs,Xs).
suffix(Xs,[Y|Ys]) :- suffix(Xs,Ys).

%QUERY suffix(X, [a,b,c,d,e,f])
%ANSWER X=[a,b,c,d,e,f]
%ANSWER X=[b,c,d,e,f]
%ANSWER X=[c,d,e,f]
%ANSWER X=[d,e,f]
%ANSWER X=[e,f]
%ANSWER X=[f]
%ANSWER X=[]
%NO

%TRUE_NO suffix([c],[a,b,c])
%TRUE_NO suffix([b,c],[a,b,c])
%TRUE_NO suffix([a,b,c],[a,b,c])
%FALSE suffix([x,a,b,c],[a,b,c])
%FALSE suffix([a,c],[a,b,c])
%FALSE suffix([a,b],[a,b,c])
%QUERY suffix([X,c],[a,b,c])
%ANSWER X=b
%NO

% Determining sublists of lists.

sublist(Xs,Ys) :- prefix(Xs,Ys).
sublist(Xs,[Y|Ys]) :- sublist(Xs,Ys).

%TRUE_NO sublist([a,b,c],[a,b,c,d,e,f])
%TRUE_NO sublist([b,c,d],[a,b,c,d,e,f])
%TRUE_NO sublist([c,d,e],[a,b,c,d,e,f])
%FALSE sublist([b,c,e],[a,b,c,d,e,f])
%QUERY sublist([b,X,d],[a,b,c,Y,e,f])
%ANSWER 
% X=c
% Y=d
%ANSWER
%NO

% Appending two lists.
% (Note: Projog provides a built-in append(X,Y,Z) predicate.)

append_to_list([],Ys,Ys).
append_to_list([X|Xs],Ys,[X|Zs]) :- append_to_list(Xs,Ys,Zs).

%TRUE append_to_list([a,b,c],[d,e,f],[a,b,c,d,e,f])
%FALSE append_to_list([a,b,c],[d,e,f],[a,b,c,d,e,g])
%FALSE append_to_list([a,b,c],[d,e,f],[a,b,c,d,e,f,g])
%QUERY append_to_list([a,X,c],[d,e,Y],[a,b,c,Z,e,f])
%ANSWER
% X=b
% Y=f
% Z=d
%ANSWER
%NO
%QUERY append_to_list([a,X,c],[Y,e(X,Y),f],[a,b,c,d,Z,f])
%ANSWER
% X=b
% Y=d
% Z=e(b, d)
%ANSWER
%NO
%QUERY append_to_list([a,b,c],[d,e,f],X)
%ANSWER X=[a,b,c,d,e,f]
%QUERY append_to_list([a,b,c],X,[a,b,c,d,e,f])
%ANSWER X=[d,e,f]
%QUERY append_to_list(X,[d,e,f],[a,b,c,d,e,f])
%ANSWER X=[a,b,c]
%NO

% Reversing the order of terms in a list.
% (Note: Projog provides a built-in reverse(X,Y) predicate.)

reverse_list(Xs,Ys) :- reverse_list(Xs,[],Ys).
reverse_list([X|Xs],Acc,Ys) :- reverse_list(Xs,[X|Acc],Ys).
reverse_list([],Ys,Ys).

%TRUE reverse_list([],[])
%TRUE_NO reverse_list([a],[a])
%FALSE reverse_list([a],[b])
%TRUE_NO reverse_list([a,b],[b,a])
%FALSE reverse_list([a,b],[a,b])
%FALSE reverse_list([a,b],[a,a])
%FALSE reverse_list([a,b],[b,b])
%FALSE reverse_list([a,b],[a])
%FALSE reverse_list([a,b],[b])
%FALSE reverse_list([a,b],[c,b,a])
%TRUE_NO reverse_list([a,b,c,d,e,f],[f,e,d,c,b,a])
%FALSE reverse_list([a,b,c,d,e,f],[f,e,d,c,a,b])
%QUERY reverse_list([a,b,c,X,e,Y],[f,Z,d,c,b,a])
%ANSWER
% X=d
% Y=f
% Z=e
%ANSWER
%NO
%QUERY reverse_list([a,b,c,d,e,f],X)
%ANSWER X=[f,e,d,c,b,a]
%NO
%QUERY reverse_list([a,b,c,[1,2,3]],X)
%ANSWER X=[[1,2,3],c,b,a]
%NO

% Determine if elements are next to each other in a list.
adjacent(X,Y,Zs) :- append_to_list(As,[X,Y|Ys],Zs).

%TRUE_NO adjacent(a,b,[a,b,c,d,e,f])
%TRUE_NO adjacent(c,d,[a,b,c,d,e,f])
%TRUE_NO adjacent(e,f,[a,b,c,d,e,f])
%FALSE adjacent(a,c,[a,b,c,d,e,f])
%FALSE adjacent(b,z,[a,b,c,d,e,f])
%QUERY adjacent(b,X,[a,b,c,d,e,f])
%ANSWER X=c
%NO
%QUERY adjacent(X,e,[a,b,c,d,e,f])
%ANSWER X=d
%NO
%QUERY adjacent(X,Y,[a,b,c,d,e,f])
%ANSWER 
% X=a
% Y=b
%ANSWER
%ANSWER 
% X=b
% Y=c
%ANSWER
%ANSWER 
% X=c
% Y=d
%ANSWER
%ANSWER 
% X=d
% Y=e
%ANSWER
%ANSWER 
% X=e
% Y=f
%ANSWER
%NO

% Find the last element of a list.
last(X,Xs) :- append_to_list(As,[X],Xs).

%TRUE_NO last(f,[a,b,c,d,e,f])
%FALSE last(a,[a,b,c,d,e,f])
%FALSE last(b,[a,b,c,d,e,f])
%FALSE last(c,[a,b,c,d,e,f])
%FALSE last(d,[a,b,c,d,e,f])
%FALSE last(e,[a,b,c,d,e,f])
%TRUE_NO last(f,[a,b,c,d,e,f])
%FALSE last(z,[a,b,c,d,e,f])
%QUERY last(X,[a,b,c,d,e,f])
%ANSWER X=f
%NO
%QUERY last(f,[a,b,c,d,e,X])
%ANSWER X=f
%NO

% Get a count of the number of terms contained in a list.
% (Note: Projog provides a built-in length(X,Y) predicate.)

list_length([],0).
list_length([X|Xs],A) :- list_length(Xs,B), A is B+1.

%QUERY list_length([],X)
%ANSWER X=0
%NO
%QUERY list_length([a],X)
%ANSWER X=1
%NO
%QUERY list_length([a,b],X)
%ANSWER X=2
%NO
%QUERY list_length([a,b,c,d,e,f],X)
%ANSWER X=6
%NO
%TRUE_NO list_length([a,b,c,d,e,f],6)
%FALSE list_length([a,b,c,d,e,f],5)

% Delete elements from a list.
% (Note: Projog provides a built-in delete(X,Y,Z) predicate.)

delete_from_list([X|Xs],X,Ys) :- delete_from_list(Xs,X,Ys).
delete_from_list([X|Xs],Z,[X|Ys]) :- \+ X==Z, delete_from_list(Xs, Z, Ys).
delete_from_list([],X,[]).

%TRUE_NO delete_from_list([a,z,c],z,[a,c])
%QUERY delete_from_list([a,z,c],y,X)
%ANSWER X=[a,z,c]
%NO
%TRUE_NO delete_from_list([z,a,z,z,b,c,z,d,e,f,z],z,[a,b,c,d,e,f])
%QUERY delete_from_list([z,a,z,z,b,c,z,d,e,f,z],X,[a,b,c,d,e,f])
%ANSWER X=z
%NO

% Select elements from a list.
% (Note: Projog provides a built-in select(X,Y,Z) predicate.)

select_from_list(X,[X|Xs],Xs).
select_from_list(X,[Y|Ys],[Y|Zs]) :- select_from_list(X,Ys,Zs).

%TRUE_NO select_from_list(b,[a,b,c],[a,c])
%FALSE select_from_list(z,[a,b,c],[a,b,c])
%QUERY select_from_list(z,[z,a,z,z,b,c,z,d,e,f,z],X)
%ANSWER X=[a,z,z,b,c,z,d,e,f,z]
%ANSWER X=[z,a,z,b,c,z,d,e,f,z]
%ANSWER X=[z,a,z,b,c,z,d,e,f,z]
%ANSWER X=[z,a,z,z,b,c,d,e,f,z]
%ANSWER X=[z,a,z,z,b,c,z,d,e,f]
%NO

% Checks terms in list are ordered.

ordered([]).
ordered([X]).
ordered([X,Y|Ys]) :- X @=< Y, ordered([Y|Ys]).

%TRUE_NO ordered([a,b,c,d,e,f])
%FALSE ordered([a,b,c,e,d,f])

% Find permuatations of terms in a list.

permutation(Xs,[Z|Zs]) :- select_from_list(Z,Xs,Ys), permutation(Ys,Zs).
permutation([],[]).

%QUERY permutation([a,b],X)
%ANSWER X=[a,b]
%ANSWER X=[b,a]
%NO

%QUERY permutation([a,b,c],X)
%ANSWER X=[a,b,c]
%ANSWER X=[a,c,b]
%ANSWER X=[b,a,c]
%ANSWER X=[b,c,a]
%ANSWER X=[c,a,b]
%ANSWER X=[c,b,a]
%NO

%TRUE_NO permutation([a,b,c,d,e,f],[f,e,d,c,b,a])

%QUERY permutation([q,w,e,r,t,y,u,i,o,p,a,s,d,f,g,h,j,k,l,z,x,c,v,b,n,m],[a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,Z])
%ANSWER Z=z
%NO
