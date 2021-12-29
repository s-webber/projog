% Atoms unify if and only if they are the same atom.
%FAIL atom1=atom2
%TRUE atom1=atom1

% Numbers unify if and only if they are the same number.
%FAIL 1=2
%TRUE 1=1

% If variable V is instantiated and term T is not a variable 
% then V and T unify if and only if
% the term instantiated on V unifies with T.
%FAIL X=atom1, X=atom2
%?- X=atom1, X=atom1
% X=atom1

% If variable V is instantiated and term T is an instantiated variable 
% then V and T unify if and only if 
% the term instantiated on V unifies with the term instantiated on T.
%FAIL X=atom1, Y=atom2, X=Y
%?- X=atom1, Y=atom1, X=Y
% X=atom1
% Y=atom1

% If variable V is instantiated and term T is an uninstantiated variable
% then V and T unifed by instantiating on T the term instantiated on V.
%?- X=atom1, X=Y
% X=atom1
% Y=atom1

% Structures unify if, and only if,
% a) their names unify
% b) they have the same number of arguments
% and c) their arguments unify.
%FAIL structure1(atom1,atom2,atom3)=structure2(atom1,atom2,atom3)
%FAIL structure1(atom1,atom2,atom3)=structure1(atom1,atom2)
%FAIL structure1(atom1,atom2,atom3)=structure1(atom1,atom2,atom4)
%TRUE structure1(atom1,atom2,atom3)=structure1(atom1,atom2,atom3)
%?- structure1(atom1,X,atom3)=structure1(atom1,atom2,Y)
% X=atom2
% Y=atom3

% Lists unify if and only if both their heads and their tails unify.
%FAIL [atom,atom2,atom3]=[atom1,atom2]
%FAIL [atom1,atom2,atom3]=[atom1,atom2,atom4]
%FAIL [atom1,atom2]=[atom2,X]

%?- [atom1,atom2,atom3]=[X,Y,Z]
% X=atom1
% Y=atom2
% Z=atom3

%?- [atom1]=[X|Y]
% X=atom1
% Y=[]

%?- [atom1,atom2,atom3]=[X,Y|Z]
% X=atom1
% Y=atom2
% Z=[atom3]

%?- [X|Y]=[atom1,atom2,atom3]
% X=atom1
% Y=[atom2,atom3]

%?- [atom1,atom2]=[atom1|X]
% X=[atom2]

%?- [atom1|Y]=[X|atom2]
% X=atom1
% Y=atom2

%?- [[atom1,Y]|Z]=[[X,atom2],[atom3,atom4]]
% X=atom1
% Y=atom2
% Z=[[atom3,atom4]]

%?- A=atom1, B=atom2, C=atom3, D=[A,B,C]
% A=atom1
% B=atom2
% C=atom3
% D=[atom1,atom2,atom3]
