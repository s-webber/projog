%?- X '=' a
%X=a

%?- write_canonical(a'='b)
%OUTPUT =(a, b)
%YES

%?- X = @, Y = '@', X=Y
%X=@
%Y=@

%?- X='(', true
%X=(

%?- X=(, true
%ERROR No matching ) for ( Line: X=(, true.

%?- X=p()
%ERROR No arguments to parse Line: X=p().

%?- X=p(a, b c)
%ERROR No suitable operands Line: X=p(a, b c).

%?- X=[a,b
%ERROR No matching ] for [ Line: X=[a,b.

%?- X=[a,b c]
%ERROR No suitable operands Line: X=[a,b c].

%?- X=[a,b|c,d]
%ERROR Expected ] Line: X=[a,b|c,d].

%?- X=[a,b|c|d]
%ERROR Expected ] Line: X=[a,b|c|d].

%?- X=(a b)
%ERROR No suitable operands Line: X=(a b).

%?- X='.'
%X=.

%?- X : 2 = 1 : 2
%X=1

%?- 1 : X = 1 : 2
%X=2

%?- 1 : 2 = X : 2
%X=1

%?- 1 : 2 = 1 : X
%X=2

%?- X=p(','(1,2)), write_canonical(X)
%OUTPUT p(,(1, 2))
%X=p(1 , 2)

%?- X=p(1,','(1+2)), write_canonical(X)
%OUTPUT p(1, ,(+(1, 2)))
%X=p(1, ,(1 + 2))

%?- X=p(1,(2+3)), write_canonical(X)
%OUTPUT p(1, +(2, 3))
%X=p(1, 2 + 3)

%?- X=p(1,','(2,3)), write_canonical(X)
%OUTPUT p(1, ,(2, 3))
%X=p(1, 2 , 3)

%?- X=p(1,','(2+3)), write_canonical(X)
%OUTPUT p(1, ,(+(2, 3)))
%X=p(1, ,(2 + 3))

%?- X=p(1,2','(3+4),5), write_canonical(X)
%OUTPUT p(1, 2, +(3, 4), 5)
%X=p(1, 2, 3 + 4, 5)
