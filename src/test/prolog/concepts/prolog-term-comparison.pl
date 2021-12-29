% Examples of comparing a uninstantiated variable to a whole number.

%?- X @< 2
% X=UNINSTANTIATED VARIABLE

%FAIL 2 @< X

%?- X @=< 2
% X=UNINSTANTIATED VARIABLE

%FAIL 2 @=< X

%FAIL X @> 2

%?- 2 @> X
% X=UNINSTANTIATED VARIABLE

%FAIL X @>= 2

%?- 2 @>= X
% X=UNINSTANTIATED VARIABLE

% Examples of comparing a uninstantiated variable to an atom.

%?- X @< atom
% X=UNINSTANTIATED VARIABLE

%FAIL atom @< X

%?- X @=< atom
% X=UNINSTANTIATED VARIABLE

%FAIL atom @=< X

%FAIL X @> atom

%?- atom @> X
% X=UNINSTANTIATED VARIABLE

%FAIL X @>= atom

%?- atom @>= X
% X=UNINSTANTIATED VARIABLE

% Examples of comparing a uninstantiated variable to a structure.

%?- X @< structure(a,b,c)
% X=UNINSTANTIATED VARIABLE

%FAIL structure(a,b,c) @< X

%?- X @=< structure(a,b,c)
% X=UNINSTANTIATED VARIABLE

%FAIL structure(a,b,c) @=< X

%FAIL X @> structure(a,b,c)

%?- structure(a,b,c) @> X
% X=UNINSTANTIATED VARIABLE

%FAIL X @>= structure(a,b,c)

%?- structure(a,b,c) @>= X
% X=UNINSTANTIATED VARIABLE

