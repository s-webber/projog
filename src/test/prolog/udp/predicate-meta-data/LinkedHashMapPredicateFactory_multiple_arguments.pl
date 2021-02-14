?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p1(a, 1, x).
p1(b, X, y) :- repeat(2).
p1(c, 3, Z).

p2(a, 1, x).
p2(X, 2, y) :- repeat(2).
p2(c, 3, Z).

p3(a, 1, x).
p3(b, X, y) :- repeat(2).
p3(X, 3, z).

%QUERY meta_data(p1(_, _, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory

%QUERY meta_data(p2(_, _, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory

%QUERY meta_data(p3(_, _, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory

%QUERY p1(X, Y, Z)
%ANSWER
% X=a
% Y=1
% Z=x
%ANSWER
%ANSWER
% X=b
% Y=UNINSTANTIATED VARIABLE
% Z=y
%ANSWER
%ANSWER
% X=b
% Y=UNINSTANTIATED VARIABLE
% Z=y
%ANSWER
%ANSWER
% X=c
% Y=3
% Z=UNINSTANTIATED VARIABLE
%ANSWER

%QUERY p2(X, Y, Z)
%ANSWER
% X=a
% Y=1
% Z=x
%ANSWER
%ANSWER
% X=UNINSTANTIATED VARIABLE
% Y=2
% Z=y
%ANSWER
%ANSWER
% X=UNINSTANTIATED VARIABLE
% Y=2
% Z=y
%ANSWER
%ANSWER
% X=c
% Y=3
% Z=UNINSTANTIATED VARIABLE
%ANSWER

%QUERY p3(X, Y, Z)
%ANSWER
% X=a
% Y=1
% Z=x
%ANSWER
%ANSWER
% X=b
% Y=UNINSTANTIATED VARIABLE
% Z=y
%ANSWER
%ANSWER
% X=b
% Y=UNINSTANTIATED VARIABLE
% Z=y
%ANSWER
%ANSWER
% X=UNINSTANTIATED VARIABLE
% Y=3
% Z=z
%ANSWER

