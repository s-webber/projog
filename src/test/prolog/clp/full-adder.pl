% https://en.wikipedia.org/wiki/Adder_(electronics)#Full_adder
full_adder(I1,I2,I3,O1,O2) :-
  O1 #<==> (I1 #\ I2) #\ I3,
  O2 #<==> (I1 #/\ I2) #\/ (I3 #/\ (I1 #\ I2)).

%?- full_adder(1,1,1,O1,O2)
% O1 = 1
% O2 = 1

%?- full_adder(1,1,0,O1,O2)
% O1 = 0
% O2 = 1

%?- full_adder(1,0,1,O1,O2)
% O1 = 0
% O2 = 1

%?- full_adder(0,1,1,O1,O2)
% O1 = 0
% O2 = 1

%?- full_adder(1,0,0,O1,O2)
% O1 = 1
% O2 = 0

%?- full_adder(0,1,0,O1,O2)
% O1 = 1
% O2 = 0

%?- full_adder(0,0,1,O1,O2)
% O1 = 1
% O2 = 0

%?- full_adder(0,0,0,O1,O2)
% O1 = 0
% O2 = 0

%?- full_adder(0,I2,I3,O1,1)
% I2 = 1
% I3 = 1
% O1 = 0

%?- full_adder(I1,0,I3,O1,1)
% I1 = 1
% I3 = 1
% O1 = 0

%?- full_adder(I1,I2,0,O1,1)
% I1 = 1
% I2 = 1
% O1 = 0
