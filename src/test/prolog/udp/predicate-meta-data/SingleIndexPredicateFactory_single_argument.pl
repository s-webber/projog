?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a).
p(b) :- repeat(2).
p(c).
p(c).
p(d(1)).
p(d(2)).
p(d(3)).
p(e).

%QUERY meta_data(p(_), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory

%QUERY p(X)
%ANSWER X=a
%ANSWER X=b
%ANSWER X=b
%ANSWER X=c
%ANSWER X=c
%ANSWER X=d(1)
%ANSWER X=d(2)
%ANSWER X=d(3)
%ANSWER X=e

%QUERY meta_data(p(a), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%TRUE p(a)

%QUERY meta_data(p(b), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory

%QUERY p(b)
%ANSWER/
%ANSWER/

%QUERY meta_data(p(c), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory

%QUERY p(c)
%ANSWER/
%ANSWER/

%QUERY meta_data(p(z), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(z)

%QUERY meta_data(p(d(_)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory

%QUERY p(d(X))
%ANSWER X=1
%ANSWER X=2
%ANSWER X=3

