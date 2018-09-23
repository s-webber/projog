%QUERY X=a:b
%ANSWER X= a : b

%QUERY X = a : b
%ANSWER X=a : b

%QUERY X=q:w:e:r:t:y
%ANSWER X=q : w : e : r : t : y

%QUERY X=q:w:e/r:t:y/u:i:o
%ANSWER X=q : w : e / r : t : y / u : i : o

%QUERY write(q:w:e/r:t:y/u:i:o)
%OUTPUT q : w : e / r : t : y / u : i : o
%ANSWER/
