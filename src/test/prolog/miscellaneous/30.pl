is_edge(Term,Edge) :-
 Term =.. [ParentName|Children],
 member(Child,Children),
 (
   Child =.. [ChildName|_],
   Edge = ParentName-ChildName
 ;
   is_edge(Child,Edge)
 ).

%?- Term=f(i(k(c))), is_edge(Term, Edge)
% Edge=f - i
% Term=f(i(k(c)))
% Edge=i - k
% Term=f(i(k(c)))
% Edge=k - c
% Term=f(i(k(c)))
%NO

%?- is_edge(f(g(a),h(b),i(k(c))), Edge)
% Edge=f - g
% Edge=g - a
% Edge=f - h
% Edge=h - b
% Edge=f - i
% Edge=i - k
% Edge=k - c
%NO
