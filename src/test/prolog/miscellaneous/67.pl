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
%ERROR Unexpected end of stream Line: X=(, true.

%?- X=p()
%ERROR No arguments to parse Line: X=p().

%?- X=p(a, b c)
%ERROR No suitable operands found in: b c Line: X=p(a, b c).

%?- X=[a,b
%ERROR Unexpected end of stream Line: X=[a,b.

%?- X=[a,b c]
%ERROR No suitable operands found in: b c Line: X=[a,b c].

%?- X=[a,b|c,d]
%ERROR Operator priority clash. , (1000) conflicts with previous priority (1000) Line: X=[a,b|c,d].

%?- X=[a,b|c|d]
%ERROR No suitable operands found in: c | d Line: X=[a,b|c|d].

%?- X=(a b)
%ERROR No suitable operands found in: a b Line: X=(a b).

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
