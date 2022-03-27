% A+B+C=17
% * + +
% D+E+F=77
% + + +
% G+H+I=10
% = = =
% 1 1 1
% 1 9 6
set_square(A,B,C,D,E,F,G,H,I) :-
  Vars = [A,B,C,D,E,F,G,H,I],
  Vars ins 1..9,
  all_different(Vars),
  17 #= A+B+C,
  77 #= (D+E)*F,
  10 #= G+H+I,
  11 #= (A*D)+G,
  19 #= B+E+H,
  16 #= C+F+I,
  label(Vars).

%?- set_square(A,B,C,D,E,F,G,H,I)
% A = 3
% B = 6
% C = 8
% D = 2
% E = 9
% F = 7
% G = 5
% H = 4
% I = 1
%NO
