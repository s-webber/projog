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
%ERROR Expected ) but got: true after , Line: X=(, true.

%?- X=p()
%ERROR No arguments specified for structure: p Line: X=p().

%?- X=p(a, b c)
%ERROR While parsing arguments of p expected ) or , but got: c Line: X=p(a, b c).

%?- X=[a,b
%ERROR While parsing list expected ] | or , but got: . Line: X=[a,b.

%?- X=[a,b c]
%ERROR While parsing list expected ] | or , but got: c Line: X=[a,b c].

%?- X=[a,b|c,d]
%ERROR Expected ] to mark end of list after tail but got: , Line: X=[a,b|c,d].

%?- X=[a,b|c|d]
%ERROR Expected ] to mark end of list after tail but got: | Line: X=[a,b|c|d].

%?- X=(a b)
%ERROR Expected ) but got: b after a Line: X=(a b).

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
