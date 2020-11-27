%FALSE p(X,Y,Z)

%TRUE assert(p(a,b,c))

%QUERY p(X,Y,Z)
%ANSWER
% X=a
% Y=b
% Z=c
%ANSWER

%TRUE p(a,b,c)
%FALSE p(z,b,c)
%FALSE p(a,z,c)
%FALSE p(a,b,z)

%TRUE assert(p(1,2,3))
%TRUE assert(p(x,y,z))

%QUERY p(X,Y,Z)
%ANSWER
% X=a
% Y=b
% Z=c
%ANSWER
%ANSWER
% X=1
% Y=2
% Z=3
%ANSWER
%ANSWER
% X=x
% Y=y
% Z=z
%ANSWER

%TRUE p(a,b,c)
%TRUE p(1,2,3)
%TRUE p(a,b,c)
%FALSE p(a,2,3)
%FALSE p(a,y,z)
%FALSE p(1,b,c)
%FALSE p(1,y,z)
%FALSE p(x,b,c)
%FALSE p(x,2,3)
%FALSE p(a,X,3)
%FALSE p(a,y,X)
%FALSE p(1,X,c)
%FALSE p(1,y,X)
%FALSE p(x,X,c)
%FALSE p(x,2,X)

%QUERY p(a,X,Y)
%ANSWER
% X=b
% Y=c
%ANSWER

%QUERY p(1,X,Y)
%ANSWER
% X=2
% Y=3
%ANSWER

%QUERY p(x,X,Y)
%ANSWER
% X=y
% Y=z
%ANSWER

%QUERY p(X,b,c)
%ANSWER X=a
%NO

%QUERY p(X,2,3)
%ANSWER X=1
%NO

%QUERY p(X,y,z)
%ANSWER X=x

%QUERY assert(p(q,X,Y))
%ANSWER
% X=UNINSTANTIATED VARIABLE
% Y=UNINSTANTIATED VARIABLE
%ANSWER

%TRUE p(a,b,c)
%TRUE p(1,2,3)
%TRUE p(x,y,z)
%TRUE p(q,w,e)
%TRUE p(q,2,3)

%QUERY assert(p(w,X,X))
%ANSWER X=UNINSTANTIATED VARIABLE

%TRUE p(a,b,c)
%TRUE p(1,2,3)
%TRUE p(x,y,z)
%TRUE p(q,w,e)
%TRUE p(q,2,3)
%TRUE p(w,2,2)
%FALSE p(w,2,3)

%QUERY p(w,7,X)
%ANSWER X=7

%QUERY p(w,X,9)
%ANSWER X=9

%QUERY assert(p(X,y,u))
%ANSWER X=UNINSTANTIATED VARIABLE

%TRUE_NO p(a,b,c)
%TRUE_NO p(1,2,3)
%TRUE_NO p(x,y,z)
%TRUE_NO p(q,w,e)
%TRUE_NO p(q,2,3)
%TRUE_NO p(q,e,e)
%TRUE_NO p(w,1,1)
%TRUE p(z,y,u)

%QUERY retract(p(X,y,u))
%ANSWER X=q
%ANSWER X=UNINSTANTIATED VARIABLE

%TRUE_NO p(a,b,c)
%TRUE_NO p(1,2,3)
%TRUE_NO p(x,y,z)
%TRUE p(w,1,1)
%FALSE p(q,w,e)
%FALSE p(q,e,e)
%FALSE p(X,y,u)

%TRUE retract(p(w,1,1))
%TRUE retract(p(x,y,z))
%TRUE retract(p(1,2,3))

%TRUE p(a,b,c)

%TRUE assert(p(d,f,g))

%TRUE_NO p(a,b,c)

%TRUE retract(p(d,f,g))
%TRUE retract(p(a,b,c))

%FALSE p(X,Y,Z)

%TRUE assert(p(t,y,u))
%TRUE assert(p(h,j,k))

%TRUE p(t,y,u)
%TRUE p(h,j,k)

