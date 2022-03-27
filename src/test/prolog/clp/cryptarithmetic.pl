donald_gerald_robert([D,O,N,A,L,D],[G,E,R,A,L,D],[R,O,B,E,R,T]) :-
  Vars = [D,O,N,A,L,G,E,R,B,T],
  Vars ins 0..9,
  all_different(Vars),
  100000*D + 10000*O + 1000*N + 100*A + 10*L + D +
  100000*G + 10000*E + 1000*R + 100*A + 10*L + D #=
  100000*R + 10000*O + 1000*B + 100*E + 10*R + T,
  label(Vars).

%?- donald_gerald_robert(X,Y,Z)
% X=[5,2,6,4,8,5]
% Y=[1,9,7,4,8,5]
% Z=[7,2,3,9,7,0]
%NO

send_more_money(Vars) :-
  Vars = [S,E,N,D,M,O,R,Y],
  Vars ins 0..9,
  S #\= 0,
  M #\= 0,
  all_different(Vars),
  1000*S + 100*E + 10*N + D +
  1000*M + 100*O + 10*R + E #=
  10000*M + 1000*O + 100*N + 10*E + Y,
  label(Vars).

%?- send_more_money(X)
% X=[9,5,6,7,1,0,8,2]
%NO
