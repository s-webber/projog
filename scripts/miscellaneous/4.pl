% Test that same variable names are used in different sentences
% different Variable instances are created
%QUERY hgjhghj(X)
%ANSWER X=agh
%ANSWER X=aghsew
etrert(agh).
etrert(aghsew).
rtyrty(X) :- etrert(X).
hgjhghj(X) :- rtyrty(X).
