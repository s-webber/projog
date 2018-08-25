% Atoms unify if and only if they are the same atom.
%FALSE atom1=atom2
%TRUE atom1=atom1

% Numbers unify if and only if they are the same number.
%FALSE 1=2
%TRUE 1=1

% If variable V is instantiated and term T is not a variable 
% then V and T unify if and only if
% the term instantiated on V unifies with T.
%FALSE X=atom1, X=atom2
%QUERY X=atom1, X=atom1
%ANSWER X=atom1

% If variable V is instantiated and term T is an instantiated variable 
% then V and T unify if and only if 
% the term instantiated on V unifies with the term instantiated on T.
%FALSE X=atom1, Y=atom2, X=Y
%QUERY X=atom1, Y=atom1, X=Y
%ANSWER
% X=atom1
% Y=atom1
%ANSWER

% If variable V is instantiated and term T is an uninstantiated variable
% then V and T unifed by instantiating on T the term instantiated on V.
%QUERY X=atom1, X=Y
%ANSWER
% X=atom1
% Y=atom1
%ANSWER

% Structures unify if, and only if,
% a) their names unify
% b) they have the same number of arguments
% and c) their arguments unify.
%FALSE structure1(atom1,atom2,atom3)=structure2(atom1,atom2,atom3)
%FALSE structure1(atom1,atom2,atom3)=structure1(atom1,atom2)
%FALSE structure1(atom1,atom2,atom3)=structure1(atom1,atom2,atom4)
%TRUE structure1(atom1,atom2,atom3)=structure1(atom1,atom2,atom3)
%QUERY structure1(atom1,X,atom3)=structure1(atom1,atom2,Y)
%ANSWER
% X=atom2
% Y=atom3
%ANSWER

% Lists unify if and only if both their heads and their tails unify.
%FALSE [atom,atom2,atom3]=[atom1,atom2]
%FALSE [atom1,atom2,atom3]=[atom1,atom2,atom4]
%FALSE [atom1,atom2]=[atom2,X]

%QUERY [atom1,atom2,atom3]=[X,Y,Z]
%ANSWER
% X=atom1
% Y=atom2
% Z=atom3
%ANSWER

%QUERY [atom1]=[X|Y]
%ANSWER
% X=atom1
% Y=[]
%ANSWER

%QUERY [atom1,atom2,atom3]=[X,Y|Z]
%ANSWER
% X=atom1
% Y=atom2
% Z=[atom3]
%ANSWER

%QUERY [X|Y]=[atom1,atom2,atom3]
%ANSWER
% X=atom1
% Y=[atom2,atom3]
%ANSWER

%QUERY [atom1,atom2]=[atom1|X]
%ANSWER X=[atom2]

%QUERY [atom1|Y]=[X|atom2]
%ANSWER
% X=atom1
% Y=atom2
%ANSWER

%QUERY [[atom1,Y]|Z]=[[X,atom2],[atom3,atom4]]
%ANSWER
% X=atom1
% Y=atom2
% Z=[[atom3,atom4]]
%ANSWER

%QUERY A=atom1, B=atom2, C=atom3, D=[A,B,C]
%ANSWER
% A=atom1
% B=atom2
% C=atom3
% D=[atom1,atom2,atom3]
%ANSWER
