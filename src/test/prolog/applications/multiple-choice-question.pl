solve(X) :-
  X = [A1,A2,A3,A4,A5,A6],
  X ins 0..1,
  A1 #<==> 5 #= A2+A3+A4+A5+A6,    % All of the below
  A2 #<==> 0 #= A3+A4+A5+A6,       % None of the below
  A3 #<==> 2 #= A1+A2,             % All of the above
  A4 #<==> 1 #= A1+A2+A3,          % One of the above
  A5 #<==> 0 #= A1+A2+A3+A4,       % None of the above
  A6 #<==> 0 #= A1+A2+A3+A4+A5,    % None of the above
  label(X).

%?- solve(X)
% X = [0,0,0,0,1,0]
%NO
