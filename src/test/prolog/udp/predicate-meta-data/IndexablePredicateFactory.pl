?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a, 1).
p(b, 2) :- repeat(2).
p(c, 2).
p(c, 3).
p(c, 3).
p(d, 4) :- repeat(2).
p(e(1), 5).
p(e(2), 6).
p(e(3), 7).
p(f, 8).

%QUERY meta_data(p(_, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory

%QUERY p(X,Y)
%ANSWER
% X=a
% Y=1
%ANSWER
%ANSWER
% X=b
% Y=2
%ANSWER
%ANSWER
% X=b
% Y=2
%ANSWER
%ANSWER
% X=c
% Y=2
%ANSWER
%ANSWER
% X=c
% Y=3
%ANSWER
%ANSWER
% X=c
% Y=3
%ANSWER
%ANSWER
% X=d
% Y=4
%ANSWER
%ANSWER
% X=d
% Y=4
%ANSWER
%ANSWER
% X=e(1)
% Y=5
%ANSWER
%ANSWER
% X=e(2)
% Y=6
%ANSWER
%ANSWER
% X=e(3)
% Y=7
%ANSWER
%ANSWER
% X=f
% Y=8
%ANSWER

%QUERY meta_data(p(a, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%QUERY p(a, X)
%ANSWER X=1

%QUERY meta_data(p(_, 1), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%QUERY p(X, 1)
%ANSWER X=a

%QUERY meta_data(p(b, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory

%QUERY p(b, X)
%ANSWER X=2
%ANSWER X=2

%QUERY meta_data(p(_, 4), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory

%QUERY p(X, 4)
%ANSWER X=d
%ANSWER X=d

%QUERY meta_data(p(c, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory

%QUERY p(c, X)
%ANSWER X=2
%ANSWER X=3
%ANSWER X=3

%QUERY meta_data(p(_, 2), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory

%QUERY p(X, 2)
%ANSWER X=b
%ANSWER X=b
%ANSWER X=c

%QUERY meta_data(p(z, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(z, X)

%QUERY meta_data(p(_, 9), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(X, 9)

%QUERY meta_data(p(a, 2), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(a, 2)

%QUERY meta_data(p(e(_), _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory

%QUERY p(e(X), Y)
%ANSWER
% X=1
% Y=5
%ANSWER
%ANSWER
% X=2
% Y=6
%ANSWER
%ANSWER
% X=3
% Y=7
%ANSWER

