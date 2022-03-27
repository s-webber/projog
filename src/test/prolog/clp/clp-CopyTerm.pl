%?- X#=5, copy_term(X,Y)
% X=5
% Y=5

%?- X#=5, copy_term(f(X),Y)
% X=5
% Y=f(5)

%?- X#>5, copy_term(X,Y)
%ERROR CLP_VARIABLE does not support copy, so is not suitable for use in this scenario

%?- X#>5, copy_term(f(X),Y)
%ERROR CLP_VARIABLE does not support copy, so is not suitable for use in this scenario
