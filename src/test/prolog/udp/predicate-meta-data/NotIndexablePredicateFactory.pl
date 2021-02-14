?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a).
p(b(_)).
p(c(1)).
p(c(2)).
p(c(3)).
p(d) :- repeat(2).
p(e(X,X)) :- repeat(2).
p(f(1)).
p(f(_)).
p(f(2)).
p(g).

%QUERY meta_data(p(_), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory

%QUERY p(X)
%ANSWER X=a
%ANSWER X=b(_)
%ANSWER X=c(1)
%ANSWER X=c(2)
%ANSWER X=c(3)
%ANSWER X=d
%ANSWER X=d
%ANSWER X=e(X, X)
%ANSWER X=e(X, X)
%ANSWER X=f(1)
%ANSWER X=f(_)
%ANSWER X=f(2)
%ANSWER X=g

%QUERY meta_data(p(a), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%TRUE p(a)

%QUERY meta_data(p(d), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory

%QUERY p(d)
%ANSWER/
%ANSWER/

%QUERY meta_data(p(b(z)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%TRUE p(b(z))

%QUERY meta_data(p(c(_)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory

%QUERY p(c(X))
%ANSWER X=1
%ANSWER X=2
%ANSWER X=3

%QUERY meta_data(p(e(1,1)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory

%QUERY p(e(1,1))
%ANSWER/
%ANSWER/

%QUERY meta_data(p(e(1,2)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(e(1,2))

%QUERY meta_data(p(f(3)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%TRUE p(f(3))

%QUERY meta_data(p(f(_)), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory

%QUERY p(f(X))
%ANSWER X=1
%ANSWER X=UNINSTANTIATED VARIABLE
%ANSWER X=2

%QUERY meta_data(p(z), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(z)

