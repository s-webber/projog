?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a, b).

%QUERY meta_data(p(_, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(X,Y)
%ANSWER
% X=a
% Y=b
%ANSWER

%QUERY meta_data(p(a, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(a,X)
%ANSWER X=b

%QUERY meta_data(p(_, b), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(X,b)
%ANSWER X=a

%QUERY meta_data(p(a, b), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%TRUE p(a,b)

%QUERY meta_data(p(b, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(b,X)

%QUERY meta_data(p(_, a), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(X,a)

%QUERY meta_data(p(b, a), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : false
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : false
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(b,a)

