is_edge(Term,Edge) :-
  Term =.. [ParentName|Children],
  member(Child,Children),
  (
    Child =.. [ChildName|_],
    Edge = ParentName-ChildName
  ;
    is_edge(Child,Edge)
  ).

%QUERY Term=f(i(k(c))), is_edge(Term, Edge)
%ANSWER
% Edge = f - i
% Term = f(i(k(c)))
%ANSWER
%ANSWER
% Edge = i - k
% Term = f(i(k(c)))
%ANSWER
%ANSWER
% Edge = k - c
% Term = f(i(k(c)))
%ANSWER
%NO

%QUERY is_edge(f(g(a),h(b),i(k(c))), Edge)
%ANSWER Edge = f - g
%ANSWER Edge = g - a
%ANSWER Edge = f - h
%ANSWER Edge = h - b
%ANSWER Edge = f - i
%ANSWER Edge = i - k
%ANSWER Edge = k - c
%NO
