full_adder(Input1,Input2,Input3,Output1,Output2) :-
  Output1 #<==> (Input1 #\ Input2) #\ Input3,
  Output2 #<==> (Input1 #/\ Input2) #\/ (Input3 #/\ (Input1 #\ Input2)).

%?- full_adder(1,1,1,Output1,Output2)
% Output1 = 1
% Output2 = 1

%?- full_adder(1,1,0,Output1,Output2)
% Output1 = 0
% Output2 = 1

%?- full_adder(1,0,1,Output1,Output2)
% Output1 = 0
% Output2 = 1

%?- full_adder(0,1,1,Output1,Output2)
% Output1 = 0
% Output2 = 1

%?- full_adder(1,0,0,Output1,Output2)
% Output1 = 1
% Output2 = 0

%?- full_adder(0,1,0,Output1,Output2)
% Output1 = 1
% Output2 = 0

%?- full_adder(0,0,1,Output1,Output2)
% Output1 = 1
% Output2 = 0

%?- full_adder(0,0,0,Output1,Output2)
% Output1 = 0
% Output2 = 0

%?- full_adder(0,Input2,Input3,Output1,1)
% Input2 = 1
% Input3 = 1
% Output1 = 0

%?- full_adder(Input1,0,Input3,Output1,1)
% Input1 = 1
% Input3 = 1
% Output1 = 0

%?- full_adder(Input1,Input2,0,Output1,1)
% Input1 = 1
% Input2 = 1
% Output1 = 0
