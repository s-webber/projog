?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a, b).

%QUERY meta_data(p(_, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%QUERY p(X,Y)
%ANSWER
% X=a
% Y=b
%ANSWER

%QUERY meta_data(p(a, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%QUERY p(a,X)
%ANSWER X=b

%QUERY meta_data(p(_, b), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%QUERY p(X,b)
%ANSWER X=a

%QUERY meta_data(p(a, b), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory

%TRUE p(a,b)

%QUERY meta_data(p(b, _), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(b,X)

%QUERY meta_data(p(_, a), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(X,a)

%QUERY meta_data(p(b, a), X)
%ANSWER X=factory : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X=actual : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X=processed : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory

%FALSE p(b,a)

