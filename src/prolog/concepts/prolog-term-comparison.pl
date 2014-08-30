% Examples of comparing a uninstantiated variable to a whole number.

%QUERY X @< 2
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE 2 @< X

%QUERY X @=< 2
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE 2 @=< X

%FALSE X @> 2

%QUERY 2 @> X
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE X @>= 2

%QUERY 2 @>= X
%ANSWER X=UNINSTANTIATED VARIABLE

% Examples of comparing a uninstantiated variable to an atom.

%QUERY X @< atom
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE atom @< X

%QUERY X @=< atom
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE atom @=< X

%FALSE X @> atom

%QUERY atom @> X
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE X @>= atom

%QUERY atom @>= X
%ANSWER X=UNINSTANTIATED VARIABLE

% Examples of comparing a uninstantiated variable to a structure.

%QUERY X @< structure(a,b,c)
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE structure(a,b,c) @< X

%QUERY X @=< structure(a,b,c)
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE structure(a,b,c) @=< X

%FALSE X @> structure(a,b,c)

%QUERY structure(a,b,c) @> X
%ANSWER X=UNINSTANTIATED VARIABLE

%FALSE X @>= structure(a,b,c)

%QUERY structure(a,b,c) @>= X
%ANSWER X=UNINSTANTIATED VARIABLE

